#ifndef ESUM_H
#define ESUM_H

struct espace;
typedef struct espace Espace;
struct mint;
typedef struct mint MInt;
struct coordonnee;
typedef struct coordonnee Coordonnee;

struct espace {
	int points_length;
	Coordonnee points[50];
};

struct mint {
	int i;
};

struct coordonnee {
	double x;
	double y;
};

#endif