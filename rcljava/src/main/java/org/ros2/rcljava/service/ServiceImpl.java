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

package org.ros2.rcljava.service;

import java.lang.ref.WeakReference;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.consumers.TriConsumer;
import org.ros2.rcljava.interfaces.MessageDefinition;
import org.ros2.rcljava.interfaces.ServiceDefinition;
import org.ros2.rcljava.node.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceImpl<T extends ServiceDefinition> implements Service<T> {
  private static final Logger logger = LoggerFactory.getLogger(ServiceImpl.class);

  static {
    try {
      JNIUtils.loadImplementation(ServiceImpl.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  private final WeakReference<Node> nodeReference;
  private long handle;
  private final String serviceName;
  private final TriConsumer<RMWRequestId, ? extends MessageDefinition, ? extends MessageDefinition>
      callback;

  private final Class<MessageDefinition> requestType;
  private final Class<MessageDefinition> responseType;

  public ServiceImpl(final WeakReference<Node> nodeReference, final long handle,
      final String serviceName,
      final TriConsumer<RMWRequestId, ? extends MessageDefinition, ? extends MessageDefinition>
          callback,
      final Class<MessageDefinition> requestType, final Class<MessageDefinition> responseType) {
    this.nodeReference = nodeReference;
    this.handle = handle;
    this.serviceName = serviceName;
    this.callback = callback;
    this.requestType = requestType;
    this.responseType = responseType;
  }

  public final Class<MessageDefinition> getRequestType() {
    return this.requestType;
  }

  public final Class<MessageDefinition> getResponseType() {
    return this.responseType;
  }

  /**
   * Destroy a ROS2 service (rcl_service_t).
   *
   * @param nodeHandle A pointer to the underlying ROS2 node structure that
   *     created this service, as an integer. Must not be zero.
   * @param handle A pointer to the underlying ROS2 service
   *     structure, as an integer. Must not be zero.
   */
  private static native void nativeDispose(long nodeHandle, long handle);

  /**
   * {@inheritDoc}
   */
  public final void dispose() {
    Node node = this.nodeReference.get();
    if (node != null) {
      node.removeService(this);
      nativeDispose(node.getHandle(), this.handle);
      this.handle = 0;
    }
  }

  /**
   * {@inheritDoc}
   */
  public final long getHandle() {
    return handle;
  }

  public void executeCallback(
      RMWRequestId rmwRequestId, MessageDefinition request, MessageDefinition response) {
    TriConsumer<RMWRequestId, MessageDefinition, MessageDefinition> callback =
        ((ServiceImpl) this).callback;

    callback.accept(rmwRequestId, request, response);
  }

  public String getServiceName() {
    return this.serviceName;
  }
}
