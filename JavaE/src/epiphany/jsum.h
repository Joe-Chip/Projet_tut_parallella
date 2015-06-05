#include <jni.h>

#ifndef _Included_Sum
#define _Included_Sum
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_Sum_einit
	(JNIEnv * env, jobject this);

JNIEXPORT void JNICALL Java_Sum_eclose
	(JNIEnv * env, jobject this);

JNIEXPORT jint JNICALL Java_Sum_callSomme
	(JNIEnv * env, jobject this, jobject data, jobject maxIter, jobject minIter);

JNIEXPORT jint JNICALL Java_Sum_joinSomme
	(JNIEnv * env, jobject this, jint id, jobject convergence);

#ifdef __cplusplus
}
#endif
#endif