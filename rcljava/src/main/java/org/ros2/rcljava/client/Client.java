/* Copyright 2016-2018 Esteve Fernandez <esteve@apache.org>
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

package org.ros2.rcljava.client;

import java.time.Duration;
import java.util.concurrent.Future;

import org.ros2.rcljava.concurrent.RCLFuture;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.interfaces.Disposable;
import org.ros2.rcljava.interfaces.MessageDefinition;
import org.ros2.rcljava.interfaces.ServiceDefinition;
import org.ros2.rcljava.service.RMWRequestId;

public interface Client<T extends ServiceDefinition> extends Disposable {
  <U extends MessageDefinition> Class<U> getRequestType();

  <U extends MessageDefinition> Class<U> getResponseType();

  <U extends MessageDefinition> void handleResponse(RMWRequestId header, U response);

  <U extends MessageDefinition, V extends MessageDefinition> Future<V> asyncSendRequest(
      final U request);

  <U extends MessageDefinition, V extends MessageDefinition> Future<V> asyncSendRequest(
      final U request, final Consumer<Future<V>> callback);

  /**
   * Check if the service server is available.
   *
   * @return true if the client can talk to the service, false otherwise.
   */
  boolean isServiceAvailable();

  /**
   * Wait for the service server to be available.
   *
   * Blocks until the service is available or the ROS context is invalidated.
   *
   * @return true if the service is available, false if the ROS context was shutdown.
   */
  boolean waitForService();

  /**
   * Wait for the service server to be available.
   *
   * Blocks until the service is available or a timeout occurs.
   * Also returns if the ROS context is invalidated.
   *
   * @param timeout Time to wait for the service to be available.
   *   A zero value causes this method to check if the service is available and return immediately.
   *   A negative value is treated as an infinite timeout.
   * @return true if the service is available, false otherwise.
   */
  boolean waitForService(Duration timeout);

  String getServiceName();
}
