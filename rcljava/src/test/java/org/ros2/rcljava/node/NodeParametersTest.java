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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NodeParametersTest {
  private Node node;
  private boolean callbackCalled = false;

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
    node = RCLJava.createNode("test_node");
  }

  @After
  public void tearDown() {
    node.dispose();
  }

  @Test
  public final void testDeclareParameter() {
    ParameterVariant p = new ParameterVariant("foo", true);
    ParameterVariant returnp = node.declareParameter(p);
    assertTrue(node.hasParameter("foo"));
    assertEquals("foo", returnp.getName());
    assertTrue(returnp.asBool());
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testDeclareParameterTwice() {
    ParameterVariant p = new ParameterVariant("foo", true);
    node.declareParameter(p);
    node.declareParameter(p);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testDeclareCallbackRejected() {
    class MyCB implements ParameterCallback {
      public rcl_interfaces.msg.SetParametersResult callback(List<ParameterVariant> parameters) {
        return new rcl_interfaces.msg.SetParametersResult().setSuccessful(false);
      }
    }

    node.addOnSetParametersCallback(new MyCB());
    node.declareParameter(new ParameterVariant("foo.bar", true));
  }

  @Test
  public final void testDeclareParameters() {
    List<ParameterVariant> plist = new ArrayList<ParameterVariant>();
    List<rcl_interfaces.msg.ParameterDescriptor> pdlist = new ArrayList<rcl_interfaces.msg.ParameterDescriptor>();

    plist.add(new ParameterVariant("foo", true));
    pdlist.add(new rcl_interfaces.msg.ParameterDescriptor());

    node.declareParameters(plist, pdlist);
    assertTrue(node.hasParameter("foo"));
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testDeclareParametersBadListLength() {
    List<ParameterVariant> plist = new ArrayList<ParameterVariant>();
    List<rcl_interfaces.msg.ParameterDescriptor> pdlist = new ArrayList<rcl_interfaces.msg.ParameterDescriptor>();

    plist.add(new ParameterVariant("foo", true));

    node.declareParameters(plist, pdlist);
  }

  @Test
  public final void testUndeclareParameter() {
    ParameterVariant p = new ParameterVariant("foo", true);
    node.declareParameter(p);
    assertTrue(node.hasParameter("foo"));
    node.undeclareParameter("foo");
    assertFalse(node.hasParameter("foo"));
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testUndeclareParameterDoesNotExist() {
    node.undeclareParameter("foo");
  }

  @Test
  public final void testGetParameters() {
    ParameterVariant p = new ParameterVariant("foo", true);
    node.declareParameter(p);
    assertTrue(node.hasParameter("foo"));
    List<ParameterVariant> results = node.getParameters(new ArrayList<String>(Arrays.asList("foo")));
    assertEquals(1, results.size());
    assertEquals("foo", results.get(0).getName());
    assertTrue("foo", results.get(0).asBool());
  }

  @Test
  public final void testGetParameter() {
    ParameterVariant p = new ParameterVariant("foo", true);
    node.declareParameter(p);
    assertTrue(node.hasParameter("foo"));
    ParameterVariant param = node.getParameter("foo");
    assertEquals("foo", param.getName());
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testGetParameterNotDeclared() {
    ParameterVariant param = node.getParameter("foo");
  }

  @Test
  public final void testGetParameterOr() {
    ParameterVariant p = new ParameterVariant("foo", true);
    node.declareParameter(p);
    ParameterVariant param = node.getParameterOr("foo", new ParameterVariant("none", false));
    assertEquals("foo", param.getName());
  }

  @Test
  public final void testGetParameterOrNotDeclared() {
    ParameterVariant param = node.getParameterOr("foo", new ParameterVariant("none", false));
    assertEquals("none", param.getName());
  }

  @Test
  public final void testGetParametersByPrefix() {
    node.declareParameter(new ParameterVariant("foo.bar", true));
    node.declareParameter(new ParameterVariant("foo.baz", true));
    node.declareParameter(new ParameterVariant("doh.baz", true));

    Map<String, ParameterVariant> params = node.getParametersByPrefix("foo");
    assertEquals(2, params.size());
    assertEquals("foo.bar", params.get("bar").getName());
    assertEquals("foo.baz", params.get("baz").getName());
  }

  @Test
  public final void testSetParameter() {
    node.declareParameter(new ParameterVariant("foo.bar", true));
    assertTrue(node.getParameter("foo.bar").asBool());

    rcl_interfaces.msg.SetParametersResult result = node.setParameter(new ParameterVariant("foo.bar", false));
    assertTrue(result.getSuccessful());
    assertFalse(node.getParameter("foo.bar").asBool());
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testSetParameterNotDeclared() {
    node.setParameter(new ParameterVariant("foo.bar", false));
  }

  @Test
  public final void testSetParameterNotSet() {
    node.declareParameter(new ParameterVariant("foo.bar", true));
    assertTrue(node.getParameter("foo.bar").asBool());

    rcl_interfaces.msg.SetParametersResult result = node.setParameter(new ParameterVariant("foo.bar"));
    assertTrue(result.getSuccessful());
    assertFalse(node.hasParameter("foo.bar"));
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testSetParameterNotSetInvalidName() {
    node.setParameter(new ParameterVariant("unset"));
  }

  @Test
  public final void testSetParameters() {
    node.declareParameter(new ParameterVariant("foo.bar", true));
    assertTrue(node.getParameter("foo.bar").asBool());

    List<ParameterVariant> params = new ArrayList<ParameterVariant>(Arrays.asList(
      new ParameterVariant("foo.bar", false)
    ));

    List<rcl_interfaces.msg.SetParametersResult> result = node.setParameters(params);
    assertEquals(1, result.size());
    assertTrue(result.get(0).getSuccessful());
    assertFalse(node.getParameter("foo.bar").asBool());
  }

  @Test
  public final void testSetParametersPartialUpdate() {
    node.declareParameter(new ParameterVariant("foo.bar", true));
    assertTrue(node.getParameter("foo.bar").asBool());

    node.declareParameter(new ParameterVariant("foo.baz", true));
    assertTrue(node.getParameter("foo.baz").asBool());

    List<ParameterVariant> params = new ArrayList<ParameterVariant>(Arrays.asList(
      new ParameterVariant("foo.bar", false),
      new ParameterVariant("unset", true),
      new ParameterVariant("foo.baz", false)
    ));

    try {
        List<rcl_interfaces.msg.SetParametersResult> result = node.setParameters(params);
    } catch (IllegalArgumentException e) {
    }
    assertTrue(node.hasParameter("foo.bar"));
    assertFalse(node.getParameter("foo.bar").asBool());  // Got updated
    assertFalse(node.hasParameter("unset"));
    assertTrue(node.hasParameter("foo.baz"));
    assertTrue(node.getParameter("foo.baz").asBool());  // Didn't get updated because of previous failure
  }

  @Test
  public final void testSetParametersAtomicallyNoUpdate() {
    node.declareParameter(new ParameterVariant("foo.bar", true));
    assertTrue(node.getParameter("foo.bar").asBool());

    node.declareParameter(new ParameterVariant("foo.baz", true));
    assertTrue(node.getParameter("foo.baz").asBool());

    List<ParameterVariant> params = new ArrayList<ParameterVariant>(Arrays.asList(
      new ParameterVariant("foo.bar", false),
      new ParameterVariant("unset", true),
      new ParameterVariant("foo.baz", false)
    ));

    try {
      node.setParametersAtomically(params);
    } catch (IllegalArgumentException e) {
    }
    assertTrue(node.hasParameter("foo.bar"));
    assertTrue(node.getParameter("foo.bar").asBool());  // Didn't get updated because of failure
    assertFalse(node.hasParameter("unset"));
    assertTrue(node.hasParameter("foo.baz"));
    assertTrue(node.getParameter("foo.baz").asBool());  // Also didn't get updated because of failure
  }

  @Test
  public final void testDescribeParameter() {
    node.declareParameter(new ParameterVariant("foo.bar", true),
                          new rcl_interfaces.msg.ParameterDescriptor().setName("foo.bar").setDescription("description"));

    rcl_interfaces.msg.ParameterDescriptor newdesc = node.describeParameter("foo.bar");
    assertEquals("foo.bar", newdesc.getName());
    assertEquals("description", newdesc.getDescription());
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testDescribeParameterNotDeclared() {
    node.describeParameter("foo.bar");
  }

  @Test
  public final void testDescribeParameters() {
    node.declareParameter(new ParameterVariant("foo.bar", true),
                          new rcl_interfaces.msg.ParameterDescriptor().setName("foo.bar").setDescription("description"));

    node.declareParameter(new ParameterVariant("foo.baz", true),
                          new rcl_interfaces.msg.ParameterDescriptor().setName("foo.baz").setDescription("description2"));

    List<rcl_interfaces.msg.ParameterDescriptor> descs = node.describeParameters(new ArrayList<String>(Arrays.asList("foo.bar", "foo.baz")));
    assertEquals(2, descs.size());
    assertEquals("foo.bar", descs.get(0).getName());
    assertEquals("description", descs.get(0).getDescription());
    assertEquals("foo.baz", descs.get(1).getName());
    assertEquals("description2", descs.get(1).getDescription());
  }

  @Test
  public final void testGetParameterTypes() {
    node.declareParameter(new ParameterVariant("foo.bar", true));
    List<ParameterType> types = node.getParameterTypes(new ArrayList<String>(Arrays.asList("foo.bar")));
    assertEquals(1, types.size());
    assertEquals(ParameterType.PARAMETER_BOOL, types.get(0));
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testGetParameterTypesUndeclared() {
    node.declareParameter(new ParameterVariant("foo.bar", true));
    node.getParameterTypes(new ArrayList<String>(Arrays.asList("foo.bar", "unset")));
  }

  @Test
  public final void testListParameters() {
    node.declareParameter(new ParameterVariant("foo.bar", true));
    node.declareParameter(new ParameterVariant("thing.do", true));

    rcl_interfaces.msg.ListParametersResult result = node.listParameters(new ArrayList<String>(), rcl_interfaces.srv.ListParameters_Request.DEPTH_RECURSIVE);
    assertEquals(2, result.getNames().size());
  }

  @Test
  public final void testParameterCallback() {
    this.callbackCalled = false;

    class MyCB implements ParameterCallback {
      private NodeParametersTest npt;
      public MyCB(NodeParametersTest npt) {
        this.npt = npt;
      }
      public rcl_interfaces.msg.SetParametersResult callback(List<ParameterVariant> parameters) {
        rcl_interfaces.msg.SetParametersResult result = new rcl_interfaces.msg.SetParametersResult();
        result.setSuccessful(true);
        this.npt.callbackCalled = true;
        return result;
      }
    }
    node.addOnSetParametersCallback(new MyCB(this));

    node.declareParameter(new ParameterVariant("foo.bar", true));
    rcl_interfaces.msg.SetParametersResult result = node.setParameter(new ParameterVariant("foo.bar", false));
    assertTrue(result.getSuccessful());
    assertTrue(this.callbackCalled);
    assertFalse(node.getParameter("foo.bar").asBool());
  }

  @Test
  public final void testParameterCallbackRejected() {
    this.callbackCalled = false;

    class MyCB implements ParameterCallback {
      private NodeParametersTest npt;
      public MyCB(NodeParametersTest npt) {
        this.npt = npt;
      }
      public rcl_interfaces.msg.SetParametersResult callback(List<ParameterVariant> parameters) {
        rcl_interfaces.msg.SetParametersResult result = new rcl_interfaces.msg.SetParametersResult();
        result.setSuccessful(false);
        this.npt.callbackCalled = true;
        return result;
      }
    }

    node.declareParameter(new ParameterVariant("foo.bar", true));
    node.addOnSetParametersCallback(new MyCB(this));
    rcl_interfaces.msg.SetParametersResult result = node.setParameter(new ParameterVariant("foo.bar", false));
    assertFalse(result.getSuccessful());
    assertTrue(this.callbackCalled);
    assertTrue(node.getParameter("foo.bar").asBool());
  }

  @Test
  public final void testParameterRemoveCallback() {
    this.callbackCalled = false;

    class MyCB implements ParameterCallback {
      private NodeParametersTest npt;
      public MyCB(NodeParametersTest npt) {
        this.npt = npt;
      }
      public rcl_interfaces.msg.SetParametersResult callback(List<ParameterVariant> parameters) {
        rcl_interfaces.msg.SetParametersResult result = new rcl_interfaces.msg.SetParametersResult();
        result.setSuccessful(true);
        this.npt.callbackCalled = true;
        return result;
      }
    }

    node.declareParameter(new ParameterVariant("foo.bar", true));
    MyCB cb = new MyCB(this);
    node.addOnSetParametersCallback(cb);
    node.removeOnSetParametersCallback(cb);
    rcl_interfaces.msg.SetParametersResult result = node.setParameter(new ParameterVariant("foo.bar", false));
    assertTrue(result.getSuccessful());
    assertFalse(this.callbackCalled);
    assertFalse(node.getParameter("foo.bar").asBool());
  }
}
