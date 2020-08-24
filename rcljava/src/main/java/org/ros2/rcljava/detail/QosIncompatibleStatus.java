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

package org.ros2.rcljava.detail;

import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.events.EventStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class serves as a bridge between rmw_qos_incompatible_event_status_t
 * and RCLJava.
 */
public class QosIncompatibleStatus implements EventStatus {
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
    return nativeAllocateRCLStatusEvent();
  }
  public final void deallocateRCLStatusEvent(long handle) {
    nativeDeallocateRCLStatusEvent(handle);
  }
  public final void fromRCLEvent(long handle) {
    nativeFromRCLEvent(handle);
  }

  private static final Logger logger = LoggerFactory.getLogger(QosIncompatibleStatus.class);
  static {
    try {
      JNIUtils.loadImplementation(QosIncompatibleStatus.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  private static native long nativeAllocateRCLStatusEvent();
  private static native void nativeDeallocateRCLStatusEvent(long handle);
  private native void nativeFromRCLEvent(long handle);
}
