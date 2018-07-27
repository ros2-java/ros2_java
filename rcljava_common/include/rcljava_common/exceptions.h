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
#ifndef RCLJAVA_COMMON__EXCEPTIONS_H_
#define RCLJAVA_COMMON__EXCEPTIONS_H_
#include <jni.h>

#include <string>

#include "rcljava_common/visibility_control.h"

namespace rcljava_common
{
namespace exceptions
{
RCLJAVA_COMMON_PUBLIC
void rcljava_throw_rclexception(JNIEnv * env, int rcl_ret_code, const std::string & message);

RCLJAVA_COMMON_PUBLIC
void rcljava_throw_exception(JNIEnv *, const char *, const std::string &);
}  // namespace exceptions
}  // namespace rcljava_common

#endif  // RCLJAVA_COMMON__EXCEPTIONS_H_
