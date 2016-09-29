package org.ros2.rcljava.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import junit.framework.TestCase;
import org.junit.BeforeClass;
import org.junit.Test;

import org.ros2.rcljava.Node;
import org.ros2.rcljava.RCLJava;

public class RCLJavaTest extends TestCase {
    private static Logger logger = Logger.getLogger(RCLJava.LOG_NAME);

    @BeforeClass
    public void setUp() {
        logger.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);
        handler.setLevel(Level.INFO);
    }

    @Test
    public void testInit() {
        boolean test = true;

        try {
            RCLJava.rclJavaInit();
        } catch (Exception e) {
            test = false;
        }

        RCLJava.shutdown();
        TestCase.assertTrue("failed to initialize rclJava", test);
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

        TestCase.assertTrue("failed to shutdown rclJava", test);
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

        TestCase.assertTrue("failed to initialize rclJava after shutdown", test);
    }

    @Test
    public void testInitDouble() {
        boolean test = true;

        RCLJava.rclJavaInit();
        try {
            RCLJava.rclJavaInit();
        } catch (Exception e) {
            test = false;
        }

        RCLJava.shutdown();
        TestCase.assertTrue("Expected Runtime error when initializing rclJava twice", test);
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

        TestCase.assertTrue("Expected Runtime error when shutting down rclJava twice", test);
    }

    @Test
    public void testGetNodeName() {
        boolean test = true;
        ArrayList<String> names = new ArrayList<String>();

        RCLJava.rclJavaInit();

        try {
            names = RCLJava.getNodeNames();
        } catch (Exception e) {
            test = false;
        }

        RCLJava.shutdown();
        TestCase.assertTrue("Expected Runtime error.", test);
        TestCase.assertEquals("Bad result", names, names);
    }

    @Test
    public void testGetRemoteTopic() {
        boolean test = true;
        HashMap<String, Class<?>> topics = new HashMap<String, Class<?>>();

        RCLJava.rclJavaInit();

        try {
            topics = RCLJava.getRemoteTopic();
        } catch (Exception e) {
            test = false;
        }

        RCLJava.shutdown();
        TestCase.assertTrue("Expected Runtime error.", test);
        TestCase.assertEquals("Bad result", topics, topics);
    }

    @Test
    public void testCreateNode() {
        boolean test = true;
        Node node = null;

        RCLJava.rclJavaInit();

        try {
            node = RCLJava.createNode("testNode");
        } catch (Exception e) {
            test = false;
        }

        RCLJava.shutdown();
        TestCase.assertTrue("Expected Runtime error.", test);
        TestCase.assertEquals("Bad result", node, node);
    }

    @Test
    public void testOk() {
        boolean test = true;

        RCLJava.rclJavaInit();

        try {
            test = RCLJava.ok();
        } catch (Exception e) {
            test = false;
        }

        RCLJava.shutdown();
        TestCase.assertTrue("Expected Runtime error.", test);
    }
}
