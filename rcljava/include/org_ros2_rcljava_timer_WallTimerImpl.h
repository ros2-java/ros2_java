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
/* Header for class org_ros2_rcljava_timer_WallTimerImpl */

#ifndef ORG_ROS2_RCLJAVA_TIMER_WALLTIMERIMPL_H_
#define ORG_ROS2_RCLJAVA_TIMER_WALLTIMERIMPL_H_
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     org_ros2_rcljava_timer_WallTimerImpl
 * Method:    nativeDispose
 * Signature: (J)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_timer_WallTimerImpl_nativeDispose(JNIEnv *, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_timer_WallTimerImpl
 * Method:    nativeIsReady
 * Signature: (J)Z
 */
JNIEXPORT jboolean
JNICALL Java_org_ros2_rcljava_timer_WallTimerImpl_nativeIsReady(JNIEnv *, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_timer_WallTimerImpl
 * Method:    nativeIsCanceled
 * Signature: (J)Z
 */
JNIEXPORT jboolean
JNICALL Java_org_ros2_rcljava_timer_WallTimerImpl_nativeIsCanceled(JNIEnv *, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_timer_WallTimerImpl
 * Method:    nativeReset
 * Signature: (J)Z
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_timer_WallTimerImpl_nativeReset(JNIEnv *, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_timer_WallTimerImpl
 * Method:    nativeCancel
 * Signature: (J)Z
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_timer_WallTimerImpl_nativeCancel(JNIEnv *, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_timer_WallTimerImpl
 * Method:    nativeTimeUntilNextCall
 * Signature: (J)J
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_timer_WallTimerImpl_nativeTimeUntilNextCall(JNIEnv *, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_timer_WallTimerImpl
 * Method:    nativeTimeSinceLastCall
 * Signature: (J)J
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_timer_WallTimerImpl_nativeTimeSinceLastCall(JNIEnv *, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_timer_WallTimerImpl
 * Method:    nativeGetTimerPeriodNS
 * Signature: (J)J
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_timer_WallTimerImpl_nativeGetTimerPeriodNS(JNIEnv *, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_timer_WallTimerImpl
 * Method:    nativeSetTimerPeriodNS
 * Signature: (JJ)Z
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_timer_WallTimerImpl_nativeSetTimerPeriodNS(
  JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     org_ros2_rcljava_timer_WallTimerImpl
 * Method:    nativeCallTimer
 * Signature: (J)Z
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_timer_WallTimerImpl_nativeCallTimer(JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif  // ORG_ROS2_RCLJAVA_TIMER_WALLTIMERIMPL_H_
