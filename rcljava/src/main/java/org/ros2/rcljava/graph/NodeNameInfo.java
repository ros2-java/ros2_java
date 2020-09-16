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

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ros2.rcljava.common.JNIUtils;

/**
 * Class that represents the queried information of a node in the graph.
 */
public class NodeNameInfo {
  /// The node name
  public String name;
  /// The node namespace
  public String namespace;
  /// The security enclave of the node.
  /**
   * For further details, see:
   * @{link http://design.ros2.org/articles/ros2_security_enclaves.html}
   */
  public String enclave;

  /// Constructor
  public NodeNameInfo(final String name, final String namespace, final String enclave) {
    this.name = name;
    this.namespace = namespace;
    this.enclave = enclave;
  }

  /// Default constructor, used from jni.
  private NodeNameInfo() {}

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof NodeNameInfo)) {
      return false;
    }
    NodeNameInfo other = (NodeNameInfo) o;
    return Objects.equals(this.name, other.name) &&
      Objects.equals(this.namespace, other.namespace) &&
      Objects.equals(this.enclave, other.enclave);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.namespace, this.enclave);
  }
}
