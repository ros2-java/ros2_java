# Copyright 2016 Open Source Robotics Foundation, Inc.
# Copyright 2016 Esteve Fernandez <esteve@apache.org>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

macro(accumulate_typesupports)
  set(_typesupport_impl "")
  get_rmw_typesupport(_typesupport_impl ${rmw_implementation} LANGUAGE "C")
  list(APPEND _typesupport_impls ${_typesupport_impl})
endmacro()

macro(rosidl_generator_java_get_typesupports TYPESUPPORT_IMPLS)
  set(TYPESUPPORT_IMPLS "")
  set(_typesupport_impls "")
  call_for_each_rmw_implementation(accumulate_typesupports)
  foreach(_typesupport_impl ${_typesupport_impls})
    list_append_unique(TYPESUPPORT_IMPLS ${_typesupport_impl})
  endforeach()
endmacro()
