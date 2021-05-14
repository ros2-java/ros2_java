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
#include "rcl/rcl.h"
#include "rcl_action/rcl_action.h"
#include "rcpputils/scope_exit.hpp"
#include "rosidl_runtime_c/message_type_support_struct.h"

#include "rcljava_common/exceptions.hpp"
#include "rcljava_common/signatures.hpp"

#include "org_ros2_rcljava_action_ActionServerImpl.h"

#include "./convert.hpp"

using rcljava_common::exceptions::rcljava_throw_exception;
using rcljava_common::exceptions::rcljava_throw_rclexception;
using rcljava_common::signatures::convert_from_java_signature;
using rcljava_common::signatures::convert_to_java_signature;
using rcljava_common::signatures::destroy_ros_message_signature;

JNIEXPORT jintArray
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_nativeGetNumberOfEntities(
  JNIEnv * env, jclass, jlong action_server_handle)
{
  size_t num_subscriptions;
  size_t num_guard_conditions;
  size_t num_timers;
  size_t num_clients;
  size_t num_services;
  rcl_action_server_t * action_server = reinterpret_cast<rcl_action_server_t *>(
    action_server_handle);
  rcl_ret_t ret = rcl_action_server_wait_set_get_num_entities(
    action_server,
    &num_subscriptions,
    &num_guard_conditions,
    &num_timers,
    &num_clients,
    &num_services);
  if (ret != RCL_RET_OK) {
    std::string msg =
      "Failed to get number of entities for an action server: " +
      std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
  }
  jintArray result = env->NewIntArray(5);
  jint temp_result[5] = {
    static_cast<jint>(num_subscriptions),
    static_cast<jint>(num_guard_conditions),
    static_cast<jint>(num_timers),
    static_cast<jint>(num_clients),
    static_cast<jint>(num_services)
  };
  env->SetIntArrayRegion(result, 0, 5, temp_result);
  return result;
}

JNIEXPORT jbooleanArray
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_nativeGetReadyEntities(
  JNIEnv * env, jclass, jlong action_server_handle, jlong wait_set_handle)
{
  rcl_action_server_t * action_server = reinterpret_cast<rcl_action_server_t *>(
    action_server_handle);
  rcl_wait_set_t * wait_set = reinterpret_cast<rcl_wait_set_t *>(wait_set_handle);

  bool is_goal_request_ready = false;
  bool is_cancel_request_ready = false;
  bool is_result_request_ready = false;
  bool is_goal_expired = false;
  rcl_ret_t ret = rcl_action_server_wait_set_get_entities_ready(
    wait_set,
    action_server,
    &is_goal_request_ready,
    &is_cancel_request_ready,
    &is_result_request_ready,
    &is_goal_expired);
  if (RCL_RET_OK != ret) {
    std::string msg = "Failed to get ready entities for action server: " +
      std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
    return NULL;
  }

  jbooleanArray result = env->NewBooleanArray(4);
  jboolean temp_result[4] = {
    is_goal_request_ready,
    is_cancel_request_ready,
    is_result_request_ready,
    is_goal_expired
  };
  env->SetBooleanArrayRegion(result, 0, 4, temp_result);
  return result;
}

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_action_ActionServerImpl_nativeDispose(
  JNIEnv * env, jclass, jlong node_handle, jlong action_server_handle)
{
  if (action_server_handle == 0) {
    // everything is ok, already destroyed
    return;
  }

  if (node_handle == 0) {
    // TODO(jacobperron): throw exception
    return;
  }

  rcl_node_t * node = reinterpret_cast<rcl_node_t *>(node_handle);

  assert(node != NULL);

  rcl_action_server_t * action_server = reinterpret_cast<rcl_action_server_t *>(
    action_server_handle);

  assert(action_server != NULL);

  rcl_ret_t ret = rcl_action_server_fini(action_server, node);

  if (ret != RCL_RET_OK) {
    std::string msg = "Failed to destroy action server: " +
      std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
  }
}

