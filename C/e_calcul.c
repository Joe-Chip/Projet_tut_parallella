/*
   Calcul réalisé par un coeur epiphany
*/

#include <stdlib.h>
#include <stdio.h>
#include "e-lib.h"
#include "calcul.h"

#define ADRESSE_CALCUL  0x80803000 // taille de 152o aujourd'hui
#define ADRESSE_RES     0x80803152 // le tableau résultat sera un tab de int au final

int main()
{
    Calcul *monCalcul = (Calcul *) ADRESSE_CALCUL;
    monCalcul->m = 567;

    // En fait la dma semble surtout intéressant pour communiquer entre les coeurs... là ça sert un peu à rien
    // TODO: regarder comment marche la mémoire partagée
    //e_dma_copy((unsigned int *) DST_ADDRESS, (unsigned int *) &res, sizeof(int));
    
    return EXIT_SUCCESS;
}
