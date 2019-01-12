//
// Created by 谢康 on 2019/1/4.
//


#include <jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_andrewtse_testdemo_activity_JniActivity_nativeToast(JNIEnv *env, jclass instance) {
    const char *returnValue = "From C++";
    return env->NewStringUTF(returnValue);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_andrewtse_testdemo_activity_JniActivity_nativeCal(JNIEnv *env, jclass type, jint a, jint b, jchar op) {
    return a + b;
}
