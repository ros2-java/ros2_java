cmake_minimum_required(VERSION 2.8.3)

project(rcljava)

find_package(ament_cmake REQUIRED)
find_package(ament_cmake_export_jars REQUIRED)
find_package(rcl REQUIRED)
find_package(rmw REQUIRED)
find_package(rmw_implementation_cmake REQUIRED)
find_package(rcljava_common REQUIRED)

find_package(std_msgs REQUIRED)
find_package(rcl_interfaces REQUIRED)
find_package(builtin_interfaces REQUIRED)

include(CrossCompilingExtra)


if(ANDROID)
  # https://cmake.org/cmake/help/v3.0/module/FindJava.html?highlight=java
  find_host_package(Java 1.6 COMPONENTS Development)
else()
  # https://cmake.org/cmake/help/v3.0/module/FindJava.html?highlight=java
  find_package(Java 1.6 COMPONENTS Development)

  # https://cmake.org/cmake/help/v3.0/module/FindJNI.html?highlight=jni
  find_package(JNI REQUIRED)
endif()

# https://cmake.org/cmake/help/v3.0/module/UseJava.html?highlight=java
include(UseJava)
include(JavaExtra)

enable_testing()

### Definitions ###
set(CMAKE_JAVA_COMPILE_FLAGS "-source" "1.6" "-target" "1.6")

if(NOT WIN32)
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -Wall -Wextra")
endif()

include_directories(include)

find_jar(JUNIT_JAR NAMES junit PATHS /usr/share/java VERSIONS 4)
find_jar(LOG_JAR NAMES commons-logging PATHS /usr/share/java)
find_jar(STD_MSGS_JARS NAMES std_msgs
  PATHS "${CMAKE_BINARY_DIR}/../std_msgs/")
find_jar(RCLINTERFACE_MSGS_JARS NAMES rcl_interfaces
  PATHS "${CMAKE_BINARY_DIR}/../rcl_interfaces/")
find_jar(BUILTIN_MSGS_JARS NAMES builtin_interfaces
  PATHS "${CMAKE_BINARY_DIR}/../builtin_interfaces/")
find_jar(BUILTIN_COMMON_JARS NAMES rosidl_generator_java
  PATHS "${CMAKE_BINARY_DIR}/../rosjava_common/")

set(STD_MSGS_JARS "${CMAKE_BINARY_DIR}/../std_msgs/std_msgs.jar")
set(BUILTIN_MSGS_JARS "${CMAKE_BINARY_DIR}/../builtin_interfaces/builtin_interfaces.jar")
set(RCLINTERFACE_MSGS_JARS NAMES "${CMAKE_BINARY_DIR}/../rcl_interfaces/rcl_interfaces.jar")
set(BUILTIN_COMMON_JARS NAMES "${CMAKE_BINARY_DIR}/../rosjava_common/rosjava_common.jar")

# Bug in find_jar for Junit4 : https://sourceforge.net/p/rdkit/mailman/message/35301727/
set(JUNIT_JAR "/usr/share/java/junit4.jar")

