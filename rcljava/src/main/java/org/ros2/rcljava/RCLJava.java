/* Copyright 2016 Esteve Fernandez <esteve@apache.org>
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

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import org.ros2.rcljava.exception.NoImplementationAvailableException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * ROS2 java client wrapper.
 *
 * <p>JNI call of ROS2 c client.</p>
 *
 * @author Esteve Fernandez <esteve@apache.org>
 */
public class RCLJava {

    public static final String LOG_NAME = RCLJava.class.getName();

    private static Logger logger = Logger.getLogger(LOG_NAME);

    /** Global List/Queue of publishers. */
    public static Queue<WeakReference<Publisher<?>>> publisherReferences =
            new LinkedBlockingQueue<WeakReference<Publisher<?>>>();

    /** Current ROS2 middleware implementation use. */
    private static String rmwImplementation = null;

    /** ROS2 client is initialized. */
    private static boolean initialized = false;

    /** List of ROS2 middleware supported. */
    private static final Map<String, String> rmwToTypesupport = new ConcurrentSkipListMap<String, String>() {
        /** Serial Id */
        private static final long serialVersionUID = 1L;

        {
            put("rmw_fastrtps_cpp",         "rosidl_typesupport_introspection_c");
            put("rmw_opensplice_cpp",       "rosidl_typesupport_opensplice_c");
            put("rmw_connext_cpp",          "rosidl_typesupport_connext_c");
            put("rmw_connext_dynamic_cpp",  "rosidl_typesupport_introspection_c");
        }
    };

    // Natives definitions
    private static native void nativeRCLJavaInit();
    private static native long nativeCreateNodeHandle(String nodeName);
    private static native boolean nativeOk();
    private static native String nativeGetRMWIdentifier();
    private static native void nativeShutdown();
    private static native long nativeGetZeroInitializedWaitSet();
    private static native void nativeWaitSetInit(
            long waitSetHandle, int numberOfSubscriptions, int numberOfGuardConditions, int numberOfTimers);
    private static native void nativeWaitSetClearSubscriptions(long waitSetHandle);
    private static native void nativeWaitSetAddSubscription(long waitSetHandle, long subscriptionHandle);
    private static native void nativeWait(long waitSetHandle);
    private static native Object nativeTake(long SubscriptionHandle, Class<?> msgType);
    //private static native ArrayList<String> nativeGetNodeNames();
    //private static native HashMap<String, Class<?>> nativeGetRemoteTopicNamesAndTypes();

