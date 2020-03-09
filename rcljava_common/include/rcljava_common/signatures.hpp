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
#ifndef RCLJAVA_COMMON__SIGNATURES_HPP_
#define RCLJAVA_COMMON__SIGNATURES_HPP_
#include <jni.h>

#include <string>

namespace rcljava_common
{
namespace signatures
{
using convert_from_java_signature = void * (*)(jobject, void *);

using convert_to_java_signature = jobject (*)(void *, jobject);

using destroy_ros_message_signature = void (*)(void *);
}  // namespace signatures
}  // namespace rcljava_common

#endif  // RCLJAVA_COMMON__SIGNATURES_HPP_
