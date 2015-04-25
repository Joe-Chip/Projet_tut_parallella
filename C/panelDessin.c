#include <math.h>
#include "panelDessin.h"

/*
int PanelDessin_convertX(PanelDessin * This, double x) {
    double res = (x + (This->deplX) - (This->minXVal))*(This->echelleX);
    return ((int)round(res));

}

int PanelDessin_convertY(PanelDessin * This, double y) {
    double res = ((This->maxYVal) - y + (This->deplY))*(This->echelleY);
    return ((int)round(res));
}

int PanelDessin_dansZoneAffichage(PanelDessin * This, double x, double y) {
    int yy = convertY(y);
    int xx = convertX(x);
    return (yy>=0 && yy<dimZone.height && xx>=0 && xx<dimZone.width);    
}
*/
void PanelDessin_ajouterPoint(PanelDessin * This, double x, double y, ListeCouleurs lc) {
    // il faut envoyer les données à la partie qui est en java sur le proc
    // arm qui s'occupera d'ajouter réellement le point
    //TODO
}
