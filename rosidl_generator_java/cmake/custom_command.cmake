# Copyright 2017 Esteve Fernandez <esteve@apache.org>
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
add_custom_command(
  OUTPUT
  ${_generated_extension_files}
  ${_generated_java_files}
  COMMAND ${PYTHON_EXECUTABLE} ${rosidl_generator_java_BIN}
  --generator-arguments-file "${generator_arguments_file}"
  --typesupport-impls "${_typesupport_impls}"
  DEPENDS ${target_dependencies} ${rosidl_generate_interfaces_TARGET}
  COMMENT "Generating Java code for ROS interfaces"
  VERBATIM
)

if(TARGET ${rosidl_generate_interfaces_TARGET}${_target_suffix})
  message(WARNING "Custom target ${rosidl_generate_interfaces_TARGET}${_target_suffix} already exists")
else()
  add_custom_target(
    ${rosidl_generate_interfaces_TARGET}${_target_suffix}
    DEPENDS
    ${_generated_extension_files}
    ${_generated_java_files}
  )
endif()

add_jar("${PROJECT_NAME}_messages_jar"
  SOURCES
  ${_generated_java_files}
  OUTPUT_NAME
  ${PROJECT_NAME}_messages
  INCLUDE_JARS
  ${_jar_deps}
)

add_source_jar("${PROJECT_NAME}_messages_source_jar"
  ${_generated_java_files}
  OUTPUT_NAME
  ${PROJECT_NAME}_messages-source
)

add_dependencies("${PROJECT_NAME}_messages_jar" "${rosidl_generate_interfaces_TARGET}${_target_suffix}")
add_dependencies("${PROJECT_NAME}_messages_source_jar" "${rosidl_generate_interfaces_TARGET}${_target_suffix}")
