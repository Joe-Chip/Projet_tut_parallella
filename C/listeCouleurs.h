#ifndef LISTE_COULEURS
#define LISTE_COULEURS


// Structure correspond à la classe Java ListeCouleurs
// On ne cherche pas à coller exactement à la classe
// mais l'idée est d'être suffisamment proche pour :
// * éviter de trop changer le code de calcul
// * faciliter les échanges entre objet Java et objet C
typedef struct ListeCouleurs {

    // Attributs
    int nbrCouleurs;
    int valeur;
    int masqueCouleur;
    int nbrBitsParCouleur;

    // Méthodes (pas la peine de les mettre toutes) !
    void (*ajouterCouleur)(struct ListeCouleurs * This, int nbCouleurs);
    int (*equals)(struct ListeCouleurs * This, struct ListeCouleurs * lc);

} ListeCouleurs;

ListeCouleurs New_ListeCouleurs(int nbrCouleurs);
void ListeCouleurs_ajouterCouleur(ListeCouleurs * This, int noCouleur);
int ListeCouleurs_equals(ListeCouleurs *This, struct ListeCouleurs * lc);

#endif   
