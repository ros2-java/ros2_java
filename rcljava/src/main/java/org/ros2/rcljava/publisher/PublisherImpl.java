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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.interfaces.MessageDefinition;
import org.ros2.rcljava.node.Node;

/**
 * {@inheritDoc}
 */
public class PublisherImpl<T extends MessageDefinition> implements Publisher<T> {
  private static final Logger logger = LoggerFactory.getLogger(PublisherImpl.class);

  static {
    try {
      JNIUtils.loadImplementation(PublisherImpl.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  private final WeakReference<Node> nodeReference;

  /**
   * An integer that represents a pointer to the underlying ROS2 publisher
   * structure (rcl_publisher_t).
   */
  private long handle;

  /**
   * The topic to which this publisher will publish messages.
   */
  private final String topic;

  /**
   * Constructor.
   *
   * @param nodeReference A {@link java.lang.ref.WeakReference} to the
   *     @{link org.ros2.rcljava.Node} that created this publisher.
   * @param handle A pointer to the underlying ROS2 publisher
   *     structure, as an integer. Must not be zero.
   * @param topic The topic to which this publisher will publish messages.
   */
  public PublisherImpl(
      final WeakReference<Node> nodeReference, final long handle, final String topic) {
    this.nodeReference = nodeReference;
    this.handle = handle;
    this.topic = topic;
  }

  /**
   * Publish a message via the underlying ROS2 mechanisms.
   *
   * @param <T> The type of the messages that this publisher will publish.
   * @param handle A pointer to the underlying ROS2 publisher
   *     structure, as an integer. Must not be zero.
   * @param message An instance of the &lt;T&gt; parameter.
   */
  private static native <T extends MessageDefinition> void nativePublish(
      long handle, long messageDestructor, T message);

  /**
   * {@inheritDoc}
   */
  public final void publish(final T message) {
    nativePublish(this.handle, message.getDestructorInstance(), message);
  }

  /**
   * {@inheritDoc}
   */
  public final long getHandle() {
    return this.handle;
  }

  /**
   * {@inheritDoc}
   */
  public final WeakReference<Node> getNodeReference() {
    return this.nodeReference;
  }

  /**
   * Destroy a ROS2 publisher (rcl_publisher_t).
   *
   * @param nodeHandle A pointer to the underlying ROS2 node structure that
   *     created this subscription, as an integer. Must not be zero.
   * @param handle A pointer to the underlying ROS2 publisher
   *     structure, as an integer. Must not be zero.
   */
  private static native void nativeDispose(long nodeHandle, long handle);

  /**
   * {@inheritDoc}
   */
  public final void dispose() {
    Node node = this.nodeReference.get();
    if (node != null) {
      node.removePublisher(this);
      nativeDispose(node.getHandle(), this.handle);
      this.handle = 0;
    }
  }
}
