/* Copyright 2017-2018 Esteve Fernandez <esteve@apache.org>
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

package org.ros2.rcljava.parameters;

public enum ParameterType {
  PARAMETER_NOT_SET(rcl_interfaces.msg.ParameterType.PARAMETER_NOT_SET),
  PARAMETER_BOOL(rcl_interfaces.msg.ParameterType.PARAMETER_BOOL),
  PARAMETER_INTEGER(rcl_interfaces.msg.ParameterType.PARAMETER_INTEGER),
  PARAMETER_DOUBLE(rcl_interfaces.msg.ParameterType.PARAMETER_DOUBLE),
  PARAMETER_STRING(rcl_interfaces.msg.ParameterType.PARAMETER_STRING),
  PARAMETER_BYTE_ARRAY(rcl_interfaces.msg.ParameterType.PARAMETER_BYTE_ARRAY),
  PARAMETER_BOOL_ARRAY(rcl_interfaces.msg.ParameterType.PARAMETER_BOOL_ARRAY),
  PARAMETER_INTEGER_ARRAY(rcl_interfaces.msg.ParameterType.PARAMETER_INTEGER_ARRAY),
  PARAMETER_DOUBLE_ARRAY(rcl_interfaces.msg.ParameterType.PARAMETER_DOUBLE_ARRAY),
  PARAMETER_STRING_ARRAY(rcl_interfaces.msg.ParameterType.PARAMETER_STRING_ARRAY);

  private final byte value;

  private final static ParameterType[] values = ParameterType.values();

  ParameterType(final byte value) {
    this.value = value;
  }

  public byte getValue() {
    return value;
  }

  public static ParameterType fromByte(final byte code) {
    for (ParameterType value : values) {
      if (value.getValue() == code) {
        return value;
      }
    }
    return null;
  }
}
