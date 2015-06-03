/*
   Calcul réalisé par un coeur epiphany
*/

#include <stdlib.h>
#include <stdio.h>
#include "e-lib.h"
#include "calcul.h"
#include "listeCouleurs.h"

#define MESSAGE 0x80800100
#define FLAG_FINI 0x80801000
#define ADRESSE_CALCUL 0x80801004 // taille de 6336 o aujourd'hui

// Devrait être une structure tout propre
// PanelDessin
#define ADRESSE_PANEL ADRESSE_CALCUL+sizeof(Calcul)

// Une structure pour PanelDessin, faute de mieux
typedef struct PanelDessin {
    double echelleX;
    double echelleY;
    double deplX;
    double deplY;
    double minXVal;
    double maxYVal;
} PanelDessin;

PanelDessin * panel = (PanelDessin *) ADRESSE_PANEL;
int * message = (int *) MESSAGE;


// Fonction membres, on devrait les préfixer avec e_ pour
// éviter les confusions
int Calcul_differentEpsilonPres(Calcul * This, double x, double y);
int Calcul_egalEpsilonPres(Calcul * This, double x, double y);
void Calcul_calculM(Calcul * This);
void Calcul_calcul(Calcul * This);

int * fini = (int *) FLAG_FINI;

// Implémentation
int main()
{
    // Notre structure est ici
    Calcul *monCalcul = (Calcul *) ADRESSE_CALCUL;

    // On rajoute les liens vers nos fonctions locales
    monCalcul->differentEpsilonPres = Calcul_differentEpsilonPres;
    monCalcul->egalEpsilonPres = Calcul_egalEpsilonPres;
    monCalcul->calcul = Calcul_calcul;
    monCalcul->calculM = Calcul_calculM;
    
    *message=(int)(monCalcul->mMax);
    //monCalcul->calcul(monCalcul);

    // En fait la dma semble surtout intéressant pour communiquer entre les coeurs... là ça sert un peu à rien
    // TODO: regarder comment marche la mémoire partagée
    //e_dma_copy((unsigned int *) DST_ADDRESS, (unsigned int *) &res, sizeof(int));
    //e_dma_copy((unsigned int *) FLAG_FINI, (unsigned int *) &machin, sizeof(int));
    *fini = 2;
    return EXIT_SUCCESS;
}


// Pour listeCouleur
ListeCouleurs New_ListeCouleurs(int nbrCouleurs);
void ListeCouleurs_ajouterCouleur(ListeCouleurs * This, int noCouleur);
int ListeCouleurs_equals(ListeCouleurs *This, struct ListeCouleurs * lc);


/////////////////////////////////////////////////////////

int convertY(double y) {
    double res = (panel->maxYVal-y+panel->deplY)*(panel->echelleY);
    return (int) res;
}

int convertX(double x) {
    double res = (x+(panel->deplX)-(panel->minXVal))*(panel->echelleX);
    return (int) res;
}

// Fonctions membres

void Calcul_differerPoint2D(Calcul * This, double x, double y, ListeCouleurs * lc) {
    
    if ( !((This->xPrec)==x && (This->yPrec)==y && (This->lcPrec).equals(&(This->lcPrec), lc)) ){
        //printf("on entre");
        This->ix = convertX(x);
        This->tabPtsY[convertY(y)] = lc->nbrCouleurs;
        
        This->xPrec = x;
        This->yPrec = y;
        
        This->lcPrec = *lc;
        
    } 
}

int Calcul_egalEpsilonPres(Calcul * This, double x, double y) {
    return (abs(x-y) <= (This->epsilonVal));
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
    //*message=3;
    
    // Booleens
    char cycleV = 0;
    char cycleH = 0;
    char cycleD = 0;
    char cycleNouv = 0;
    
    This->ordreCycle = 0;
     
    // Initialisation iteration 0
    int k, n, j, m;
    for (k = 0; k < This->mMax; k++) {
        (This->lgN)[0][k] = (This->valInit)[k];
    }

    This->indiceIterationCourante = 0;
    This->indiceIterationSuivante = 1;
    This->indiceIterationPrecedente = -1;
    int indiceIterationAvantPrecedente = -1;
    
    for (k = 1; k <= 100; k++) { // 100/ 16 coeurs = 6,25
        //*message=k;
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
                
                This->m = j;
                This->calculM(This);

                if (!isnan((This->lgN)[This->indiceIterationCourante][j]) &&
                    !isinf((This->lgN)[This->indiceIterationCourante][j])) {
                    
                    // calcul des cycles H et V
                    ListeCouleurs lc = New_ListeCouleurs(5);
                    lc.ajouterCouleur(&lc, 0);
                    //printf("On a créé ListeCouleurs\n");
                    
                    /* on ne gère pas panelDessin en C
                    if (panelDessinDistant == NULL) {
                        panelDessin.ajouterPoint(a, lgN[indiceIterationCourante][j], lc);
                        panelDessin_ajouterPoint(This->a, (This->lgN)[This->indiceIterationCourante][j], lc); // epiphany -> arm
                    }
                    else {
                    */
   
                    Calcul_differerPoint2D(This, This->a, (This->lgN)[This->indiceIterationCourante][j], &lc); // problème
                    
                    for (m=2; m<(This->mMax); m++) {
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
                            if (cycleV) lc.ajouterCouleur(&lc, 1);
                            if (cycleH) lc.ajouterCouleur(&lc, 2);
                            if (cycleD) lc.ajouterCouleur(&lc, 3);
                            if (cycleNouv) lc.ajouterCouleur(&lc, 4);
                        } 
                    }     
                }
                (This->ctrCalculs)++;
            }
        }
    }       
    /* fin ajout */
    *fini = 1;
    //envoyerLstPointsDifferes2D(k,n,j);    
}


void ListeCouleurs_ajouterCouleur(ListeCouleurs * This, int noCouleur) {
    // je sais pas ce que c'est censé faire
    // j'espère que java a les mêmes priorités que C pour les opérateurs
    // parce que c'est du copié-collé
    This->valeur = (This->valeur) | 1 << noCouleur;
}

int ListeCouleurs_equals(ListeCouleurs *This, struct ListeCouleurs * lc) {
    // TODO: à vérifier
    //printf("Equals?\n");
    return (This->valeur == lc->valeur);
}

ListeCouleurs New_ListeCouleurs(int nbrCouleurs) {
    
    //printf("[ListeCouleurs]Entrée constructeur\n");

    // Alloc
    //printf("[ListeCouleurs]Allocation\n");
    ListeCouleurs This;
    
    // Init
    //printf("[ListeCouleurs]Intialisation\n");
    This.nbrCouleurs = nbrCouleurs;
    if (nbrCouleurs != 1) {
        This.nbrBitsParCouleur = 24/nbrCouleurs;
        This.masqueCouleur = 0;
        int i;
        for (i = 0; i < This.nbrBitsParCouleur; i++) {
            This.masqueCouleur = (This.masqueCouleur << 1) | 1;
        }
    }

    This.ajouterCouleur = ListeCouleurs_ajouterCouleur;
    This.equals = ListeCouleurs_equals;
    
   // printf("[ListeCouleurs]OK\n");
    return This;
}

