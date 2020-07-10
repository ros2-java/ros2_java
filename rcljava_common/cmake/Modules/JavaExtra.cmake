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

function(ament_add_junit_tests TARGET_NAME)

  cmake_parse_arguments(ARG
    ""
    "TIMEOUT;WORKING_DIRECTORY"
    "APPEND_ENV;APPEND_LIBRARY_DIRS;ENV;TESTS;SOURCES;INCLUDE_JARS"
    ${ARGN}
  )
  if(ARG_UNPARSED_ARGUMENTS)
    message(FATAL_ERROR "ament_add_junit_tests() called with unused arguments: "
    "${ARG_UNPARSED_ARGUMENTS}")
  endif()

  message("CHRIS: INCLUDE_JARS: ${ARG_INCLUDE_JARS}")

  set(_source_files ${ARG_SOURCES})

  if(WIN32 AND NOT CYGWIN)
    set(SEPARATOR "\;")
  else()
    set(SEPARATOR ":")
  endif()

  set(exported_jars "")

  find_jar(JUNIT_JAR NAMES junit4)
  if(${JUNIT_JAR})
    list(APPEND exported_jars ${JUNIT_JAR})
  else()
    find_jar(JUNIT_JAR NAMES junit VERSIONS 4)
    if(${JUNIT_JAR})
      list(APPEND exported_jars ${JUNIT_JAR})
    else()
      set(junit_version "4.12")

      set(junit_sha256 "59721f0805e223d84b90677887d9ff567dc534d7c502ca903c0c2b17f05c116a")

      set(junit_url "https://repo1.maven.org/maven2/junit/junit/${junit_version}/junit-${junit_version}.jar")

      set(junit_jar_path "${CMAKE_CURRENT_BINARY_DIR}/jars/junit-${junit_version}.jar")

      file(DOWNLOAD ${junit_url} ${junit_jar_path} EXPECTED_HASH SHA256=${junit_sha256})

      install(FILES
        ${junit_jar_path}
        DESTINATION
        "share/${PROJECT_NAME}/java")

      list(APPEND exported_jars "share/${PROJECT_NAME}/java/junit-${junit_version}.jar")

      set(JUNIT_JAR "${junit_jar_path}")
    endif()
  endif()

  find_jar(HAMCREST_JAR NAMES hamcrest-all)
  if(${HAMCREST_JAR})
    list(APPEND exported_jars ${HAMCREST_JAR})
  else()
    set(hamcrest_version "1.3")

    set(hamcrest_sha256 "4877670629ab96f34f5f90ab283125fcd9acb7e683e66319a68be6eb2cca60de")

    set(hamcrest_url "https://repo1.maven.org/maven2/org/hamcrest/hamcrest-all/${hamcrest_version}/hamcrest-all-${hamcrest_version}.jar")

    set(hamcrest_jar_path "${CMAKE_CURRENT_BINARY_DIR}/jars/hamcrest-all-${hamcrest_version}.jar")

    file(DOWNLOAD ${hamcrest_url} ${hamcrest_jar_path} EXPECTED_HASH SHA256=${hamcrest_sha256})

    install(FILES
      ${hamcrest_jar_path}
      DESTINATION
      "share/${PROJECT_NAME}/java")

    list(APPEND exported_jars "share/${PROJECT_NAME}/java/hamcrest-all-${hamcrest_version}.jar")

    set(HAMCREST_JAR "${hamcrest_jar_path}")
  endif()

  ament_export_jars(${exported_jars})

  message("CHRIS: JUNIT_JAR before normalize: ${JUNIT_JAR}")
  #normalize_path(JUNIT_JAR "${JUNIT_JAR}")
  message("CHRIS: JUNIT_JAR after normalize: ${JUNIT_JAR}")
  message("CHRIS: HAMCREST_JAR before normalize: ${HAMCREST_JAR}")
  #normalize_path(HAMCREST_JAR "${HAMCREST_JAR}")
  message("CHRIS: HAMCREST_JAR after normalize: ${HAMCREST_JAR}")
  set(${TARGET_NAME}_jar_dependencies "${JUNIT_JAR}${SEPARATOR}${HAMCREST_JAR}")

  add_jar("${TARGET_NAME}_jar"
    "${_source_files}"
    OUTPUT_NAME
    "${TARGET_NAME}"
    INCLUDE_JARS
    "${ARG_INCLUDE_JARS}"
    "${JUNIT_JAR}"
    "${HAMCREST_JAR}"
  )

  get_property(_jar_test_file
    TARGET "${TARGET_NAME}_jar"
    PROPERTY "JAR_FILE"
  )

  #normalize_path(_jar_test_file "${_jar_test_file}")
  #set(${TARGET_NAME}_jar_dependencies "${${TARGET_NAME}_jar_dependencies}${SEPARATOR}${_jar_test_file}")
  string(APPEND ${TARGET_NAME}_jar_dependencies "${SEPARATOR}${_jar_test_file}")
  foreach(_jar_dep ${ARG_INCLUDE_JARS})
    #set(${TARGET_NAME}_jar_dependencies "${${TARGET_NAME}_jar_dependencies}${SEPARATOR}${_jar_dep}")
    #normalize_path(_jar_dep "${_jar_dep}")
    string(APPEND ${TARGET_NAME}_jar_dependencies "${SEPARATOR}${_jar_dep}")
  endforeach()

  #string(REPLACE ";" "${SEPARATOR}" _library_paths "${ARG_APPEND_LIBRARY_DIRS}")
  set(_library_paths "")
  foreach(_lib ${ARG_APPEND_LIBRARY_DIRS})
    #normalize_path(_lib "${_lib}")
    string(APPEND _library_paths "${SEPARATOR}${_lib}")
  endforeach()
  string(SUBSTRING ${_library_paths} 1 -1 _library_paths)
  message("CHRIS: Separator: '${SEPARATOR}'")
  message("CHRIS: ARG_APPEND_LIBRARY_DIRS: '${ARG_APPEND_LIBRARY_DIRS}'")
  message("CHRIS: jar_dependencies: '${${TARGET_NAME}_jar_dependencies}'")
  message("CHRIS: _library_paths: '${_library_paths}'")

  if(ARG_ENV)
    set(ARG_ENV "ENV" ${ARG_ENV})
  endif()
  if(ARG_APPEND_ENV)
    set(ARG_APPEND_ENV "APPEND_ENV" ${ARG_APPEND_ENV})
  endif()
  if(ARG_APPEND_LIBRARY_DIRS)
    set(ARG_APPEND_LIBRARY_DIRS "APPEND_LIBRARY_DIRS" ${ARG_APPEND_LIBRARY_DIRS})
  endif()
  if(ARG_TIMEOUT)
    set(ARG_TIMEOUT "TIMEOUT" "${ARG_TIMEOUT}")
  endif()
  if(ARG_WORKING_DIRECTORY)
    set(ARG_WORKING_DIRECTORY "WORKING_DIRECTORY" "${ARG_WORKING_DIRECTORY}")
  endif()
  if(ARG_SKIP_TEST)
    set(ARG_SKIP_TEST "SKIP_TEST")
  endif()

  ament_add_test(
    ${TARGET_NAME}
    GENERATE_RESULT_FOR_RETURN_CODE_ZERO
    COMMAND ${Java_JAVA_EXECUTABLE}
    ${JVMARGS} -classpath "${${TARGET_NAME}_jar_dependencies}" -Djava.library.path="${_library_paths}"
    org.junit.runner.JUnitCore
    ${ARG_TESTS}
    ${ARG_SKIP_TEST}
    ${ARG_ENV}
    ${ARG_APPEND_ENV}
    ${ARG_APPEND_LIBRARY_DIRS}
    ${ARG_TIMEOUT}
    ${ARG_WORKING_DIRECTORY}
  )

  add_custom_target(${TARGET_NAME} DEPENDS ${_jar_test_file})

  add_dependencies(${TARGET_NAME} "${TARGET_NAME}_jar")
endfunction()
