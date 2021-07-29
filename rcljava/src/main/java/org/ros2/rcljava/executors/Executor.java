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

import java.util.concurrent.Future;

import org.ros2.rcljava.node.ComposableNode;

public interface Executor {
  public void addNode(ComposableNode node);

  public void removeNode(ComposableNode node);

  public void spinOnce();

  public void spinOnce(long timeout);

  public void spinUntilComplete(Future future, long maxDurationNs);

  public void spinUntilComplete(Future future);

  public void spinSome();

  public void spinSome(long maxDurationNs);

  public void spinAll(long maxDurationNs);

  public void spin();
}
