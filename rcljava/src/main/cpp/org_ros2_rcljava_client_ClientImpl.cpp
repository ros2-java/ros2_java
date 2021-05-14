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

#include <cassert>
#include <cstdlib>
#include <string>

#include "rcl/error_handling.h"
#include "rcl/graph.h"
#include "rcl/node.h"
#include "rcl/rcl.h"
#include "rmw/rmw.h"
#include "rosidl_runtime_c/message_type_support_struct.h"

#include "rcljava_common/exceptions.hpp"
#include "rcljava_common/signatures.hpp"

#include "org_ros2_rcljava_client_ClientImpl.h"

using rcljava_common::exceptions::rcljava_throw_rclexception;
using rcljava_common::signatures::convert_from_java_signature;
using rcljava_common::signatures::destroy_ros_message_signature;

JNIEXPORT jlong JNICALL
Java_org_ros2_rcljava_client_ClientImpl_nativeSendClientRequest(
  JNIEnv * env, jclass, jlong client_handle, jlong jrequest_from_java_converter_handle,
  jlong jrequest_destructor_handle, jobject jrequest_msg)
{
  assert(client_handle != 0);
  assert(jrequest_from_java_converter_handle != 0);
  assert(jrequest_destructor_handle != 0);
  assert(jrequest_msg != nullptr);

  rcl_client_t * client = reinterpret_cast<rcl_client_t *>(client_handle);

  convert_from_java_signature convert_from_java =
    reinterpret_cast<convert_from_java_signature>(jrequest_from_java_converter_handle);

  void * request_msg = convert_from_java(jrequest_msg, nullptr);

  int64_t sequence_number;
  rcl_ret_t ret = rcl_send_request(client, request_msg, &sequence_number);

  destroy_ros_message_signature destroy_ros_message =
    reinterpret_cast<destroy_ros_message_signature>(jrequest_destructor_handle);
  destroy_ros_message(request_msg);

  if (ret != RCL_RET_OK) {
    std::string msg =
      "Failed to send request from a client: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
  }
  return static_cast<jlong>(sequence_number);
}

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_client_ClientImpl_nativeDispose(
  JNIEnv * env, jclass, jlong node_handle, jlong client_handle)
{
  if (client_handle == 0) {
    // everything is ok, already destroyed
    return;
  }

  if (node_handle == 0) {
    // TODO(esteve): handle this, node is null, but client isn't
    return;
  }

  rcl_node_t * node = reinterpret_cast<rcl_node_t *>(node_handle);

  assert(node != NULL);

  rcl_client_t * client = reinterpret_cast<rcl_client_t *>(client_handle);

  assert(client != NULL);

  rcl_ret_t ret = rcl_client_fini(client, node);

  if (ret != RCL_RET_OK) {
    std::string msg = "Failed to destroy client: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
  }
}

JNIEXPORT jboolean JNICALL
Java_org_ros2_rcljava_client_ClientImpl_nativeIsServiceAvailable(
  JNIEnv * env, jclass, jlong node_handle, jlong client_handle)
{
  rcl_node_t * node = reinterpret_cast<rcl_node_t *>(node_handle);
  assert(node != NULL);
  rcl_client_t * client = reinterpret_cast<rcl_client_t *>(client_handle);
  assert(client != NULL);

  bool is_ready;
  rcl_ret_t ret = rcl_service_server_is_available(node, client, &is_ready);
  if (RCL_RET_NODE_INVALID == ret) {
    if (node && !rcl_context_is_valid(node->context)) {
      // context is shutdown, do a soft failure
      return false;
    }
  }
  if (ret != RCL_RET_OK) {
    std::string msg =
      "Failed to check if service is available: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
  }
  return is_ready;
}
