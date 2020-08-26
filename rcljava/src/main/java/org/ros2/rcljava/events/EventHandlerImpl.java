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
import java.util.function.Supplier;

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
   * @param parentReference A {@link java.lang.ref.WeakReference} to the
   *     @{link org.ros2.rcljava.Publisher} or @{link org.ros2.rcljava.Subscription}
   *     that created this event handler.
   * @param handle A pointer to the underlying ROS 2 event structure, as an integer.
   *     Must not be zero.
   *     Ownership of the passed `handle` is taken, and this object is responsible
   *     of disposing it. That can be done using @{link EventHandler#dispose()}.
   * @param eventStatusFactory Factory of an event status.
   * @param callback The callback function that will be called when the event
   *     is triggered.
   * @param disposeCallback Callback that will be called when this object is disposed.
   */
  public EventHandlerImpl(
      final WeakReference<ParentT> parentReference,
      final long handle,
      final Supplier<T> eventStatusFactory,
      final Consumer<T> callback,
      final Consumer<EventHandler> disposeCallback) {
    this.parentReference = parentReference;
    this.handle = handle;
    this.eventStatusFactory = eventStatusFactory;
    this.callback = callback;
    this.disposeCallback = disposeCallback;
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
  public synchronized final void dispose() {
    if (this.handle == 0) {
      return;
    }
    this.disposeCallback.accept(this);
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
  public synchronized final void executeCallback() {
    T eventStatus = eventStatusFactory.get();
    long nativeEventStatusHandle = eventStatus.allocateRCLStatusEvent();
    nativeTake(this.handle, nativeEventStatusHandle);
    eventStatus.fromRCLEvent(nativeEventStatusHandle);
    eventStatus.deallocateRCLStatusEvent(nativeEventStatusHandle);
    this.callback.accept(eventStatus);
  }

  private final Supplier<T> eventStatusFactory;
  private final WeakReference<ParentT> parentReference;
  private long handle;
  private final Consumer<T> callback;
  private final Consumer<EventHandler> disposeCallback;
}
