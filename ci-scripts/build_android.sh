#!/bin/sh
set -e

ANDROID_NDK_VERSION=android-ndk-r17b
# tools_r26.0.2
ANDROID_SDK_VERSION=3859397

ANDROID_TARGET=android-21
ANDROID_ABI=armeabi-v7a
ANDROID_NDK=/opt/android/${ANDROID_NDK_VERSION}/

ROS2_CURDIR=${PWD}
ROS2_JAVA_DIR=$(test -n "${TRAVIS}" && echo /home/travis/build || echo ${ROS2_CURDIR})
ROS2_OUTPUT_DIR=${ROS2_JAVA_DIR}/output
AMENT_WS=${ROS2_JAVA_DIR}/ament_ws
ROS2_ANDROID_WS=${ROS2_JAVA_DIR}/ros2_android_ws
AMENT_BUILD_DIR=${ROS2_OUTPUT_DIR}/build_isolated_ament
AMENT_INSTALL_DIR=${ROS2_OUTPUT_DIR}/install_isolated_ament
ROS2_ANDROID_BUILD_DIR=${ROS2_OUTPUT_DIR}/build_isolated_android
ROS2_ANDROID_INSTALL_DIR=${ROS2_OUTPUT_DIR}/install_isolated_android
#TOOLCHAIN_FILE=/opt/android/android-ndk-r13b/build/cmake/android.toolchain.cmake
ANDROID_NDK=/opt/android/${ANDROID_NDK_VERSION}
TOOLCHAIN_FILE=${ANDROID_NDK}/build/cmake/android.toolchain.cmake
ANDROID_STL=c++_shared
# ANDROID_STL=gnustl_shared
ANDROID_TARGET=android-21
ANDROID_ABI=armeabi-v7a

mkdir -p ${ROS2_JAVA_DIR}
mkdir -p ${AMENT_WS}/src
mkdir -p ${ROS2_ANDROID_WS}/src

if [ -n "${TRAVIS}" ]; then
  wget -O /tmp/android-ndk.zip https://dl.google.com/android/repository/${ANDROID_NDK_VERSION}-linux-x86_64.zip && mkdir -p /opt/android/ && cd /opt/android/ && unzip -q /tmp/android-ndk.zip && rm /tmp/android-ndk.zip
  wget -O /tmp/android-sdk.zip https://dl.google.com/android/repository/sdk-tools-linux-${ANDROID_SDK_VERSION}.zip && mkdir -p /opt/android/android-sdk-linux && cd /opt/android/android-sdk-linux && unzip -q /tmp/android-sdk.zip && rm /tmp/android-sdk.zip

# Accept licenses
  yes | ${ANDROID_SDK}/tools/bin/sdkmanager --licenses

# Install platform tools
  yes | ${ANDROID_SDK}/tools/bin/sdkmanager --verbose "platforms;${ANDROID_TARGET}"

# Disable emulator for now
# yes | ${ANDROID_SDK}/tools/bin/sdkmanager --verbose "emulator"
# yes | ${ANDROID_SDK}/tools/bin/sdkmanager --verbose "system-images;${ANDROID_TARGET};default;armeabi-v7a"
# yes | ${ANDROID_SDK}/tools/bin/sdkmanager --verbose "system-images;${ANDROID_TARGET};default;x86_64"
# echo no | ${ANDROID_SDK}/tools/bin/avdmanager create avd --force --name test --package "system-images;${ANDROID_TARGET};default;${ANDROID_ABI}" --abi ${ANDROID_ABI}
fi

if [ -z "$ROS2_JAVA_SKIP_FETCH" ]; then
  cd $ROS2_JAVA_DIR
  echo "branch: $ROS2_JAVA_BRANCH"

  cd $AMENT_WS
  wget https://raw.githubusercontent.com/esteve/ament_java/$ROS2_JAVA_BRANCH/ament_java.repos || wget https://raw.githubusercontent.com/esteve/ament_java/master/ament_java.repos
  vcs import $AMENT_WS/src < ament_java.repos
  cd src/ament_java
  vcs custom --git --args checkout $ROS2_JAVA_BRANCH || true
  cd $AMENT_WS
  vcs export --exact

  cd $ROS2_ANDROID_WS
  wget https://raw.githubusercontent.com/esteve/ros2_java/$ROS2_JAVA_BRANCH/ros2_java_android.repos || wget https://raw.githubusercontent.com/esteve/ros2_java/master/ros2_java_android.repos
  vcs import $ROS2_ANDROID_WS/src < ros2_java_android.repos || true
  cd src/ros2_java
  vcs custom --git --args checkout $ROS2_JAVA_BRANCH || true
  cd $ROS2_ANDROID_WS
  vcs export --exact

  cd $ROS2_ANDROID_WS/src/ros2/rosidl
  test -e $ROS2_ANDROID_WS/src/ros2/rmw_fastrtps/rmw_fastrtps_c/package.xml && touch rosidl_generator_cpp/AMENT_IGNORE
  test -e $ROS2_ANDROID_WS/src/ros2/rmw_fastrtps/rmw_fastrtps_c/package.xml && touch rosidl_typesupport_introspection_cpp/AMENT_IGNORE
fi

cd $ROS2_ANDROID_WS/src/ros2/rmw_fastrtps
test -e $ROS2_ANDROID_WS/src/ros2/rmw_fastrtps/rmw_fastrtps_c/package.xml && touch rmw_fastrtps_cpp/AMENT_IGNORE

cd $ROS2_ANDROID_WS/src/ros2/rosidl_typesupport
test -e $ROS2_ANDROID_WS/src/ros2/rmw_fastrtps/rmw_fastrtps_c/package.xml && touch rosidl_typesupport_cpp/AMENT_IGNORE

if [ -z "$ROS2_JAVA_SKIP_AMENT" ]; then
  cd $AMENT_WS
  $AMENT_WS/src/ament/ament_tools/scripts/ament.py build --parallel --symlink-install --isolated --install-space $AMENT_INSTALL_DIR --build-space $AMENT_BUILD_DIR
fi

. $AMENT_INSTALL_DIR/local_setup.sh

cd $ROS2_ANDROID_WS
ament build --skip-packages test_msgs \
  --parallel --symlink-install --isolated --install-space $ROS2_ANDROID_INSTALL_DIR --build-space $ROS2_ANDROID_BUILD_DIR --cmake-args \
  -DTHIRDPARTY=ON \
  -DPYTHON_EXECUTABLE=/usr/bin/python3 -DCMAKE_TOOLCHAIN_FILE=$TOOLCHAIN_FILE \
  -DANDROID_FUNCTION_LEVEL_LINKING=OFF -DANDROID_NATIVE_API_LEVEL=$ANDROID_TARGET -DANDROID_TOOLCHAIN_NAME=arm-linux-androideabi-clang -DANDROID_STL=$ANDROID_STL \
  -DANDROID_ABI=$ANDROID_ABI -DANDROID_NDK=$ANDROID_NDK -DTHIRDPARTY=ON -DCOMPILE_EXAMPLES=OFF -DCMAKE_FIND_ROOT_PATH="$AMENT_INSTALL_DIR;$ROS2_ANDROID_INSTALL_DIR" \
  -- \
  --ament-gradle-args \
  -Pament.android_stl=$ANDROID_STL -Pament.android_abi=$ANDROID_ABI -Pament.android_ndk=$ANDROID_NDK -- $@

cd $ROS2_CURDIR
