// Copyright 2018 Esteve Fernandez <esteve@apache.org>
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

#ifndef RCLJAVA_COMMON__VISIBILITY_CONTROL_H_
#define RCLJAVA_COMMON__VISIBILITY_CONTROL_H_

#ifdef __cplusplus
extern "C"
{
#endif

// This logic was borrowed (then namespaced) from the examples on the gcc wiki:
//     https://gcc.gnu.org/wiki/Visibility

#if defined _WIN32 || defined __CYGWIN__
  #ifdef __GNUC__
    #define RCLJAVA_COMMON_EXPORT __attribute__ ((dllexport))
    #define RCLJAVA_COMMON_IMPORT __attribute__ ((dllimport))
  #else
    #define RCLJAVA_COMMON_EXPORT __declspec(dllexport)
    #define RCLJAVA_COMMON_IMPORT __declspec(dllimport)
  #endif
  #ifdef RCLJAVA_COMMON_BUILDING_DLL
    #define RCLJAVA_COMMON_PUBLIC RCLJAVA_COMMON_EXPORT
  #else
    #define RCLJAVA_COMMON_PUBLIC RCLJAVA_COMMON_IMPORT
  #endif
  #define RCLJAVA_COMMON_PUBLIC_TYPE RCLJAVA_COMMON_PUBLIC
  #define RCLJAVA_COMMON_LOCAL
#else
  #define RCLJAVA_COMMON_EXPORT __attribute__ ((visibility("default")))
  #define RCLJAVA_COMMON_IMPORT
  #if __GNUC__ >= 4
    #define RCLJAVA_COMMON_PUBLIC __attribute__ ((visibility("default")))
    #define RCLJAVA_COMMON_LOCAL  __attribute__ ((visibility("hidden")))
  #else
    #define RCLJAVA_COMMON_PUBLIC
    #define RCLJAVA_COMMON_LOCAL
  #endif
  #define RCLJAVA_COMMON_PUBLIC_TYPE
#endif

#ifdef __cplusplus
}
#endif

#endif  // RCLJAVA_COMMON__VISIBILITY_CONTROL_H_
