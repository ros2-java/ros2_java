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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.ros2.rcljava.action.ActionServer;
import org.ros2.rcljava.action.ActionServerGoalHandle;
import org.ros2.rcljava.action.CancelCallback;
import org.ros2.rcljava.action.GoalCallback;
import org.ros2.rcljava.client.Client;
import org.ros2.rcljava.concurrent.Callback;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.consumers.TriConsumer;
import org.ros2.rcljava.graph.EndpointInfo;
import org.ros2.rcljava.graph.NameAndTypes;
import org.ros2.rcljava.graph.NodeNameInfo;
import org.ros2.rcljava.interfaces.Disposable;
import org.ros2.rcljava.interfaces.ActionDefinition;
import org.ros2.rcljava.interfaces.GoalRequestDefinition;
import org.ros2.rcljava.interfaces.MessageDefinition;
import org.ros2.rcljava.interfaces.ServiceDefinition;
import org.ros2.rcljava.parameters.ParameterCallback;
import org.ros2.rcljava.parameters.ParameterType;
import org.ros2.rcljava.parameters.ParameterVariant;
import org.ros2.rcljava.parameters.client.AsyncParametersClient;
import org.ros2.rcljava.parameters.client.SyncParametersClient;
import org.ros2.rcljava.publisher.Publisher;
import org.ros2.rcljava.qos.QoSProfile;
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
   * @return All the @{link ActionServer}s that were created by this instance.
   */
  Collection<ActionServer> getActionServers();

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

  <T extends ServiceDefinition> Service<T> createService(
      final Class<T> serviceType,
      final String serviceName,
      final TriConsumer<RMWRequestId, ? extends MessageDefinition, ? extends MessageDefinition>
          callback,
      final QoSProfile qosProfile);

  <T extends ServiceDefinition> Service<T> createService(
      final Class<T> serviceType,
      final String serviceName,
      final TriConsumer<RMWRequestId, ? extends MessageDefinition, ? extends MessageDefinition>
          callback);

  <T extends ServiceDefinition> Client<T> createClient(
      final Class<T> serviceType, final String serviceName, final QoSProfile qosProfile);

  <T extends ServiceDefinition> Client<T> createClient(
      final Class<T> serviceType, final String serviceName);

  /**
   * Create an ActionServer&lt;T&gt;.
   *
   * @param <T> The type of action that will be handled by the created @{link ActionServer}.
   * @param actionName The name of action that the create @{link ActionServer} will offer.
   * @param goalCallback The callback that will be called when the @{link ActionServer}
   *     receives a new goal request.
   * @param cancelCallback The callback that will be called when the @{link ActionServer}
   *     receives a cancel request for an active goal.
   * @param acceptedCallback The callback that will be called when the @{link ActionServer}
   *     accepts a goal request.
   */
  <T extends ActionDefinition> ActionServer<T> createActionServer(final Class<T> actionType,
      final String actionName,
      final GoalCallback<? extends GoalRequestDefinition<T>> goalCallback,
      final CancelCallback<T> cancelCallback,
      final Consumer<ActionServerGoalHandle<T>> acceptedCallback);

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

  /**
   * Remove an @{link ActionServer} created by this Node.
   *
   * Calling this method effectively invalidates the passed @{link ActionServer}.
   * If the server was not created by this Node, then nothing happens.
   *
   * @param actionServer The object to remove from this node.
   * @return true if the server was removed, false if the server was already
   *   removed or was never created by this Node.
   */
  boolean removeActionServer(final ActionServer actionServer);

  /**
   * Create a wall timer.
   *
   * A wall timer uses steady time.
   * To create a timer that uses ROS time, see @{link Node#createTimer}.
   *
   * @param period Time between calls to the provided callback function.
   * @param unit Time unit of the timer period.
   * @param callback Function that is called when the timer expires.
   * @return The created timer.
   */
  @SuppressWarnings("deprecation")
  WallTimer createWallTimer(final long period, final TimeUnit unit, final Callback callback);

  /**
   * Create a timer.
   *
   * The timer will use the same time source as the node, ie. ROS time.
   *
   * @param period Time between calls to the provided callback function.
   * @param unit Time unit of the timer period.
   * @param callback Function that is called when the timer expires.
   * @return The created timer.
   */
  Timer createTimer(final long period, final TimeUnit unit, final Callback callback);

  /** Get the name of the node.
   *
   * @return The name of the node.
   */
  String getName();

  /** Get the namespace of the node.
   *
   * This namespace is the "node's" namespace, and therefore is not affected
   * by any sub-namespace's that may affect entities created with this instance.
   *
   * @return The namespace of the node.
   */
  String getNamespace();

  /**
   * Create an asynchronous parameter client.
   */
  AsyncParametersClient createAsyncParametersClient(String nodeName);

  /**
   * Create an synchronous parameter client.
   */
  SyncParametersClient createSyncParametersClient(String nodeName);

  /**
   * Declare and initialize a parameter, return the effective value.
   *
   * See the documentation for the full signature of declareParameter for
   * more details.
   * This method will use a default descriptor that sets only the name.
   */
  ParameterVariant declareParameter(ParameterVariant parameter);

  /**
   * Declare and initialize a parameter, return the effective value.
   *
   * This method is used to declare that a parameter exists on this node.
   * Whatever name and value is in the given ParameterVariant will be used
   * as the name and initial value of the parameter.
   * If successful, the passed in ParameterVariant is returned.
   *
   * The passed in parameter descriptor will be stored alongside the parameter.
   * This descriptor can be used for additional meta-information about the
   * parameter, like ranges, read-only, etc.
   *
   * This method, if successful, will result in any callback registered with
   * addOnSetParametersCallback to be called.
   * If that callback prevents the initial value for the parameter from being
   * set then an InvalidParameterValueException is thrown.
   *
   * The returned reference will remain valid until the parameter is
   * undeclared.
   *
   * @param parameter The parameter to declare.
   * @param descriptor The descriptor to declare.
   * @return The parameter.
   * @throws InvalidParametersException if the parameter name is invalid.
   * @throws ParameterAlreadyDeclaredException if parameter has already been declared.
   * @throws InvalidParameterValueException if initial value fails to be set.
   */
  ParameterVariant declareParameter(ParameterVariant parameter, rcl_interfaces.msg.ParameterDescriptor descriptor);

  /**
   * Declare and initialize several parameters.
   *
   * Each parameter in the parameters List is declared on the node along with
   * the corresponding descriptor.
   * Note that the length of the parameters list and descriptors list must be
   * the same, or an IllegalArgumentException is thrown.
   * A list of the ParameterVariants will be returned.
   *
   * This method, if successful, will result in any callback registered with
   * addOnSetParametersCallback to be called, once for each parameter.
   * If that callback prevents the initial value for any parameter from being
   * set then an InvalidParameterValueException is thrown.
   *
   * @param parameters The parameters to set.
   * @param descriptors The descriptors to set.
   * @return The list of parameters that were declared.
   * @throws IllegalArgumentException if the parameters list and descriptors
   *   list are not the same length.
   * @throws InvalidParametersException if any of the parameter names is invalid.
   * @throws ParameterAlreadyDeclaredException if any parameter in the list has already
   *   been declared.
   * @throws InvalidParameterValueException if any of the initial values fail to be set.
   */
  List<ParameterVariant> declareParameters(List<ParameterVariant> parameters, List<rcl_interfaces.msg.ParameterDescriptor> descriptors);

  /**
   * Undeclare a previously declared parameter.
   *
   * This method will not cause a callback registered with
   * addOnSetParametersCallback to be called.
   *
   * @param name The name of the parameter to be undeclared.
   * @throws ParameterNotDeclaredException if the parameter
   *   has not been declared.
   */
  void undeclareParameter(String name);

  /**
   * Return true if a given parameter is declared.
   *
   * @param name The name of the parameter to check for being declared.
   * @return true if the parameter name has been declared, otherwise false.
   */
  boolean hasParameter(String name);

  /**
   * Return a list of parameters by the given list of parameter names.
   *
   * This method will throw a ParameterNotDeclaredException if any of the requested
   * parameters have not been declared and undeclared parameters are not
   * allowed.
   *
   * If undeclared parameters are allowed and the parameter has not been
   * declared, then the returned ParameterVariant will be default initialized
   * and therefore have the type ParameterType::PARAMETER_NOT_SET.
   *
   * @param names The names of the parameters to be retrieved.
   * @return The parameters that were retrieved.
   * @throws ParameterNotDeclaredException if any of the parameters have not been
   *   declared and undeclared parameters are not allowed.
   */
  List<ParameterVariant> getParameters(List<String> names);

  /**
   * Return a list of parameters by the given list of parameter names.
   *
   * See the documentation for getParameters for more details.
   *
   * @param name The names of the parameters to be retrieved.
   * @return The parameter that was retrieved.
   * @throws ParameterNotDeclaredException if the parameter has not been declared
   *   and undeclared parameters are not allowed.
   */
  ParameterVariant getParameter(String name);

  /**
   * Return the named parameter value, or the "alternativeValue" if not set.
   *
   * This method will not throw ParameterNotDeclaredException if a parameter is
   * not declared.
   * Instead, it will return the alternativeValue.
   *
   * In all cases, the parameter is never set or declared within the node.
   *
   * @param name The name of the parameter to get.
   * @param alternativeValue Value to be returned if the parameter was not set.
   * @return The parameter that was retrieved.
   */
  ParameterVariant getParameterOr(String name, ParameterVariant alternateValue);

  /**
   * Get parameters that have a given prefix in their names.
   *
   * The names which are used as keys in the returned Map have the prefix
   * removed.
   * For example, if you use the prefix "foo" and the parameters "foo.ping",
   * "foo.pong" and "bar.baz" exist, then the returned dictionary will have the
   * keys "ping" and "pong".
   * Note that the parameter separator is also removed from the parameter name
   * to create the keys.
   *
   * An empty string for the prefix will match all parameters.
   *
   * If no parameters with the prefix are found, an empty Map will be returned.
   *
   * @param prefix The prefix of the parameters to get.
   * @return A set of parameters with the given prefix.
   */
  Map<String, ParameterVariant> getParametersByPrefix(String prefix);

  /**
   * Set the given parameter and then return result of the set action.
   *
   * If the parameter has not been declared and undeclared parameters are not
   * allowed, this function will throw a ParameterNotDeclaredException.
   *
   * If undeclared parameters are allowed, then the parameter is implicitly
   * declared with the default parameter meta data before being set.
   * Parameter overrides are ignored by set_parameter.
   *
   * This method will result in any callback registered with
   * addOnSetParametersCallback to be called.
   * If the callback prevents the parameter from being set, then it will be
   * reflected in the SetParametersResult that is returned, but no exception
   * will be thrown.
   *
   * If the value type of the passed in parameter is
   * ParameterType::PARAMETER_NOT_SET, and the existing parameter type is
   * something else, then the parameter will be implicitly undeclared.
   *
   * @param parameter The parameter to be set.
   * @return The result of the set action.
   * @throws InvalidParametersException if parameter name is invalid.
   * @throws ParameterNotDeclaredException if the parameter
   *   has not been declared and undeclared parameters are not allowed.
   */
  rcl_interfaces.msg.SetParametersResult setParameter(ParameterVariant parameter);

  /**
   * Set the given parameters, one at a time, and then return the result of each
   * set action.
   *
   * Parameters are set in the order they are given within the input List.
   *
   * Like setParameter, if any of the parameters to be set have not first been
   * declared, and undeclared parameters are not allowed (the default), then
   * this method will throw a ParameterNotDeclaredException.
   *
   * If setting a parameter fails due to not being declared, then the
   * parameters which have already been set will stay set, and no attempt will
   * be made to set the parameters which come after.
   *
   * If a parameter fails to be set due to any other reason, like being
   * rejected by the user's callback (basically any reason other than not
   * having been declared beforehand), then that is reflected in the
   * corresponding SetParametersResult in the vector returned by this function.
   *
   * This method will result in any callback registered with
   * addOnSetParametersCallback to be called, once for each parameter.
   * If the callback prevents the parameter from being set, then, as mentioned
   * before, it will be reflected in the corresponding SetParametersResult
   * that is returned, but no exception will be thrown.
   *
   * Like setParameter this method will implicitly undeclare parameters
   * with the type ParameterType::PARAMETER_NOT_SET.
   *
   * @param parameters The list of parameters to be set.
   * @return The results for each set action as a list.
   * @throws InvalidParametersException if any parameter name is invalid.
   * @throws ParameterNotDeclaredException if any parameter has not been declared
   *   and undeclared parameters are not allowed.
   */
  List<rcl_interfaces.msg.SetParametersResult> setParameters(List<ParameterVariant> parameters);

  /**
   * Set the given parameters, all at one time, and then aggregate result.
   *
   * In contrast to setParameters, either all of the parameters are set or none
   * of them are set.
   *
   * Like setParameter and setParameters, this method will throw a
   * ParameterNotDeclaredException if any of the parameters to be set have not first
   * been declared.
   * If the exception is thrown then none of the parameters will have been set.
   *
   * This method will result in any callback registered with
   * addOnSetParametersCallback to be called, just one time.
   * If the callback prevents the parameters from being set, then it will be
   * reflected in the SetParametersResult which is returned, but no exception
   * will be thrown.
   *
   * If multiple ParameterVariant instances in the list have the same name, then
   * only the last one in the list (forward iteration) will be set.
   *
   * Like setParameter this method will implicitly undeclare parameters
   * with the type ParameterType::PARAMETER_NOT_SET.
   *
   * @param parameters The list of parameters to be set.
   * @return The aggregate result of setting all the parameters atomically.
   * @throws InvalidParametersException if any parameter name is invalid.
   * @throws ParameterNotDeclaredException if any parameter has not been declared
   *   and undeclared parameters are not allowed.
   */
  rcl_interfaces.msg.SetParametersResult setParametersAtomically(List<ParameterVariant> parameters);

  /**
   * Add a callback to be called when parameters are being set or declared.
   *
   * The callback signature is designed to allow handling of any of the above
   * `setParameter*` or `declareParameter*` methods, and so it takes a list of
   * parameters to be set, and returns an instance of
   * rcl_interfaces::msg::SetParametersResult to indicate whether or not the
   * parameters should be set or not, and if not why.
   *
   * Users who wish to register a callback must implement the ParameterCallback
   * class, then call this method with an instance of the implemented class.
   *
   * Note that the callback is called when declareParameter and its variants
   * are called, and so it cannot be assumed any of the parameter has been set
   * before this callback.  If checking a new value against the existing one,
   * the case where the parameter is not yet set must be accounted for.
   *
   * The callback may introspect other already set parameters (by calling any
   * of the {get,list,describe}Parameter methods), but may *not* modify
   * other parameters (by calling any of the {set,declare}Parameter methods)
   * or modify the registered callback itself (by calling the
   * addOnSetParametersCallback method).
   *
   * The registered callbacks are called when a parameter is set.
   * When a callback returns a not successful result, the remaining callbacks aren't called.
   * The order of the callback is the reverse from the registration order.
   *
   * @param cb The callback to register.
   */
  void addOnSetParametersCallback(ParameterCallback cb);

  /**
   * Remove a callback registered with `addOnSetParametersCallback`.
   *
   * Calling `remove_on_set_parameters_callback` more than once is an error.
   *
   * @param cb The callback handler to remove.
   */
  void removeOnSetParametersCallback(ParameterCallback cb);

  /**
   * Return the parameter descriptor for the given parameter name.
   *
   * This method will throw an ParameterNotDeclaredException if the requested
   * parameter has not been declared and undeclared parameters are not allowed.
   *
   * If undeclared parameters are allowed, then a default initialized
   * descriptor will be returned.
   *
   * @param name The name of the parameter to describe.
   * @return The descriptor for the given parameter name.
   * @throws ParameterNotDeclaredException if the parameter has not been declared
   *   and undeclared parameters are not allowed.
   */
  rcl_interfaces.msg.ParameterDescriptor describeParameter(String name);

  /**
   * Return a List of parameter descriptors, one for each of the given names.
   *
   * This method will throw an ParameterNotDeclaredException if any of the requested
   * parameters have not been declared and undeclared parameters are not allowed.
   *
   * If undeclared parameters are allowed, then a default initialized
   * descriptor will be returned for each of the undeclared parameter's descriptor.
   *
   * If the names list is empty, then an empty list will be returned.
   *
   * @param names The list of parameter names to describe.
   * @return A list of parameter descriptors, one for each parameter given.
   * @throws ParameterNotDeclaredException if any of the parameters have not been
   *   declared and undeclared parameters are not allowed.
   */
  List<rcl_interfaces.msg.ParameterDescriptor> describeParameters(List<String> names);

  /**
   * Return a list of parameter types, one for each of the given names.
   *
   * This method will throw an ParameterNotDeclaredException if any of the requested
   * parameters have not been declared and undeclared parameters are not allowed.
   *
   * If undeclared parameters are allowed, then the default type
   * ParameterType::PARAMETER_NOT_SET will be returned.
   *
   * @param names The list of parameter names to get the types.
   * @return A list of parameter types, one for each parameter given.
   * @throws ParameterNotDeclaredException if any of the parameters have not been
   *   declared and undeclared parameters are not allowed.
   */
  List<ParameterType> getParameterTypes(List<String> names);

  /**
   * List the parameters with the given prefixes at the given depth.
   *
   * This method finds all parameters with the given prefixes at no deeper than
   * the given depth and returns them in an aggregated result.
   * If the prefixes list is empty, then all parameters up to the given depth
   * are returned.
   * If the depth is rcl_interfaces.srv.ListParameters_Request.DEPTH_RECURSIVE,
   * then all depths are considered.
   *
   * Thus, it is possible to get a list of all of the parameters by calling this
   * method with an empty prefix and a depth of DEPTH_RECURSIVE.
   * To get all parameters up to a depth of 2, call this method with an empty
   * prefix and a depth equal to 2.
   * To get all parameters with a prefix of "foo", call this method with a prefix
   * containing "foo" and a depth of DEPTH_RECURSIVE.
   *
   * The return value is a ListParametersResult message, which contains the
   * prefixes and the names of all matching parameters.
   *
   * @param prefixes The list of prefixes to find (may be the empty list).
   * @param depth How deep to look for parameters (may be DEPTH_RECURSIVE for all depths).
   * @return rcl_interfaces.msg.ListParametersResult
   */
  rcl_interfaces.msg.ListParametersResult listParameters(List<String> prefixes, long depth);

  /**
   * Returns a collection of node names that were detected in the ROS graph.
   * See @{link NodeNameInfo} for more information about the return value.
   */
  Collection<NodeNameInfo> getNodeNames();

  /**
   * Return the topics names and types that were detected in the graph.
   * See @{link graph#NameAndTypes} for more information about the returned value.
   *
   * @return the detected topic names and types.
   */
  Collection<NameAndTypes> getTopicNamesAndTypes();

  /**
   * Return the service names and types that were detected in the graph.
   * See @{link graph#NameAndTypes} for more information about the returned value.
   *
   * @return the detected service names and types.
   */
  Collection<NameAndTypes> getServiceNamesAndTypes();

  /**
   * Get information of all publishers in a topic.
   *
   * The queried information includes the node that created the publisher, its qos, etc.
   * For more info, see @{link EndpointInfo}.
   *
   * @param topicName The topic name of interest.
   * @return A collection of `EndpointInfo` instances, describing all publishers in the
   *    passed topic.
   */
  Collection<EndpointInfo> getPublishersInfo(final String topicName);

  /**
   * Get information of all subscriptions in a topic.
   *
   * The queried information includes the node that created the publisher, its qos, etc.
   * For more info, see @{link EndpointInfo}.
   *
   * @param topicName The topic name of interest.
   * @return A collection of `EndpointInfo` instances, describing all subscriptions in the
   *    passed topic.
   */
  Collection<EndpointInfo> getSubscriptionsInfo(final String topicName);

  /**
   * Return the publisher names and types that were created from the node specified by the given
   * node name and namespace.
   * See @{link graph#NameAndTypes} for more information about the returned value.
   *
   * @param nodeName name of the node we want to know its publishers.
   * @param nodeNamespace namespace of the node we want to know its publishers.
   * @return the detected publisher names and types.
   */
  Collection<NameAndTypes> getPublisherNamesAndTypesByNode(String nodeName, String nodeNamespace);

  /**
   * Return the subscription names and types that were created from the node specified by the given
   * node name and namespace.
   * See @{link graph#NameAndTypes} for more information about the returned value.
   *
   * @param nodeName name of the node we want to know its subscriptions.
   * @param nodeNamespace namespace of the node we want to know its subscriptions.
   * @return the detected subscription names and types.
   */
  Collection<NameAndTypes> getSubscriptionNamesAndTypesByNode(
    String nodeName, String nodeNamespace);

  /**
   * Return the service server names and types that were created from the node specified by the
   * given node name and namespace.
   * See @{link graph#NameAndTypes} for more information about the returned value.
   *
   * @param nodeName name of the node we want to know its services.
   * @param nodeNamespace namespace of the node we want to know its services.
   * @return the detected service server names and types.
   */
  Collection<NameAndTypes> getServiceNamesAndTypesByNode(
    String nodeName, String nodeNamespace);

  /**
   * Return the client names and types that were created from the node specified by the
   * given node name and namespace.
   * See @{link graph#NameAndTypes} for more information about the returned value.
   *
   * @param nodeName name of the node we want to know its clients.
   * @param nodeNamespace namespace of the node we want to know its clients.
   * @return the detected client names and types.
   */
  Collection<NameAndTypes> getClientNamesAndTypesByNode(
    String nodeName, String nodeNamespace);
}
