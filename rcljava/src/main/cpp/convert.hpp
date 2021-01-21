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
//
#include <jni.h>

#include <cassert>
#include <cstdint>

#include "rmw/rmw.h"

#ifndef MAIN__CPP__CONVERT_HPP_
#define MAIN__CPP__CONVERT_HPP_

namespace rcljava
{

jobject
convert_rmw_request_id_to_java(JNIEnv * env, rmw_request_id_t * request_id)
{
  jclass jrequest_id_class = env->FindClass("org/ros2/rcljava/service/RMWRequestId");
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

  jsize writer_guid_len = sizeof(request_id->writer_guid) / sizeof(request_id->writer_guid[0]);

  jbyteArray jwriter_guid = env->NewByteArray(writer_guid_len);
  env->SetByteArrayRegion(jwriter_guid, 0, writer_guid_len, reinterpret_cast<jbyte *>(writer_guid));
  env->SetObjectField(jrequest_id, jwriter_guid_field_id, jwriter_guid);

  return jrequest_id;
}

rmw_request_id_t *
convert_rmw_request_id_from_java(JNIEnv * env, jobject jrequest_id)
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

  jsize writer_guid_len = sizeof(request_id->writer_guid) / sizeof(request_id->writer_guid[0]);

  jbyteArray jwriter_guid = (jbyteArray)env->GetObjectField(jrequest_id, jwriter_guid_field_id);
  env->GetByteArrayRegion(jwriter_guid, 0, writer_guid_len, reinterpret_cast<jbyte *>(writer_guid));

  return request_id;
}

}  // namespace rcljava

#endif  // MAIN__CPP__CONVERT_HPP_
