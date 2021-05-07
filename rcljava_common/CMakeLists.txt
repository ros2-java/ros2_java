cmake_minimum_required(VERSION 3.5)

project(rcljava_common)

find_package(ament_cmake REQUIRED)

find_package(ament_cmake_export_jars REQUIRED)
find_package(ament_cmake_export_libraries REQUIRED)

list(INSERT CMAKE_MODULE_PATH 0 "${CMAKE_CURRENT_SOURCE_DIR}/cmake/Modules")

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

set(${PROJECT_NAME}_java_sources
  "src/main/java/org/ros2/rcljava/common/JNIUtils.java"
  "src/main/java/org/ros2/rcljava/exceptions/RCLException.java"
  "src/main/java/org/ros2/rcljava/exceptions/RCLReturn.java"
  "src/main/java/org/ros2/rcljava/interfaces/ActionDefinition.java"
  "src/main/java/org/ros2/rcljava/interfaces/Disposable.java"
  "src/main/java/org/ros2/rcljava/interfaces/FeedbackDefinition.java"
  "src/main/java/org/ros2/rcljava/interfaces/FeedbackMessageDefinition.java"
  "src/main/java/org/ros2/rcljava/interfaces/GoalDefinition.java"
  "src/main/java/org/ros2/rcljava/interfaces/GoalResponseDefinition.java"
  "src/main/java/org/ros2/rcljava/interfaces/GoalRequestDefinition.java"
  "src/main/java/org/ros2/rcljava/interfaces/MessageDefinition.java"
  "src/main/java/org/ros2/rcljava/interfaces/ResultDefinition.java"
  "src/main/java/org/ros2/rcljava/interfaces/ResultRequestDefinition.java"
  "src/main/java/org/ros2/rcljava/interfaces/ResultResponseDefinition.java"
  "src/main/java/org/ros2/rcljava/interfaces/ServiceDefinition.java"
)

set(${PROJECT_NAME}_cpp_sources
  "src/main/cpp/rcljava_common.cpp"
)

set(${PROJECT_NAME}_exported_jars "")
find_jar(COMMONS_LANG3_JAR NAMES commons-lang3)
if(${COMMONS_LANG3_JAR})
  list(APPEND ${PROJECT_NAME}_exported_jars ${COMMONS_LANG3_JAR})
else()
  set(commons_lang3_version "3.7")
  set(commons_lang3_sha256 "6e8dc31e046508d9953c96534edf0c2e0bfe6f468966b5b842b3f87e43b6a847")
  set(commons_lang3_url
    "https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/${commons_lang3_version}/commons-lang3-${commons_lang3_version}.jar")
  set(commons_lang3_jar_path "${CMAKE_CURRENT_BINARY_DIR}/jars/commons-lang3-${commons_lang3_version}.jar")
  file(DOWNLOAD ${commons_lang3_url} ${commons_lang3_jar_path} EXPECTED_HASH SHA256=${commons_lang3_sha256})

  install(FILES
    ${commons_lang3_jar_path}
    DESTINATION
    "share/${PROJECT_NAME}/java")

  set(COMMONS_LANG3_JAR "${commons_lang3_jar_path}")
  list(APPEND ${PROJECT_NAME}_exported_jars "share/${PROJECT_NAME}/java/commons-lang3-${commons_lang3_version}.jar")
endif()

find_jar(SFL4J_JAR NAMES slf4j-api)
if(${SFL4J_JAR})
  list(APPEND ${PROJECT_NAME}_exported_jars ${SFL4J_JAR})
