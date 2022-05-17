/* Copyright 2020 ros2-java contributors
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

package org.ros2.rcljava.action;

import org.ros2.rcljava.interfaces.ActionDefinition;
import org.ros2.rcljava.interfaces.Disposable;
import org.ros2.rcljava.interfaces.MessageDefinition;

public interface ActionServerGoalHandle<T extends ActionDefinition> extends Disposable {
  /**
   * Get the message containing the timestamp and ID for the goal.
   */
  public action_msgs.msg.GoalInfo getGoalInfo();

  /**
   * Get the goal message.
   */
  public MessageDefinition getGoal();

  /**
   * Get the goal status.
   */
  public GoalStatus getGoalStatus();

  /**
   * Returns true if the goal is in the CANCELING state.
   */
  public boolean isCanceling();

  /**
   * Transition the goal to the EXECUTING state.
   *
   * Pre-condition: the goal must be in the ACCEPTED state.
   */
  public void execute();

  /**
   * Transition the goal to the SUCCEEDED state.
   *
   * Pre-condition: the goal must be in the EXECUTING or CANCELING state.
   */
  public void succeed();

  /**
   * Transition the goal the the CANCELED state.
   *
   * Pre-condition: the goal must be in the CANCELING state.
   */
  public void canceled();

  /**
   * Transition the goal the the CANCELED state.
   *
   * Pre-condition: the goal must be in the EXCUTING or CANCELING state.
   */
  public void abort();
}
