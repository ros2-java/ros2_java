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

find_package(ament_cmake_export_jars REQUIRED)
find_package(ament_cmake_export_jni_libraries REQUIRED)
find_package(rosidl_generator_c REQUIRED)
find_package(rmw_implementation_cmake REQUIRED)
find_package(rmw REQUIRED)
find_package(rcljava_common REQUIRED)

include(CrossCompilingExtra)

if(CMAKE_CROSSCOMPILING)
  find_host_package(Java COMPONENTS Development REQUIRED)
else()
  find_package(Java COMPONENTS Development REQUIRED)
endif()
if(NOT ANDROID)
  find_package(JNI REQUIRED)
endif()
include(UseJava)

if(NOT WIN32)
  if(CMAKE_CXX_COMPILER_ID STREQUAL "GNU")
    set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,--no-undefined")
  elseif(CMAKE_CXX_COMPILER_ID MATCHES "Clang")
    set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,-undefined,error")
  endif()
endif()

# Get a list of typesupport implementations from valid rmw implementations.
rosidl_generator_java_get_typesupports(_typesupport_impls)

if(_typesupport_impls STREQUAL "")
  message(WARNING "No valid typesupport for Java generator. Java messages will not be generated.")
  return()
endif()

set(_output_path
  "${CMAKE_CURRENT_BINARY_DIR}/rosidl_generator_java/${PROJECT_NAME}")
set(_generated_extension_files "")
set(_generated_java_files "")

foreach(_typesupport_impl ${_typesupport_impls})
  set(_generated_extension_${_typesupport_impl}_files "")
endforeach()

foreach(_abs_idl_file ${rosidl_generate_interfaces_ABS_IDL_FILES})
  get_filename_component(_parent_folder "${_abs_idl_file}" DIRECTORY)
  get_filename_component(_parent_folder "${_parent_folder}" NAME)
  get_filename_component(_idl_name "${_abs_idl_file}" NAME_WE)
  list(APPEND _generated_java_files
    "${_output_path}/${_parent_folder}/${_idl_name}.java"
  )
  # TODO(jacobperron): Is there a more robust way of detecting services and actions
  # Services generate extra files
  if(_parent_folder STREQUAL "srv")
    list(APPEND _generated_java_files
      "${_output_path}/${_parent_folder}/${_idl_name}_Request.java"
      "${_output_path}/${_parent_folder}/${_idl_name}_Response.java"
    )
    foreach(_typesupport_impl ${_typesupport_impls})
      list(APPEND _generated_extension_${_typesupport_impl}_files "${_output_path}/${_parent_folder}/${_idl_name}_Request.ep.${_typesupport_impl}.cpp")
      list(APPEND _generated_extension_${_typesupport_impl}_files "${_output_path}/${_parent_folder}/${_idl_name}_Response.ep.${_typesupport_impl}.cpp")
    endforeach()
  endif()
  # Actions generate extra files
  if(_parent_folder STREQUAL "action")
    list(APPEND _generated_java_files
      "${_output_path}/${_parent_folder}/${_idl_name}_Goal.java"
      "${_output_path}/${_parent_folder}/${_idl_name}_Result.java"
      "${_output_path}/${_parent_folder}/${_idl_name}_Feedback.java"
    )
    foreach(_typesupport_impl ${_typesupport_impls})
      list(APPEND _generated_extension_${_typesupport_impl}_files "${_output_path}/${_parent_folder}/${_idl_name}_Goal.ep.${_typesupport_impl}.cpp")
      list(APPEND _generated_extension_${_typesupport_impl}_files "${_output_path}/${_parent_folder}/${_idl_name}_Result.ep.${_typesupport_impl}.cpp")
      list(APPEND _generated_extension_${_typesupport_impl}_files "${_output_path}/${_parent_folder}/${_idl_name}_Feedback.ep.${_typesupport_impl}.cpp")
    endforeach()
  endif()

  foreach(_typesupport_impl ${_typesupport_impls})
    list(APPEND _generated_extension_${_typesupport_impl}_files "${_output_path}/${_parent_folder}/${_idl_name}.ep.${_typesupport_impl}.cpp")
  endforeach()
endforeach()

foreach(_typesupport_impl ${_typesupport_impls})
  foreach(_generated_file ${_generated_extension_${_typesupport_impl}_files})
    list(APPEND _generated_extension_files "${_generated_file}")
    list(APPEND _typesupport_by_generated_cpp_files "${_typesupport_impl}")
  endforeach()
endforeach()

set(_dependency_files "")
set(_dependencies "")
foreach(_pkg_name ${rosidl_generate_interfaces_DEPENDENCY_PACKAGE_NAMES})
  foreach(_idl_file ${${_pkg_name}_IDL_FILES})
    set(_abs_idl_file "${${_pkg_name}_DIR}/../${_idl_file}")
    normalize_path(_abs_idl_file "${_abs_idl_file}")
    list(APPEND _dependency_files "${_abs_idl_file}")
    list(APPEND _dependencies "${_pkg_name}:${_abs_idl_file}")
  endforeach()
