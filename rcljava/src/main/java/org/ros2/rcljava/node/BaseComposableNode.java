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

package org.ros2.rcljava.node;

import org.ros2.rcljava.RCLJava;

public class BaseComposableNode implements ComposableNode {
  private final String name;

  protected final Node node;

  public BaseComposableNode(String name) {
    this.name = name;
    node = RCLJava.createNode(this.name);
  }

  public BaseComposableNode(String name, String namespace) {
    this.name = name;
    node = RCLJava.createNode(name, namespace);
  }

  public Node getNode() {
    return node;
  }
}
