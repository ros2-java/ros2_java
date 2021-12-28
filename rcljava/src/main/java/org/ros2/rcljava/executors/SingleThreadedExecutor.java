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

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.node.ComposableNode;
import org.ros2.rcljava.executors.BaseExecutor;

public class SingleThreadedExecutor implements Executor {
  private BaseExecutor baseExecutor = new BaseExecutor();

  public void addNode(ComposableNode node) {
    this.baseExecutor.addNode(node);
  }

  public void removeNode(ComposableNode node) {
    this.baseExecutor.removeNode(node);
  }

  public void spinOnce() {
    this.spinOnce(-1);
  }

  public void spinOnce(long timeout) {
    this.baseExecutor.spinOnce(timeout);
  }

  public void spinSome() {
    this.spinSome(0);
  }

  public void spinSome(long maxDurationNs) {
    this.baseExecutor.spinSome(maxDurationNs);
  }

  public void spinAll(long maxDurationNs) {
    this.baseExecutor.spinAll(maxDurationNs);
  }

  public void spin() {
    while (RCLJava.ok()) {
      this.spinOnce();
    }
  }
}
