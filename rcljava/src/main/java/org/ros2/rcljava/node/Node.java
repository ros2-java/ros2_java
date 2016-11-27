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
package org.ros2.rcljava.node;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ros2.rcljava.Log;
import org.ros2.rcljava.qos.QoSProfile;
import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.exception.NotImplementedException;
import org.ros2.rcljava.internal.INode;
import org.ros2.rcljava.namespace.GraphName;
import org.ros2.rcljava.node.parameter.ParameterConsumer;
import org.ros2.rcljava.node.parameter.ParameterService;
import org.ros2.rcljava.node.parameter.ParameterVariant;
import org.ros2.rcljava.node.service.Client;
import org.ros2.rcljava.node.service.RMWRequestId;
import org.ros2.rcljava.node.service.Service;
import org.ros2.rcljava.node.service.TriConsumer;
import org.ros2.rcljava.node.topic.Consumer;
import org.ros2.rcljava.node.topic.Publisher;
import org.ros2.rcljava.node.topic.Subscription;

import builtin_interfaces.msg.Time;
import rcl_interfaces.msg.Parameter;
import rcl_interfaces.msg.ParameterEvent;
import rcl_interfaces.msg.ParameterType;
import rcl_interfaces.msg.SetParametersResult;

/**
 * This class serves as a bridge between ROS2's rcl_node_t and RCLJava.
 * A Node must be created via @{link RCLJava#createNode(String)}
 *
 */
public class Node implements INode {

    private static final Logger logger = LoggerFactory.getLogger(Node.class);

    // Loading JNI library.
    static {
        RCLJava.loadLibrary("rcljavanode_Node__" + RCLJava.getRMWIdentifier());
    }

    /** Name of the node */
    private String name;

    /** Name space of the node. */
    private String nameSpace;

    /**
     * An integer that represents a pointer to the underlying ROS2 node
     * structure (rcl_node_t).
     */
    private final long nodeHandle;

    /**
     * All the @{link Subscription}s that have been created through this instance.
     */
    private final Queue<Subscription<?>> subscriptions;

    /**
     * All the @{link Publisher}s that have been created through this instance.
     */
    private final Queue<Publisher<?>> publishers;

    /**
     * All the @{link Service}s that have been created through this instance.
     */
    private final Queue<Service<?>> services;

    /**
     * All the @{link Client}s that have been created through this instance.
     */
    private final Queue<Client<?>> clients;

    /**
     *  List of parameters
     */
    private final HashMap<String, ParameterVariant<?>> parameters;

    /**
     * Parameter service.
     */
    private final ParameterService parameterService;
    private ParameterConsumer parameterCallback ;

    public void onParamChange(ParameterConsumer parameterCallback) {
        this.parameterCallback = parameterCallback;
    }

    /**
     * Log to ROS2.
     */
    private final Log logRos;

    // Native call.
    /**
     * Create a ROS2 publisher (rcl_publisher_t) and return a pointer to
     *     it as an integer.
     *
     * @param <T> The type of the messages that will be published by the
     *     created @{link Publisher}.
     * @param nodeHandle A pointer to the underlying ROS2 node structure.
     * @param messageType The class of the messages that will be published by the
     *     created @{link Publisher}.
     * @param topic The topic to which the created @{link Publisher} will
     *     publish messages.
     * @param qosProfileHandle A pointer to the underlying ROS2 QoS profile
     *     structure.
     * @return A pointer to the underlying ROS2 publisher structure.
     */
    private static native <T extends org.ros2.rcljava.internal.message.Message> long nativeCreatePublisherHandle(
            long nodeHandle, Class<T> messageType, String topic, long qosProfileHandle);

    /**
     * Create a ROS2 subscription (rcl_subscription_t) and return a pointer to
     *     it as an integer.
     *
     * @param <T> The type of the messages that will be received by the
     *     created @{link Subscription}.
     * @param nodeHandle A pointer to the underlying ROS2 node structure.
     * @param messageType The class of the messages that will be received by the
     *     created @{link Subscription}.
     * @param topic The topic from which the created @{link Subscription} will
     *     receive messages.
     * @param qosProfileHandle A pointer to the underlying ROS2 QoS profile
     *     structure.
     * @return A pointer to the underlying ROS2 subscription structure.
     */
    private static native <T> long nativeCreateSubscriptionHandle(
            long nodeHandle, Class<T> messageType, String topic, long qosProfileHandle);

