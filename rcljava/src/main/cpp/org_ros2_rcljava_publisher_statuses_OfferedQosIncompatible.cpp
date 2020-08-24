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

#include "org_ros2_rcljava_publisher_statuses_OfferedQosIncompatible.h"

#include <jni.h>

#include "rcl/event.h"

JNIEXPORT jint JNICALL
Java_org_ros2_rcljava_publisher_statuses_OfferedQosIncompatible_nativeGetEventType(
  JNIEnv *, jclass)
{
  return RCL_PUBLISHER_OFFERED_INCOMPATIBLE_QOS;
}