set(${PROJECT_NAME}_sources
  "src/main/java/org/ros2/rcljava/Log.java"
  "src/main/java/org/ros2/rcljava/NativeUtils.java"
  "src/main/java/org/ros2/rcljava/RCLJava.java"

  "src/main/java/org/ros2/rcljava/internal/IClient.java"
  "src/main/java/org/ros2/rcljava/internal/IGraph.java"
  "src/main/java/org/ros2/rcljava/internal/INode.java"
  "src/main/java/org/ros2/rcljava/internal/IPublisher.java"
  "src/main/java/org/ros2/rcljava/internal/IService.java"
  "src/main/java/org/ros2/rcljava/internal/ISubscription.java"

  "src/main/java/org/ros2/rcljava/namespace/GraphName.java"

  "src/main/java/org/ros2/rcljava/node/Node.java"

  "src/main/java/org/ros2/rcljava/qos/policies/Durability.java"
  "src/main/java/org/ros2/rcljava/qos/policies/History.java"
  "src/main/java/org/ros2/rcljava/qos/policies/QoSPolicy.java"
  "src/main/java/org/ros2/rcljava/qos/policies/Reliability.java"
  "src/main/java/org/ros2/rcljava/qos/QoSProfile.java"

  "src/main/java/org/ros2/rcljava/node/topic/Consumer.java"
  "src/main/java/org/ros2/rcljava/node/topic/Publisher.java"
  "src/main/java/org/ros2/rcljava/node/topic/Subscription.java"
  "src/main/java/org/ros2/rcljava/node/topic/Topics.java"

  "src/main/java/org/ros2/rcljava/node/service/BiConsumer.java"
  "src/main/java/org/ros2/rcljava/node/service/Client.java"
  "src/main/java/org/ros2/rcljava/node/service/RCLFuture.java"
  "src/main/java/org/ros2/rcljava/node/service/RMWRequestId.java"
  "src/main/java/org/ros2/rcljava/node/service/Service.java"
  "src/main/java/org/ros2/rcljava/node/service/TriConsumer.java"

  "src/main/java/org/ros2/rcljava/node/parameter/ParameterConsumer.java"
  "src/main/java/org/ros2/rcljava/node/parameter/ParameterEventConsumer.java"
  "src/main/java/org/ros2/rcljava/node/parameter/ParameterService.java"
  "src/main/java/org/ros2/rcljava/node/parameter/ParameterVariant.java"
  "src/main/java/org/ros2/rcljava/node/parameter/SyncParametersClient.java"

  "src/main/java/org/ros2/rcljava/exception/ImplementationAlreadyImportedException.java"
  "src/main/java/org/ros2/rcljava/exception/InvalidRCLJAVAImplementation.java"
  "src/main/java/org/ros2/rcljava/exception/NoImplementationAvailableException.java"
  "src/main/java/org/ros2/rcljava/exception/NotImplementedException.java"
  "src/main/java/org/ros2/rcljava/exception/NotInitializedException.java"
)

### Macro ###
macro(target)
  if(NOT "${target_suffix} " STREQUAL " ")
    get_rcl_information("${rmw_implementation}" "rcl${target_suffix}")
  endif()

  set(_jni_classes "RCLJava;node_Node;node_topic_Publisher;node_service_Client")

  foreach(_jni_class ${_jni_classes})
    set(_target_name "${PROJECT_NAME}${_jni_class}${target_suffix}")

    add_library(
      ${_target_name}
      SHARED
        src/main/cpp/org_ros2_rcljava_${_jni_class}.cpp
    )

    target_compile_definitions(
      ${_target_name}
      PRIVATE
        "RMW_IMPLEMENTATION_SUFFIX=${target_suffix}"
    )

    if(WIN32)
      set(_build_types ";_DEBUG;_MINSIZEREL;_RELEASE;_RELWITHDEBINFO")
    else()
      set(_build_types "")
    endif()

    foreach(_build_type ${_build_types})
      set_target_properties(
        ${_target_name}
        PROPERTIES
          LIBRARY_OUTPUT_DIRECTORY${_build_type} "${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}"
          RUNTIME_OUTPUT_DIRECTORY${_build_type} "${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}"
      )
    endforeach()

    ament_target_dependencies(${_target_name}
      "rcl${target_suffix}"
      "rcljava_common"
      "rcl_interfaces"
    )

    target_include_directories(
      ${_target_name}
      PUBLIC
        ${JNI_INCLUDE_DIRS}
    )

    if(NOT rosidl_generate_interfaces_SKIP_INSTALL)
      ament_export_libraries(${_target_name})
    endif()

    install(TARGETS ${_target_name}
      ARCHIVE DESTINATION lib
      LIBRARY DESTINATION lib
    )

  endforeach()

endmacro()

set(CMAKE_JAVA_INCLUDE_PATH "${BUILTIN_COMMON_JARS}:${BUILTIN_MSGS_JARS}:${LOG_JAR}")
add_jar("${PROJECT_NAME}_jar"
  "${${PROJECT_NAME}_sources}"
  OUTPUT_NAME
  "${PROJECT_NAME}"
  INCLUDE_JARS
  "${std_msgs_JARS}"
  "${rcl_interfaces_JARS}"
  "${builtin_interfaces_JARS}"
  "${rcljava_common_JARS}"
  "${_jar_file}"
)

call_for_each_rmw_implementation(target)

install_jar("${PROJECT_NAME}_jar" "share/${PROJECT_NAME}/java")
ament_export_jars("share/${PROJECT_NAME}/java/${PROJECT_NAME}.jar")

