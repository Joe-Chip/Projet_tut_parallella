#include <stdio.h>
#include <stdlib.h>
#include <math.h> // pour isnan (=> Double.isNaN) et isinf (=> Double.isInfinite)
#include <unistd.h>
#include "calcul.h"
#include "balayageK2_Interface.h"
#include "e-hal.h" // Bibliotheque hote pour Epiphany


// Fonctions indépendantes /////////////////////////////////
/*
void envoyerLstPointsDifferes2D(int k, int n, int j) {
    // TODO 
    // là on envoie les données vers le proc arm
    //test
    printf("k = %d\n", k);
    printf("n = %d\n", n);
    printf("j = %d\n", j);
}*/


// Fonction pour ouvrir l'Epiphany
int open_Epiphany (Calcul * monCalcul) {
    int addr_message = 0x2000; 
    int addr_flag = 0x2004;
    int addr_calcul = 0x6000;
    int addr_panel = 0x4000;
    int addr_echelleX = addr_panel;
    int addr_echelleY = addr_echelleX + sizeof(double);
    int addr_deplX = addr_echelleY + sizeof(double);
    int addr_deplY = addr_deplX + sizeof(double);
    int addr_minXVal = addr_deplY + sizeof(double);
    int addr_maxYVal = addr_minXVal + sizeof(double);
    
    /*
     * Initialisation
     */

    // Structure contenant les infos de la plateforme Epiphany
    e_platform_t eplat;
    
    // Structure comprenant les infos d'un groupe de coeurs
    e_epiphany_t edev;

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


    // Recuperer les infos de la plateforme
    if (E_OK != e_get_platform_info(&eplat)) {
        fprintf(stderr, "Problème lors de la récupération des informations sur la plate-forme\n");
    }

    /*
     * Chargement programme sur coeur
     */

    if (E_OK != e_open(&edev, 0, 0, 1, 1)) {
        fprintf(stderr, "Erreur lors de la définition du groupe de trvail\n");
        return EXIT_FAILURE;
    }
    
    if (E_OK != e_reset_group(&edev)) {
        fprintf(stderr, "Erreur lors de la réinitialisation du groupe de travail\n");
        return EXIT_FAILURE;
    }

    if (E_OK != e_load("C/e_calcul.srec", &edev, 0, 0, E_FALSE)) {
        fprintf(stderr, "Erreur chargement coeur\n");
        return EXIT_FAILURE;
    }
    
    usleep(10000);
    printf("valInit[0] = %lf\n", monCalcul->valInit[0]);
    
    
    /*
     * Envoi données calcul
     */
    int flag = -2;
    int message = -2;
    e_write(&edev, 0, 0, addr_flag, &flag, sizeof(int));
    e_write(&edev, 0, 0, addr_calcul, monCalcul, sizeof(Calcul));
    e_write(&edev, 0, 0, addr_echelleX, &echelleX, sizeof(double)); 
    e_write(&edev, 0, 0, addr_echelleY, &echelleY, sizeof(double));
    e_write(&edev, 0, 0, addr_deplX, &deplX, sizeof(double)); 
    e_write(&edev, 0, 0, addr_deplY, &deplY, sizeof(double)); 
    e_write(&edev, 0, 0, addr_minXVal, &minXVal, sizeof(double)); 
    e_write(&edev, 0, 0, addr_maxYVal, &maxYVal, sizeof(double)); 
    e_write(&edev, 0, 0, addr_message, &message, sizeof(int));

    /*
     * Lancement programme + réception résultat
     */

    if (E_OK != e_start_group(&edev)) {
        fprintf(stderr, "Erreur de lancement du groupe\n");
        return EXIT_FAILURE;
    }
    usleep(10000);
    printf("On a lancé le groupe\n");
    
    while(flag != 1) {
        printf("Programme en cours, veuillez patienter\n");
        printf("flag = %d\n", flag);
        printf("message = %d\n", message);
        sleep(1);
        e_read(&edev, 0, 0, addr_flag, &flag, sizeof(int));
        e_read(&edev, 0, 0, addr_message, &message, sizeof(int));
    }
    
    printf("C'est bon !\n");
    e_read(&edev, 0, 0, addr_calcul, monCalcul, sizeof(Calcul));
    e_read(&edev, 0, 0, addr_flag, &flag, sizeof(int));
    printf("flag = %d\n", flag);
    e_read(&edev, 0, 0, addr_message, &message, sizeof(int));
    printf("message = %d\n", message);


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


// rajouter les arguments
// les attributs sont initialisés côté Java
// on ne fait que les copier ici
/*    double xPrec;*/
/*    double yPrec;*/
/*    ListeCouleurs * lcPrec;*/
/*    */
/*    // Attributs*/
/*    char ordreCycle; // byte java*/
/*    int mMax; // 30 ?*/
/*    int nMax; // 30*/
/*    int m; // oui, bien sûr*/
/*    double a; // ouiiiiiiiiiiii*/
/*    double b;*/
/*    double epsilonVal;*/
/*    int nombreLignes;*/
/*    int masqueIndiceLigne;// = nombreLignes-1*/

/*    int lstChoixPlanSelectedIndex;*/
/*    double ** lgN;//[nombreLignes][mMax];*/
/*    double * valInit;//[nMax];*/

/*    int indiceIterationCourante;*/
/*    int indiceIterationSuivante;*/
/*    int indiceIterationPrecedente;*/
/*    int noIterationCourante;*/
/*    */
/*    int arretRunner;//booleen*/
/*    */
/*    long ctrCalculs;// y avait un static en java...*/

/*    // Attributs "privés"*/
/*    int ctrV;*/
/*    int ctrH;*/
/*    int ctrD;*/
/*    int ctrG;*/
Calcul Calcul_creer(double * valInit, double a, double b, double epsilonVal,
                    int mMax, int nMax, int m, int nombreLignes, int masqueIndiceLigne,
                    int lstChoixPlanSelectedIndex, long long ctrCalculs) {
    Calcul This;
    int i;
    
    //printf("On crée un calcul\n");
    // Initialisation
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
    //This.valInit = valInit;// à copier plutôt
    //This.lcPrec = NULL;
    
    for(i = 0; i < nMax; i++) {
        This.valInit[i] = valInit[i];
    }
    
    // Pas besoin de récupérer lgN, on va l'initialiser de toute façon
    
/*    This.lgN = malloc(nombreLignes*sizeof(double *));*/

/*    if (This.lgN == NULL) {*/
/*        printf("Il y a un gros problème\n");*/
/*        exit(1);*/
/*    }*/
    
/*    for(i = 0; i<nombreLignes; i++) {*/
/*        This.lgN[i] = malloc(mMax*sizeof(double));*/
/*        if (This.lgN[i] == NULL) {*/
/*            printf("Il y a un gros problème\n");*/
/*            exit(1);*/
/*        }*/
/*    }*/
    
    for(i = 0; i < 1000; i++) {
        This.tabPtsY[i] = -1;
    }

    // Méthodes : à faire dans e_calcul.c
    //This.differentEpsilonPres = Calcul_differentEpsilonPres;
    //This.egalEpsilonPres = Calcul_egalEspilonPres;
    //This.calcul = Calcul_calcul;
    //This.calculM = Calcul_calculM;

    return This;    
}

// tests
//FIXME: libérer données!

jobject NewDouble(JNIEnv* env, jdouble value)
{
    jclass cls = (*env)->FindClass(env, "java/lang/Double");
    jmethodID methodID = (*env)->GetMethodID(env, cls, "<init>", "(D)V");
    return (*env)->NewObject(env, cls, methodID, value);
}

jobject NewInteger(JNIEnv* env, jint value)
{
    jclass cls = (*env)->FindClass(env, "java/lang/Integer");
    jmethodID methodID = (*env)->GetMethodID(env, cls, "<init>", "(I)V");
    return (*env)->NewObject(env, cls, methodID, value);
}


JNIEXPORT jintArray JNICALL Java_balayageK2_Interface_tests_1calcul(
    JNIEnv *env,
    jclass thisClass,
    jobject obj,
    jdoubleArray j_valInit,
    /*
    jdouble a,
    jdouble b,
    jdouble epsilonVal,
    jint mMax,
    jint nMax,
    jint m,
    jint nombreLignes,
    jint masqueIndiceLigne,
    jint lstChoixPlanSelectedIndex,
    jlong ctrCalculs,*/
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

    // Nettoyage
     
    return result;

}
