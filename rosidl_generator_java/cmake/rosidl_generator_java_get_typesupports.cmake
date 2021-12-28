# Copyright 2016-2017 Open Source Robotics Foundation, Inc.
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

macro(rosidl_generator_java_get_typesupports TYPESUPPORT_IMPLS)
  set(${TYPESUPPORT_IMPLS} "")
  ament_index_get_resources(${TYPESUPPORT_IMPLS} "rosidl_typesupport_c")
  list(APPEND ${TYPESUPPORT_IMPLS} "rosidl_typesupport_c")
  foreach(_typesupport ${${TYPESUPPORT_IMPLS}})
    find_package(${_typesupport} QUIET)
    if(NOT ${_typesupport}_FOUND)
      list(REMOVE_ITEM ${TYPESUPPORT_IMPLS} "${_typesupport}")
    endif()
  endforeach()
endmacro()
