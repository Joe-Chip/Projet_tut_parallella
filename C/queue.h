#ifndef QUEUE_H
#define QUEUE_H

typedef struct element_t {
    int elt;
    struct element_t * suiv;
    struct element_t * prec;
} element_t;

typedef struct file_t {
    element_t * tete;
    element_t * queue;
} file_t;

#endif
