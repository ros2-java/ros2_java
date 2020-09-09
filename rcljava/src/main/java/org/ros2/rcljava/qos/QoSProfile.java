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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.qos.policies.Durability;
import org.ros2.rcljava.qos.policies.History;
import org.ros2.rcljava.qos.policies.Liveliness;
import org.ros2.rcljava.qos.policies.QoSPolicy;
import org.ros2.rcljava.qos.policies.Reliability;

/**
 * Implementation of the ROS QoS profile abstraction.
 *
 * It works as a bridge with rmw_qos_profile_t.
 * This class provides several static methods to instantiate the default profiles,
 * getters for the different policies, and setters that allow method chaining.
 *
 * Examples:
 *
 *    QoSProfile.keepLast(10);  // default qos profile with depth 10
 *    // keep all qos profile with best effort reliability and transient local durability
 *    QoSProfile.keepAll().
 *      setReliability(Reliability.BEST_EFFORT).setDurability(Durability.TRANSIENT_LOCAL);
 *    QoSProfile.sensorData();  // get predefined sensor data qos profile
 */
public class QoSProfile {
  // TODO(ivanpauno): Update all qos policies in a way that the objects are created from RCL,
  // to avoid depending on the enum values.

  /**
   * Construct a keep last QoS profile with the specified depth.
   *
   * @param depth history depth.
   * @return a new QoSProfile.
   */
  public static QoSProfile keepLast(int depth) {
    return defaultProfile().setDepth(depth);
  }

  /**
   * Construct a keep all qos profile.
   *
   * @param depth history depth.
   * @return a new QoSProfile.
   */
  public static QoSProfile keepAll() {
    return defaultProfile().setHistory(History.KEEP_ALL);
  }

  // TODO(ivanpauno): Please deprecate me
  public QoSProfile(History history, int depth, Reliability reliability, Durability durability,
      boolean avoidROSNamespaceConventions) {
    this.history = history;
    this.depth = depth;
    this.reliability = reliability;
    this.durability = durability;
    this.avoidROSNamespaceConventions = avoidROSNamespaceConventions;
  }

  /**
   * @return history policy of the profile.
   */
  public final History getHistory() {
    return this.history;
  }

  /**
   * @param history history policy to be set.
   * @return reference to `this` object, allowing method chaining.
   */
  public final QoSProfile setHistory(final History history) {
    this.history = history;
    return this;
  }

  /**
   * @return history depth of the profile.
   */
  public final int getDepth() {
    return this.depth;
  }

  /**
   * @param depth history depth to be set.
   * @return reference to `this` object, allowing method chaining.
   */
  public final QoSProfile setDepth(final int depth) {
    this.depth = depth;
    return this;
  }

  /**
   * @return reliability policy of the profile.
   */
  public final Reliability getReliability() {
    return this.reliability;
  }

  /**
   * @param reliability reliability policy to be set.
   * @return reference to `this` object, allowing method chaining.
   */
  public final QoSProfile setReliability(final Reliability reliability) {
    this.reliability = reliability;
    return this;
  }

  /**
   * @return durability policy of the profile.
   */
  public final Durability getDurability() {
    return this.durability;
  }

  /**
   * @param durability durability policy to be set.
   * @return reference to `this` object, allowing method chaining.
   */
  public final QoSProfile setDurability(final Durability durability) {
    this.durability = durability;
    return this;
  }

  /**
   * @return deadline policy of the profile.
   */
  public final Duration getDeadline() {
    return this.deadline;
  }

  /**
   * @param deadline deadline policy to be set. See
   *  <a href="http://design.ros2.org/articles/qos_deadline_liveliness_lifespan.html">here</a>.
   * @return reference to `this` object, allowing method chaining.
   */
  public final QoSProfile setDeadline(final Duration deadline) {
    this.deadline = deadline;
    return this;
  }

  /**
   * @return lifespan policy of the profile.
   */
  public final Duration getLifespan() {
    return this.lifespan;
  }

  /**
   * @param lifespan lifespan policy to be set. See
   *  <a href="http://design.ros2.org/articles/qos_deadline_liveliness_lifespan.html">here</a>.
   * @return reference to `this` object, allowing method chaining.
   */
  public final QoSProfile setLifespan(final Duration lifespan) {
    this.lifespan = lifespan;
    return this;
  }

  /**
   * @return liveliness policy of the profile.
   */
  public final Liveliness getLiveliness() {
    return this.liveliness;
  }

  /**
   * @param liveliness liveliness policy to be set. See
   *  <a href="http://design.ros2.org/articles/qos_deadline_liveliness_lifespan.html">here</a>.
   * @return reference to `this` object, allowing method chaining.
   */
  public final QoSProfile setLiveliness(final Liveliness liveliness) {
    this.liveliness = liveliness;
    return this;
  }

