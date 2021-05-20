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

import java.util.concurrent.Future;

import org.ros2.rcljava.client.Client;
import org.ros2.rcljava.concurrent.RCLFuture;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.qos.QoSProfile;
import org.ros2.rcljava.parameters.ParameterNames;
import org.ros2.rcljava.parameters.ParameterType;
import org.ros2.rcljava.parameters.ParameterVariant;

public class AsyncParametersClientImpl implements AsyncParametersClient {
  private final Node node;

  private final String remoteName;

  private final Client<rcl_interfaces.srv.GetParameters> getParametersClient;

  private final Client<rcl_interfaces.srv.GetParameterTypes> getParameterTypesClient;

  private final Client<rcl_interfaces.srv.SetParameters> setParametersClient;

  private final Client<rcl_interfaces.srv.SetParametersAtomically> setParametersAtomicallyClient;

  private final Client<rcl_interfaces.srv.ListParameters> listParametersClient;

  private final Client<rcl_interfaces.srv.DescribeParameters> describeParametersClient;

  public AsyncParametersClientImpl(final Node node, final String remoteName,
      final QoSProfile qosProfile) throws NoSuchFieldException, IllegalAccessException {
    this.node = node;
    if (remoteName != "") {
      this.remoteName = remoteName;
    } else {
      this.remoteName = this.node.getName();
    }

    this.getParametersClient = this.node.<rcl_interfaces.srv.GetParameters>createClient(
        rcl_interfaces.srv.GetParameters.class,
        this.remoteName + "/" + ParameterNames.GET_PARAMETERS, qosProfile);

    this.getParameterTypesClient = this.node.<rcl_interfaces.srv.GetParameterTypes>createClient(
        rcl_interfaces.srv.GetParameterTypes.class,
        this.remoteName + "/" + ParameterNames.GET_PARAMETER_TYPES, qosProfile);

    this.setParametersClient = this.node.<rcl_interfaces.srv.SetParameters>createClient(
        rcl_interfaces.srv.SetParameters.class,
        this.remoteName + "/" + ParameterNames.SET_PARAMETERS, qosProfile);

    this.setParametersAtomicallyClient =
        this.node.<rcl_interfaces.srv.SetParametersAtomically>createClient(
            rcl_interfaces.srv.SetParametersAtomically.class,
            this.remoteName + "/" + ParameterNames.SET_PARAMETERS_ATOMICALLY, qosProfile);

    this.listParametersClient = this.node.<rcl_interfaces.srv.ListParameters>createClient(
        rcl_interfaces.srv.ListParameters.class,
        this.remoteName + "/" + ParameterNames.LIST_PARAMETERS, qosProfile);

    this.describeParametersClient = this.node.<rcl_interfaces.srv.DescribeParameters>createClient(
        rcl_interfaces.srv.DescribeParameters.class,
        this.remoteName + "/" + ParameterNames.DESCRIBE_PARAMETERS, qosProfile);
  }

  public AsyncParametersClientImpl(final Node node, final QoSProfile qosProfile)
      throws NoSuchFieldException, IllegalAccessException {
    this(node, "", qosProfile);
  }

  public AsyncParametersClientImpl(final Node node, final String remoteName)
      throws NoSuchFieldException, IllegalAccessException {
    this(node, remoteName, QoSProfile.PARAMETERS);
  }

  public AsyncParametersClientImpl(final Node node)
      throws NoSuchFieldException, IllegalAccessException {
    this(node, "", QoSProfile.PARAMETERS);
  }

  public Future<List<ParameterVariant>> getParameters(final List<String> names) {
    return getParameters(names, null);
  }

