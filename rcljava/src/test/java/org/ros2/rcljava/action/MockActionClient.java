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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ros2.rcljava.client.Client;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.subscription.Subscription;

public class MockActionClient {
  class FeedbackCallback implements Consumer<test_msgs.action.Fibonacci_Feedback> {
    public List<test_msgs.action.Fibonacci_Feedback> feedbackReceived;

    public FeedbackCallback() {
      feedbackReceived = Collections.synchronizedList(
        new ArrayList<test_msgs.action.Fibonacci_Feedback>());
    }

    public void accept(final test_msgs.action.Fibonacci_Feedback feedback) {
      this.feedbackReceived.add(feedback);
    }
  }

  class StatusCallback implements Consumer<action_msgs.msg.GoalStatusArray> {
    private action_msgs.msg.GoalStatusArray statusArray;

    public synchronized action_msgs.msg.GoalStatusArray getStatusArrayMessage() {
      return this.statusArray;
    }

    public synchronized void accept(final action_msgs.msg.GoalStatusArray statusArray) {
      this.statusArray = statusArray;
    }
  }

  public Client<test_msgs.action.Fibonacci_SendGoal> sendGoalClient;
  public Client<test_msgs.action.Fibonacci_GetResult> getResultClient;
  public Client<action_msgs.srv.CancelGoal> cancelGoalClient;
  public FeedbackCallback feedbackCallback;
  public StatusCallback statusCallback;
  public Subscription<test_msgs.action.Fibonacci_Feedback> feedbackSubscription;
  public Subscription<action_msgs.msg.GoalStatusArray> statusSubscription;

  public MockActionClient(Node node, String actionName) throws IllegalAccessException, NoSuchFieldException {
    // Create mock service clients that make up an action client
    sendGoalClient = node.<test_msgs.action.Fibonacci_SendGoal>createClient(
      test_msgs.action.Fibonacci_SendGoal.class, actionName + "/_action/send_goal");
    getResultClient = node.<test_msgs.action.Fibonacci_GetResult>createClient(
      test_msgs.action.Fibonacci_GetResult.class, actionName + "/_action/get_result");
    cancelGoalClient = node.<action_msgs.srv.CancelGoal>createClient(
      action_msgs.srv.CancelGoal.class, actionName + "/_action/cancel_goal");
    // Create mock subscriptions that make up an action client
    feedbackCallback = new FeedbackCallback();
    statusCallback = new StatusCallback();
    feedbackSubscription = node.<test_msgs.action.Fibonacci_Feedback>createSubscription(
      test_msgs.action.Fibonacci_Feedback.class,
      actionName + "/_action/feedback",
      feedbackCallback);
    statusSubscription = node.<action_msgs.msg.GoalStatusArray>createSubscription(
      action_msgs.msg.GoalStatusArray.class,
      actionName + "/_action/status",
      statusCallback);
  }

  public void dispose() {
    this.sendGoalClient.dispose();
    this.getResultClient.dispose();
    this.cancelGoalClient.dispose();
    this.feedbackSubscription.dispose();
    this.statusSubscription.dispose();
  }

  public boolean waitForActionServer(Duration timeout) {
    if (!this.sendGoalClient.waitForService(timeout)) {
      return false;
    }
    if (!this.getResultClient.waitForService(timeout)) {
      return false;
    }
    if (!this.cancelGoalClient.waitForService(timeout)) {
      return false;
    }
    //TODO(jacobperron): wait for feedback and status subscriptions to match publishers, when API is available
    return true;
  }
}
