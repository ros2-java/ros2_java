/* Copyright 2019 Open Source Robotics Foundation, Inc.
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

package org.ros2.rcljava.time;

import org.ros2.rcljava.common.JNIUtils;

import org.ros2.rcljava.interfaces.Disposable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clock implements Disposable {
  private static final Logger logger = LoggerFactory.getLogger(Clock.class);

  static {
    try {
      JNIUtils.loadImplementation(Clock.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  /**
   * The underlying clock handle (rcl_clock_t).
   */
  private long handle;

  /**
   * The clock type.
   */
  private final ClockType clockType;

  /**
   * Create an RCL clock (rcl_clock_t).
   *
   * @param clockType The RCL clock type.
   * @return A pointer to the underlying clock structure as an integer.
   */
  private static native long nativeCreateClockHandle(ClockType clockType);

  /**
   * Constructor.
   *
   * @param clockType The type of clock to create.
   */
  public Clock(ClockType clockType) {
    this.clockType = clockType;
    this.handle = Clock.nativeCreateClockHandle(clockType);
  }

  /**
   * @return The clock type.
   */
  public ClockType getClockType() {
    return clockType;
  }

  /**
   * Destroy an RCL clock (rcl_clock_t).
   *
   * @param handle A pointer to the underlying clock structure.
   */
  private static native void nativeDispose(long handle);

  /**
   * {@inheritDoc}
   */
  public final void dispose() {
    Clock.nativeDispose(this.handle);
    this.handle = 0;
  }

  /**
   * {@inheritDoc}
   */
  public final long getHandle() {
    return this.handle;
  }
}
