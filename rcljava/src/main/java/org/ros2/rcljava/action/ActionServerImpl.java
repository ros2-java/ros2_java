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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.interfaces.MessageDefinition;
import org.ros2.rcljava.interfaces.ActionDefinition;
import org.ros2.rcljava.interfaces.GoalDefinition;
import org.ros2.rcljava.interfaces.GoalRequestDefinition;
import org.ros2.rcljava.interfaces.GoalResponseDefinition;
import org.ros2.rcljava.interfaces.FeedbackDefinition;
import org.ros2.rcljava.interfaces.FeedbackMessageDefinition;
import org.ros2.rcljava.interfaces.ResultDefinition;
import org.ros2.rcljava.interfaces.ResultRequestDefinition;
import org.ros2.rcljava.interfaces.ResultResponseDefinition;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.service.RMWRequestId;
import org.ros2.rcljava.time.Clock;
import org.ros2.rcljava.Time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionServerImpl<T extends ActionDefinition> implements ActionServer<T> {
  private static final Logger logger = LoggerFactory.getLogger(ActionServerImpl.class);

  static {
    try {
      JNIUtils.loadImplementation(ActionServerImpl.class);
      JNIUtils.loadImplementation(ActionServerImpl.GoalHandleImpl.class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
      JNIUtils.loadImplementation(ActionServerImpl.class);
  }

  class GoalHandleImpl implements ActionServerGoalHandle<T> {
    private long handle;
    private action_msgs.msg.GoalInfo goalInfo;
    private GoalDefinition<T> goal;

    private native long nativeAcceptNewGoal(
      long actionServerHandle,
      long goalInfoFromJavaConverterHandle,
      long goalInfoDestructorHandle,
      MessageDefinition goalInfo);
    private native int nativeGetStatus(long goalHandle);
    private native void nativeUpdateGoalState(long goalHandle, long event);
    private native void nativeDispose(long handle);

    public GoalHandleImpl(
      action_msgs.msg.GoalInfo goalInfo, GoalDefinition<T> goal)
    {
      this.goalInfo = goalInfo;
      this.goal = goal;
      long goalInfoFromJavaConverterHandle = goalInfo.getFromJavaConverterInstance();
      long goalInfoDestructorHandle = goalInfo.getDestructorInstance();
      this.handle = nativeAcceptNewGoal(
        ActionServerImpl.this.getHandle(),
        goalInfoFromJavaConverterHandle,
        goalInfoDestructorHandle,
        goalInfo);
    }

    /**
     * {@inheritDoc}
     */
    public Class<? extends ResultDefinition> getResultType() {
      return ActionServerImpl.this.actionTypeInstance.getResultType();
    }

    /**
     * {@inheritDoc}
     */
    public Class<? extends FeedbackDefinition> getFeedbackType() {
      return ActionServerImpl.this.actionTypeInstance.getFeedbackType();
    }

    /**
     * {@inheritDoc}
     */
    public action_msgs.msg.GoalInfo getGoalInfo() {
      return this.goalInfo;
    }

    /**
     * {@inheritDoc}
     */
    public GoalDefinition<T> getGoal() {
      return this.goal;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized GoalStatus getGoalStatus() {
      int status = nativeGetStatus(this.handle);
      return GoalStatus.fromMessageValue((byte)status);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized boolean isCanceling() {
      return this.getGoalStatus() == GoalStatus.CANCELING;
    }

    /**
     * Transition the goal to the EXECUTING state.
     */
    public synchronized void execute() {
      // It's possible that there has been a request to cancel the goal prior to executing.
      // In this case we want to avoid the illegal state transition to EXECUTING
      // but still call the users execute callback to let them handle canceling the goal.
      if (!this.isCanceling()) {
        nativeUpdateGoalState(this.handle, action_msgs.msg.GoalStatus.STATUS_EXECUTING);
      }
    }

    /**
     * Transition the goal to the CANCELING state.
     */
    public synchronized void cancelGoal() {
      nativeUpdateGoalState(this.handle, action_msgs.msg.GoalStatus.STATUS_CANCELING);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void succeed(ResultDefinition<T> result) {
      this.toTerminalState(action_msgs.msg.GoalStatus.STATUS_SUCCEEDED, result);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void canceled(ResultDefinition<T> result) {
      this.toTerminalState(action_msgs.msg.GoalStatus.STATUS_CANCELED, result);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void abort(ResultDefinition<T> result) {
      this.toTerminalState(action_msgs.msg.GoalStatus.STATUS_ABORTED, result);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void publishFeedback(FeedbackDefinition<T> feedback) {
      Class<? extends FeedbackMessageDefinition> feedbackMessageType =
        ActionServerImpl.this.actionTypeInstance.getFeedbackMessageType();
      FeedbackMessageDefinition<T> feedbackMessage = null;
      try {
        feedbackMessage = feedbackMessageType.newInstance();
      } catch (Exception ex) {
        throw new IllegalArgumentException("Failed to instantiate feedback message", ex);
      }
      feedbackMessage.setFeedback(feedback);
      feedbackMessage.setGoalUuid(this.goalInfo.getGoalId().getUuidAsList());
      ActionServerImpl.this.publishFeedbackMessage(feedbackMessage);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized final void dispose() {
      nativeDispose(this.handle);
      this.handle = 0;
    }

    /**
     * {@inheritDoc}
     */
    public final long getHandle() {
      return this.handle;
    }

    private synchronized final void toTerminalState(byte status, ResultDefinition<T> result) {
      nativeUpdateGoalState(this.handle, status);
      ResultResponseDefinition resultResponse = ActionServerImpl.this.createResultResponse();
      resultResponse.setGoalStatus(status);
      resultResponse.setResult(result);
      ActionServerImpl.this.sendResult(goalInfo.getGoalId().getUuidAsList(), resultResponse);
      ActionServerImpl.this.publishStatus();
      ActionServerImpl.this.notifyGoalDone();
      ActionServerImpl.this.goalHandles.remove(this.goalInfo.getGoalId().getUuidAsList());
    }
  }  // class GoalHandleImpl

  private final WeakReference<Node> nodeReference;
  private final Clock clock;
  private final T actionTypeInstance;
  private final String actionName;
  private long handle;
  private final GoalCallback goalCallback;
  private final CancelCallback<T> cancelCallback;
  private final Consumer<ActionServerGoalHandle<T>> acceptedCallback;

  private boolean[] readyEntities;

  private Map<List<Byte>, GoalHandleImpl> goalHandles;
  private Map<List<Byte>, List<RMWRequestId>> goalRequests;
  private Map<List<Byte>, ResultResponseDefinition<T>> goalResults;

  private boolean isGoalRequestReady() {
    return this.readyEntities[0];
  }

  private boolean isCancelRequestReady() {
    return this.readyEntities[1];
  }

  private boolean isResultRequestReady() {
    return this.readyEntities[2];
  }

  private boolean isGoalExpiredReady() {
    return this.readyEntities[3];
  }

  private native long nativeCreateActionServer(
    long nodeHandle, long clockHandle, Class<T> cls, String actionName);

  /**
   * Create an action server.
   *
   * @param nodeReference A reference to the node to use to create this action server.
   * @param actionType The type of the action.
   * @param actionName The name of the action.
   * @param goalCallback Callback triggered when a new goal request is received.
   * @param cancelCallback Callback triggered when a new cancel request is received.
   * @param acceptedCallback Callback triggered when a new goal is accepted.
   */
  public ActionServerImpl(
      final WeakReference<Node> nodeReference,
      final Class<T> actionType,
      final String actionName,
      final GoalCallback<? extends GoalRequestDefinition<T>> goalCallback,
      final CancelCallback<T> cancelCallback,
      final Consumer<ActionServerGoalHandle<T>> acceptedCallback) throws IllegalArgumentException {
    this.nodeReference = nodeReference;
    try {
      this.actionTypeInstance = actionType.newInstance();
    } catch (Exception ex) {
      throw new IllegalArgumentException("Failed to instantiate provided action type", ex);
    }
    this.actionName = actionName;
    this.goalCallback = goalCallback;
    this.cancelCallback = cancelCallback;
    this.acceptedCallback = acceptedCallback;

    this.goalHandles = new HashMap<List<Byte>, GoalHandleImpl>();
    this.goalRequests = new HashMap<List<Byte>, List<RMWRequestId>>();
    this.goalResults = new HashMap<List<Byte>, ResultResponseDefinition<T>>();

    Node node = nodeReference.get();
    if (node == null) {
      throw new IllegalArgumentException("Node reference is null");
    }

    this.clock = node.getClock();

    this.handle = nativeCreateActionServer(
      node.getHandle(), node.getClock().getHandle(), actionType, actionName);
    // TODO(jacobperron): Introduce 'Waitable' interface for entities like timers, services, etc
    // node.addWaitable(this);
  }

  private static native int[] nativeGetNumberOfEntities(long handle);

  /**
   * {@inheritDoc}
   */
  public int getNumberOfSubscriptions() {
    return nativeGetNumberOfEntities(this.handle)[0];
  }

  /**
   * {@inheritDoc}
   */
  public int getNumberOfTimers() {
    return nativeGetNumberOfEntities(this.handle)[2];
  }

  /**
   * {@inheritDoc}
   */
  public int getNumberOfClients() {
    return nativeGetNumberOfEntities(this.handle)[3];
  }

  /**
   * {@inheritDoc}
   */
  public int getNumberOfServices() {
    return nativeGetNumberOfEntities(this.handle)[4];
  }

  private static native boolean[] nativeGetReadyEntities(
    long actionServerHandle, long waitSetHandle);

  /**
   * {@inheritDoc}
   */
  public boolean isReady(long waitSetHandle) {
    this.readyEntities = nativeGetReadyEntities(this.handle, waitSetHandle);
    for (boolean isReady : this.readyEntities) {
      if (isReady) {
        return true;
      }
    }
    return false;
  }

  private ActionServerGoalHandle<T> executeGoalRequest(
    RMWRequestId rmwRequestId,
    GoalRequestDefinition<T> requestMessage,
    GoalResponseDefinition<T> responseMessage)
  {
    builtin_interfaces.msg.Time timeRequestHandled = this.clock.now().toMsg();
    responseMessage.setStamp(timeRequestHandled.getSec(), timeRequestHandled.getNanosec());

    // Create and populate a GoalInfo message
    List<Byte> goalUuid = requestMessage.getGoalUuid();

    action_msgs.msg.GoalInfo goalInfo = this.createGoalInfo(goalUuid);
    // Check that the goal ID isn't already being used
    boolean goalExists = this.goalExists(goalInfo);
    if (goalExists) {
      logger.warn("Received goal request for goal already being tracked by action server. Goal ID: " + goalUuid);
      responseMessage.accept(false);
      return null;
    }

    // Call user callback
    GoalCallback.GoalResponse response = this.goalCallback.handleGoal(requestMessage);

    boolean accepted = GoalCallback.GoalResponse.ACCEPT_AND_DEFER == response
      || GoalCallback.GoalResponse.ACCEPT_AND_EXECUTE == response;
    responseMessage.accept(accepted);

    if (!accepted) {
      return null;
    }

    // Create a goal handle and add it to the list of goals
    GoalHandleImpl goalHandle = this.new GoalHandleImpl(
      goalInfo, requestMessage.getGoal());
    this.goalHandles.put(requestMessage.getGoalUuid(), goalHandle);
    if (GoalCallback.GoalResponse.ACCEPT_AND_EXECUTE == response) {
      goalHandle.execute();
      this.acceptedCallback.accept(goalHandle);
    }
    return goalHandle;
  }

  private action_msgs.srv.CancelGoal_Response executeCancelRequest(
    action_msgs.srv.CancelGoal_Response inputMessage)
  {
    action_msgs.srv.CancelGoal_Response outputMessage = new action_msgs.srv.CancelGoal_Response();
    outputMessage.setReturnCode(inputMessage.getReturnCode());
    List<action_msgs.msg.GoalInfo> goalsToCancel = new ArrayList<action_msgs.msg.GoalInfo>();

    // Process user callback for each goal in cancel request
    for (action_msgs.msg.GoalInfo goalInfo : inputMessage.getGoalsCanceling()) {
      List<Byte> goalUuid = goalInfo.getGoalId().getUuidAsList();
      // It's possible a goal may not be tracked by the user
      if (!this.goalHandles.containsKey(goalUuid)) {
        logger.warn("Ignoring cancel request for untracked goal handle with ID '" + goalUuid + "'");
        continue;
      }
      GoalHandleImpl goalHandle = this.goalHandles.get(goalUuid);
      CancelCallback.CancelResponse cancelResponse = this.cancelCallback.handleCancel(goalHandle);

      if (CancelCallback.CancelResponse.ACCEPT == cancelResponse) {
        // Update goal state to CANCELING
        goalHandle.cancelGoal();

        // Add to returned response
        goalsToCancel.add(goalInfo);
      }
    }

    outputMessage.setGoalsCanceling(goalsToCancel);
    return outputMessage;
  }

  private void sendResultResponse(
    RMWRequestId rmwRequestId,
    ResultResponseDefinition<T> resultResponse)
  {
    long resultFromJavaConverterHandle = resultResponse.getFromJavaConverterInstance();
    long resultToJavaConverterHandle = resultResponse.getToJavaConverterInstance();
    long resultDestructorHandle = resultResponse.getDestructorInstance();

    nativeSendResultResponse(
      this.handle,
      rmwRequestId,
      resultFromJavaConverterHandle,
      resultToJavaConverterHandle,
      resultDestructorHandle,
      resultResponse);
  }

  private static native RMWRequestId nativeTakeGoalRequest(
    long actionServerHandle,
    long requestFromJavaConverterHandle,
    long requestToJavaConverterHandle,
    long requestDestructorHandle,
    MessageDefinition requestMessage);

  private static native RMWRequestId nativeTakeCancelRequest(
    long actionServerHandle,
    long requestFromJavaConverterHandle,
    long requestToJavaConverterHandle,
    long requestDestructorHandle,
    MessageDefinition requestMessage);

  private static native RMWRequestId nativeTakeResultRequest(
    long actionServerHandle,
    long requestFromJavaConverterHandle,
    long requestToJavaConverterHandle,
    long requestDestructorHandle,
    MessageDefinition requestMessage);

  private static native void nativeSendGoalResponse(
    long actionServerHandle,
    RMWRequestId header,
    long responseFromJavaConverterHandle,
    long responseToJavaConverterHandle,
    long responseDestructorHandle,
    MessageDefinition responseMessage);

  private static native void nativeSendCancelResponse(
    long actionServerHandle,
    RMWRequestId header,
    long responseFromJavaConverterHandle,
    long responseToJavaConverterHandle,
    long responseDestructorHandle,
    MessageDefinition responseMessage);

  private static native void nativeSendResultResponse(
    long actionServerHandle,
    RMWRequestId header,
    long responseFromJavaConverterHandle,
    long responseToJavaConverterHandle,
    long responseDestructorHandle,
    MessageDefinition responseMessage);

  private static native void nativeProcessCancelRequest(
    long actionServerHandle,
    long requestFromJavaConverterHandle,
    long requestDestructorHandle,
    long responseToJavaConverterHandle,
    MessageDefinition requestMessage,
    MessageDefinition responseMessage);

  private static native boolean nativeCheckGoalExists(
    long handle,
    MessageDefinition goalInfo,
    long goalInfoFromJavaConverterHandle,
    long goalInfoDestructorHandle);

  private boolean goalExists(action_msgs.msg.GoalInfo goalInfo) {
    long goalInfoFromJavaConverterHandle = goalInfo.getFromJavaConverterInstance();
    long goalInfoDestructorHandle = goalInfo.getDestructorInstance();

    return nativeCheckGoalExists(
      this.handle, goalInfo, goalInfoFromJavaConverterHandle, goalInfoDestructorHandle);
  }

  private static native void nativePublishStatus(long handle);

  private void publishStatus() {
    this.nativePublishStatus(this.handle);
  }

  private static native void nativePublishFeedbackMessage(
    long handle,
    FeedbackMessageDefinition feedbackMessage,
    long feedbackMessageFromJavaConverterHandle,
    long feedbackMessageDestructorHandle);

  private void publishFeedbackMessage(FeedbackMessageDefinition<T> feedbackMessage) {
    this.nativePublishFeedbackMessage(
      this.handle,
      feedbackMessage,
      feedbackMessage.getFromJavaConverterInstance(),
      feedbackMessage.getDestructorInstance());
  }

  private static native void nativeNotifyGoalDone(long handle);

  private void notifyGoalDone() {
    this.nativeNotifyGoalDone(this.handle);
  }

  private static native void nativeExpireGoals(
    long handle, action_msgs.msg.GoalInfo goalInfo,
    long goalInfoToJavaConverterHandle, Consumer<action_msgs.msg.GoalInfo> onExpiredGoal);

  private void expireGoals() {
    action_msgs.msg.GoalInfo goalInfo = new action_msgs.msg.GoalInfo();
    long goalInfoToJavaConverterHandle = goalInfo.getFromJavaConverterInstance();
    nativeExpireGoals(
      this.handle, goalInfo, goalInfoToJavaConverterHandle,
      new Consumer<action_msgs.msg.GoalInfo>() {
        public void accept(action_msgs.msg.GoalInfo goalInfo) {
          List<Byte> goalUuid = goalInfo.getGoalId().getUuidAsList();
          ActionServerImpl.this.goalResults.remove(goalUuid);
          ActionServerImpl.this.goalRequests.remove(goalUuid);
          ActionServerImpl.this.goalHandles.remove(goalUuid);
        }
      });
  }

  private action_msgs.msg.GoalInfo createGoalInfo(List<Byte> goalUuid) {
    action_msgs.msg.GoalInfo goalInfo = new action_msgs.msg.GoalInfo();
    unique_identifier_msgs.msg.UUID uuidMessage= new unique_identifier_msgs.msg.UUID();
    uuidMessage.setUuid(goalUuid);
    goalInfo.setGoalId(uuidMessage);
    return goalInfo;
  }

  private ResultResponseDefinition<T> createResultResponse() {
    ResultResponseDefinition<T> resultResponse;
    try {
      resultResponse = this.actionTypeInstance.getGetResultResponseType().newInstance();
    } catch (Exception ex) {
      throw new IllegalArgumentException("Failed to instantiate provided action type", ex);
    }
    return resultResponse;
  }

  // This will store the result, so it can be sent to future result requests, and 
  // will also send a result response to all requests that were already made.
  private void sendResult(List<Byte> goalUuid, ResultResponseDefinition<T> resultResponse) {
    boolean goalExists = this.goalExists(this.createGoalInfo(goalUuid));
    if (!goalExists) {
      throw new IllegalStateException("Asked to publish result for goal that does not exist");
    }
    this.goalResults.put(goalUuid, resultResponse);

    // if there are clients who already asked for the result, send it to them
    List<RMWRequestId> requests = this.goalRequests.get(goalUuid);
    if (requests != null) {
      for (RMWRequestId request : requests) {
        this.sendResultResponse(request, resultResponse);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public void execute() {
    if (this.isGoalRequestReady()) {
      Class<? extends GoalRequestDefinition> requestType = this.actionTypeInstance.getSendGoalRequestType();
      Class<? extends GoalResponseDefinition> responseType = this.actionTypeInstance.getSendGoalResponseType();

      GoalRequestDefinition<T> requestMessage = null;
      GoalResponseDefinition<T> responseMessage = null;

      try {
        requestMessage = requestType.newInstance();
        responseMessage = responseType.newInstance();
      } catch (InstantiationException ie) {
        ie.printStackTrace();
      } catch (IllegalAccessException iae) {
        iae.printStackTrace();
      }

      if (requestMessage != null && responseMessage != null) {
        long requestFromJavaConverterHandle = requestMessage.getFromJavaConverterInstance();
        long requestToJavaConverterHandle = requestMessage.getToJavaConverterInstance();
        long requestDestructorHandle = requestMessage.getDestructorInstance();
        long responseFromJavaConverterHandle = responseMessage.getFromJavaConverterInstance();
        long responseToJavaConverterHandle = responseMessage.getToJavaConverterInstance();
        long responseDestructorHandle = responseMessage.getDestructorInstance();

        RMWRequestId rmwRequestId =
          nativeTakeGoalRequest(
            this.handle,
            requestFromJavaConverterHandle, requestToJavaConverterHandle, requestDestructorHandle,
            requestMessage);
        if (rmwRequestId != null) {
          ActionServerGoalHandle<T> goalHandle = this.executeGoalRequest(
            rmwRequestId, requestMessage, responseMessage);
          nativeSendGoalResponse(
            this.handle, rmwRequestId,
            responseFromJavaConverterHandle, responseToJavaConverterHandle,
            responseDestructorHandle, responseMessage);
        }
      }
    }

    if (this.isCancelRequestReady()) {
      action_msgs.srv.CancelGoal_Request requestMessage = new action_msgs.srv.CancelGoal_Request();
      action_msgs.srv.CancelGoal_Response responseMessage = new action_msgs.srv.CancelGoal_Response();

      long requestFromJavaConverterHandle = requestMessage.getFromJavaConverterInstance();
      long requestToJavaConverterHandle = requestMessage.getToJavaConverterInstance();
      long requestDestructorHandle = requestMessage.getDestructorInstance();
      long responseFromJavaConverterHandle = responseMessage.getFromJavaConverterInstance();
      long responseToJavaConverterHandle = responseMessage.getToJavaConverterInstance();
      long responseDestructorHandle = responseMessage.getDestructorInstance();

      RMWRequestId rmwRequestId =
        nativeTakeCancelRequest(
          this.handle,
          requestFromJavaConverterHandle, requestToJavaConverterHandle, requestDestructorHandle,
          requestMessage);
      if (rmwRequestId != null) {
        nativeProcessCancelRequest(
          this.handle,
          requestFromJavaConverterHandle,
          requestDestructorHandle,
          responseToJavaConverterHandle,
          requestMessage,
          responseMessage);
        responseMessage = executeCancelRequest(responseMessage);
        nativeSendCancelResponse(
          this.handle, rmwRequestId,
          responseFromJavaConverterHandle, responseToJavaConverterHandle,
          responseDestructorHandle, responseMessage);
      }
    }

    if (this.isResultRequestReady()) {
      Class<? extends ResultRequestDefinition> requestType = this.actionTypeInstance.getGetResultRequestType();

      ResultRequestDefinition<T> requestMessage = null;
      try {
        requestMessage = requestType.newInstance();
      } catch (Exception ex) {
        throw new IllegalArgumentException("Failed to instantiate action result request", ex);
      }

      if (requestMessage != null) {
        long requestFromJavaConverterHandle = requestMessage.getFromJavaConverterInstance();
        long requestToJavaConverterHandle = requestMessage.getToJavaConverterInstance();
        long requestDestructorHandle = requestMessage.getDestructorInstance();

        RMWRequestId rmwRequestId =
          nativeTakeResultRequest(
            this.handle,
            requestFromJavaConverterHandle, requestToJavaConverterHandle, requestDestructorHandle,
            requestMessage);

        if (rmwRequestId == null) {
          return;
        }

        List<Byte> goalUuid = requestMessage.getGoalUuid();
        boolean goalExists = this.goalExists(this.createGoalInfo(goalUuid));

        ResultResponseDefinition<T> resultResponse = null;
        if (!goalExists) {
          resultResponse = this.createResultResponse();
          resultResponse.setGoalStatus(action_msgs.msg.GoalStatus.STATUS_UNKNOWN);
        } else {
          resultResponse = this.goalResults.get(goalUuid);
        }

        if (null == resultResponse) {
          List<RMWRequestId> requestIds = null;
          requestIds = this.goalRequests.get(goalUuid);
          if (requestIds == null) {
            requestIds = new ArrayList();
            this.goalRequests.put(goalUuid, requestIds);
          }
          requestIds.add(rmwRequestId);
        } else {
          this.sendResultResponse(rmwRequestId, resultResponse);
        }
      }
    }

    if (this.isGoalExpiredReady()) {
      this.expireGoals();
    }
  }

  /**
   * Destroy the underlying rcl_action_server_t.
   *
   * @param nodeHandle A pointer to the underlying rcl_node_t handle that
   *     created this action server.
   * @param handle A pointer to the underlying rcl_action_server_t
   */
  private static native void nativeDispose(long nodeHandle, long handle);

  /**
   * {@inheritDoc}
   */
  public final void dispose() {
    Node node = this.nodeReference.get();
    if (node != null) {
      nativeDispose(node.getHandle(), this.handle);
      node.removeActionServer(this);
      this.handle = 0;
    }
  }

  /**
   * {@inheritDoc}
   */
  public final long getHandle() {
    return handle;
  }
}