endforeach()

set(target_dependencies
  "${rosidl_generator_java_BIN}"
  ${rosidl_generator_java_GENERATOR_FILES}
  "${rosidl_generator_java_TEMPLATE_DIR}/action.cpp.em"
  "${rosidl_generator_java_TEMPLATE_DIR}/idl.cpp.em"
  "${rosidl_generator_java_TEMPLATE_DIR}/msg.cpp.em"
  "${rosidl_generator_java_TEMPLATE_DIR}/srv.cpp.em"
  "${rosidl_generator_java_TEMPLATE_DIR}/action.java.em"
  "${rosidl_generator_java_TEMPLATE_DIR}/idl.java.em"
  "${rosidl_generator_java_TEMPLATE_DIR}/msg.java.em"
  "${rosidl_generator_java_TEMPLATE_DIR}/srv.java.em"
  ${rosidl_generate_interfaces_ABS_IDL_FILES}
  ${_dependency_files})
foreach(dep ${target_dependencies})
  if(NOT EXISTS "${dep}")
    message(FATAL_ERROR "Target dependency '${dep}' does not exist")
  endif()
endforeach()

set(generator_arguments_file "${CMAKE_BINARY_DIR}/rosidl_generator_java__arguments.json")
rosidl_write_generator_arguments(
  "${generator_arguments_file}"
  PACKAGE_NAME "${PROJECT_NAME}"
  IDL_TUPLES "${rosidl_generate_interfaces_IDL_TUPLES}"
  ROS_INTERFACE_DEPENDENCIES "${_dependencies}"
  OUTPUT_DIR "${_output_path}"
  TEMPLATE_DIR "${rosidl_generator_java_TEMPLATE_DIR}"
  TARGET_DEPENDENCIES ${target_dependencies}
)

file(MAKE_DIRECTORY "${_output_path}")

set(_extension_dependencies "")
set(_target_suffix "__java")

set(_jar_deps "")
find_package(rcljava_common REQUIRED)
foreach(_jar_dep ${rcljava_common_JARS})
  list(APPEND _jar_deps "${_jar_dep}")
endforeach()

foreach(_pkg_name ${rosidl_generate_interfaces_DEPENDENCY_PACKAGE_NAMES})
  find_package(${_pkg_name} REQUIRED)
  foreach(_jar_dep ${${_pkg_name}_JARS})
    list(APPEND _jar_deps "${_jar_dep}")
  endforeach()
endforeach()

# needed to avoid multiple calls to the Java generator and add_jar()
# trick copied from
# https://github.com/ros2/rosidl/blob/master/rosidl_generator_py/cmake/rosidl_generator_py_generate_interfaces.cmake
set(_subdir "${CMAKE_CURRENT_BINARY_DIR}/${rosidl_generate_interfaces_TARGET}${_target_suffix}")
file(MAKE_DIRECTORY "${_subdir}")
file(READ "${rosidl_generator_java_DIR}/custom_command.cmake" _custom_command)
file(WRITE "${_subdir}/CMakeLists.txt" "${_custom_command}")
add_subdirectory("${_subdir}" ${rosidl_generate_interfaces_TARGET}${_target_suffix})

set_property(
  SOURCE
  ${_generated_extension_files}
  ${_generated_java_files}
  PROPERTY GENERATED 1)

macro(set_properties _build_type)
  set_target_properties(${_library_name} PROPERTIES
    COMPILE_OPTIONS "${_extension_compile_flags}"
    LIBRARY_OUTPUT_DIRECTORY${_build_type} ${_output_path}/${_parent_folder}
    RUNTIME_OUTPUT_DIRECTORY${_build_type} ${_output_path}/${_parent_folder}
    C_STANDARD 11
    CXX_STANDARD 14
    OUTPUT_NAME "${_library_path}")
endmacro()

