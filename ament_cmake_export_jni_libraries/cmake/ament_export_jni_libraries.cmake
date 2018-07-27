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

#
# Export JNI libraries to downstream packages.
#
# :param ARGN: a list of JNI libraries.
#   Each element might either be an absolute path to a JNI library.
# :type ARGN: list of strings
#
# @public
#
macro(ament_export_jni_libraries)
  if(_${PROJECT_NAME}_AMENT_PACKAGE)
    message(FATAL_ERROR
      "ament_export_jni_libraries() must be called before ament_package()")
  endif()

  if(${ARGC} GREATER 0)
    _ament_cmake_export_jni_libraries_register_package_hook()

    foreach(_arg ${ARGN})
      if(IS_ABSOLUTE "${_arg}")
        if(NOT EXISTS "${_arg}")
          message(WARNING
            "ament_export_jni_libraries() package '${PROJECT_NAME}' exports the "
            "JNI library '${_arg}' which doesn't exist")
        else()
          list(APPEND _AMENT_CMAKE_EXPORT_JNI_LIBRARIES "${_arg}")
        endif()
      elseif(TARGET "${_arg}")
        get_target_property(_library_path "${_arg}" OUTPUT_NAME)
        list(APPEND _AMENT_CMAKE_EXPORT_JNI_LIBRARIES "${_library_path}")
      else()
        set(_arg "\${${PROJECT_NAME}_DIR}/../../../${_arg}")
        list(APPEND _AMENT_CMAKE_EXPORT_JNI_LIBRARIES "${_arg}")
      endif()
    endforeach()

    if(WIN32)
      set(_ament_build_type_gradle_jni_library_path_key "ament_build_type_gradle_jni_library_path_bat")
      set(_ament_build_type_gradle_jni_library_path_filename
        "${CMAKE_CURRENT_BINARY_DIR}/ament_build_type_gradle_jni_library_path.bat.in")
    else()
      set(_ament_build_type_gradle_jni_library_path_key "ament_build_type_gradle_jni_library_path_sh")
      set(_ament_build_type_gradle_jni_library_path_filename
        "${CMAKE_CURRENT_BINARY_DIR}/ament_build_type_gradle_jni_library_path.sh.in")
    endif()

    ament_index_get_resource(
      jni_library_path_template "templates" "${_ament_build_type_gradle_jni_library_path_key}")

    file(WRITE
      "${_ament_build_type_gradle_jni_library_path_filename}"
      "${jni_library_path_template}")

    if(NOT WIN32)
      find_package(ament_cmake_core QUIET REQUIRED)
      ament_environment_hooks("${_ament_build_type_gradle_jni_library_path_filename}")
    endif()
  endif()
endmacro()
