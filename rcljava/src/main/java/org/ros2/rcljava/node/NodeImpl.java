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

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collection;
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

  private Object mutex;

  private Map<String, ParameterVariant> parameters;

  /**
   * Constructor.
   *
   * @param handle A pointer to the underlying ROS2 node structure. Must not
   *     be zero.
   */
  public NodeImpl(final long handle, final String name, final Context context) {
    this.clock = new Clock(ClockType.SYSTEM_TIME);
    this.handle = handle;
    this.name = name;
    this.context = context;
    this.publishers = new LinkedBlockingQueue<Publisher>();
    this.subscriptions = new LinkedBlockingQueue<Subscription>();
    this.services = new LinkedBlockingQueue<Service>();
    this.clients = new LinkedBlockingQueue<Client>();
    this.timers = new LinkedBlockingQueue<Timer>();
    this.mutex = new Object();
    this.parameters = new ConcurrentHashMap<String, ParameterVariant>();
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

  public List<ParameterVariant> getParameters(List<String> names) {
    synchronized (mutex) {
      List<ParameterVariant> results = new ArrayList<ParameterVariant>();
      for (String name : names) {
        for (Map.Entry<String, ParameterVariant> entry : this.parameters.entrySet()) {
          if (name.equals(entry.getKey())) {
            results.add(entry.getValue());
          }
        }
      }
      return results;
    }
  }

  public List<ParameterType> getParameterTypes(List<String> names) {
    synchronized (mutex) {
      List<ParameterType> results = new ArrayList<ParameterType>();
      for (String name : names) {
        for (Map.Entry<String, ParameterVariant> entry : this.parameters.entrySet()) {
          if (name.equals(entry.getKey())) {
            results.add(entry.getValue().getType());
          } else {
            results.add(ParameterType.fromByte(rcl_interfaces.msg.ParameterType.PARAMETER_NOT_SET));
          }
        }
      }
      return results;
    }
  }

  public List<rcl_interfaces.msg.SetParametersResult> setParameters(
      List<ParameterVariant> parameters) {
    List<rcl_interfaces.msg.SetParametersResult> results =
        new ArrayList<rcl_interfaces.msg.SetParametersResult>();
    for (ParameterVariant p : parameters) {
      rcl_interfaces.msg.SetParametersResult result =
          this.setParametersAtomically(java.util.Arrays.asList(new ParameterVariant[] {p}));
      results.add(result);
    }
    return results;
  }

  public rcl_interfaces.msg.SetParametersResult setParametersAtomically(
      List<ParameterVariant> parameters) {
    synchronized (mutex) {
      rcl_interfaces.msg.SetParametersResult result = new rcl_interfaces.msg.SetParametersResult();
      for (ParameterVariant p : parameters) {
        this.parameters.put(p.getName(), p);
      }
      result.setSuccessful(true);
      return result;
    }
  }

  public List<rcl_interfaces.msg.ParameterDescriptor> describeParameters(
      List<String> names) {
    synchronized (mutex) {
      List<rcl_interfaces.msg.ParameterDescriptor> results =
          new ArrayList<rcl_interfaces.msg.ParameterDescriptor>();
      for (String name : names) {
        for (Map.Entry<String, ParameterVariant> entry : this.parameters.entrySet()) {
          if (name.equals(entry.getKey())) {
            rcl_interfaces.msg.ParameterDescriptor parameterDescriptor =
                new rcl_interfaces.msg.ParameterDescriptor();
            parameterDescriptor.setName(entry.getKey());
            parameterDescriptor.setType(entry.getValue().getType().getValue());
            results.add(parameterDescriptor);
          }
        }
      }
      return results;
    }
  }

  public rcl_interfaces.msg.ListParametersResult listParameters(
      List<String> prefixes, long depth) {
    synchronized (mutex) {
      rcl_interfaces.msg.ListParametersResult result =
          new rcl_interfaces.msg.ListParametersResult();

      String separator = ".";
      for (Map.Entry<String, ParameterVariant> entry : this.parameters.entrySet()) {
        boolean getAll =
            (prefixes.size() == 0)
            && ((depth == rcl_interfaces.srv.ListParameters_Request.DEPTH_RECURSIVE)
                   || ((entry.getKey().length() - entry.getKey().replace(separator, "").length())
                          < depth));
        boolean prefixMatches = false;
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
