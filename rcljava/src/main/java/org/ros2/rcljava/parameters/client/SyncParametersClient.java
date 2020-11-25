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

package org.ros2.rcljava.parameters.client;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.parameters.ParameterType;
import org.ros2.rcljava.parameters.ParameterVariant;

public interface SyncParametersClient {
  public ParameterVariant[] getParameters(final String[] names)
      throws InterruptedException, ExecutionException;

  public ParameterType[] getParameterTypes(final String[] names)
      throws InterruptedException, ExecutionException;

  public rcl_interfaces.msg.SetParametersResult[] setParameters(
      final ParameterVariant[] parameters)
      throws InterruptedException, ExecutionException;

  public rcl_interfaces.msg.SetParametersResult setParametersAtomically(
      final ParameterVariant[] parameters)
      throws InterruptedException, ExecutionException;

  public rcl_interfaces.msg.ListParametersResult listParameters(final String[] prefixes,
      long depth) throws InterruptedException, ExecutionException;

  public rcl_interfaces.msg.ParameterDescriptor[] describeParameters(
      final String[] names) throws InterruptedException, ExecutionException;
}