// Copyright 2020 Open Source Robotics Foundation, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.ros2.rcljava.events;

import org.ros2.rcljava.interfaces.Disposable;

/**
 * This class serves as a bridge between ROS2's rcl event status types and RCLJava.
 */
public interface EventStatus {
  /**
   * Allocates an rcl event status.
   *
   * @return A pointer to the allocated status.
   */
  long allocateRCLStatusEvent();

  /**
   * Deallocates an rcl event status.
   *
   * @param handle Pointer to a previously allocated event status.
   */
  void deallocateRCLStatusEvent(long handle);

  /**
   * Loads the event with the data of the rcl event status.
   *
   * @param handle A pointer to the underlying event status.
   */
  void fromRCLEvent(long handle);
}
