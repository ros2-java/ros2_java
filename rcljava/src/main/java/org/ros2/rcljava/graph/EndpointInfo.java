/* Copyright 2020 Open Source Robotics Foundation, Inc.
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

package org.ros2.rcljava.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.qos.QoSProfile;

/**
 * Class that represents queried information of an endpoint.
 */
public class EndpointInfo {
  /// Name of the node that created the endpoint.
  public String nodeName;
  /// Namespace of the node that created the endpoint.
  public String nodeNamespace;
  /// Topic type.
  public String topicType;
  /// Kind of endpoint, i.e.: publisher or subscription.
  public EndpointType endpointType;
  /// Gid of the endpoint.
  public byte[] endpointGID;
  /// Quality of service of the endpoint.
  public QoSProfile qos;

  public enum EndpointType {
    INVALID,
    PUBLISHER,
    SUBSCRIPTION;
  }

  private native final void nativeFromRCL(long handle);

  private static final Logger logger = LoggerFactory.getLogger(EndpointInfo.class);
  static {
    try {
      JNIUtils.loadImplementation(EndpointInfo.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }
}
