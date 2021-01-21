/* Copyright 2020 ros2-java contributors
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

package org.ros2.rcljava.action;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.executors.SingleThreadedExecutor;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.node.ComposableNode;
import org.ros2.rcljava.node.Node;

public class ActionServerTest {
  class MockGoalCallback implements GoalCallback<test_msgs.action.Fibonacci.SendGoalRequest> {
    public test_msgs.action.Fibonacci_Goal goal;
    public GoalResponse handleGoal(test_msgs.action.Fibonacci.SendGoalRequest goal) {
      this.goal = goal.getGoal();
      return GoalResponse.ACCEPT;
    }
  }

  class MockCancelCallback implements CancelCallback<test_msgs.action.Fibonacci> {
    public ActionServerGoalHandle<test_msgs.action.Fibonacci> goalHandle;
    public CancelResponse handleCancel(ActionServerGoalHandle<test_msgs.action.Fibonacci> goalHandle) {
      this.goalHandle = goalHandle;
      return CancelResponse.ACCEPT;
    }
  }

  class MockAcceptedCallback implements Consumer<ActionServerGoalHandle<test_msgs.action.Fibonacci>> {
    public ActionServerGoalHandle<test_msgs.action.Fibonacci> goalHandle;
    public void accept(final ActionServerGoalHandle<test_msgs.action.Fibonacci> goalHandle) {
      this.goalHandle = goalHandle;
    }
  }

  private SingleThreadedExecutor executor;
  private Node node;
  private ComposableNode composableNode;
  private ActionServer<test_msgs.action.Fibonacci> actionServer;
  private MockGoalCallback goalCallback;
  private MockCancelCallback cancelCallback;
  private MockAcceptedCallback acceptedCallback;
  private MockActionClient mockActionClient;

  @BeforeClass
  public static void setupOnce() {
    RCLJava.rclJavaInit();
    org.apache.log4j.BasicConfigurator.configure();
  }

  @AfterClass
  public static void tearDownOnce() {
    RCLJava.shutdown();
  }

  @Before
  public void setUp() throws Exception {
    // Create a node
    node = RCLJava.createNode("test_action_server_node");

    assertNotEquals(null, node);

    // Executor requires a ComposableNode type
    composableNode = new ComposableNode() {
      public Node getNode() {
        return node;
      }
    };
    executor = new SingleThreadedExecutor();
    executor.addNode(composableNode);

    // Create action server callbacks
    goalCallback = new MockGoalCallback();
    cancelCallback = new MockCancelCallback();
    acceptedCallback = new MockAcceptedCallback();

    // Create an action server
    actionServer = node.<test_msgs.action.Fibonacci>createActionServer(
      test_msgs.action.Fibonacci.class, "test_action",
      goalCallback, cancelCallback, acceptedCallback);

    // Create mock client
    mockActionClient = new MockActionClient(node, "test_action");

    // Wait for mock client to discover action sever
    // TODO(jacobperron): also wait for action server to discover the client
    assertEquals(true, mockActionClient.waitForActionServer(Duration.ofSeconds(5)));
  }

  @After
  public void tearDown() {
    // We expect that calling dispose should result in a zero handle
    // and the reference is dropped from the Node
    actionServer.dispose();
    assertEquals(0, actionServer.getHandle());
    assertEquals(0, this.node.getActionServers().size());

    mockActionClient.dispose();

    executor.removeNode(composableNode);

    node.dispose();
  }

  public test_msgs.action.Fibonacci_SendGoal_Response sendGoal(int order) throws Exception {
    test_msgs.action.Fibonacci_SendGoal_Request request =
      new test_msgs.action.Fibonacci_SendGoal_Request();
    test_msgs.action.Fibonacci_Goal goal = new test_msgs.action.Fibonacci_Goal();
    goal.setOrder(order);
    request.setGoal(goal);

    Future<test_msgs.action.Fibonacci_SendGoal_Response> future =
      this.mockActionClient.sendGoalClient.asyncSendRequest(request);

    test_msgs.action.Fibonacci_SendGoal_Response response = null;
    long startTime = System.nanoTime();
    while (RCLJava.ok() && !future.isDone()) {
      this.executor.spinOnce(1);
      response = future.get(100, TimeUnit.MILLISECONDS);

      // Check for timeout
      long duration = System.nanoTime() - startTime;
      if (TimeUnit.NANOSECONDS.toSeconds(duration) >= 5) {
        break;
      }
    }
    return response;
  }

  @Test
  public final void testCreateAndDispose() {
    assertNotEquals(0, this.actionServer.getHandle());
    assertEquals(1, this.node.getActionServers().size());

    // Assert no callbacks triggered
    assertEquals(null, this.goalCallback.goal);
    assertEquals(null, this.cancelCallback.goalHandle);
    assertEquals(null, this.acceptedCallback.goalHandle);
  }

  @Test
  public final void testAcceptGoal() throws Exception {
    assertNotEquals(0, this.actionServer.getHandle());
    assertEquals(1, this.node.getActionServers().size());

    // Send a goal
    test_msgs.action.Fibonacci_SendGoal_Response response = sendGoal(42);

    assertNotEquals(null, response);

    // Assert goal callback and accepted callback triggered
    assertNotEquals(null, this.goalCallback.goal);
    assertNotEquals(null, this.acceptedCallback.goalHandle);

    assertEquals(42, this.goalCallback.goal.getOrder());
    test_msgs.action.Fibonacci_Goal acceptedGoal = (test_msgs.action.Fibonacci_Goal)this.acceptedCallback.goalHandle.getGoal();
    assertEquals(42, acceptedGoal.getOrder());

    // Assert cancel callback not triggered
    assertEquals(null, this.cancelCallback.goalHandle);
  }

  @Test
  public final void testCancelGoal() throws Exception {
    assertNotEquals(0, this.actionServer.getHandle());
    assertEquals(1, this.node.getActionServers().size());

    // Send a goal
    test_msgs.action.Fibonacci_SendGoal_Response response = sendGoal(42);

    assertNotEquals(null, response);

    // Cancel the goal
    action_msgs.srv.CancelGoal_Request cancelRequest = new action_msgs.srv.CancelGoal_Request();
    // A zero GoalInfo means cancel all goals
    action_msgs.msg.GoalInfo goalInfo = new action_msgs.msg.GoalInfo();
    unique_identifier_msgs.msg.UUID zeroGoalId = new unique_identifier_msgs.msg.UUID();
    // TODO(jacobperron): code generator should zero initialize to 16 elements
    zeroGoalId.setUuid(new byte[16]); 
    goalInfo.setGoalId(zeroGoalId);
    cancelRequest.setGoalInfo(goalInfo);
    Future<action_msgs.srv.CancelGoal_Response> cancelResponseFuture =
      this.mockActionClient.cancelGoalClient.asyncSendRequest(cancelRequest);

    // Wait for cancel response
    long startTime = System.nanoTime();
    while (RCLJava.ok() && !cancelResponseFuture.isDone()) {
      this.executor.spinOnce(100000000);  // timeout of 100 milliseconds

      // Check for timeout
      long duration = System.nanoTime() - startTime;
      if (TimeUnit.NANOSECONDS.toSeconds(duration) >= 5) {
        break;
      }
    }

    assertEquals(true, cancelResponseFuture.isDone());
    action_msgs.srv.CancelGoal_Response cancelResponse = cancelResponseFuture.get();
    action_msgs.msg.GoalInfo[] goalsCanceling = cancelResponse.getGoalsCanceling();
    assertEquals(1, goalsCanceling.length);

    // Assert cancel callback was triggered
    assertNotEquals(null, this.cancelCallback.goalHandle);
    test_msgs.action.Fibonacci_Goal cancelingGoal = (test_msgs.action.Fibonacci_Goal)this.cancelCallback.goalHandle.getGoal();
    assertEquals(42, cancelingGoal.getOrder());
  }
}
