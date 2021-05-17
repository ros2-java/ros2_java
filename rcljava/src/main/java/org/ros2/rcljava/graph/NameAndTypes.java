/* Copyright 2020 Open Source Robotics Foundation, Inc.
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

package org.ros2.rcljava.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ros2.rcljava.common.JNIUtils;

/**
 * Class that represents a topic/service/action name, together with all the types
 * of message/service/action that are using that name.
 */
public class NameAndTypes {
  /// Name of the topic/service/action.
  public String name;
  /// Types of the topic/service/action with the given name.
  public Collection<String> types;

  /**
   * Construct from given name and types.
   *
   * @param name name of the topic/service/action.
   * @param types types of the given topic/service/action.
   *  A shallow copy of the given collection will be stored,
   *  but given that String is immutable, this is not a problem.
   * @param typesSize size of the \a typesHandle array.
   */
  public NameAndTypes(final String name, final Collection<String> types) {
    this.name = name;
    this.types = new ArrayList<String>(types);
  }

  /// @internal Default constructor, only used from jni code.
  private NameAndTypes() {
    this.types = new ArrayList<String>();
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof NameAndTypes)) {
      return false;
    }
    NameAndTypes other = (NameAndTypes) o;
    return Objects.equals(this.name, other.name) &&
      Objects.equals(this.types, other.types);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.types);
  }
}
