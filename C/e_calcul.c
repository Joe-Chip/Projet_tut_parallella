/*
   Calcul réalisé par un coeur epiphany
*/

#include <stdlib.h>
#include <stdio.h>
#include "e-lib.h"

// On lit au début de la mémoire partagé (c'est là qu'on a écrit dans calcul.c, adresse 0x0)
#define BUF_ADDRESS 0x8e000000

int main()
{
    int q;
    
    e_dma_copy(&q, (char *) BUF_ADDRESS, sizeof(q));
    q++;
    e_dma_copy((char *)BUF_ADDRESS, &q, sizeof(q));
    
    return EXIT_SUCCESS;
}
