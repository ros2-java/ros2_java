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

package org.ros2.rcljava.node;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.ros2.rcljava.client.Client;
import org.ros2.rcljava.concurrent.Callback;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.consumers.TriConsumer;
import org.ros2.rcljava.qos.QoSProfile;
import org.ros2.rcljava.interfaces.Disposable;
import org.ros2.rcljava.interfaces.MessageDefinition;
import org.ros2.rcljava.interfaces.ServiceDefinition;
import org.ros2.rcljava.parameters.ParameterType;
import org.ros2.rcljava.parameters.ParameterVariant;
import org.ros2.rcljava.publisher.Publisher;
import org.ros2.rcljava.service.RMWRequestId;
import org.ros2.rcljava.service.Service;
import org.ros2.rcljava.subscription.Subscription;
import org.ros2.rcljava.time.Clock;
import org.ros2.rcljava.timer.Timer;
import org.ros2.rcljava.timer.WallTimer;

/**
 * This class serves as a bridge between ROS2's rcl_node_t and RCLJava.
 * A Node must be created via @{link RCLJava#createNode(String)}
 */
public interface Node extends Disposable {
  /**
   * @return The nodes @{link Clock}.
   */
  Clock getClock();

  /**
   * return All the @{link Client}s that have been created by this instance.
   */
  Collection<Client> getClients();

  /**
   * return All the @{link Service}s that have been created by this instance.
   */
  Collection<Service> getServices();

  /**
   * @return All the @{link Subscription}s that were created by this instance.
   */
  Collection<Subscription> getSubscriptions();

  /**
   * @return All the @{link Publisher}s that were created by this instance.
   */
  Collection<Publisher> getPublishers();

  /**
   * @return All the @{link Timer}s that were created by this instance.
   */
  Collection<Timer> getTimers();

  /**
   * Create a Subscription&lt;T&gt;.
   *
   * @param <T> The type of the messages that will be received by the
   *     created @{link Subscription}.
   * @param messageType The class of the messages that will be received by the
   *     created @{link Subscription}.
   * @param topic The topic from which the created @{link Subscription} will
   *     receive messages.
   * @param callback The callback function that will be triggered when a
   *     message is received by the @{link Subscription}.
   * @return A @{link Subscription} that represents the underlying ROS2
   *     subscription structure.
   */
  <T extends MessageDefinition> Subscription<T> createSubscription(final Class<T> messageType,
      final String topic, final Consumer<T> callback, final QoSProfile qosProfile);

  <T extends MessageDefinition> Subscription<T> createSubscription(
      final Class<T> messageType, final String topic, final Consumer<T> callback);

  /**
   * Create a Publisher&lt;T&gt;.
   *
   * @param <T> The type of the messages that will be published by the
   *     created @{link Publisher}.
   * @param messageType The class of the messages that will be published by the
   *     created @{link Publisher}.
   * @param topic The topic to which the created @{link Publisher} will
   *     publish messages.
   * @return A @{link Publisher} that represents the underlying ROS2 publisher
   *     structure.
   */
  <T extends MessageDefinition> Publisher<T> createPublisher(
      final Class<T> messageType, final String topic, final QoSProfile qosProfile);

  <T extends MessageDefinition> Publisher<T> createPublisher(
      final Class<T> messageType, final String topic);

  <T extends ServiceDefinition> Service<T> createService(final Class<T> serviceType,
      final String serviceName,
      final TriConsumer<RMWRequestId, ? extends MessageDefinition, ? extends MessageDefinition>
          callback,
      final QoSProfile qosProfile) throws NoSuchFieldException, IllegalAccessException;

  <T extends ServiceDefinition> Service<T> createService(final Class<T> serviceType,
      final String serviceName,
      final TriConsumer<RMWRequestId, ? extends MessageDefinition, ? extends MessageDefinition>
          callback) throws NoSuchFieldException, IllegalAccessException;

  <T extends ServiceDefinition> Client<T> createClient(
      final Class<T> serviceType, final String serviceName, final QoSProfile qosProfile)
      throws NoSuchFieldException, IllegalAccessException;

  <T extends ServiceDefinition> Client<T> createClient(final Class<T> serviceType,
      final String serviceName) throws NoSuchFieldException, IllegalAccessException;

  /**
   * Remove a Subscription created by this Node.
   *
   * Calling this method effectively invalidates the passed @{link Subscription}.
   * If the subscription was not created by this Node, then nothing happens.
   *
   * @param subscription The object to remove from this node.
   * @return true if the subscription was removed, false if the subscription was already
   *   removed or was never created by this Node.
   */
  boolean removeSubscription(final Subscription subscription);

  /**
   * Remove a Publisher created by this Node.
   *
   * Calling this method effectively invalidates the passed @{link Publisher}.
   * If the publisher was not created by this Node, then nothing happens.
   *
   * @param publisher The object to remove from this node.
   * @return true if the publisher was removed, false if the publisher was already
   *   removed or was never created by this Node.
   */
  boolean removePublisher(final Publisher publisher);

  /**
   * Remove a Service created by this Node.
   *
   * Calling this method effectively invalidates the passed @{link Service}.
   * If the service was not created by this Node, then nothing happens.
   *
   * @param service The object to remove from this node.
   * @return true if the service was removed, false if the service was already
   *   removed or was never created by this Node.
   */
  boolean removeService(final Service service);

  /**
   * Remove a Client created by this Node.
   *
   * Calling this method effectively invalidates the passed @{link Client}.
   * If the client was not created by this Node, then nothing happens.
   *
   * @param client The object to remove from this node.
   * @return true if the client was removed, false if the client was already
   *   removed or was never created by this Node.
   */
  boolean removeClient(final Client client);


  WallTimer createWallTimer(final long period, final TimeUnit unit, final Callback callback);

  String getName();

  List<ParameterVariant> getParameters(List<String> names);

  List<ParameterType> getParameterTypes(List<String> names);

  List<rcl_interfaces.msg.SetParametersResult> setParameters(List<ParameterVariant> parameters);

  rcl_interfaces.msg.SetParametersResult setParametersAtomically(List<ParameterVariant> parameters);

  List<rcl_interfaces.msg.ParameterDescriptor> describeParameters(List<String> names);

  rcl_interfaces.msg.ListParametersResult listParameters(List<String> prefixes, long depth);
}