else()
  set(slf4j_version "1.7.21")
  set(slf4j_api_sha256 "1d5aeb6bd98b0fdd151269eae941c05f6468a791ea0f1e68d8e7fe518af3e7df")
  set(slf4j_api_url "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/${slf4j_version}/slf4j-api-${slf4j_version}.jar")
  set(slf4j_api_jar_path "${CMAKE_CURRENT_BINARY_DIR}/jars/slf4j-api-${slf4j_version}.jar")
  file(DOWNLOAD ${slf4j_api_url} ${slf4j_api_jar_path} EXPECTED_HASH SHA256=${slf4j_api_sha256})

  install(FILES
    ${slf4j_api_jar_path}
    DESTINATION
    "share/${PROJECT_NAME}/java")

  set(SFL4J_JAR "${slf4j_api_jar_path}")
  list(APPEND ${PROJECT_NAME}_exported_jars "share/${PROJECT_NAME}/java/slf4j-api-${slf4j_version}.jar")

  if(ANDROID)
    set(slf4j_android_sha256 "3874b0975f9ad283328c0371ccde2de353754da33fda36e611702623ee1e5a95")
    set(slf4j_android_url "https://repo1.maven.org/maven2/org/slf4j/slf4j-android/${slf4j_version}/slf4j-android-${slf4j_version}.jar")
    set(slf4j_android_jar_path "${CMAKE_CURRENT_BINARY_DIR}/jars/slf4j-android-${slf4j_version}.jar")
    file(DOWNLOAD ${slf4j_android_url} ${slf4j_android_jar_path} EXPECTED_HASH SHA256=${slf4j_android_sha256})
    list(APPEND logging_jars "${slf4j_android_jar_path}")

    install(FILES
      ${slf4j_android_jar_path}
      DESTINATION
      "share/${PROJECT_NAME}/java")

    list(APPEND ${PROJECT_NAME}_exported_jars "share/${PROJECT_NAME}/java/slf4j-android-${slf4j_version}.jar")
  else()
    set(log4j_version "1.2.17")

    set(log4j_sha256 "1d31696445697720527091754369082a6651bd49781b6005deb94e56753406f9")
    set(slf4j_log4j_sha256 "9563e26fd7863cca0f5d468ef09f2927047f8b85a8c76d98766ed5863cb678b2")
    set(slf4j_jdk14_sha256 "2c369503c911bf9ac7c5f4813bc1f8f95866e2e029fae203a02712f5671f5e4a")

    set(log4j_url "https://repo1.maven.org/maven2/log4j/log4j/${log4j_version}/log4j-${log4j_version}.jar")
    set(slf4j_log4j_url "https://repo1.maven.org/maven2/org/slf4j/slf4j-log4j12/${slf4j_version}/slf4j-log4j12-${slf4j_version}.jar")
    set(slf4j_jdk14_url "https://repo1.maven.org/maven2/org/slf4j/slf4j-jdk14/${slf4j_version}/slf4j-jdk14-${slf4j_version}.jar")

    set(log4j_jar_path "${CMAKE_CURRENT_BINARY_DIR}/jars/log4j-${log4j_version}.jar")
    set(slf4j_log4j_jar_path "${CMAKE_CURRENT_BINARY_DIR}/jars/slf4j-log4j12-${slf4j_version}.jar")
    set(slf4j_jdk14_jar_path "${CMAKE_CURRENT_BINARY_DIR}/jars/slf4j-jdk14-${slf4j_version}.jar")

    file(DOWNLOAD ${log4j_url} ${log4j_jar_path} EXPECTED_HASH SHA256=${log4j_sha256})
    file(DOWNLOAD ${slf4j_log4j_url} ${slf4j_log4j_jar_path} EXPECTED_HASH SHA256=${slf4j_log4j_sha256})
    file(DOWNLOAD ${slf4j_jdk14_url} ${slf4j_jdk14_jar_path} EXPECTED_HASH SHA256=${slf4j_jdk14_sha256})
    list(APPEND logging_jars "${log4j_jar_path}")
    list(APPEND logging_jars "${slf4j_log4j_jar_path}")
    list(APPEND logging_jars "${slf4j_jdk14_jar_path}")

    install(FILES
      ${log4j_jar_path} ${slf4j_api_jar_path}
      ${slf4j_log4j_jar_path} ${slf4j_android_jar_path}
      ${slf4j_jdk14_jar_path}
      DESTINATION
      "share/${PROJECT_NAME}/java")

    list(APPEND ${PROJECT_NAME}_exported_jars "share/${PROJECT_NAME}/java/log4j-${log4j_version}.jar")
    list(APPEND ${PROJECT_NAME}_exported_jars "share/${PROJECT_NAME}/java/slf4j-log4j12-${slf4j_version}.jar")
    list(APPEND ${PROJECT_NAME}_exported_jars "share/${PROJECT_NAME}/java/slf4j-jdk14-${slf4j_version}.jar")
  endif()
endif()

add_jar("${PROJECT_NAME}_jar"
  "${${PROJECT_NAME}_java_sources}"
  OUTPUT_NAME
  "${PROJECT_NAME}"
  INCLUDE_JARS
  "${SFL4J_JAR}"
)

install_jar("${PROJECT_NAME}_jar" "share/${PROJECT_NAME}/java")
list(APPEND ${PROJECT_NAME}_exported_jars "share/${PROJECT_NAME}/java/${PROJECT_NAME}.jar")

ament_export_jars(${${PROJECT_NAME}_exported_jars})

add_source_jar("${PROJECT_NAME}-source_jar"
  ${${PROJECT_NAME}_java_sources}
  OUTPUT_NAME
  ${PROJECT_NAME}-source
)

install_jar("${PROJECT_NAME}-source_jar" "share/${PROJECT_NAME}/java")

include_directories(include)

add_library(${PROJECT_NAME} SHARED ${${PROJECT_NAME}_cpp_sources})

target_include_directories(${PROJECT_NAME}
  PUBLIC
  ${JNI_INCLUDE_DIRS}
)

ament_export_dependencies(ament_cmake)
ament_export_include_directories(include)
ament_export_libraries(${PROJECT_NAME})

install(
  TARGETS ${PROJECT_NAME}
  ARCHIVE DESTINATION lib
  LIBRARY DESTINATION lib
  RUNTIME DESTINATION bin)

install(
  DIRECTORY include/
  DESTINATION include
)

if(BUILD_TESTING)
  find_package(ament_lint_auto REQUIRED)
  ament_lint_auto_find_test_dependencies()
endif()

ament_package(
  CONFIG_EXTRAS "rcljava_cmake_module-extras.cmake.in"
)

install(DIRECTORY cmake
  DESTINATION share/${PROJECT_NAME}
)
