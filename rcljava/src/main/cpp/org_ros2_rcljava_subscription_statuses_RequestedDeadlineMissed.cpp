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

#include "org_ros2_rcljava_subscription_statuses_RequestedDeadlineMissed.h"

#include <jni.h>
#include <stdlib.h>

#include "rmw/types.h"
#include "rcl/event.h"
#include "rcljava_common/exceptions.hpp"

using rcljava_common::exceptions::rcljava_throw_exception;

JNIEXPORT jlong JNICALL
Java_org_ros2_rcljava_subscription_statuses_RequestedDeadlineMissed_nativeAllocateRCL(
  JNIEnv * env, jclass)
{
  void * p = malloc(sizeof(rmw_requested_deadline_missed_status_t));
  if (!p) {
    rcljava_throw_exception(
      env, "java/lang/OutOfMemoryError", "failed to allocate missed deadline status");
  }
  return reinterpret_cast<jlong>(p);
}

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_subscription_statuses_RequestedDeadlineMissed_nativeDeallocateRCL(
  JNIEnv *, jclass, jlong handle)
{
  free(reinterpret_cast<void *>(handle));
}

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_subscription_statuses_RequestedDeadlineMissed_nativeFromRCL(
  JNIEnv * env, jobject self, jlong handle)
{
  auto * p = reinterpret_cast<rmw_requested_deadline_missed_status_t *>(handle);
  if (!p) {
    rcljava_throw_exception(
      env, "java/lang/IllegalArgumentException", "passed rmw object handle is NULL");
    return;
  }
  // TODO(ivanpauno): class and field lookup could be done at startup time
  jclass clazz = env->GetObjectClass(self);
  jfieldID total_count_fid = env->GetFieldID(clazz, "totalCount", "I");
  if (env->ExceptionCheck()) {
    return;
  }
  jfieldID total_count_change_fid = env->GetFieldID(clazz, "totalCountChange", "I");
  if (env->ExceptionCheck()) {
    return;
  }
  env->SetIntField(self, total_count_fid, p->total_count);
  env->SetIntField(self, total_count_change_fid, p->total_count_change);
}

JNIEXPORT jint JNICALL
Java_org_ros2_rcljava_subscription_statuses_RequestedDeadlineMissed_nativeGetEventType(
  JNIEnv *, jclass)
{
  return RCL_SUBSCRIPTION_REQUESTED_DEADLINE_MISSED;
}
