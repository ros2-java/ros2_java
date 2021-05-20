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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.concurrent.Future;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.concurrent.RCLFuture;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.parameters.ParameterVariant;
import org.ros2.rcljava.parameters.client.SyncParametersClient;
import org.ros2.rcljava.parameters.client.SyncParametersClientImpl;
import org.ros2.rcljava.parameters.service.ParameterService;
import org.ros2.rcljava.parameters.service.ParameterServiceImpl;

public class SyncParametersClientTest {

  private Node node;
  private ParameterService parameterService;
  private SyncParametersClient parametersClient;

  @BeforeClass
  public static void setupOnce() throws Exception {
    RCLJava.rclJavaInit();
    try
    {
      // Configure log4j. Doing this dynamically so that Android does not complain about missing
      // the log4j JARs, SLF4J uses Android's native logging mechanism instead.
      Class c = Class.forName("org.apache.log4j.BasicConfigurator");
      Method m = c.getDeclaredMethod("configure", (Class<?>[]) null);
      Object o = m.invoke(null, (Object[]) null);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @Before
  public void setUp() throws Exception {
    node = RCLJava.createNode("test_node");
    parameterService = new ParameterServiceImpl(node);
    parametersClient = new SyncParametersClientImpl(node);
  }

  @After
  public void tearDown() {
    node.dispose();
  }

  @AfterClass
  public static void tearDownOnce() {
    RCLJava.shutdown();
  }

  @Test
  public final void testSetParameters() throws Exception {
    List<ParameterVariant> parameters = Arrays.asList(new ParameterVariant[] {
        new ParameterVariant("foo", 2), new ParameterVariant("bar", "hello"),
        new ParameterVariant("baz", 1.45), new ParameterVariant("foo.first", 8),
        new ParameterVariant("foo.second", 42), new ParameterVariant("foobar", true)});

    List<rcl_interfaces.msg.SetParametersResult> setParametersResults =
        parametersClient.setParameters(parameters);

    List<String> parameterNames =
        Arrays.asList(new String[] {"foo", "bar", "baz", "foo.first", "foo.second", "foobar"});

    List<ParameterVariant> results = node.getParameters(parameterNames);
    assertEquals(parameters, results);
  }

  @Test
  public final void testGetParameters() throws Exception {
    List<ParameterVariant> parameters = Arrays.asList(new ParameterVariant[] {
        new ParameterVariant("foo", 2), new ParameterVariant("bar", "hello"),
        new ParameterVariant("baz", 1.45), new ParameterVariant("foo.first", 8),
        new ParameterVariant("foo.second", 42), new ParameterVariant("foobar", true)});

    node.setParameters(parameters);

    List<String> parameterNames =
        Arrays.asList(new String[] {"foo", "bar", "baz", "foo.first", "foo.second", "foobar"});

    List<ParameterVariant> getParametersResults = parametersClient.getParameters(parameterNames);

    assertEquals(parameters, getParametersResults);
  }

  @Test
  public final void testListParameters() throws Exception {
    List<ParameterVariant> parameters = Arrays.asList(new ParameterVariant[] {
        new ParameterVariant("foo", 2), new ParameterVariant("bar", "hello"),
        new ParameterVariant("baz", 1.45), new ParameterVariant("foo.first", 8),
        new ParameterVariant("foo.second", 42), new ParameterVariant("foobar", true)});

    node.setParameters(parameters);

    rcl_interfaces.msg.ListParametersResult parametersAndPrefixes =
        parametersClient.listParameters(Arrays.asList(new String[] {"foo", "bar"}), 10);

    assertArrayEquals(
        parametersAndPrefixes.getNames(), new String[] {"foo.first", "foo.second"});
    assertEquals(parametersAndPrefixes.getPrefixes(), new String[] {"foo"});
  }

  @Test
  public final void testDescribeParameters() throws Exception {
    List<ParameterVariant> parameters = Arrays.asList(new ParameterVariant[] {
        new ParameterVariant("foo", 2), new ParameterVariant("bar", "hello"),
        new ParameterVariant("baz", 1.45), new ParameterVariant("foo.first", 8),
        new ParameterVariant("foo.second", 42), new ParameterVariant("foobar", true)});

    node.setParameters(parameters);

    List<rcl_interfaces.msg.ParameterDescriptor> results =
        parametersClient.describeParameters(Arrays.asList(new String[] {"foo", "bar"}));

    List<rcl_interfaces.msg.ParameterDescriptor> expected =
        Arrays.asList(new rcl_interfaces.msg.ParameterDescriptor[] {
            new rcl_interfaces.msg.ParameterDescriptor().setName("foo").setType(
                rcl_interfaces.msg.ParameterType.PARAMETER_INTEGER),
            new rcl_interfaces.msg.ParameterDescriptor().setName("bar").setType(
                rcl_interfaces.msg.ParameterType.PARAMETER_STRING)});
    assertEquals(expected, results);
  }
}
