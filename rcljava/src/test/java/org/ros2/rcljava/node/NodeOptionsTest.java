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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.node.Node;

public class NodeOptionsTest {
  @BeforeClass
  public static void setupOnce() throws Exception {
    // Just to quiet down warnings
    org.apache.log4j.BasicConfigurator.configure();

    RCLJava.rclJavaInit();
  }

  @AfterClass
  public static void tearDownOnce() {
    RCLJava.shutdown();
  }

  @Test
  public final void testCreateNodeWithArgs() {
    ArrayList<String> args = new ArrayList<String>(Arrays.asList("--ros-args", "-r", "__ns:=/foo"));
    //ArrayList<String> args = new ArrayList<String>();
    Node node = RCLJava.createNode("test_node", "", RCLJava.getDefaultContext(), true, true, args, false);
    assertEquals("test_node", node.getName());
    assertEquals("/foo", node.getNamespace());

    node.dispose();
  }
}
