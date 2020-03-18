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
#include <string>

#include "rcljava_common/exceptions.hpp"

namespace rcljava_common
{
namespace exceptions
{
void
rcljava_throw_rclexception(JNIEnv * env, int rcl_ret_code, const std::string & message)
{
  jclass rclexception_class = env->FindClass("org/ros2/rcljava/exceptions/RCLException");
  jmethodID rclexception_init =
    env->GetMethodID(rclexception_class, "<init>", "(ILjava/lang/String;)V");
  jstring jmessage = env->NewStringUTF(message.c_str());
  jthrowable rclexception = static_cast<jthrowable>(
    env->NewObject(rclexception_class, rclexception_init, rcl_ret_code, jmessage));
  env->Throw(rclexception);
}

void
rcljava_throw_exception(JNIEnv * env, const char * class_name, const std::string & message)
{
  jclass exception_class;

  exception_class = env->FindClass(class_name);

  assert(exception_class != nullptr);

  env->ThrowNew(exception_class, message.c_str());
}
}  // namespace exceptions
}  // namespace rcljava_common
