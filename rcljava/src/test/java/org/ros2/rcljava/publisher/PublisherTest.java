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

package org.ros2.rcljava.publisher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.lang.reflect.Method;

import org.junit.BeforeClass;
import org.junit.Test;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.events.EventHandler;
import org.ros2.rcljava.publisher.statuses.LivelinessLost;
import org.ros2.rcljava.publisher.statuses.OfferedDeadlineMissed;
import org.ros2.rcljava.publisher.statuses.OfferedQosIncompatible;
import org.ros2.rcljava.exceptions.RCLException;
import org.ros2.rcljava.node.Node;

public class PublisherTest {
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
    Publisher<std_msgs.msg.String> publisher =
        node.<std_msgs.msg.String>createPublisher(std_msgs.msg.String.class, "test_topic");
    assertEquals(node.getHandle(), publisher.getNodeReference().get().getHandle());
    assertNotEquals(0, publisher.getNodeReference().get().getHandle());
    assertNotEquals(0, publisher.getHandle());
    assertEquals(1, node.getPublishers().size());

    // We expect that calling dispose should result in a zero handle
    // and the reference is dropped from the Node
    publisher.dispose();
    assertEquals(0, publisher.getHandle());
    assertEquals(0, node.getPublishers().size());

    RCLJava.shutdown();
  }

  @Test
  public final void testCreateLivelinessLostEvent() {
    RCLJava.rclJavaInit();
    Node node = RCLJava.createNode("test_node");
    Publisher<std_msgs.msg.String> publisher =
        node.<std_msgs.msg.String>createPublisher(std_msgs.msg.String.class, "test_topic");
    EventHandler eventHandler = publisher.createEventHandler(
      LivelinessLost.factory, new Consumer<LivelinessLost>() {
        public void accept(final LivelinessLost status) {
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
  public final void testCreateOfferedQosIncompatibleEvent() {
    String identifier = RCLJava.getRMWIdentifier();
    if (identifier.equals("rmw_fastrtps_cpp") || identifier.equals("rmw_fastrtps_dynamic_cpp")) {
      // event not supported in these implementations
      return;
    }
    RCLJava.rclJavaInit();
    Node node = RCLJava.createNode("test_node");
    Publisher<std_msgs.msg.String> publisher =
        node.<std_msgs.msg.String>createPublisher(std_msgs.msg.String.class, "test_topic");
    EventHandler eventHandler = publisher.createEventHandler(
      OfferedQosIncompatible.factory, new Consumer<OfferedQosIncompatible>() {
        public void accept(final OfferedQosIncompatible status) {
          assertEquals(status.totalCount, 0);
          assertEquals(status.totalCountChange, 0);
          assertEquals(status.lastPolicyKind, OfferedQosIncompatible.PolicyKind.INVALID);
        }
      }
    );
    assertNotEquals(0, eventHandler.getHandle());
    assertEquals(1, publisher.getEventHandlers().size());
    // force executing the callback, so we check that taking an event works
    eventHandler.executeCallback();
    eventHandler.dispose();
    assertEquals(0, eventHandler.getHandle());
    assertEquals(0, publisher.getEventHandlers().size());
    RCLJava.shutdown();
  }

  @Test
  public final void testCreateOfferedDeadlineMissedEvent() {
    RCLJava.rclJavaInit();
    Node node = RCLJava.createNode("test_node");
    Publisher<std_msgs.msg.String> publisher =
        node.<std_msgs.msg.String>createPublisher(std_msgs.msg.String.class, "test_topic");
    EventHandler eventHandler = publisher.createEventHandler(
      OfferedDeadlineMissed.factory, new Consumer<OfferedDeadlineMissed>() {
        public void accept(final OfferedDeadlineMissed status) {
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
}
