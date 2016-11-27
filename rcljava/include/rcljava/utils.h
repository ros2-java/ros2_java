// Copyright 2016 Esteve Fernandez <esteve@apache.org>
// Copyright 2016 Mickael Gaillard <mick.gaillard@gmail.com>
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
#include <cstdlib>
#include <cassert>
#include <cstdio>

#include "rcl/graph.h"
#include "rmw/rmw.h"
#include "rmw/types.h"
#include "rosidl_generator_c/message_type_support.h"

#ifndef RCLJAVA__UTILS_H_
#define RCLJAVA__UTILS_H_
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Convert jstring to std::string.
 */
std::string
jstring2String(JNIEnv * env, jstring jsubject)
{
  const char * subject_tmp = env->GetStringUTFChars(jsubject, 0);
  std::string result(subject_tmp);
  env->ReleaseStringUTFChars(jsubject, subject_tmp);

  return result;
}

/*
 * Convertion of Message type.
 */
rosidl_message_type_support_t *
jclass2MessageType(JNIEnv * env, jclass jmessage_class)
{
  jmethodID mid =
    env->GetStaticMethodID(jmessage_class, "getTypeSupport", "()J");

  jlong jts = env->CallStaticLongMethod(jmessage_class, mid);

  rosidl_message_type_support_t * ts =
    reinterpret_cast<rosidl_message_type_support_t *>(jts);

  return ts;
}

/*
 *
 */
void *
jclass2Message(JNIEnv * env, jclass jmessage_class)
{
  jmethodID jfrom_mid = env->GetStaticMethodID(jmessage_class, "getFromJavaConverter", "()J");
  jlong jfrom_java_converter = env->CallStaticLongMethod(jmessage_class, jfrom_mid);

  using convert_from_java_signature = void * (*)(jobject, void *);
  convert_from_java_signature convert_from_java =
    reinterpret_cast<convert_from_java_signature>(jfrom_java_converter);

  jmethodID jconstructor = env->GetMethodID(jmessage_class, "<init>", "()V");
  jobject jmsg = env->NewObject(jmessage_class, jconstructor);

  void * taken_msg = convert_from_java(jmsg, nullptr);
  return taken_msg;
}

jobject
jclass2JMessage(JNIEnv * env, jclass jmessage_class, void * taken_msg)
{
  jmethodID jto_mid = env->GetStaticMethodID(jmessage_class, "getToJavaConverter", "()J");
  jlong jto_java_converter = env->CallStaticLongMethod(jmessage_class, jto_mid);

  using convert_to_java_signature = jobject (*)(void *, jobject);
  convert_to_java_signature convert_to_java =
    reinterpret_cast<convert_to_java_signature>(jto_java_converter);

  jobject jtaken_msg = convert_to_java(taken_msg, nullptr);
  return jtaken_msg;
}

/*
 *
 */
void *
jobject2Message(JNIEnv * env, jobject jmessage)
{
  jclass jmessage_class = env->GetObjectClass(jmessage);
  jmethodID mid = env->GetStaticMethodID(jmessage_class, "getFromJavaConverter", "()J");
  jlong jfrom_java_converter = env->CallStaticLongMethod(jmessage_class, mid);

  using convert_from_java_signature = void * (*)(jobject, void *);
  convert_from_java_signature convert_from_java =
    reinterpret_cast<convert_from_java_signature>(jfrom_java_converter);

  void * raw_ros_message = convert_from_java(jmessage, nullptr);
  return raw_ros_message;
}

/*
 *
 */
rosidl_service_type_support_t *
jclass2ServiceType(JNIEnv * env, jclass jmessage_class)
{
  jmethodID mid =
    env->GetStaticMethodID(jmessage_class, "getServiceTypeSupport", "()J");
  assert(mid != NULL);

  jlong jts = env->CallStaticLongMethod(jmessage_class, mid);
  assert(jts != 0);

  rosidl_service_type_support_t * ts =
    reinterpret_cast<rosidl_service_type_support_t *>(jts);

  return ts;
}

/*
 *
 */
jlong
instance2Handle(void * obj)
{
  jlong handler = reinterpret_cast<jlong>(obj);

  assert(handler != 0);

  return handler;
}

/*
 *
 */
void
throwException(JNIEnv * env, std::string message)
{
  const char * class_name = "java/lang/IllegalStateException";
  jclass exception_class = env->FindClass(class_name);

  assert(exception_class != NULL);

  env->ThrowNew(exception_class, message.c_str());
}

/*
 *
 */