    private static native <T> long nativeCreateClientHandle(
            long nodeHandle, Class<T> cls, String serviceName, long qosProfileHandle);

    private static native <T> long nativeCreateServiceHandle(
            long nodeHandle, Class<T> cls, String serviceName, long qosProfileHandle);

    private static native void nativeDispose(long nodeHandle);

    private static native String nativeGetName(long nodeHandle);

    private static native int nativeCountPublishers(long nodeHandle, String topic);

    private static native int nativeCountSubscribers(long nodeHandle, String topic);

    private static native HashMap<String, String> getListTopics(long nodeHandle);

//    private static native  ; //rcl_service_server_is_available

    /**
     * Constructor.
     *
     * @param nodeHandle A pointer to the underlying ROS2 node structure. Must not
     *     be zero.
     */
    public Node(final long nodeHandle,final String ns, final String nodeName) {

        if (nodeHandle==0) throw new NullPointerException("Node Handle is not define !");
        if (nodeName==null || nodeName.length() == 0) throw new NullPointerException("Node name is needed !");

        this.nameSpace      = ns;
        this.name           = nodeName;
        this.nodeHandle     = nodeHandle;
        this.subscriptions  = new LinkedBlockingQueue<Subscription<?>>();
        this.publishers     = new LinkedBlockingQueue<Publisher<?>>();
        this.clients        = new LinkedBlockingQueue<Client<?>>();
        this.services       = new LinkedBlockingQueue<Service<?>>();
        this.parameters     = new HashMap<String, ParameterVariant<?>>();

        Node.logger.debug("Init Node stack : " + this.name);

        GraphName.addNode(this);
        this.parameterService = new ParameterService(this);
        this.logRos = new Log(this);
    }

    /**
     * Safely destroy the underlying ROS2 node structure.
     */
    @Override
    public void dispose() {
        Node.logger.debug("Destroy Node stack : " + this.name);
        this.parameterService.dispose();

        Queue<Client<?>> tmpClients = new LinkedBlockingQueue<Client<?>>(this.clients);
        for (Client<?> client : tmpClients) {
            client.dispose();
        }

        Queue<Publisher<?>> tmpPublishers = new LinkedBlockingQueue<Publisher<?>>(this.publishers);
        for (Publisher<?> publisher : tmpPublishers) {
            publisher.dispose();
        }

        Queue<Service<?>> tmpServices = new LinkedBlockingQueue<Service<?>>(this.services);
        for (Service<?> service : tmpServices) {
            service.dispose();
        }

        Queue<Subscription<?>> tmpSubscribers = new LinkedBlockingQueue<Subscription<?>>(this.subscriptions);
        for (Subscription<?> subscriber : tmpSubscribers) {
            subscriber.dispose();
        }

        Node.nativeDispose(this.nodeHandle);
        GraphName.removeNode(this);
    }

    /**
     * Get the name of the node.
     *
     * @return The name of the node.
     */
    @Override
    public String getName() {
        String name = Node.nativeGetName(this.nodeHandle);

        if (!this.name.equals(name)) {
            Node.logger.debug("Node name has changed ! from " + this.name + " to " + name);
            this.name = name;
        }

        return name;
    }

    /**
     * Create a Publisher&lt;T&gt;.
     *
     * @param <T> The type of the messages that will be published by the
     *     created @{link Publisher}.
     * @param messageType The class of the messages that will be published by the
     *     created @{link Publisher}.
     * @param topic The topic to which the created @{link Publisher} will
     *     publish messages.
     *
     * @return A @{link Publisher} that represents the underlying ROS2 publisher
     *     structure.
     */
    @Override
    public <T extends org.ros2.rcljava.internal.message.Message> Publisher<T> createPublisher(
            final Class<T> messageType,
            final String topic,
            final QoSProfile qosProfile) {

        if (messageType==null) throw new NullPointerException("Message Type can't be null.");
        if (topic==null) throw new NullPointerException("Topic can't be null.");
        if (qosProfile==null) throw new NullPointerException("QOS can't be null;");

        String fqnTopic =  GraphName.getFullName(this, topic, null);
        Node.logger.debug("Create Publisher : " + fqnTopic);
        Publisher<T> publisher = null;

        if (GraphName.isValidTopic(fqnTopic)) {
            long qosProfileHandle = RCLJava.convertQoSProfileToHandle(qosProfile);
            long publisherHandle = Node.nativeCreatePublisherHandle(this.nodeHandle, messageType, fqnTopic, qosProfileHandle);
            RCLJava.disposeQoSProfile(qosProfileHandle);

            publisher = new Publisher<T>(this, publisherHandle, messageType, topic, qosProfile);
        }

        return publisher;
    }

