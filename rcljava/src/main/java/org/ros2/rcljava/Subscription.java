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

/**
 * <h1>Subscrition of node.</h1>
 * <p></p>
 * @param <T> Message Type.
 * @author Esteve Fernandez <esteve@apache.org>
 * @author Mickael Gaillard <mick.gaillard@gmail.com>
 */
public class Subscription<T> {

    /** Node Handler. */
    private final long nodeHandle;

    /** Subsciption Hander. */
    private final long subscriptionHandle;

    /** Message Type. */
    private final Class<T> messageType;

    /** Topic subscribed. */
    private final String topic;

    /** Callback. */
    private final Consumer<T> callback;

    /** Quality of Service profil. */
    private final QoSProfile qosProfile;

    // Native call.
    private static native void nativeDispose(long nodeHandle, long publisherHandle);

    public Subscription(long nodeHandle, long subscriptionHandle, Class<T> messageType, String topic, Consumer<T> callback, QoSProfile qosProfile) {
        this.nodeHandle = nodeHandle;
        this.subscriptionHandle = subscriptionHandle;
        this.messageType = messageType;
        this.topic = topic;
        this.callback = callback;
        this.qosProfile = qosProfile;
    }

    /**
     * Get Callback.
     * @return
     */
    public Consumer<T> getCallback() {
        return this.callback;
    }

    public long getSubscriptionHandle() {
        return this.subscriptionHandle;
    }

    /**
     * Get message type.
     * @return
     */
    public Class<T> getMsgType() {
        return this.messageType;
    }

    /**
     * Get topic name.
     * @return
     */
    public String getTopic() {
        return this.topic;
    }

    /**
     * Release all Publisher ressource.
     */
    public void dispose() {
        //TODO implement to JNI
        // Subscription.nativeDispose(this.nodeHandle, this.subscriptionHandle);
    }
}
