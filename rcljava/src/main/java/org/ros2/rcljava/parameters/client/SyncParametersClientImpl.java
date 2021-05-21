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

import org.ros2.rcljava.client.Client;
import org.ros2.rcljava.concurrent.RCLFuture;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.executors.Executor;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.qos.QoSProfile;
import org.ros2.rcljava.parameters.ParameterType;
import org.ros2.rcljava.parameters.ParameterVariant;

public class SyncParametersClientImpl implements SyncParametersClient {
  private static class ConsumerHelper<T> implements Consumer<Future<T>> {
    private RCLFuture<T> resultFuture;

    public void accept(final Future<T> future) {
      T result = null;
      try {
        result = future.get();
      } catch (Exception e) {
        // TODO(esteve): do something
      }
      this.resultFuture.set(result);
    }

    public ConsumerHelper(RCLFuture<T> resultFuture) {
      this.resultFuture = resultFuture;
    }
  }

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

  public List<ParameterVariant> getParameters(final List<String> names)
      throws InterruptedException, ExecutionException {
    if (executor != null) {
      RCLFuture<List<ParameterVariant>> future = new RCLFuture<List<ParameterVariant>>(executor);
      asyncParametersClient.getParameters(
          names, new ConsumerHelper<List<ParameterVariant>>(future));
      return future.get();
    } else {
      return asyncParametersClient.getParameters(names, null).get();
    }
  }

  public List<ParameterType> getParameterTypes(final List<String> names)
      throws InterruptedException, ExecutionException {
    if (executor != null) {
      RCLFuture<List<ParameterType>> future = new RCLFuture<List<ParameterType>>(executor);
      asyncParametersClient.getParameterTypes(
          names, new ConsumerHelper<List<ParameterType>>(future));
      return future.get();
    } else {
      return asyncParametersClient.getParameterTypes(names, null).get();
    }
  }

  public List<rcl_interfaces.msg.SetParametersResult> setParameters(
      final List<ParameterVariant> parameters) throws InterruptedException, ExecutionException {
    if (executor != null) {
      RCLFuture<List<rcl_interfaces.msg.SetParametersResult>> future =
          new RCLFuture<List<rcl_interfaces.msg.SetParametersResult>>(executor);
      asyncParametersClient.setParameters(
          parameters, new ConsumerHelper<List<rcl_interfaces.msg.SetParametersResult>>(future));
      return future.get();
    } else {
      return asyncParametersClient.setParameters(parameters, null).get();
    }
  }

  public rcl_interfaces.msg.SetParametersResult setParametersAtomically(
      final List<ParameterVariant> parameters) throws InterruptedException, ExecutionException {
    if (executor != null) {
      RCLFuture<rcl_interfaces.msg.SetParametersResult> future =
          new RCLFuture<rcl_interfaces.msg.SetParametersResult>(executor);
      asyncParametersClient.setParametersAtomically(
          parameters, new ConsumerHelper<rcl_interfaces.msg.SetParametersResult>(future));
      return future.get();
    } else {
      return asyncParametersClient.setParametersAtomically(parameters, null).get();
    }
  }

  public rcl_interfaces.msg.ListParametersResult listParameters(
      final List<String> prefixes, long depth) throws InterruptedException, ExecutionException {
    if (executor != null) {
      RCLFuture<rcl_interfaces.msg.ListParametersResult> future =
          new RCLFuture<rcl_interfaces.msg.ListParametersResult>(executor);
      asyncParametersClient.listParameters(
          prefixes, depth, new ConsumerHelper<rcl_interfaces.msg.ListParametersResult>(future));
      return future.get();
    } else {
      return asyncParametersClient.listParameters(prefixes, depth, null).get();
    }
  }

  public List<rcl_interfaces.msg.ParameterDescriptor> describeParameters(final List<String> names)
      throws InterruptedException, ExecutionException {
    if (executor != null) {
      RCLFuture<List<rcl_interfaces.msg.ParameterDescriptor>> future =
          new RCLFuture<List<rcl_interfaces.msg.ParameterDescriptor>>(executor);
      asyncParametersClient.describeParameters(
          names, new ConsumerHelper<List<rcl_interfaces.msg.ParameterDescriptor>>(future));
      return future.get();
    } else {
      return asyncParametersClient.describeParameters(names, null).get();
    }
  }
}