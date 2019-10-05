// Copyright 2016-2018 Esteve Fernandez <esteve@apache.org>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#include <jni.h>
/* Header for class org_ros2_rcljava_RCLJava */

#ifndef ORG_ROS2_RCLJAVA_RCLJAVA_H_
#define ORG_ROS2_RCLJAVA_RCLJAVA_H_
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_ros2_rcljava_RCLJava
 * Method:    nativeCreateContextHandle
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL
  Java_org_ros2_rcljava_RCLJava_nativeCreateContextHandle(JNIEnv *, jclass);

/*
 * Class:     org_ros2_rcljava_RCLJava
 * Method:    nativeCreateNodeHandle
 * Signature: (Ljava/lang/String;Ljava/lang/String;J)J
 */
JNIEXPORT jlong JNICALL
  Java_org_ros2_rcljava_RCLJava_nativeCreateNodeHandle(JNIEnv *, jclass, jstring, jstring, jlong);

/*
 * Class:     org_ros2_rcljava_RCLJava
 * Method:    nativeGetRMWIdentifier
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_ros2_rcljava_RCLJava_nativeGetRMWIdentifier(JNIEnv *, jclass);

/*
 * Class:     org_ros2_rcljava_RCLJava
 * Method:    nativeConvertQoSProfileToHandle
 * Signature: (IIIIZ)J
 */
JNIEXPORT jlong JNICALL Java_org_ros2_rcljava_RCLJava_nativeConvertQoSProfileToHandle(
  JNIEnv *, jclass, jint, jint, jint, jint, jboolean);

/*
 * Class:     org_ros2_rcljava_RCLJava
 * Method:    nativeDisposeQoSProfile
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
  Java_org_ros2_rcljava_RCLJava_nativeDisposeQoSProfile(JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif  // ORG_ROS2_RCLJAVA_RCLJAVA_H_
