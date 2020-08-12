// Copyright 2020 Open Source Robotics Foundation, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.ros2.rcljava.events;

import java.lang.ref.WeakReference;

import org.ros2.rcljava.interfaces.Disposable;
import org.ros2.rcljava.events.EventStatus;

/**
 * This class serves as a bridge between a rcl_event_t and RCLJava.
 * An EventHandler must be created via
 * @{link Publisher#createEventHandler(Class&lt;T&gt;, Consumer&lt;T&gt;)}
 * @{link Subscription#createEventHandler(Class&lt;T&gt;, Consumer&lt;T&gt;)}
 *
 * @param <T> The event status type.
 * @param <ParentT> The parent class type.
 */
public interface EventHandler<T extends EventStatus, ParentT extends Disposable> extends Disposable {
  /**
   * @return The event status type.
   */
  Class<T> getEventStatusType();

  /**
   * @return The parent entity type.
   */
  Class<ParentT> getParentType();

  /**
   * @return A weak reference to the parent.
   */
  WeakReference<ParentT> getParentReference();

  /**
   * @return Execute the registered event callback
   */
  void executeCallback();
}
