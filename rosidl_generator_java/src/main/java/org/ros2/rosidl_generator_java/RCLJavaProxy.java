/* Copyright 2016 Esteve Fernandez <esteve@apache.org>
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
package org.ros2.rosidl_generator_java;

import java.lang.reflect.Method;

public class RCLJavaProxy {
    public static synchronized String getTypesupportIdentifier() {
        try {
            Class c = Class.forName("org.ros2.rcljava.RCLJava");
            Method m = c.getDeclaredMethod("getTypesupportIdentifier", (Class<?> []) null);
            Object o = m.invoke(null, (Class<?> []) null);
            return (String)o;
        } catch(Exception e) {
            // TODO(esteve): handle exception
            return null;
        }
    }

    public static synchronized String getRMWIdentifier() {
        try {
            Class c = Class.forName("org.ros2.rcljava.RCLJava");
            Method m = c.getDeclaredMethod("getRMWIdentifier", (Class<?> []) null);
            Object o = m.invoke(null, (Class<?> []) null);
            return (String)o;
        } catch(Exception e) {
            // TODO(esteve): handle exception
            return null;
        }
    }

    public static synchronized void loadLibrary(String name) {
        try {
            Class c = Class.forName("org.ros2.rcljava.RCLJava");
            Method m = c.getDeclaredMethod("loadLibrary", String.class);
            Object o = m.invoke(null, name);
        } catch(Exception e) {
            // TODO(esteve): handle exception
        }
    }
}
