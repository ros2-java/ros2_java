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

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.exception.ImplementationAlreadyImportedException;
import org.ros2.rcljava.exception.NoImplementationAvailableException;

/**
 *
 */
public class RmwTest {

    @BeforeClass
    public static void beforeClass() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
    }

    @Test
    public void testRmwFastrtps() {
        boolean test = true;

        try {
            RCLJava.setRMWImplementation("rmw_fastrtps_cpp");
            RCLJava.rclJavaInit();
            RCLJava.shutdown();
        } catch (Exception e) {
            test = false;
        } finally {
            try {
                RCLJava.setRMWImplementation(null);
            } catch (Exception e) { }
        }

        Assert.assertTrue("failed to initialize rclJava with Fastrtps", test);
    }

    @Ignore
    @Test
    public void testRmwOpensplice() {
        boolean test = true;

        try {
            RCLJava.setRMWImplementation("rmw_opensplice_cpp");
            RCLJava.rclJavaInit();
            RCLJava.shutdown();
        } catch (Exception e) {
            test = false;
        } finally {
            try {
                RCLJava.setRMWImplementation(null);
            } catch (Exception e) { }
        }

        Assert.assertTrue("failed to initialize rclJava with Opensplice", test);
    }

    @Ignore
    @Test
    public void testRmwConnext() {
        boolean test = true;

        try {
            RCLJava.setRMWImplementation("rmw_connext_cpp");
            RCLJava.rclJavaInit();
            RCLJava.shutdown();
        } catch (Exception e) {
            test = false;
        } finally {
            try {
                RCLJava.setRMWImplementation(null);
            } catch (Exception e) { }
        }

        Assert.assertTrue("failed to initialize rclJava with Connext", test);
    }

    @Ignore
    @Test
    public void testRmwConnextDynamic() {
        boolean test = true;

        try {
            RCLJava.setRMWImplementation("rmw_connext_dynamic_cpp");
            RCLJava.rclJavaInit();
            RCLJava.shutdown();
        } catch (Exception e) {
            test = false;
        } finally {
            try {
                RCLJava.setRMWImplementation(null);
            } catch (Exception e) { }
        }

        Assert.assertTrue("failed to initialize rclJava with Connext Dynamic", test);
    }

    @Test
    public void testNoImplementationAvailableException() {
        boolean test = false;

        try {
            RCLJava.setRMWImplementation("foo");
        } catch (NoImplementationAvailableException e) {
            test = true;
        } catch (ImplementationAlreadyImportedException e) {
            test = false;
        } finally {
            try {
                RCLJava.setRMWImplementation(null);
            } catch (Exception e) { }
        }

        Assert.assertTrue("failed not implementation available exception !", test);
    }

    @Test
    public void testImplementationAlreadyImportedException() {
        boolean test = false;

        try {
            RCLJava.setRMWImplementation("rmw_fastrtps_cpp");
            RCLJava.setRMWImplementation("rmw_fastrtps_cpp");
        } catch (ImplementationAlreadyImportedException e) {
            test = true;
        } catch (NoImplementationAvailableException e) {
            test = false;
        }  finally {
            try {
                RCLJava.setRMWImplementation(null);
            } catch (Exception e) { }
        }

        Assert.assertTrue("failed implementation already imported exception !", test);
    }
}
