/* Copyright 2016 Esteve Fernandez <esteve@apache.org>
 * Copyright 2016 Mickael Gaillard <mick.gaillard@gmail.com>
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
package org.ros2.rcljava;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ros2.rcljava.exception.NotInitializedException;
import org.ros2.rcljava.node.Node;

public class RCLJavaTest {

    @BeforeClass
    public static void beforeClass() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
    }

    @Test
    public void testInit() {
        boolean test = true;

        try {
            Assert.assertEquals(false, RCLJava.isInitialized());
            RCLJava.rclJavaInit();
            Assert.assertEquals(true, RCLJava.isInitialized());
        } catch (Exception e) {
            test = false;
        }

        RCLJava.shutdown();
        Assert.assertTrue("failed to initialize rclJava", test);
    }

    @Test
    public void testInitShutdown() {
        boolean test = true;

        try {
            RCLJava.rclJavaInit();
            RCLJava.shutdown();
        } catch (Exception e) {
            test = false;
        }

        Assert.assertTrue("failed to shutdown rclJava", test);
    }

    @Test
    public void testInitShutdownSequence() {
        boolean test = true;

        RCLJava.rclJavaInit();
        RCLJava.shutdown();
        try {
            RCLJava.rclJavaInit();
            RCLJava.shutdown();
        } catch (Exception e) {
            test = false;
        }

        Assert.assertTrue("failed to initialize rclJava after shutdown", test);
    }

    @Test
    public void testInitDouble() {
        boolean test = false;

        RCLJava.rclJavaInit();
        try {
            RCLJava.rclJavaInit();
        } catch (Exception e) {
            test = true;
        }

        RCLJava.shutdown();
        Assert.assertTrue("Expected Runtime error when initializing rclJava twice", test);
    }

    @Test
    public void testShutdownDouble() {
        boolean test = false;

        RCLJava.rclJavaInit();
        RCLJava.shutdown();
        try {
            RCLJava.shutdown();
        } catch (Exception e) {
            test = true;
        }

        Assert.assertTrue("Expected Runtime error when shutting down rclJava twice", test);
    }

    @Test
    public void testCreateNode() {
        boolean test = true;
        Node node = null;

        RCLJava.rclJavaInit();

        try {
            node = RCLJava.createNode("testNode");
            node.dispose();
        } catch (Exception e) {
            test = false;
        }

        RCLJava.shutdown();
        Assert.assertTrue("Expected Runtime error.", test);
        Assert.assertEquals("Bad result", node, node);
    }

    @Test
    public void testOk() {
        boolean test = true;

        RCLJava.rclJavaInit();

        try {
            test = RCLJava.ok();
            Assert.assertEquals(true, RCLJava.ok());
        } catch (Exception e) {
            test = false;
        }

        try {
            RCLJava.shutdown();
            assertEquals(false, RCLJava.ok());
            test = false;
        } catch (Exception e) {  }

        Assert.assertTrue("Expected Runtime error.", test);
    }

    @Test
    public void testNotInitializedException() {
        boolean test = false;

        try {
            RCLJava.createNode("testNode");
        } catch (NotInitializedException e) {
            test = true;
        }

        Assert.assertTrue("failed not initialized exception !", test);
    }

}
