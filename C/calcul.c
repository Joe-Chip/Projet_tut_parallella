#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "calcul.h"
#include "balayageK2_Interface.h"
#include "e-hal.h" // Bibliotheque hote pour Epiphany


e_epiphany_t ** cores;
e_platform_t platform;

/*
 * Initialisation
 */
int init_Epiphany() {

    // Allumeeeeeeeeeeeez les coeurs
    if (E_OK != e_init(NULL)) {
        fprintf(stderr, "Problème lor de l'initialisation de la carte\n");
        return EXIT_FAILURE;
    }

    // Reset d'Epiphany (on voudra peut-etre faire attention à ne l'utiliser qu'une fois)
    if (E_OK !=  e_reset_system()) {
        fprintf(stderr, "Problème de la réinitialisation du système\n");
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}


int ouvrir_tous_coeurs_Epiphany(e_epiphany_t * edev, e_platform_t * eplat) {
    
    // Recuperer les infos de la plateforme
    if (E_OK != e_get_platform_info(eplat)) {
        fprintf(stderr, "Problème lors de la récupération des informations sur la plate-forme\n");
        return EXIT_FAILURE;
    }

    if (E_OK != e_open(edev, 0, 0, eplat->rows, eplat->cols)) {
        fprintf(stderr, "Erreur lors de la définition du groupe de travail\n");
        return EXIT_FAILURE;
    }
    
    if (E_OK != e_reset_group(edev)) {
        fprintf(stderr, "Erreur lors de la réinitialisation du groupe de travail\n");
        return EXIT_FAILURE;
    }
    
    return EXIT_SUCCESS;
}

int trouver_coeur_libre(e_platform_t * eplat, e_epiphany_t * edev, int * row, int * column) {

    int coeur_libre = 0;
    int r = 0;
    int c = 0;
    
    while ((!coeur_libre) && (r < eplat->rows)) {
        while ((!coeur_libre) && (c < eplat->cols)) {
            e_read(edev, r, c, FLAG_FINI, &coeur_libre, sizeof(int));
            c++;
        }
        r++;
    }
    
    if (coeur_libre) {
        *row = r-1;
        *column = c-1;
        return 0;
    } else {
        return -1;
    }
    
}

void envoyer_donnees_calcul(e_epiphany_t * edev, int row, int column, Calcul * monCalcul) {
    
    int addr_echelleX = ADRESSE_PANEL;
    int addr_echelleY = addr_echelleX + sizeof(double);
    int addr_deplX = addr_echelleY + sizeof(double);
    int addr_deplY = addr_deplX + sizeof(double);
    int addr_minXVal = addr_deplY + sizeof(double);
    int addr_maxYVal = addr_minXVal + sizeof(double);
    
    int flag = -2;
    int message = -2;
    
    e_write(edev, row, column, FLAG_FINI, &flag, sizeof(int));
    e_write(edev, row, column, ADRESSE_CALCUL, monCalcul, sizeof(Calcul));
    e_write(edev, row, column, addr_echelleX, &echelleX, sizeof(double)); 
    e_write(edev, row, column, addr_echelleY, &echelleY, sizeof(double));
    e_write(edev, row, column, addr_deplX, &deplX, sizeof(double)); 
    e_write(edev, row, column, addr_deplY, &deplY, sizeof(double)); 
    e_write(edev, row, column, addr_minXVal, &minXVal, sizeof(double)); 
    e_write(edev, row, column, addr_maxYVal, &maxYVal, sizeof(double)); 
    e_write(edev, row, column, MESSAGE, &message, sizeof(int));
    
}

int calcul_termine(e_epiphany_t * edev, int row, int column) {
    
    int flag = -2;
    
    e_read(edev, row, column, FLAG_FINI, &flag, sizeof(int));
    
    if (flag == 1)
        return 1;
    else
        return 0;
}
    
// Devrait être un thread
int lancer_calcul(e_platform_t * eplat, e_epiphany_t * edev, Calcul * monCalcul) {

    int row, column;
    
    while (trouver_coeur_libre(eplat, edev, &row, &column) == -1) {
        // attente active...
        usleep(10000);
    }
    
    // On a récupéré la ligne et la colonne d'un coeur libre
    envoyer_donnees_calcul(edev, row, column, monCalcul);
    
    // On démarre le programme situé sur le coeur (row, column)
    if (E_OK != e_start(edev, row, column)) {
        fprintf(stderr, "Erreur lors du lancement du programme sur le coeur (%d,%d)\n", row, column);
        return EXIT_FAILURE;
    }
    

    int message = 42;
    int flag = 42;
    while(!calcul_termine(edev, row, column)) {
        // attente active...
        e_read(edev, 0, 0, MESSAGE, &message, sizeof(int));
        e_read(edev, 0, 0, FLAG_FINI, &flag, sizeof(int));
        printf("[%d,%d] message = 0x%x (%d)\n", row, column, message, message);
        printf("[%d,%d] flag = 0x%x (%d)\n", row, column, flag, flag);
        usleep(10000);
        sleep(1);
    }
        
    e_read(&edev, 0, 0, ADRESSE_CALCUL, monCalcul, sizeof(Calcul));
    
    return EXIT_SUCCESS;
    
}

// Un calcul pour l'instant
int open_Epiphany (Calcul * monCalcul) {
    
    e_epiphany_t edev;
    e_platform_t eplat;
    
    init_Epiphany();
    
    ouvrir_tous_coeurs_Epiphany(&edev, &eplat);

    // On charge le programme sur tous les coeurs
    if (E_OK != e_load_group("C/e_calcul.srec", &edev, 0, 0, eplat.rows, eplat.cols, E_FALSE)) {
        fprintf(stderr, "Erreur chargement coeur\n");
        return EXIT_FAILURE;
    }
    
    usleep(10000);
    
    // On lance le programme sur un coeur seulement pour l'instant
    lancer_calcul(&eplat, &edev, monCalcul);

    /*
     * Fermeture coeurs
     */

    // Fermeture du workgroup
    e_close(&edev);

    // Fermeture du coeur
    e_finalize();
    usleep(10000);

    printf("Programme Epiphany terminé\n");
    return EXIT_SUCCESS;
}


Calcul Calcul_creer(double * valInit, double a, double b, double epsilonVal,
                    int mMax, int nMax, int m, int nombreLignes, int masqueIndiceLigne,
                    int lstChoixPlanSelectedIndex, long long ctrCalculs) {
    Calcul This;
    int i;
    
    //printf("On crée un calcul\n");
    This.a = a;
    This.b = b;
    This.epsilonVal = epsilonVal;
    This.mMax = mMax;printf("mMax = %d\n", mMax);
    This.nMax = nMax;printf("nMax = %d\n", nMax);

    This.m = m;printf("nMax = %d\n", m);
    This.nombreLignes = nombreLignes;printf("nMax = %d\n", nombreLignes);
    This.masqueIndiceLigne = masqueIndiceLigne;printf("nMax = %d\n", masqueIndiceLigne);
    This.lstChoixPlanSelectedIndex = lstChoixPlanSelectedIndex;
    This.ctrCalculs = ctrCalculs;
    
    for(i = 0; i < nMax; i++) {
        This.valInit[i] = valInit[i];
    }
    
    for(i = 0; i < 1000; i++) {
        This.tabPtsY[i] = -1;
    }

    return This;    
}


// Fonctions visibles par JNI ///////////////////////////////////////

// Pas encore utilisée
JNIEXPORT jint JNICALL Java_balayageK2_Interface_einit
    (JNIEnv * env, jclass thisClass) {
    
    init_Epiphany();
    
    if (E_OK != e_get_platform_info(&platform)) {
        fprintf(stderr, "Problème lors de la récupération des informations sur la plate-forme\n");
        return (jint) EXIT_FAILURE;
    }

    cores = malloc(platform.rows*platform.cols*sizeof(e_epiphany_t *));
    int r, c;
    for (r = 0; r < platform.rows; r++) {
        for (c = 0; c < platform.cols; c++) {
            cores[r*platform.cols+c] = NULL;
        }
    }
    
    return (jint) EXIT_SUCCESS;
}

// Pas encore utilisée
JNIEXPORT void JNICALL Java_balayageK2_Interface_eclose
    (JNIEnv * env, jclass thisClass) {
    
    int r, c;
    for (r = 0; r < platform.rows; r++) {
        for (c = 0; c < platform.cols; c++) {
            if (cores[r*platform.cols+c] != NULL) {
                e_close(cores[r*platform.cols+c]);
                free(cores[r*platform.cols+c]);
            }
        }
    }
    free(cores);
    e_finalize();

}

JNIEXPORT jintArray JNICALL Java_balayageK2_Interface_tests_1calcul(
    JNIEnv *env,
    jclass thisClass,
    jobject obj,
    jdoubleArray j_valInit,
    jdouble j_echelleX,
    jdouble j_echelleY,
    jdouble j_deplX,
    jdouble j_deplY,
    jdouble j_minXVal,
    jdouble j_maxYVal
    ) {
    
    jclass classe = (*env)->GetObjectClass(env, obj);

    jfieldID id_a = (*env)->GetFieldID(env, classe, "a", "D");
    jfieldID id_b = (*env)->GetFieldID(env, classe, "b", "D");
    jfieldID id_epsilonVal = (*env)->GetFieldID(env, classe, "epsilonVal", "D");
    jfieldID id_mMax = (*env)->GetFieldID(env, classe, "mMax", "I");
    jfieldID id_nMax = (*env)->GetFieldID(env, classe, "nMax", "I");
    jfieldID id_m = (*env)->GetFieldID(env, classe, "m", "I");
    jfieldID id_nombreLignes = (*env)->GetFieldID(env, classe, "nombreLignes", "I");
    jfieldID id_masqueIndiceLigne = (*env)->GetFieldID(env, classe, "masqueIndiceLigne", "I");
    jfieldID id_lstChoixPlanSelectedIndex = (*env)->GetFieldID(env, classe, "lstChoixPlanSelectedIndex", "I");
     
    jdouble j_a = (*env)->GetDoubleField(env, obj, id_a);
    jdouble j_b = (*env)->GetDoubleField(env, obj, id_b);
    jdouble j_epsilonVal = (*env)->GetDoubleField(env, obj, id_epsilonVal);
    jint j_mMax = (*env)->GetIntField(env, obj, id_mMax);
    jint j_nMax = (*env)->GetIntField(env, obj, id_nMax);
    jint j_m = (*env)->GetIntField(env, obj, id_m);
    jint j_nombreLignes = (*env)->GetIntField(env, obj, id_nombreLignes);
    jint j_masqueIndiceLigne = (*env)->GetIntField(env, obj, id_masqueIndiceLigne);
    jint j_lstChoixPlanSelectedIndex = (*env)->GetIntField(env, obj, id_lstChoixPlanSelectedIndex);

    
    printf("On rentre dans le C\n"); 
    // JAVA => C
    // On doit convertir les tableaux java vers des tableaux C
    jboolean isCopy;
    jdouble * valInitC = (*env)->GetDoubleArrayElements(env, j_valInit, &isCopy);
  
    // À mettre dans une structure panelDessin ! Pour l'instant en global
    echelleX = (double) j_echelleX;
    echelleY = (double) j_echelleY;
    deplX = (double) j_deplX;
    deplY = (double) j_deplY;
    minXVal = (double) j_minXVal;
    maxYVal = (double) j_maxYVal;

    printf("echelleX = %lf\n", echelleX);
    printf("echelleY = %lf\n", echelleY);
  
    // On crée notre calcul
    printf("On va créer le calcul\n");
    Calcul calcul = Calcul_creer(
                               (double *) valInitC,
                               (double) j_a,
                               (double) j_b,
                               (double) j_epsilonVal,
                               (int) j_mMax,
                               (int) j_nMax,
                               (int) j_m,
                               (int) j_nombreLignes,
                               (int) j_masqueIndiceLigne,
                               (int) j_lstChoixPlanSelectedIndex,
                               0//(long long) ctrCalculs               //possiblement inutile
                              );
    //calcul.calcul(&calcul);
    printf("taille de Calcul = %d\n", sizeof(calcul));
    printf("taille de Calcul = %d\n", sizeof(Calcul));
    
    open_Epiphany(&calcul);
    printf("On a fini de calculer\n");
    
/*    printf("Voici le tableau de couleurs :\n");*/
/*    int i;*/
/*    for (i = 0; i < 1000; i++) {*/
/*        printf("%d\n", calcul.tabPtsY[i]);*/
/*    }*/
    
    jintArray result = (*env)->NewIntArray(env, 1000);
    
    // fill a temp structure to use to populate the java int array
    jint tmp[1000];
    int i;
    for (i = 0; i < 1000; i++) {
        tmp[i] = calcul.tabPtsY[i]; // put whatever logic you want to populate the values here.
    }
 
    (*env)->SetIntArrayRegion(env, result, 0, 1000, tmp);
  
    // On libère les données java
    (*env)->ReleaseDoubleArrayElements(env, j_valInit,valInitC, JNI_ABORT);

    return result;

}
