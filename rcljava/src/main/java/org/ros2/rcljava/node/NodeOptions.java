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

package org.ros2.rcljava.node;

import java.util.ArrayList;

public class NodeOptions {
  private boolean useGlobalArguments;
  private boolean enableRosout;
  private boolean allowUndeclaredParameters;
  private ArrayList<String> cliArgs;

  public NodeOptions() {
    this.useGlobalArguments = true;
    this.enableRosout = true;
    this.allowUndeclaredParameters = false;
    this.cliArgs = new ArrayList<String>();
  }

  public final boolean getUseGlobalArguments() {
    return this.useGlobalArguments;
  }

  public NodeOptions setUseGlobalArguments(boolean useGlobal) {
    this.useGlobalArguments = useGlobal;
    return this;
  }

  public final boolean getEnableRosout() {
    return this.enableRosout;
  }

  public NodeOptions setEnableRosout(boolean enable) {
    this.enableRosout = enable;
    return this;
  }

  public final boolean getAllowUndeclaredParameters() {
    return this.allowUndeclaredParameters;
  }

  public NodeOptions setAllowUndeclaredParameters(boolean allow) {
    this.allowUndeclaredParameters = allow;
    return this;
  }

  public final ArrayList<String> getCliArgs() {
    return this.cliArgs;
  }

  public NodeOptions setCliArgs(ArrayList<String> newArgs) {
    this.cliArgs = newArgs;
    return this;
  }
}
