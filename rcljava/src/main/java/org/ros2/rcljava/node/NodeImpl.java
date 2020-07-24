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

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.client.Client;
import org.ros2.rcljava.client.ClientImpl;
import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.concurrent.Callback;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.consumers.TriConsumer;
import org.ros2.rcljava.contexts.Context;
import org.ros2.rcljava.qos.QoSProfile;
import org.ros2.rcljava.interfaces.Disposable;
import org.ros2.rcljava.interfaces.MessageDefinition;
import org.ros2.rcljava.interfaces.ServiceDefinition;
import org.ros2.rcljava.parameters.InvalidParametersException;
import org.ros2.rcljava.parameters.InvalidParameterValueException;
import org.ros2.rcljava.parameters.ParameterAlreadyDeclaredException;
import org.ros2.rcljava.parameters.ParameterCallback;
import org.ros2.rcljava.parameters.ParameterNotDeclaredException;
import org.ros2.rcljava.parameters.ParameterType;
import org.ros2.rcljava.parameters.ParameterVariant;
import org.ros2.rcljava.publisher.Publisher;
import org.ros2.rcljava.publisher.PublisherImpl;
import org.ros2.rcljava.service.RMWRequestId;
import org.ros2.rcljava.service.Service;
import org.ros2.rcljava.service.ServiceImpl;
import org.ros2.rcljava.subscription.Subscription;
import org.ros2.rcljava.subscription.SubscriptionImpl;
import org.ros2.rcljava.time.Clock;
import org.ros2.rcljava.time.ClockType;
import org.ros2.rcljava.timer.Timer;
import org.ros2.rcljava.timer.WallTimer;
import org.ros2.rcljava.timer.WallTimerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * {@inheritDoc}
 */
public class NodeImpl implements Node {
  private static final Logger logger = LoggerFactory.getLogger(NodeImpl.class);
  private static final String separator = ".";

