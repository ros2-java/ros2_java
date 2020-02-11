// Copyright 2017-2018 Esteve Fernandez <esteve@apache.org>
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
/* Header for class org_ros2_rcljava_executors_BaseExecutor */

#ifndef ORG_ROS2_RCLJAVA_EXECUTORS_BASEEXECUTOR_H_
#define ORG_ROS2_RCLJAVA_EXECUTORS_BASEEXECUTOR_H_
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeGetZeroInitializedWaitSet
 * Signature: ()J
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeGetZeroInitializedWaitSet(
  JNIEnv *, jclass);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeWaitSetInit
 * Signature: (JJIIIIII)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeWaitSetInit(
  JNIEnv *, jclass, jlong, jlong, jint, jint, jint, jint, jint, jint);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeDisposeWaitSet
 * Signature: (J)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeDisposeWaitSet(JNIEnv *, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeWaitSetClear
 * Signature: (J)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeWaitSetClear(JNIEnv *, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeWaitSetAddSubscription
 * Signature: (JJ)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeWaitSetAddSubscription(
  JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeWait
 * Signature: (JJ)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeWait(JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeTake
 * Signature: (JLjava/lang/Class;)Lorg/ros2/rcljava/interfaces/MessageDefinition;
 */
JNIEXPORT jobject
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeTake(JNIEnv *, jclass, jlong, jclass);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeWaitSetAddService
 * Signature: (JJ)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeWaitSetAddService(
  JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeWaitSetAddClient
 * Signature: (JJ)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeWaitSetAddClient(
  JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeTakeRequest
 * Signature: (JJJJLorg/ros2/rcljava/interfaces/MessageDefinition;)Lorg/ros2/rcljava/RMWRequestId;
 */
JNIEXPORT jobject
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeTakeRequest(
  JNIEnv *, jclass, jlong, jlong, jlong, jlong, jobject);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeSendServiceResponse
 * Signature: (JLorg/ros2/rcljava/RMWRequestId;JJJLorg/ros2/rcljava/interfaces/MessageDefinition;)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeSendServiceResponse(
  JNIEnv *, jclass, jlong, jobject, jlong, jlong, jlong, jobject);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeTakeResponse
 * Signature: (JJJJLorg/ros2/rcljava/interfaces/MessageDefinition;)Lorg/ros2/rcljava/RMWRequestId;
 */
JNIEXPORT jobject
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeTakeResponse(
  JNIEnv *, jclass, jlong, jlong, jlong, jlong, jobject);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeWaitSetAddTimer
 * Signature: (JJ)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeWaitSetAddTimer(
  JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeWaitSetSubscriptionIsReady
 * Signature: (JJ)Z
 */
JNIEXPORT jboolean
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeWaitSetSubscriptionIsReady(
  JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeWaitSetTimerIsReady
 * Signature: (JJ)Z
 */
JNIEXPORT jboolean
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeWaitSetTimerIsReady(
  JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeWaitSetServiceIsReady
 * Signature: (JJ)Z
 */
JNIEXPORT jboolean
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeWaitSetServiceIsReady(
  JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     org_ros2_rcljava_executors_BaseExecutor
 * Method:    nativeWaitSetClientIsReady
 * Signature: (JJ)Z
 */
JNIEXPORT jboolean
JNICALL Java_org_ros2_rcljava_executors_BaseExecutor_nativeWaitSetClientIsReady(
  JNIEnv *, jclass, jlong, jlong);

#ifdef __cplusplus
}
#endif
#endif  // ORG_ROS2_RCLJAVA_EXECUTORS_BASEEXECUTOR_H_
