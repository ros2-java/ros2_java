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
/* Header for class org_ros2_rcljava_Time */

#ifndef ORG_ROS2_RCLJAVA_TIME_H_
#define ORG_ROS2_RCLJAVA_TIME_H_
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_ros2_rcljava_Time
 * Method:    nativeRCLSystemTimeNow
 * Signature: ()J
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_Time_nativeRCLSystemTimeNow(JNIEnv *, jclass);

/*
 * Class:     org_ros2_rcljava_Time
 * Method:    nativeRCLSteadyTimeNow
 * Signature: ()J
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_Time_nativeRCLSteadyTimeNow(JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif  // ORG_ROS2_RCLJAVA_TIME_H_