JNIEXPORT jlong JNICALL
Java_org_ros2_rcljava_action_ActionServerImpl_nativeCreateActionServer(
  JNIEnv * env,
  jobject,
  jlong node_handle,
  jlong clock_handle,
  jclass jaction_class,
  jstring jaction_name)
{
  jmethodID mid = env->GetStaticMethodID(jaction_class, "getActionTypeSupport", "()J");
  assert(mid != NULL);

  jlong jts = env->CallStaticLongMethod(jaction_class, mid);
  assert(jts != 0);

  const char * action_name = env->GetStringUTFChars(jaction_name, 0);

  rcl_node_t * node = reinterpret_cast<rcl_node_t *>(node_handle);
  rcl_clock_t * clock = reinterpret_cast<rcl_clock_t *>(clock_handle);

  rosidl_action_type_support_t * ts = reinterpret_cast<rosidl_action_type_support_t *>(jts);

  rcl_action_server_t * action_server = static_cast<rcl_action_server_t *>(
    malloc(sizeof(rcl_action_server_t)));
  *action_server = rcl_action_get_zero_initialized_server();
  rcl_action_server_options_t action_server_ops = rcl_action_server_get_default_options();

  rcl_ret_t ret = rcl_action_server_init(
    action_server, node, clock, ts, action_name, &action_server_ops);
  env->ReleaseStringUTFChars(jaction_name, action_name);

  if (ret != RCL_RET_OK) {
    std::string msg = "Failed to create action server: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
    free(action_server);
    return 0;
  }

  jlong jaction_server = reinterpret_cast<jlong>(action_server);
  return jaction_server;
}

#define RCLJAVA_ACTION_SERVER_TAKE_REQUEST(Type) \
  do { \
    assert(jrequest_from_java_converter_handle != 0); \
    assert(jrequest_to_java_converter_handle != 0); \
    rcl_action_server_t * action_server = reinterpret_cast<rcl_action_server_t *>( \
      action_server_handle); \
    convert_from_java_signature convert_from_java = \
      reinterpret_cast<convert_from_java_signature>(jrequest_from_java_converter_handle); \
    convert_to_java_signature convert_to_java = \
      reinterpret_cast<convert_to_java_signature>(jrequest_to_java_converter_handle); \
    destroy_ros_message_signature destroy_ros_message = \
      reinterpret_cast<destroy_ros_message_signature>(jrequest_destructor_handle); \
    void * taken_msg = convert_from_java(jrequest_msg, nullptr); \
    rmw_request_id_t header; \
    rcl_ret_t ret = rcl_action_take_ ## Type ## _request(action_server, &header, taken_msg); \
    if (ret != RCL_RET_OK && ret != RCL_RET_ACTION_SERVER_TAKE_FAILED) { \
      destroy_ros_message(taken_msg); \
      std::string msg = \
        "Failed to take " #Type " request: " + std::string(rcl_get_error_string().str); \
      rcl_reset_error(); \
      rcljava_throw_rclexception(env, ret, msg); \
      return nullptr; \
    } \
    if (RCL_RET_OK == ret) { \
      convert_to_java(taken_msg, jrequest_msg); \
      destroy_ros_message(taken_msg); \
      jobject jheader = rcljava::convert_rmw_request_id_to_java(env, &header); \
      return jheader; \
    } \
    destroy_ros_message(taken_msg); \
    return nullptr; \
  } \
  while (0)

#define RCLJAVA_ACTION_SERVER_SEND_RESPONSE(Type) \
  do { \
    assert(jresponse_from_java_converter_handle != 0); \
    assert(jresponse_destructor_handle != 0); \
    rcl_action_server_t * action_server = reinterpret_cast<rcl_action_server_t *>( \
      action_server_handle); \
    convert_from_java_signature convert_from_java = \
      reinterpret_cast<convert_from_java_signature>(jresponse_from_java_converter_handle); \
    void * response_msg = convert_from_java(jresponse_msg, nullptr); \
    rmw_request_id_t * request_id = rcljava::convert_rmw_request_id_from_java(env, jrequest_id); \
    rcl_ret_t ret = rcl_action_send_ ## Type ## _response( \
      action_server, request_id, response_msg); \
    destroy_ros_message_signature destroy_ros_message = \
      reinterpret_cast<destroy_ros_message_signature>(jresponse_destructor_handle); \
    destroy_ros_message(response_msg); \
    if (ret != RCL_RET_OK) { \
      std::string msg = \
        "Failed to send " #Type " response: " + std::string(rcl_get_error_string().str); \
      rcl_reset_error(); \
      rcljava_throw_rclexception(env, ret, msg); \
    } \
  } \
  while (0)

