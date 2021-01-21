/* Copyright 2020 ros2-java contributors
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

package org.ros2.rcljava.action;

import java.util.Collection;

import org.ros2.rcljava.interfaces.Disposable;
import org.ros2.rcljava.interfaces.ActionDefinition;

public interface ActionServer<T extends ActionDefinition> extends Disposable {
  // TODO(jacobperron): Move most of these methods to a new "Waitable" interface
  /**
   * Get the number of underlying subscriptions that the action server uses.
   *
   * @return The number of subscriptions.
   */
  int getNumberOfSubscriptions();

  /**
   * Get the number of underlying timers that the action server uses.
   *
   * @return The number of timers.
   */
  int getNumberOfTimers();

  /**
   * Get the number of underlying clients that the action server uses.
   *
   * @return The number of clients.
   */
  int getNumberOfClients();

  /**
   * Get the number of underlying services that the action server uses.
   *
   * @return The number of services.
   */
  int getNumberOfServices();

  /**
   * Check if an entity of the action server is ready in the wait set.
   *
   * @param waitSetHandle Handle to the rcl wait set that this action server was added to.
   *
   * @return true if at least one entity is ready, false otherwise.
   */
  boolean isReady(long waitSetHandle);

  /**
   * Execute any entities that are ready in the underlying wait set.
   */
  void execute();
}
