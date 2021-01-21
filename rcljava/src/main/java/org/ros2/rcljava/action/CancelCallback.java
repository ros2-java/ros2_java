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

public interface CancelCallback<T extends ActionDefinition> {
  enum CancelResponse {
    REJECT,
    ACCEPT,
  };

  /**
   * Called when a new cancel request is received.
   *
   * @param goalHandle The goal handle.
   * @return Cancel response indicating if the cancel request was accepted or not.
   */
  CancelResponse handleCancel(ActionServerGoalHandle<T> goalHandle);
}
