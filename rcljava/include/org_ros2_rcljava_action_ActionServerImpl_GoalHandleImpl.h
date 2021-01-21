// Copyright 2020 ros2-java contributors
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
/* Header for class org_ros2_rcljava_action_ActionServerImpl_GoalHandleImpl */

#ifndef ORG_ROS2_RCLJAVA_ACTION_ACTIONSERVERIMPL_GOALHANDLEIMPL_H_
#define ORG_ROS2_RCLJAVA_ACTION_ACTIONSERVERIMPL_GOALHANDLEIMPL_H_
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     org_ros2_rcljava_action_ActionServerImpl$GoalHandleImpl
 * Method:    nativeAcceptNewGoal
 * Signature: (JJJLorg/ros2/rcljava/interfaces/MessageDefinition;)J
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_00024GoalHandleImpl_nativeAcceptNewGoal(
  JNIEnv *, jclass, jlong, jlong, jlong, jobject);

/*
 * Class:     org_ros2_rcljava_action_ActionServerImpl$GoalHandleImpl
 * Method:    nativeGetStatus
 * Signature: (J)I
 */
JNIEXPORT int
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_00024GoalHandleImpl_nativeGetStatus(
  JNIEnv *, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_action_ActionServerImpl$GoalHandleImpl
 * Method:    nativeGoalEventExecute
 * Signature: (J)
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_00024GoalHandleImpl_nativeGoalEventExecute(
  JNIEnv * env, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_action_ActionServerImpl$GoalHandleImpl
 * Method:    nativeGoalEventCancelGoal
 * Signature: (J)
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_00024GoalHandleImpl_nativeGoalEventCancelGoal(
  JNIEnv * env, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_action_ActionServerImpl$GoalHandleImpl
 * Method:    nativeGoalEventSucceed
 * Signature: (J)
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_00024GoalHandleImpl_nativeGoalEventSucceed(
  JNIEnv * env, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_action_ActionServerImpl$GoalHandleImpl
 * Method:    nativeGoalEventAbort
 * Signature: (J)
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_00024GoalHandleImpl_nativeGoalEventAbort(
  JNIEnv * env, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_action_ActionServerImpl$GoalHandleImpl
 * Method:    nativeGoalEventCanceled
 * Signature: (J)
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_00024GoalHandleImpl_nativeGoalEventCanceled(
  JNIEnv * env, jclass, jlong);

/*
 * Class:     org_ros2_rcljava_action_ActionServerImpl$GoalHandleImpl
 * Method:    nativeDispose
 * Signature: (J)
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_00024GoalHandleImpl_nativeDipose(
  JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif  // ORG_ROS2_RCLJAVA_ACTION_ACTIONSERVERIMPL_GOALHANDLEIMPL_H__
