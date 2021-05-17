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
/* Header for class org_ros2_rcljava_subscription_SubscriptionImpl */

#ifndef ORG_ROS2_RCLJAVA_SUBSCRIPTION_SUBSCRIPTIONIMPL_H_
#define ORG_ROS2_RCLJAVA_SUBSCRIPTION_SUBSCRIPTIONIMPL_H_
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_ros2_rcljava_subscription_SubscriptionImpl
 * Method:    nativeDispose
 * Signature: (JJ)V
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_subscription_SubscriptionImpl_nativeDispose(
  JNIEnv *, jclass, jlong, jlong);

#ifdef __cplusplus
}
#endif
#endif  // ORG_ROS2_RCLJAVA_SUBSCRIPTION_SUBSCRIPTIONIMPL_H_
