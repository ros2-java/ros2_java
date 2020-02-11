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

#include <string>

#include "rcl/error_handling.h"
#include "rcl/rcl.h"

#include "rcljava_common/exceptions.h"

#include "org_ros2_rcljava_Time.h"

using rcljava_common::exceptions::rcljava_throw_rclexception;

JNIEXPORT jlong JNICALL
Java_org_ros2_rcljava_Time_nativeRCLSystemTimeNow(JNIEnv * env, jclass)
{
  rcutils_time_point_value_t rcutils_now = 0;
  rcutils_ret_t ret = RCUTILS_RET_ERROR;
  ret = rcutils_system_time_now(&rcutils_now);
  if (ret != RCUTILS_RET_OK) {
    std::string msg = "Could not get current time: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
  }
  return rcutils_now;
}

JNIEXPORT jlong JNICALL
Java_org_ros2_rcljava_Time_nativeRCLSteadyTimeNow(JNIEnv * env, jclass)
{
  rcutils_time_point_value_t rcutils_now = 0;
  rcutils_ret_t ret = RCUTILS_RET_ERROR;
  ret = rcutils_steady_time_now(&rcutils_now);
  if (ret != RCUTILS_RET_OK) {
    std::string msg = "Could not get current time: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
  }
  return rcutils_now;
}