    /**
     * Create and return a Publisher. (Retro-compatibility)
     *
     * @param <T> Message definition.
     * @param messageType Message class.
     * @param topic The topic for this publisher to publish on.
     * @return Publisher instance of the created publisher.
     */
    @Override
    public <T extends org.ros2.rcljava.internal.message.Message> Publisher<T> createPublisher(
            final Class<T> messageType,
            final String topic) {
        return this.createPublisher(messageType, topic, QoSProfile.DEFAULT);
    }

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
     * @param qos The quality of service profile to pass on to the rmw implementation.
     * @return A @{link Subscription} that represents the underlying ROS2
     *     subscription structure.
     */
    @Override
    public <T extends org.ros2.rcljava.internal.message.Message> Subscription<T> createSubscription(
            final Class<T> messageType,
            final String topic,
            final Consumer<T> callback,
            final QoSProfile qosProfile) {

        if (messageType==null) throw new NullPointerException("Message Type can't be null.");
        if (topic==null) throw new NullPointerException("Topic can't be null.");
        if (callback==null) throw new NullPointerException("Callback can't be null;");
        if (qosProfile==null) throw new NullPointerException("QOS can't be null;");

        String fqnTopic =  GraphName.getFullName(this, topic, null);
        Node.logger.debug("Create Subscription : " + fqnTopic);
        Subscription<T> subscription = null;

        if (GraphName.isValidTopic(fqnTopic)) {
            long qosProfileHandle = RCLJava.convertQoSProfileToHandle(qosProfile);
            long subscriptionHandle = Node.nativeCreateSubscriptionHandle(this.nodeHandle, messageType, fqnTopic, qosProfileHandle);
            RCLJava.disposeQoSProfile(qosProfileHandle);

            subscription = new Subscription<T>(
                    this,
                    subscriptionHandle,
                    messageType,
                    topic,
                    callback,
                    qosProfile);
        }

        return subscription;
    }

    /**
     * Create and return a Subscription. (Retro-compatibility)
     *
     * @param <T> Message definition.
     * @param messageType Message Class
     * @param topic The topic to subscribe on.
     * @param callback The user-defined callback function.
     * @param qos The quality of service profile to pass on to the rmw implementation.
     * @return Subscription instance of the created subscription.
     */
    @Override
    public <T extends org.ros2.rcljava.internal.message.Message> Subscription<T> createSubscription(
            final Class<T> messageType,
            final String topic,
            final Consumer<T> callback) {
        return this.createSubscription(messageType, topic, callback, QoSProfile.DEFAULT);
    }

