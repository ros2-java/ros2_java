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

import org.ros2.rcljava.internal.IService;
import org.ros2.rcljava.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service Server.
 *
 * @param <T> Service Type.
 */
public class Service<T> implements IService {

    private static final Logger logger = LoggerFactory.getLogger(Service.class);

    public static final String SCHEME = "rostopic://";

    /** Name of the service */
    private final String serviceName;

    /** Node owner. */
    private final Node ownerNode;

    /** Service Handler. */
    private final long serviceHandle;

    private final Class<T> serviceType;

    private final TriConsumer<RMWRequestId, ?, ?> callback;

    private final long requestFromJavaConverterHandle;
    private final long requestToJavaConverterHandle;

    private final long responseFromJavaConverterHandle;
    private final long responseToJavaConverterHandle;

    private final Class<?> requestType;
    private final Class<?> responseType;

    /**
     * Constructor.
     *
     * @param nodeHandle
     * @param serviceName
     */
    public Service(
            final Node node,
            final long serviceHandle,
            final Class<T> serviceType,
            final String serviceName,
            final TriConsumer<RMWRequestId, ?, ?> callback,
            final Class<?> requestType,
            final Class<?> responseType,
            final long requestFromJavaConverterHandle,
            final long requestToJavaConverterHandle,
            final long responseFromJavaConverterHandle,
            final long responseToJavaConverterHandle) {

        if (node == null && serviceHandle == 0) {
            throw new RuntimeException("Need to provide active node with handle object");
        }

        Service.logger.debug("Init Service stack : " + serviceName);

        this.ownerNode = node;
        this.serviceHandle = serviceHandle;

        this.serviceType = serviceType;
        this.serviceName = serviceName;
        this.callback = callback;
        this.requestType = requestType;
        this.responseType = responseType;
        this.requestFromJavaConverterHandle = requestFromJavaConverterHandle;
        this.requestToJavaConverterHandle = requestToJavaConverterHandle;
        this.responseFromJavaConverterHandle = responseFromJavaConverterHandle;
        this.responseToJavaConverterHandle = responseToJavaConverterHandle;

        this.ownerNode.getServices().add(this);
    }

    @Override
    public void dispose() {
        Service.logger.debug("Destroy Service stack : " + this.serviceName);
        this.ownerNode.getServices().remove(this);
    }

    public void sendResponse() {

    }

    public String getServiceName() {
        return this.serviceName;
    }

    public final TriConsumer<RMWRequestId, ?, ?> getCallback() {
        return callback;
    }

    public final Class<T> getServiceType() {
        return serviceType;
    }

    public final long getServiceHandle() {
        return this.serviceHandle;
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

    public final Class<?> getRequestType() {
        return this.requestType;
    }

    public final Class<?> getResponseType() {
        return this.responseType;
    }
}
