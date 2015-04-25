#ifndef CALCUL_H
#define CALCUL_H

#include "listeCouleurs.h"

// Fonctions indépendantes
void calculM(); // FAIT (pour quadratique)
void differerPoint2D(double x, double y, ListeCouleurs * lc);// CalculBase.java
int differentEpsilonPres(double x, double y);//FAIT
int egalEpsilonPres(double x, double y);//FAIT
void envoyerLstPointsDifferes2D();// epiphany -> arm (CalculBase.java)

// Structure correspond à la classe Java CalculCascade2D
// On ne cherche pas à coller exactement à la classe
// mais l'idée est d'être suffisamment proche pour :
// * éviter de trop changer le code de calcul
// * faciliter les échanges entre objet Java et objet C
typedef struct Calcul {
    
    // Attributs
    char ordreCycle; // byte java
    int mMax;
    int nMax;
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
    
    long ctrCalculs;// y avait un static en java...

    // Attributs "privés"
    int ctrV;
    int ctrH;
    int ctrD;
    int ctrG;

    // Méthodes
    int (*differentEpsilonPres)(struct Calcul * This, double x, double y);
    int (*egalEpsilonPres)(struct Calcul * This, double x, double y);
    void (*calculM)(struct Calcul * This);
    void (*calcul)(struct Calcul * This);

} Calcul;

Calcul Calcul_creer();
int Calcul_differentEpsilonPres(Calcul * This, double x, double y);
int Calcul_egalEpsilonPres(Calcul * This, double x, double y);
void Calcul_calculM(Calcul * This);
void Calcul_calcul(Calcul * This);

#endif

