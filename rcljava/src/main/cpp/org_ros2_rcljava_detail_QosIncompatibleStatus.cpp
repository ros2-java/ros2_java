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

#include "org_ros2_rcljava_detail_QosIncompatibleStatus.h"

#include <jni.h>
#include <stdlib.h>

#include "rmw/incompatible_qos_events_statuses.h"
#include "rmw/types.h"
#include "rcl/event.h"
#include "rcljava_common/exceptions.hpp"

using rcljava_common::exceptions::rcljava_throw_exception;

JNIEXPORT jlong JNICALL
Java_org_ros2_rcljava_detail_QosIncompatibleStatus_nativeAllocateRCLStatusEvent(
  JNIEnv * env, jclass)
{
  void * p = malloc(sizeof(rmw_qos_incompatible_event_status_t));
  if (!p) {
    rcljava_throw_exception(
      env, "java/lang/OutOfMemoryError", "failed to allocate qos incompatible status");
  }
  return reinterpret_cast<jlong>(p);
}

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_detail_QosIncompatibleStatus_nativeDeallocateRCLStatusEvent(
  JNIEnv *, jclass, jlong handle)
{
  free(reinterpret_cast<void *>(handle));
}

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_detail_QosIncompatibleStatus_nativeFromRCLEvent(
  JNIEnv * env, jobject self, jlong handle)
{
  auto * p = reinterpret_cast<rmw_qos_incompatible_event_status_t *>(handle);
  if (!p) {
    rcljava_throw_exception(
      env, "java/lang/IllegalArgumentException", "passed rmw object handle is NULL");
  }
  // TODO(ivanpauno): class and field lookup could be done at startup time
  jclass clazz = env->GetObjectClass(self);
  jclass qos_kind_clazz = env->FindClass(
    "org/ros2/rcljava/detail/QosIncompatibleStatus$PolicyKind");
  if (env->ExceptionCheck()) {
    return;
  }
  jfieldID total_count_fid = env->GetFieldID(clazz, "totalCount", "I");
  if (env->ExceptionCheck()) {
    return;
  }
  jfieldID total_count_change_fid = env->GetFieldID(clazz, "totalCountChange", "I");
  if (env->ExceptionCheck()) {
    return;
  }
  const char * enum_class_path =
    "Lorg/ros2/rcljava/detail/QosIncompatibleStatus$PolicyKind;";
  jfieldID policy_kind_fid = env->GetFieldID(clazz, "lastPolicyKind", enum_class_path);
  if (env->ExceptionCheck()) {
    return;
  }

  jfieldID enum_value_fid;
  switch (p->last_policy_kind) {
    case RMW_QOS_POLICY_INVALID:
      enum_value_fid = env->GetStaticFieldID(qos_kind_clazz, "INVALID", enum_class_path);
      break;
    case RMW_QOS_POLICY_DURABILITY:
      enum_value_fid = env->GetStaticFieldID(qos_kind_clazz, "DURABILITY", enum_class_path);
      break;
    case RMW_QOS_POLICY_DEADLINE:
      enum_value_fid = env->GetStaticFieldID(qos_kind_clazz, "DEADLINE", enum_class_path);
      break;
    case RMW_QOS_POLICY_LIVELINESS:
      enum_value_fid = env->GetStaticFieldID(qos_kind_clazz, "LIVELINESS", enum_class_path);
      break;
    case RMW_QOS_POLICY_RELIABILITY:
      enum_value_fid = env->GetStaticFieldID(qos_kind_clazz, "RELIABILITY", enum_class_path);
      break;
    case RMW_QOS_POLICY_HISTORY:
      enum_value_fid = env->GetStaticFieldID(qos_kind_clazz, "HISTORY", enum_class_path);
      break;
    case RMW_QOS_POLICY_LIFESPAN:
      enum_value_fid = env->GetStaticFieldID(qos_kind_clazz, "LIFESPAN", enum_class_path);
      break;
    default:
      rcljava_throw_exception(
        env, "java/lang/IllegalArgumentException", "unknown rmw qos policy kind");
      break;
  }
  if (env->ExceptionCheck()) {
    return;
  }
  jobject enum_value = env->GetStaticObjectField(qos_kind_clazz, enum_value_fid);

  env->SetIntField(self, total_count_fid, p->total_count);
  env->SetIntField(self, total_count_change_fid, p->total_count_change);
  env->SetObjectField(self, policy_kind_fid, enum_value);
}
