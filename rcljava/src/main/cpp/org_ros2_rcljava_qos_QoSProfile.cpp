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

#include "org_ros2_rcljava_qos_QoSProfile.h"

#include <jni.h>
#include <limits>

#include "rcljava_common/exceptions.hpp"
#include "rmw/qos_profiles.h"
#include "rmw/types.h"

using rcljava_common::exceptions::rcljava_throw_exception;

static void
qos_set_duration(JNIEnv * env, uint64_t seconds, uint64_t nanos, jobject jqos, jfieldID fid)
{
  if (static_cast<uint64_t>(std::numeric_limits<jlong>::max()) < seconds) {
    // Throwing an exception here would be weird, as we cannot control the durability that was set
    // on all other endpoints in the network. Set jqos seconds to jlong max.
    seconds = static_cast<uint64_t>(std::numeric_limits<jlong>::max());
  }
  if (static_cast<uint64_t>(std::numeric_limits<jlong>::max()) < nanos) {
    // This should never happen, as nanoseconds within a second can perfectly be represented
    // in a jlong.
    nanos = static_cast<uint64_t>(std::numeric_limits<jlong>::max());
  }
  jclass duration_clazz = env->FindClass("java/time/Duration");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jmethodID factory_mid = env->GetStaticMethodID(
    duration_clazz, "ofSeconds", "(JJ)Ljava/time/Duration;");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jobject jduration = env->CallStaticObjectMethod(
    duration_clazz, factory_mid, static_cast<jlong>(seconds), static_cast<jlong>(nanos));
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  env->SetObjectField(jqos, fid, jduration);
}

