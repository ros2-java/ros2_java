/* Copyright 2017-2018 Esteve Fernandez <esteve@apache.org>
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

package org.ros2.rcljava.executors;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.client.Client;
import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.executors.AnyExecutable;
import org.ros2.rcljava.executors.Executor;
import org.ros2.rcljava.interfaces.MessageDefinition;
import org.ros2.rcljava.interfaces.ServiceDefinition;
import org.ros2.rcljava.node.ComposableNode;
import org.ros2.rcljava.service.RMWRequestId;
import org.ros2.rcljava.service.Service;
import org.ros2.rcljava.subscription.Subscription;
import org.ros2.rcljava.timer.Timer;

public class BaseExecutor {
  private static final Logger logger = LoggerFactory.getLogger(BaseExecutor.class);

  static {
    try {
      JNIUtils.loadImplementation(BaseExecutor.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  private BlockingQueue<ComposableNode> nodes = new LinkedBlockingQueue<ComposableNode>();

  private List<Map.Entry<Long, Subscription>> subscriptionHandles =
      new ArrayList<Map.Entry<Long, Subscription>>();

  private List<Map.Entry<Long, Timer>> timerHandles = new ArrayList<Map.Entry<Long, Timer>>();

  private List<Map.Entry<Long, Service>> serviceHandles = new ArrayList<Map.Entry<Long, Service>>();

  private List<Map.Entry<Long, Client>> clientHandles = new ArrayList<Map.Entry<Long, Client>>();

  protected void addNode(ComposableNode node) {
    this.nodes.add(node);
  }

  protected void removeNode(ComposableNode node) {
    this.nodes.remove(node);
  }

  protected void executeAnyExecutable(AnyExecutable anyExecutable) {
    if (anyExecutable.timer != null) {
      anyExecutable.timer.callTimer();
      anyExecutable.timer.executeCallback();
      timerHandles.remove(anyExecutable.timer.getHandle());
    }

    if (anyExecutable.subscription != null) {
      MessageDefinition message = nativeTake(
          anyExecutable.subscription.getHandle(), anyExecutable.subscription.getMessageType());
      if (message != null) {
        anyExecutable.subscription.executeCallback(message);
      }
      subscriptionHandles.remove(anyExecutable.subscription.getHandle());
    }

    if (anyExecutable.service != null) {
      Class<MessageDefinition> requestType = anyExecutable.service.getRequestType();
      Class<MessageDefinition> responseType = anyExecutable.service.getResponseType();

      MessageDefinition requestMessage = null;
      MessageDefinition responseMessage = null;

      try {
        requestMessage = requestType.newInstance();
        responseMessage = responseType.newInstance();
      } catch (InstantiationException ie) {
        ie.printStackTrace();
      } catch (IllegalAccessException iae) {
        iae.printStackTrace();
      }

      if (requestMessage != null && responseMessage != null) {
        long requestFromJavaConverterHandle = requestMessage.getFromJavaConverterInstance();
        long requestToJavaConverterHandle = requestMessage.getToJavaConverterInstance();
        long requestDestructorHandle = requestMessage.getDestructorInstance();
        long responseFromJavaConverterHandle = responseMessage.getFromJavaConverterInstance();
        long responseToJavaConverterHandle = responseMessage.getToJavaConverterInstance();
        long responseDestructorHandle = responseMessage.getDestructorInstance();

        RMWRequestId rmwRequestId =
            nativeTakeRequest(anyExecutable.service.getHandle(), requestFromJavaConverterHandle,
                requestToJavaConverterHandle, requestDestructorHandle, requestMessage);
        if (rmwRequestId != null) {
          anyExecutable.service.executeCallback(rmwRequestId, requestMessage, responseMessage);
          nativeSendServiceResponse(anyExecutable.service.getHandle(), rmwRequestId,
              responseFromJavaConverterHandle, responseToJavaConverterHandle,
              responseDestructorHandle, responseMessage);
        }
      }
      serviceHandles.remove(anyExecutable.service.getHandle());
    }

    if (anyExecutable.client != null) {
      Class<MessageDefinition> requestType = anyExecutable.client.getRequestType();
      Class<MessageDefinition> responseType = anyExecutable.client.getResponseType();

      MessageDefinition requestMessage = null;
      MessageDefinition responseMessage = null;

      try {
        requestMessage = requestType.newInstance();
        responseMessage = responseType.newInstance();
      } catch (InstantiationException ie) {
        ie.printStackTrace();
      } catch (IllegalAccessException iae) {
        iae.printStackTrace();
      }

      if (requestMessage != null && responseMessage != null) {
        long requestFromJavaConverterHandle = requestMessage.getFromJavaConverterInstance();
        long requestToJavaConverterHandle = requestMessage.getToJavaConverterInstance();
        long responseFromJavaConverterHandle = responseMessage.getFromJavaConverterInstance();
        long responseToJavaConverterHandle = responseMessage.getToJavaConverterInstance();
        long responseDestructorHandle = responseMessage.getDestructorInstance();

        RMWRequestId rmwRequestId =
            nativeTakeResponse(anyExecutable.client.getHandle(), responseFromJavaConverterHandle,
                responseToJavaConverterHandle, responseDestructorHandle, responseMessage);

        if (rmwRequestId != null) {
          anyExecutable.client.handleResponse(rmwRequestId, responseMessage);
        }
      }
      clientHandles.remove(anyExecutable.client.getHandle());
    }
  }

  protected void waitForWork(long timeout) {
    this.subscriptionHandles.clear();
    this.timerHandles.clear();
    this.serviceHandles.clear();
    this.clientHandles.clear();

    for (ComposableNode node : this.nodes) {
      for (Subscription<MessageDefinition> subscription : node.getNode().getSubscriptions()) {
        this.subscriptionHandles.add(new AbstractMap.SimpleEntry<Long, Subscription>(
            subscription.getHandle(), subscription));
      }

      for (Timer timer : node.getNode().getTimers()) {
        this.timerHandles.add(new AbstractMap.SimpleEntry<Long, Timer>(timer.getHandle(), timer));
      }

      for (Service<ServiceDefinition> service : node.getNode().getServices()) {
        this.serviceHandles.add(
            new AbstractMap.SimpleEntry<Long, Service>(service.getHandle(), service));
      }

      for (Client<ServiceDefinition> client : node.getNode().getClients()) {
        this.clientHandles.add(
            new AbstractMap.SimpleEntry<Long, Client>(client.getHandle(), client));
      }
    }

    int subscriptionsSize = 0;
    int timersSize = 0;
    int clientsSize = 0;
    int servicesSize = 0;

    for (ComposableNode node : this.nodes) {
      subscriptionsSize += node.getNode().getSubscriptions().size();
      timersSize += node.getNode().getTimers().size();
      clientsSize += node.getNode().getClients().size();
      servicesSize += node.getNode().getServices().size();
    }

    if (subscriptionsSize == 0 && timersSize == 0 && clientsSize == 0 && servicesSize == 0) {
      return;
    }

    long waitSetHandle = nativeGetZeroInitializedWaitSet();
    long contextHandle = RCLJava.getDefaultContext().getHandle();
    nativeWaitSetInit(waitSetHandle, contextHandle, subscriptionsSize, 0, timersSize, clientsSize, servicesSize, 0);

    nativeWaitSetClear(waitSetHandle);

    for (Map.Entry<Long, Subscription> entry : this.subscriptionHandles) {
      nativeWaitSetAddSubscription(waitSetHandle, entry.getKey());
    }

    for (Map.Entry<Long, Timer> entry : this.timerHandles) {
      nativeWaitSetAddTimer(waitSetHandle, entry.getKey());
    }

    for (Map.Entry<Long, Service> entry : this.serviceHandles) {
      nativeWaitSetAddService(waitSetHandle, entry.getKey());
    }

    for (Map.Entry<Long, Client> entry : this.clientHandles) {
      nativeWaitSetAddClient(waitSetHandle, entry.getKey());
    }

    nativeWait(waitSetHandle, timeout);

    for (int i = 0; i < this.subscriptionHandles.size(); ++i) {
      if (!nativeWaitSetSubscriptionIsReady(waitSetHandle, i)) {
        this.subscriptionHandles.get(i).setValue(null);
      }
    }

    for (int i = 0; i < this.timerHandles.size(); ++i) {
      if (!nativeWaitSetTimerIsReady(waitSetHandle, i)) {
        this.timerHandles.get(i).setValue(null);
      }
    }

    for (int i = 0; i < this.serviceHandles.size(); ++i) {
      if (!nativeWaitSetServiceIsReady(waitSetHandle, i)) {
        this.serviceHandles.get(i).setValue(null);
      }
    }

    for (int i = 0; i < this.clientHandles.size(); ++i) {
      if (!nativeWaitSetClientIsReady(waitSetHandle, i)) {
        this.clientHandles.get(i).setValue(null);
      }
    }

    Iterator<Map.Entry<Long, Subscription>> subscriptionIterator =
        this.subscriptionHandles.iterator();
    while (subscriptionIterator.hasNext()) {
      Map.Entry<Long, Subscription> entry = subscriptionIterator.next();
      if (entry.getValue() == null) {
        subscriptionIterator.remove();
      }
    }

    Iterator<Map.Entry<Long, Timer>> timerIterator = this.timerHandles.iterator();
    while (timerIterator.hasNext()) {
      Map.Entry<Long, Timer> entry = timerIterator.next();
      if (entry.getValue() == null) {
        timerIterator.remove();
      }
    }

    Iterator<Map.Entry<Long, Service>> serviceIterator = this.serviceHandles.iterator();
    while (serviceIterator.hasNext()) {
      Map.Entry<Long, Service> entry = serviceIterator.next();
      if (entry.getValue() == null) {
        serviceIterator.remove();
      }
    }

    Iterator<Map.Entry<Long, Client>> clientIterator = this.clientHandles.iterator();
    while (clientIterator.hasNext()) {
      Map.Entry<Long, Client> entry = clientIterator.next();
      if (entry.getValue() == null) {
        clientIterator.remove();
      }
    }

    nativeDisposeWaitSet(waitSetHandle);
  }

  protected AnyExecutable getNextExecutable() {
    AnyExecutable anyExecutable = new AnyExecutable();

    for (Map.Entry<Long, Timer> entry : this.timerHandles) {
      if (entry.getValue() != null) {
        Timer timer = entry.getValue();
        if (timer.isReady()) {
          anyExecutable.timer = timer;
          entry.setValue(null);
          return anyExecutable;
        }
      }
    }

    for (Map.Entry<Long, Subscription> entry : this.subscriptionHandles) {
      if (entry.getValue() != null) {
        anyExecutable.subscription = entry.getValue();
        entry.setValue(null);
        return anyExecutable;
      }
    }

    for (Map.Entry<Long, Service> entry : this.serviceHandles) {
      if (entry.getValue() != null) {
        anyExecutable.service = entry.getValue();
        entry.setValue(null);
        return anyExecutable;
      }
    }

    for (Map.Entry<Long, Client> entry : this.clientHandles) {
      if (entry.getValue() != null) {
        anyExecutable.client = entry.getValue();
        entry.setValue(null);
        return anyExecutable;
      }
    }

    return null;
  }

  protected void spinSome() {
    AnyExecutable anyExecutable = getNextExecutable();
    if (anyExecutable == null) {
      waitForWork(0);
      do {
        anyExecutable = getNextExecutable();
        if (anyExecutable != null) {
          executeAnyExecutable(anyExecutable);
        }
      } while (anyExecutable != null);
    }
  }

  protected void spinOnce(long timeout) {
    AnyExecutable anyExecutable = getNextExecutable();
    if (anyExecutable == null) {
      waitForWork(timeout);
      anyExecutable = getNextExecutable();
    }

    if (anyExecutable != null) {
      executeAnyExecutable(anyExecutable);
    }
  }

  private static native void nativeDisposeWaitSet(long waitSetHandle);

  private static native long nativeGetZeroInitializedWaitSet();

  private static native void nativeWaitSetInit(
      long waitSetHandle, long contextHandle, int numberOfSubscriptions,
      int numberOfGuardConditions, int numberOfTimers, int numberOfClients,
      int numberOfServices, int numberOfEvents);

  private static native void nativeWaitSetClear(long waitSetHandle);

  private static native void nativeWaitSetAddSubscription(
      long waitSetHandle, long subscriptionHandle);

  private static native void nativeWait(long waitSetHandle, long timeout);

  private static native MessageDefinition nativeTake(
      long subscriptionHandle, Class<MessageDefinition> messageType);

  private static native void nativeWaitSetAddService(long waitSetHandle, long serviceHandle);

  private static native void nativeWaitSetAddClient(long waitSetHandle, long clientHandle);

  private static native void nativeWaitSetAddTimer(long waitSetHandle, long timerHandle);

  private static native RMWRequestId nativeTakeRequest(long serviceHandle,
      long requestFromJavaConverterHandle, long requestToJavaConverterHandle,
      long requestDestructorHandle, MessageDefinition requestMessage);

  private static native void nativeSendServiceResponse(long serviceHandle, RMWRequestId header,
      long responseFromJavaConverterHandle, long responseToJavaConverterHandle,
      long responseDestructorHandle, MessageDefinition responseMessage);

  private static native RMWRequestId nativeTakeResponse(long clientHandle,
      long responseFromJavaConverterHandle, long responseToJavaConverterHandle,
      long responseDestructorHandle, MessageDefinition responseMessage);

  private static native boolean nativeWaitSetSubscriptionIsReady(long waitSetHandle, long index);

  private static native boolean nativeWaitSetTimerIsReady(long waitSetHandle, long index);

  private static native boolean nativeWaitSetServiceIsReady(long waitSetHandle, long index);

  private static native boolean nativeWaitSetClientIsReady(long waitSetHandle, long index);
}
