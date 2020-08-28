/* Copyright 2016-2018 Esteve Fernandez <esteve@apache.org>
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

package org.ros2.rcljava.subscription;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.lang.reflect.Method;

import org.junit.BeforeClass;
import org.junit.Test;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.events.EventHandler;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.subscription.statuses.LivelinessChanged;
import org.ros2.rcljava.subscription.statuses.RequestedDeadlineMissed;
import org.ros2.rcljava.subscription.statuses.RequestedQosIncompatible;

public class SubscriptionTest {
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
  }

  @Test
  public final void testCreateAndDispose() {
    RCLJava.rclJavaInit();
    Node node = RCLJava.createNode("test_node");
    Subscription<std_msgs.msg.String> subscription = node.<std_msgs.msg.String>createSubscription(
        std_msgs.msg.String.class, "test_topic", new Consumer<std_msgs.msg.String>() {
          public void accept(final std_msgs.msg.String msg) {}
        });
    assertEquals(node.getHandle(), subscription.getNodeReference().get().getHandle());
    assertNotEquals(0, subscription.getNodeReference().get().getHandle());
    assertNotEquals(0, subscription.getHandle());
    assertEquals(1, node.getSubscriptions().size());

    // We expect that calling dispose should result in a zero handle
    // and the reference is dropped from the Node
    subscription.dispose();
    assertEquals(0, subscription.getHandle());
    assertEquals(0, node.getSubscriptions().size());

    RCLJava.shutdown();
  }

  @Test
  public final void testCreateLivelinessChangedEvent() {
    String identifier = RCLJava.getRMWIdentifier();
    RCLJava.rclJavaInit();
    Node node = RCLJava.createNode("test_node");
    Subscription<std_msgs.msg.String> subscription = node.<std_msgs.msg.String>createSubscription(
      std_msgs.msg.String.class, "test_topic", new Consumer<std_msgs.msg.String>() {
        public void accept(final std_msgs.msg.String msg) {}
      });
    EventHandler eventHandler = subscription.createEventHandler(
      LivelinessChanged.factory, new Consumer<LivelinessChanged>() {
        public void accept(final LivelinessChanged status) {
          assertEquals(status.aliveCount, 0);
          assertEquals(status.notAliveCount, 0);
          assertEquals(status.aliveCountChange, 0);
          assertEquals(status.notAliveCountChange, 0);
        }
      }
    );
    assertNotEquals(0, eventHandler.getHandle());
    // force executing the callback, so we check that taking an event works
    eventHandler.executeCallback();
    RCLJava.shutdown();
    assertEquals(0, eventHandler.getHandle());
  }

  @Test
  public final void testCreateRequestedDeadlineMissedEvent() {
    String identifier = RCLJava.getRMWIdentifier();
    RCLJava.rclJavaInit();
    Node node = RCLJava.createNode("test_node");
    Subscription<std_msgs.msg.String> subscription = node.<std_msgs.msg.String>createSubscription(
      std_msgs.msg.String.class, "test_topic", new Consumer<std_msgs.msg.String>() {
        public void accept(final std_msgs.msg.String msg) {}
      });
    EventHandler eventHandler = subscription.createEventHandler(
      RequestedDeadlineMissed.factory, new Consumer<RequestedDeadlineMissed>() {
        public void accept(final RequestedDeadlineMissed status) {
          assertEquals(status.totalCount, 0);
          assertEquals(status.totalCountChange, 0);
        }
      }
    );
    assertNotEquals(0, eventHandler.getHandle());
    // force executing the callback, so we check that taking an event works
    eventHandler.executeCallback();
    RCLJava.shutdown();
    assertEquals(0, eventHandler.getHandle());
  }

  @Test
  public final void testCreateRequestedQosIncompatibleEvent() {
    String identifier = RCLJava.getRMWIdentifier();
    if (identifier.equals("rmw_fastrtps_cpp") || identifier.equals("rmw_fastrtps_dynamic_cpp")) {
      // event not supported in these implementations
      return;
    }
    RCLJava.rclJavaInit();
    Node node = RCLJava.createNode("test_node");
    Subscription<std_msgs.msg.String> subscription = node.<std_msgs.msg.String>createSubscription(
      std_msgs.msg.String.class, "test_topic", new Consumer<std_msgs.msg.String>() {
        public void accept(final std_msgs.msg.String msg) {}
      });
    EventHandler eventHandler = subscription.createEventHandler(
      RequestedQosIncompatible.factory, new Consumer<RequestedQosIncompatible>() {
        public void accept(final RequestedQosIncompatible status) {
          assertEquals(status.totalCount, 0);
          assertEquals(status.totalCountChange, 0);
          assertEquals(status.lastPolicyKind, RequestedQosIncompatible.PolicyKind.INVALID);
        }
      }
    );
    assertNotEquals(0, eventHandler.getHandle());
    // force executing the callback, so we check that taking an event works
    eventHandler.executeCallback();
    RCLJava.shutdown();
    assertEquals(0, eventHandler.getHandle());
  }
}