    /**
     * Create and return a Client.
     *
     * @param <T> Message definition.
     * @param serviceType Service Class
     * @param serviceName The service to subscribe on.
     * @param qos The quality of service profile to pass on to the rmw implementation.
     * @return Client instance of the service.
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     */
    @Override
    public <T extends org.ros2.rcljava.internal.service.Service> Client<T> createClient(
            final Class<T> serviceType,
            final String serviceName,
            final QoSProfile qosProfile) throws Exception {


        if (serviceType==null) throw new NullPointerException("Service Type can't be null.");
        if (serviceName==null) throw new NullPointerException("Service name can't be null.");
        if (qosProfile==null) throw new NullPointerException("QOS can't be null;");

        String fqnService =  GraphName.getFullName(this, serviceName, null);
        Node.logger.debug("Create Client : " + fqnService);
        Client<T> client = null;

        if (GraphName.isValidTopic(fqnService)) {
            Class<?> requestType = (Class<?>)serviceType.getField("RequestType").get(null);

            Method requestFromJavaConverterMethod = requestType.getDeclaredMethod("getFromJavaConverter", (Class<?> []) null);
            long requestFromJavaConverterHandle = (Long)requestFromJavaConverterMethod.invoke(null, (Object []) null);

            Method requestToJavaConverterMethod = requestType.getDeclaredMethod("getToJavaConverter", (Class<?> []) null);
            long requestToJavaConverterHandle = (Long)requestToJavaConverterMethod.invoke(null, (Object []) null);

            Class<?> responseType = (Class<?>)serviceType.getField("ResponseType").get(null);

            Method responseFromJavaConverterMethod = responseType.getDeclaredMethod("getFromJavaConverter", (Class<?> []) null);
            long responseFromJavaConverterHandle = (Long)responseFromJavaConverterMethod.invoke(null, (Object []) null);

            Method responseToJavaConverterMethod = responseType.getDeclaredMethod("getToJavaConverter", (Class<?> []) null);
            long responseToJavaConverterHandle = (Long)responseToJavaConverterMethod.invoke(null, (Object []) null);

            long qosProfileHandle = RCLJava.convertQoSProfileToHandle(qosProfile);
            long clientHandle = Node.nativeCreateClientHandle(
                    this.nodeHandle,
                    serviceType,
                    fqnService,
                    qosProfileHandle);
            RCLJava.disposeQoSProfile(qosProfileHandle);

            client = new Client<T>(
                    new WeakReference<Node>(this),
                    clientHandle,
                    serviceType,
                    serviceName,
                    requestType,
                    responseType,
                    requestFromJavaConverterHandle,
                    requestToJavaConverterHandle,
                    responseFromJavaConverterHandle,
                    responseToJavaConverterHandle);
        }

        return client;
    }

    /**
     * Create and return a Client. (Retro-compatibility)
     *
     * @param <T> Message definition.
     * @param message Message Class
     * @param service The service to subscribe on.
     * @return Client instance of the service.
     * @throws Exception
     */
    @Override
    public <T extends org.ros2.rcljava.internal.service.Service> Client<T> createClient(
            final Class<T> serviceType,
            final String serviceName) throws Exception {
        return this.createClient(serviceType, serviceName, QoSProfile.SERVICES_DEFAULT);
    }

    /**
     * Create and return a Service.
     *
     * @param <T> Message definition.
     * @param serviceType Service Class
     * @param serviceName The service for this publisher to publish on.
     * @param callback The user-defined callback function.
     * @param qos The quality of service profile to pass on to the rmw implementation.
     * @return Service instance of the service.
     */
    @Override
    public <T extends org.ros2.rcljava.internal.service.Service> Service<T> createService(
            final Class<T> serviceType,
            final String serviceName,
            final TriConsumer<RMWRequestId, ?, ?> callback,
            final QoSProfile qosProfile
            ) throws Exception {

        if (serviceType==null) throw new NullPointerException("Service Type can't be null.");
        if (serviceName==null) throw new NullPointerException("Service name can't be null.");
        if (callback==null) throw new NullPointerException("Callback can't be null;");
        if (qosProfile==null) throw new NullPointerException("QOS can't be null;");

        String fqnService =  GraphName.getFullName(this, serviceName, null);
        Node.logger.debug("Create Service : " + fqnService);
        Service<T> service = null;

        if (GraphName.isValidTopic(fqnService)) {
//            long serviceHandle = Node.nativeCreateServiceHandle(this.nodeHandle, message, service, qos);
//            Service<T> srv = new Service<T>(this.nodeHandle, serviceHandle, service);

            Class<?> requestType = (Class<?>)serviceType.getField("RequestType").get(null);

            Method requestFromJavaConverterMethod = requestType.getDeclaredMethod("getFromJavaConverter", (Class<?> []) null);
            long requestFromJavaConverterHandle = (Long)requestFromJavaConverterMethod.invoke(null, (Object []) null);

            Method requestToJavaConverterMethod = requestType.getDeclaredMethod("getToJavaConverter", (Class<?> []) null);
            long requestToJavaConverterHandle = (Long)requestToJavaConverterMethod.invoke(null, (Object []) null);

            Class<?> responseType = (Class<?>)serviceType.getField("ResponseType").get(null);

            Method responseFromJavaConverterMethod = responseType.getDeclaredMethod("getFromJavaConverter", (Class<?> []) null);
            long responseFromJavaConverterHandle = (Long)responseFromJavaConverterMethod.invoke(null, (Object []) null);

            Method responseToJavaConverterMethod = responseType.getDeclaredMethod("getToJavaConverter", (Class<?> []) null);
            long responseToJavaConverterHandle = (Long)responseToJavaConverterMethod.invoke(null, (Object []) null);

            long qosProfileHandle = RCLJava.convertQoSProfileToHandle(qosProfile);
            long serviceHandle = Node.nativeCreateServiceHandle(this.nodeHandle, serviceType, serviceName, qosProfileHandle);
            RCLJava.disposeQoSProfile(qosProfileHandle);

            service = new Service<T>(this,
                    serviceHandle,
                    serviceType,
                    fqnService,
                    callback,
                    requestType,
                    responseType,
                    requestFromJavaConverterHandle,
                    requestToJavaConverterHandle,
                    responseFromJavaConverterHandle,
                    responseToJavaConverterHandle);
        }

        return service;
    }

