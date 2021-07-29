/* Copyright 2017-2018 Esteve Fernandez <esteve@apache.org>
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

package org.ros2.rcljava.timer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.concurrent.Callback;
import org.ros2.rcljava.concurrent.RCLFuture;
import org.ros2.rcljava.node.Node;

public class TimerTest {
  public static class TimerCallback implements Callback {
    private final RCLFuture<Boolean> future;
    private int counter;
    private final int maxCount;

    TimerCallback(final RCLFuture<Boolean> future, int maxCount) {
      this.future = future;
      this.counter = 0;
      this.maxCount = maxCount;
    }

    public void call() {
      this.counter++;
      if (this.counter >= this.maxCount) {
        this.future.set(true);
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
  public final void testCreateWallTimer() throws Exception {
    int max_iterations = 4;

    Node node = RCLJava.createNode("test_timer_node");
    RCLFuture<Boolean> future = new RCLFuture<Boolean>();
    TimerCallback timerCallback = new TimerCallback(future, max_iterations);
    Timer timer = node.createWallTimer(250, TimeUnit.MILLISECONDS, timerCallback);
    assertNotEquals(0, timer.getHandle());
    assertFalse(timer.isCanceled());
    assertEquals(
        TimeUnit.NANOSECONDS.convert(250, TimeUnit.MILLISECONDS), timer.getTimerPeriodNS());

    RCLJava.spinUntilComplete(node, future);
    boolean result = future.get(3, TimeUnit.SECONDS);
    assertTrue(result);
    assertEquals(4, timerCallback.getCounter());

    timer.cancel();
    assertFalse(timer.isReady());
    assertTrue(timer.isCanceled());
  }

  @Test
  public final void testCreateTimer() throws Exception {
    int max_iterations = 4;

    Node node = RCLJava.createNode("test_timer_node");
    RCLFuture<Boolean> future = new RCLFuture<Boolean>();
    TimerCallback timerCallback = new TimerCallback(future, max_iterations);
    Timer timer = node.createTimer(250, TimeUnit.MILLISECONDS, timerCallback);
    assertNotEquals(0, timer.getHandle());
    assertFalse(timer.isCanceled());
    assertEquals(
        TimeUnit.NANOSECONDS.convert(250, TimeUnit.MILLISECONDS), timer.getTimerPeriodNS());

    RCLJava.spinUntilComplete(node, future);
    boolean result = future.get(3, TimeUnit.SECONDS);
    assertTrue(result);
    assertEquals(4, timerCallback.getCounter());

    timer.cancel();
    assertFalse(timer.isReady());
    assertTrue(timer.isCanceled());
  }
}
