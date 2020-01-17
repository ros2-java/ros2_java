// Copyright 2019 Open Source Robotics Foundation, Inc.
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
/* Header for class org_ros2_rcljava_time_Clock */

#ifndef ORG_ROS2_RCLJAVA_TIME_CLOCK_H_
#define ORG_ROS2_RCLJAVA_TIME_CLOCK_H_
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_ros2_rcljava_time_Clock
 * Method:    nativeCreateClock
 * Signature: (Lorg/ros2/rcljava/time/ClockType;)J
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_time_Clock_nativeCreateClockHandle(JNIEnv *, jclass, jobject);

/*
 * Class:     org_ros2_rcljava_time_Clock
 * Method:    nativeDispose
 * Signature: (J)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_time_Clock_nativeDispose(JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif  // ORG_ROS2_RCLJAVA_TIME_CLOCK_H_
