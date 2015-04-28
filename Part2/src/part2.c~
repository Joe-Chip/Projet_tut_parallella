#include "part2.h"


JNIEXPORT void JNICALL Java_part2_init
  (JNIEnv * env , jobject thisObj){

jclass thisClass = (*env)->GetObjectClass(env, thisObj);

jfieldID fidJ = (*env)->GetFieldID(env, thisClass, "j","I");  //I signature d'int

if (NULL == fidJ) return;

jfieldID fidI = (*env)->GetFieldID(env, thisClass, "i","I");

if (NULL == fidI) return;

(*env)->SetIntField(env, thisObj, fidJ, 40);
(*env)->SetIntField(env, thisObj, fidI, 33);

jmethodID fidF = (*env)->GetMethodID(env, thisClass, "printvar", "()V");

if (NULL == fidF) return;

(*env)->CallVoidMethod(env, thisObj, fidF);




}
