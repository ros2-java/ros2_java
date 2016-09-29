/* Copyright 2016 Open Source Robotics Foundation, Inc.
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
package org.ros2.rcljava;

/**
 * <h1>Quality of Service Profile</h1>
 * <p><i>TODO Not full implemented !!</i></p>
 * @author Mickael Gaillard <mickael.gaillard@gmail.com>
 */
public class QoSProfile {

    /** Default Depth level */
    public static final int DEPTH_SYSTEM_DEFAULT = 0;

    /** Reliability Policy enum. */
    public enum ReliabilityPolicy {
        SYSTEM_DEFAULT,
        RELIABLE,
        BEST_EFFORT
    }

    /** History Policy enum. */
    public enum HistoryPolicy {
        SYSTEM_DEFAULT,
        KEEP_LAST,
        KEEP_ALL
    }

    /** Durability Policy enum. */
    public enum DurabilityPolicy {
        SYSTEM_DEFAULT,
        TRANSIENT_LOCAL,
        VOLATILE
    }

    /** Sensor QOS Profile */
    public static final QoSProfile PROFILE_SENSOR_DATA = new QoSProfile(
            HistoryPolicy.KEEP_LAST,
            5,
            ReliabilityPolicy.BEST_EFFORT,
            DurabilityPolicy.SYSTEM_DEFAULT
            );

    /** Parameter QOS Profile */
    public static final QoSProfile PROFILE_PARAMETER = new QoSProfile(
            HistoryPolicy.KEEP_LAST,
            1000,
            ReliabilityPolicy.RELIABLE,
            DurabilityPolicy.SYSTEM_DEFAULT
            );

    /** Default QOS Profile */
    public static final QoSProfile PROFILE_DEFAULT = new QoSProfile(
            HistoryPolicy.KEEP_ALL,
            10,
            ReliabilityPolicy.RELIABLE,
            DurabilityPolicy.SYSTEM_DEFAULT
            );

    /** Services default QOS Profile */
    public static final QoSProfile PROFILE_SERVICES_DEFAULT = new QoSProfile(
            HistoryPolicy.KEEP_LAST,
            10,
            ReliabilityPolicy.RELIABLE,
            DurabilityPolicy.TRANSIENT_LOCAL
            );

    /** Parameter events QOS Profile */
    public static final QoSProfile PROFILE_PARAMETER_EVENTS = new QoSProfile(
            HistoryPolicy.KEEP_ALL,
            1000,
            ReliabilityPolicy.RELIABLE,
            DurabilityPolicy.SYSTEM_DEFAULT
            );

    /** System default QOS Profile */
    public static final QoSProfile PROFILE_SYSTEM_DEFAULT = new QoSProfile(
            HistoryPolicy.SYSTEM_DEFAULT,
            DEPTH_SYSTEM_DEFAULT,
            ReliabilityPolicy.SYSTEM_DEFAULT,
            DurabilityPolicy.SYSTEM_DEFAULT
            );

    /** History Policy */
    protected final HistoryPolicy history;

    /** Depth */
    protected final int depth;

    /** Reliability Policy */
    protected final ReliabilityPolicy reliability;

    /** Durability Policy */
    protected final DurabilityPolicy durability;

    /**
     * Constuctor.
     *
     * @param history
     * @param depth
     * @param reliability
     * @param durability
     */
    public QoSProfile(
            HistoryPolicy history,
            int depth,
            ReliabilityPolicy reliability,
            DurabilityPolicy durability) {

        this.history = history;
        this.depth = depth;
        this.reliability = reliability;
        this.durability = durability;
    }
}
