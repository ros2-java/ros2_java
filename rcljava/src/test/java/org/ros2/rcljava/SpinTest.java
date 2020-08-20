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

package org.ros2.rcljava;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.lang.System;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.concurrent.Callback;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.events.EventHandler;
import org.ros2.rcljava.events.publisher_statuses.OfferedQosIncompatible;
import org.ros2.rcljava.executors.Executor;
import org.ros2.rcljava.executors.SingleThreadedExecutor;
import org.ros2.rcljava.node.ComposableNode;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.timer.WallTimer;
import org.ros2.rcljava.publisher.Publisher;
import org.ros2.rcljava.qos.policies.Durability;
import org.ros2.rcljava.qos.policies.History;
import org.ros2.rcljava.qos.policies.Reliability;
import org.ros2.rcljava.qos.QoSProfile;
import org.ros2.rcljava.subscription.Subscription;

public class SpinTest {
  public static class TimerCallback implements Callback {
    private int counter;
    private final long timeToSleepMs;

    TimerCallback(long sleepTimeMs) {
      timeToSleepMs = sleepTimeMs;
    }

    public void call() {
      this.counter++;
      try {
        Thread.sleep(timeToSleepMs);
      } catch (InterruptedException ex) {
        // We do nothing on exception here; if we didn't wait long enough, then
        // the assert below will fail
      }
    }

    public int getCounter() {
      return this.counter;
    }
  }

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

  @Test
  public final void testSpinOnce() {
    Executor executor = new SingleThreadedExecutor();
    final Node node = RCLJava.createNode("spin_once_node");
    TimerCallback timerCallback = new TimerCallback(0);
    WallTimer timer = node.createWallTimer(100, TimeUnit.MILLISECONDS, timerCallback);
    assertNotEquals(0, timer.getHandle());

    ComposableNode composableNode = new ComposableNode() {
      public Node getNode() {
        return node;
      }
    };

    executor.addNode(composableNode);

    executor.spinOnce();
    assertEquals(1, timerCallback.getCounter());
  }

  @Test
  public final void testSpinOnceTimeout() {
    Executor executor = new SingleThreadedExecutor();
    final Node node = RCLJava.createNode("spin_once_timeout");
    TimerCallback timerCallback = new TimerCallback(0);
    WallTimer timer = node.createWallTimer(100, TimeUnit.MILLISECONDS, timerCallback);
    assertNotEquals(0, timer.getHandle());

    ComposableNode composableNode = new ComposableNode() {
      public Node getNode() {
        return node;
      }
    };

    executor.addNode(composableNode);

    executor.spinOnce(200*1000*1000);
    assertEquals(1, timerCallback.getCounter());
  }

  @Test
  public final void testSpinOnceTimeoutTooShort() {
    Executor executor = new SingleThreadedExecutor();
    final Node node = RCLJava.createNode("spin_once_timeout_too_short_node");
    TimerCallback timerCallback = new TimerCallback(0);
    WallTimer timer = node.createWallTimer(100, TimeUnit.MILLISECONDS, timerCallback);
    assertNotEquals(0, timer.getHandle());

    ComposableNode composableNode = new ComposableNode() {
      public Node getNode() {
        return node;
      }
    };

    executor.addNode(composableNode);

    executor.spinOnce(50*1000*1000);
    assertEquals(0, timerCallback.getCounter());
  }

  @Test
  public final void testSpinSome() {
    Executor executor = new SingleThreadedExecutor();
    final Node node = RCLJava.createNode("spin_some_node");
    TimerCallback timerCallback = new TimerCallback(0);
    WallTimer timer = node.createWallTimer(100, TimeUnit.MILLISECONDS, timerCallback);
    assertNotEquals(0, timer.getHandle());

    ComposableNode composableNode = new ComposableNode() {
      public Node getNode() {
        return node;
      }
    };

    executor.addNode(composableNode);

    try {
      // The timer is configured to wake up after 100 milliseconds, so wait 200
      // which should be enough to make it trigger.
      Thread.sleep(200);
    } catch (InterruptedException ex) {
      // We do nothing on exception here; if we didn't wait long enough, then
      // the assert below will fail
    }
    executor.spinSome();
    assertEquals(1, timerCallback.getCounter());
  }

