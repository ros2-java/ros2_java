// Copyright 2020 Open Source Robotics Foundation, Inc.
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
/* Header for class org_ros2_rcljava_publisher_statuses_OfferedQosIncompatible */

#ifndef ORG_ROS2_RCLJAVA_PUBLISHER_STATUSES_OFFEREDQOSINCOMPATIBLE_H_
#define ORG_ROS2_RCLJAVA_PUBLISHER_STATUSES_OFFEREDQOSINCOMPATIBLE_H_
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     org_ros2_rcljava_publisher_statuses_OfferedQosIncompatible
 * Method:    nAllocateRCLStatusEvent
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL
Java_org_ros2_rcljava_publisher_statuses_OfferedQosIncompatible_nAllocateRCLStatusEvent(
  JNIEnv *, jclass);

/*
 * Class:     org_ros2_rcljava_publisher_statuses_OfferedQosIncompatible
 * Method:    nDeallocateRCLStatusEvent
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_org_ros2_rcljava_publisher_statuses_OfferedQosIncompatible_nDeallocateRCLStatusEvent(
  JNIEnv *, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_publisher_statuses_OfferedQosIncompatible
 * Method:    nFromRCLEvent
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_org_ros2_rcljava_publisher_statuses_OfferedQosIncompatible_nFromRCLEvent(
  JNIEnv *, jobject, jlong);

/*
 * Class:     org_ros2_rcljava_publisher_statuses_OfferedQosIncompatible
 * Method:    nGetPublisherEventType
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_org_ros2_rcljava_publisher_statuses_OfferedQosIncompatible_nGetPublisherEventType(
  JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif  // ORG_ROS2_RCLJAVA_PUBLISHER_STATUSES_OFFEREDQOSINCOMPATIBLE_H_
