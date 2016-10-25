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
package org.ros2.rcljava.exception;

/**
 * Raised when the rcljava implementation is accessed before RclJava().
 *
 * @author Mickael Gaillard <mick.gaillard@gmail.com>
 *
 */
public class NotInitializedException extends RuntimeException {

    /** Serial ID */
    private static final long serialVersionUID = -5109722435632105485L;

    /**
     * Constructor.
     *
     * @param cause
     */
    public NotInitializedException() {
        this("RCLJava.rclJavaInit() has not been called !", null);
    }

    /**
     * Constructor.
     *
     * @param cause
     */
    public NotInitializedException(String msg) {
        this(msg, null);
    }

    /**
     * Constructor.
     *
     * @param cause
     */
    public NotInitializedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}