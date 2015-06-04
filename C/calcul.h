#ifndef CALCUL_H
#define CALCUL_H

#include "listeCouleurs.h"


// Une structure pour PanelDessin, faute de mieux
typedef struct PanelDessin {
    double echelleX;
    double echelleY;
    double deplX;
    double deplY;
    double minXVal;
    double maxYVal;
} PanelDessin;

// À déplacer dans une structure PanelDessin
double echelleX;
double echelleY;
double deplX;
double deplY;
double minXVal;
double maxYVal;

// Structure correspond à la classe Java CalculCascade2D
// On ne cherche pas à coller exactement à la classe
// mais l'idée est d'être suffisamment proche pour :
// * éviter de trop changer le code de calcul
// * faciliter les échanges entre objet Java et objet C
typedef struct Calcul {

    
    // CalculBase
    double xPrec;
    double yPrec;
    ListeCouleurs lcPrec;
    
    // Attributs
    signed char ordreCycle; // byte java
    int m;
    int mMax; // 30 ?
    int nMax; // 30
    double a;
    double b;
    double epsilonVal;
    int nombreLignes;
    int masqueIndiceLigne;// = nombreLignes-1

    // Initialement dans panel dessin
    // Le tableau de valeurs que l'on renvoie
    int tabPtsY[1000];
    int ix;
    
    int lstChoixPlanSelectedIndex;
    double lgN[8][30];// variable, normalement [nombreLignes][mMax];
    double valInit[30];//[nMax];

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

Calcul Calcul_creer(
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
                    long long ctrCalculs
                   );

// Fonction(s) à rentrer dans la structure
void envoyerLstPointsDifferes2D();// epiphany -> arm (CalculBase.java)
#endif

