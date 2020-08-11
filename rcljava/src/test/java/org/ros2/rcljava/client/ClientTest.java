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

package org.ros2.rcljava.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.ref.WeakReference;
import java.time.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.concurrent.RCLFuture;
import org.ros2.rcljava.consumers.TriConsumer;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.service.RMWRequestId;
import org.ros2.rcljava.service.Service;

public class ClientTest {
  private Node node;

  @BeforeClass
  public static void setupOnce() throws Exception {
    RCLJava.rclJavaInit();
    try
    {
      // Configure log4j. Doing this dynamically so that Android does not complain about missing
      // the log4j JARs, SLF4J uses Android's native logging mechanism instead.
      Class c = Class.forName("org.apache.log4j.BasicConfigurator");
      Method m = c.getDeclaredMethod("configure", (Class<?>[]) null);
      Object o = m.invoke(null, (Object<?>[]) null);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public class TestClientConsumer implements TriConsumer<RMWRequestId,
      rcljava.srv.AddTwoInts_Request, rcljava.srv.AddTwoInts_Response> {
    private final RCLFuture<rcljava.srv.AddTwoInts_Response> future;

    TestClientConsumer(final RCLFuture<rcljava.srv.AddTwoInts_Response> future) {
      this.future = future;
    }

    public final void accept(final RMWRequestId header,
        final rcljava.srv.AddTwoInts_Request request,
        final rcljava.srv.AddTwoInts_Response response) {
      if (!this.future.isDone()) {
        response.setSum(request.getA() + request.getB());
        this.future.set(response);
      }
    }
  }

  @Before
  public void setUp() {
    node = RCLJava.createNode("test_node");
  }

  @After
  public void tearDown() {
    node.dispose();
  }

  @AfterClass
  public static void tearDownOnce() {
    RCLJava.shutdown();
  }

  @Test
  public final void testAdd() throws Exception {
    RCLFuture<rcljava.srv.AddTwoInts_Response> consumerFuture =
        new RCLFuture<rcljava.srv.AddTwoInts_Response>(new WeakReference<Node>(node));

    TestClientConsumer clientConsumer = new TestClientConsumer(consumerFuture);

    Service<rcljava.srv.AddTwoInts> service = node.<rcljava.srv.AddTwoInts>createService(
        rcljava.srv.AddTwoInts.class, "add_two_ints", clientConsumer);

    rcljava.srv.AddTwoInts_Request request = new rcljava.srv.AddTwoInts_Request();
    request.setA(2);
    request.setB(3);

    Client<rcljava.srv.AddTwoInts> client =
        node.<rcljava.srv.AddTwoInts>createClient(rcljava.srv.AddTwoInts.class, "add_two_ints");

    assertTrue(client.waitForService(Duration.ofSeconds(10)));

    Future<rcljava.srv.AddTwoInts_Response> responseFuture = client.asyncSendRequest(request);

    rcljava.srv.AddTwoInts_Response response = responseFuture.get(10, TimeUnit.SECONDS);

    // Check that the message was received by the service
    assertTrue(consumerFuture.isDone());

    // Check the contents of the response
    assertEquals(5, response.getSum());

    assertEquals(1, node.getClients().size());
    assertEquals(1, node.getServices().size());

    // We expect that calling dispose should result in a zero handle
    // and the reference is dropped from the Node
    client.dispose();
    assertEquals(0, client.getHandle());
    assertEquals(0, node.getClients().size());
    service.dispose();
    assertEquals(0, service.getHandle());
    assertEquals(0, node.getServices().size());
  }
}
