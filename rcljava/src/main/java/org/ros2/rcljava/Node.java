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
package org.ros2.rcljava;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * <h1>Node ROS2.</h1>
 * <p></p>
 * @author Esteve Fernandez <esteve@apache.org>
 * @author Mickael Gaillard <mick.gaillard@gmail.com>
 */
public class Node {
    private static Logger logger = Logger.getLogger(RCLJava.LOG_NAME);

    static {
        RCLJava.loadLibrary("rcljavaNode__" + RCLJava.getRMWIdentifier());
    }

    /** Node handler */
    private final long nodeHandle;

    /** List of subscriptions */
    private final List<Subscription<?>> subscriptions;

    // Native call.
    private static native <T> long nativeCreatePublisherHandle(
            long nodeHandle, Class<T> cls, String topic);

    private static native <T> long nativeCreateSubscriptionHandle(
            long nodeHandle, Class<T> cls, String topic);

    /**
     * Constructor of Node.
     * @param nodeHandle Handler to the node.
     */
    public Node(long nodeHandle) {
        this.nodeHandle = nodeHandle;
        this.subscriptions = new ArrayList<Subscription<?>>();
    }

    /**
     * <h1>Create a new publisher.</h1>
     *
     * @param <T> Message definition.
     * @param message Message class.
     * @param topic Topic to publish.
     * @param qos QOS profile.
     * @return Publisher instance.
     */
    public <T> Publisher<T> createPublisher(Class<T> message, String topic,  QoSProfile qos) {
        logger.fine("Create Publisher : " + topic);
        long publisherHandle = Node.nativeCreatePublisherHandle(this.nodeHandle, message, topic);

        Publisher<T> publisher = new Publisher<T>(this.nodeHandle, publisherHandle, message, topic, qos);
        RCLJava.publisherReferences.add(new WeakReference<Publisher<?>>(publisher));

        return publisher;
    }

    /**
     * <h1>Create a new Subscriber with callback.</h1>
     *
     * @param <T> Message definition.
     * @param message Message Class
     * @param topic Topic to subscribe.
     * @param callback Function to call on recieve.
     * @param qos QOS profile.
     * @return
     */
    public <T> Subscription<T> createSubscription(
            Class<T> message,
            String topic,
            Consumer<T> callback,
            QoSProfile qos) {
        logger.fine("Create Subscription : " + topic);
        long subscriptionHandle = Node.nativeCreateSubscriptionHandle(this.nodeHandle, message, topic);

        Subscription<T> subscription = new Subscription<T>(
                this.nodeHandle,
                subscriptionHandle,
                message,
                topic,
                callback,
                qos);
        this.subscriptions.add(subscription);
        return subscription;
    }

    /**
     * Get list of Subscriptions.
     * @return ArrayList of Subscriptions
     */
    public List<Subscription<?>> getSubscriptions() {
        return this.subscriptions;
    }

    /**
     * Release all Publisher ressource.
     */
    public void dispose() {
        //TODO Implement on JNI
    }
}
