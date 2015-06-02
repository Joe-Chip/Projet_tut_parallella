#include <stdio.h>
#include <stdlib.h>
#include <math.h> // pour isnan (=> Double.isNaN) et isinf (=> Double.isInfinite)
#include <unistd.h>
#include "calcul.h"
#include "balayageK2_Interface.h"
#include "e-hal.h" // Bibliotheque hote pour Epiphany


// Fonctions indépendantes /////////////////////////////////

void envoyerLstPointsDifferes2D(int k, int n, int j) {
    // TODO 
    // là on envoie les données vers le proc arm
    //test
    printf("k = %d\n", k);
    printf("n = %d\n", n);
    printf("j = %d\n", j);
}


// Fonction pour ouvrir l'Epiphany
int open_Epiphany (Calcul * monCalcul) {
    
    int addr = 0x3000;
    
    /*
     * Initialisation
     */

    // Structure contenant les infos de la plateforme Epiphany
    e_platform_t eplat;
    
    // Structure comprenant les infos d'un groupe de coeurs
    e_epiphany_t edev;
    
    // Allumeeeeeeeeeeeez les coeurs
    e_init(NULL);

    // Reset d'Epiphany (on voudra peut-etre faire attention à ne l'utiliser qu'une fois)
    e_reset_system();

    // Recuperer les infos de la plateforme
    e_get_platform_info(&eplat);


    /*
     * Chargement programme sur coeur
     */

    e_open(&edev, 0, 0, 1, 1);
    e_reset_group(&edev);

    if (E_OK != e_load("C/e_calcul.srec", &edev, 0, 0, E_FALSE)) {
        fprintf(stderr, "Erreur chargement coeur\n");
        return EXIT_FAILURE;
    }


    /*
     * Envoi données calcul
     */
    //Calcul calculDeTest, resultat2;
    //calculDeTest.m = 123; // test : on envoie 123
    e_write(&edev, 0, 0, 0x3000, monCalcul, sizeof(Calcul));


    /*
     * Lancement programme + réception résultat
     */

    e_start_group(&edev);
    usleep(10000);
    Calcul resultat2; 
    e_read(&edev, 0, 0, 0x3000, &resultat2, sizeof(Calcul));
    printf("Resultat2.a = %d\n", resultat2.m); // on récupère 567


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


/////////////////////////////////////////////////////////

int convertY(double y) {
    double res = (maxYVal-y+deplY)*echelleY;
    return (int) res;
}

int convertX(double x) {
    double res = (x+deplX-minXVal)*echelleX;
    return (int) res;
}

// Fonctions membres

void Calcul_differerPoint2D(Calcul * This, double x, double y, ListeCouleurs * lc) {
    
    if ( !((This->lcPrec)!=NULL && (This->xPrec)==x && (This->yPrec)==y && This->lcPrec->equals(This->lcPrec, lc)) ){
        
        This->ix = convertX(x);
        This->tabPtsY[convertY(y)] = lc->nbrCouleurs;
        
        This->xPrec = x;
        This->yPrec = y;
        
        if (This->lcPrec != NULL) This->lcPrec->Free(This->lcPrec);
        This->lcPrec = NULL;
        This->lcPrec = lc;
        
    }
  
}

int Calcul_egalEspilonPres(Calcul * This, double x, double y) {
    return (abs(x-y) <= (This->epsilonVal));
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
    
    //printf("On crée un calcul\n");
    // Initialisation
    This.a = a;
    This.b = b;
    This.epsilonVal = epsilonVal;
    This.mMax = mMax;printf("mMax = %d\n", mMax);
    This.nMax = nMax;
    This.m = m;
    This.nombreLignes = nombreLignes;
    This.masqueIndiceLigne = masqueIndiceLigne;
    This.lstChoixPlanSelectedIndex = lstChoixPlanSelectedIndex;
    This.ctrCalculs = ctrCalculs;
    This.valInit = valInit;// à copier plutôt
    This.lcPrec = NULL;
    This.lgN = NULL;
    
    // Pas besoin de récupérer lgN, on va l'initialiser de toute façon
    
    This.lgN = malloc(nombreLignes*sizeof(double *));

    if (This.lgN == NULL) {
        printf("Il y a un gros problème\n");
        exit(1);
    }
    
    int i;
    for(i = 0; i<nombreLignes; i++) {
        This.lgN[i] = malloc(mMax*sizeof(double));
        if (This.lgN[i] == NULL) {
            printf("Il y a un gros problème\n");
            exit(1);
        }
    }
    
    for(i = 0; i < 1000; i++) {
        This.tabPtsY[i] = -1;
    }

    // Méthodes
    This.differentEpsilonPres = Calcul_differentEpsilonPres;
    This.egalEpsilonPres = Calcul_egalEspilonPres;
    This.calcul = Calcul_calcul;
    This.calculM = Calcul_calculM;

    return This;
}

int Calcul_differentEpsilonPres(Calcul * This, double x, double y) {
    return (abs(x-y) > (This->epsilonVal));
}

// Quadratique
void Calcul_calculM(Calcul * This) {
    //printf("On est dans calculM\n");
    double valM1 = (This->lgN)[This->indiceIterationCourante][(This->m)-1];//y[i][j-1]
    double valM2 = (This->lgN)[This->indiceIterationPrecedente][(This->m)-1];//y[i-1][j-1]
    double valMnouveau;
    if (This->lstChoixPlanSelectedIndex == 0) 
        valMnouveau =  valM2 * valM2  + (This->b) * valM1 + (This->a); // A
    else
        valMnouveau =  valM2 * valM2  + (This->a) * valM1 + (This->b);
    (This->lgN)[This->indiceIterationCourante][This->m] = valMnouveau;
    //printf("On sort de calculM\n");
}

/*
 * ÇA COMMENCE LÀ
 */

/* Principe de l'algo

pour k=1 to nbr_max
 pour i=1 to n
  pour j=1 to n
   calculer y(i,j)
   tq cycle et no gal à 2 on plot en noir
    si cycl=2 
     on test si V (on plot en vert), H (on plot en vert),D (on plot en rouge) ou G (on plot en jaune)
    fin si
   fin tq
  fin j
 fin i
fin k

*/
void Calcul_calcul(Calcul * This) {
    
    printf("Démarrage du calcul...\n");
    // Booleens
    char cycleV = 0;
    char cycleH = 0;
    char cycleD = 0;
    char cycleNouv = 0;
    
    This->ordreCycle = 0;
    
    // Initialisation iteration 0
    int k;
    for (k = 0; k < This->mMax; k++) {
        (This->lgN)[0][k] = (This->valInit)[k];
    }
    This->indiceIterationCourante = 0;
    This->indiceIterationSuivante = 1;
    This->indiceIterationPrecedente = -1;
    int indiceIterationAvantPrecedente = -1;
    
    int n, j;
    for (k = 1; k <= 100; k++) { // 100/ 16 coeurs = 6,25
        // 100*30*30*30 = 2 700 000 boucles
        //printf("k = %d\n",k);
        for (n = 1; n < This->nMax; n++) {
            //printf("n=%d\n",n);
            This->noIterationCourante = n;
            This->indiceIterationPrecedente = This->indiceIterationCourante;
            This->indiceIterationCourante = This->indiceIterationSuivante;
            This->indiceIterationSuivante = (n+1) & (This->masqueIndiceLigne);
            (This->lgN)[This->indiceIterationCourante][0] = (This->valInit)[n];

            for (j = 1; j < This->mMax; j++) {
                //printf("j = %d\n",j);
                //if (This->arretRunner) return; // arm -> epiphany
                
                This->m = j;
                This->calculM(This);

                if (!isnan((This->lgN)[This->indiceIterationCourante][j]) &&
                    !isinf((This->lgN)[This->indiceIterationCourante][j])) {
                    
                    // calcul des cycles H et V
                    //ListeCouleurs lc = new ListeCouleurs(5); // /!\ JAVA
                    ListeCouleurs * lc = New_ListeCouleurs(5);
                    lc->ajouterCouleur(lc, 0);
                    //printf("On a créé ListeCouleurs\n");
                    
                    /* on ne gère pas panelDessin en C
                    if (panelDessinDistant == NULL) {
                        panelDessin.ajouterPoint(a, lgN[indiceIterationCourante][j], lc);
                        panelDessin_ajouterPoint(This->a, (This->lgN)[This->indiceIterationCourante][j], lc); // epiphany -> arm
                    }
                    else {
                    */
   
                    Calcul_differerPoint2D(This, This->a, (This->lgN)[This->indiceIterationCourante][j], lc); // problème

                    int m; 
                    for (m=2; m<This->mMax; m++) {
                        int cycle = 2; // cycle 2 uniquement
                        if ((abs((This->lgN)[This->indiceIterationCourante][m]-(This->lgN)[This->indiceIterationSuivante][This->m]) > This->epsilonVal) &&
                            (abs((This->lgN)[This->indiceIterationCourante][m]-(This->lgN)[(This->noIterationCourante-cycle) & (This->masqueIndiceLigne)][m]) <= (This->epsilonVal)) &&
                            (abs((This->lgN)[This->indiceIterationCourante][m]-(This->lgN)[This->indiceIterationCourante][m-cycle]) <= This->epsilonVal)) {

                            // c'est un cycle 2 !

                            // Test cycle vertical
                            if (This->differentEpsilonPres(This, (This->lgN)[This->indiceIterationCourante][m], (This->lgN)[This->indiceIterationCourante][m-1]) &&
                                This->egalEpsilonPres(This, (This->lgN)[This->indiceIterationCourante][m], (This->lgN)[This->indiceIterationPrecedente][m]) &&
                                This->egalEpsilonPres(This, (This->lgN)[This->indiceIterationCourante][m-1], (This->lgN)[This->indiceIterationPrecedente][m-1])) {
                               
                                //cycleV = true;
                                cycleV = 1;
                            }
                            
                            // test cycle horizontal
                            if (This->differentEpsilonPres(This, (This->lgN)[This->indiceIterationCourante][m], (This->lgN)[This->indiceIterationPrecedente][m]) &&
                                This->egalEpsilonPres(This, (This->lgN)[This->indiceIterationCourante][m], (This->lgN)[This->indiceIterationCourante][m-1]) &&
                                This->egalEpsilonPres(This, (This->lgN)[This->indiceIterationPrecedente][m], (This->lgN)[This->indiceIterationPrecedente][m-1])) {
                            
                                cycleH = 1;
                            }
                            
                            // test cycle diagonal
                            if (This->differentEpsilonPres(This, (This->lgN)[This->indiceIterationCourante][m], (This->lgN)[This->indiceIterationPrecedente][m]) &&
                                This->egalEpsilonPres(This, (This->lgN)[This->indiceIterationCourante][m], (This->lgN)[This->indiceIterationPrecedente][m-1]) &&
                                This->egalEpsilonPres(This, (This->lgN)[This->indiceIterationCourante][m-1], (This->lgN)[This->indiceIterationPrecedente][m])) {

                                cycleD = 1;
                            }
      
                            // test cycle Nouv
                            if (indiceIterationAvantPrecedente!=-1 &&
                                This->differentEpsilonPres(This, (This->lgN)[This->indiceIterationCourante][m], (This->lgN)[This->indiceIterationCourante][m-1]) && // y1#x1
                                This->differentEpsilonPres(This, (This->lgN)[This->indiceIterationCourante][m-1], (This->lgN)[This->indiceIterationPrecedente][m-1]) && // x1#x2
                                This->differentEpsilonPres(This, (This->lgN)[This->indiceIterationCourante][m-1], (This->lgN)[This->indiceIterationPrecedente][m]) && // x1#y2
                                This->differentEpsilonPres(This, (This->lgN)[This->indiceIterationCourante][m], (This->lgN)[This->indiceIterationPrecedente][m-1]) && // y1#x2
                                This->differentEpsilonPres(This, (This->lgN)[This->indiceIterationCourante][m], (This->lgN)[This->indiceIterationPrecedente][m]) && // y1#y2
                                This->differentEpsilonPres(This, (This->lgN)[This->indiceIterationPrecedente][m-1], (This->lgN)[This->indiceIterationPrecedente][m]) && // x2#y2
                                abs((This->lgN)[This->indiceIterationCourante][m-1]-(This->lgN)[indiceIterationAvantPrecedente][m-1]) <= This->epsilonVal &&
                                abs((This->lgN)[This->indiceIterationCourante][m]-(This->lgN)[indiceIterationAvantPrecedente][m]) <= This->epsilonVal) {
  
                                cycleNouv = 1;
                            }
                           
                            if (cycleV) (This->ctrV)++;
                            if (cycleH) (This->ctrH)++;
                            if (cycleD) (This->ctrD)++;
                            if (cycleNouv) (This->ctrG)++;
                            //if (cycleV) lc->ajouterCouleur(lc, 1);
                            //if (cycleH) lc->ajouterCouleur(lc, 2);
                            //if (cycleD) lc->ajouterCouleur(lc, 3);
                            //if (cycleNouv) lc->ajouterCouleur(lc, 4);
                        } 
                    }     
                }
                (This->ctrCalculs)++;
            }
        }
    }       
    /* fin ajout */
    envoyerLstPointsDifferes2D(k,n,j);
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
     
    jdouble j_a = (*env)->GetDoubleField(env, classe, id_a);
    jdouble j_b = (*env)->GetDoubleField(env, classe, id_b);
    jdouble j_epsilonVal = (*env)->GetDoubleField(env, classe, id_epsilonVal);
    jint j_mMax = (*env)->GetIntField(env, classe, id_mMax);
    jint j_nMax = (*env)->GetIntField(env, classe, id_nMax);
    jint j_m = (*env)->GetIntField(env, classe, id_m);
    jint j_nombreLignes = (*env)->GetIntField(env, classe, id_nombreLignes);
    jint j_masqueIndiceLigne = (*env)->GetIntField(env, classe, id_masqueIndiceLigne);
    jint j_lstChoixPlanSelectedIndex = (*env)->GetIntField(env, classe, id_lstChoixPlanSelectedIndex);

    
    printf("On rentre dans le C\n"); 
    // JAVA => C
    // On doit convertir les tableaux java vers des tableaux C
    jboolean isCopy;
    jdouble * valInitC = (*env)->GetDoubleArrayElements(env, j_valInit, &isCopy);
    printf("test1\n"); 
  
    // À mettre dans une structure panelDessin ! Pour l'instant en global
    echelleX = (double) j_echelleX;
    echelleY = (double) j_echelleY;
    deplX = (double) j_deplX;
    deplY = (double) j_deplY;
    minXVal = (double) j_minXVal;
    maxYVal = (double) j_maxYVal;
  
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
     
    for(i = 0; i<((int)j_nombreLignes); i++) {
        free(calcul.lgN[i]);
    }
    free(calcul.lgN);
    
  
    return result;

}
