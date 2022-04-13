cmake_minimum_required(VERSION 3.5)

project(rcljava)

find_package(ament_cmake REQUIRED)
find_package(ament_cmake_export_jars REQUIRED)
find_package(ament_cmake_export_jni_libraries REQUIRED)
find_package(builtin_interfaces REQUIRED)
find_package(rcpputils REQUIRED)
find_package(rcl REQUIRED)
find_package(rcl_interfaces REQUIRED)
find_package(rcljava_common REQUIRED)
find_package(rmw REQUIRED)
find_package(rmw_implementation_cmake REQUIRED)

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
include(JavaExtra)

# Default to C++14
if(NOT CMAKE_CXX_STANDARD)
  set(CMAKE_CXX_STANDARD 14)
endif()

if(NOT WIN32)
  add_compile_options(-Wall -Wextra -Wpedantic)
endif()

set(CMAKE_JAVA_COMPILE_FLAGS "-source" "1.6" "-target" "1.6")

include_directories(include)

function(set_properties _target_name _build_type)
  set_target_properties(${_target_name} PROPERTIES
    PREFIX ""
    LIBRARY_OUTPUT_DIRECTORY${_build_type} "${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}"
    RUNTIME_OUTPUT_DIRECTORY${_build_type} "${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}"
  )
endfunction()

# Only build the library if a C typesupport exists
get_rmw_typesupport(typesupport_impls "rmw_implementation" LANGUAGE "c")
if(typesupport_impls STREQUAL "")
  message(STATUS "Skipping rcljava because no C typesupport library was found.")
  return()
endif()

set(${PROJECT_NAME}_jni_sources
  "src/main/cpp/org_ros2_rcljava_RCLJava.cpp"
  "src/main/cpp/org_ros2_rcljava_Time.cpp"
  "src/main/cpp/org_ros2_rcljava_client_ClientImpl.cpp"
  "src/main/cpp/org_ros2_rcljava_contexts_ContextImpl.cpp"
  "src/main/cpp/org_ros2_rcljava_detail_QosIncompatibleStatus.cpp"
  "src/main/cpp/org_ros2_rcljava_executors_BaseExecutor.cpp"
  "src/main/cpp/org_ros2_rcljava_events_EventHandlerImpl.cpp"
  "src/main/cpp/org_ros2_rcljava_graph_EndpointInfo"
  "src/main/cpp/org_ros2_rcljava_publisher_statuses_LivelinessLost.cpp"
  "src/main/cpp/org_ros2_rcljava_publisher_statuses_OfferedDeadlineMissed.cpp"
  "src/main/cpp/org_ros2_rcljava_publisher_statuses_OfferedQosIncompatible.cpp"
  "src/main/cpp/org_ros2_rcljava_node_NodeImpl.cpp"
  "src/main/cpp/org_ros2_rcljava_publisher_PublisherImpl.cpp"
  "src/main/cpp/org_ros2_rcljava_qos_QoSProfile.cpp"
  "src/main/cpp/org_ros2_rcljava_service_ServiceImpl.cpp"
  "src/main/cpp/org_ros2_rcljava_subscription_SubscriptionImpl.cpp"
  "src/main/cpp/org_ros2_rcljava_subscription_statuses_LivelinessChanged.cpp"
  "src/main/cpp/org_ros2_rcljava_subscription_statuses_MessageLost.cpp"
  "src/main/cpp/org_ros2_rcljava_subscription_statuses_RequestedDeadlineMissed.cpp"
  "src/main/cpp/org_ros2_rcljava_subscription_statuses_RequestedQosIncompatible.cpp"
  "src/main/cpp/org_ros2_rcljava_time_Clock.cpp"
  "src/main/cpp/org_ros2_rcljava_timer_WallTimerImpl.cpp"
)