  public Future<List<ParameterVariant>> getParameters(
      final List<String> names, final Consumer<Future<List<ParameterVariant>>> callback) {
    final RCLFuture<List<ParameterVariant>> futureResult =
        new RCLFuture<List<ParameterVariant>>(new WeakReference<Node>(this.node));
    final rcl_interfaces.srv.GetParameters_Request request =
        new rcl_interfaces.srv.GetParameters_Request();
    request.setNames(names);

    getParametersClient.asyncSendRequest(
        request, new Consumer<Future<rcl_interfaces.srv.GetParameters_Response>>() {
          public void accept(final Future<rcl_interfaces.srv.GetParameters_Response> future) {
            List<ParameterVariant> parameterVariants = new ArrayList<ParameterVariant>();
            rcl_interfaces.msg.ParameterValue[] pvalues = new rcl_interfaces.msg.ParameterValue[0];
            try {
              pvalues = future.get().getValues();
            } catch (Exception e) {
              // TODO(esteve): do something
            }
            for (int i = 0; i < pvalues.length; i++) {
              rcl_interfaces.msg.Parameter parameter = new rcl_interfaces.msg.Parameter();
              parameter.setName(request.getNames()[i]);
              parameter.setValue(pvalues[i]);
              parameterVariants.add(ParameterVariant.fromParameter(parameter));
            }
            futureResult.set(parameterVariants);
            if (callback != null) {
              callback.accept(futureResult);
            }
          }
        });
    return futureResult;
  }

  public Future<List<ParameterType>> getParameterTypes(final List<String> names) {
    return getParameterTypes(names, null);
  }

  public Future<List<ParameterType>> getParameterTypes(
      final List<String> names, final Consumer<Future<List<ParameterType>>> callback) {
    final RCLFuture<List<ParameterType>> futureResult =
        new RCLFuture<List<ParameterType>>(new WeakReference<Node>(this.node));
    final rcl_interfaces.srv.GetParameterTypes_Request request =
        new rcl_interfaces.srv.GetParameterTypes_Request();
    request.setNames(names);

    getParameterTypesClient.asyncSendRequest(
        request, new Consumer<Future<rcl_interfaces.srv.GetParameterTypes_Response>>() {
          public void accept(final Future<rcl_interfaces.srv.GetParameterTypes_Response> future) {
            List<ParameterType> parameterTypes = new ArrayList<ParameterType>();
            byte[] pts = new byte[0];
            try {
              pts = future.get().getTypes();
            } catch (Exception e) {
              // TODO(esteve): do something
            }
            for (byte pt : pts) {
              parameterTypes.add(ParameterType.fromByte(pt));
            }
            futureResult.set(parameterTypes);
            if (callback != null) {
              callback.accept(futureResult);
            }
          }
        });
    return futureResult;
  }

  public Future<List<rcl_interfaces.msg.SetParametersResult>> setParameters(
      final List<ParameterVariant> parameters) {
    return setParameters(parameters, null);
  }

  public Future<List<rcl_interfaces.msg.SetParametersResult>> setParameters(
      final List<ParameterVariant> parameters,
      final Consumer<Future<List<rcl_interfaces.msg.SetParametersResult>>> callback) {
    final RCLFuture<List<rcl_interfaces.msg.SetParametersResult>> futureResult =
        new RCLFuture<List<rcl_interfaces.msg.SetParametersResult>>(
            new WeakReference<Node>(this.node));
    final rcl_interfaces.srv.SetParameters_Request request =
        new rcl_interfaces.srv.SetParameters_Request();
    List<rcl_interfaces.msg.Parameter> requestParameters =
        new ArrayList<rcl_interfaces.msg.Parameter>();
    for (ParameterVariant parameterVariant : parameters) {
      requestParameters.add(parameterVariant.toParameter());
    }
    request.setParameters(requestParameters);

    setParametersClient.asyncSendRequest(
        request, new Consumer<Future<rcl_interfaces.srv.SetParameters_Response>>() {
          public void accept(final Future<rcl_interfaces.srv.SetParameters_Response> future) {
            List<rcl_interfaces.msg.SetParametersResult> setParametersResult = null;
            try {
              setParametersResult = future.get().getResultsAsList();
            } catch (Exception e) {
              // TODO(esteve): do something
            }
            futureResult.set(setParametersResult);
            if (callback != null) {
              callback.accept(futureResult);
            }
          }
        });
    return futureResult;
  }

  public Future<rcl_interfaces.msg.SetParametersResult> setParametersAtomically(
      final List<ParameterVariant> parameters) {
    return setParametersAtomically(parameters, null);
  }

