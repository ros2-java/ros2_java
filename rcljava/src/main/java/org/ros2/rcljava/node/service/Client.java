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
package org.ros2.rcljava.node.service;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.exception.NotImplementedException;
import org.ros2.rcljava.internal.IClient;
import org.ros2.rcljava.node.Node;

/**
 * Service Client.
 *
 * @param <T> Service Type.
 */
public class Client<T extends org.ros2.rcljava.internal.service.Service> implements IClient {

    // Loading JNI library.
    static {
        RCLJava.loadLibrary("rcljavanode_service_Client__" + RCLJava.getRMWIdentifier());
    }

    private static native void nativeSendClientRequest(
            long clientHandle,
            long sequenceNumber,
            long requestFromJavaConverterHandle,
            long requestToJavaConverterHandle,
            Object requestMessage);


    private final WeakReference<Node> nodeReference;

    /** Client Handler. */
    private final long clientHandle;

    private final Class<T> serviceType;
    private final String serviceName;
    private long sequenceNumber = 0;
    private Map<Long, RCLFuture<?>> pendingRequests;

    private long requestFromJavaConverterHandle = 0;
    private long requestToJavaConverterHandle = 0;

    private long responseFromJavaConverterHandle = 0;
    private long responseToJavaConverterHandle = 0;

    private final Class<?> requestType;
    private final Class<?> responseType;

    /**
     * Constructor.
     *
     */
    public Client(
            final WeakReference<Node> nodeReference,
            final long clientHandle,
            final Class<T> serviceType,
            final String serviceName,
            final Class<?> requestType,
            final Class<?> responseType,
            final long requestFromJavaConverterHandle,
            final long requestToJavaConverterHandle,
            final long responseFromJavaConverterHandle,
            final long responseToJavaConverterHandle) {

        if (nodeReference == null && clientHandle == 0) {
            throw new RuntimeException("Need to provide active node with handle object");
        }

        this.nodeReference = nodeReference;
        this.clientHandle = clientHandle;

        this.serviceType = serviceType;
        this.serviceName = serviceName;
        this.requestType = requestType;
        this.responseType = responseType;
        this.requestFromJavaConverterHandle = requestFromJavaConverterHandle;
        this.requestToJavaConverterHandle = requestToJavaConverterHandle;
        this.responseFromJavaConverterHandle = responseFromJavaConverterHandle;
        this.responseToJavaConverterHandle = responseToJavaConverterHandle;
        this.pendingRequests = new HashMap<Long, RCLFuture<?>>();

        this.nodeReference.get().getClients().add(this);
    }

    @Override
    public void dispose() {
        this.nodeReference.get().getClients().remove(this);
    }

    @Override
    public final <U extends org.ros2.rcljava.internal.message.Message, V extends org.ros2.rcljava.internal.message.Message> Future<V> sendRequest(final U request) {
        synchronized(this.pendingRequests) {
              this.sequenceNumber++;
              Client.nativeSendClientRequest(
                      this.clientHandle,
                      this.sequenceNumber,
                      this.requestFromJavaConverterHandle,
                      this.requestToJavaConverterHandle,
                      request);
              RCLFuture<V> future = new RCLFuture<V>(this.nodeReference);
              pendingRequests.put(sequenceNumber, future);
              return future;
            }
    }

    @SuppressWarnings("unchecked")
    public final <V extends org.ros2.rcljava.internal.message.Message> void handleResponse(final RMWRequestId header,final V response) {
        synchronized(pendingRequests) {
            long sequenceNumber = header.sequenceNumber;
            RCLFuture<V> future = (RCLFuture<V>) pendingRequests.remove(sequenceNumber);
            future.set(response);
        }
    }

    public final Class<?> getRequestType() {
        return this.requestType;
    }

    public final Class<?> getResponseType() {
        return this.responseType;
    }

    public final String getServiceName() {
        return this.serviceName;
    }

    public final Class<T> getServiceType() {
        return this.serviceType;
    }

    public final long getClientHandle() {
        return this.clientHandle;
    }

    public final long getRequestFromJavaConverterHandle() {
        return this.requestFromJavaConverterHandle;
    }

    public final long getRequestToJavaConverterHandle() {
        return this.requestToJavaConverterHandle;
    }

    public final long getResponseFromJavaConverterHandle() {
        return this.responseFromJavaConverterHandle;
    }

    public final long getResponseToJavaConverterHandle() {
        return this.responseToJavaConverterHandle;
    }

    public final boolean waitForService(final int i) {
      //TODO
        throw new NotImplementedException();
//        return false;
    }
}
