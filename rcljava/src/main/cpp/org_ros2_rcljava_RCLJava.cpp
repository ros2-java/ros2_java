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
#include "rcl/node.h"
#include "rcl/rcl.h"
#include "rcl/timer.h"
#include "rmw/rmw.h"
#include "rosidl_runtime_c/message_type_support_struct.h"

#include "rcljava_common/exceptions.hpp"
#include "rcljava_common/signatures.hpp"

#include "org_ros2_rcljava_RCLJava.h"

using rcljava_common::exceptions::rcljava_throw_rclexception;
using rcljava_common::signatures::convert_from_java_signature;
using rcljava_common::signatures::convert_to_java_signature;
using rcljava_common::signatures::destroy_ros_message_signature;

JNIEXPORT jlong JNICALL
Java_org_ros2_rcljava_RCLJava_nativeCreateContextHandle(JNIEnv *, jclass)
{
  rcl_context_t * context = static_cast<rcl_context_t *>(malloc(sizeof(rcl_context_t)));
  *context = rcl_get_zero_initialized_context();
  jlong context_handle = reinterpret_cast<jlong>(context);
  return context_handle;
}

JNIEXPORT jlong JNICALL
Java_org_ros2_rcljava_RCLJava_nativeCreateNodeHandle(
  JNIEnv * env, jclass, jstring jnode_name, jstring jnamespace, jlong context_handle,
  jobject cli_args, jboolean use_global_arguments, jboolean enable_rosout)
{
  const char * node_name = env->GetStringUTFChars(jnode_name, 0);
  const char * node_namespace = env->GetStringUTFChars(jnamespace, 0);

  rcl_context_t * context = reinterpret_cast<rcl_context_t *>(context_handle);

  // TODO(clalancette): we could probably make this faster by just doing these
  // method lookups during some initialization time.  But we'd have to add a lot
  // more infrastructure for that, and we don't expect to call
  // 'nativeCreateNodeHandle' that often, so I think this is OK for now.
  jclass java_util_ArrayList =
    static_cast<jclass>(env->NewGlobalRef(env->FindClass("java/util/ArrayList")));
  jmethodID java_util_ArrayList_size = env->GetMethodID(java_util_ArrayList, "size", "()I");
  jmethodID java_util_ArrayList_get = env->GetMethodID(
    java_util_ArrayList, "get", "(I)Ljava/lang/Object;");

  rcl_arguments_t arguments = rcl_get_zero_initialized_arguments();
  char ** argv = NULL;
  jint argc = env->CallIntMethod(cli_args, java_util_ArrayList_size);
  argv = static_cast<char **>(malloc(argc * sizeof(char *)));
  for (jint i = 0; i < argc; ++i) {
    jstring element =
      static_cast<jstring>(env->CallObjectMethod(cli_args, java_util_ArrayList_get, i));
    argv[i] = const_cast<char *>(env->GetStringUTFChars(element, nullptr));
  }
  rcl_ret_t ret = rcl_parse_arguments(argc, argv, rcl_get_default_allocator(), &arguments);
  for (jint i = 0; i < argc; ++i) {
    jstring element =
      static_cast<jstring>(env->CallObjectMethod(cli_args, java_util_ArrayList_get, i));
    env->ReleaseStringUTFChars(element, argv[i]);
  }
  free(argv);
  if (ret != RCL_RET_OK) {
    std::string msg = "Failed to parse node arguments: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
    return 0;
  }

  rcl_node_t * node = static_cast<rcl_node_t *>(malloc(sizeof(rcl_node_t)));
  *node = rcl_get_zero_initialized_node();

  rcl_node_options_t options = rcl_node_get_default_options();
  options.use_global_arguments = use_global_arguments;
  options.arguments = arguments;
  options.enable_rosout = enable_rosout;
  ret = rcl_node_init(node, node_name, node_namespace, context, &options);
  if (ret != RCL_RET_OK) {
    std::string msg = "Failed to create node: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    free(node);
    if (rcl_arguments_fini(&arguments) != RCL_RET_OK) {
      // We are already throwing an exception, just ignore the return here
    }
    rcljava_throw_rclexception(env, ret, msg);
    return 0;
  }

  ret = rcl_arguments_fini(&arguments);
  if (ret != RCL_RET_OK) {
    // We failed to cleanup
    std::string msg = "Failed to cleanup after creating node: " + std::string(
      rcl_get_error_string().str);
    rcl_reset_error();
    if (rcl_node_fini(node) != RCL_RET_OK) {
      // We are already throwing an exception, just ignore the return here
    }
    free(node);
    rcljava_throw_rclexception(env, ret, msg);
    return 0;
  }

  return reinterpret_cast<jlong>(node);
}

JNIEXPORT jstring JNICALL
Java_org_ros2_rcljava_RCLJava_nativeGetRMWIdentifier(JNIEnv * env, jclass)
{
  const char * rmw_implementation_identifier = rmw_get_implementation_identifier();

  return env->NewStringUTF(rmw_implementation_identifier);
}

JNIEXPORT jlong JNICALL
Java_org_ros2_rcljava_RCLJava_nativeConvertQoSProfileToHandle(
  JNIEnv *, jclass, jint history, jint depth, jint reliability, jint durability,
  jboolean avoidROSNamespaceConventions)
{
  rmw_qos_profile_t * qos_profile =
    static_cast<rmw_qos_profile_t *>(malloc(sizeof(rmw_qos_profile_t)));
  qos_profile->history = static_cast<rmw_qos_history_policy_t>(history);
  qos_profile->depth = depth;
  qos_profile->reliability = static_cast<rmw_qos_reliability_policy_t>(reliability);
  qos_profile->durability = static_cast<rmw_qos_durability_policy_t>(durability);
  // TODO(jacobperron): Expose deadline, lifespan, and liveliness settings as parameters
  qos_profile->deadline = rmw_qos_profile_default.deadline;
  qos_profile->lifespan = rmw_qos_profile_default.lifespan;
  qos_profile->liveliness = rmw_qos_profile_default.liveliness;
  qos_profile->liveliness_lease_duration = rmw_qos_profile_default.liveliness_lease_duration;
  qos_profile->avoid_ros_namespace_conventions = avoidROSNamespaceConventions;
  return reinterpret_cast<jlong>(qos_profile);
}

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_RCLJava_nativeDisposeQoSProfile(JNIEnv *, jclass, jlong qos_profile_handle)
{
  rmw_qos_profile_t * qos_profile = reinterpret_cast<rmw_qos_profile_t *>(qos_profile_handle);
  free(qos_profile);
}
