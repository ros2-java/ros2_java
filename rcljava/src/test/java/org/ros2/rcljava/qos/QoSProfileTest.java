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

package org.ros2.rcljava.qos;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.BeforeClass;
import org.junit.Test;

import org.ros2.rcljava.qos.QoSProfile;
import org.ros2.rcljava.qos.policies.Durability;
import org.ros2.rcljava.qos.policies.History;
import org.ros2.rcljava.qos.policies.Reliability;

public class QoSProfileTest {
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
  public final void testQoSProfileBasic() {
    QoSProfile qosProfile = new QoSProfile(
      History.KEEP_LAST, 1, Reliability.RELIABLE, Durability.VOLATILE, false);
    assertEquals(qosProfile.getHistory(), History.KEEP_LAST);
    assertEquals(qosProfile.getDepth(), 1);
    assertEquals(qosProfile.getReliability(), Reliability.RELIABLE);
    assertEquals(qosProfile.getDurability(), Durability.VOLATILE);
    assertEquals(qosProfile.getAvoidROSNamespaceConventions(), false);
  }

  @Test
  public final void testQoSProfileChaining() {
    QoSProfile qosProfile = QoSProfile.defaultProfile()
      .setHistory(History.KEEP_ALL)
      .setDepth(11)
      .setReliability(Reliability.BEST_EFFORT)
      .setDurability(Durability.TRANSIENT_LOCAL)
      .setAvoidROSNamespaceConventions(true);
    assertEquals(qosProfile.getHistory(), History.KEEP_ALL);
    assertEquals(qosProfile.getDepth(), 11);
    assertEquals(qosProfile.getReliability(), Reliability.BEST_EFFORT);
    assertEquals(qosProfile.getDurability(), Durability.TRANSIENT_LOCAL);
    assertEquals(qosProfile.getAvoidROSNamespaceConventions(), true);
  }
}
