# ROS 2 Java client library

### Build status

| Target                                    | Status        |
|-------------------------------------------|---------------|
| **ROS Galactic - Ubuntu Focal (OpenJDK)** | ![Build Status](https://github.com/ros2-java/ros2_java/workflows/CI/badge.svg?branch=main) |

## Introduction

This is a set of projects (bindings, code generator, examples and more) that enables developers to write ROS 2
applications for the JVM and Android.

Besides this repository itself, there's also:
- https://github.com/ros2-java/ament_java, which adds support for Gradle to Ament
- https://github.com/ros2-java/ament_gradle_plugin, a Gradle plugin that makes it easier to use ROS 2 in Java and Android project. The Gradle plugin can be installed from Gradle Central https://plugins.gradle.org/plugin/org.ros2.tools.gradle
- https://github.com/ros2-java/ros2_java_examples, examples for the Java Runtime Environment
- https://github.com/ros2-java/ros2_android_examples, examples for Android

### Is this Java only?

No, any language that targets the JVM can be used to write ROS 2 applications.

### Including Android?

Yep! Make sure to use Fast-RTPS as your DDS vendor and at least [this revision](https://github.com/eProsima/Fast-RTPS/commit/5301ef203d45528a083821c3ba582164d782360b).

### Features

The current set of features include:
- Generation of all builtin and complex ROS types, including arrays, strings, nested types, constants, etc.
- Support for publishers and subscriptions
- Tunable Quality of Service (e.g. lossy networks, reliable delivery, etc.)
- Clients and services
- Timers
- Composition (i.e. more than one node per process)
- Time handling (system and steady, ROS time not yet supported https://github.com/ros2-java/ros2_java/issues/122)
- Support for Android
- Parameters services and clients (both asynchronous and synchronous)

## Sounds great, how can I try this out?

### Install dependencies

> Note: While the following instructions use a Linux shell the same can be done on other platforms like Windows with slightly adjusted commands.

1. [Install ROS 2](https://index.ros.org/doc/ros2/Installation).

1. Install Java and a JDK.

    On Ubuntu, you can install OpenJDK with:

        sudo apt install default-jdk

1. Install Gradle.
Make sure you have Gradle 3.2 (or later) installed.

    *Ubuntu Bionic or later*

        sudo apt install gradle

    *macOS*

        brew install gradle

    Note: if run into compatibily issues between gradle 3.x and Java 9, try using Java 8,

        brew tap caskroom/versions
        brew cask install java8
        export JAVA_HOME=/Library/Java/JavaVirtualMachines/1.8.0.jdk/Contents/Home

    *Windows*

        choco install gradle

1. Install build tools:

        sudo apt install curl python3-colcon-common-extensions python3-pip python3-vcstool

1. Install Gradle extensions for colcon:

        python3 -m pip install -U colcon-gradle colcon-ros-gradle

### Download and Build ROS 2 Java for Desktop

1. Source your ROS 2 installation, for example:

        source /opt/ros/galactic/setup.bash

1. Download the ROS 2 Java repositories into a workspace:

        mkdir -p ros2_java_ws/src
        cd ros2_java_ws
        curl -skL https://raw.githubusercontent.com/ros2-java/ros2_java/main/ros2_java_desktop.repos | vcs import src

1. **Linux only** Install ROS dependencies:

        rosdep install --from-paths src -y -i --skip-keys "ament_tools"

1. Build desktop packages:

        colcon build --symlink-install

    *Note, on Windows we have to use `--merge-install`*

        colcon build --merge-install


### Download and Build ROS 2 Java for Android

The Android setup is slightly more complex, you'll need the SDK and NDK installed, and an Android device where you can run the examples.

Make sure to download at least the SDK for Android Lollipop (or greater), the examples require the API level 21 at least and NDK 14.

You may download the Android NDK from [the official](https://developer.android.com/ndk/downloads/index.html) website, let's assume you've downloaded 16b (the latest stable version as of 2018-04-28) and you unpack it to `~/android_ndk`

We'll also need to have the [Android SDK](https://developer.android.com/studio/#downloads) installed, for example, in `~/android_sdk` and set the `ANDROID_HOME` environment variable pointing to it.

Although the `ros2_java_android.repos` file contains all the repositories for the Android bindings to compile, we'll have to disable certain packages (`python_cmake_module`, `rosidl_generator_py`, `test_msgs`) that are included the repositories and that we either don't need or can't cross-compile properly (e.g. the Python generator)

1. Download the [Android NDK](https://developer.android.com/ndk/downloads/index.html) and set the environment variable `ANDROID_NDK` to the path where it is extracted.

1. Download the [Android SDK](https://developer.android.com/studio/#downloads) and set the environment variable `ANDROID_HOME` to the path where it is extracted.

1. Clone ROS 2 and ROS 2 Java source code:

        mkdir -p $HOME/ros2_android_ws/src
        cd $HOME/ros2_android_ws
        curl https://raw.githubusercontent.com/ros2-java/ros2_java/main/ros2_java_android.repos | vcs import src

1. Set Android build configuration:

        export PYTHON3_EXEC="$( which python3 )"
        export PYTHON3_LIBRARY="$( ${PYTHON3_EXEC} -c 'import os.path; from distutils import sysconfig; print(os.path.realpath(os.path.join(sysconfig.get_config_var("LIBPL"), sysconfig.get_config_var("LDLIBRARY"))))' )"
        export PYTHON3_INCLUDE_DIR="$( ${PYTHON3_EXEC} -c 'from distutils import sysconfig; print(sysconfig.get_config_var("INCLUDEPY"))' )"
        export ANDROID_ABI=armeabi-v7a
        export ANDROID_NATIVE_API_LEVEL=android-21
        export ANDROID_TOOLCHAIN_NAME=arm-linux-androideabi-clang

1. Build (skipping packages that we don't need or can't cross-compile):

        colcon build \
          --packages-ignore cyclonedds rcl_logging_log4cxx rosidl_generator_py \
          --packages-up-to rcljava \
          --cmake-args \
          -DPYTHON_EXECUTABLE=${PYTHON3_EXEC} \
          -DPYTHON_LIBRARY=${PYTHON3_LIBRARY} \
          -DPYTHON_INCLUDE_DIR=${PYTHON3_INCLUDE_DIR} \
          -DCMAKE_TOOLCHAIN_FILE=${ANDROID_NDK}/build/cmake/android.toolchain.cmake \
          -DANDROID_FUNCTION_LEVEL_LINKING=OFF \
          -DANDROID_NATIVE_API_LEVEL=${ANDROID_NATIVE_API_LEVEL} \
          -DANDROID_TOOLCHAIN_NAME=${ANDROID_TOOLCHAIN_NAME} \
          -DANDROID_STL=c++_shared \
          -DANDROID_ABI=${ANDROID_ABI} \
          -DANDROID_NDK=${ANDROID_NDK} \
          -DTHIRDPARTY=ON \
          -DCOMPILE_EXAMPLES=OFF \
          -DCMAKE_FIND_ROOT_PATH="${PWD}/install"

You can find more information about the Android examples at https://github.com/ros2-java/ros2_android_examples

## Contributing

Contributions are more than welcome!
If you'd like to contribute to the project, please read [CONTRIBUTING](CONTRIBUTING.md) for contributing guidelines.
