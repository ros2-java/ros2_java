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

#include "org_ros2_rcljava_graph_EndpointInfo.h"

#include <jni.h>
#include <limits>

#include "rcl/graph.h"
#include "rcljava_common/exceptions.hpp"

using rcljava_common::exceptions::rcljava_throw_exception;

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_graph_EndpointInfo_nativeFromRCL(JNIEnv * env, jobject self, jlong handle)
{
  auto * p = reinterpret_cast<rcl_topic_endpoint_info_t *>(handle);
  if (!p) {
    rcljava_throw_exception(
      env, "java/lang/IllegalArgumentException", "passed rcl endpoint info handle is NULL");
    return;
  }
  const char * endpoint_type_enum_path =
    "Lorg/ros2/rcljava/graph/EndpointInfo$EndpointType;";
  // TODO(ivanpauno): class and field lookup could be done at startup time
  jclass endpoint_type_clazz = env->FindClass("org/ros2/rcljava/graph/EndpointInfo$EndpointType");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jclass clazz = env->GetObjectClass(self);
  jfieldID node_name_fid = env->GetFieldID(clazz, "nodeName", "Ljava/lang/String;");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jfieldID node_namespace_fid = env->GetFieldID(clazz, "nodeNamespace", "Ljava/lang/String;");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jfieldID topic_type_fid = env->GetFieldID(clazz, "topicType", "Ljava/lang/String;");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jfieldID endpoint_type_fid = env->GetFieldID(clazz, "endpointType", endpoint_type_enum_path);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jfieldID endpoint_gid_fid = env->GetFieldID(clazz, "endpointGID", "[B");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jfieldID qos_fid = env->GetFieldID(clazz, "qos", "Lorg/ros2/rcljava/qos/QoSProfile;");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);

  jstring jnode_name = env->NewStringUTF(p->node_name);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  env->SetObjectField(self, node_name_fid, jnode_name);
  jstring jnode_namespace = env->NewStringUTF(p->node_namespace);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  env->SetObjectField(self, node_namespace_fid, jnode_namespace);
  jstring jtopic_type = env->NewStringUTF(p->topic_type);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  env->SetObjectField(self, topic_type_fid, jtopic_type);
  jfieldID enum_value_fid = nullptr;
  switch (p->endpoint_type) {
    case RMW_ENDPOINT_INVALID:
      enum_value_fid = env->GetStaticFieldID(
        endpoint_type_clazz, "INVALID", endpoint_type_enum_path);
      break;
    case RMW_ENDPOINT_PUBLISHER:
      enum_value_fid = env->GetStaticFieldID(
        endpoint_type_clazz, "PUBLISHER", endpoint_type_enum_path);
      break;
    case RMW_ENDPOINT_SUBSCRIPTION:
      enum_value_fid = env->GetStaticFieldID(
        endpoint_type_clazz, "SUBSCRIPTION", endpoint_type_enum_path);
      break;
    default:
      rcljava_throw_exception(
        env, "java/lang/IllegalArgumentException", "unknown endpoint type");
      break;
  }
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jobject enum_value = env->GetStaticObjectField(endpoint_type_clazz, enum_value_fid);
  env->SetObjectField(self, endpoint_type_fid, enum_value);
  jbyteArray jgid = env->NewByteArray(RMW_GID_STORAGE_SIZE);
  if (jgid == NULL) {
    rcljava_throw_exception(
      env, "java/lang/OutOfMemoryError", "cannot allocate java gid byte array");
    return;
  }
  jbyte * gid_content = env->GetByteArrayElements(jgid, nullptr);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  for (size_t i = 0; i < RMW_GID_STORAGE_SIZE; ++i) {
    gid_content[i] = p->endpoint_gid[i];
  }
  env->ReleaseByteArrayElements(jgid, gid_content, 0);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  env->SetObjectField(self, endpoint_gid_fid, jgid);
  jclass qos_clazz = env->FindClass("org/ros2/rcljava/qos/QoSProfile");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jmethodID qos_init_mid = env->GetMethodID(qos_clazz, "<init>", "()V");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jobject jqos = env->NewObject(qos_clazz, qos_init_mid);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  jmethodID qos_from_rcl_mid = env->GetMethodID(qos_clazz, "nativeFromRCL", "(J)V");
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  env->CallObjectMethod(jqos, qos_from_rcl_mid, &p->qos_profile);
  RCLJAVA_COMMON_CHECK_FOR_EXCEPTION(env);
  env->SetObjectField(self, qos_fid, jqos);
}