    /** Release all ressources at shutdown. */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (RCLJava.initialized) {
                    logger.fine("Final Shutdown...");

                    String[] list = NativeUtils.getLoadedLibraries(RCLJava.class.getClassLoader());
                    StringBuilder msgLog = new StringBuilder();
                    for (String key : list) {
                        msgLog.append(key);
                        msgLog.append("\n");
                    }
                    logger.fine("Native libraries Loaded: \n" + msgLog.toString());

                    for(WeakReference<Publisher<?>> publisherReference : RCLJava.publisherReferences) {
                        if(publisherReference.get() != null) {
                            publisherReference.get().dispose();
                        }
                    }
                }
            }
        });
    }

    /**
     * Global initialization of rcl.
     *
     * <p>Unless otherwise noted, this must be called before using any rcl functions.</p>
     *
     * <p>This function can only be run once after starting the program, and once
     * after each call to rcl_shutdown. Repeated calls will fail with
     * RCL_RET_ALREADY_INIT. This function is not thread safe.</p>
     */
    public static void rclJavaInit(String... args) {
        synchronized (RCLJava.class) {
            if (!RCLJava.initialized) {
                if (RCLJava.rmwImplementation == null) {
                    String libpath = System.getProperty("java.library.path");
                    logger.fine("Native Library path : \n" + libpath.replace(':', '\n'));

                    for (Map.Entry<String, String> entry : rmwToTypesupport.entrySet()) {
                        try {
                            logger.config("Try to load native " + entry.getKey() + "...");
                            RCLJava.setRMWImplementation(entry.getKey());
                            logger.config(entry.getKey() + " loaded !");
                            break;
                        } catch (NoImplementationAvailableException e) {
                            logger.config(entry.getKey() + " not available ! (" + e.getMessage() + ")");
                        }
                    }
                }

                if (RCLJava.rmwImplementation == null) {
                    logger.severe("No RMW implementation found...");
                    System.exit(1);
                } else {
                    RCLJava.nativeRCLJavaInit();
                    RCLJava.initialized = true;
                }
            }
        }
    }

    /**
     * Get Node name
     *
     * @return
     */
    public static ArrayList<String> getNodeNames() {
        return new ArrayList<String>();
//        return RCLJava.nativeGetNodeNames();
    }

    /**
     * Get Topic (name & type).
     * @return
     */
    public static HashMap<String, Class<?>> getRemoteTopic() {
        return new HashMap<String, Class<?>>();
//        return RCLJava.nativeGetRemoteTopicNamesAndTypes();
    }

    /**
     * Node Reference of native node.
     *
     * @param nodeName Name of the node.
     * @return Instance of Node Reference.
     */
    public static Node createNode(String nodeName) {
        logger.fine("Create Node stack : " + nodeName);
        long nodeHandle = RCLJava.nativeCreateNodeHandle(nodeName);
        return new Node(nodeHandle);
    }

    /**
     * Wait for once loop.
     *
     * @param node
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void spinOnce(Node node) {
        long waitSetHandle = RCLJava.nativeGetZeroInitializedWaitSet();

        RCLJava.nativeWaitSetInit(waitSetHandle, node.getSubscriptions().size(), 0, 0);

        RCLJava.nativeWaitSetClearSubscriptions(waitSetHandle);
        for(Subscription<?> subscription : node.getSubscriptions()) {
            RCLJava.nativeWaitSetAddSubscription(waitSetHandle, subscription.getSubscriptionHandle());
        }

        RCLJava.nativeWait(waitSetHandle);

        for(Subscription subscription : node.getSubscriptions()) {
            Object msg = RCLJava.nativeTake(subscription.getSubscriptionHandle(), subscription.getMsgType());
            if (msg != null) {
                subscription.getCallback().accept(msg);
            }
        }
    }

    /**
     * Helper Spin.
     * @param node
     */
    public static void spin(Node node) {
        while(RCLJava.ok()) {
            RCLJava.spinOnce(node);
        }
    }

    /**
     *
     * @param node
     * @param topic
     * @return
     */
    public static Object waitForMessage(Node node, String topic) {
        Object msg = null;
        long waitSetHandle = RCLJava.nativeGetZeroInitializedWaitSet();

        RCLJava.nativeWaitSetInit(waitSetHandle, 1, 0, 0);

        RCLJava.nativeWaitSetClearSubscriptions(waitSetHandle);
        for(Subscription<?> subscription : node.getSubscriptions()) {
            if (subscription.getTopic() == topic) {
                RCLJava.nativeWaitSetAddSubscription(waitSetHandle, subscription.getSubscriptionHandle());
            }
        }

        RCLJava.nativeWait(waitSetHandle);

        for(Subscription<?> subscription : node.getSubscriptions()) {
            if (subscription.getTopic() == topic) {
                msg = RCLJava.nativeTake(subscription.getSubscriptionHandle(), subscription.getMsgType());
                if (msg != null) {
                    break;
                }
            }
        }

        return msg;
    }

    /**
     * Return true if rcl is currently initialized, otherwise false.
     *
     * @see rcl_ok();
     * @return true if the node is valid, else false.
     */
    public static boolean ok() {
        return RCLJava.nativeOk();
    }

    /**
     * Signal global shutdown of RCLJava.
     *
     * <p>This function does not have to be called on exit, but does have to be called making a
     * repeat call to RCLJava.rclJavaInit.</p>
     *
     * <p>This function can only be called once after each call to RCLJava.rclJavaInit.</p>
     */
    public static void shutdown() {
        logger.fine("Shutdown...");
        RCLJava.nativeShutdown();
        RCLJava.initialized = false;
        //RCLJava.rmwImplementation = null; // For check test
    }

    /**
     * Get ROS Midleware indentifier.
     *
     * @return indentifier.
     */
    public static String getRMWIdentifier() {
        return RCLJava.nativeGetRMWIdentifier();
    }

    /**
     * Get identifier of the ROS2 middleware use.
     *
     * <p>TODO rename to list of RMW available.</p>
     *
     * @return Identifier string of ROS2 middleware.
     */
    public static String getTypesupportIdentifier() {
        String typesupportIdentifier = rmwToTypesupport.get(RCLJava.getRMWIdentifier());

        return typesupportIdentifier;
    }

    /**
     * Switch of ROS2 middleware implementation
     *
     * <p>TODO need to check implementation available.</p>
     *
     * @param rmwImplementation
     * @throws NoImplementationAvailableException
     */
    public static void setRMWImplementation(String rmwImplementation)
            throws NoImplementationAvailableException {

        synchronized(RCLJava.class) {
            String file = "rcljavaRCLJava__" + rmwImplementation;
            logger.fine("Load native file : lib" + file + ".so");

            try {
                System.loadLibrary(file);
                RCLJava.rmwImplementation = rmwImplementation;
            } catch (UnsatisfiedLinkError ule) {
                throw new NoImplementationAvailableException(ule);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * <h1>Load Native ROS library</h1>
     * <p>load from java.library.path .</p>
     * @param name Name of the library.
     */
    public static void loadLibrary(String name) {
        synchronized(RCLJava.class) {
            logger.fine("Load native file : lib" + name + ".so");
            try {
                System.loadLibrary(name);
            } catch (UnsatisfiedLinkError e) {
                System.err.println("Native code library failed to load.\n" + e);
                System.exit(1);
            }
        }
    }
}
