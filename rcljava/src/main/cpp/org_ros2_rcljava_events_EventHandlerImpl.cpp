// Copyright 2020 Open Source Robotics Foundation, Inc.
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

#include "rcl/error_handling.h"
#include "rcl/event.h"

#include "rcljava_common/exceptions.hpp"

#include "org_ros2_rcljava_events_EventHandlerImpl.h"

using rcljava_common::exceptions::rcljava_throw_exception;
using rcljava_common::exceptions::rcljava_throw_rclexception;

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_events_EventHandlerImpl_nativeDispose(
  JNIEnv * env, jclass, jlong event_handle)
{
  if (event_handle == 0) {
    // everything is ok, already destroyed
    return;
  }

  auto * event = reinterpret_cast<rcl_event_t *>(event_handle);

  rcl_ret_t ret = rcl_event_fini(event);

  if (RCL_RET_OK != ret) {
    std::string msg = "Failed to destroy event: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_exception(env, "java/lang/IllegalStateException", msg);
  }
}

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_events_EventHandlerImpl_nativeTake(JNIEnv *env, jclass, jlong event_handle, jlong event_status_handle)
{
  auto * event = reinterpret_cast<rcl_event_t *>(event_handle);
  if (!event) {
    rcljava_throw_exception(
      env,
      "java/lang/IllegalArgumentException",
      "The underlying rcl_event_t has been already disposed");
  }
  void * event_status = reinterpret_cast<void *>(event_status_handle);
  if (!event_status) {
    rcljava_throw_exception(
      env,
      "java/lang/IllegalArgumentException",
      "The passed event status is NULL");
  }
  rcl_ret_t ret = rcl_take_event(event, event_status);
  if (RCL_RET_OK != ret) {
    rcljava_throw_rclexception(env, ret, rcl_get_error_string().str);
    rcl_reset_error();
  }
}
