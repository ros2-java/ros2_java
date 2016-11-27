/* Copyright 2016 Esteve Fernandez <esteve@apache.org>
 * Copyright 2016 Mickael Gaillard <mick.gaillard@gmail.com>
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
 * Raised when there is no rmw implementation with a Java extension available.
 *
 */
public class NoImplementationAvailableException extends Exception {

    /** Serial ID */
    private static final long serialVersionUID = -2351440132432398102L;

    /**
     * Constructor.
     *
     * @param cause
     */
    public NoImplementationAvailableException(Throwable cause) {
        super("no rmw implementation with a Java extension available", cause);
    }
}