static void
qos_from_rcl(JNIEnv * env, const rmw_qos_profile_t & qos, jobject jqos)
{
  // TODO(ivanpauno): class and field lookup could be done at startup time
  jclass clazz = env->GetObjectClass(jqos);
  const char * history_class_path = "Lorg/ros2/rcljava/qos/policies/History;";
  jfieldID history_fid = env->GetFieldID(clazz, "history", history_class_path);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jfieldID depth_fid = env->GetFieldID(clazz, "depth", "I");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  const char * reliability_class_path = "Lorg/ros2/rcljava/qos/policies/Reliability;";
  jfieldID reliability_fid = env->GetFieldID(clazz, "reliability", reliability_class_path);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  const char * durability_class_path = "Lorg/ros2/rcljava/qos/policies/Durability;";
  jfieldID durability_fid = env->GetFieldID(clazz, "durability", durability_class_path);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jfieldID deadline_fid = env->GetFieldID(clazz, "deadline", "Ljava/time/Duration;");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jfieldID lifespan_fid = env->GetFieldID(clazz, "lifespan", "Ljava/time/Duration;");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  const char * liveliness_class_path = "Lorg/ros2/rcljava/qos/policies/Liveliness;";
  jfieldID liveliness_fid = env->GetFieldID(clazz, "liveliness", liveliness_class_path);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jfieldID liveliness_lease_fid = env->GetFieldID(
    clazz, "livelinessLeaseDuration", "Ljava/time/Duration;");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jfieldID avoid_ros_conventions_fid = env->GetFieldID(
    clazz, "avoidROSNamespaceConventions", "Z");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);

  jclass history_clazz = env->FindClass("org/ros2/rcljava/qos/policies/History");
  jfieldID history_value_fid;
  switch (qos.history) {
    case RMW_QOS_POLICY_HISTORY_SYSTEM_DEFAULT:
      history_value_fid = env->GetStaticFieldID(
        history_clazz, "SYSTEM_DEFAULT", history_class_path);
      break;
    case RMW_QOS_POLICY_HISTORY_KEEP_LAST:
      history_value_fid = env->GetStaticFieldID(
        history_clazz, "KEEP_LAST", history_class_path);
      break;
    case RMW_QOS_POLICY_HISTORY_KEEP_ALL:
      history_value_fid = env->GetStaticFieldID(
        history_clazz, "KEEP_ALL", history_class_path);
      break;
    case RMW_QOS_POLICY_HISTORY_UNKNOWN:
      history_value_fid = env->GetStaticFieldID(
        history_clazz, "UNKNOWN", history_class_path);
      break;
    default:
      rcljava_throw_exception(
        env, "java/lang/IllegalStateException", "unknown history policy value");
      break;
  }
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jobject history_value = env->GetStaticObjectField(history_clazz, history_value_fid);
  env->SetObjectField(jqos, history_fid, history_value);

  env->SetIntField(jqos, depth_fid, qos.depth);

  jclass reliability_clazz = env->FindClass("org/ros2/rcljava/qos/policies/Reliability");
  jfieldID reliability_value_fid;
  switch (qos.reliability) {
    case RMW_QOS_POLICY_RELIABILITY_SYSTEM_DEFAULT:
      reliability_value_fid = env->GetStaticFieldID(
        reliability_clazz, "SYSTEM_DEFAULT", reliability_class_path);
      break;
    case RMW_QOS_POLICY_RELIABILITY_RELIABLE:
      reliability_value_fid = env->GetStaticFieldID(
        reliability_clazz, "RELIABLE", reliability_class_path);
      break;
    case RMW_QOS_POLICY_RELIABILITY_BEST_EFFORT:
      reliability_value_fid = env->GetStaticFieldID(
        reliability_clazz, "BEST_EFFORT", reliability_class_path);
      break;
    case RMW_QOS_POLICY_RELIABILITY_UNKNOWN:
      reliability_value_fid = env->GetStaticFieldID(
        reliability_clazz, "UNKNOWN", reliability_class_path);
      break;
    default:
      rcljava_throw_exception(
        env, "java/lang/IllegalStateException", "unknown reliability policy value");
      break;
  }
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jobject reliability_value = env->GetStaticObjectField(reliability_clazz, reliability_value_fid);
  env->SetObjectField(jqos, reliability_fid, reliability_value);

  jclass durability_clazz = env->FindClass("org/ros2/rcljava/qos/policies/Durability");
  jfieldID durability_value_fid;
  switch (qos.durability) {
    case RMW_QOS_POLICY_DURABILITY_SYSTEM_DEFAULT:
      durability_value_fid = env->GetStaticFieldID(
        durability_clazz, "SYSTEM_DEFAULT", durability_class_path);
      break;
    case RMW_QOS_POLICY_DURABILITY_TRANSIENT_LOCAL:
      durability_value_fid = env->GetStaticFieldID(
        durability_clazz, "TRANSIENT_LOCAL", durability_class_path);
      break;
    case RMW_QOS_POLICY_DURABILITY_VOLATILE:
      durability_value_fid = env->GetStaticFieldID(
        durability_clazz, "VOLATILE", durability_class_path);
      break;
    case RMW_QOS_POLICY_DURABILITY_UNKNOWN:
      durability_value_fid = env->GetStaticFieldID(
        durability_clazz, "UNKNOWN", durability_class_path);
      break;
    default:
      rcljava_throw_exception(
        env, "java/lang/IllegalStateException", "unknown durability policy value");
      break;
  }
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jobject durability_value = env->GetStaticObjectField(durability_clazz, durability_value_fid);
  env->SetObjectField(jqos, durability_fid, durability_value);

  qos_set_duration(env, qos.deadline.sec, qos.deadline.nsec, jqos, deadline_fid);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  qos_set_duration(env, qos.lifespan.sec, qos.lifespan.nsec, jqos, lifespan_fid);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  qos_set_duration(
    env, qos.liveliness_lease_duration.sec,
    qos.liveliness_lease_duration.nsec, jqos, liveliness_lease_fid);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);

  jclass liveliness_clazz = env->FindClass("org/ros2/rcljava/qos/policies/Liveliness");
  jfieldID liveliness_value_fid;
  switch (qos.liveliness) {
    case RMW_QOS_POLICY_LIVELINESS_SYSTEM_DEFAULT:
      liveliness_value_fid = env->GetStaticFieldID(
        liveliness_clazz, "SYSTEM_DEFAULT", liveliness_class_path);
      break;
    case RMW_QOS_POLICY_LIVELINESS_AUTOMATIC:
      liveliness_value_fid = env->GetStaticFieldID(
        liveliness_clazz, "AUTOMATIC", liveliness_class_path);
      break;
    case RMW_QOS_POLICY_LIVELINESS_MANUAL_BY_TOPIC:
      liveliness_value_fid = env->GetStaticFieldID(
        liveliness_clazz, "MANUAL_BY_TOPIC", liveliness_class_path);
      break;
    case RMW_QOS_POLICY_LIVELINESS_UNKNOWN:
      liveliness_value_fid = env->GetStaticFieldID(
        liveliness_clazz, "UNKNOWN", liveliness_class_path);
      break;
    default:
      rcljava_throw_exception(
        env, "java/lang/IllegalStateException", "unknown liveliness policy value");
      break;
  }
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jobject liveliness_value = env->GetStaticObjectField(liveliness_clazz, liveliness_value_fid);
  env->SetObjectField(jqos, liveliness_fid, liveliness_value);

  env->SetBooleanField(jqos, avoid_ros_conventions_fid, qos.avoid_ros_namespace_conventions);
}

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_qos_QoSProfile_nativeFromRCL(JNIEnv * env, jobject jqos, jlong handle)
{
  auto * qos_profile = reinterpret_cast<rmw_qos_profile_t *>(handle);
  if (!handle) {
    rcljava_throw_exception(
      env, "java/lang/IllegalArgumentException", "rmw qos profile handle is NULL");
    return;
  }
  qos_from_rcl(env, *qos_profile, jqos);
}

JNIEXPORT jlong JNICALL
Java_org_ros2_rcljava_qos_QoSProfile_nativeGetHandleFromName(
  JNIEnv * env, jclass, jstring jprofile_name)
{
  const rmw_qos_profile_t * qos = NULL;
  const char * profile_name = env->GetStringUTFChars(jprofile_name, NULL);
  if (strcmp(profile_name, "default") == 0) {
    qos = &rmw_qos_profile_default;
  }
  if (strcmp(profile_name, "system_default") == 0) {
    qos = &rmw_qos_profile_system_default;
  }
  if (strcmp(profile_name, "sensor_data") == 0) {
    qos = &rmw_qos_profile_sensor_data;
  }
  if (strcmp(profile_name, "parameters") == 0) {
    qos = &rmw_qos_profile_parameters;
  }
  if (strcmp(profile_name, "services") == 0) {
    qos = &rmw_qos_profile_services_default;
  }
  if (strcmp(profile_name, "parameter_events") == 0) {
    qos = &rmw_qos_profile_parameter_events;
  }

  env->ReleaseStringUTFChars(jprofile_name, profile_name);
  if (qos) {
    return reinterpret_cast<jlong>(qos);
  }
  rcljava_throw_exception(
    env, "java/lang/IllegalArgumentException", "unexpected profile identifier");
  return 0;
}
