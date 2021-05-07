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

#include <cassert>
#include <cstdlib>
#include <string>

#include "rcl/error_handling.h"
#include "rcl_action/rcl_action.h"

#include "rcljava_common/exceptions.hpp"
#include "rcljava_common/signatures.hpp"

#include "org_ros2_rcljava_action_ActionServerImpl_GoalHandleImpl.h"

using rcljava_common::exceptions::rcljava_throw_exception;
using rcljava_common::exceptions::rcljava_throw_rclexception;
using rcljava_common::signatures::convert_from_java_signature;
using rcljava_common::signatures::destroy_ros_message_signature;

JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_00024GoalHandleImpl_nativeAcceptNewGoal(
  JNIEnv * env, jclass, jlong action_server_handle,
  jlong jgoal_info_from_java_converter_handle, jlong jgoal_info_destructor_handle,
  jobject jgoal_info_message)
{
  assert(jgoal_info_from_java_converter_handle != 0);

  rcl_action_server_t * action_server = reinterpret_cast<rcl_action_server_t *>(
    action_server_handle);
  convert_from_java_signature convert_from_java =
    reinterpret_cast<convert_from_java_signature>(jgoal_info_from_java_converter_handle);
  destroy_ros_message_signature destroy_ros_message =
    reinterpret_cast<destroy_ros_message_signature>(jgoal_info_destructor_handle);

  rcl_action_goal_info_t * goal_info_message =
    reinterpret_cast<rcl_action_goal_info_t *>(convert_from_java(jgoal_info_message, nullptr));

  rcl_action_goal_handle_t * goal_handle = rcl_action_accept_new_goal(
    action_server, goal_info_message);
  destroy_ros_message(goal_info_message);
  if (!goal_handle) {
    std::string msg = "Failed to accept new goal: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    // '1' is arbitrary since we don't have a specific return code
    rcljava_throw_rclexception(env, 1, msg);
    return 0;
  }

  jlong jgoal_handle = reinterpret_cast<jlong>(goal_handle);
  return jgoal_handle;
}

JNIEXPORT int
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_00024GoalHandleImpl_nativeGetStatus(
  JNIEnv * env, jclass, jlong jgoal_handle)
{
  rcl_action_goal_handle_t * goal_handle = reinterpret_cast<rcl_action_goal_handle_t *>(
    jgoal_handle);
  rcl_action_goal_state_t status;
  rcl_ret_t ret = rcl_action_goal_handle_get_status(goal_handle, &status);
  if (RCL_RET_OK != ret) {
    std::string msg = "Failed to get goal status: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
    return static_cast<jint>(GOAL_STATE_UNKNOWN);
  }

  return static_cast<jint>(status);
}

JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_00024GoalHandleImpl_nativeUpdateGoalState(
  JNIEnv * env, jclass, jlong jgoal_handle, jlong jto_status)
{
  assert(0 != jgoal_handle);
  rcl_action_goal_event_t event = GOAL_EVENT_NUM_EVENTS;
  switch (jto_status) {
    case GOAL_STATE_EXECUTING:
      event = GOAL_EVENT_EXECUTE;
      break;
    case GOAL_STATE_CANCELING:
      event = GOAL_EVENT_CANCEL_GOAL;
      break;
    case GOAL_STATE_SUCCEEDED:
      event = GOAL_EVENT_SUCCEED;
      break;
    case GOAL_STATE_CANCELED:
      event = GOAL_EVENT_CANCELED;
      break;
    case GOAL_STATE_ABORTED:
      event = GOAL_EVENT_ABORT;
      break;
    case GOAL_STATE_ACCEPTED:  // fallthrough
    case GOAL_STATE_UNKNOWN:  // fallthrough
    default:
      break;
  }
  if (event >= GOAL_EVENT_NUM_EVENTS) {
    rcljava_throw_exception(
      env, "java/lang/IllegalStateException", "nativeUpdateGoalState(): Unknown event");
    return;
  }

  auto * goal_handle = reinterpret_cast<rcl_action_goal_handle_t *>(jgoal_handle);
  rcl_ret_t ret = rcl_action_update_goal_state(goal_handle, event);
  if (RCL_RET_OK != ret) {
    std::string msg = "Failed to update goal state with event: " +
      std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
  }
}

JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_00024GoalHandleImpl_nativeDipose(
  JNIEnv * env, jclass, jlong jgoal_handle)
{
  rcl_action_goal_handle_t * goal_handle = reinterpret_cast<rcl_action_goal_handle_t *>(
    jgoal_handle);
  if (!goal_handle) {
    // Nothing to dispose
    return;
  }

  rcl_ret_t ret = rcl_action_goal_handle_fini(goal_handle);
  if (RCL_RET_OK != ret) {
    std::string msg = "Failed to finalize goal handle: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
  }
}
