/* Copyright 2016 Esteve Fernandez <esteve@apache.org>
 * Copyright 2016 Mickael Gaillard <mick.gaillard@gmail.com>
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
package org.ros2.rcljava.node.topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ros2.rcljava.qos.QoSProfile;
import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.internal.IPublisher;
import org.ros2.rcljava.node.Node;

/**
 * This class serves as a bridge between ROS2's rcl_publisher_t and RCLJava.
 * A Publisher must be created via
 * @{link Node#createPublisher(Class&lt;T&gt;, String)}
 *
 * @param <T> The type of the messages that this publisher will publish.
 */
public class Publisher<T extends org.ros2.rcljava.internal.message.Message> implements IPublisher<T> {

    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);

    static {
        RCLJava.loadLibrary("rcljavanode_topic_Publisher__" + RCLJava.getRMWIdentifier());
    }

    /**
     * Node owner.
     */
    private final Node ownerNode;

    /**
     * An integer that represents a pointer to the underlying ROS2 publisher
     * structure (rcl_publisher_t).
     */
    private final long publisherHandle;

    /** Message Type. */
    private final Class<T> messageType;

    /**
     * The topic to which this publisher will publish messages.
     */
    private final String topic;

    /** Quality of Service profil. */
    private final QoSProfile qosProfile;

    // Native call.
    /**
     * Publish a message via the underlying ROS2 mechanisms.
     *
     * @param <T> The type of the messages that this publisher will publish.
     * @param publisherHandle A pointer to the underlying ROS2 publisher
     *     structure, as an integer. Must not be zero.
     * @param message An instance of the &lt;T&gt; parameter.
     */
    private static native <T extends org.ros2.rcljava.internal.message.Message> void nativePublish(long publisherHandle, T message);

    /**
     * Destroy a ROS2 publisher (rcl_publisher_t).
     *
     * @param nodeHandle A pointer to the underlying ROS2 node structure that
     *     created this subscription, as an integer. Must not be zero.
     * @param publisherHandle A pointer to the underlying ROS2 publisher
     *     structure, as an integer. Must not be zero.
     */
    private static native void nativeDispose(long nodeHandle, long publisherHandle);

    /**
     * Constructor.
     *
     * @param nodeHandle A pointer to the underlying ROS2 node structure that
     *     created this subscription, as an integer. Must not be zero.
     * @param publisherHandle A pointer to the underlying ROS2 publisher
     *     structure, as an integer. Must not be zero.
     * @param topic The topic to which this publisher will publish messages.
     * @param qos Quality of Service profile.
     */
    public Publisher(final Node node, final long publisherHandle, final Class<T> messageType, final String topic, final QoSProfile qosProfile) {
        if (node == null && publisherHandle == 0) {
            throw new RuntimeException("Need to provide active node with handle object");
        }

        this.ownerNode = node;
        this.publisherHandle = publisherHandle;
        this.messageType = messageType;
        this.topic = topic;
        this.qosProfile = qosProfile;

        this.ownerNode.getPublishers().add(this);
    }

    /**
     * Publish a message.
     *
     * @param message An instance of the &lt;T&gt; parameter.
     */
    @Override
    public void publish(final T message) {
        Publisher.nativePublish(this.publisherHandle, message);
    }

    /**
     * Get message type.
     * @return
     */
    public final Class<T> getMsgType() {
        return this.messageType;
    }

    /**
     * Get topic name.
     * @return
     */
    public final String getTopic() {
        return this.topic;
    }

    public final Node getNode() {
        return this.ownerNode;
    }

    public final long getPublisherHandle() {
        return this.publisherHandle;
    }

    /**
     * Safely destroy the underlying ROS2 publisher structure.
     */
    @Override
    public void dispose() {
        Publisher.logger.debug("Destroy Publisher of topic : " + this.topic);
        this.ownerNode.getPublishers().remove(this);
        Publisher.nativeDispose(this.ownerNode.getNodeHandle(), this.publisherHandle);
    }
}
