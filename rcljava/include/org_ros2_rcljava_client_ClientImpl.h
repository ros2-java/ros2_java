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
/* Header for class org_ros2_rcljava_client_ClientImpl */

#ifndef ORG_ROS2_RCLJAVA_CLIENT_CLIENTIMPL_H_
#define ORG_ROS2_RCLJAVA_CLIENT_CLIENTIMPL_H_
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_ros2_rcljava_client_ClientImpl
 * Method:    nativeSendClientRequest
 * Signature: (JJJJJLorg/ros2/rcljava/interfaces/MessageDefinition;)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_client_ClientImpl_nativeSendClientRequest(
  JNIEnv *, jclass, jlong, jlong, jlong, jlong, jlong, jobject);

/*
 * Class:     org_ros2_rcljava_client_ClientImpl
 * Method:    nativeDispose
 * Signature: (JJ)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_client_ClientImpl_nativeDispose(JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     org_ros2_rcljava_client_ClientImpl
 * Method:    nativeIsServiceAvailable
 * Signature: (JJ)Z
 */
JNIEXPORT jboolean
JNICALL Java_org_ros2_rcljava_client_ClientImpl_nativeIsServiceAvailable(
  JNIEnv *, jclass, jlong, jlong);


#ifdef __cplusplus
}
#endif
#endif  // ORG_ROS2_RCLJAVA_CLIENT_CLIENTIMPL_H_
