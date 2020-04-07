# Copyright 2016-2017 Esteve Fernandez <esteve@apache.org>
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

#
# Export JAR files to downstream packages.
#
# :param ARGN: a list of JAR files.
#   Each element might either be an absolute path to a JAR file.
# :type ARGN: list of strings
#
# @public
#
macro(ament_export_jars)
  if(_${PROJECT_NAME}_AMENT_PACKAGE)
    message(FATAL_ERROR
      "ament_export_jars() must be called before ament_package()")
  endif()

  if(${ARGC} GREATER 0)
    _ament_cmake_export_jars_register_package_hook()

    foreach(_arg ${ARGN})
      if(IS_ABSOLUTE "${_arg}")
        if(NOT EXISTS "${_arg}")
          message(WARNING
            "ament_export_jars() package '${PROJECT_NAME}' exports the "
            "jar '${_arg}' which doesn't exist")
        else()
            list_append_unique(_AMENT_EXPORT_ABSOLUTE_CLASSPATH "${_arg}")
            list_append_unique(_AMENT_EXPORT_ABSOLUTE_JARS "${_arg}")
        endif()
      else()
        list_append_unique(_AMENT_EXPORT_RELATIVE_CLASSPATH "${_arg}")
        set(_arg "\${${PROJECT_NAME}_DIR}/../../../${_arg}")
        list_append_unique(_AMENT_EXPORT_RELATIVE_JARS "${_arg}")
      endif()
    endforeach()

    set(_AMENT_EXPORT_JARS_CLASSPATH
      ${_AMENT_EXPORT_RELATIVE_CLASSPATH}
      ${_AMENT_EXPORT_ABSOLUTE_CLASSPATH})

    set(_ament_build_type_gradle_dsv_key "ament_build_type_gradle_classpath_dsv")
    set(_ament_build_type_gradle_dsv_filename
      "${CMAKE_CURRENT_BINARY_DIR}/ament_build_type_gradle_classpath.dsv.in")

    ament_index_get_resource(
      dsv_template "templates" "${_ament_build_type_gradle_dsv_key}")

    file(WRITE
      "${_ament_build_type_gradle_dsv_filename}"
      "${dsv_template}")

    find_package(ament_cmake_core REQUIRED)
    ament_environment_hooks("${_ament_build_type_gradle_dsv_filename}")

  endif()
endmacro()
