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
import org.ros2.rcljava.detail.QosIncompatibleStatus;
import org.ros2.rcljava.events.SubscriptionEventStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class serves as a bridge between rmw_requested_qos_incompatible_event_status_t
 * and RCLJava.
 */
public class RequestedQosIncompatible
extends QosIncompatibleStatus implements SubscriptionEventStatus {
  public final int getSubscriptionEventType() {
    return nativeGetEventType();
  }
  // TODO(ivanpauno): Remove this when -source 8 can be used (method references for the win)
  public static final Supplier<RequestedQosIncompatible>
  factory = new Supplier<RequestedQosIncompatible>() {
    public RequestedQosIncompatible get() {
      return new RequestedQosIncompatible();
    }
  };

  private static final Logger logger = LoggerFactory.getLogger(RequestedQosIncompatible.class);
  static {
    try {
      JNIUtils.loadImplementation(RequestedQosIncompatible.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  private static native int nativeGetEventType();
}
