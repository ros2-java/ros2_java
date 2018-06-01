ROS2 for Java
=============

Windows Tutorial
------------

Sounds great, how can I try this out?
-------------------------------------

First, make sure you have followed the [Windows Development Setup](https://github.com/ros2/ros2/wiki/Windows-Development-Setup) guide.

Download the ament repositories in a separate workspace:

```
> md \dev\ament_ws\src
> cd \dev\ament_ws
> curl https://raw.githubusercontent.com/esteve/ament_java/master/ament_java.repos -o ament_java.repos
> vcs import src < ament_java.repos
> python src\ament\ament_tools\scripts\ament.py build --isolated
```

We need to split the build process between Ament and the rest of `ros2_java` workspace so that the additional build type for Gradle projects is picked up by Ament.

Make sure you have Gradle 3.2 (or later) installed. Ubuntu 16.04 ships with Gradle 2.10, you can install a more recent version of Gradle with chocolatey:

```
> choco install -y gradle
```

The following sections deal with building the `ros2_java` codebase for the desktop Java runtime and for Android.

Desktop
-------

```
> md \dev\ros2_java_ws\src
> cd \dev\ros2_java_ws
> curl https://raw.githubusercontent.com/esteve/ros2_java/master/ros2_java_desktop.repos -o ros2_java_desktop.repos
> vcs import src < ros2_java_desktop.repos
> cd src\ros2\rosidl_typesupport
> patch -p1 < ..\..\ros2_java\ros2_java\rosidl_typesupport_ros2_java.patch
> cd \dev\ros2_java_ws
> call ..\ament_ws\install_isolated\local_setup.bat
> ament build --symlink-install --isolated
```

Now you can just run a bunch of examples, head over to https://github.com/esteve/ros2_java_examples for more information.
