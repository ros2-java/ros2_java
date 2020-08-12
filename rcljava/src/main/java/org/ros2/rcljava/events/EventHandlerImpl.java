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
import java.lang.reflect.InvocationTargetException;

import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.events.EventHandler;
import org.ros2.rcljava.events.EventStatus;
import org.ros2.rcljava.interfaces.Disposable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class serves as a bridge between a rcl_event_t and RCLJava.
 * An EventHandler must be created via
 * @{link Publisher#createEventHandler(Class&lt;T&gt;, Consumer&lt;T&gt;)}
 * @{link Subscription#createEventHandler(Class&lt;T&gt;, Consumer&lt;T&gt;)}
 *
 * @param <T> The status event type.
 * @param <ParentT> The parent class type.
 */
public class EventHandlerImpl<
    T extends EventStatus,
    ParentT extends Disposable>
implements EventHandler<T, ParentT> {
  private static final Logger logger = LoggerFactory.getLogger(EventHandlerImpl.class);

  static {
    try {
      JNIUtils.loadImplementation(EventHandlerImpl.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  /**
   * Constructor.
   *
   * @param parentType The <code>Class</code> type of the parent.
   *    It can be either a @{link org.ros2.rcljava.Publisher} or a
   *    @{link org.ros2.rcljava.Subscription} class.
   * @param parentReference A {@link java.lang.ref.WeakReference} to the
   *     @{link org.ros2.rcljava.Publisher} or @{link org.ros2.rcljava.Subscription}
   *     that created this event handler.
   * @param handle A pointer to the underlying ROS 2 event structure, as an integer.
   *     Must not be zero.
   * @param eventStatusType The <code>Class</code> of the messages that this
   *     subscription will receive. We need this because of Java's type erasure,
   *     which doesn't allow us to use the generic parameter of
   *     @{link org.ros2.rcljava.Subscription} directly.
   * @param callback The callback function that will be called when the event
   *     is triggered.
   */
  public EventHandlerImpl(
      final Class<ParentT> parentType,
      final WeakReference<ParentT> parentReference,
      final long handle,
      final Class<T> eventStatusType,
      final Consumer<T> callback) {
    this.parentType = parentType;
    this.parentReference = parentReference;
    this.handle = handle;
    this.eventStatusType = eventStatusType;
    this.callback = callback;
  }

  /**
   * {@inheritDoc}
   */
  public final Class<T> getEventStatusType() {
    return this.eventStatusType;
  }

  /**
   * {@inheritDoc}
   */
  public final Class<ParentT> getParentType() {
    return this.parentType;
  }

  /**
   * {@inheritDoc}
   */
  public final WeakReference<ParentT> getParentReference() {
    return this.parentReference;
  }

  /**
   * {@inheritDoc}
   */
  public final long getHandle() {
    return this.handle;
  }

  /**
   * Destroy a ROS 2 event (rcl_event_t).
   *
   * @param handle A pointer to the underlying ROS 2 event structure,
   *     as an integer. Must not be zero.
   */
  private static native void nativeDispose(long handle);

  /**
   * {@inheritDoc}
   */
  public final void dispose() {
    nativeDispose(this.handle);
    this.handle = 0;
  }

  /**
   * Takes the RCL event status and returns a pointer to it.
   *
   * @param event_handle A pointer to the underlying ROS 2 event (rcl_event_t).
   * @param event_status_handle A pointer to the underlying ROS 2 event status (void *).
   *     Must not be zero.
   */
  private static native void nativeTake(long event_handle, long event_status_handle);

  /**
   * {@inheritDoc}
   */
  public final void executeCallback() {
    T eventStatus = null;
    try {
        eventStatus = this.eventStatusType.getDeclaredConstructor().newInstance();
    } catch (NoSuchMethodException nme) {
        nme.printStackTrace();
    } catch (InvocationTargetException ite) {
        ite.printStackTrace();
    } catch (InstantiationException ie) {
        ie.printStackTrace();
    } catch (IllegalAccessException iae) {
        iae.printStackTrace();
    }
    long nativeEventStatusHandle = eventStatus.allocateRCLStatusEvent();
    nativeTake(this.handle, nativeEventStatusHandle);
    eventStatus.fromRCLEvent(nativeEventStatusHandle);
    eventStatus.deallocateRCLStatusEvent(nativeEventStatusHandle);
    callback.accept(eventStatus);
  }

  private final Class<T> eventStatusType;
  private final Class<ParentT> parentType;
  private final WeakReference<ParentT> parentReference;
  private long handle;
  private final Consumer<T> callback;
}
