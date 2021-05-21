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

package org.ros2.rcljava.parameters.service;

import java.util.ArrayList;
import java.util.List;

import org.ros2.rcljava.consumers.TriConsumer;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.parameters.ParameterNames;
import org.ros2.rcljava.parameters.ParameterType;
import org.ros2.rcljava.parameters.ParameterVariant;
import org.ros2.rcljava.qos.QoSProfile;
import org.ros2.rcljava.service.RMWRequestId;
import org.ros2.rcljava.service.Service;

public class ParameterServiceImpl implements ParameterService {
  private final Node node;

  Service<rcl_interfaces.srv.GetParameters> getParametersService;
  Service<rcl_interfaces.srv.GetParameterTypes> getParameterTypesService;
  Service<rcl_interfaces.srv.SetParameters> setParametersService;
  Service<rcl_interfaces.srv.SetParametersAtomically> setParametersAtomicallyService;
  Service<rcl_interfaces.srv.DescribeParameters> describeParametersService;
  Service<rcl_interfaces.srv.ListParameters> listParametersService;

  public ParameterServiceImpl(final Node node, final QoSProfile qosProfile)
  {
    this.node = node;
    this.getParametersService = node.<rcl_interfaces.srv.GetParameters>createService(
        rcl_interfaces.srv.GetParameters.class,
        node.getName() + "/" + ParameterNames.GET_PARAMETERS,
        new TriConsumer<RMWRequestId, rcl_interfaces.srv.GetParameters_Request,
            rcl_interfaces.srv.GetParameters_Response>() {
          public void accept(RMWRequestId rmwRequestId,
              rcl_interfaces.srv.GetParameters_Request request,
              rcl_interfaces.srv.GetParameters_Response response) {
            List<ParameterVariant> values = node.getParameters(request.getNamesAsList());
            List<rcl_interfaces.msg.ParameterValue> pvalues =
                new ArrayList<rcl_interfaces.msg.ParameterValue>();
            for (ParameterVariant pvariant : values) {
              pvalues.add(pvariant.getParameterValue());
            }
            response.setValues(pvalues);
          }
        },
        qosProfile);

    this.getParameterTypesService = node.<rcl_interfaces.srv.GetParameterTypes>createService(
        rcl_interfaces.srv.GetParameterTypes.class,
        node.getName() + "/" + ParameterNames.GET_PARAMETER_TYPES,
        new TriConsumer<RMWRequestId, rcl_interfaces.srv.GetParameterTypes_Request,
            rcl_interfaces.srv.GetParameterTypes_Response>() {
          public void accept(RMWRequestId rmwRequestId,
              rcl_interfaces.srv.GetParameterTypes_Request request,
              rcl_interfaces.srv.GetParameterTypes_Response response) {
            List<ParameterType> types = node.getParameterTypes(request.getNamesAsList());
            List<Byte> ptypes = new ArrayList<Byte>();
            for (ParameterType type : types) {
              ptypes.add(type.getValue());
            }
            response.setTypes(ptypes);
          }
        },
        qosProfile);

    this.setParametersService = node.<rcl_interfaces.srv.SetParameters>createService(
        rcl_interfaces.srv.SetParameters.class,
        node.getName() + "/" + ParameterNames.SET_PARAMETERS,
        new TriConsumer<RMWRequestId, rcl_interfaces.srv.SetParameters_Request,
            rcl_interfaces.srv.SetParameters_Response>() {
          public void accept(RMWRequestId rmwRequestId,
              rcl_interfaces.srv.SetParameters_Request request,
              rcl_interfaces.srv.SetParameters_Response response) {
            List<ParameterVariant> pvariants = new ArrayList<ParameterVariant>();
            for (rcl_interfaces.msg.Parameter p : request.getParameters()) {
              pvariants.add(ParameterVariant.fromParameter(p));
            }
            List<rcl_interfaces.msg.SetParametersResult> results = node.setParameters(pvariants);
            response.setResults(results);
          }
        },
        qosProfile);

    this.setParametersAtomicallyService =
        node.<rcl_interfaces.srv.SetParametersAtomically>createService(
            rcl_interfaces.srv.SetParametersAtomically.class,
            node.getName() + "/" + ParameterNames.SET_PARAMETERS_ATOMICALLY,
            new TriConsumer<RMWRequestId, rcl_interfaces.srv.SetParametersAtomically_Request,
                rcl_interfaces.srv.SetParametersAtomically_Response>() {
              public void accept(RMWRequestId rmwRequestId,
                  rcl_interfaces.srv.SetParametersAtomically_Request request,
                  rcl_interfaces.srv.SetParametersAtomically_Response response) {
                List<ParameterVariant> pvariants = new ArrayList<ParameterVariant>();
                for (rcl_interfaces.msg.Parameter p : request.getParameters()) {
                  pvariants.add(ParameterVariant.fromParameter(p));
                }
                rcl_interfaces.msg.SetParametersResult result =
                    node.setParametersAtomically(pvariants);
                response.setResult(result);
              }
            },
            qosProfile);

    this.describeParametersService = node.<rcl_interfaces.srv.DescribeParameters>createService(
        rcl_interfaces.srv.DescribeParameters.class,
        node.getName() + "/" + ParameterNames.DESCRIBE_PARAMETERS,
        new TriConsumer<RMWRequestId, rcl_interfaces.srv.DescribeParameters_Request,
            rcl_interfaces.srv.DescribeParameters_Response>() {
          public void accept(RMWRequestId rmwRequestId,
              rcl_interfaces.srv.DescribeParameters_Request request,
              rcl_interfaces.srv.DescribeParameters_Response response) {
            List<rcl_interfaces.msg.ParameterDescriptor> descriptors =
                node.describeParameters(request.getNamesAsList());
            response.setDescriptors(descriptors);
          }
        },
        qosProfile);

    this.listParametersService = node.<rcl_interfaces.srv.ListParameters>createService(
        rcl_interfaces.srv.ListParameters.class,
        node.getName() + "/" + ParameterNames.LIST_PARAMETERS,
        new TriConsumer<RMWRequestId, rcl_interfaces.srv.ListParameters_Request,
            rcl_interfaces.srv.ListParameters_Response>() {
          public void accept(RMWRequestId rmwRequestId,
              rcl_interfaces.srv.ListParameters_Request request,
              rcl_interfaces.srv.ListParameters_Response response) {
            rcl_interfaces.msg.ListParametersResult result =
                node.listParameters(request.getPrefixesAsList(), request.getDepth());
            response.setResult(result);
          }
        },
        qosProfile);
  }

  public ParameterServiceImpl(final Node node) {
    this(node, QoSProfile.PARAMETERS);
  }
}