  @Test
  public final void testSpinSomeMaxDuration() {
    Executor executor = new SingleThreadedExecutor();
    final Node node = RCLJava.createNode("spin_some_max_duration_node");
    TimerCallback timerCallback = new TimerCallback(0);
    WallTimer timer = node.createWallTimer(100, TimeUnit.MILLISECONDS, timerCallback);
    assertNotEquals(0, timer.getHandle());

    ComposableNode composableNode = new ComposableNode() {
      public Node getNode() {
        return node;
      }
    };

    executor.addNode(composableNode);

    try {
      // The timer is configured to wake up after 100 milliseconds, so wait 200
      // which should be enough to make it trigger.
      Thread.sleep(200);
    } catch (InterruptedException ex) {
      // We do nothing on exception here; if we didn't wait long enough, then
      // the assert below will fail
    }
    executor.spinSome(100*1000*1000);
    assertEquals(1, timerCallback.getCounter());
  }

  @Test
  public final void testSpinSomeMaxDurationMultipleTimers() {
    Executor executor = new SingleThreadedExecutor();
    final Node node = RCLJava.createNode("spin_some_max_duration_multiple_timers_node");
    TimerCallback timerCallback = new TimerCallback(200);
    WallTimer timer = node.createWallTimer(100, TimeUnit.MILLISECONDS, timerCallback);
    WallTimer timer2 = node.createWallTimer(100, TimeUnit.MILLISECONDS, timerCallback);
    assertNotEquals(0, timer.getHandle());
    assertNotEquals(0, timer2.getHandle());

    ComposableNode composableNode = new ComposableNode() {
      public Node getNode() {
        return node;
      }
    };

    executor.addNode(composableNode);

    try {
      // The timer is configured to wake up after 100 milliseconds, so wait 200
      // which should be enough to make it trigger.
      Thread.sleep(200);
    } catch (InterruptedException ex) {
      // We do nothing on exception here; if we didn't wait long enough, then
      // the assert below will fail
    }
    executor.spinSome(400*1000*1000);
    assertEquals(2, timerCallback.getCounter());
  }

  @Test
  public final void testSpinSomeMaxDurationTooShortMultipleTimers() {
    Executor executor = new SingleThreadedExecutor();
    final Node node = RCLJava.createNode("spin_some_max_duration_too_short_node");
    TimerCallback timerCallback = new TimerCallback(200);
    WallTimer timer = node.createWallTimer(100, TimeUnit.MILLISECONDS, timerCallback);
    WallTimer timer2 = node.createWallTimer(100, TimeUnit.MILLISECONDS, timerCallback);
    assertNotEquals(0, timer.getHandle());
    assertNotEquals(0, timer2.getHandle());

    ComposableNode composableNode = new ComposableNode() {
      public Node getNode() {
        return node;
      }
    };

    executor.addNode(composableNode);

    try {
      // The timer is configured to wake up after 100 milliseconds, so wait 200
      // which should be enough to make it trigger.
      Thread.sleep(200);
    } catch (InterruptedException ex) {
      // We do nothing on exception here; if we didn't wait long enough, then
      // the assert below will fail
    }
    executor.spinSome(100*1000*1000);
    assertEquals(1, timerCallback.getCounter());
  }

  @Test
  public final void testSpinAll() {
    Executor executor = new SingleThreadedExecutor();
    final Node node = RCLJava.createNode("spin_all_node");
    TimerCallback timerCallback = new TimerCallback(0);
    WallTimer timer = node.createWallTimer(100, TimeUnit.MILLISECONDS, timerCallback);
    assertNotEquals(0, timer.getHandle());

    ComposableNode composableNode = new ComposableNode() {
      public Node getNode() {
        return node;
      }
    };

    executor.addNode(composableNode);

    try {
      // The timer is configured to wake up after 100 milliseconds, so wait 200
      // which should be enough to make it trigger.
      Thread.sleep(200);
    } catch (InterruptedException ex) {
      // We do nothing on exception here; if we didn't wait long enough, then
      // the assert below will fail
    }

    executor.spinAll(100*1000*1000);
    assertEquals(1, timerCallback.getCounter());
  }

