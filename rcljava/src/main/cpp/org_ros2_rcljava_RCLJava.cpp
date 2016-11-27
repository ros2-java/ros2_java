// Copyright 2016 Esteve Fernandez <esteve@apache.org>
// Copyright 2016 Mickael Gaillard <mick.gaillard@gmail.com>
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

#include "rmw/rmw.h"
#include "rcl/error_handling.h"
#include "rcl/rcl.h"
#include "rcl/node.h"

#include "rosidl_generator_c/message_type_support.h"

#include "rcljava_common/exceptions.h"
#include "rcljava_common/signatures.h"

#include "rcljava/org_ros2_rcljava_RCLJava.h"
#include "rcljava/utils.h"

jobject convert_rmw_request_id_to_java(JNIEnv *, rmw_request_id_t *);

rmw_request_id_t * convert_rmw_request_id_from_java(JNIEnv *, jobject);

/*
 *
 */
JNIEXPORT void JNICALL Java_org_ros2_rcljava_RCLJava_nativeRCLJavaInit(
  JNIEnv * env,
  jclass,
  jobjectArray arg)
{
  // TODO(esteve): parse args
  int argc = arg != NULL ? env->GetArrayLength(arg) : 0;
  char ** argv = nullptr;
  rcl_allocator_t allocator = rcl_get_default_allocator();

  rcl_ret_t ret = rcl_init(argc, argv, allocator);
  if (ret != RCL_RET_OK) {
    std::string message("Failed to init: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

/*
 *
 */
JNIEXPORT void JNICALL Java_org_ros2_rcljava_RCLJava_nativeShutdown(
  JNIEnv * env,
  jclass)
{
  rcl_ret_t ret = rcl_shutdown();
  if (ret != RCL_RET_OK) {
    std::string message("Failed to shutdown: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

/*
 *
 */
JNIEXPORT jboolean JNICALL Java_org_ros2_rcljava_RCLJava_nativeOk(
  JNIEnv *,
  jclass)
{
  return rcl_ok();
}

/*
 *
 */
JNIEXPORT jstring JNICALL Java_org_ros2_rcljava_RCLJava_nativeGetRMWIdentifier(
  JNIEnv * env,
  jclass)
{
  const char * rmw_implementation_identifier = rmw_get_implementation_identifier();

  return env->NewStringUTF(rmw_implementation_identifier);
}

/*
 *
 */
JNIEXPORT jlong JNICALL Java_org_ros2_rcljava_RCLJava_nativeCreateNodeHandle(
  JNIEnv * env,
  jclass,
  jstring jnode_name)
{
  std::string node_name = jstring2String(env, jnode_name);

  rcl_node_t * node = makeInstance<rcl_node_t>();
  node->impl = nullptr;

  rcl_node_options_t default_options = rcl_node_get_default_options();

  rcl_ret_t ret = rcl_node_init(node, node_name.c_str(), &default_options);
  if (ret != RCL_RET_OK) {
    std::string message("Failed to create node: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);

    return 0;
  }

  jlong node_handle = instance2Handle(node);
  return node_handle;
}


/*
 *
 */
JNIEXPORT jlong JNICALL Java_org_ros2_rcljava_RCLJava_nativeGetZeroInitializedWaitSet(
  JNIEnv *,
  jclass)
{
  // ~ rcl_get_zero_initialized_wait_set();
  rcl_wait_set_t * wait_set = makeInstance<rcl_wait_set_t>();
  wait_set->subscriptions = nullptr;
  wait_set->size_of_subscriptions = 0;
  wait_set->guard_conditions = nullptr;
  wait_set->size_of_guard_conditions = 0;
  wait_set->timers = nullptr;
  wait_set->size_of_timers = 0;
  wait_set->clients = nullptr;
  wait_set->size_of_clients = 0;
  wait_set->services = nullptr;
  wait_set->size_of_services = 0;
  wait_set->impl = nullptr;

  jlong wait_set_handle = instance2Handle(wait_set);
  return wait_set_handle;
}

/*
 *
 */
JNIEXPORT void JNICALL Java_org_ros2_rcljava_RCLJava_nativeWaitSetInit(
  JNIEnv * env,
  jclass,
  jlong wait_set_handle,
  jint number_of_subscriptions,
  jint number_of_guard_conditions,
  jint number_of_timers,
  jint number_of_clients,
  jint number_of_services
)
{
  rcl_wait_set_t * wait_set = handle2Instance<rcl_wait_set_t>(wait_set_handle);

  rcl_ret_t ret = rcl_wait_set_init(
    wait_set,
    number_of_subscriptions,
    number_of_guard_conditions,
    number_of_timers,
    number_of_clients,
    number_of_services,
    rcl_get_default_allocator());

  if (ret != RCL_RET_OK) {
    std::string message("Failed to initialize wait set: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

/*
 *
 */
JNIEXPORT void JNICALL Java_org_ros2_rcljava_RCLJava_nativeWaitSetClearSubscriptions(
  JNIEnv * env,
  jclass,
  jlong wait_set_handle)
{
  rcl_wait_set_t * wait_set = handle2Instance<rcl_wait_set_t>(wait_set_handle);

  rcl_ret_t ret = rcl_wait_set_clear_subscriptions(wait_set);
  if (ret != RCL_RET_OK) {
    std::string message("Failed to clear subscriptions from wait set: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

/*
 *
 */
JNIEXPORT void JNICALL Java_org_ros2_rcljava_RCLJava_nativeWaitSetAddSubscription(
  JNIEnv * env,
  jclass,
  jlong wait_set_handle,
  jlong subscription_handle)
{
  rcl_wait_set_t * wait_set = handle2Instance<rcl_wait_set_t>(wait_set_handle);
  rcl_subscription_t * subscription =
    handle2Instance<rcl_subscription_t>(subscription_handle);

  rcl_ret_t ret = rcl_wait_set_add_subscription(wait_set, subscription);
  if (ret != RCL_RET_OK) {
    std::string message("Failed to add subscription to wait set: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

/*
 *
 */
JNIEXPORT void JNICALL Java_org_ros2_rcljava_RCLJava_nativeWait(
  JNIEnv * env,
  jclass,
  jlong wait_set_handle)
{
  rcl_wait_set_t * wait_set = handle2Instance<rcl_wait_set_t>(wait_set_handle);

  rcl_ret_t ret = rcl_wait(wait_set, RCL_S_TO_NS(1));
  if (ret != RCL_RET_OK && ret != RCL_RET_TIMEOUT) {
    std::string message("Failed to wait on wait set: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

/*
 *
 */
JNIEXPORT jobject JNICALL Java_org_ros2_rcljava_RCLJava_nativeTake(
  JNIEnv * env,
  jclass,
  jlong subscription_handle,
  jclass jmessage_class)
{
  rcl_subscription_t * subscription =
    handle2Instance<rcl_subscription_t>(subscription_handle);

  void * taken_msg = jclass2Message(env, jmessage_class);

  rcl_ret_t ret = rcl_take(subscription, taken_msg, nullptr);
  if (ret != RCL_RET_OK && ret != RCL_RET_SUBSCRIPTION_TAKE_FAILED) {
    std::string message("Failed to take from a subscription: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }

  if (ret != RCL_RET_SUBSCRIPTION_TAKE_FAILED) {
    jobject jtaken_msg = jclass2JMessage(env, jmessage_class, taken_msg);

    return jtaken_msg;
  }

  return nullptr;
}

/*
 *
 */
JNIEXPORT void JNICALL Java_org_ros2_rcljava_RCLJava_nativeWaitSetFini(
  JNIEnv * env,
  jclass,
  jlong wait_set_handle)
{
  rcl_wait_set_t * wait_set = handle2Instance<rcl_wait_set_t>(wait_set_handle);

  rcl_ret_t ret = rcl_wait_set_fini(wait_set);
  if (ret != RCL_RET_OK) {
    std::string message("Failed to release wait set: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}


JNIEXPORT jobject JNICALL Java_org_ros2_rcljava_RCLJava_nativeGetNodeNames(
  JNIEnv *,
  jclass)
{
  return NULL;
}


JNIEXPORT void JNICALL Java_org_ros2_rcljava_RCLJava_nativeWaitSetClearServices(
  JNIEnv * env,
  jclass,
  jlong wait_set_handle)
{
  rcl_wait_set_t * wait_set = handle2Instance<rcl_wait_set_t>(wait_set_handle);

  rcl_ret_t ret = rcl_wait_set_clear_services(wait_set);
  if (ret != RCL_RET_OK) {
    std::string message("Failed to clear services from wait set: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

JNIEXPORT void JNICALL Java_org_ros2_rcljava_RCLJava_nativeWaitSetAddService(
  JNIEnv * env,
  jclass,
  jlong wait_set_handle,
  jlong service_handle)
{
  rcl_wait_set_t * wait_set = handle2Instance<rcl_wait_set_t>(wait_set_handle);
  rcl_service_t * service = handle2Instance<rcl_service_t>(service_handle);

  rcl_ret_t ret = rcl_wait_set_add_service(wait_set, service);
  if (ret != RCL_RET_OK) {
    std::string message("Failed to add service to wait set: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

JNIEXPORT void JNICALL Java_org_ros2_rcljava_RCLJava_nativeWaitSetClearClients(
  JNIEnv * env,
  jclass,
  jlong wait_set_handle)
{
  rcl_wait_set_t * wait_set = handle2Instance<rcl_wait_set_t>(wait_set_handle);

  rcl_ret_t ret = rcl_wait_set_clear_clients(wait_set);
  if (ret != RCL_RET_OK) {
    std::string message("Failed to clear clients from wait set: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

JNIEXPORT void JNICALL Java_org_ros2_rcljava_RCLJava_nativeWaitSetAddClient(
  JNIEnv * env,
  jclass,
  jlong wait_set_handle,
  jlong client_handle)
{
  rcl_wait_set_t * wait_set = handle2Instance<rcl_wait_set_t>(wait_set_handle);
  rcl_client_t * client = handle2Instance<rcl_client_t>(client_handle);

  rcl_ret_t ret = rcl_wait_set_add_client(wait_set, client);
  if (ret != RCL_RET_OK) {
    std::string message("Failed to add client to wait set: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

JNIEXPORT jobject JNICALL Java_org_ros2_rcljava_RCLJava_nativeTakeRequest(
  JNIEnv * env,
  jclass,
  jlong service_handle,
  jlong jrequest_from_java_converter_handle,
  jlong jrequest_to_java_converter_handle,
  jobject jrequest_msg)
{
  assert(service_handle != 0);
  assert(jrequest_from_java_converter_handle != 0);
  assert(jrequest_to_java_converter_handle != 0);
  assert(jrequest_msg != nullptr);

  rcl_service_t * service = handle2Instance<rcl_service_t>(service_handle);

  convert_from_java_signature convert_from_java =
    reinterpret_cast<convert_from_java_signature>(jrequest_from_java_converter_handle);

  convert_to_java_signature convert_to_java =
    reinterpret_cast<convert_to_java_signature>(jrequest_to_java_converter_handle);

  void * taken_msg = convert_from_java(jrequest_msg, nullptr);

  rmw_request_id_t header;

  rcl_ret_t ret = rcl_take_request(service, &header, taken_msg);
  if (ret != RCL_RET_OK && ret != RCL_RET_SERVICE_TAKE_FAILED) {
    std::string message("Failed to take request from a service: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
    return nullptr;
  }

  if (ret != RCL_RET_SERVICE_TAKE_FAILED) {
    jobject jtaken_msg = convert_to_java(taken_msg, jrequest_msg);
    assert(jtaken_msg != nullptr);

    jobject jheader = convert_rmw_request_id_to_java(env, &header);
    return jheader;
  }

  return nullptr;
}

JNIEXPORT void JNICALL Java_org_ros2_rcljava_RCLJava_nativeSendServiceResponse(
  JNIEnv * env,
  jclass,
  jlong service_handle,
  jobject jrequest_id,
  jlong jresponse_from_java_converter_handle,
  jlong jresponse_to_java_converter_handle,
  jobject jresponse_msg)
{
  assert(service_handle != 0);
  assert(jresponse_from_java_converter_handle != 0);
  assert(jresponse_to_java_converter_handle != 0);
  assert(jresponse_msg != nullptr);

  rcl_service_t * service = handle2Instance<rcl_service_t>(service_handle);

  convert_from_java_signature convert_from_java =
    reinterpret_cast<convert_from_java_signature>(jresponse_from_java_converter_handle);

  void * response_msg = convert_from_java(jresponse_msg, nullptr);

  rmw_request_id_t * request_id = convert_rmw_request_id_from_java(env, jrequest_id);

  rcl_ret_t ret = rcl_send_response(service, request_id, response_msg);
  if (ret != RCL_RET_OK) {
    std::string message("Failed to send response from a service: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

JNIEXPORT jobject JNICALL Java_org_ros2_rcljava_RCLJava_nativeTakeResponse(
  JNIEnv * env,
  jclass,
  jlong client_handle,
  jlong jresponse_from_java_converter_handle,
  jlong jresponse_to_java_converter_handle,
  jobject jresponse_msg)
{
  assert(client_handle != 0);
  assert(jresponse_from_java_converter_handle != 0);
  assert(jresponse_to_java_converter_handle != 0);
  assert(jresponse_msg != nullptr);

  rcl_client_t * client = handle2Instance<rcl_client_t>(client_handle);

  convert_from_java_signature convert_from_java =
    reinterpret_cast<convert_from_java_signature>(jresponse_from_java_converter_handle);

  convert_to_java_signature convert_to_java =
    reinterpret_cast<convert_to_java_signature>(jresponse_to_java_converter_handle);

  void * taken_msg = convert_from_java(jresponse_msg, nullptr);

  rmw_request_id_t header;

  rcl_ret_t ret = rcl_take_response(client, &header, taken_msg);
  if (ret != RCL_RET_OK && ret != RCL_RET_CLIENT_TAKE_FAILED) {
    std::string message("Failed to take request from a service: " +
      std::string(rcl_get_error_string_safe()));
    throwException(env, message);
    return nullptr;
  }

  if (ret != RCL_RET_CLIENT_TAKE_FAILED) {
    jobject jtaken_msg = convert_to_java(taken_msg, jresponse_msg);

    assert(jtaken_msg != nullptr);

    jobject jheader = convert_rmw_request_id_to_java(env, &header);
    return jheader;
  }

  return nullptr;
}

JNIEXPORT jlong JNICALL Java_org_ros2_rcljava_RCLJava_nativeConvertQoSProfileToHandle(
  JNIEnv *,
  jclass,
  jint history,
  jint depth,
  jint reliability,
  jint durability)
{
  rmw_qos_profile_t * qos_profile =
    static_cast<rmw_qos_profile_t *>(malloc(sizeof(rmw_qos_profile_t)));
  qos_profile->history = static_cast<rmw_qos_history_policy_t>(history);
  qos_profile->depth = depth;
  qos_profile->reliability = static_cast<rmw_qos_reliability_policy_t>(reliability);
  qos_profile->durability = static_cast<rmw_qos_durability_policy_t>(durability);
  return reinterpret_cast<jlong>(qos_profile);
}

JNIEXPORT void JNICALL Java_org_ros2_rcljava_RCLJava_nativeDisposeQoSProfile(
  JNIEnv *,
  jclass,
  jlong qos_profile_handle)
{
  rmw_qos_profile_t * qos_profile =
    reinterpret_cast<rmw_qos_profile_t *>(qos_profile_handle);
  free(qos_profile);
}
