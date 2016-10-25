/* Copyright 2016 Esteve Fernandez <esteve@apache.org>
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
import org.ros2.rcljava.internal.message.Message;

/**
 * This class serves as a bridge between ROS2's rcl_publisher_t and RCLJava.
 * A Publisher must be created via
 * @{link Node#createPublisher(Class&lt;T&gt;, String)}
 *
 * @param <T> The type of the messages that this publisher will publish.
 * @author Esteve Fernandez <esteve@apache.org>
 * @author Mickael Gaillard <mick.gaillard@gmail.com>
 */
public class Publisher<T extends Message> {

    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);

    /**
     * An integer that represents a pointer to the underlying ROS2 node
     * structure (rcl_node_t).
     */
    private final long nodeHandle;

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
    private static native <T extends Message> void nativePublish(long publisherHandle, T message);

    /**
     * Destroy a ROS2 publisher (rcl_publisher_t).
     *
     * @param nodeHandle A pointer to the underlying ROS2 node structure that
     *     created this subscription, as an integer. Must not be zero.
     * @param publisherHandle A pointer to the underlying ROS2 publisher
     *     structure, as an integer. Must not be zero.
     */
    private static native void nativeDispose(long nodeHandle, long publisherHandle);

    static {
        RCLJava.loadLibrary("rcljavanode_topic_Publisher__" + RCLJava.getRMWIdentifier());
    }

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
    public Publisher(final long nodeHandle, final long publisherHandle, final Class<T> messageType, final String topic, final QoSProfile qosProfile) {
        this.nodeHandle = nodeHandle;
        this.publisherHandle = publisherHandle;
        this.messageType = messageType;
        this.topic = topic;
        this.qosProfile = qosProfile;
    }

    /**
     * Publish a message.
     *
     * @param message An instance of the &lt;T&gt; parameter.
     */
    public void publish(final T msg) {
        Publisher.nativePublish(this.publisherHandle, msg);
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

    public final long getNodeHandle() {
        return this.nodeHandle;
    }

    public final long getPublisherHandle() {
        return this.publisherHandle;
    }

    /**
     * Safely destroy the underlying ROS2 publisher structure.
     */
    public void dispose() {
        Publisher.logger.debug("Destroy Publisher of topic : " + this.topic);

        Publisher.nativeDispose(this.nodeHandle, this.publisherHandle);
    }
}