  @Test
  public final void testSpinAllMaxDurationMultipleTimers() {
    Executor executor = new SingleThreadedExecutor();
    final Node node = RCLJava.createNode("spin_all_max_duration_multiple_timers_node");
    TimerCallback timerCallback = new TimerCallback(200);
    WallTimer timer = node.createWallTimer(100, TimeUnit.MILLISECONDS, timerCallback);
    WallTimer timer2 = node.createWallTimer(100, TimeUnit.MILLISECONDS, timerCallback);
    assertNotEquals(0, timer.getHandle());
    assertNotEquals(0, timer2.getHandle());

    ComposableNode composableNode = new ComposableNode() {
      public Node getNode() {
        return node;
      }
    };

    executor.addNode(composableNode);

    try {
      // The timer is configured to wake up after 100 milliseconds, so wait 200
      // which should be enough to make it trigger.
      Thread.sleep(200);
    } catch (InterruptedException ex) {
      // We do nothing on exception here; if we didn't wait long enough, then
      // the assert below will fail
    }
    executor.spinAll(400*1000*1000);
    assertEquals(2, timerCallback.getCounter());
  }

  @Test
  public final void testSpinAllMaxDurationTooShortMultipleTimers() {
    Executor executor = new SingleThreadedExecutor();
    final Node node = RCLJava.createNode("spin_all_max_duration_too_short_node");
    TimerCallback timerCallback = new TimerCallback(200);
    WallTimer timer = node.createWallTimer(100, TimeUnit.MILLISECONDS, timerCallback);
    WallTimer timer2 = node.createWallTimer(100, TimeUnit.MILLISECONDS, timerCallback);
    assertNotEquals(0, timer.getHandle());
    assertNotEquals(0, timer2.getHandle());

    ComposableNode composableNode = new ComposableNode() {
      public Node getNode() {
        return node;
      }
    };

    executor.addNode(composableNode);

    try {
      // The timer is configured to wake up after 100 milliseconds, so wait 200
      // which should be enough to make it trigger.
      Thread.sleep(200);
    } catch (InterruptedException ex) {
      // We do nothing on exception here; if we didn't wait long enough, then
      // the assert below will fail
    }
    executor.spinAll(100*1000*1000);
    assertEquals(1, timerCallback.getCounter());
  }

  // custom event consumer
  public static class EventConsumer implements Consumer<OfferedQosIncompatible> {
    public boolean done = false;

    public void accept(final OfferedQosIncompatible status) {
      assertEquals(status.totalCount, 1);
      assertEquals(status.totalCountChange, 1);
      assertEquals(status.lastPolicyKind, OfferedQosIncompatible.PolicyKind.RELIABILITY);
      this.done = true;
    }
  }

  @Test
  public final void testSpinEvent() {
    String identifier = RCLJava.getRMWIdentifier();
    if (identifier.equals("rmw_fastrtps_cpp") || identifier.equals("rmw_fastrtps_dynamic_cpp")) {
      // OfferedQosIncompatible event not supported in these implementations
      return;
    }
    final Node node = RCLJava.createNode("test_node_spin_event");
    Publisher<std_msgs.msg.String> publisher = node.<std_msgs.msg.String>createPublisher(
          std_msgs.msg.String.class, "test_topic_spin_event", new QoSProfile(
            History.KEEP_LAST, 1,
            Reliability.BEST_EFFORT,
            Durability.VOLATILE,
            false));
    // create a OfferedQoSIncompatible event handler with custom event consumer
    EventConsumer eventConsumer = new EventConsumer();
    EventHandler eventHandler = publisher.createEventHandler(
      OfferedQosIncompatible.factory, eventConsumer
    );
    // create an incompatible subscription (reliable vs best effort publisher)
    Subscription<std_msgs.msg.String> subscription = node.<std_msgs.msg.String>createSubscription(
          std_msgs.msg.String.class, "test_topic_spin_event",
          new Consumer<std_msgs.msg.String>() {
            public void accept(final std_msgs.msg.String msg) {}
          },
          new QoSProfile(
            History.KEEP_LAST, 1,
            Reliability.RELIABLE,
            Durability.VOLATILE,
            false));
    // set up executor
    ComposableNode composableNode = new ComposableNode() {
      public Node getNode() {
        return node;
      }
    };
    Executor executor = new SingleThreadedExecutor();
    executor.addNode(composableNode);
    long start = System.currentTimeMillis();
    do {
      executor.spinAll((1000 + System.currentTimeMillis() - start) * 1000 * 1000);
    } while (!eventConsumer.done && System.currentTimeMillis() < start + 1000);
    assert(eventConsumer.done);
  }
}
