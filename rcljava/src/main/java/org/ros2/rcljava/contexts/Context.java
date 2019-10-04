/* Copyright 2019 Open Source Robotics Foundation, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ros2.rcljava.contexts;

import org.ros2.rcljava.interfaces.Disposable;

/**
 * Encapsulates the non-global state of a ROS init/shutdown cycle.
 *
 * Generally, after a context is created it is initialized with @{link #init()}
 * and before it is disposed @{link #shutdown()} is called.
 *
 * For more information on the lifecylce of a context, see the RCL documentation:
 * http://docs.ros2.org/dashing/api/rcl/context_8h.html#ae051a6821ad89dc78910a557029c8ae2
 *
 * This class serves as a bridge between a rcl_context_t and RCLJava.
 * A Context must be created via @{link RCLJava#createContext()}
 */
public interface Context extends Disposable {
  /**
   * Initialize the context.
   * // TODO(jacobperron): Pass arguments for parsing
   * // TODO(jacobperron): Pass in InitOptions object
   */
  void init();

  /**
   * Shutdown the context.
   */
  void shutdown();

  /**
   * return true if the Context is valid, false otherwise.
   */
  boolean isValid();
}