  public Future<rcl_interfaces.msg.SetParametersResult> setParametersAtomically(
      final List<ParameterVariant> parameters,
      final Consumer<Future<rcl_interfaces.msg.SetParametersResult>> callback) {
    final RCLFuture<rcl_interfaces.msg.SetParametersResult> futureResult =
        new RCLFuture<rcl_interfaces.msg.SetParametersResult>(new WeakReference<Node>(this.node));
    final rcl_interfaces.srv.SetParametersAtomically_Request request =
        new rcl_interfaces.srv.SetParametersAtomically_Request();
    List<rcl_interfaces.msg.Parameter> requestParameters =
        new ArrayList<rcl_interfaces.msg.Parameter>();
    for (ParameterVariant parameterVariant : parameters) {
      requestParameters.add(parameterVariant.toParameter());
    }
    request.setParameters(requestParameters);

    setParametersAtomicallyClient.asyncSendRequest(
        request, new Consumer<Future<rcl_interfaces.srv.SetParametersAtomically_Response>>() {
          public void accept(
              final Future<rcl_interfaces.srv.SetParametersAtomically_Response> future) {
            rcl_interfaces.msg.SetParametersResult setParametersResult = null;
            try {
              setParametersResult = future.get().getResult();
            } catch (Exception e) {
              // TODO(esteve): do something
            }
            futureResult.set(setParametersResult);
            if (callback != null) {
              callback.accept(futureResult);
            }
          }
        });
    return futureResult;
  }

  public Future<rcl_interfaces.msg.ListParametersResult> listParameters(
      final List<String> prefixes, long depth) {
    return listParameters(prefixes, depth, null);
  }

  public Future<rcl_interfaces.msg.ListParametersResult> listParameters(final List<String> prefixes,
      long depth, final Consumer<Future<rcl_interfaces.msg.ListParametersResult>> callback) {
    final RCLFuture<rcl_interfaces.msg.ListParametersResult> futureResult =
        new RCLFuture<rcl_interfaces.msg.ListParametersResult>(new WeakReference<Node>(this.node));
    final rcl_interfaces.srv.ListParameters_Request request =
        new rcl_interfaces.srv.ListParameters_Request();
    request.setPrefixes(prefixes);
    request.setDepth(depth);

    listParametersClient.asyncSendRequest(
        request, new Consumer<Future<rcl_interfaces.srv.ListParameters_Response>>() {
          public void accept(final Future<rcl_interfaces.srv.ListParameters_Response> future) {
            rcl_interfaces.msg.ListParametersResult listParametersResult = null;
            try {
              listParametersResult = future.get().getResult();
            } catch (Exception e) {
              // TODO(esteve): do something
            }
            futureResult.set(listParametersResult);
            if (callback != null) {
              callback.accept(futureResult);
            }
          }
        });
    return futureResult;
  }

  public Future<List<rcl_interfaces.msg.ParameterDescriptor>> describeParameters(
      final List<String> names) {
    return describeParameters(names, null);
  }

  public Future<List<rcl_interfaces.msg.ParameterDescriptor>> describeParameters(
      final List<String> names,
      final Consumer<Future<List<rcl_interfaces.msg.ParameterDescriptor>>> callback) {
    final RCLFuture<List<rcl_interfaces.msg.ParameterDescriptor>> futureResult =
        new RCLFuture<List<rcl_interfaces.msg.ParameterDescriptor>>(
            new WeakReference<Node>(this.node));
    final rcl_interfaces.srv.DescribeParameters_Request request =
        new rcl_interfaces.srv.DescribeParameters_Request();
    request.setNames(names);

    describeParametersClient.asyncSendRequest(
        request, new Consumer<Future<rcl_interfaces.srv.DescribeParameters_Response>>() {
          public void accept(final Future<rcl_interfaces.srv.DescribeParameters_Response> future) {
            List<rcl_interfaces.msg.ParameterDescriptor> parameterDescriptors = null;
            try {
              parameterDescriptors = future.get().getDescriptorsAsList();
            } catch (Exception e) {
              // TODO(esteve): do something
            }
            futureResult.set(parameterDescriptors);
            if (callback != null) {
              callback.accept(futureResult);
            }
          }
        });
    return futureResult;
  }
}
