/* Copyright 2016-2018 Esteve Fernandez <esteve@apache.org>
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

package org.ros2.rcljava.qos;

import java.time.Duration;

import org.ros2.rcljava.qos.policies.Durability;
import org.ros2.rcljava.qos.policies.History;
import org.ros2.rcljava.qos.policies.Liveliness;
import org.ros2.rcljava.qos.policies.QoSPolicy;
import org.ros2.rcljava.qos.policies.Reliability;

public class QoSProfile {
  // TODO(ivanpauno): Update all qos policies in a way that the objects are created from RCL,
  // to avoid depending on the enum values.
  private History history;

  private int depth;

  private Reliability reliability;

  private Durability durability;

  private Duration deadline = Duration.ofSeconds(0, 0);

  private Duration lifespan = Duration.ofSeconds(0, 0);

  private Liveliness liveliness = Liveliness.SYSTEM_DEFAULT;

  private Duration livelinessLeaseDuration = Duration.ofSeconds(0, 0);

  private boolean avoidROSNamespaceConventions;

  public QoSProfile(int depth) {
    this(History.KEEP_LAST, depth, Reliability.RELIABLE, Durability.VOLATILE, false);
  }

  public QoSProfile(History history, int depth, Reliability reliability, Durability durability,
      boolean avoidROSNamespaceConventions) {
    this.history = history;
    this.depth = depth;
    this.reliability = reliability;
    this.durability = durability;
    this.avoidROSNamespaceConventions = avoidROSNamespaceConventions;
  }

  public final History getHistory() {
    return this.history;
  }

  public final QoSProfile setHistory(final History history) {
    this.history = history;
    return this;
  }

  public final int getDepth() {
    return this.depth;
  }

  public final QoSProfile setDepth(final int depth) {
    this.depth = depth;
    return this;
  }

  public final Reliability getReliability() {
    return this.reliability;
  }

  public final QoSProfile setReliability(final Reliability reliability) {
    this.reliability = reliability;
    return this;
  }

  public final Durability getDurability() {
    return this.durability;
  }

  public final QoSProfile setDurability(final Durability durability) {
    this.durability = durability;
    return this;
  }

  public final Duration getDeadline() {
    return this.deadline;
  }

  public final QoSProfile setDeadline(final Duration deadline) {
    this.deadline = deadline;
    return this;
  }

  public final Duration getLifespan() {
    return this.lifespan;
  }

  public final QoSProfile setLifespan(final Duration lifespan) {
    this.lifespan = lifespan;
    return this;
  }

  public final Liveliness getLiveliness() {
    return this.liveliness;
  }

  public final QoSProfile setLiveliness(final Liveliness liveliness) {
    this.liveliness = liveliness;
    return this;
  }

  public final Duration getLivelinessLeaseDuration() {
    return this.livelinessLeaseDuration;
  }

  public final QoSProfile setLivelinessLeaseDuration(final Duration livelinessLeaseDuration) {
    this.livelinessLeaseDuration = livelinessLeaseDuration;
    return this;
  }

  public final boolean getAvoidROSNamespaceConventions() {
    return this.avoidROSNamespaceConventions;
  }

  public final QoSProfile setAvoidROSNamespaceConventions(final boolean avoidROSConventions) {
    this.avoidROSNamespaceConventions = avoidROSConventions;
    return this;
  }

  // TODO(ivanpauno): refactor all static default profiles methods,
  // so that the return value is get from the rmw definition directly
  // (leveraging a native function).
  public static final QoSProfile defaultProfile() {
    return new QoSProfile(10);
  }

  public static final QoSProfile systemDefault() {
    return new QoSProfile(
      History.SYSTEM_DEFAULT, QoSProfile.DEPTH_SYSTEM_DEFAULT,
      Reliability.SYSTEM_DEFAULT, Durability.SYSTEM_DEFAULT, false);
  }

  public static final QoSProfile sensorData() {
    return new QoSProfile(
      History.KEEP_LAST, 5, Reliability.BEST_EFFORT, Durability.VOLATILE, false);
  }

  public static final QoSProfile parametersDefault() {
    return new QoSProfile(
      History.KEEP_LAST, 1000, Reliability.RELIABLE, Durability.VOLATILE, false);
  }

  public static final QoSProfile servicesDefault() {
    return new QoSProfile(
      History.KEEP_LAST, 10, Reliability.RELIABLE, Durability.VOLATILE, false);
  }

  public static final QoSProfile parameterEventsDefault() {
    return new QoSProfile(
      History.KEEP_ALL, 1000, Reliability.RELIABLE, Durability.VOLATILE, false);
  }

  public static final int DEPTH_SYSTEM_DEFAULT = 0;

  public static final QoSProfile SENSOR_DATA = sensorData();

  public static final QoSProfile PARAMETERS = parametersDefault();

  public static final QoSProfile DEFAULT = defaultProfile();

  public static final QoSProfile SERVICES_DEFAULT = servicesDefault();

  public static final QoSProfile PARAMETER_EVENTS = parameterEventsDefault();

  public static final QoSProfile SYSTEM_DEFAULT = systemDefault();
}