foreach(_generated_cpp_file ${_generated_extension_files})
  list(FIND _generated_extension_files ${_generated_cpp_file} _file_index)
  list(GET _typesupport_by_generated_cpp_files ${_file_index} _typesupport_impl)
  find_package(${_typesupport_impl} REQUIRED)
  get_filename_component(_parent_folder "${_generated_cpp_file}" DIRECTORY)
  get_filename_component(_parent_folder "${_parent_folder}" NAME)
  get_filename_component(_parent_folder "${_parent_folder}" NAME)
  get_filename_component(_base_name "${_generated_cpp_file}" NAME_WE)
  string(REGEX REPLACE "^rosidl_typesupport_" "" _short_typesupport_impl ${_typesupport_impl})
  set(_library_name "${_parent_folder}${_base_name}${_short_typesupport_impl}")
  set(_library_path
    "${rosidl_generate_interfaces_TARGET}_${_parent_folder}_${_base_name}__jni__${_typesupport_impl}"
  )
  string_camel_case_to_lower_case_underscore(${_library_path} _library_path)

  add_library(${_library_name} SHARED
    ${_generated_cpp_file}
  )
  add_dependencies(
    ${_library_name}
    ${rosidl_generate_interfaces_TARGET}${_target_suffix}
    ${rosidl_generate_interfaces_TARGET}__rosidl_typesupport_c
    ${_target_dependencies}
  )
  set(_extension_compile_flags "")
  if(CMAKE_COMPILER_IS_GNUCXX OR CMAKE_CXX_COMPILER_ID MATCHES "Clang")
    set(_extension_compile_flags -Wall -Wextra)
  endif()
  set_properties("")
  if(WIN32)
    set_properties("_DEBUG")
    set_properties("_MINSIZEREL")
    set_properties("_RELEASE")
    set_properties("_RELWITHDEBINFO")
  endif()
  set(_extension_link_flags "")
  if(NOT WIN32)
    target_compile_options(${_library_name} PRIVATE -Wall -Wextra -Wpedantic)
    if(CMAKE_COMPILER_IS_GNUCXX)
      set(_extension_link_flags "-Wl,--no-undefined")
    elseif(CMAKE_CXX_COMPILER_ID MATCHES "Clang")
      set(_extension_link_flags "-Wl,-undefined,error")
    endif()
  endif()
  target_link_libraries(
    ${_library_name}
    ${PROJECT_NAME}__${_typesupport_impl}
    ${_extension_link_flags}
  )
  rosidl_target_interfaces(${_library_name}
    ${PROJECT_NAME} rosidl_typesupport_c)
  target_include_directories(${_library_name}
    PUBLIC
    ${CMAKE_CURRENT_BINARY_DIR}/rosidl_generator_c
    ${CMAKE_CURRENT_BINARY_DIR}/rosidl_generator_java
    ${JNI_INCLUDE_DIRS}
  )
  ament_target_dependencies(${_library_name}
    "rosidl_generator_c"
    "rosidl_generator_java"
    "rcljava_common"
    "rosidl_typesupport_c"
    "rosidl_typesupport_interface"
  )
  foreach(_pkg_name ${rosidl_generate_interfaces_DEPENDENCY_PACKAGE_NAMES})
    ament_target_dependencies(${_library_name}
      ${_pkg_name}
    )
    target_link_libraries(${_library_name} ${${_pkg_name}_JNI_LIBRARIES})
  endforeach()
  if(NOT rosidl_generate_interfaces_SKIP_INSTALL)
    install(TARGETS ${_library_name}
      ARCHIVE DESTINATION lib/jni
      LIBRARY DESTINATION lib/jni
      RUNTIME DESTINATION lib/jni
    )
    ament_export_jni_libraries(${_library_name})
  endif()
endforeach()

get_property(${PROJECT_NAME}_messages_jar_file
  TARGET "${PROJECT_NAME}_messages_jar"
  PROPERTY "JAR_FILE"
)

# Dummy command that depends on the target created by add_jar() in the CMake subproject
# so that other targets can resolve the JAR dependency properly
add_custom_command(
  OUTPUT
  ${${PROJECT_NAME}_messages_jar_file}
  DEPENDS
  ${PROJECT_NAME}_messages_jar
)

if(NOT rosidl_generate_interfaces_SKIP_INSTALL)
  set(_install_jar_dir "${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}")
  if(NOT _generated_java_files STREQUAL "")
    install_jar("${PROJECT_NAME}_messages_jar" "share/${PROJECT_NAME}/java")
    ament_export_jars("share/${PROJECT_NAME}/java/${PROJECT_NAME}_messages.jar")
  endif()
endif()

if(BUILD_TESTING AND rosidl_generate_interfaces_ADD_LINTER_TESTS)
  if(
    NOT _generated_java_files STREQUAL "" OR
    NOT _generated_extension_files STREQUAL ""
  )
    find_package(ament_cmake_cppcheck REQUIRED)
    ament_cppcheck(
      TESTNAME "cppcheck_rosidl_generated_java"
      "${_output_path}")

    find_package(ament_cmake_cpplint REQUIRED)
    get_filename_component(_cpplint_root "${_output_path}" DIRECTORY)
    ament_cpplint(
      TESTNAME "cpplint_rosidl_generated_java"
      # the generated code might contain longer lines for templated types
      MAX_LINE_LENGTH 999
      ROOT "${_cpplint_root}"
      "${_output_path}")

    find_package(ament_cmake_uncrustify REQUIRED)
    ament_uncrustify(
      TESTNAME "uncrustify_rosidl_generated_java"
      # the generated code might contain longer lines for templated types
      MAX_LINE_LENGTH 999
      "${_output_path}")
  endif()
endif()
