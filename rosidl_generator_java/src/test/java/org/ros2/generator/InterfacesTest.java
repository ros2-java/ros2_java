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

package org.ros2.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class InterfacesTest {
  @BeforeClass
  public static void setupOnce() {
    org.apache.log4j.BasicConfigurator.configure();
  }

  @Rule public ExpectedException thrown = ExpectedException.none();

  @Test
  public final void testBool() {
    boolean expected1 = false;
    rosidl_generator_java.msg.Bool boolOne = new rosidl_generator_java.msg.Bool();
    boolOne.setEmptyBool(expected1);
    assertEquals(expected1, boolOne.getEmptyBool());

    boolean expected2 = true;
    rosidl_generator_java.msg.Bool boolTwo = new rosidl_generator_java.msg.Bool();
    boolTwo.setEmptyBool(expected2);
    assertEquals(expected2, boolTwo.getEmptyBool());

    boolOne.setEmptyBool(expected2);
    assertEquals(expected2, boolOne.getEmptyBool());
  }

  @Test
  public final void testByte() {
    byte expected1 = 123;
    rosidl_generator_java.msg.Byte byteOne = new rosidl_generator_java.msg.Byte();
    byteOne.setEmptyByte(expected1);
    assertEquals(expected1, byteOne.getEmptyByte());

    byte expected2 = -42;
    rosidl_generator_java.msg.Byte byteTwo = new rosidl_generator_java.msg.Byte();
    byteTwo.setEmptyByte(expected2);
    assertEquals(expected2, byteTwo.getEmptyByte());

    byteOne.setEmptyByte(expected2);
    assertEquals(expected2, byteOne.getEmptyByte());
  }

  @Test
  public final void testChar() {
    byte expected1 = 'a';
    rosidl_generator_java.msg.Char charOne = new rosidl_generator_java.msg.Char();
    charOne.setEmptyChar(expected1);
    assertEquals(expected1, charOne.getEmptyChar());

    byte expected2 = 'b';
    rosidl_generator_java.msg.Char charTwo = new rosidl_generator_java.msg.Char();
    charTwo.setEmptyChar(expected2);
    assertEquals(expected2, charTwo.getEmptyChar());

    charOne.setEmptyChar(expected2);
    assertEquals(expected2, charOne.getEmptyChar());
  }

  @Test
  public final void testConstants() {
    assertEquals(123, rosidl_generator_java.msg.Constants.X);
    assertEquals(-123, rosidl_generator_java.msg.Constants.Y);
    assertEquals("foo", rosidl_generator_java.msg.Constants.FOO);
    assertEquals('\u007f', rosidl_generator_java.msg.Constants.TOTO);
    assertEquals(48, rosidl_generator_java.msg.Constants.TATA);
  }

  @Test
  public final void testEmpty() {
    rosidl_generator_java.msg.Empty empty = new rosidl_generator_java.msg.Empty();
    assertNotEquals(null, empty);
  }

  @Test
  public final void testFloat32() {
    float expected1 = 12.34f;
    rosidl_generator_java.msg.Float32 float32One = new rosidl_generator_java.msg.Float32();
    float32One.setEmptyFloat32(expected1);
    assertEquals(expected1, float32One.getEmptyFloat32(), 0.01);

    float expected2 = -43.21f;
    rosidl_generator_java.msg.Float32 float32Two = new rosidl_generator_java.msg.Float32();
    float32Two.setEmptyFloat32(expected2);
    assertEquals(expected2, float32Two.getEmptyFloat32(), 0.01);

    float32One.setEmptyFloat32(expected2);
    assertEquals(expected2, float32One.getEmptyFloat32(), 0.01);
  }

  @Test
  public final void testFloat64() {
    double expected1 = 12.34;
    rosidl_generator_java.msg.Float64 float64One = new rosidl_generator_java.msg.Float64();
    float64One.setEmptyFloat64(expected1);
    assertEquals(expected1, float64One.getEmptyFloat64(), 0.01);

    double expected2 = -43.21;
    rosidl_generator_java.msg.Float64 float64Two = new rosidl_generator_java.msg.Float64();
    float64Two.setEmptyFloat64(expected2);
    assertEquals(expected2, float64Two.getEmptyFloat64(), 0.01);

    float64One.setEmptyFloat64(expected2);
    assertEquals(expected2, float64One.getEmptyFloat64(), 0.01);
  }

  @Test
  public final void testInt8() {
    byte expected1 = 123;
    rosidl_generator_java.msg.Int8 byteOne = new rosidl_generator_java.msg.Int8();
    byteOne.setEmptyInt8(expected1);
    assertEquals(expected1, byteOne.getEmptyInt8());

    byte expected2 = -42;
    rosidl_generator_java.msg.Int8 byteTwo = new rosidl_generator_java.msg.Int8();
    byteTwo.setEmptyInt8(expected2);
    assertEquals(expected2, byteTwo.getEmptyInt8());

    byteOne.setEmptyInt8(expected2);
    assertEquals(expected2, byteOne.getEmptyInt8());
  }

  @Test
  public final void testInt16() {
    short expected1 = 1230;
    rosidl_generator_java.msg.Int16 shortOne = new rosidl_generator_java.msg.Int16();
    shortOne.setEmptyInt16(expected1);
    assertEquals(expected1, shortOne.getEmptyInt16());

    short expected2 = -420;
    rosidl_generator_java.msg.Int16 shortTwo = new rosidl_generator_java.msg.Int16();
    shortTwo.setEmptyInt16(expected2);
    assertEquals(expected2, shortTwo.getEmptyInt16());

    shortOne.setEmptyInt16(expected2);
    assertEquals(expected2, shortOne.getEmptyInt16());
  }

  @Test
  public final void testInt32() {
    int expected1 = 123000;
    rosidl_generator_java.msg.Int32 intOne = new rosidl_generator_java.msg.Int32();
    intOne.setEmptyInt32(expected1);
    assertEquals(expected1, intOne.getEmptyInt32());

    int expected2 = -42000;
    rosidl_generator_java.msg.Int32 intTwo = new rosidl_generator_java.msg.Int32();
    intTwo.setEmptyInt32(expected2);
    assertEquals(expected2, intTwo.getEmptyInt32());

    intOne.setEmptyInt32(expected2);
    assertEquals(expected2, intOne.getEmptyInt32());
  }

  @Test
  public final void testInt64() {
    long expected1 = 42949672960L;
    rosidl_generator_java.msg.Int64 longOne = new rosidl_generator_java.msg.Int64();
    longOne.setEmptyInt64(expected1);
    assertEquals(expected1, longOne.getEmptyInt64());

    long expected2 = -4200000L;
    rosidl_generator_java.msg.Int64 longTwo = new rosidl_generator_java.msg.Int64();
    longTwo.setEmptyInt64(expected2);
    assertEquals(expected2, longTwo.getEmptyInt64());

    longOne.setEmptyInt64(expected2);
    assertEquals(expected2, longOne.getEmptyInt64());
  }

  @Test
  public final void testDefaultValues() {
    rosidl_generator_java.msg.Strings a = new rosidl_generator_java.msg.Strings();

    assertEquals("", a.getEmptyString());
    assertEquals("Hello world!", a.getDefString());
    a.setDefString("Bye world");
    assertEquals("Bye world", a.getDefString());

    rosidl_generator_java.msg.Various b = new rosidl_generator_java.msg.Various();
    assertEquals(Arrays.asList(new Short[] {5, 23}), b.getTwoUint16Value());
    assertEquals(
        Arrays.asList(new Integer[] {5, 23}), b.getUpToThreeInt32ValuesWithDefaultValues());

    assertEquals('\u0001', b.getCharValue());
    assertNotEquals('1', b.getCharValue());
    assertEquals((byte) '\u0001', b.getByteValue());
    assertNotEquals((byte) '1', b.getByteValue());
  }

  @Test
  public final void testCheckStringConstraints() {
    rosidl_generator_java.msg.Strings a = new rosidl_generator_java.msg.Strings();
    a.setEmptyString("test");
    assertEquals("test", a.getEmptyString());

    char[] chars22 = new char[22];
    Arrays.fill(chars22, 'a');
    String chars22String = new String(chars22);
    a.setUbString(chars22String);
    assertEquals(chars22String, a.getUbString());

    char[] chars23 = new char[23];
    Arrays.fill(chars23, 'a');
    String chars23String = new String(chars23);

    thrown.expect(IllegalArgumentException.class);
    a.setUbString(chars23String);
  }

  @Test
  public final void testCheckFixedArrayConstraints() {
    rosidl_generator_java.msg.Nested b = new rosidl_generator_java.msg.Nested();
    rosidl_generator_java.msg.Primitives primitives = new rosidl_generator_java.msg.Primitives();
    b.setPrimitives(primitives);
    assertEquals(primitives, b.getPrimitives());

    List listOfPrimitives =
        Arrays.asList(new rosidl_generator_java.msg.Primitives[] {primitives, primitives});
    b.setTwoPrimitives(listOfPrimitives);
    assertEquals(listOfPrimitives, b.getTwoPrimitives());

    thrown.expect(IllegalArgumentException.class);
    b.setTwoPrimitives(Arrays.asList(new rosidl_generator_java.msg.Primitives[] {primitives}));
  }

  @Test
  public final void testCheckUpperboundArrayConstraints() {
    rosidl_generator_java.msg.Nested b = new rosidl_generator_java.msg.Nested();
    rosidl_generator_java.msg.Primitives primitives = new rosidl_generator_java.msg.Primitives();
    b.setUpToThreePrimitives(Arrays.asList(new rosidl_generator_java.msg.Primitives[] {}));
    assertEquals(
        Arrays.asList(new rosidl_generator_java.msg.Primitives[] {}), b.getUpToThreePrimitives());
    b.setUpToThreePrimitives(
        Arrays.asList(new rosidl_generator_java.msg.Primitives[] {primitives}));
    assertEquals(Arrays.asList(new rosidl_generator_java.msg.Primitives[] {primitives}),
        b.getUpToThreePrimitives());
    b.setUpToThreePrimitives(
        Arrays.asList(new rosidl_generator_java.msg.Primitives[] {primitives, primitives}));
    assertEquals(Arrays.asList(new rosidl_generator_java.msg.Primitives[] {primitives, primitives}),
        b.getUpToThreePrimitives());
    b.setUpToThreePrimitives(Arrays.asList(
        new rosidl_generator_java.msg.Primitives[] {primitives, primitives, primitives}));
    assertEquals(Arrays.asList(new rosidl_generator_java.msg.Primitives[] {
                     primitives, primitives, primitives}),
        b.getUpToThreePrimitives());

    thrown.expect(IllegalArgumentException.class);
    b.setUpToThreePrimitives(Arrays.asList(new rosidl_generator_java.msg.Primitives[] {
        primitives, primitives, primitives, primitives}));
  }

  @Test
  public final void testCheckUnboundedArrays() {
    rosidl_generator_java.msg.Nested b = new rosidl_generator_java.msg.Nested();
    rosidl_generator_java.msg.Primitives primitives = new rosidl_generator_java.msg.Primitives();
    b.setUnboundedPrimitives(
        Arrays.asList(new rosidl_generator_java.msg.Primitives[] {primitives, primitives}));
    assertEquals(Arrays.asList(new rosidl_generator_java.msg.Primitives[] {primitives, primitives}),
        b.getUnboundedPrimitives());
  }

  @Test
  public final void testCheckIntArraysConstraints() {
    rosidl_generator_java.msg.Various c = new rosidl_generator_java.msg.Various();
    c.setUpToThreeInt32Values(Arrays.asList(new Integer[] {}));
    assertEquals(Arrays.asList(new Integer[] {}), c.getUpToThreeInt32Values());
    c.setUpToThreeInt32Values(Arrays.asList(new Integer[] {12345}));
    assertEquals(Arrays.asList(new Integer[] {12345}), c.getUpToThreeInt32Values());
    c.setUpToThreeInt32Values(Arrays.asList(new Integer[] {12345, -12345}));
    assertEquals(Arrays.asList(new Integer[] {12345, -12345}), c.getUpToThreeInt32Values());
    c.setUpToThreeInt32Values(Arrays.asList(new Integer[] {12345, -12345, 6789}));
    assertEquals(Arrays.asList(new Integer[] {12345, -12345, 6789}), c.getUpToThreeInt32Values());

    // Test that arrays of primitives are also accepted
    c.setUpToThreeInt32Values(new int[] {});
    assertEquals(Arrays.asList(new Integer[] {}), c.getUpToThreeInt32Values());
    c.setUpToThreeInt32Values(new int[] {12345});
    assertEquals(Arrays.asList(new Integer[] {12345}), c.getUpToThreeInt32Values());
    c.setUpToThreeInt32Values(new int[] {12345, -12345});
    assertEquals(Arrays.asList(new Integer[] {12345, -12345}), c.getUpToThreeInt32Values());
    c.setUpToThreeInt32Values(new int[] {12345, -12345, 6789});
    assertEquals(Arrays.asList(new Integer[] {12345, -12345, 6789}), c.getUpToThreeInt32Values());

    thrown.expect(IllegalArgumentException.class);
    c.setUpToThreeInt32Values(Arrays.asList(new Integer[] {12345, -12345, 6789, -6789}));
  }

  @Test
  public final void testCheckUpperboundStringConstraints() {
    rosidl_generator_java.msg.Various c = new rosidl_generator_java.msg.Various();
    c.setUpToThreeStringValues(Arrays.asList(new String[] {}));
    assertEquals(Arrays.asList(new String[] {}), c.getUpToThreeStringValues());
    c.setUpToThreeStringValues(Arrays.asList(new String[] {"foo"}));
    assertEquals(Arrays.asList(new String[] {"foo"}), c.getUpToThreeStringValues());
    c.setUpToThreeStringValues(Arrays.asList(new String[] {"foo", "bar"}));
    assertEquals(Arrays.asList(new String[] {"foo", "bar"}), c.getUpToThreeStringValues());
    c.setUpToThreeStringValues(Arrays.asList(new String[] {"foo", "bar", "baz"}));
    assertEquals(Arrays.asList(new String[] {"foo", "bar", "baz"}), c.getUpToThreeStringValues());

    thrown.expect(IllegalArgumentException.class);
    c.setUpToThreeStringValues(Arrays.asList(new String[] {"foo", "bar", "baz", "hello"}));
  }
}
