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
/* Header for class org_ros2_rcljava_node_NodeImpl */

#ifndef ORG_ROS2_RCLJAVA_NODE_NODEIMPL_H_
#define ORG_ROS2_RCLJAVA_NODE_NODEIMPL_H_
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     org_ros2_rcljava_node_NodeImpl
 * Method:    nativeGetName
 * Signature: (J)Ljava/lang/String
 */
JNIEXPORT jstring
JNICALL Java_org_ros2_rcljava_node_NodeImpl_nativeGetName(
  JNIEnv * env, jclass, jlong node_handle);

/*
 * Class:     org_ros2_rcljava_node_NodeImpl
 * Method:    nativeGetNamespace
 * Signature: (J)Ljava/lang/String
 */
JNIEXPORT jstring
JNICALL Java_org_ros2_rcljava_node_NodeImpl_nativeGetNamespace(
  JNIEnv * env, jclass, jlong node_handle);

/*
 * Class:     org_ros2_rcljava_node_NodeImpl
 * Method:    nativeCreatePublisherHandle
 * Signature: (JLjava/lang/Class;Ljava/lang/String;J)J
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_node_NodeImpl_nativeCreatePublisherHandle(
  JNIEnv *, jclass, jlong, jclass, jstring, jlong);

/*
 * Class:     org_ros2_rcljava_node_NodeImpl
 * Method:    nativeCreateSubscriptionHandle
 * Signature: (JLjava/lang/Class;Ljava/lang/String;J)J
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_node_NodeImpl_nativeCreateSubscriptionHandle(
  JNIEnv *, jclass, jlong, jclass, jstring, jlong);

/*
 * Class:     org_ros2_rcljava_node_NodeImpl
 * Method:    nativeCreateServiceHandle
 * Signature: (JLjava/lang/Class;Ljava/lang/String;J)J
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_node_NodeImpl_nativeCreateServiceHandle(
  JNIEnv *, jclass, jlong, jclass, jstring, jlong);

/*
 * Class:     org_ros2_rcljava_node_NodeImpl
 * Method:    nativeCreateClientHandle
 * Signature: (JLjava/lang/Class;Ljava/lang/String;J)J
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_node_NodeImpl_nativeCreateClientHandle(
  JNIEnv *, jclass, jlong, jclass, jstring, jlong);

/*
 * Class:     org_ros2_rcljava_node_NodeImpl
 * Method:    nativeDispose
 * Signature: (J)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_node_NodeImpl_nativeDispose(JNIEnv *, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_node_NodeImpl
 * Method:    nativeCreateTimerHandle
 * Signature: (JJJ)J
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_node_NodeImpl_nativeCreateTimerHandle(
  JNIEnv *, jclass, jlong, jlong, jlong);

#ifdef __cplusplus
}
#endif
#endif  // ORG_ROS2_RCLJAVA_NODE_NODEIMPL_H_
