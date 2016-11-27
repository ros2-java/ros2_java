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

#include "rcljava/org_ros2_rcljava_node_service_Client.h"

JNIEXPORT void JNICALL Java_org_ros2_rcljava_node_service_Client_nativeSendClientRequest(
  JNIEnv * env,
  jclass,
  jlong client_handle,
  jlong sequence_number,
  jlong jrequest_from_java_converter_handle,
  jlong jrequest_to_java_converter_handle,
  jobject jrequest_msg)
{
  assert(client_handle != 0);
  assert(jrequest_from_java_converter_handle != 0);
  assert(jrequest_to_java_converter_handle != 0);
  assert(jrequest_msg != nullptr);

  rcl_client_t * client = reinterpret_cast<rcl_client_t *>(client_handle);

  convert_from_java_signature convert_from_java =
    reinterpret_cast<convert_from_java_signature>(jrequest_from_java_converter_handle);

  void * request_msg = convert_from_java(jrequest_msg, nullptr);

  rcl_ret_t ret = rcl_send_request(client, request_msg, &sequence_number);

  if (ret != RCL_RET_OK) {
    rcljava_throw_exception(
      env, "java/lang/IllegalStateException",
      "Failed to send request from a client: " + std::string(rcl_get_error_string_safe()));
  }
}
