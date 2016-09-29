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
 * <h1>Publisher of node.</h1>
 * <p></p>
 * @param <T> Message Type.
 * @author Esteve Fernandez <esteve@apache.org>
 * @author Mickael Gaillard <mick.gaillard@gmail.com>
 */
public class Publisher<T> {

    /** Node Handler. */
    private final long nodeHandle;

    /** Publisher Hander. */
    private final long publisherHandle;

    /** Message Type. */
    private final Class<T> messageType;

    /** Topic to publish. */
    private final String topic;

    /** Quality of Service profil. */
    private final QoSProfile qosProfile;

    // Native call.
    private static native <T> void nativePublish(long publisherHandle, T msg);
    private static native void nativeDispose(long nodeHandle, long publisherHandle);

    static {
        RCLJava.loadLibrary("rcljavaPublisher__" + RCLJava.getRMWIdentifier());
    }

    /**
     * Constructor of Publisher.
     *
     * @param nodeHandle Node handler initialize.
     * @param publisherHandle Publisher handler.
     * @param messageType Message type.
     * @param topic Topic to publish.
     * @param qos Quality of Service profile.
     */
    public Publisher(long nodeHandle, long publisherHandle, Class<T> messageType, String topic, QoSProfile qosProfile) {
        this.nodeHandle = nodeHandle;
        this.publisherHandle = publisherHandle;
        this.messageType = messageType;
        this.topic = topic;
        this.qosProfile = qosProfile;
    }

    /**
     * Publish a message.
     * @param msg Message to publish.
     */
    public void publish(T msg) {
        Publisher.nativePublish(this.publisherHandle, msg); //TODO(theos) add qosProfile
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
        //Publisher.nativeDispose(this.nodeHandle, this.publisherHandle);
    }
}