foreach(_jni_source ${${PROJECT_NAME}_jni_sources})

  get_filename_component(_target_name "${_jni_source}" NAME_WE)
  string_camel_case_to_lower_case_underscore("${_target_name}" _target_name)

  add_library(${_target_name}
    SHARED ${_jni_source}
  )
  if(WIN32)
    set(_jni_prefix "")
  else()
    set(_jni_prefix "lib")
  endif()
  set_target_properties(${_target_name} PROPERTIES
    OUTPUT_NAME "${_jni_prefix}${_target_name}__jni"
  )

  set_properties("${_target_name}" "")
  if(WIN32)
    set_properties("${_target_name}" "_DEBUG")
    set_properties("${_target_name}" "_MINSIZEREL")
    set_properties("${_target_name}" "_RELEASE")
    set_properties("${_target_name}" "_RELWITHDEBINFO")
  endif()

  ament_target_dependencies(${_target_name}
    "builtin_interfaces"
    "rcl"
    "rcljava_common"
    "rcl_interfaces"
    "rcpputils"
  )

  target_include_directories(${_target_name}
    PUBLIC
    ${JNI_INCLUDE_DIRS}
  )

  ament_export_jni_libraries(${_target_name})

  install(TARGETS ${_target_name}
    ARCHIVE DESTINATION lib/jni
    LIBRARY DESTINATION lib/jni
  )

endforeach()

set(${PROJECT_NAME}_sources
  "src/main/java/org/ros2/rcljava/RCLJava.java"
  "src/main/java/org/ros2/rcljava/Time.java"
  "src/main/java/org/ros2/rcljava/client/Client.java"
  "src/main/java/org/ros2/rcljava/client/ClientImpl.java"
  "src/main/java/org/ros2/rcljava/concurrent/Callback.java"
  "src/main/java/org/ros2/rcljava/concurrent/RCLFuture.java"
  "src/main/java/org/ros2/rcljava/contexts/Context.java"
  "src/main/java/org/ros2/rcljava/contexts/ContextImpl.java"
  "src/main/java/org/ros2/rcljava/consumers/BiConsumer.java"
  "src/main/java/org/ros2/rcljava/consumers/Consumer.java"
  "src/main/java/org/ros2/rcljava/consumers/TriConsumer.java"
  "src/main/java/org/ros2/rcljava/detail/QosIncompatibleStatus.java"
  "src/main/java/org/ros2/rcljava/events/EventHandler.java"
  "src/main/java/org/ros2/rcljava/events/EventHandlerImpl.java"
  "src/main/java/org/ros2/rcljava/events/EventStatus.java"
  "src/main/java/org/ros2/rcljava/events/PublisherEventStatus.java"
  "src/main/java/org/ros2/rcljava/events/SubscriptionEventStatus.java"
  "src/main/java/org/ros2/rcljava/graph/NameAndTypes.java"
  "src/main/java/org/ros2/rcljava/graph/NodeNameInfo.java"
  "src/main/java/org/ros2/rcljava/executors/AnyExecutable.java"
  "src/main/java/org/ros2/rcljava/executors/BaseExecutor.java"
  "src/main/java/org/ros2/rcljava/executors/Executor.java"
  "src/main/java/org/ros2/rcljava/executors/MultiThreadedExecutor.java"
  "src/main/java/org/ros2/rcljava/executors/SingleThreadedExecutor.java"
  "src/main/java/org/ros2/rcljava/graph/EndpointInfo.java"
  "src/main/java/org/ros2/rcljava/node/BaseComposableNode.java"
  "src/main/java/org/ros2/rcljava/node/ComposableNode.java"
  "src/main/java/org/ros2/rcljava/node/Node.java"
  "src/main/java/org/ros2/rcljava/node/NodeImpl.java"
  "src/main/java/org/ros2/rcljava/node/NodeOptions.java"
  "src/main/java/org/ros2/rcljava/parameters/client/AsyncParametersClient.java"
  "src/main/java/org/ros2/rcljava/parameters/client/AsyncParametersClientImpl.java"
  "src/main/java/org/ros2/rcljava/parameters/client/SyncParametersClient.java"
  "src/main/java/org/ros2/rcljava/parameters/client/SyncParametersClientImpl.java"
  "src/main/java/org/ros2/rcljava/parameters/InvalidParametersException.java"
  "src/main/java/org/ros2/rcljava/parameters/InvalidParameterValueException.java"
  "src/main/java/org/ros2/rcljava/parameters/ParameterAlreadyDeclaredException.java"
  "src/main/java/org/ros2/rcljava/parameters/ParameterCallback.java"
  "src/main/java/org/ros2/rcljava/parameters/ParameterNames.java"
  "src/main/java/org/ros2/rcljava/parameters/ParameterNotDeclaredException.java"
  "src/main/java/org/ros2/rcljava/parameters/ParameterType.java"
  "src/main/java/org/ros2/rcljava/parameters/ParameterVariant.java"
  "src/main/java/org/ros2/rcljava/parameters/service/ParameterService.java"
  "src/main/java/org/ros2/rcljava/parameters/service/ParameterServiceImpl.java"
  "src/main/java/org/ros2/rcljava/publisher/Publisher.java"
  "src/main/java/org/ros2/rcljava/publisher/PublisherImpl.java"
  "src/main/java/org/ros2/rcljava/publisher/statuses/LivelinessLost.java"
  "src/main/java/org/ros2/rcljava/publisher/statuses/OfferedDeadlineMissed.java"
  "src/main/java/org/ros2/rcljava/publisher/statuses/OfferedQosIncompatible.java"
  "src/main/java/org/ros2/rcljava/qos/policies/Durability.java"
  "src/main/java/org/ros2/rcljava/qos/policies/History.java"
  "src/main/java/org/ros2/rcljava/qos/policies/Liveliness.java"
  "src/main/java/org/ros2/rcljava/qos/policies/QoSPolicy.java"
  "src/main/java/org/ros2/rcljava/qos/policies/Reliability.java"
  "src/main/java/org/ros2/rcljava/qos/QoSProfile.java"
  "src/main/java/org/ros2/rcljava/service/RMWRequestId.java"
  "src/main/java/org/ros2/rcljava/service/Service.java"
  "src/main/java/org/ros2/rcljava/service/ServiceImpl.java"
  "src/main/java/org/ros2/rcljava/subscription/Subscription.java"
  "src/main/java/org/ros2/rcljava/subscription/SubscriptionImpl.java"
  "src/main/java/org/ros2/rcljava/subscription/statuses/LivelinessChanged.java"
  "src/main/java/org/ros2/rcljava/subscription/statuses/MessageLost.java"
  "src/main/java/org/ros2/rcljava/subscription/statuses/RequestedDeadlineMissed.java"
  "src/main/java/org/ros2/rcljava/subscription/statuses/RequestedQosIncompatible.java"
  "src/main/java/org/ros2/rcljava/time/Clock.java"
  "src/main/java/org/ros2/rcljava/time/ClockType.java"
  "src/main/java/org/ros2/rcljava/timer/Timer.java"
  "src/main/java/org/ros2/rcljava/timer/WallTimer.java"
  "src/main/java/org/ros2/rcljava/timer/WallTimerImpl.java"
)

