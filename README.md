ROS2 for Java
=============

Build status
------------

| Target                                    | Status        |
|-------------------------------------------|---------------|
| **ROS Dashing - Ubuntu Bionic (OpenJDK)** | ![Build Status](https://github.com/ros2-java/ros2_java/workflows/CI/badge.svg?branch=dashing) |

Introduction
------------

This is a set of projects (bindings, code generator, examples and more) that enables developers to write ROS2
applications for the JVM and Android.

Besides this repository itself, there's also:
- https://github.com/esteve/ament_java, which adds support for Gradle to Ament
- https://github.com/esteve/ament_gradle_plugin, a Gradle plugin that makes it easier to use ROS2 in Java and Android project. The Gradle plugin can be installed from Gradle Central https://plugins.gradle.org/plugin/org.ros2.tools.gradle
- https://github.com/esteve/ros2_java_examples, examples for the Java Runtime Environment
- https://github.com/esteve/ros2_android_examples, examples for Android

Is this Java only?
------------------

No, any language that targets the JVM can be used to write ROS2 applications.

Including Android?
------------------

Yep! Make sure to use Fast-RTPS as your DDS vendor and at least [this revision](https://github.com/eProsima/Fast-RTPS/commit/5301ef203d45528a083821c3ba582164d782360b).

Features
--------

The current set of features include:
- Generation of all builtin and complex ROS types, including arrays, strings, nested types, constants, etc.
- Support for publishers and subscriptions
- Tunable Quality of Service (e.g. lossy networks, reliable delivery, etc.)
- Clients and services
- Timers
- Composition (i.e. more than one node per process)
- Time handling (system and steady, ROS time not yet supported https://github.com/ros2/ros2/issues/350)
- Support for Android
- Parameters services and clients (both asynchronous and synchronous)

Sounds great, how can I try this out?
-------------------------------------

> Note: While the following instructions use a Linux shell the same can be done on other platforms like Windows with slightly adjusted commands.
> 
> For Windows and Mac, first follow the steps for installing prerequisites on the binary installation page: https://github.com/ros2/ros2/wiki/Installation
>
> Stop and return here when you reach the "Downloading ROS 2" section.

Download the ament repositories in a separate workspace:

```
mkdir -p ament_ws/src
cd ament_ws
curl -skL https://raw.githubusercontent.com/esteve/ament_java/master/ament_java.repos -o ament_java.repos
vcs import src < ament_java.repos
src/ament/ament_tools/scripts/ament.py build --symlink-install --isolated
```

> Note: On Windows, use `python src/ament/ament_tools/scripts/ament.py build`, as `*.py` scripts must be prefixed with `python`, `--symlink-install` is not supported due to a bug in Python symlinks, and `--isolated` creates paths that are too long.
> Additionally, you may need to call `call "C:\Program Files (x86)\Microsoft Visual Studio\2017\Community\VC\Auxiliary\Build\vcvars64.bat"` if you have not run from a VS 2017 terminal.

We need to split the build process between Ament and the rest of `ros2_java` workspace so that the additional build type for Gradle projects is picked up by Ament.

Make sure you have Gradle 3.2 (or later) installed. Ubuntu 16.04 ships with Gradle 2.10, you can install a more recent version of Gradle from the following PPA:

```
$ sudo add-apt-repository ppa:cwchien/gradle
$ sudo apt install -y gradle
```
For OSX, use homebrew command to install gradle:

```
$ brew install gradle
```

For OSX users, there some compatibily issues have been reported between gradle 3.x and Java 9. Currently only the combination of Gradle 3.x + Java 8 has been tested successfully. Make sure to use install and use Java 8 for the building process:

```
$ brew tap caskroom/versions
$ brew cask install java8
$ export JAVA_HOME=/Library/Java/JavaVirtualMachines/1.8.0.jdk/Contents/Home
``` 

> Note: On Windows, you may use `choco install -y gradle`

The following sections deal with building the `ros2_java` codebase for the desktop Java runtime and for Android.

Desktop
-------

```
mkdir -p ros2_java_ws/src
cd ros2_java_ws
curl -skL https://raw.githubusercontent.com/ros2-java/ros2_java/dashing/ros2_java_desktop.repos -o ros2_java_desktop.repos
vcs import src < ros2_java_desktop.repos
. ../ament_ws/install_isolated/local_setup.sh
ament build --symlink-install --isolated
```

> On Windows, if you would like to use OpenSplice, call `call "C:\opensplice67\HDE\x86_64.win64\release.bat"` before building.

Now you can just run a bunch of examples, head over to https://github.com/esteve/ros2_java_examples for more information.

Android
-------

The Android setup is slightly more complex, you'll need the SDK and NDK installed, and an Android device where you can run the examples.

Make sure to download at least the SDK for Android Lollipop (or greater), the examples require the API level 21 at least and NDK 14.

You may download the Android NDK from [the official](https://developer.android.com/ndk/downloads/index.html) website, let's assume you've downloaded 16b (the latest stable version as of 2018-04-28) and you unpack it to `~/android_ndk`

We'll also need to have the [Android SDK](https://developer.android.com/studio/#downloads) installed, for example, in `~/android_sdk` and set the `ANDROID_HOME` environment variable pointing to it.

Although the `ros2_java_android.repos` file contains all the repositories for the Android bindings to compile, we'll have to disable certain packages (`python_cmake_module`, `rosidl_generator_py`, `test_msgs`) that are included the repositories and that we either don't need or can't cross-compile properly (e.g. the Python generator)

```
# define paths
ROOT_DIR = ${HOME}
AMENT_WORKSPACE=${ROOT_DIR}/ament_ws
ROS2_ANDROID_WORKSPACE=${ROOT_DIR}/ros2_android_ws

# pull and build ament
mkdir -p ${AMENT_WORKSPACE}/src
cd ${AMENT_WORKSPACE}
curl https://raw.githubusercontent.com/esteve/ament_java/master/ament_java.repos
vcs import ${AMENT_WORKSPACE}/src < ament_java.repos
src/ament/ament_tools/scripts/ament.py build --symlink-install --isolated

# android build configuration
export PYTHON3_EXEC="$( which python3 )"
export ANDROID_ABI=armeabi-v7a
export ANDROID_NATIVE_API_LEVEL=android-21
export ANDROID_TOOLCHAIN_NAME=arm-linux-androideabi-clang

# pull and build ros2 for android
mkdir -p ${ROS2_ANDROID_WORKSPACE}/src
cd ${ROS2_ANDROID_WORKSPACE}
curl https://raw.githubusercontent.com/ros2-java/ros2_java/dashing/ros2_java_android.repos
vcs import ${ROS2_ANDROID_WORKSPACE}/src < ros2_java_android.repos
source ${AMENT_WORKSPACE}/install_isolated/local_setup.sh
ament build --isolated --skip-packages test_msgs \
  --cmake-args \
  -DPYTHON_EXECUTABLE=${PYTHON3_EXEC} \
  -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake \
  -DANDROID_FUNCTION_LEVEL_LINKING=OFF \
  -DANDROID_NATIVE_API_LEVEL=${ANDROID_NATIVE_API_LEVEL} \
  -DANDROID_TOOLCHAIN_NAME=${ANDROID_TOOLCHAIN_NAME} \
  -DANDROID_STL=gnustl_shared \
  -DANDROID_ABI=${ANDROID_ABI} \
  -DANDROID_NDK=${ANDROID_NDK} \
  -DTHIRDPARTY=ON \
  -DCOMPILE_EXAMPLES=OFF \
  -DCMAKE_FIND_ROOT_PATH="$AMENT_WORKSPACE/install_isolated;$ROS2_ANDROID_WORKSPACE/install_isolated" \
  -- \
  --parallel \
  --ament-gradle-args \
  -Pament.android_stl=gnustl_shared -Pament.android_abi=$ANDROID_ABI -Pament.android_ndk=$ANDROID_NDK --
```

You can find more information about the Android examples at https://github.com/esteve/ros2_android_examples
