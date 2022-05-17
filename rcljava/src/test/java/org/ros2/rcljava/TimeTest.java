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

package org.ros2.rcljava;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.BeforeClass;
import org.junit.Test;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.time.ClockType;

public class TimeTest {
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
  public final void testTimeNoArgConstructor() {
    Time time = new Time();
    assertEquals(0, time.nanoseconds());
    assertEquals(ClockType.SYSTEM_TIME, time.clockType());
  }

  @Test
  public final void testTimeFromMsgConstructor() {
    builtin_interfaces.msg.Time timeMsg = new builtin_interfaces.msg.Time();
    timeMsg.setSec(42);
    timeMsg.setNanosec(100);
    Time time = new Time(timeMsg, ClockType.SYSTEM_TIME);
    assertEquals(42000000100L, time.nanoseconds());
    assertEquals(ClockType.SYSTEM_TIME, time.clockType());
  }

  @Test
  public final void testTimeNanos() {
    Time time = new Time(45, ClockType.SYSTEM_TIME);
    assertEquals(45, time.nanoseconds());
    assertEquals(ClockType.SYSTEM_TIME, time.clockType());
  }

  @Test
  public final void testTimeAllArgs() {
    Time time = new Time(0, 45, ClockType.SYSTEM_TIME);
    assertEquals(45, time.nanoseconds());
    assertEquals(ClockType.SYSTEM_TIME, time.clockType());
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testTimeBadSecs() {
    Time time = new Time(-1, 0, ClockType.SYSTEM_TIME);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testTimeBadNanos() {
    Time time = new Time(0, -45, ClockType.SYSTEM_TIME);
  }

  @Test
  public final void testTimeToMsg() {
    {
      Time time = new Time();
      builtin_interfaces.msg.Time timeMsgOut = time.toMsg();
      assertEquals(0, timeMsgOut.getSec());
      assertEquals(0, timeMsgOut.getNanosec());
    }
    {
      builtin_interfaces.msg.Time timeMsg = new builtin_interfaces.msg.Time();
      timeMsg.setSec(42);
      timeMsg.setNanosec(100);
      Time time = new Time(timeMsg, ClockType.SYSTEM_TIME);
      builtin_interfaces.msg.Time timeMsgOut = time.toMsg();
      assertEquals(42, timeMsgOut.getSec());
      assertEquals(100, timeMsgOut.getNanosec());
    }
    {
      Time time = new Time(0, 45, ClockType.SYSTEM_TIME);
      builtin_interfaces.msg.Time timeMsgOut = time.toMsg();
      assertEquals(0, timeMsgOut.getSec());
      assertEquals(45, timeMsgOut.getNanosec());
    }
  }
}
