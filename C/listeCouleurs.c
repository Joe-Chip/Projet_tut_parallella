#include <stdio.h>
#include <stdlib.h>
#include "listeCouleurs.h"

void ListeCouleurs_Free(ListeCouleurs * This) {
    printf("[ListeCouleurs]Entrée destructeur (free)\n");
    if (This) {
        printf("[ListeCouleurs]Free\n");
        free(This);
    }
}

void ListeCouleurs_ajouterCouleur(ListeCouleurs * This, int noCouleur) {
    // je sais pas ce que c'est censé faire
    // j'espère que java a les mêmes priorités que C pour les opérateurs
    // parce que c'est du copié-collé
    This->valeur = (This->valeur) | 1 << noCouleur;
}

int ListeCouleurs_equals(ListeCouleurs *This, struct ListeCouleurs * lc) {
    // TODO: à vérifier
    return (This->valeur == lc->valeur);
}

ListeCouleurs * New_ListeCouleurs(int nbrCouleurs) {
    
    printf("[ListeCouleurs]Entrée constructeur\n");

    // Alloc
    printf("[ListeCouleurs]Allocation\n");
    ListeCouleurs * This = malloc(sizeof(ListeCouleurs));
    if (!This) return NULL;
    
    // Init
    printf("[ListeCouleurs]Intialisation\n");
    This->nbrCouleurs = nbrCouleurs;
    if (nbrCouleurs != 1) {
        This->nbrBitsParCouleur = 24/nbrCouleurs;
        This->masqueCouleur = 0;
        int i;
        for (i = 0; i < This->nbrBitsParCouleur; i++) {
            This->masqueCouleur = (This->masqueCouleur << 1) | 1;
        }
    }

    This->Free = ListeCouleurs_Free;
    This->ajouterCouleur = ListeCouleurs_ajouterCouleur;
    
    printf("[ListeCouleurs]OK\n");
    return This;
}

// tests, ont l'air de marcher
/*int main() {

    ListeCouleurs * lc = New_ListeCouleurs(5);
    printf("masque = 0x%x\n", lc->masqueCouleur);
    printf("valeur = %d\n", lc->valeur);
    lc->ajouterCouleur(lc, 5);
    printf("masque = 0x%x\n", lc->masqueCouleur);
    printf("valeur = %d\n", lc->valeur);
    lc->Free(lc);
    
    return 0;

}*/
