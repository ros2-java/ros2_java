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

package org.ros2.rcljava.subscription.statuses;

import java.util.function.Supplier;

import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.events.SubscriptionEventStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class serves as a bridge between a rmw_requested_deadline_missed_status_t and RCLJava.
 */
public class RequestedDeadlineMissed implements SubscriptionEventStatus {
  public int totalCount;
  public int totalCountChange;

  public final long allocateRCLStatusEvent() {
    return nativeAllocateRCL();
  }
  public final void deallocateRCLStatusEvent(long handle) {
    nativeDeallocateRCL(handle);
  }
  public final void fromRCLEvent(long handle) {
    nativeFromRCL(handle);
  }
  public final int getSubscriptionEventType() {
    return nativeGetEventType();
  }
  // TODO(ivanpauno): Remove this when -source 8 can be used (method references for the win)
  public static final Supplier<RequestedDeadlineMissed> factory = new Supplier<RequestedDeadlineMissed>() {
    public RequestedDeadlineMissed get() {
      return new RequestedDeadlineMissed();
    }
  };

  private static final Logger logger = LoggerFactory.getLogger(RequestedDeadlineMissed.class);
  static {
    try {
      JNIUtils.loadImplementation(RequestedDeadlineMissed.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  private static native long nativeAllocateRCL();
  private static native void nativeDeallocateRCL(long handle);
  private native void nativeFromRCL(long handle);
  private static native int nativeGetEventType();
}
