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
/* Header for class org_ros2_rcljava_qos_QoSProfile */

#ifndef ORG_ROS2_RCLJAVA_QOS_QOSPROFILE_H_
#define ORG_ROS2_RCLJAVA_QOS_QOSPROFILE_H_
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_ros2_rcljava_qos_QoSProfile
 * Method:    nativeFromRCL
 * Signature: (J)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_qos_QoSProfile_nativeFromRCL(JNIEnv *, jobject, jlong);

/*
 * Class:     org_ros2_rcljava_qos_QoSProfile
 * Method:    nativeGetHandleFromName
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_qos_QoSProfile_nativeGetHandleFromName(JNIEnv *, jclass, jstring);
#ifdef __cplusplus
}
#endif
#endif  // ORG_ROS2_RCLJAVA_QOS_QOSPROFILE_H_