  static {
    try {
      JNIUtils.loadImplementation(NodeImpl.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  /**
   * An integer that represents a pointer to the underlying ROS2 node
   * structure (rcl_node_t).
   */
  private long handle;

  /**
   * The context that this node is associated with.
   */
  private Context context;

  /**
   * The clock used by this node.
   */
  private Clock clock;

  /**
   * All the @{link Subscription}s that have been created through this instance.
   */
  private final Collection<Subscription> subscriptions;

  /**
   * All the @{link Publisher}s that have been created through this instance.
   */
  private final Collection<Publisher> publishers;

  /**
   * All the @{link Service}s that have been created through this instance.
   */
  private final Collection<Service> services;

  /**
   * All the @{link Client}s that have been created through this instance.
   */
  private final Collection<Client> clients;

  /**
   * All the @{link Timer}s that have been created through this instance.
   */
  private final Collection<Timer> timers;

  private final String name;

  private Object parametersMutex;

  class ParameterAndDescriptor {
    public ParameterVariant parameter;
    public rcl_interfaces.msg.ParameterDescriptor descriptor;
  }

  private Map<String, ParameterAndDescriptor> parameters;
  private boolean allowUndeclaredParameters;

  private Object parameterCallbacksMutex;
  private List<ParameterCallback> parameterCallbacks;

  /**
   * Constructor.
   *
   * @param handle A pointer to the underlying ROS2 node structure. Must not
   *     be zero.
   */
  public NodeImpl(final long handle, final String name, final Context context, final boolean allowUndeclaredParameters) {
    this.clock = new Clock(ClockType.SYSTEM_TIME);
    this.handle = handle;
    this.name = name;
    this.context = context;
    this.publishers = new LinkedBlockingQueue<Publisher>();
    this.subscriptions = new LinkedBlockingQueue<Subscription>();
    this.services = new LinkedBlockingQueue<Service>();
    this.clients = new LinkedBlockingQueue<Client>();
    this.timers = new LinkedBlockingQueue<Timer>();
    this.parametersMutex = new Object();
    this.parameters = new ConcurrentHashMap<String, ParameterAndDescriptor>();
    this.allowUndeclaredParameters = allowUndeclaredParameters;
    this.parameterCallbacksMutex = new Object();
    this.parameterCallbacks = new ArrayList<ParameterCallback>();
  }

  /**
   * Create a ROS2 publisher (rcl_publisher_t) and return a pointer to
   *     it as an integer.
   *
   * @param <T> The type of the messages that will be published by the
   *     created @{link Publisher}.
   * @param handle A pointer to the underlying ROS2 node structure.
   * @param messageType The class of the messages that will be published by the
   *     created @{link Publisher}.
   * @param topic The topic to which the created @{link Publisher} will
   *     publish messages.
   * @param qosProfileHandle A pointer to the underlying ROS2 QoS profile
   *     structure.
   * @return A pointer to the underlying ROS2 publisher structure.
   */
  private static native <T extends MessageDefinition> long nativeCreatePublisherHandle(
      long handle, Class<T> messageType, String topic, long qosProfileHandle);

  /**
   * Create a ROS2 subscription (rcl_subscription_t) and return a pointer to
   *     it as an integer.
   *
   * @param <T> The type of the messages that will be received by the
   *     created @{link Subscription}.
   * @param handle A pointer to the underlying ROS2 node structure.
   * @param messageType The class of the messages that will be received by the
   *     created @{link Subscription}.
   * @param topic The topic from which the created @{link Subscription} will
   *     receive messages.
   * @param qosProfileHandle A pointer to the underlying ROS2 QoS profile
   *     structure.
   * @return A pointer to the underlying ROS2 subscription structure.
   */
  private static native <T extends MessageDefinition> long nativeCreateSubscriptionHandle(
      long handle, Class<T> messageType, String topic, long qosProfileHandle);

  /**
   * {@inheritDoc}
   */
  public final <T extends MessageDefinition> Publisher<T> createPublisher(
      final Class<T> messageType, final String topic, final QoSProfile qosProfile) {
    long qosProfileHandle = RCLJava.convertQoSProfileToHandle(qosProfile);
    long publisherHandle =
        nativeCreatePublisherHandle(this.handle, messageType, topic, qosProfileHandle);
    RCLJava.disposeQoSProfile(qosProfileHandle);

    Publisher<T> publisher =
        new PublisherImpl<T>(new WeakReference<Node>(this), publisherHandle, topic);
    this.publishers.add(publisher);

    return publisher;
  }

  public final <T extends MessageDefinition> Publisher<T> createPublisher(
      final Class<T> messageType, final String topic) {
    return this.<T>createPublisher(messageType, topic, QoSProfile.DEFAULT);
  }

  /**
   * {@inheritDoc}
   */
  public final <T extends MessageDefinition> Subscription<T> createSubscription(
      final Class<T> messageType, final String topic, final Consumer<T> callback,
      final QoSProfile qosProfile) {
    long qosProfileHandle = RCLJava.convertQoSProfileToHandle(qosProfile);
    long subscriptionHandle =
        nativeCreateSubscriptionHandle(this.handle, messageType, topic, qosProfileHandle);
    RCLJava.disposeQoSProfile(qosProfileHandle);

    Subscription<T> subscription = new SubscriptionImpl<T>(
        new WeakReference<Node>(this), subscriptionHandle, messageType, topic, callback);

    this.subscriptions.add(subscription);

    return subscription;
  }

  public final <T extends MessageDefinition> Subscription<T> createSubscription(
      final Class<T> messageType, final String topic, final Consumer<T> callback) {
    return this.<T>createSubscription(messageType, topic, callback, QoSProfile.DEFAULT);
  }

  /**
   * {@inheritDoc}
   */
  public boolean removeSubscription(final Subscription subscription) {
    return this.subscriptions.remove(subscription);
  }

  /**
   * {@inheritDoc}
   */
  public boolean removePublisher(final Publisher publisher) {
    return this.publishers.remove(publisher);
  }

  /**
   * {@inheritDoc}
   */
  public final Collection<Subscription> getSubscriptions() {
    return this.subscriptions;
  }

  /**
   * {@inheritDoc}
   */
  public final Collection<Publisher> getPublishers() {
    return this.publishers;
  }

  private static native <T extends ServiceDefinition> long nativeCreateServiceHandle(
      long handle, Class<T> cls, String serviceName, long qosProfileHandle);

  public final <T extends ServiceDefinition> Service<T> createService(final Class<T> serviceType,
      final String serviceName,
      final TriConsumer<RMWRequestId, ? extends MessageDefinition, ? extends MessageDefinition>
          callback,
      final QoSProfile qosProfile) throws NoSuchFieldException, IllegalAccessException {
    Class<MessageDefinition> requestType = (Class) serviceType.getField("RequestType").get(null);

    Class<MessageDefinition> responseType = (Class) serviceType.getField("ResponseType").get(null);

    long qosProfileHandle = RCLJava.convertQoSProfileToHandle(qosProfile);
    long serviceHandle =
        nativeCreateServiceHandle(this.handle, serviceType, serviceName, qosProfileHandle);
    RCLJava.disposeQoSProfile(qosProfileHandle);

    Service<T> service = new ServiceImpl<T>(new WeakReference<Node>(this), serviceHandle,
        serviceName, callback, requestType, responseType);
    this.services.add(service);

    return service;
  }

  public <T extends ServiceDefinition> Service<T> createService(final Class<T> serviceType,
      final String serviceName,
      final TriConsumer<RMWRequestId, ? extends MessageDefinition, ? extends MessageDefinition>
          callback) throws NoSuchFieldException, IllegalAccessException {
    return this.<T>createService(serviceType, serviceName, callback, QoSProfile.SERVICES_DEFAULT);
  }

  /**
   * {@inheritDoc}
   */
  public final Collection<Service> getServices() {
    return this.services;
  }

  public final <T extends ServiceDefinition> Client<T> createClient(
      final Class<T> serviceType, final String serviceName, final QoSProfile qosProfile)
      throws NoSuchFieldException, IllegalAccessException {
    Class<MessageDefinition> requestType = (Class) serviceType.getField("RequestType").get(null);

    Class<MessageDefinition> responseType = (Class) serviceType.getField("ResponseType").get(null);

    long qosProfileHandle = RCLJava.convertQoSProfileToHandle(qosProfile);
    long clientHandle =
        nativeCreateClientHandle(this.handle, serviceType, serviceName, qosProfileHandle);
    RCLJava.disposeQoSProfile(qosProfileHandle);

    Client<T> client = new ClientImpl<T>(
        new WeakReference<Node>(this), clientHandle, serviceName, requestType, responseType);
    this.clients.add(client);

    return client;
  }

  public <T extends ServiceDefinition> Client<T> createClient(final Class<T> serviceType,
      final String serviceName) throws NoSuchFieldException, IllegalAccessException {
    return this.<T>createClient(serviceType, serviceName, QoSProfile.SERVICES_DEFAULT);
  }

  private static native <T extends ServiceDefinition> long nativeCreateClientHandle(
      long handle, Class<T> cls, String serviceName, long qosProfileHandle);

  /**
   * {@inheritDoc}
   */
  public boolean removeService(final Service service) {
    return this.services.remove(service);
  }

  /**
   * {@inheritDoc}
   */
  public boolean removeClient(final Client client) {
    return this.clients.remove(client);
  }

  /**
   * {@inheritDoc}
   */
  public final Collection<Client> getClients() {
    return this.clients;
  }

  /**
   * Destroy a ROS2 node (rcl_node_t).
   *
   * @param handle A pointer to the underlying ROS2 node
   *     structure, as an integer. Must not be zero.
   */
  private static native void nativeDispose(long handle);

  private <T extends Disposable> void cleanupDisposables(Collection<T> disposables) {
    for (Disposable disposable : disposables) {
      disposable.dispose();
    }
    disposables.clear();
  }

  private void cleanup() {
    cleanupDisposables(subscriptions);
    cleanupDisposables(publishers);
    cleanupDisposables(timers);
    cleanupDisposables(services);
    cleanupDisposables(clients);
  }

  /**
   * {@inheritDoc}
   */
  public final void dispose() {
    cleanup();
    nativeDispose(this.handle);
    this.handle = 0;
  }

  /**
   * {@inheritDoc}
   */
  public final long getHandle() {
    return this.handle;
  }

  private static native long nativeCreateTimerHandle(long clockHandle, long contextHandle, long timerPeriod);

  public WallTimer createWallTimer(
      final long period, final TimeUnit unit, final Callback callback) {
    long timerPeriodNS = TimeUnit.NANOSECONDS.convert(period, unit);
    long timerHandle = nativeCreateTimerHandle(clock.getHandle(), context.getHandle(), timerPeriodNS);
    WallTimer timer =
        new WallTimerImpl(new WeakReference<Node>(this), timerHandle, callback, timerPeriodNS);
    this.timers.add(timer);
    return timer;
  }

  /**
   * {@inheritDoc}
   */
  public final Collection<Timer> getTimers() {
    return this.timers;
  }

  /**
   * {@inheritDoc}
   */
  public final String getName() {
    return this.name;
  }

  public ParameterVariant declareParameter(ParameterVariant parameter) {
    return declareParameter(parameter, new rcl_interfaces.msg.ParameterDescriptor().setName(parameter.getName()));
  }

  public ParameterVariant declareParameter(ParameterVariant parameter, rcl_interfaces.msg.ParameterDescriptor descriptor) {
    return declareParameters(java.util.Arrays.asList(new ParameterVariant[] {parameter}),
                             java.util.Arrays.asList(new rcl_interfaces.msg.ParameterDescriptor[] {descriptor})).get(0);
  }

  public List<ParameterVariant> declareParameters(List<ParameterVariant> parameters, List<rcl_interfaces.msg.ParameterDescriptor> descriptors) {
    if (parameters.size() != descriptors.size()) {
      throw new IllegalArgumentException("Parameters length must be equal to descriptors length");
    }

    for (ParameterVariant parameter : parameters) {
      if (parameter.getName().length() == 0) {
        throw new InvalidParametersException("Parameter name must not be empty");
      }
    }

    synchronized (parameterCallbacksMutex) {
      for (ParameterCallback cb : this.parameterCallbacks) {
        rcl_interfaces.msg.SetParametersResult result = cb.callback(parameters);
        if (!result.getSuccessful()) {
          throw new InvalidParameterValueException(String.format("Parameters were rejected by callback: %s", result.getReason()));
        }
      }
    }

    List<ParameterVariant> results = new ArrayList<ParameterVariant>();
    Iterator<ParameterVariant> itp = parameters.iterator();
    Iterator<rcl_interfaces.msg.ParameterDescriptor> itpd = descriptors.iterator();
    synchronized (parametersMutex) {
      while (itp.hasNext() && itpd.hasNext()) {
        ParameterVariant parameter = itp.next();
        rcl_interfaces.msg.ParameterDescriptor descriptor = itpd.next();

        if (this.parameters.containsKey(parameter.getName())) {
          throw new ParameterAlreadyDeclaredException(String.format("Parameter '%s' is already declared", parameter.getName()));
        }

        ParameterAndDescriptor pandd = new ParameterAndDescriptor();
        pandd.parameter = parameter;
        pandd.descriptor = descriptor;

        this.parameters.put(parameter.getName(), pandd);
        results.add(parameter);
      }
    }

    return results;
  }

  public void undeclareParameter(String name) {
    synchronized (parametersMutex) {
      if (!this.parameters.containsKey(name)) {
        throw new ParameterNotDeclaredException(String.format("Parameter '%s' is not declared", name));
      }

      this.parameters.remove(name);
    }
  }

  public boolean hasParameter(String name) {
    synchronized (parametersMutex) {
      return this.parameters.containsKey(name);
    }
  }

  public List<ParameterVariant> getParameters(List<String> names) {
    List<ParameterVariant> results = new ArrayList<ParameterVariant>();

    synchronized (parametersMutex) {
      for (String name : names) {
        if (this.parameters.containsKey(name)) {
          results.add(this.parameters.get(name).parameter);
        } else if (this.allowUndeclaredParameters) {
          results.add(new ParameterVariant(name));
        } else {
          throw new ParameterNotDeclaredException(String.format("Parameter '%s' is not declared", name));
        }
      }
    }

    return results;
  }

  public ParameterVariant getParameter(String name) {
    return getParameters(java.util.Arrays.asList(new String[] {name})).get(0);
  }

  public ParameterVariant getParameterOr(String name, ParameterVariant alternateValue) {
    synchronized (parametersMutex) {
      if (this.parameters.containsKey(name)) {
        return this.parameters.get(name).parameter;
      }
    }

    return alternateValue;
  }

  public Map<String, ParameterVariant> getParametersByPrefix(String prefix) {
    String new_prefix;
    if (prefix.length() > 0) {
      new_prefix = prefix.concat(separator);
    } else {
      new_prefix = prefix;
    }

    Map<String, ParameterVariant> results = new HashMap<String, ParameterVariant>();
    synchronized (parametersMutex) {
      for (Map.Entry<String, ParameterAndDescriptor> entry : this.parameters.entrySet()) {
        if (entry.getKey().startsWith(new_prefix)) {
          results.put(entry.getKey().substring(new_prefix.length()), entry.getValue().parameter);
        }
      }
    }

    return results;
  }

  private rcl_interfaces.msg.SetParametersResult internalSetParametersAtomically(
      List<ParameterVariant> parameters) {
    rcl_interfaces.msg.SetParametersResult result = new rcl_interfaces.msg.SetParametersResult();

    for (ParameterVariant parameter : parameters) {
      if (parameter.getName().length() == 0) {
        throw new InvalidParametersException("Parameter name must not be empty");
      }
    }

    synchronized (parameterCallbacksMutex) {
      for (ParameterCallback cb : this.parameterCallbacks) {
        result = cb.callback(parameters);
        if (!result.getSuccessful()) {
          return result;
        }
      }
    }

    // Walk through the list of new parameters, checking if they have been
    // declared.  If they haven't, and we are not allowing undeclared
    // parameters, throw an exception.  If they haven't and we are allowing
    // undeclared parameters, collect the names so we can default initialize
    // them in the parameter list before setting them to the new value.  We
    // do this multi-stage thing so that all of the parameters are set, or
    // none of them.
    List<String> parametersToDeclare = new ArrayList<String>();
    List<String> parametersToUndeclare = new ArrayList<String>();
    for (ParameterVariant parameter : parameters) {
      if (this.parameters.containsKey(parameter.getName())) {
        if (parameter.getType() == ParameterType.PARAMETER_NOT_SET) {
          parametersToUndeclare.add(parameter.getName());
        }
      } else {
        if (this.allowUndeclaredParameters) {
          parametersToDeclare.add(parameter.getName());
        } else {
          throw new ParameterNotDeclaredException(String.format("Parameter '%s' is not declared", parameter.getName()));
        }
      }
    }

    // Check to make sure that a parameter isn't both trying to be declared
    // and undeclared simultaneously.
    for (String name : parametersToDeclare) {
      if (parametersToUndeclare.contains(name)) {
        throw new IllegalArgumentException(String.format("Cannot both declare and undeclare the same parameter name '%s'", name));
      }
    }

    // Undeclare any parameters that have their type set to NOT_SET
    for (String name : parametersToUndeclare) {
      if (!hasParameter(name)) {
        throw new IllegalArgumentException(String.format("Parameter '%s' is not declared", name));
      }

      this.parameters.remove(name);
    }

    for (String name : parametersToDeclare) {
      this.parameters.put(name, new ParameterAndDescriptor());
    }

    for (ParameterVariant parameter : parameters) {
      // We already dealt with unsetting parameters above, so skip them
      if (parameter.getType() == ParameterType.PARAMETER_NOT_SET) {
        continue;
      }
      this.parameters.get(parameter.getName()).parameter = parameter;
    }

    result.setSuccessful(true);
    return result;
  }

  public rcl_interfaces.msg.SetParametersResult setParameter(ParameterVariant parameter) {
    return this.setParametersAtomically(java.util.Arrays.asList(new ParameterVariant[] {parameter}));
  }

  public List<rcl_interfaces.msg.SetParametersResult> setParameters(
      List<ParameterVariant> parameters) {
    List<rcl_interfaces.msg.SetParametersResult> results =
        new ArrayList<rcl_interfaces.msg.SetParametersResult>();

    synchronized (parametersMutex) {
      for (ParameterVariant parameter : parameters) {
        rcl_interfaces.msg.SetParametersResult result =
            this.internalSetParametersAtomically(java.util.Arrays.asList(new ParameterVariant[] {parameter}));
        results.add(result);
      }
    }

    return results;
  }

  public rcl_interfaces.msg.SetParametersResult setParametersAtomically(
      List<ParameterVariant> parameters) {
    synchronized (parametersMutex) {
      return internalSetParametersAtomically(parameters);
    }
  }

  public void addOnSetParametersCallback(ParameterCallback cb) {
    synchronized (parameterCallbacksMutex) {
      this.parameterCallbacks.add(0, cb);
    }
  }

  public void removeOnSetParametersCallback(ParameterCallback cb) {
    synchronized (parameterCallbacksMutex) {
      this.parameterCallbacks.remove(cb);
    }
  }

  public rcl_interfaces.msg.ParameterDescriptor describeParameter(String name) {
    return this.describeParameters(java.util.Arrays.asList(new String[] {name})).get(0);
  }

  public List<rcl_interfaces.msg.ParameterDescriptor> describeParameters(
      List<String> names) {
    List<rcl_interfaces.msg.ParameterDescriptor> results =
        new ArrayList<rcl_interfaces.msg.ParameterDescriptor>();

    synchronized (parametersMutex) {
      for (String name : names) {
        if (this.parameters.containsKey(name)) {
          results.add(this.parameters.get(name).descriptor);
        } else if (this.allowUndeclaredParameters) {
          results.add(new rcl_interfaces.msg.ParameterDescriptor().setName(name));
        } else {
          throw new ParameterNotDeclaredException(String.format("Parameter '%s' is not declared", name));
        }
      }
    }

    return results;
  }

  public List<ParameterType> getParameterTypes(List<String> names) {
    List<ParameterType> results = new ArrayList<ParameterType>();

    synchronized (parametersMutex) {
      for (String name : names) {
        if (this.parameters.containsKey(name)) {
          results.add(this.parameters.get(name).parameter.getType());
        } else if (this.allowUndeclaredParameters) {
          results.add(ParameterType.fromByte(rcl_interfaces.msg.ParameterType.PARAMETER_NOT_SET));
        } else {
          throw new ParameterNotDeclaredException(String.format("Parameter '%s' is not declared", name));
        }
      }
    }

    return results;
  }

  public rcl_interfaces.msg.ListParametersResult listParameters(
      List<String> prefixes, long depth) {
    rcl_interfaces.msg.ListParametersResult result =
        new rcl_interfaces.msg.ListParametersResult();

    synchronized (parametersMutex) {
      for (Map.Entry<String, ParameterAndDescriptor> entry : this.parameters.entrySet()) {
        boolean getAll =
            (prefixes.size() == 0)
            && ((depth == rcl_interfaces.srv.ListParameters_Request.DEPTH_RECURSIVE)
                   || ((entry.getKey().length() - entry.getKey().replace(separator, "").length())
                          < depth));
        boolean prefixMatches = false;
        if (!getAll) {
          for (String prefix : prefixes) {
            if (entry.getKey() == prefix) {
              prefixMatches = true;
              break;
            } else if (entry.getKey().startsWith(prefix + separator)) {
              int length = prefix.length();
              String substr = entry.getKey().substring(length);
              prefixMatches = (depth == rcl_interfaces.srv.ListParameters_Request.DEPTH_RECURSIVE)
                  || ((entry.getKey().length() - entry.getKey().replace(separator, "").length())
                                  < depth);
            }
          }
        }

        if (getAll || prefixMatches) {
          result.getNames().add(entry.getKey());
          int lastSeparator = entry.getKey().lastIndexOf(separator);
          if (-1 != lastSeparator) {
            String prefix = entry.getKey().substring(0, lastSeparator);
            if (!result.getPrefixes().contains(prefix)) {
              result.getPrefixes().add(prefix);
            }
          }
        }
      }
      return result;
    }
  }
}
