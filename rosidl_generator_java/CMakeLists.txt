cmake_minimum_required(VERSION 3.5)

project(rosidl_generator_java)

find_package(ament_cmake REQUIRED)

find_package(rcljava_common REQUIRED)

include(CrossCompilingExtra)

ament_export_dependencies(ament_cmake)
ament_export_dependencies(rosidl_cmake)

ament_python_install_package(${PROJECT_NAME})

if(CMAKE_CROSSCOMPILING)
  find_host_package(Java COMPONENTS Development REQUIRED)
else()
  find_package(Java COMPONENTS Development REQUIRED)
endif()
if(NOT ANDROID)
  find_package(JNI REQUIRED)
endif()
include(UseJava)
include(JavaExtra)

ament_index_register_resource("rosidl_generator_packages")

if(BUILD_TESTING)
  find_package(rosidl_cmake REQUIRED)
  find_package(rosidl_generator_c REQUIRED)
  find_package(test_interface_files REQUIRED)

  find_package(ament_lint_auto REQUIRED)
  ament_lint_auto_find_test_dependencies()

  include(cmake/register_java.cmake)
  include(cmake/rosidl_generator_java_get_typesupports.cmake)

  # Trick ament_target_dependencies() into thinking this package has been found
  set(rosidl_generator_java_FOUND "1")
  set(rosidl_generator_java_DIR "${CMAKE_CURRENT_SOURCE_DIR}/cmake")

  rosidl_generator_java_extras(
    "${CMAKE_CURRENT_SOURCE_DIR}/bin/rosidl_generator_java"
    "${CMAKE_CURRENT_SOURCE_DIR}/rosidl_generator_java/__init__.py"
    "${CMAKE_CURRENT_SOURCE_DIR}/resource"
  )

  rosidl_generate_interfaces(${PROJECT_NAME}
    ${message_files}
    ${test_interface_files_MSG_FILES}
    ${test_interface_files_SRV_FILES}
    SKIP_INSTALL
  )

  set(${PROJECT_NAME}_test_sources
    "src/test/java/org/ros2/generator/InterfacesTest.java"
  )

  set(${PROJECT_NAME}_testsuites
    "org.ros2.generator.InterfacesTest"
  )

  get_property(_${PROJECT_NAME}_messages_jar_file
    TARGET "${PROJECT_NAME}_messages_jar"
    PROPERTY "JAR_FILE")

  set(_deps_library_dirs "")
  list_append_unique(_deps_library_dirs ${CMAKE_CURRENT_BINARY_DIR})
  list_append_unique(_deps_library_dirs ${CMAKE_CURRENT_BINARY_DIR}/rosidl_generator_java/rosidl_generator_java/msg/)
  list_append_unique(_deps_library_dirs ${CMAKE_CURRENT_BINARY_DIR}/rosidl_generator_java/rosidl_generator_java/srv/)

  foreach(testsuite ${${PROJECT_NAME}_testsuites})
    ament_add_junit_tests("${PROJECT_NAME}_tests_${testsuite}"
      TIMEOUT 90
      WORKING_DIRECTORY
      "${CMAKE_CURRENT_BINARY_DIR}"
      SOURCES
      "${${PROJECT_NAME}_test_sources}"
      TESTS
      "${testsuite}"
      INCLUDE_JARS
      "${rcljava_common_JARS}"
      "${rosidl_generator_java_JARS}"
      "${_${PROJECT_NAME}_messages_jar_file}"
      APPEND_LIBRARY_DIRS
      "${_deps_library_dirs}"
    )

    add_dependencies("${PROJECT_NAME}_tests_${testsuite}" "${PROJECT_NAME}_messages_jar")
  endforeach()

endif()

install(
  PROGRAMS bin/rosidl_generator_java
  DESTINATION lib/rosidl_generator_java
)

install(
  DIRECTORY cmake resource
  DESTINATION share/${PROJECT_NAME}
)

ament_package(
  CONFIG_EXTRAS "cmake/rosidl_generator_java_get_typesupports.cmake"
    "cmake/register_java.cmake"
    "rosidl_generator_java-extras.cmake.in"
)
