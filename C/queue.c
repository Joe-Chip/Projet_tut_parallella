#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include "queue.h"

file_t ma_file;
int fini;

pthread_mutex_t mutex_file = PTHREAD_MUTEX_INITIALIZER;

void enfiler(file_t * une_file, int nv) {

    element_t * nv_elt = malloc(sizeof(element_t));
    
    if (nv_elt == NULL) {
        fprintf(stderr, "Impossible d'allouer la mémoire\n");
        exit(1);
    }

    pthread_mutex_lock(&mutex_file);
    
    nv_elt->elt = nv;

    if (une_file->queue == NULL || une_file->tete == NULL) {
        // file vide au moment d'enfiler
        fprintf(stdout, "File vide à l'insertion\n");
        une_file->tete = nv_elt;
    } else {
        une_file->queue->prec = nv_elt;
    }
    
    nv_elt->suiv = une_file->queue;
    nv_elt->prec = NULL;
    une_file->queue = nv_elt;

    pthread_mutex_unlock(&mutex_file);
}

int defiler(file_t * une_file) {
    
    int retour = -1;
    element_t * tete;

    pthread_mutex_lock(&mutex_file);

    if (une_file->tete != NULL) {
        tete = une_file->tete;
        une_file->tete = tete->prec;
        
        if (tete->prec != NULL) {
            tete->prec->suiv = NULL;
        }

        if (tete == une_file->queue) {
            une_file->queue = NULL;
        }
        retour = tete->elt;
        free(tete);
        tete = NULL;

    } else {
        fprintf(stdout, "Attention, file vide\n");
    }

    pthread_mutex_unlock(&mutex_file);

    return retour;
}

int est_vide(file_t * une_file) {
    int retour;

    pthread_mutex_lock(&mutex_file);
    retour = ((une_file == NULL) || (une_file->tete == NULL) || (une_file->queue == NULL));
    pthread_mutex_unlock(&mutex_file);
    
    return retour;
}

void print_file(file_t * une_file) {

    if (une_file != NULL) {

        element_t * courant = une_file->queue;
    
        fprintf(stdout, "Début de file\n");
        while(courant != NULL) {
            printf("elt = %d\n", courant->elt);
            courant = courant->suiv;
        }

        fprintf(stdout, "Fin de file\n");
    }
}

void * distribuer(void * arg) {
    
    while(!fini) {
        if (!est_vide(&ma_file)) {
            printf("[distribuer] On lance le calcul %d\n", defiler(&ma_file));
            usleep(10000);
        } else {
            printf("[distribuer] Pas de calcul en attente\n");
            sleep(1);
        }
    }

    pthread_exit(NULL);
}

void * test_ajouter_elts(void * arg) {
    
    int i;

    for (i = 0; i < 1000; i++) {
        printf("[ajouter] On ajoute %d\n", i);
        enfiler(&ma_file, i);
        usleep(10000);
    }

    pthread_exit(NULL);
}
int main() {
    
    pthread_t id_distribuer, id_test_ajouter_elts;
    fini = 0;

    pthread_create(&id_test_ajouter_elts, NULL, (void *) test_ajouter_elts, NULL);
    pthread_create(&id_distribuer, NULL, (void *) distribuer, NULL);

    sleep(20);

    printf("On quitte le main\n");
    pthread_exit(NULL);
}

