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
import org.ros2.rcljava.interfaces.GoalRequestDefinition;
import org.ros2.rcljava.interfaces.GoalResponseDefinition;
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
    private ActionServer<T> actionServer;
    private action_msgs.msg.GoalInfo goalInfo;
    private MessageDefinition goal;

    private native long nativeAcceptNewGoal(
      long actionServerHandle,
      long goalInfoFromJavaConverterHandle,
      long goalInfoDestructorHandle,
      MessageDefinition goalInfo);
    private native int nativeGetStatus(long goalHandle);
    private native void nativeGoalEventExecute(long goalHandle);
    private native void nativeGoalEventCancelGoal(long goalHandle);
    private native void nativeGoalEventSucceed(long goalHandle);
    private native void nativeGoalEventAbort(long goalHandle);
    private native void nativeGoalEventCanceled(long goalHandle);
    private native void nativeDispose(long handle);

    public GoalHandleImpl(
      ActionServer<T> actionServer, action_msgs.msg.GoalInfo goalInfo, MessageDefinition goal)
    {
      this.actionServer = actionServer;
      this.goalInfo = goalInfo;
      this.goal = goal;
      long goalInfoFromJavaConverterHandle = goalInfo.getFromJavaConverterInstance();
      long goalInfoDestructorHandle = goalInfo.getDestructorInstance();
      this.handle = nativeAcceptNewGoal(
        actionServer.getHandle(),
        goalInfoFromJavaConverterHandle,
        goalInfoDestructorHandle,
        goalInfo);
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
    public MessageDefinition getGoal() {
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
        nativeGoalEventExecute(this.handle);
      }
    }

    /**
     * Transition the goal to the CANCELING state.
     */
    public synchronized void cancelGoal() {
      nativeGoalEventCancelGoal(this.handle);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void succeed() {
      nativeGoalEventSucceed(this.handle);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void canceled() {
      nativeGoalEventCanceled(this.handle);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void abort() {
      nativeGoalEventAbort(this.handle);
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
    action_msgs.msg.GoalInfo goalInfo = new action_msgs.msg.GoalInfo();
    unique_identifier_msgs.msg.UUID uuidMessage= new unique_identifier_msgs.msg.UUID();
    uuidMessage.setUuid(goalUuid);
    goalInfo.setGoalId(uuidMessage);
    goalInfo.setStamp(timeRequestHandled);

    long goalInfoFromJavaConverterHandle = goalInfo.getFromJavaConverterInstance();
    long goalInfoDestructorHandle = goalInfo.getDestructorInstance();

    // Check that the goal ID isn't already being used
    boolean goalExists = nativeCheckGoalExists(
      this.handle, goalInfo, goalInfoFromJavaConverterHandle, goalInfoDestructorHandle);
    if (goalExists) {
      logger.warn("Received goal request for goal already being tracked by action server. Goal ID: " + goalUuid);
      responseMessage.accept(false);
      return null;
    }

    // Call user callback
    GoalCallback.GoalResponse response = this.goalCallback.handleGoal(requestMessage);

    boolean accepted = GoalCallback.GoalResponse.ACCEPT == response;
    responseMessage.accept(accepted);

    System.out.println("Goal request handled " + accepted);
    if (!accepted) {
      return null;
    }

    // Create a goal handle and add it to the list of goals
    GoalHandleImpl goalHandle = this.new GoalHandleImpl(
      this, goalInfo, requestMessage.getGoal());
    this.goalHandles.put(requestMessage.getGoalUuid(), goalHandle);
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
          if (goalHandle != null) {
            this.acceptedCallback.accept(goalHandle);
          }
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
      // executeResultRequest(rmwRequestId, requestMessage, responseMessage);
      // TODO
    }

    if (this.isGoalExpiredReady()) {
      // cleanupExpiredGoals();
      // TODO
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
