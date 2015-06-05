#include "ssum.h"
#include "jsum.h"
#include "e-hal.h"

e_epiphany_t ** cores;
e_platform_t platform;

jfieldID espace_pointsID;

jfieldID mint_iID;

jfieldID coordonnee_xID;
jfieldID coordonnee_yID;


JNIEXPORT void JNICALL Java_Sum_einit
	(JNIEnv * env, jobject this) {
	e_init(NULL);
	e_reset_system();
	e_get_platform_info(&platform);
	cores = malloc(platform.rows*platform.cols*sizeof(e_epiphany_t *));
	int r, c;
	for (r = 0; r < platform.rows; r++) {
		for (c = 0; c < platform.cols; c++) {
			cores[r*platform.cols+c] = NULL;
		}
	}
	jclass ejava = (*env)->GetObjectClass(env, this);

	jfieldID espaceID = (*env)->GetFieldID(env, ejava, "espace", "Ljava.lang.Class");
	jclass espaceClass = (jclass) (*env)->GetObjectField(env, this, espaceID);
	espace_pointsID = (*env)->GetFieldID(env, espaceClass, "points", "[Lepiphany.Coordonnee");

	jfieldID mintID = (*env)->GetFieldID(env, ejava, "mint", "Ljava.lang.Class");
	jclass mintClass = (jclass) (*env)->GetObjectField(env, this, mintID);
	mint_iID = (*env)->GetFieldID(env, mintClass, "i", "I");

	jfieldID coordonneeID = (*env)->GetFieldID(env, ejava, "coordonnee", "Ljava.lang.Class");
	jclass coordonneeClass = (jclass) (*env)->GetObjectField(env, this, coordonneeID);
	coordonnee_xID = (*env)->GetFieldID(env, coordonneeClass, "x", "D");
	coordonnee_yID = (*env)->GetFieldID(env, coordonneeClass, "y", "D");

}

JNIEXPORT void JNICALL Java_Sum_eclose
	(JNIEnv * env, jobject this) {
	int r, c;
	for (r = 0; r < platform.rows; r++) {
		for (c = 0; c < platform.cols; c++) {
			if (cores[r*platform.cols+c] != NULL) {
				e_close(cores[r*platform.cols+c]);
				free(cores[r*platform.cols+c]);
			}
		}
	}
	free(cores);
	e_finalize();
}

JNIEXPORT jint JNICALL Java_Sum_joinSomme
	(JNIEnv * env, jobject this, jint id, jobject convergence); {
	char end = 0;
	int r = id/platform.cols;
	int c = id%platform.cols;
	e_read(cores[id], r, c, 3000, &end, sizeof(char));
	if (end) {
		Coordonnee sconvergence;
		e_read(cores[id], r, c, 3001, &sconvergence, sizeof(Coordonnee));
		(*env)->SetDoubleField(env, convergence, coordonnee_xID, sconvergence.x);
		(*env)->SetDoubleField(env, convergence, coordonnee_yID, sconvergence.y);
		return id;
	}
	else return -1;
}

JNIEXPORT jint JNICALL Java_Sum_callSomme
	(JNIEnv * env, jobject this, jobject data, jobject maxIter, jobject minIter) {
	int r, c;
	for (r = 0; r < platform.rows; r++) {
		for (c = 0; c < platform.cols; c++) {
			if (cores[r*platform.cols+c] == NULL) {
				cores[r*platform.cols+c] = malloc(siseof(e_epiphany_t));
				e_open(cores[r*platform.cols+c], r, c, 1, 1);
				e_load("src/epiphany/somme.srec", cores[r*platform.cols+c], r, c, FALSE);
				MInt sminIter;
				sminIter.i = (int) (*env)->GetIntField(env, minIter, mint_iID);
				e_write(cores[r*platform.cols+c], r, c, 3017, &sminIter, sizeof(MInt));
				MInt smaxIter;
				smaxIter.i = (int) (*env)->GetIntField(env, maxIter, mint_iID);
				e_write(cores[r*platform.cols+c], r, c, 3021, &smaxIter, sizeof(MInt));
				Espace sdata;
				jobjectArray asdata_points = (jobjectArray) (*env)->GetObjectField(env, data, espace_pointsID);
				int isdata_points;
				sdata.points_length = getArrayLength(env, asdata_points);
				for (isdata_points = 0; isdata_points < sdata.points_length ; isdata_points++) {
					jobject sdata_points_isdata_points_ = (*env)->GetObjectField(env, data, espace_pointsID);
					sdata.points[isdata_points].x = (double) (*env)->GetDoubleField(env, sdata_points_isdata_points_, coordonnee_xID);
					sdata.points[isdata_points].y = (double) (*env)->GetDoubleField(env, sdata_points_isdata_points_, coordonnee_yID);
				}

				e_write(cores[r*platform.cols+c], r, c, 3025, &sdata, sizeof(Espace));
				char end = 0;
				e_write(cores[r*platform.cols+c], r, c, 3000, &end, sizeof(char));
				e_start(cores[r*platform.cols+c], r, c);
				return r * platform.cols + c;
			}
		}
	}
	return -1;
}

