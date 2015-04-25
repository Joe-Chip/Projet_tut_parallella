#ifndef PANEL_DESSIN
#define PANEL_DESSIN

#include "listeCouleurs.h"

/*
typedef struct Dimension {
    int width;
    int height;
} Dimension;
*/

// Structure correspond à la classe Java PanelDessin
// On ne cherche pas à coller exactement à la classe
// mais l'idée est d'être suffisamment proche pour :
// * éviter de trop changer le code de calcul
// * faciliter les échanges entre objet Java et objet C
typedef struct PanelDessin {
    // Attributs
    /* Probablement inutile
    double echelleY;
    double echelleX;
    double minXVal;
    double minYVal;
    double echelleX;
    double echelleY;
    ListeCouleurs lcPrec;
    Dimension dimZone;

    // Méthodes
    int (*convertX)(struct PanelDessin * This, double x);
    int (*convertY)(struct PanelDessin * This, double y);
    int (*dansZoneAffichage)(struct PanelDessin * This, double x, double y);
    */
    void (*ajouterPoint)(struct PanelDessin * This, double x, double y, ListeCouleurs lc);// là il faut envoyer les données vers la partie en Java


} PanelDessin;

/*
int PanelDessin_convertX(PanelDessin * This, double x);
int PanelDessin_convertY(PanelDessin * This, double y);
int PanelDessin_dansZoneAffichage(PanelDessin * This, double x, double y);
*/
void PanelDessin_ajouterPoint(PanelDessin * This, double x, double y, ListeCouleurs lc);

#endif
