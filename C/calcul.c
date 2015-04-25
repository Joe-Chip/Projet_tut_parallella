#include <stdio.h>
#include <stdlib.h>
#include <math.h> // pour isnan (=> Double.isNaN) et isinf (=> Double.isInfinite)
#include "calcul.h"


// Fonctions indépendantes /////////////////////////////////

void differerPoint2D(double x, double y, ListeCouleurs * lc) {
    // TODO
    // je suppose qu'il faudrait éviter d'appeler le code java sur le proc arm ici
    // mais je ne vois pas trop comment faire
    /*
    if (lcPrec!=null && xPrec==x && yPrec==y && lcPrec.equals(lc)) return;
    if (panelDessinBase.dansZoneAffichage(x, y)) {
        lstPtsX.add(x);
        lstPtsY.add(y);
        lstPtsC.add(lc.nbrCouleurs);
    }
    xPrec = x;
    yPrec = y;
    lcPrec = lc;
    */
}

void envoyerLstPointsDifferes2D() {
    // TODO 
    // là on envoie les données vers le proc arm
}


/////////////////////////////////////////////////////////

// Fonctions membres

int Calcul_egalEspilonPres(Calcul * This, double x, double y) {
    return (abs(x-y) <= (This->epsilonVal));
}
// rajouter les arguments
// les attributs sont initialisés côté Java
// on ne fait que les copier ici
Calcul Calcul_creer() {
    Calcul This;

    // Initialisation
    // Attributs: TODO

    // Méthodes
    This.differentEpsilonPres = Calcul_differentEpsilonPres;
    This.egalEpsilonPres = Calcul_egalEspilonPres;
    This.calcul = Calcul_calcul;

    return This;
}


int Calcul_differentEpsilonPres(Calcul * This, double x, double y) {
    return (abs(x-y) > (This->epsilonVal));
}

// Quadratique
void Calcul_calculM(Calcul * This) {
    double valM1 = (This->lgN)[This->indiceIterationCourante][(This->m)-1];//y[i][j-1]
    double valM2 = (This->lgN)[This->indiceIterationPrecedente][(This->m)-1];//y[i-1][j-1]
    double valMnouveau;
    if (This->lstChoixPlanSelectedIndex == 0) 
        valMnouveau =  valM2 * valM2  + (This->b) * valM1 + (This->a); // A
    else
        valMnouveau =  valM2 * valM2  + (This->a) * valM1 + (This->b);
    (This->lgN)[This->indiceIterationCourante][This->m] = valMnouveau;
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
    
    // À DISTRIBUER SUR LES COEURS EPIPHANY -> e_calcul.c
    int n, j;
    for (k = 1; k <= 100; k++) {
        for (n = 1; n < This->nMax; n++) {

            This->noIterationCourante = n;
            This->indiceIterationPrecedente = This->indiceIterationCourante;
            This->indiceIterationCourante = This->indiceIterationSuivante;
            This->indiceIterationSuivante = (n+1) & (This->masqueIndiceLigne);
            (This->lgN)[This->indiceIterationCourante][0] = (This->valInit)[n];

            for (j = 1; j < This->mMax; j++) {
                if (This->arretRunner) return; // arm -> epiphany

                This->m = j;
                This->calculM(This);
        
                if (!isnan((This->lgN)[This->indiceIterationCourante][j]) &&
                    !isinf((This->lgN)[This->indiceIterationCourante][j])) {
                    
                    // calcul des cycles H et V
                    //ListeCouleurs lc = new ListeCouleurs(5); // /!\ JAVA
                    ListeCouleurs * lc = New_ListeCouleurs(5); // penser au free; pas sûr qu'il y ait besoin d'un pointeur en fait
                    //lc.ajouterCouleur(0);
                    lc->ajouterCouleur(lc, 0);
                    
                    if (panelDessinDistant == NULL) {
                        //panelDessin.ajouterPoint(a, lgN[indiceIterationCourante][j], lc);
                        panelDessin_ajouterPoint(This->a, (This->lgN)[This->indiceIterationCourante][j], lc); // epiphany -> arm
                    }
                    else {
                        differerPoint2D(This->a, (This->lgN)[This->indiceIterationCourante][j], lc); // problème
                    }
                   
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
                            if (cycleV) lc->ajouterCouleur(lc, 1);
                            if (cycleH) lc->ajouterCouleur(lc, 2);
                            if (cycleD) lc->ajouterCouleur(lc, 3);
                            if (cycleNouv) lc->ajouterCouleur(lc, 4);
                        } 
                    }     
                }
                (This->ctrCalculs)++;
            }
        }
    }       
    /* fin ajout */
    envoyerLstPointsDifferes2D();// epiphany -> arm
}

// tests
int main() {
    return 0;
} 
