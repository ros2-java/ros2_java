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

package org.ros2.rcljava.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.parameters.ParameterCallback;
import org.ros2.rcljava.parameters.ParameterType;
import org.ros2.rcljava.parameters.ParameterVariant;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class NodeUndeclaredParametersTest {
  private Node node;

  @BeforeClass
  public static void setupOnce() throws Exception {
    // Just to quiet down warnings
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

    RCLJava.rclJavaInit();
  }

  @AfterClass
  public static void tearDownOnce() {
    RCLJava.shutdown();
  }

  @Before
  public void setUp() {
    NodeOptions options = new NodeOptions();
    options.setAllowUndeclaredParameters(true);
    node = RCLJava.createNode("test_node", "", RCLJava.getDefaultContext(), options);
  }

  @After
  public void tearDown() {
    node.dispose();
  }

  @Test
  public final void testGetParameterNotDeclared() {
    ParameterVariant param = node.getParameter("foo");
    assertEquals("foo", param.getName());
  }

  @Test
  public final void testSetParametersAtomicallyNotDeclared() {
    List<ParameterVariant> params = new ArrayList<ParameterVariant>(Arrays.asList(
      new ParameterVariant("foo.bar", false),
      new ParameterVariant("foo.baz", true)
    ));

    node.setParametersAtomically(params);
    assertTrue(node.hasParameter("foo.bar"));
    assertFalse(node.getParameter("foo.bar").asBool());
    assertTrue(node.hasParameter("foo.baz"));
    assertTrue(node.getParameter("foo.baz").asBool());
  }

  @Test
  public final void testDescribeParameterNotDeclared() {
    rcl_interfaces.msg.ParameterDescriptor newdesc = node.describeParameter("foo.bar");
    assertEquals("foo.bar", newdesc.getName());
  }

  @Test
  public final void testGetParameterTypesNotDeclared() {
    node.declareParameter(new ParameterVariant("foo.bar", true));
    List<ParameterType> types = node.getParameterTypes(new ArrayList<String>(Arrays.asList("foo.bar", "unset")));
    assertEquals(2, types.size());
    assertEquals(ParameterType.PARAMETER_BOOL, types.get(0));
    assertEquals(ParameterType.PARAMETER_NOT_SET, types.get(1));
  }
}