    /**
     * Create and return a Service. (Retro-compatibility)
     *
     * @param <T> Message definition.
     * @param message Message Class
     * @param service The service for this publisher to publish on.
     * @param callback The user-defined callback function.
     * @return Service instance of the service.
     */
    @Override
    public <T extends org.ros2.rcljava.internal.service.Service> Service<T> createService(
            final Class<T> serviceType,
            final String serviceName,
            final TriConsumer<RMWRequestId, ?, ?> callback) throws Exception {

        return this.createService(serviceType, serviceName, callback, QoSProfile.SERVICES_DEFAULT);
    }

    @Override
    public List<SetParametersResult> setParameters(final List<ParameterVariant<?>> parameters) {
        List<SetParametersResult> results = new ArrayList<SetParametersResult>();

        for (ParameterVariant<?> parameterVariantRequest : parameters) {
            SetParametersResult result = this.setParametersAtomically(new ArrayList<ParameterVariant<?>>(Arrays.asList(parameterVariantRequest)));
            results.add(result);
        }

        return results;
    }

    @Override
    public SetParametersResult setParametersAtomically(final List<ParameterVariant<?>> parameters) {
        ParameterEvent parameter_event = new ParameterEvent();

        // TODO (jacquelinekay) Check handle parameter constraints
        SetParametersResult result = new SetParametersResult();

        if (this.parameterCallback != null) {
            result = this.parameterCallback.onParamChange(parameters);
        } else {
            result.setSuccessful(true);
        }

        if (result.getSuccessful()){
            for (ParameterVariant<?> paramVarReq : parameters) {
                Parameter parameter = paramVarReq.toParameter();

                if (!this.parameters.containsKey(paramVarReq.getName())) {
                    if (parameter.getValue().getType() != ParameterType.PARAMETER_NOT_SET) {
                        parameter_event.getNewParameters().add(parameter);
                    }
                } else {
                    if (parameter.getValue().getType() != ParameterType.PARAMETER_NOT_SET) {
                        parameter_event.getChangedParameters().add(parameter);
                    } else {
                        parameter_event.getDeletedParameters().add(parameter);
                    }
                }
                this.parameters.put(paramVarReq.getName(), paramVarReq);
            }
        }

        if (this.parameterService != null) {
            this.parameterService.notifyAddEvent(parameter_event);
        }

        return result;
    }

    @Override
    public List<ParameterVariant<?>> getParameters(final List<String> names) {
        List<ParameterVariant<?>>  result = new ArrayList<ParameterVariant<?>>();

        for (String name : names) {
            ParameterVariant<?> param = this.getParameter(name);
            if (param != null) {
                result.add(param);
            }
        }

        return result;
    }

    @Override
    public ParameterVariant<?> getParameter(final String name) {
        ParameterVariant<?> result = null;

        if (this.parameters.containsKey(name)) {
            result = this.parameters.get(name);
        }

        return result;
    }

