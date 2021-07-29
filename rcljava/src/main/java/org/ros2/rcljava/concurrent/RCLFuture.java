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

package org.ros2.rcljava.concurrent;

import java.lang.Deprecated;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.node.Node;

public class RCLFuture<V> implements Future<V> {
  private WeakReference<Node> nodeReference;
  private boolean done = false;
  private V value = null;

  public RCLFuture() {}

  public final synchronized V get() throws InterruptedException, ExecutionException {
    if(this.value != null) {
      return this.value;
    }
    while (RCLJava.ok() && !isDone()) {
      this.wait();
    }
    return this.value;
  }

  public final synchronized V get(final long timeout, final TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    if (isDone()) {
      return value;
    }

    long endTime = System.nanoTime();
    long timeoutNS = unit.toNanos(timeout);

    if (timeoutNS > 0) {
      endTime += timeoutNS;
    }

    while (RCLJava.ok()) {
      this.wait(TimeUnit.NANOSECONDS.toMillis(timeoutNS), (int) (timeoutNS % 1000000l));

      if (isDone()) {
        return value;
      }

      long now = System.nanoTime();
      if (now >= endTime) {
        throw new TimeoutException();
      } else {
        timeoutNS = endTime - now;
      }
    }
    throw new InterruptedException();
  }

  public final boolean isDone() {
    return done;
  }

  public final boolean isCancelled() {
    return false;
  }

  public final boolean cancel(final boolean mayInterruptIfRunning) {
    return false;
  }

  public final synchronized void set(final V value) {
    this.value = value;
    done = true;
    this.notify();
  }
}
