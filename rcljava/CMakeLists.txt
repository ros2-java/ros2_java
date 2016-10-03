cmake_minimum_required(VERSION 2.8.3)

project(rcljava)

find_package(ament_cmake REQUIRED)
find_package(ament_cmake_export_jars REQUIRED)
find_package(rcl REQUIRED)
find_package(rmw REQUIRED)
find_package(rmw_implementation_cmake REQUIRED)

if (ANDROID)
  find_host_package(Java COMPONENTS Development)
else()
  find_package(Java COMPONENTS Development)
  find_package(JNI REQUIRED)
endif()

include (UseJava)

set(CMAKE_JAVA_COMPILE_FLAGS "-source" "1.6" "-target" "1.6")

if(NOT WIN32)
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -Wall -Wextra")
endif()

macro(target)
  if(NOT "${target_suffix} " STREQUAL " ")
    get_rcl_information("${rmw_implementation}" "rcl${target_suffix}")
  endif()

  set(_jni_classes "RCLJava;Node;Publisher")

  foreach(_jni_class ${_jni_classes})

    set(_target_name "${PROJECT_NAME}${_jni_class}${target_suffix}")

    add_library(
      ${_target_name}
      SHARED src/main/cpp/org_ros2_rcljava_${_jni_class}.cpp
    )
    target_compile_definitions(${_target_name}
      PRIVATE "RMW_IMPLEMENTATION_SUFFIX=${target_suffix}")

    if(WIN32)
      set(_build_types ";_DEBUG;_MINSIZEREL;_RELEASE;_RELWITHDEBINFO")
    else()
      set(_build_types "")
    endif()

    foreach(_build_type ${_build_types})
      set_target_properties(${_target_name} PROPERTIES
        LIBRARY_OUTPUT_DIRECTORY${_build_type} "${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}"
        RUNTIME_OUTPUT_DIRECTORY${_build_type} "${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}"
      )
    endforeach()

    ament_target_dependencies(${_target_name}
      "rcl${target_suffix}"
    )

    target_include_directories(${_target_name}
      PUBLIC
      ${JNI_INCLUDE_DIRS}
    )

    if(NOT rosidl_generate_interfaces_SKIP_INSTALL)
      ament_export_libraries(${_target_name})

      install(TARGETS ${_msg_name}${_javaext_suffix}
        DESTINATION "${PYTHON_INSTALL_DIR}/${PROJECT_NAME}/${_msg_package_dir2}"
        ARCHIVE DESTINATION lib
        LIBRARY DESTINATION lib
      )
      install(TARGETS ${_target_name}
        ARCHIVE DESTINATION lib
        LIBRARY DESTINATION lib
      )
    endif()

  endforeach()

endmacro()

set(${PROJECT_NAME}_sources
  "src/main/java/org/ros2/rcljava/Node.java"
  "src/main/java/org/ros2/rcljava/RCLJava.java"
  "src/main/java/org/ros2/rcljava/Publisher.java"
  "src/main/java/org/ros2/rcljava/Subscription.java"
  "src/main/java/org/ros2/rcljava/Consumer.java"
  "src/main/java/org/ros2/rcljava/Log.java"
  "src/main/java/org/ros2/rcljava/QoSProfile.java"
  "src/main/java/org/ros2/rcljava/NativeUtils.java"
  "src/main/java/org/ros2/rcljava/exception/ImplementationAlreadyImportedException.java"
  "src/main/java/org/ros2/rcljava/exception/InvalidRCLJAVAImplementation.java"
  "src/main/java/org/ros2/rcljava/exception/NoImplementationAvailableException.java"
  "src/main/java/org/ros2/rcljava/exception/NotInitializedException.java"
)

add_jar("${PROJECT_NAME}_jar"
  "${${PROJECT_NAME}_sources}"
  OUTPUT_NAME
  "${PROJECT_NAME}"
)

call_for_each_rmw_implementation(target)

if(AMENT_ENABLE_TESTING)
  find_package(ament_lint_auto REQUIRED)
  ament_lint_auto_find_test_dependencies()

  ament_add_nose_test(rcljavatests test
    WORKING_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}"
  )
endif()

if(NOT rosidl_generate_interfaces_SKIP_INSTALL)
  set(_install_jar_dir "${CMAKE_CURRENT_BINARY_DIR}/${PROJECT_NAME}")
  get_property(_jar_file TARGET "${PROJECT_NAME}_jar" PROPERTY "JAR_FILE")

  install_jar("${PROJECT_NAME}_jar" "share/${PROJECT_NAME}/java")
  ament_export_jars("share/${PROJECT_NAME}/java/${PROJECT_NAME}.jar")
endif()

ament_package()
