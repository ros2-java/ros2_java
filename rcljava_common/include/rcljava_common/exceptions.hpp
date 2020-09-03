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
#ifndef RCLJAVA_COMMON__EXCEPTIONS_HPP_
#define RCLJAVA_COMMON__EXCEPTIONS_HPP_
#include <jni.h>

#include <string>

#include "rcljava_common/visibility_control.hpp"

/// Execute \a error_statement if an exception has happened.
/**
 * \param env a JNIEnv pointer, used to check for exceptions.
 * \param error_statement statement executed if an exception has happened.
 */
#define RCLJAVA_COMMON_EXCEPTION_CHECK_X(env, error_statement) \
  do { \
    if (env->ExceptionCheck()) { \
      error_statement; \
    } \
  } while (0)

/// Return from the current function if a java exception has happened.
/**
 * \param env a JNIEnv pointer, used to check for exceptions.
 */
#define RCLJAVA_COMMON_EXCEPTION_CHECK(env) RCLJAVA_COMMON_EXCEPTION_CHECK_X(env, return )

/// Call \ref rcljava_throw_rclexception if \a ret is not RCL_RET_OK,
/// and execute \a error_statement in that case.
/**
 * \param env a JNIEnv pointer, used to throw a java exception from the rcl error.
 * \param ret rcl_ret_t error that will be checked.
 * \param message error message that will be passed to the thrown exception.
 * \param error_statement statement executed if ret was not RCL_RET_OK.
 */
#define RCLJAVA_COMMON_THROW_FROM_RCL_X(env, ret, message, error_statement) \
  do { \
    if (RCL_RET_OK != ret) { \
      rcljava_common::exception::rcljava_throw_rclexception(env, ret, message); \
      error_statement; \
    } \
  } while (0)

/// Call \ref rcljava_throw_rclexception if \a ret is not RCL_RET_OK and return.
/**
 * \param env a JNIEnv pointer, used to check for exceptions.
 * \param ret rcl_ret_t error that will be checked.
 * \param message error message that will be passed to the thrown exception.
 */
#define RCLJAVA_COMMON_THROW_FROM_RCL(env, ret, message) \
  RCLJAVA_COMMON_THROW_FROM_RCL_X(env, ret, message, return )

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

#endif  // RCLJAVA_COMMON__EXCEPTIONS_HPP_
