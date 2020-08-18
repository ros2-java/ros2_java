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

package org.ros2.rcljava.publisher;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.interfaces.Disposable;
import org.ros2.rcljava.interfaces.MessageDefinition;
import org.ros2.rcljava.events.EventHandler;
import org.ros2.rcljava.events.PublisherEventStatus;
import org.ros2.rcljava.node.Node;

/**
 * This class serves as a bridge between ROS2's rcl_publisher_t and RCLJava.
 * A Publisher must be created via
 * @{link Node#createPublisher(Class&lt;T&gt;, String)}
 *
 * @param <T> The type of the messages that this publisher will publish.
 */
public interface Publisher<T extends MessageDefinition> extends Disposable {
  /**
   * Publish a message.
   *
   * @param message An instance of the &lt;T&gt; parameter.
   */
  void publish(final T message);

  /**
   * A @{link java.lang.ref.WeakReference} to the @{link org.ros2.rcljava.Node}
   * that created this publisher.
   */
  WeakReference<Node> getNodeReference();

  /**
   * Create an event handler.
   *
   * @param <T> A publisher event status type.
   * @param factory A factory that can instantiate an event status of type T.
   * @param callback Callback that will be called when the event is triggered.
   */
  <T extends PublisherEventStatus> EventHandler<T, Publisher> createEventHandler(
    Supplier<T> factory, Consumer<T> callback);

  /**
   * Remove a previously registered event handler.
   *
   * @param <T> A publisher event status type.
   * @param eventHandler An event handler that was registered previously in this object.
   */
  <T extends PublisherEventStatus> void removeEventHandler(
    EventHandler<T, Publisher> eventHandler);
}
