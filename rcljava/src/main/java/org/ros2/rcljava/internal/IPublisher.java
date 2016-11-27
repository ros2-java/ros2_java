/* Copyright 2016 Mickael Gaillard <mick.gaillard@gmail.com>
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
package org.ros2.rcljava.internal;

public interface IPublisher<T extends org.ros2.rcljava.internal.message.Message>  {

    /**
     * Publish a message.
     *
     * @param message An instance of the &lt;T&gt; parameter.
     */
    void publish(final T message);

    /**
     * Safely destroy the underlying ROS2 publisher structure.
     */
    void dispose();
}
