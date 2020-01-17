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

#include <string>

#include "rcl/context.h"
#include "rcl/error_handling.h"
#include "rcl/init.h"

#include "rcljava_common/exceptions.h"
#include "rcljava_common/signatures.h"

#include "org_ros2_rcljava_contexts_ContextImpl.h"

using rcljava_common::exceptions::rcljava_throw_rclexception;

JNIEXPORT jboolean JNICALL
Java_org_ros2_rcljava_contexts_ContextImpl_nativeIsValid(JNIEnv *, jclass, jlong context_handle)
{
  rcl_context_t * context = reinterpret_cast<rcl_context_t *>(context_handle);
  bool is_valid = rcl_context_is_valid(context);
  return is_valid;
}

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_contexts_ContextImpl_nativeInit(JNIEnv * env, jclass, jlong context_handle)
{
  // TODO(jacobperron): Encapsulate init options into a Java class
  rcl_init_options_t init_options = rcl_get_zero_initialized_init_options();
  rcl_ret_t ret = rcl_init_options_init(&init_options, rcl_get_default_allocator());
  if (RCL_RET_OK != ret) {
    std::string msg = "Failed to init context options: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
    return;
  }

  rcl_context_t * context = reinterpret_cast<rcl_context_t *>(context_handle);
  // TODO(esteve): parse args
  ret = rcl_init(0, nullptr, &init_options, context);
  if (RCL_RET_OK != ret) {
    std::string msg = "Failed to init context: " + std::string(rcl_get_error_string().str);
    rcl_ret_t ignored_ret = rcl_init_options_fini(&init_options);
    (void)ignored_ret;
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
    return;
  }

  rcl_ret_t fini_ret = rcl_init_options_fini(&init_options);
  if (RCL_RET_OK != fini_ret) {
    std::string msg = "Failed to init context: " + std::string(rcl_get_error_string().str);
    rcl_ret_t ignored_ret = rcl_shutdown(context);
    (void)ignored_ret;
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
    return;
  }
}

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_contexts_ContextImpl_nativeShutdown(
  JNIEnv * env, jclass, jlong context_handle)
{
  rcl_context_t * context = reinterpret_cast<rcl_context_t *>(context_handle);
  rcl_ret_t ret = rcl_shutdown(context);
  if (RCL_RET_OK != ret) {
    std::string msg = "Failed to shutdown context: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
  }
}

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_contexts_ContextImpl_nativeDispose(JNIEnv * env, jclass, jlong context_handle)
{
  if (0 == context_handle) {
    // already destroyed
    return;
  }

  rcl_context_t * context = reinterpret_cast<rcl_context_t *>(context_handle);

  rcl_ret_t ret = rcl_context_fini(context);

  if (RCL_RET_OK != ret) {
    std::string msg = "Failed to destroy context: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
  }
}