    public List<String> getParametersNames() {
        return new ArrayList<String>(this.parameters.keySet());
    }

    public List<Byte> getParametersTypes(List<String> names) {
        List<Byte> result = new ArrayList<Byte>();

        for (String name : names) {
            if (this.parameters.containsKey(name)) {
                result.add(this.parameters.get(name).toParameterValue().getType());
            }
        }

        return result;
    }

    @Override
    public HashMap<String, String> getTopicNamesAndTypes() {
        HashMap<String, String> topics =  Node.getListTopics(this.nodeHandle);

        for (Entry<String, String> entry : topics.entrySet()) {
            Node.logger.debug("\t - Topics: " + entry.getKey() + "\t Value: " + entry.getValue());
        }

        return topics;
    }

    @Override
    public int countPublishers(final String topic) {
        return Node.nativeCountPublishers(this.nodeHandle, topic);
    }

    @Override
    public int countSubscribers(final String topic) {
        return Node.nativeCountSubscribers(this.nodeHandle, topic);
    }

    /**
     * Return the rcl_node_t node handle (non-const version).
     * @return
     */
    @Override
    public final long getNodeHandle() {
        return this.nodeHandle;
    }

    /**
     * Notify threads waiting on graph changes.
     *
     * Affects threads waiting on the notify guard condition, see:
     * get_notify_guard_condition(), as well as the threads waiting on graph
     * changes using a graph Event, see: wait_for_graph_change().
     *
     * This is typically only used by the rclcpp::graph_listener::GraphListener.
     *
     * @throws RCLBaseError (a child of that exception) when an rcl error occurs
     */
    @Override
    public void notifyGraphChange() {
        //TODO
        throw new NotImplementedException();
    }

    /** Notify any and all blocking node actions that shutdown has occurred. */
    @Override
    public void notifyShutdown() {
        //TODO
        throw new NotImplementedException();
    }

    /**
     * Return a graph event, which will be set anytime a graph change occurs.
     *
     * The graph Event object is a loan which must be returned.
     * The Event object is scoped and therefore to return the load just let it go
     * out of scope.
     */
    @Override
    public Object getGraphEvent() {
        //TODO
        throw new NotImplementedException();
//        return null;
    }

    /**
     * Wait for a graph event to occur by waiting on an Event to become set.
     *
     * The given Event must be acquire through the get_graph_event() method.
     *
     * @throws InvalidEventError if the given event is nullptr
     * @throws EventNotRegisteredError if the given event was not acquired with get_graph_event().
     */
    @Override
    public void waitForGraphChange(Object event, int timeout) {
        //TODO
        throw new NotImplementedException();
    }

    /**
     * This is typically only used by the rclcpp::graph_listener::GraphListener.
     * @return the number of on loan graph events, see get_graph_event().
     */
    @Override
    public int countGraphUsers() {
        //TODO
        throw new NotImplementedException();
//        return 0;
    }

    /**
     * Register the callback for parameter changes.
     * Repeated invocations of this function will overwrite previous callbacks
     *
     * @param User defined callback function, It is expected to atomically set parameters.
     */
    @Override
    public <T extends org.ros2.rcljava.internal.message.Message> void registerParamChangeCallback(Consumer<T> callback) {
        //TODO
        throw new NotImplementedException();
    }

    /**
     * @return All the @{link Subscription}s that were created by this instance.
     */
    public Queue<Subscription<? extends org.ros2.rcljava.internal.message.Message>> getSubscriptions() {
        return this.subscriptions;
    }

    /**
     * @return All the @{link Publisher}s that were created by this instance.
     */
    public final Queue<Publisher<? extends org.ros2.rcljava.internal.message.Message>> getPublishers() {
      return this.publishers;
    }

    /**
     * Get list of Clients.
     * @return ArrayList of Clients
     */
    public final Queue<Client<?>> getClients() {
        return this.clients;
    }

    /**
     * Get list of Services.
     * @return ArrayList of Services
     */
    public final Queue<Service<?>> getServices() {
        return this.services;
    }

    public Time getCurrentTime() {
        //TODO (theos)
        return null;
    }

    public Log getLog() {
        return this.logRos;
    }

    public String getNameSpace() {
        return this.nameSpace;
    }
}