add_jar("${PROJECT_NAME}_jar"
  ${${PROJECT_NAME}_sources}
  OUTPUT_NAME
  ${PROJECT_NAME}
  INCLUDE_JARS
  ${rcljava_common_JARS}
  ${builtin_interfaces_JARS}
  ${rcl_interfaces_JARS}
)

install_jar("${PROJECT_NAME}_jar" "share/${PROJECT_NAME}/java")
ament_export_jars("share/${PROJECT_NAME}/java/${PROJECT_NAME}.jar")

if(BUILD_TESTING)
  find_package(ament_lint_auto REQUIRED)
  find_package(std_msgs REQUIRED)
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

  set(${PROJECT_NAME}_service_files
    "srv/AddTwoInts.srv"
  )

  rosidl_generator_java_get_typesupports(_java_type_supports)

  rosidl_generate_interfaces(${PROJECT_NAME}
    ${${PROJECT_NAME}_message_files}
    ${${PROJECT_NAME}_service_files}
    DEPENDENCIES
    builtin_interfaces
    rcl_interfaces
    ${_java_type_supports}
    SKIP_INSTALL
  )

  get_property(_${PROJECT_NAME}_jar_file
    TARGET "${PROJECT_NAME}_jar"
    PROPERTY "JAR_FILE")

  get_property(_${PROJECT_NAME}_messages_jar_file
    TARGET "${PROJECT_NAME}_messages_jar"
    PROPERTY "JAR_FILE")

  set(${PROJECT_NAME}_test_sources
    "src/test/java/org/ros2/rcljava/RCLJavaTest.java"
    "src/test/java/org/ros2/rcljava/SpinTest.java"
    "src/test/java/org/ros2/rcljava/TimeTest.java"
    "src/test/java/org/ros2/rcljava/client/ClientTest.java"
    "src/test/java/org/ros2/rcljava/node/NodeOptionsTest.java"
    "src/test/java/org/ros2/rcljava/node/NodeParametersTest.java"
    "src/test/java/org/ros2/rcljava/node/NodeUndeclaredParametersTest.java"
    "src/test/java/org/ros2/rcljava/node/NodeTest.java"
    # "src/test/java/org/ros2/rcljava/parameters/AsyncParametersClientTest.java"
    # "src/test/java/org/ros2/rcljava/parameters/SyncParametersClientTest.java"
    "src/test/java/org/ros2/rcljava/publisher/PublisherTest.java"
    "src/test/java/org/ros2/rcljava/qos/QoSProfileTest.java"
    "src/test/java/org/ros2/rcljava/subscription/SubscriptionTest.java"
    "src/test/java/org/ros2/rcljava/timer/TimerTest.java"
  )

  set(${PROJECT_NAME}_testsuites
    "org.ros2.rcljava.RCLJavaTest"
    "org.ros2.rcljava.SpinTest"
    "org.ros2.rcljava.TimeTest"
    "org.ros2.rcljava.client.ClientTest"
    "org.ros2.rcljava.node.NodeOptionsTest"
    "org.ros2.rcljava.node.NodeParametersTest"
    "org.ros2.rcljava.node.NodeUndeclaredParametersTest"
    "org.ros2.rcljava.node.NodeTest"
    # "org.ros2.rcljava.parameters.SyncParametersClientTest"
    "org.ros2.rcljava.publisher.PublisherTest"
    "org.ros2.rcljava.qos.QoSProfileTest"
    "org.ros2.rcljava.subscription.SubscriptionTest"
    "org.ros2.rcljava.timer.TimerTest"
  )

  set(_deps_library_dirs "")
  foreach(_dep_lib ${rcljava_test_msgs_LIBRARIES})
    get_filename_component(_dep_dir "${_dep_lib}" DIRECTORY)
    list_append_unique(_deps_library_dirs ${_dep_dir})
  endforeach()
  foreach(_dep_lib ${rcljava_test_msgs_JNI_LIBRARIES})
    get_filename_component(_dep_dir "${_dep_lib}" DIRECTORY)
    list_append_unique(_deps_library_dirs ${_dep_dir})
  endforeach()

  foreach(_dep_lib ${std_msgs_LIBRARIES})
    get_filename_component(_dep_dir "${_dep_lib}" DIRECTORY)
    list_append_unique(_deps_library_dirs ${_dep_dir})
  endforeach()
  foreach(_dep_lib ${std_msgs_JNI_LIBRARIES})
    get_filename_component(_dep_dir "${_dep_lib}" DIRECTORY)
    list_append_unique(_deps_library_dirs ${_dep_dir})
  endforeach()

  foreach(_dep_lib ${builtin_interfaces_LIBRARIES})
    get_filename_component(_dep_dir "${_dep_lib}" DIRECTORY)
    list_append_unique(_deps_library_dirs ${_dep_dir})
  endforeach()
  foreach(_dep_lib ${builtin_interfaces_JNI_LIBRARIES})
    get_filename_component(_dep_dir "${_dep_lib}" DIRECTORY)
    list_append_unique(_deps_library_dirs ${_dep_dir})
  endforeach()

  foreach(_dep_lib ${rcl_interfaces_LIBRARIES})
    get_filename_component(_dep_dir "${_dep_lib}" DIRECTORY)
    list_append_unique(_deps_library_dirs ${_dep_dir})
  endforeach()
  foreach(_dep_lib ${rcl_interfaces_JNI_LIBRARIES})
    get_filename_component(_dep_dir "${_dep_lib}" DIRECTORY)
    list_append_unique(_deps_library_dirs ${_dep_dir})
  endforeach()

  list_append_unique(_deps_library_dirs ${CMAKE_CURRENT_BINARY_DIR})
  list_append_unique(_deps_library_dirs ${CMAKE_CURRENT_BINARY_DIR}/rcljava)
  list_append_unique(_deps_library_dirs ${CMAKE_CURRENT_BINARY_DIR}/rosidl_generator_java/rcljava/msg/)
  list_append_unique(_deps_library_dirs ${CMAKE_CURRENT_BINARY_DIR}/rosidl_generator_java/rcljava/srv/)

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
      "${rcljava_test_msgs_JARS}"
      "${std_msgs_JARS}"
      "${builtin_interfaces_JARS}"
      "${rcl_interfaces_JARS}"
      "${_${PROJECT_NAME}_jar_file}"
      "${_${PROJECT_NAME}_messages_jar_file}"
      APPEND_LIBRARY_DIRS
      "${_deps_library_dirs}"
    )

    add_dependencies("${PROJECT_NAME}_tests_${testsuite}" "${PROJECT_NAME}_messages_jar")
  endforeach()

endif()

ament_package()
