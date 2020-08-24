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

package org.ros2.rcljava.publisher.statuses;

import java.util.function.Supplier;

import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.events.PublisherEventStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class serves as a bridge between a rmw_qos_incompatible_event_status_t and RCLJava.
 */
public class OfferedQosIncompatible implements PublisherEventStatus {
  public int totalCount;
  public int totalCountChange;
  public PolicyKind lastPolicyKind;

  public enum PolicyKind {
    INVALID,
    DURABILITY,
    DEADLINE,
    LIVELINESS,
    RELIABILITY,
    HISTORY,
    LIFESPAN;
  }

  public final long allocateRCLStatusEvent() {
    return nAllocateRCLStatusEvent();
  }
  public final void deallocateRCLStatusEvent(long handle) {
    nDeallocateRCLStatusEvent(handle);
  }
  public final void fromRCLEvent(long handle) {
    nFromRCLEvent(handle);
  }
  public final int getPublisherEventType() {
    return nGetPublisherEventType();
  }
  // TODO(ivanpauno): Remove this when -source 8 can be used (method references for the win)
  public static final Supplier<OfferedQosIncompatible> factory = new Supplier<OfferedQosIncompatible>() {
    public OfferedQosIncompatible get() {
      return new OfferedQosIncompatible();
    }
  };

  private static final Logger logger = LoggerFactory.getLogger(OfferedQosIncompatible.class);
  static {
    try {
      JNIUtils.loadImplementation(OfferedQosIncompatible.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  private static native long nAllocateRCLStatusEvent();
  private static native void nDeallocateRCLStatusEvent(long handle);
  private native void nFromRCLEvent(long handle);
  private static native int nGetPublisherEventType();
}
