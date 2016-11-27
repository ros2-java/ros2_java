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
package org.ros2.rcljava.node.service;

/**
 * Based on {@link java.util.funcion.BiConsumer}.
 *
 * @param <T> - the type of the first input to the operation
 * @param <U> - the type of the second input to the operation
 * @param <V> - the type of the third input to the operation
 */
public interface TriConsumer<T, U, V> {

  /**
   * Performs this operation on the given argument.
   *
   * @param input1 - the first input argument
   * @param input2 - the second input argument
   * @param input3 - the third input argument
   */
  void accept(T input1, U input2, V input3);

}