jobject
makeJTopics(JNIEnv * env, rcl_topic_names_and_types_t * topic_names_and_types)
{
  env->PushLocalFrame(256);  // fix for local references

  jsize map_len = topic_names_and_types->topic_count;
  jclass mapClass = env->FindClass("java/util/HashMap");
  jmethodID init = env->GetMethodID(mapClass, "<init>", "(I)V");
  jobject hashMap = env->NewObject(mapClass, init, map_len);
  jmethodID put = env->GetMethodID(mapClass, "put",
      "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

  for (size_t i = 0; i < topic_names_and_types->topic_count; ++i) {
    env->CallObjectMethod(hashMap, put,
      env->NewStringUTF(topic_names_and_types->topic_names[i]),
      env->NewStringUTF(topic_names_and_types->type_names[i]));
  }

  env->PopLocalFrame(hashMap);

  return hashMap;
}

/*
jobject
makeJNames(rmw_ros_meta_t * ros_meta)
{
  java_util_ArrayList      = static_cast<jclass>(env->NewGlobalRef(env->FindClass("java/util/ArrayList")));
  java_util_ArrayList_     = env->GetMethodID(java_util_ArrayList, "<init>", "(I)V");
  java_util_ArrayList_size = env->GetMethodID (java_util_ArrayList, "size", "()I");
//  java_util_ArrayList_get  = env->GetMethodID(java_util_ArrayList, "get", "(I)Ljava/lang/Object;");
  java_util_ArrayList_add  = env->GetMethodID(java_util_ArrayList, "add", "(Ljava/lang/Object;)Z");

  jobject result = env->NewObject(java_util_ArrayList, java_util_ArrayList_, vector.size());

  for (size_t i = 0; i < rmw_ros_meta_t->topic_count; ++i) {
    jstring element = env->NewStringUTF(ros_meta_data->node_names[i].c_str());

    env->CallBooleanMethod(result, java_util_ArrayList_add, element);
    env->DeleteLocalRef(element);
  }
  return result;
}
*/

jobject convert_rmw_request_id_to_java(JNIEnv * env, rmw_request_id_t * request_id)
{
  jclass jrequest_id_class = env->FindClass("org/ros2/rcljava/node/service/RMWRequestId");
  assert(jrequest_id_class != nullptr);

  jmethodID jconstructor = env->GetMethodID(jrequest_id_class, "<init>", "()V");
  assert(jconstructor != nullptr);

  jobject jrequest_id = env->NewObject(jrequest_id_class, jconstructor);

  jfieldID jsequence_number_field_id = env->GetFieldID(jrequest_id_class, "sequenceNumber", "J");
  jfieldID jwriter_guid_field_id = env->GetFieldID(jrequest_id_class, "writerGUID", "[B");

  assert(jsequence_number_field_id != nullptr);
  assert(jwriter_guid_field_id != nullptr);

  int8_t * writer_guid = request_id->writer_guid;
  int64_t sequence_number = request_id->sequence_number;

  env->SetLongField(jrequest_id, jsequence_number_field_id, sequence_number);

  jsize writer_guid_len = 16;  // See rmw/rmw/include/rmw/types.h

  jbyteArray jwriter_guid = env->NewByteArray(writer_guid_len);
  env->SetByteArrayRegion(jwriter_guid, 0, writer_guid_len, reinterpret_cast<jbyte *>(writer_guid));
  env->SetObjectField(jrequest_id, jwriter_guid_field_id, jwriter_guid);

  return jrequest_id;
}

rmw_request_id_t * convert_rmw_request_id_from_java(JNIEnv * env, jobject jrequest_id)
{
  assert(jrequest_id != nullptr);

  jclass jrequest_id_class = env->GetObjectClass(jrequest_id);
  assert(jrequest_id_class != nullptr);

  jfieldID jsequence_number_field_id = env->GetFieldID(jrequest_id_class, "sequenceNumber", "J");
  jfieldID jwriter_guid_field_id = env->GetFieldID(jrequest_id_class, "writerGUID", "[B");

  assert(jsequence_number_field_id != nullptr);
  assert(jwriter_guid_field_id != nullptr);

  rmw_request_id_t * request_id = static_cast<rmw_request_id_t *>(malloc(sizeof(rmw_request_id_t)));

  int8_t * writer_guid = request_id->writer_guid;
  request_id->sequence_number = env->GetLongField(jrequest_id, jsequence_number_field_id);

  jsize writer_guid_len = 16;  // See rmw/rmw/include/rmw/types.h

  jbyteArray jwriter_guid = (jbyteArray)env->GetObjectField(jrequest_id, jwriter_guid_field_id);
  env->GetByteArrayRegion(jwriter_guid, 0, writer_guid_len, reinterpret_cast<jbyte *>(writer_guid));

  return request_id;
}

#ifdef __cplusplus
}
#endif

/*
 *
 */
template<typename T>
T *
makeInstance()
{
  return (T *)malloc(sizeof(T));
}

/*
 *
 */
template<typename T>
T *
handle2Instance(jlong handle)
{
  T * obj = reinterpret_cast<T *>(handle);

  assert(obj != NULL);

  return obj;
}

#endif  // RCLJAVA__UTILS_H_
