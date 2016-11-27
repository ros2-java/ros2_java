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
package org.ros2.rcljava.node.topic;

import org.ros2.rcljava.internal.message.Message;

/**
 * This is a copy of {@link java.util.funcion.Consumer} for platforms that don't
 * support Java 8's API (e.g. Android &lt; 7.0)
 *
 * @param <T> - the type of the input to the operation
 */
public interface Consumer<T extends Message> {

    /**
       * Performs this operation on the given argument.
       *
       * @param message - the input argument
       */
    void accept(T message);
}
