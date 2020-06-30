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

package org.ros2.rcljava.subscription;

import java.lang.ref.WeakReference;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.interfaces.MessageDefinition;
import org.ros2.rcljava.node.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class serves as a bridge between ROS2's rcl_subscription_t and RCLJava.
 * A Subscription must be created via
 * @{link Node#createSubscription(Class&lt;T&gt;, String, Consumer&lt;T&gt;)}
 *
 * @param <T> The type of the messages that this subscription will receive.
 */
public class SubscriptionImpl<T extends MessageDefinition> implements Subscription<T> {
  private static final Logger logger = LoggerFactory.getLogger(SubscriptionImpl.class);

  static {
    try {
      JNIUtils.loadImplementation(SubscriptionImpl.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  private final WeakReference<Node> nodeReference;

  /**
   * @{inheritDoc}
   */
  private long handle;

  /**
   * The class of the messages that this subscription may receive.
   */
  private final Class<T> messageType;

  /**
   * The topic to which this subscription is subscribed.
   */
  private final String topic;

  /**
   * The callback function that will be triggered when a new message is
   * received.
   */
  private final Consumer<T> callback;

  /**
   * Constructor.
   *
   * @param nodeReference A {@link java.lang.ref.WeakReference} to the
   *     @{link org.ros2.rcljava.Node} that created this subscription.
   * @param handle A pointer to the underlying ROS2 subscription
   *     structure, as an integer. Must not be zero.
   * @param messageType The <code>Class</code> of the messages that this
   *     subscription will receive. We need this because of Java's type erasure,
   *     which doesn't allow us to use the generic parameter of
   *     @{link org.ros2.rcljava.Subscription} directly.
   * @param topic The topic to which this subscription will be subscribed.
   * @param callback The callback function that will be triggered when a new
   *     message is received.
   */
  public SubscriptionImpl(final WeakReference<Node> nodeReference, final long handle,
      final Class<T> messageType, final String topic, final Consumer<T> callback) {
    this.nodeReference = nodeReference;
    this.handle = handle;
    this.messageType = messageType;
    this.topic = topic;
    this.callback = callback;
  }

  /**
   * {@inheritDoc}
   */
  public final Class<T> getMessageType() {
    return messageType;
  }

  /**
   * {@inheritDoc}
   */
  public final long getHandle() {
    return handle;
  }

  /**
   * {@inheritDoc}
   */
  public final WeakReference<Node> getNodeReference() {
    return this.nodeReference;
  }

  /**
   * Destroy a ROS2 subscription (rcl_subscription_t).
   *
   * @param nodeHandle A pointer to the underlying ROS2 node structure that
   *     created this subscription, as an integer. Must not be zero.
   * @param handle A pointer to the underlying ROS2 subscription
   *     structure, as an integer. Must not be zero.
   */
  private static native void nativeDispose(long nodeHandle, long handle);

  /**
   * {@inheritDoc}
   */
  public final void dispose() {
    Node node = this.nodeReference.get();
    if (node != null) {
      node.removeSubscription(this);
      nativeDispose(node.getHandle(), this.handle);
      this.handle = 0;
    }
  }

  public void executeCallback(T message) {
    this.callback.accept(message);
  }
}
