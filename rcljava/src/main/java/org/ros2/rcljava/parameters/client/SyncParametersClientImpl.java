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

import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.client.Client;
import org.ros2.rcljava.concurrent.RCLFuture;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.executors.Executor;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.qos.QoSProfile;
import org.ros2.rcljava.parameters.ParameterType;
import org.ros2.rcljava.parameters.ParameterVariant;

public class SyncParametersClientImpl implements SyncParametersClient {
  private Executor executor;

  public AsyncParametersClient asyncParametersClient;

  public SyncParametersClientImpl(
    final Node node,
    final String remoteName,
    final QoSProfile qosProfile)
  {
    this.asyncParametersClient = new AsyncParametersClientImpl(node, remoteName, qosProfile);
  }

  public SyncParametersClientImpl(final Node node, final QoSProfile qosProfile)
  {
    this(node, "", qosProfile);
  }

  public SyncParametersClientImpl(final Node node, final String remoteName)
  {
    this(node, remoteName, QoSProfile.PARAMETERS);
  }

  public SyncParametersClientImpl(final Node node)
  {
    this(node, "", QoSProfile.PARAMETERS);
  }

  public SyncParametersClientImpl(
    final Executor executor,
    final Node node,
    final String remoteName,
    final QoSProfile qosProfile)
  {
    this.executor = executor;
    this.asyncParametersClient = new AsyncParametersClientImpl(node, remoteName, qosProfile);
  }

  public SyncParametersClientImpl(
    final Executor executor,
    final Node node,
    final QoSProfile qosProfile)
  {
    this(executor, node, "", qosProfile);
  }

  public SyncParametersClientImpl(
    final Executor executor, final Node node, final String remoteName)
  {
    this(executor, node, remoteName, QoSProfile.PARAMETERS);
  }

  public SyncParametersClientImpl(final Executor executor, final Node node)
  {
    this(executor, node, "", QoSProfile.PARAMETERS);
  }

  private <T> T spinUntilComplete(Future<T> future)
    throws InterruptedException, ExecutionException
  {
    if (executor != null) {
      executor.spinUntilComplete(future);
    } else {
      RCLJava.spinUntilComplete(this.asyncParametersClient.getNode(), future);
    }
    return future.get();
  }

  public List<ParameterVariant> getParameters(final List<String> names)
      throws InterruptedException, ExecutionException {
    Future<List<ParameterVariant>> future = asyncParametersClient.getParameters(names, null);
    return spinUntilComplete(future);
  }

  public List<ParameterType> getParameterTypes(final List<String> names)
      throws InterruptedException, ExecutionException {
    Future<List<ParameterType>> future = asyncParametersClient.getParameterTypes(names, null);
    return spinUntilComplete(future);
  }

  public List<rcl_interfaces.msg.SetParametersResult> setParameters(
      final List<ParameterVariant> parameters) throws InterruptedException, ExecutionException {
    Future<List<rcl_interfaces.msg.SetParametersResult>> future = asyncParametersClient.setParameters(
      parameters, null);
    return spinUntilComplete(future);
  }

  public rcl_interfaces.msg.SetParametersResult setParametersAtomically(
      final List<ParameterVariant> parameters) throws InterruptedException, ExecutionException {
    Future<rcl_interfaces.msg.SetParametersResult> future = asyncParametersClient.setParametersAtomically(
      parameters, null);
    return spinUntilComplete(future);
  }

  public rcl_interfaces.msg.ListParametersResult listParameters(
      final List<String> prefixes, long depth) throws InterruptedException, ExecutionException {
    Future<rcl_interfaces.msg.ListParametersResult> future = asyncParametersClient.listParameters(
      prefixes, depth, null);
    return spinUntilComplete(future);
  }

  public List<rcl_interfaces.msg.ParameterDescriptor> describeParameters(final List<String> names)
      throws InterruptedException, ExecutionException {
    Future<List<rcl_interfaces.msg.ParameterDescriptor>> future = asyncParametersClient.describeParameters(
      names, null);
    return spinUntilComplete(future);
  }
}