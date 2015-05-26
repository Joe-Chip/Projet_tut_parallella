#include <stdio.h>
#include <stdlib.h>
#include <math.h> // pour isnan (=> Double.isNaN) et isinf (=> Double.isInfinite)
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
void open_Epiphany () {
    // Structure contenant les infos de la plateforme Epiphany
    e_platform_t eplat;
    // Structure comprenant les infos d'un groupe de coeurs
    e_epiphany_t edev;
    // Un potentiel buffer
    e_mem_t emem;

    // Allumeeeeeeeeeeeez les coeurs
    e_init(NULL);

    // Reset d'Epiphany (on voudra peut-etre faire attention à ne l'utiliser qu'une fois)
    e_reset_system();

    // Recuperer les infos de la plateforme
    e_get_platform(&eplat);

    // Fermeture des coeurs
    e_finalize();
}


/////////////////////////////////////////////////////////

// Fonctions membres

void Calcul_differerPoint2D(Calcul * This, double x, double y, ListeCouleurs * lc) {
    // TODO
    //printf("On diffère\n");
    static int i = 0;
    
    //printf("i = %d\n", i);
    
    if ((This->lcPrec)!=NULL && (This->xPrec)==x && (This->yPrec)==y && This->lcPrec->equals(This->lcPrec, lc)) return;
    //printf("On finit de différer1\n");
    
    //  if (x < 
    /* On le retire car on ne peut pas gérer PanelDessin en C
    if (panelDessinBase.dansZoneAffichage(x, y)) {
    */
    lstPtsX[i] = x;
    lstPtsY[i] = y;
    lstPtsC[i] = lc->nbrCouleurs;
    i++;
    /*}
    */
    

    This->xPrec = x;
    This->yPrec = y;
    //ListeCouleurs_Free(This->lcPrec);
    This->lcPrec = lc;// FIXME !!!!!!!!!!!!!!!!!
    //printf("On finit de différer\n");
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
Calcul Calcul_creer(signed char ordreCycle, double * valInit, double a, double b, double epsilonVal,
                    int mMax, int nMax, int m, int nombreLignes, int masqueIndiceLigne,
                    int lstChoixPlanSelectedIndex, int indiceIterationCourante,
                    int indiceIterationPrecedente, int noIterationCourante,
                    //int height, int width,
                    long long ctrCalculs) {
    Calcul This;
    
    //printf("On crée un calcul\n");
    // Initialisation
    // Attributs: TODO
    This.ordreCycle = ordreCycle;
    This.a = a;
    This.b = b;
    This.epsilonVal = epsilonVal;
    This.mMax = mMax;
    This.nMax = nMax;
    This.m = m;
    This.nombreLignes = nombreLignes;
    This.masqueIndiceLigne = masqueIndiceLigne;
    This.lstChoixPlanSelectedIndex = lstChoixPlanSelectedIndex;
    This.indiceIterationCourante = indiceIterationCourante;
    This.indiceIterationPrecedente = indiceIterationPrecedente;
    This.noIterationCourante = noIterationCourante;
    This.ctrCalculs = ctrCalculs;
    This.valInit = valInit;
    //This.lstPtsX = 1;
    //This.panelHeight = height;
    //This.panelWidth = width;
    
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
                    //lc.ajouterCouleur(0);
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


JNIEXPORT void JNICALL Java_balayageK2_Interface_tests_1calcul
  (JNIEnv *env,
   jclass thisClass,
   jbyte ordreCycle,
   jdoubleArray valInit,
   jdouble a,
   jdouble b,
   jdouble epsilonVal,
   jint mMax,
   jint nMax,
   jint m,
   jint nombreLignes,
   jint masqueIndiceLigne,
   jint lstChoixPlanSelectedIndex,
   jint indiceIterationCourante,
   jint indiceIterationPrecedente,
   jint noIterationCourante,
   jlong ctrCalculs,
   jobject j_lstPtsX,
   jobject j_lstPtsY,
   jobject j_lstPtsC
   ) {
  
  
  // JAVA => C
  // On doit convertir les tableaux java vers des tableaux C
  jboolean isCopy;
  jdouble * valInitC = (*env)->GetDoubleArrayElements(env, valInit, &isCopy);

/*  printf("mMax = %d\n", (int) mMax);*/
  printf("hello ?\n");
  
  
  // On crée notre calcul
  Calcul calcul = Calcul_creer((signed char) ordreCycle,             // ne sert à rien ??
                               (double *) valInitC,
                               (double) a,
                               (double) b,
                               (double) epsilonVal,
                               (int) mMax,
                               (int) nMax,
                               (int) m,
                               (int) nombreLignes,
                               (int) masqueIndiceLigne,
                               (int) lstChoixPlanSelectedIndex,
                               (int) indiceIterationCourante,       //sans doute inutile (on l'initialise)
                               (int) indiceIterationPrecedente,     //sans doute inutile (on l'initialise)
                               (int) noIterationCourante,           //sans doute inutile (on l'initialise)
                               //(int) height,
                               //(int) width,
                               (long long) ctrCalculs               //possiblement inutile
                              );
  
    calcul.calcul(&calcul);
    
    jclass clsvec = (*env)->FindClass(env,"java/util/Vector");

    jmethodID jsize = (*env)->GetMethodID(env, clsvec, "size", "()I");
    if (jsize == NULL) printf("method ID not valid\n\n");
    
    jmethodID jadd = (*env)->GetMethodID(env, clsvec, "addElement", "(Ljava/lang/Object;)V");
    if (jsize == NULL) printf("pas de addElement\n\n");
    
    printf("size = %d\n", (*env)->CallIntMethod(env, j_lstPtsX, jsize));
    
    int i;
    for (i = 0 ; i < 84100 ; i++) {
        jobject unPtX = NewDouble(env,(jdouble) lstPtsX[i]);
        (*env)->CallObjectMethod(env, j_lstPtsX, jadd, unPtX);
    }
    
    for(i = 0; i < 84100 ; i++) {
        jobject unPtY = NewDouble(env,(jdouble) lstPtsY[i]);
        (*env)->CallObjectMethod(env, j_lstPtsY, jadd, unPtY);
    }
    
    for(i = 0; i < 84100 ; i++) {
        jobject unPtC = NewInteger(env,(jdouble) lstPtsC[i]);
        (*env)->CallObjectMethod(env, j_lstPtsC, jadd, unPtC);
    }
    
  
  // On libère les données java
  printf("Délivréééééé, libérééééééééééé\n");
  (*env)->ReleaseDoubleArrayElements(env, valInit,valInitC, JNI_ABORT);

  // Nettoyage
  /*
  int i;
  for(i = 0; i<nombreLignes; i++) {
    free(calcul.lgN[i]);
  }
  free(calcul.lgN);
  */

}

/*int main() {*/
/*    return 0;*/
/*} */
