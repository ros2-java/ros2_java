# Copyright 2014 Open Source Robotics Foundation, Inc.
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
# Install a Python package (and its recursive subpackages).
#
# :param package_name: the Python package name
# :type package_name: string
# :param PACKAGE_DIR: the path to the Python package directory (default:
#   <package_name> folder relative to the CMAKE_CURRENT_LIST_DIR)
# :type PACKAGE_DIR: string
#
macro(ament_java_install_package)
  _ament_cmake_java_register_environment_hook()
endmacro()

# register environment hook for JAVAPATH once
macro(_ament_cmake_java_register_environment_hook)
  if(NOT DEFINED _AMENT_CMAKE_JAVA_ENVIRONMENT_HOOK_REGISTERED)
    set(_AMENT_CMAKE_JAVA_ENVIRONMENT_HOOK_REGISTERED TRUE)

    _ament_cmake_java_get_java_install_dir()
    set(JAVAPATH "$AMENT_CURRENT_PREFIX/share/${PROJECT_NAME}/java/*")

    find_package(ament_cmake_core QUIET REQUIRED)

    message("INFO _ament_cmake_java_register_environment_hook")

    # backup variable
    set(_JAVA_INSTALL_DIR "${JAVA_INSTALL_DIR}")
    # use native separators in environment hook to match what pure Python packages do
    file(TO_NATIVE_PATH "${JAVA_INSTALL_DIR}" JAVA_INSTALL_DIR)
    ament_environment_hooks(
      "${ament_cmake_package_templates_ENVIRONMENT_HOOK_JAVAPATH}")
    # restore variable
    set(JAVA_INSTALL_DIR "${_JAVA_INSTALL_DIR}")
  endif()
endmacro()

macro(_ament_cmake_java_get_java_install_dir)
  if(NOT DEFINED JAVA_INSTALL_DIR)

    message("INFO _ament_cmake_java_get_java_install_dir")
    # avoid storing backslash in cached variable since CMake will interpret it
    # as escape character
    set(_java_code
      "from distutils.sysconfig import get_python_lib"
      "import os"
      "print(os.path.relpath(get_python_lib(prefix='${CMAKE_INSTALL_PREFIX}'), start='${CMAKE_INSTALL_PREFIX}').replace(os.sep, '/'))"
    )
    execute_process(
      COMMAND
      "${PYTHON_EXECUTABLE}"
      "-c"
      "${_java_code}"
      OUTPUT_VARIABLE _output
      RESULT_VARIABLE _result
      OUTPUT_STRIP_TRAILING_WHITESPACE
    )
    if(NOT _result EQUAL 0)
      message(FATAL_ERROR
        "execute_process(${PYTHON_EXECUTABLE} -c '${_java_code}') returned "
        "error code ${_result}")
    endif()

    set(JAVA_INSTALL_DIR
      "${_output}"
      CACHE INTERNAL
      "The directory for Python library installation. This needs to be in JAVAPATH when 'setup.py install' is called.")
  endif()
endmacro()
