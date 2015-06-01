#ifndef CALCUL_H
#define CALCUL_H

#include "listeCouleurs.h"

// Fonction indépendante
void calculM(); // FAIT (pour quadratique)

// Fonction(s) à rentrer dans la structure
void envoyerLstPointsDifferes2D();// epiphany -> arm (CalculBase.java)

//Des vectors normalement
// On les sort de la structure car ils sont trop gros
double lstPtsX[100];
double lstPtsY[100];
int lstPtsC[100];

// Structure correspond à la classe Java CalculCascade2D
// On ne cherche pas à coller exactement à la classe
// mais l'idée est d'être suffisamment proche pour :
// * éviter de trop changer le code de calcul
// * faciliter les échanges entre objet Java et objet C
typedef struct Calcul {

    // Initialement dans panel dessin
    //int panelHeight;
    //int panelWidth;
    
    // CalculBase
    double xPrec;
    double yPrec;
   ListeCouleurs * lcPrec;
    
    // Attributs
    signed char ordreCycle; // byte java
    int mMax; // 30 ?
    int nMax; // 30
    int m; // oui, bien sûr
    double a; // ouiiiiiiiiiiii
    double b;
    double epsilonVal;
    int nombreLignes;
    int masqueIndiceLigne;// = nombreLignes-1

    int lstChoixPlanSelectedIndex;
    double ** lgN;//[nombreLignes][mMax];
    double * valInit;//[nMax];

    int indiceIterationCourante;
    int indiceIterationSuivante;
    int indiceIterationPrecedente;
    int noIterationCourante;
    
    int arretRunner;//booleen
    
    long long ctrCalculs;// y avait un static en java...

    // Attributs "privés"
    int ctrV;
    int ctrH;
    int ctrD;
    int ctrG;

    // Méthodes
    int (*differentEpsilonPres)(struct Calcul * This, double x, double y);
    int (*egalEpsilonPres)(struct Calcul * This, double x, double y);
    void (*differerPoint2D)(struct Calcul * This, double x, double y, ListeCouleurs *lc);
    void (*calculM)(struct Calcul * This);
    void (*calcul)(struct Calcul * This);

} Calcul;

Calcul Calcul_creer(signed char ordreCycle,
                    double * valInit,  
                    double a,
                    double b,
                    double epsilonVal,
                    int mMax,
                    int nMax,
                    int m,
                    int nombreLignes,
                    int masqueIndiceLigne,
                    int lstChoixPlanSelectedIndex,
                    int indiceIterationCourante,
                    int indiceIterationPrecedente,
                    int noIterationCourante,
                    //int height,
                    //int width,
                    long long ctrCalculs
                   );
int Calcul_differentEpsilonPres(Calcul * This, double x, double y);
int Calcul_egalEpsilonPres(Calcul * This, double x, double y);
void Calcul_calculM(Calcul * This);
void Calcul_calcul(Calcul * This);
void Calcul_differerPoint2D(Calcul * This, double x, double y, ListeCouleurs * lc);

#endif

