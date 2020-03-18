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
#include "rmw/rmw.h"
#include "rosidl_generator_c/message_type_support_struct.h"

#include "rcljava_common/exceptions.hpp"
#include "rcljava_common/signatures.hpp"

#include "org_ros2_rcljava_subscription_SubscriptionImpl.h"

using rcljava_common::exceptions::rcljava_throw_rclexception;

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_subscription_SubscriptionImpl_nativeDispose(
  JNIEnv * env, jclass, jlong node_handle, jlong subscription_handle)
{
  if (subscription_handle == 0) {
    // everything is ok, already destroyed
    return;
  }

  if (node_handle == 0) {
    // TODO(esteve): handle this, node is null, but subscription isn't
    return;
  }

  rcl_node_t * node = reinterpret_cast<rcl_node_t *>(node_handle);

  assert(node != NULL);

  rcl_subscription_t * subscription = reinterpret_cast<rcl_subscription_t *>(subscription_handle);

  assert(subscription != NULL);

  rcl_ret_t ret = rcl_subscription_fini(subscription, node);

  if (ret != RCL_RET_OK) {
    std::string msg = "Failed to destroy subscription: " + std::string(rcl_get_error_string().str);
    rcl_reset_error();
    rcljava_throw_rclexception(env, ret, msg);
  }
}
