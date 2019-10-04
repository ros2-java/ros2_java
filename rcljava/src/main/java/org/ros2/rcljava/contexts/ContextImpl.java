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

package org.ros2.rcljava.contexts;

import org.ros2.rcljava.common.JNIUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@inheritDoc}
 */
public class ContextImpl implements Context {
  private static final Logger logger = LoggerFactory.getLogger(ContextImpl.class);

  static {
    try {
      JNIUtils.loadImplementation(ContextImpl.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  /**
   * An integer that represents a pointer to the underlying context structure (rcl_context_t).
   */
  private long handle;

  /**
   * Constructor.
   *
   * @param handle A pointer to the underlying context structure.
   *     Must not be zero.
   */
  public ContextImpl(final long handle) {
    this.handle = handle;
  }

  /**
   * Destroy a context (rcl_context_t).
   *
   * @param handle A pointer to the underlying context structure.
   */
  private static native void nativeDispose(long handle);

  /**
   * {@inheritDoc}
   */
  public final void dispose() {
    // Ensure the context is shutdown first
    shutdown();
    nativeDispose(this.handle);
    this.handle = 0;
  }

  /**
   * {@inheritDoc}
   */
  public final long getHandle() {
    return this.handle;
  }

  /**
   * Initialize a rcl_context_t.
   *
   * @param contextHandle The pointer to the context structure.
   */
  private static native void nativeInit(long contextHandle);

  /**
   * {@inheritDoc}
   */
  public final void init() {
    nativeInit(this.handle);
  }

  /**
   * Shutdown a rcl_context_t.
   *
   * @param contextHandle The pointer to the context structure.
   */
  private static native void nativeShutdown(long contextHandle);

  /**
   * {@inheritDoc}
   */
  public final void shutdown() {
    nativeShutdown(this.handle);
  }

  /**
   * Check if a context is valid (rcl_context_t).
   *
   * @param handle A pointer to the underlying context structure.
   */
  private static native boolean nativeIsValid(long handle);

  /**
   * {@inheritDoc}
   */
  public final boolean isValid() {
    return nativeIsValid(this.handle);
  }
}
