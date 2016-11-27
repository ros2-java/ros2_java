/* Copyright 2016 Esteve Fernandez <esteve@apache.org>
 * Copyright 2016 Mickael Gaillard <mick.gaillard@gmail.com>
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

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ros2.rcljava.namespace.GraphName;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.node.topic.Topics;

public class GraphNameTest {

    @BeforeClass
    public static void beforeClass() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
    }

    @Test
    public final void testIsTopicName() {
        // @see http://design.ros2.org/articles/topic_and_service_names.html

        // must not be empty
        Assert.assertFalse("Topic must not be null.", GraphName.isValidTopic(null));
        Assert.assertFalse("Topic must not be empty.", GraphName.isValidTopic(""));

        // may contain alphanumeric characters ([0-9|a-z|A-Z]), underscores (_), or forward slashes (/)
//        Assert.assertFalse("Topic can be contains valid charactere.", GraphName.isValidTopic("&é'(-èçà)=#[|\^@]^$ù*,;:!£%µ?.§"));
        Assert.assertTrue("Topic may contains valid charactere.", GraphName.isValidTopic("~/_foo"));
        Assert.assertTrue("Topic may contains valid charactere.", GraphName.isValidTopic("azertyuiopqsdfghjklmwxcvbn1234567890"));

        // may use balanced curly braces ({}) for substitutions
        Assert.assertTrue("Topic may use balanced curly braces.", GraphName.isValidTopic("{bar}"));

        // may start with a tilde (~), the private namespace substitution character
        Assert.assertTrue("Topic may start with a tilde (~)", GraphName.isValidTopic("~/foo"));
        Assert.assertFalse("Topic may start with a tilde (~).", GraphName.isValidTopic("foo~"));

        // must not start with a numeric character ([0-9])
        Assert.assertFalse("Topic must not start with a numeric character ([0-9]).", GraphName.isValidTopic("8foo"));

        // must not contain any number of repeated underscores (_)
        Assert.assertFalse("Topic must not contain any number of repeated underscores (_).", GraphName.isValidTopic("foo__bar"));

        // must not end with an underscore (_)
        Assert.assertFalse("Topic must not end with an underscore (_).", GraphName.isValidTopic("foo_"));

        // must not have an underscore (_) followed by a forward slash (/), i.e. _/
        Assert.assertFalse("Topic must not have an underscore (_) followed by a forward slash.", GraphName.isValidTopic("foo_/bar"));

        // must not end with a forward slash (/)
        Assert.assertFalse("Topic must not end with a forward slash (/).", GraphName.isValidTopic("foo/bar/"));

        // must not contain any number of repeated forward slashes (/)
        Assert.assertFalse("Topic must not contain any number of repeated forward slashes (/).", GraphName.isValidTopic("foo//bar"));

        // must separate a tilde (~) from the rest of the name with a forward slash (/), i.e. ~/foo not ~foo
        Assert.assertFalse("Topic must separate a tilde (~) from the rest of the name with a forward slash (/).", GraphName.isValidTopic("~foo"));

        // must have balanced curly braces ({}) when used, i.e. {sub}/foo but not {sub/foo nor /foo}
        Assert.assertTrue("Topic must have balanced curly braces ({}) when used.", GraphName.isValidTopic("~/foo"));

        // Test case Valid
        Assert.assertTrue(GraphName.isValidTopic("foo"));
        Assert.assertTrue(GraphName.isValidTopic("abc123"));
        Assert.assertTrue(GraphName.isValidTopic("_foo"));
        Assert.assertTrue(GraphName.isValidTopic("Foo"));
        Assert.assertTrue(GraphName.isValidTopic("BAR"));
        Assert.assertTrue(GraphName.isValidTopic("~"));
        Assert.assertTrue(GraphName.isValidTopic("foo/bar"));
        Assert.assertTrue(GraphName.isValidTopic("~/foo"));
        Assert.assertTrue(GraphName.isValidTopic("{foo}_bar"));
        Assert.assertTrue(GraphName.isValidTopic("foo/{ping}/bar"));
        Assert.assertTrue(GraphName.isValidTopic(Topics.SCHEME + "/foo"));
        Assert.assertTrue(GraphName.isValidTopic(Topics.SCHEME + "foo/bar"));

        // Test case not valid
        Assert.assertFalse(GraphName.isValidTopic("123abc"));
        Assert.assertFalse(GraphName.isValidTopic("123"));
        Assert.assertFalse(GraphName.isValidTopic("__foo"));
        Assert.assertFalse(GraphName.isValidTopic("foo__bar"));
        Assert.assertFalse(GraphName.isValidTopic("foo bar"));
        Assert.assertFalse(GraphName.isValidTopic("foo__"));
        Assert.assertFalse(GraphName.isValidTopic(" "));
        Assert.assertFalse(GraphName.isValidTopic("foo_"));
        Assert.assertFalse(GraphName.isValidTopic("foo//bar"));
        Assert.assertFalse(GraphName.isValidTopic("foo/"));
        Assert.assertFalse(GraphName.isValidTopic("foo_/bar	"));
        Assert.assertFalse(GraphName.isValidTopic("~foo"));
        Assert.assertFalse(GraphName.isValidTopic("foo~"));
        Assert.assertFalse(GraphName.isValidTopic("foo~/bar"));
        Assert.assertFalse(GraphName.isValidTopic("foo/~bar"));
        Assert.assertFalse(GraphName.isValidTopic("/_/bar"));
        Assert.assertFalse(GraphName.isValidTopic("_"));
        Assert.assertFalse(GraphName.isValidTopic("_/_bar"));
        Assert.assertFalse(GraphName.isValidTopic("/~"));
        Assert.assertFalse(GraphName.isValidTopic("foo/~/bar"));
    }

    @Test
    public final void testIsSubstitution() {
        // must not be empty
        Assert.assertFalse("Substitution must not be null.", GraphName.isValidSubstitution(null));
        Assert.assertFalse("Substitution must not be empty.", GraphName.isValidSubstitution(""));

        // may contain alphanumeric characters ([0-9|a-z|A-Z]) and underscores (_)
        Assert.assertTrue("Substitution may contain alphanumeric characters.", GraphName.isValidSubstitution("foo_bar"));
        Assert.assertTrue("Substitution may contain alphanumeric characters.", GraphName.isValidSubstitution("azertyuiopqsdfghjklmwxcvbn1234567890"));

        // must not start with a numeric character ([0-9])
        Assert.assertFalse("Topic must not start with a numeric character ([0-9]).", GraphName.isValidTopic("8foo"));

        // are unlike topic and service names in that they:
        // may start with or end with underscores (_) //TODO (theos) For me risky (case far_{foo} with foo=_baz ) !!
        Assert.assertTrue("Substitution may start with underscores.", GraphName.isValidSubstitution("_foo"));
//        Assert.assertTrue("Substitution may end with underscores.", GraphName.isValidSubstitution("foo_"));

        // may contain repeated underscores, e.g. __ //TODO (theos) For me not valid !!
        Assert.assertTrue("Substitution may contain repeated underscores.", GraphName.isValidSubstitution("foo__bar"));
    }

    @Test
    public final void testIsTopicFQN() {
        // must start with a forward slash (/), i.e. they must be absolute
        Assert.assertTrue("FQN must start with a forward slash.", GraphName.isValideFQDN("/foo"));
        Assert.assertFalse("FQN must start with a forward slash.", GraphName.isValideFQDN("foo"));

        // must not contain tilde (~) or curly braces ({})
        Assert.assertFalse("FQN must not contain tilde.", GraphName.isValideFQDN("~foo"));
        Assert.assertFalse("FQN must not contain curly braces.", GraphName.isValideFQDN("foo/{bar}"));

        Assert.assertTrue(GraphName.isValideFQDN("/foo"));
        Assert.assertTrue(GraphName.isValideFQDN("/bar/baz"));
        Assert.assertTrue(GraphName.isValideFQDN(Topics.SCHEME + "/ping"));
        Assert.assertTrue(GraphName.isValideFQDN("/_private/thing"));
        Assert.assertTrue(GraphName.isValideFQDN("/public_namespace/_private/thing"));
    }

    @Test
    public final void testGetFullName() {
        RCLJava.rclJavaInit();

        Node node = RCLJava.createNode("my_node");
        Assert.assertEquals("/ping",            GraphName.getFullName(node, "ping",     null));
        Assert.assertEquals("/ping",            GraphName.getFullName(node, "/ping",    null));
        Assert.assertEquals("/my_node",         GraphName.getFullName(node, "~",        null));
        Assert.assertEquals("/my_node/ping",    GraphName.getFullName(node, "~/ping",   null));
        node.dispose();

        node = RCLJava.createNode("my_ns", "my_node");
        Assert.assertEquals("/my_ns/ping",          GraphName.getFullName(node, "ping",     null));
        Assert.assertEquals("/ping",                GraphName.getFullName(node, "/ping",    null));
        Assert.assertEquals("/my_ns/my_node",       GraphName.getFullName(node, "~",        null));
        Assert.assertEquals("/my_ns/my_node/ping",  GraphName.getFullName(node, "~/ping",   null));
        node.dispose();

        node = RCLJava.createNode("/my_ns/my_subns", "my_node");
        Assert.assertEquals("/my_ns/my_subns/ping",         GraphName.getFullName(node, "ping",     null));
        Assert.assertEquals("/ping",                        GraphName.getFullName(node, "/ping",    null));
        Assert.assertEquals("/my_ns/my_subns/my_node",      GraphName.getFullName(node, "~",        null));
        Assert.assertEquals("/my_ns/my_subns/my_node/ping", GraphName.getFullName(node, "~/ping",   null));
        node.dispose();

        RCLJava.shutdown();
    }
}