if(BUILD_TESTING)
  find_package(ament_lint_auto REQUIRED)
  ament_lint_auto_find_test_dependencies()

  set(${PROJECT_NAME}_message_files
    "msg/BoundedArrayNested.msg"
    "msg/BoundedArrayPrimitives.msg"
    "msg/Builtins.msg"
    "msg/DynamicArrayNested.msg"
    "msg/DynamicArrayPrimitives.msg"
    "msg/Empty.msg"
    "msg/FieldsWithSameType.msg"
    "msg/Nested.msg"
    "msg/Primitives.msg"
    "msg/StaticArrayNested.msg"
    "msg/StaticArrayPrimitives.msg"
    "msg/UInt32.msg"
  )

  rosidl_generate_interfaces(${PROJECT_NAME}
    ${${PROJECT_NAME}_message_files}
    DEPENDENCIES builtin_interfaces
    SKIP_INSTALL
  )

  get_property(_${PROJECT_NAME}_jar_file
    TARGET "${PROJECT_NAME}_jar"
    PROPERTY "JAR_FILE")

  get_property(_${PROJECT_NAME}_messages_jar_file
    TARGET "${PROJECT_NAME}_messages_jar"
    PROPERTY "JAR_FILE")

  set(${PROJECT_NAME}_test_sources
    "src/test/java/org/ros2/rcljava/GraphNameTest.java"
    "src/test/java/org/ros2/rcljava/NodeTest.java"
    "src/test/java/org/ros2/rcljava/MessageTest.java"
    "src/test/java/org/ros2/rcljava/PublisherTest.java"
    "src/test/java/org/ros2/rcljava/RCLJavaTest.java"
    "src/test/java/org/ros2/rcljava/RmwTest.java"
    "src/test/java/org/ros2/rcljava/SubscriptionTest.java"
  )

  set(${PROJECT_NAME}_testsuites
    "org.ros2.rcljava.GraphNameTest"
    "org.ros2.rcljava.NodeTest"
    "org.ros2.rcljava.MessageTest"
    "org.ros2.rcljava.PublisherTest"
    "org.ros2.rcljava.RCLJavaTest"
    "org.ros2.rcljava.RmwTest"
    "org.ros2.rcljava.SubscriptionTest"
  )

  set(_deps_library_paths "")
  foreach(_dep_lib ${rcljava_test_msgs_LIBRARIES})
    get_filename_component(_dep_dir "${_dep_lib}" DIRECTORY)
    list(APPEND _deps_library_paths ${_dep_dir})
  endforeach()
  foreach(_dep_lib ${std_msgs_LIBRARIES})
    get_filename_component(_dep_dir "${_dep_lib}" DIRECTORY)
    list(APPEND _deps_library_paths ${_dep_dir})
  endforeach()
  foreach(_dep_lib ${rcl_interfaces_LIBRARIES})
    get_filename_component(_dep_dir "${_dep_lib}" DIRECTORY)
    list(APPEND _deps_library_paths ${_dep_dir})
  endforeach()
  foreach(_dep_lib ${builtin_interfaces_LIBRARIES})
    get_filename_component(_dep_dir "${_dep_lib}" DIRECTORY)
    list(APPEND _deps_library_paths ${_dep_dir})
  endforeach()
  list(APPEND _deps_library_paths ${CMAKE_CURRENT_BINARY_DIR})

  list(REMOVE_DUPLICATES _deps_library_paths)
  list(SORT _deps_library_paths)
  list(APPEND _deps_library_paths ${CMAKE_CURRENT_BINARY_DIR}/rosidl_generator_java/rcljava/msg/)

  foreach(testsuite ${${PROJECT_NAME}_testsuites})
    add_junit_tests("${PROJECT_NAME}_tests_${testsuite}"
      "${${PROJECT_NAME}_test_sources}"
      TESTS
      "${testsuite}"
      INCLUDE_JARS
      "${rcljava_common_JARS}"
      "${rcljava_test_msgs_JARS}"
      "${std_msgs_JARS}"
      "${builtin_interfaces_JARS}"
      "${rcl_interfaces_JARS}"
      "${_${PROJECT_NAME}_jar_file}"
      "${_${PROJECT_NAME}_messages_jar_file}"
      "${_jar_file}"
      LIBRARY_PATHS
      "${_deps_library_paths}"
    )
  endforeach()

endif()

ament_package()