JNIEXPORT jobject
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_nativeTakeGoalRequest(
  JNIEnv * env, jclass, jlong action_server_handle, jlong jrequest_from_java_converter_handle,
  jlong jrequest_to_java_converter_handle, jlong jrequest_destructor_handle, jobject jrequest_msg)
{
  RCLJAVA_ACTION_SERVER_TAKE_REQUEST(goal);
}

JNIEXPORT jobject
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_nativeTakeCancelRequest(
  JNIEnv * env, jclass, jlong action_server_handle, jlong jrequest_from_java_converter_handle,
  jlong jrequest_to_java_converter_handle, jlong jrequest_destructor_handle, jobject jrequest_msg)
{
  RCLJAVA_ACTION_SERVER_TAKE_REQUEST(cancel);
}

JNIEXPORT jobject
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_nativeTakeResultRequest(
  JNIEnv * env, jclass, jlong action_server_handle, jlong jrequest_from_java_converter_handle,
  jlong jrequest_to_java_converter_handle, jlong jrequest_destructor_handle, jobject jrequest_msg)
{
  RCLJAVA_ACTION_SERVER_TAKE_REQUEST(result);
}

JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_nativeSendGoalResponse(
  JNIEnv * env, jclass, jlong action_server_handle, jobject jrequest_id,
  jlong jresponse_from_java_converter_handle, jlong jresponse_destructor_handle,
  jobject jresponse_msg)
{
  RCLJAVA_ACTION_SERVER_SEND_RESPONSE(goal);
}

JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_nativeSendCancelResponse(
  JNIEnv * env, jclass, jlong action_server_handle, jobject jrequest_id,
  jlong jresponse_from_java_converter_handle, jlong jresponse_destructor_handle,
  jobject jresponse_msg)
{
  RCLJAVA_ACTION_SERVER_SEND_RESPONSE(cancel);
}

JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_nativeSendResultResponse(
  JNIEnv * env, jclass, jlong action_server_handle, jobject jrequest_id,
  jlong jresponse_from_java_converter_handle, jlong jresponse_destructor_handle,
  jobject jresponse_msg)
{
  RCLJAVA_ACTION_SERVER_SEND_RESPONSE(result);
}

JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_nativeProcessCancelRequest(
  JNIEnv * env, jclass,
  jlong action_server_handle,
  jlong jrequest_from_java_converter_handle,
  jlong jrequest_destructor_handle,
  jlong jresponse_to_java_converter_handle,
  jobject jrequest_msg,
  jobject jresponse_msg)
{
  assert(jrequest_from_java_converter_handle != 0);
  assert(jrequest_destructor_handle != 0);
  assert(jresponse_to_java_converter_handle != 0);

  rcl_action_server_t * action_server = reinterpret_cast<rcl_action_server_t *>(
    action_server_handle);
  convert_from_java_signature request_convert_from_java =
    reinterpret_cast<convert_from_java_signature>(jrequest_from_java_converter_handle);
  convert_to_java_signature response_convert_to_java = reinterpret_cast<convert_to_java_signature>(
    jresponse_to_java_converter_handle);
  destroy_ros_message_signature request_destroy_ros_message =
    reinterpret_cast<destroy_ros_message_signature>(jrequest_destructor_handle);

  rcl_action_cancel_request_t * request_msg = reinterpret_cast<rcl_action_cancel_request_t *>(
    request_convert_from_java(jrequest_msg, nullptr));
  rcl_action_cancel_response_t response_msg = rcl_action_get_zero_initialized_cancel_response();

  rcl_ret_t ret = rcl_action_process_cancel_request(
    action_server, request_msg, &response_msg);
  request_destroy_ros_message(request_msg);
  if (ret != RCL_RET_OK) {
    std::string msg = \
      "Failed to process cancel request: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
    return;
  }

  response_convert_to_java(&response_msg, jresponse_msg);
}

JNIEXPORT jboolean
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_nativeCheckGoalExists(
  JNIEnv *, jclass,
  jlong jaction_server,
  jobject jgoal_info,
  jlong jgoal_info_from_java_converter_handle,
  jlong jgoal_info_destructor_handle)
{
  assert(0 != jgoal_info_from_java_converter_handle);
  assert(0 != jgoal_info_destructor_handle);

  rcl_action_server_t * action_server = reinterpret_cast<rcl_action_server_t *>(
    jaction_server);
  convert_from_java_signature convert_from_java =
    reinterpret_cast<convert_from_java_signature>(jgoal_info_from_java_converter_handle);
  destroy_ros_message_signature destroy_ros_message =
    reinterpret_cast<destroy_ros_message_signature>(jgoal_info_destructor_handle);

  rcl_action_goal_info_t * goal_info =
    reinterpret_cast<rcl_action_goal_info_t *>(convert_from_java(jgoal_info, nullptr));
  bool exists = rcl_action_server_goal_exists(action_server, goal_info);
  destroy_ros_message(goal_info);

  return exists;
}

JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_nativePublishStatus(
  JNIEnv * env, jclass, jlong jaction_server)
{
  assert(0 != jaction_server);
  auto * action_server = reinterpret_cast<rcl_action_server_t *>(jaction_server);
  rcl_action_goal_status_array_t status_message =
    rcl_action_get_zero_initialized_goal_status_array();
  rcl_ret_t ret = rcl_action_get_goal_status_array(action_server, &status_message);
  if (ret != RCL_RET_OK) {
    std::string msg = \
      "Failed to get goal status array: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
    return;
  }

  ret = rcl_action_publish_status(action_server, &status_message);

  if (ret != RCL_RET_OK) {
    std::string msg = \
      "Failed to publish status array: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
    return;
  }
}

JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_nativePublishFeedbackMessage(
  JNIEnv * env, jclass, jlong jaction_server, jobject jfeedback_msg,
  jlong jfeedback_from_java, jlong jfeedback_destroy)
{
  assert(0 != jaction_server);
  assert(nullptr != jfeedback_msg);
  assert(0 != jfeedback_from_java);
  assert(0 != jfeedback_destroy);

  auto * action_server = reinterpret_cast<rcl_action_server_t *>(jaction_server);
  auto destroy_feedback = reinterpret_cast<destroy_ros_message_signature>(jfeedback_destroy);
  auto from_java_feedback = reinterpret_cast<convert_from_java_signature>(jfeedback_from_java);
  void * feedback = from_java_feedback(jfeedback_msg, nullptr);
  auto destroy_feedback_scope_exit = rcpputils::make_scope_exit(
    [feedback, destroy_feedback]() {destroy_feedback(feedback);});
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  if (!feedback) {
    return;
  }
  rcl_ret_t ret = rcl_action_publish_feedback(action_server, feedback);
  RCLJAVA_COMMON_THROW_FROM_RCL(env, ret, "Failed to publish feedback");
}

JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_nativeNotifyGoalDone(
  JNIEnv * env, jclass, jlong jaction_server)
{
  assert(0 != jaction_server);
  auto * action_server = reinterpret_cast<rcl_action_server_t *>(jaction_server);
  rcl_ret_t ret = rcl_action_notify_goal_done(action_server);
  RCLJAVA_COMMON_THROW_FROM_RCL(env, ret, "Failed to notify goal done");
}

JNIEXPORT void
JNICALL Java_org_ros2_rcljava_action_ActionServerImpl_nativeExpireGoals(
  JNIEnv * env, jclass, jlong jaction_server, jobject jgoal_info,
  jlong jgoal_info_to_java, jobject jaccept)
{
  assert(0 != jaction_server);
  auto * action_server = reinterpret_cast<rcl_action_server_t *>(jaction_server);

  rcl_action_goal_info_t expired_goal;
  size_t num_expired = 1;

  // Loop in case more than 1 goal expired
  jclass jaccept_class = env->GetObjectClass(jaccept);
  if (!jaccept_class) {
    rcljava_throw_exception(
      env, "java/lang/IllegalStateException",
      "nativeExpireGoals(): could not find jaccept class");
    return;
  }
  jmethodID jaccept_mid = env->GetMethodID(
    jaccept_class, "accept", "(Laction_msgs/msg/GoalInfo;)V");
  if (!jaccept_mid) {
    rcljava_throw_exception(
      env, "java/lang/IllegalStateException",
      "nativeExpireGoals(): could not find jaccept accept method");
    return;
  }
  while (num_expired > 0u) {
    rcl_ret_t ret;
    ret = rcl_action_expire_goals(action_server, &expired_goal, 1, &num_expired);
    RCLJAVA_COMMON_THROW_FROM_RCL(env, ret, "Failed to expire goals");
    if (num_expired) {
      auto goal_info_to_java = reinterpret_cast<convert_to_java_signature>(jgoal_info_to_java);
      goal_info_to_java(&expired_goal, jgoal_info);
      env->CallVoidMethod(jaccept, jaccept_mid, jgoal_info);
      RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
    }
  }
}