  /**
   * @return liveliness lease duration of the profile.
   */
  public final Duration getLivelinessLeaseDuration() {
    return this.livelinessLeaseDuration;
  }

  /**
   * @param livelinessLeaseDuration liveliness lease duration to be set. See
   *  <a href="http://design.ros2.org/articles/qos_deadline_liveliness_lifespan.html">here</a>.
   * @return reference to `this` object, allowing method chaining.
   */
  public final QoSProfile setLivelinessLeaseDuration(final Duration livelinessLeaseDuration) {
    this.livelinessLeaseDuration = livelinessLeaseDuration;
    return this;
  }

  /**
   * @return `true` if ROS namespace conventions are ignored, else `false`.
   */
  public final boolean getAvoidROSNamespaceConventions() {
    return this.avoidROSNamespaceConventions;
  }

  /**
   * @param avoidROSConventions when `true`, ROS name mangling conventions will be ignored.
   * @return reference to `this` object, allowing method chaining.
   */
  public final QoSProfile setAvoidROSNamespaceConventions(final boolean avoidROSConventions) {
    this.avoidROSNamespaceConventions = avoidROSConventions;
    return this;
  }

  private static final Logger logger = LoggerFactory.getLogger(QoSProfile.class);
  static {
    try {
      JNIUtils.loadImplementation(QoSProfile.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  // TODO(ivanpauno): Deprecate and use a private alternative, to match rclcpp/rclpy.
  public static final QoSProfile defaultProfile() {
    QoSProfile qos = new QoSProfile();
    qos.nativeFromRCL(qos.nativeGetHandleFromName("default"));
    return qos;
  }

  /**
   * @return Construct a new instance of the predefined system default profile.
   */
  public static final QoSProfile systemDefault() {
    QoSProfile qos = new QoSProfile();
    qos.nativeFromRCL(qos.nativeGetHandleFromName("system_default"));
    return qos;
  }

  /**
   * @return Construct a new instance of the predefined sensor data profile.
   */
  public static final QoSProfile sensorData() {
    QoSProfile qos = new QoSProfile();
    qos.nativeFromRCL(qos.nativeGetHandleFromName("sensor_data"));
    return qos;
  }

  /**
   * @return Construct a new instance of the predefined parameters default profile.
   */
  public static final QoSProfile parametersDefault() {
    QoSProfile qos = new QoSProfile();
    qos.nativeFromRCL(qos.nativeGetHandleFromName("parameters"));
    return qos;
  }

  /**
   * @return Construct a new instance of the predefined services default profile.
   */
  public static final QoSProfile servicesDefault() {
    QoSProfile qos = new QoSProfile();
    qos.nativeFromRCL(qos.nativeGetHandleFromName("services"));
    return qos;
  }

  /**
   * @return Construct a new instance of the predefined parameter events default profile.
   */
  public static final QoSProfile parameterEventsDefault() {
    QoSProfile qos = new QoSProfile();
    qos.nativeFromRCL(qos.nativeGetHandleFromName("parameter_events"));
    return qos;
  }

  // TODO(ivanpauno): Deprecate.
  public static final int DEPTH_SYSTEM_DEFAULT = 0;

  // TODO(ivanpauno): Deprecate.
  public static final QoSProfile SENSOR_DATA = sensorData();

  // TODO(ivanpauno): Deprecate.
  public static final QoSProfile PARAMETERS = parametersDefault();

  // TODO(ivanpauno): Deprecate.
  public static final QoSProfile DEFAULT = defaultProfile();

  // TODO(ivanpauno): Deprecate.
  public static final QoSProfile SERVICES_DEFAULT = servicesDefault();

  // TODO(ivanpauno): Deprecate.
  public static final QoSProfile PARAMETER_EVENTS = parameterEventsDefault();

  // TODO(ivanpauno): Deprecate.
  public static final QoSProfile SYSTEM_DEFAULT = systemDefault();

  /**
   * @internal
   * Private default constructor.
   *
   * Used from native code or private methods.
   * Typically combined with @{link QoSProfile#nativeFromRCL(handle)}.
   */
  private QoSProfile() {}

  /**
   * @internal
   * Load from an rmw_qos_profile_t.
   *
   * @param handle A `rmw_qos_profile_t *`, from where the profile is loaded.
   *  The handle is only borrowed, and a reference to it is NOT kept.
   */
  private final native void nativeFromRCL(long handle);
  private final static native long nativeGetHandleFromName(String profileName);

  private History history;
  private int depth;
  private Reliability reliability;
  private Durability durability;
  private Duration deadline = Duration.ofSeconds(0, 0);
  private Duration lifespan = Duration.ofSeconds(0, 0);
  private Liveliness liveliness = Liveliness.SYSTEM_DEFAULT;
  private Duration livelinessLeaseDuration = Duration.ofSeconds(0, 0);
  private boolean avoidROSNamespaceConventions;
}
