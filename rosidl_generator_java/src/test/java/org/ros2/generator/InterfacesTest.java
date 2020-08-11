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
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.Arrays;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class InterfacesTest {
  @BeforeClass
  public static void setupOnce() {
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

  @Rule public ExpectedException thrown = ExpectedException.none();

  // TODO(jacobperron): Replace with JUnit's assertThrows method when we switch to JUnit 5
  // See: https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/Assertions.html
  private static void assertThrows(Class expectedException, Callable func) {
    try {
      func.call();
    }
    catch(Exception exception) {
      if (expectedException.isInstance(exception)) {
        return;
      }
    }
    assertTrue("Callable did not throw the expected exception", false);
  }

  @Test
  public final void testEmpty() {
    rosidl_generator_java.msg.Empty empty = new rosidl_generator_java.msg.Empty();
    assertNotEquals(null, empty);
  }

  @Test
  public final void testBasicTypes() {
    // Test setting/getting positive values
    rosidl_generator_java.msg.BasicTypes basicTypesOne = new rosidl_generator_java.msg.BasicTypes();
    boolean expectedBool1 = true;
    basicTypesOne.setBoolValue(expectedBool1);
    byte expectedByte1 = 123;
    basicTypesOne.setByteValue(expectedByte1);
    byte expectedChar1 = 'a';
    basicTypesOne.setCharValue(expectedChar1);
    float expectedFloat1 = 12.34f;
    basicTypesOne.setFloat32Value(expectedFloat1);
    double expectedDouble1 = 12.34;
    basicTypesOne.setFloat64Value(expectedDouble1);
    byte expectedInt81 = 123;
    basicTypesOne.setInt8Value(expectedInt81);
    short expectedInt161 = 1230;
    basicTypesOne.setInt16Value(expectedInt161);
    int expectedInt321 = 123000;
    basicTypesOne.setInt32Value(expectedInt321);
    long expectedInt641 = 42949672960L;
    basicTypesOne.setInt64Value(expectedInt641);

    assertEquals(expectedBool1, basicTypesOne.getBoolValue());
    assertEquals(expectedByte1, basicTypesOne.getByteValue());
    assertEquals(expectedChar1, basicTypesOne.getCharValue());
    assertEquals(expectedFloat1, basicTypesOne.getFloat32Value(), 0.01f);
    assertEquals(expectedDouble1, basicTypesOne.getFloat64Value(), 0.01);
    assertEquals(expectedInt81, basicTypesOne.getInt8Value());
    assertEquals(expectedInt161, basicTypesOne.getInt16Value());
    assertEquals(expectedInt321, basicTypesOne.getInt32Value());
    assertEquals(expectedInt641, basicTypesOne.getInt64Value());

    // Test setting/getting negative values
    rosidl_generator_java.msg.BasicTypes basicTypesTwo = new rosidl_generator_java.msg.BasicTypes();
    boolean expectedBool2 = false;
    basicTypesTwo.setBoolValue(expectedBool2);
    byte expectedByte2 = -42;
    basicTypesTwo.setByteValue(expectedByte2);
    byte expectedChar2 = ' ';
    basicTypesTwo.setCharValue(expectedChar2);
    float expectedFloat2 = -43.21f;
    basicTypesTwo.setFloat32Value(expectedFloat2);
    double expectedDouble2 = -43.21;
    basicTypesTwo.setFloat64Value(expectedDouble2);
    byte expectedInt82 = -42;
    basicTypesTwo.setInt8Value(expectedInt82);
    short expectedInt162 = -420;
    basicTypesTwo.setInt16Value(expectedInt162);
    int expectedInt322 = -42000;
    basicTypesTwo.setInt32Value(expectedInt322);
    long expectedInt642 = -4200000L;
    basicTypesTwo.setInt64Value(expectedInt642);

    assertEquals(expectedBool2, basicTypesTwo.getBoolValue());
    assertEquals(expectedByte2, basicTypesTwo.getByteValue());
    assertEquals(expectedChar2, basicTypesTwo.getCharValue());
    assertEquals(expectedFloat2, basicTypesTwo.getFloat32Value(), 0.01f);
    assertEquals(expectedDouble2, basicTypesTwo.getFloat64Value(), 0.01);
    assertEquals(expectedInt82, basicTypesTwo.getInt8Value());
    assertEquals(expectedInt162, basicTypesTwo.getInt16Value());
    assertEquals(expectedInt322, basicTypesTwo.getInt32Value());
    assertEquals(expectedInt642, basicTypesTwo.getInt64Value());
  }

  @Test
  public final void testConstants() {
    assertEquals(true, rosidl_generator_java.msg.Constants.BOOL_CONST);
    assertEquals(50, rosidl_generator_java.msg.Constants.BYTE_CONST);
    assertEquals(100, rosidl_generator_java.msg.Constants.CHAR_CONST);
    assertEquals(1.125f, rosidl_generator_java.msg.Constants.FLOAT32_CONST, 0.01f);
    assertEquals(1.125, rosidl_generator_java.msg.Constants.FLOAT64_CONST, 0.01);
    assertEquals(-50, rosidl_generator_java.msg.Constants.INT8_CONST);
    assertEquals((byte) 200, rosidl_generator_java.msg.Constants.UINT8_CONST);
    assertEquals(-1000, rosidl_generator_java.msg.Constants.INT16_CONST);
    assertEquals(2000, rosidl_generator_java.msg.Constants.UINT16_CONST);
    assertEquals(-30000, rosidl_generator_java.msg.Constants.INT32_CONST);
    assertEquals(60000, rosidl_generator_java.msg.Constants.UINT32_CONST);
    assertEquals(-40000000, rosidl_generator_java.msg.Constants.INT64_CONST);
    assertEquals(50000000, rosidl_generator_java.msg.Constants.UINT64_CONST);

    assertEquals("Hello world!", rosidl_generator_java.msg.Strings.STRING_CONST);
  }

  @Test
  public final void testDefaultValues() {
    rosidl_generator_java.msg.Defaults defaults = new rosidl_generator_java.msg.Defaults();
    assertEquals(true, defaults.getBoolValue());
    assertEquals(50, defaults.getByteValue());
    assertEquals(100, defaults.getCharValue());
    assertEquals(1.125f, defaults.getFloat32Value(), 0.01f);
    assertEquals(1.125, defaults.getFloat64Value(), 0.01);
    assertEquals(-50, defaults.getInt8Value());
    assertEquals((byte) 200, defaults.getUint8Value());
    assertEquals(-1000, defaults.getInt16Value());
    assertEquals(2000, defaults.getUint16Value());
    assertEquals(-30000, defaults.getInt32Value());
    assertEquals(60000, defaults.getUint32Value());
    assertEquals(-40000000, defaults.getInt64Value());
    assertEquals(50000000, defaults.getUint64Value());

    rosidl_generator_java.msg.Strings strings = new rosidl_generator_java.msg.Strings();
    assertEquals("Hello world!", strings.getStringValueDefault1());
    assertEquals("Hello'world!", strings.getStringValueDefault2());
    assertEquals("Hello\"world!", strings.getStringValueDefault3());
    assertEquals("Hello'world!", strings.getStringValueDefault4());
    assertEquals("Hello\"world!", strings.getStringValueDefault5());
    assertEquals("Hello world!", strings.getBoundedStringValueDefault1());
    assertEquals("Hello'world!", strings.getBoundedStringValueDefault2());
    assertEquals("Hello\"world!", strings.getBoundedStringValueDefault3());
    assertEquals("Hello'world!", strings.getBoundedStringValueDefault4());
    assertEquals("Hello\"world!", strings.getBoundedStringValueDefault5());
  }

  @Test
  public final void testCheckStringConstraints() {
    rosidl_generator_java.msg.Strings strings = new rosidl_generator_java.msg.Strings();
    strings.setStringValue("test");
    assertEquals("test", strings.getStringValue());

    char[] chars22 = new char[22];
    Arrays.fill(chars22, 'a');
    String chars22String = new String(chars22);
    strings.setBoundedStringValue(chars22String);
    assertEquals(chars22String, strings.getBoundedStringValue());

    char[] chars23 = new char[23];
    Arrays.fill(chars23, 'a');
    String chars23String = new String(chars23);

    thrown.expect(IllegalArgumentException.class);
    strings.setBoundedStringValue(chars23String);
  }

  @Test
  public final void testArrays() {
    rosidl_generator_java.msg.Arrays arrays = new rosidl_generator_java.msg.Arrays();

    // This value should not change and is asserted at end of test
    arrays.setAlignmentCheck(42);

    // Test setting/getting fixed length arrays of primitive types
    List boolList = Arrays.asList(true, false, true);
    arrays.setBoolValues(boolList);
    assertEquals(boolList, arrays.getBoolValues());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setBoolValues(Arrays.asList(true, false, true, false)));
    List byteList = Arrays.asList((byte) 0, (byte) 1, (byte) 255);
    arrays.setByteValues(byteList);
    assertEquals(byteList, arrays.getByteValues());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setByteValues(Arrays.asList((byte) 1, (byte) 2)));
    List charList = Arrays.asList(' ', 'a', 'Z');
    arrays.setCharValues(charList);
    assertEquals(charList, arrays.getCharValues());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setCharValues(Arrays.asList((byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd')));
    List float32List = Arrays.asList(0.0f, -1.125f, 1.125f);
    arrays.setFloat32Values(float32List);
    assertEquals(float32List, arrays.getFloat32Values());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setFloat32Values(Arrays.asList(1.0f, 2.0f)));
    List float64List = Arrays.asList(0.0f, -3.1415, 3.1415);
    arrays.setFloat64Values(float64List);
    assertEquals(float64List, arrays.getFloat64Values());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setFloat64Values(Arrays.asList(1.0, 2.0, 3.0, 4.0)));
    List int8List = Arrays.asList(0, -128, 127);
    arrays.setInt8Values(int8List);
    assertEquals(int8List, arrays.getInt8Values());
    assertThrows(IllegalArgumentException.class,
      () ->arrays.setInt8Values(Arrays.asList((byte) 1, (byte) 2)));
    List uint8List = Arrays.asList(0, 1, 255);
    arrays.setUint8Values(uint8List);
    assertEquals(uint8List, arrays.getUint8Values());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setUint8Values(Arrays.asList((byte) 1, (byte) 2, (byte) 3, (byte) 4)));
    List int16List = Arrays.asList(0, -32768, 32767);
    arrays.setInt16Values(int16List);
    assertEquals(int16List, arrays.getInt16Values());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setInt16Values(Arrays.asList((short) 1, (short) 2)));
    List uint16List = Arrays.asList(0, 1, 65535);
    arrays.setUint16Values(uint16List);
    assertEquals(uint16List, arrays.getUint16Values());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setUint16Values(Arrays.asList((short) 1, (short) 2, (short) 3, (short) 4)));
    List int32List = Arrays.asList(0, -2147483648, 2147483647);
    arrays.setInt32Values(int32List);
    assertEquals(int32List, arrays.getInt32Values());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setInt32Values(Arrays.asList(1, 2)));
    List uint32List = Arrays.asList(0, 1, 4294967295L);
    arrays.setUint32Values(uint32List);
    assertEquals(uint32List, arrays.getUint32Values());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setUint32Values(Arrays.asList(1, 2, 3, 4)));
    List int64List = Arrays.asList(0, -9223372036854775808L, 9223372036854775807L);
    arrays.setInt64Values(int64List);
    assertEquals(int64List, arrays.getInt64Values());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setInt64Values(Arrays.asList(1L, 2L)));
    List uint64List = Arrays.asList(0, 1, -1);
    arrays.setUint64Values(uint64List);
    assertEquals(uint64List, arrays.getUint64Values());
    assertThrows(IllegalArgumentException.class,
      () ->arrays.setUint64Values(Arrays.asList(1L, 2L, 3L, 4L)));

    // Test setting/getting fixed length arrays of strings
    List stringList = Arrays.asList("", "min value", "max_value");
    arrays.setStringValues(stringList);
    assertEquals(stringList, arrays.getStringValues());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setStringValues(Arrays.asList("too", "few")));

    // Test setting/getting fixed length arrays of nested types
    rosidl_generator_java.msg.BasicTypes basicTypes = new rosidl_generator_java.msg.BasicTypes();
    List basicTypesList = Arrays.asList(
        new rosidl_generator_java.msg.BasicTypes[] {basicTypes, basicTypes, basicTypes});
    arrays.setBasicTypesValues(basicTypesList);
    assertEquals(basicTypesList, arrays.getBasicTypesValues());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setBasicTypesValues(Arrays.asList(new rosidl_generator_java.msg.BasicTypes[] {basicTypes})));
    rosidl_generator_java.msg.Constants constants = new rosidl_generator_java.msg.Constants();
    List constantsList = Arrays.asList(
        new rosidl_generator_java.msg.Constants[] {constants, constants, constants});
    arrays.setConstantsValues(constantsList);
    assertEquals(constantsList, arrays.getConstantsValues());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setConstantsValues(Arrays.asList(new rosidl_generator_java.msg.Constants[] {constants})));
    rosidl_generator_java.msg.Defaults defaults = new rosidl_generator_java.msg.Defaults();
    List defaultsList = Arrays.asList(
        new rosidl_generator_java.msg.Defaults[] {defaults, defaults, defaults});
    arrays.setDefaultsValues(defaultsList);
    assertEquals(defaultsList, arrays.getDefaultsValues());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setDefaultsValues(Arrays.asList(new rosidl_generator_java.msg.Defaults[] {defaults})));

    assertEquals(42, arrays.getAlignmentCheck());
  }

  @Test
  public final void testBoundedSequences() {
    rosidl_generator_java.msg.BoundedSequences bounded_seq = new rosidl_generator_java.msg.BoundedSequences();

    // This value should not change and is asserted at end of test
    bounded_seq.setAlignmentCheck(42);

    // Test setting/getting fixed length bounded_seq of primitive types
    List boolList = Arrays.asList(true, false, true);
    bounded_seq.setBoolValues(boolList);
    assertEquals(boolList, bounded_seq.getBoolValues());
    List boolListShort = Arrays.asList(false);
    bounded_seq.setBoolValues(boolListShort);
    assertEquals(boolListShort, bounded_seq.getBoolValues());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setBoolValues(Arrays.asList(true, false, true, false)));
    List byteList = Arrays.asList((byte) 0, (byte) 1, (byte) 255);
    bounded_seq.setByteValues(byteList);
    assertEquals(byteList, bounded_seq.getByteValues());
    List byteListShort = Arrays.asList((byte) 1);
    bounded_seq.setByteValues(byteListShort);
    assertEquals(byteListShort, bounded_seq.getByteValues());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setByteValues(Arrays.asList((byte) 1, (byte) 2, (byte) 3, (byte) 4)));
    List charList = Arrays.asList(' ', 'a', 'Z');
    bounded_seq.setCharValues(charList);
    assertEquals(charList, bounded_seq.getCharValues());
    List charListShort = Arrays.asList('z', 'A');
    bounded_seq.setCharValues(charListShort);
    assertEquals(charListShort, bounded_seq.getCharValues());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setCharValues(Arrays.asList((byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd')));
    List float32List = Arrays.asList(0.0f, -1.125f, 1.125f);
    bounded_seq.setFloat32Values(float32List);
    assertEquals(float32List, bounded_seq.getFloat32Values());
    List float32ListShort = Arrays.asList(1.125f, -1.125f);
    bounded_seq.setFloat32Values(float32ListShort);
    assertEquals(float32ListShort, bounded_seq.getFloat32Values());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setFloat32Values(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f)));
    List float64List = Arrays.asList(0.0f, -3.1415, 3.1415);
    bounded_seq.setFloat64Values(float64List);
    assertEquals(float64List, bounded_seq.getFloat64Values());
    List float64ListShort = Arrays.asList(3.1415, -3.1415);
    bounded_seq.setFloat64Values(float64ListShort);
    assertEquals(float64ListShort, bounded_seq.getFloat64Values());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setFloat64Values(Arrays.asList(1.0, 2.0, 3.0, 4.0)));
    List int8List = Arrays.asList(0, -128, 127);
    bounded_seq.setInt8Values(int8List);
    assertEquals(int8List, bounded_seq.getInt8Values());
    List int8ListShort = Arrays.asList(127, -128);
    bounded_seq.setInt8Values(int8ListShort);
    assertEquals(int8ListShort, bounded_seq.getInt8Values());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setInt8Values(Arrays.asList((byte) 1, (byte) 2, (byte) 3, (byte) 4)));
    List uint8List = Arrays.asList(0, 1, 255);
    bounded_seq.setUint8Values(uint8List);
    assertEquals(uint8List, bounded_seq.getUint8Values());
    List uint8ListShort = Arrays.asList(255, 1);
    bounded_seq.setUint8Values(uint8ListShort);
    assertEquals(uint8ListShort, bounded_seq.getUint8Values());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setUint8Values(Arrays.asList((byte) 1, (byte) 2, (byte) 3, (byte) 4)));
    List int16List = Arrays.asList(0, -32768, 32767);
    bounded_seq.setInt16Values(int16List);
    assertEquals(int16List, bounded_seq.getInt16Values());
    List int16ListShort = Arrays.asList(32767, -32768);
    bounded_seq.setInt16Values(int16ListShort);
    assertEquals(int16ListShort, bounded_seq.getInt16Values());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setInt16Values(Arrays.asList((short) 1, (short) 2, (short) 3, (short) 4)));
    List uint16List = Arrays.asList(0, 1, 65535);
    bounded_seq.setUint16Values(uint16List);
    assertEquals(uint16List, bounded_seq.getUint16Values());
    List uint16ListShort = Arrays.asList(0, 1, 65535);
    bounded_seq.setUint16Values(uint16ListShort);
    assertEquals(uint16ListShort, bounded_seq.getUint16Values());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setUint16Values(Arrays.asList((short) 1, (short) 2, (short) 3, (short) 4)));
    List int32List = Arrays.asList(0, -2147483648, 2147483647);
    bounded_seq.setInt32Values(int32List);
    assertEquals(int32List, bounded_seq.getInt32Values());
    List int32ListShort = Arrays.asList(2147483647, -2147483648);
    bounded_seq.setInt32Values(int32ListShort);
    assertEquals(int32ListShort, bounded_seq.getInt32Values());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setInt32Values(Arrays.asList(1, 2, 3, 4)));
    List uint32List = Arrays.asList(0, 1, 4294967295L);
    bounded_seq.setUint32Values(uint32List);
    assertEquals(uint32List, bounded_seq.getUint32Values());
    List uint32ListShort = Arrays.asList(4294967295L, 1);
    bounded_seq.setUint32Values(uint32ListShort);
    assertEquals(uint32ListShort, bounded_seq.getUint32Values());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setUint32Values(Arrays.asList(1, 2, 3, 4)));
    List int64List = Arrays.asList(0, -9223372036854775808L, 9223372036854775807L);
    bounded_seq.setInt64Values(int64List);
    assertEquals(int64List, bounded_seq.getInt64Values());
    List int64ListShort = Arrays.asList(0, -9223372036854775808L, 9223372036854775807L);
    bounded_seq.setInt64Values(int64ListShort);
    assertEquals(int64ListShort, bounded_seq.getInt64Values());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setInt64Values(Arrays.asList(1L, 2L, 3L, 4L)));
    List uint64List = Arrays.asList(0, 1, -1);
    bounded_seq.setUint64Values(uint64List);
    assertEquals(uint64List, bounded_seq.getUint64Values());
    List uint64ListShort = Arrays.asList(0, 1, -1);
    bounded_seq.setUint64Values(uint64ListShort);
    assertEquals(uint64ListShort, bounded_seq.getUint64Values());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setUint64Values(Arrays.asList(1L, 2L, 3L, 4L)));

    // Test setting/getting fixed length bounded_seq of strings
    List stringList = Arrays.asList("", "min value", "max_value");
    bounded_seq.setStringValues(stringList);
    assertEquals(stringList, bounded_seq.getStringValues());
    List stringListShort = Arrays.asList("max_value", "");
    bounded_seq.setStringValues(stringListShort);
    assertEquals(stringListShort, bounded_seq.getStringValues());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setStringValues(Arrays.asList("too", "many", "values", "!")));

    // Test setting/getting fixed length bounded_seq of nested types
    rosidl_generator_java.msg.BasicTypes basicTypes = new rosidl_generator_java.msg.BasicTypes();
    List basicTypesList = Arrays.asList(
        new rosidl_generator_java.msg.BasicTypes[] {basicTypes, basicTypes, basicTypes});
    bounded_seq.setBasicTypesValues(basicTypesList);
    assertEquals(basicTypesList, bounded_seq.getBasicTypesValues());
    List basicTypesListShort = Arrays.asList(
        new rosidl_generator_java.msg.BasicTypes[] {basicTypes});
    bounded_seq.setBasicTypesValues(basicTypesListShort);
    assertEquals(basicTypesListShort, bounded_seq.getBasicTypesValues());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setBasicTypesValues(
          Arrays.asList(new rosidl_generator_java.msg.BasicTypes[] {basicTypes, basicTypes, basicTypes, basicTypes})));
    rosidl_generator_java.msg.Constants constants = new rosidl_generator_java.msg.Constants();
    List constantsList = Arrays.asList(
        new rosidl_generator_java.msg.Constants[] {constants, constants, constants});
    bounded_seq.setConstantsValues(constantsList);
    assertEquals(constantsList, bounded_seq.getConstantsValues());
    List constantsListShort = Arrays.asList(
        new rosidl_generator_java.msg.Constants[] {constants});
    bounded_seq.setConstantsValues(constantsListShort);
    assertEquals(constantsListShort, bounded_seq.getConstantsValues());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setConstantsValues(
          Arrays.asList(new rosidl_generator_java.msg.Constants[] {constants, constants, constants, constants})));
    rosidl_generator_java.msg.Defaults defaults = new rosidl_generator_java.msg.Defaults();
    List defaultsList = Arrays.asList(
        new rosidl_generator_java.msg.Defaults[] {defaults, defaults, defaults});
    bounded_seq.setDefaultsValues(defaultsList);
    assertEquals(defaultsList, bounded_seq.getDefaultsValues());
    List defaultsListShort = Arrays.asList(
        new rosidl_generator_java.msg.Defaults[] {defaults, defaults, defaults});
    bounded_seq.setDefaultsValues(defaultsListShort);
    assertEquals(defaultsListShort, bounded_seq.getDefaultsValues());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setDefaultsValues(
          Arrays.asList(new rosidl_generator_java.msg.Defaults[] {defaults, defaults, defaults, defaults})));

    assertEquals(42, bounded_seq.getAlignmentCheck());
  }

  @Test
  public final void testUnboundedSequences() {
    rosidl_generator_java.msg.UnboundedSequences unbounded_seq = new rosidl_generator_java.msg.UnboundedSequences();

    // This value should not change and is asserted at end of test
    unbounded_seq.setAlignmentCheck(42);

    // Test setting/getting fixed length unbounded_seq of primitive types
    List boolList = Arrays.asList(true, false, true);
    unbounded_seq.setBoolValues(boolList);
    assertEquals(boolList, unbounded_seq.getBoolValues());
    List byteList = Arrays.asList((byte) 0, (byte) 1, (byte) 255);
    unbounded_seq.setByteValues(byteList);
    assertEquals(byteList, unbounded_seq.getByteValues());
    List charList = Arrays.asList(' ', 'a', 'Z');
    unbounded_seq.setCharValues(charList);
    assertEquals(charList, unbounded_seq.getCharValues());
    List float32List = Arrays.asList(0.0f, -1.125f, 1.125f);
    unbounded_seq.setFloat32Values(float32List);
    assertEquals(float32List, unbounded_seq.getFloat32Values());
    List float64List = Arrays.asList(0.0f, -3.1415, 3.1415);
    unbounded_seq.setFloat64Values(float64List);
    assertEquals(float64List, unbounded_seq.getFloat64Values());
    List int8List = Arrays.asList(0, -128, 127);
    unbounded_seq.setInt8Values(int8List);
    assertEquals(int8List, unbounded_seq.getInt8Values());
    List uint8List = Arrays.asList(0, 1, 255);
    unbounded_seq.setUint8Values(uint8List);
    assertEquals(uint8List, unbounded_seq.getUint8Values());
    List int16List = Arrays.asList(0, -32768, 32767);
    unbounded_seq.setInt16Values(int16List);
    assertEquals(int16List, unbounded_seq.getInt16Values());
    List uint16List = Arrays.asList(0, 1, 65535);
    unbounded_seq.setUint16Values(uint16List);
    assertEquals(uint16List, unbounded_seq.getUint16Values());
    List int32List = Arrays.asList(0, -2147483648, 2147483647);
    unbounded_seq.setInt32Values(int32List);
    assertEquals(int32List, unbounded_seq.getInt32Values());
    List uint32List = Arrays.asList(0, 1, 4294967295L);
    unbounded_seq.setUint32Values(uint32List);
    assertEquals(uint32List, unbounded_seq.getUint32Values());
    List int64List = Arrays.asList(0, -9223372036854775808L, 9223372036854775807L);
    unbounded_seq.setInt64Values(int64List);
    assertEquals(int64List, unbounded_seq.getInt64Values());
    List uint64List = Arrays.asList(0, 1, -1);
    unbounded_seq.setUint64Values(uint64List);
    assertEquals(uint64List, unbounded_seq.getUint64Values());

    // Test setting/getting fixed length unbounded_seq of strings
    List stringList = Arrays.asList("", "min value", "max_value");
    unbounded_seq.setStringValues(stringList);
    assertEquals(stringList, unbounded_seq.getStringValues());

    // Test setting/getting fixed length unbounded_seq of nested types
    rosidl_generator_java.msg.BasicTypes basicTypes = new rosidl_generator_java.msg.BasicTypes();
    List basicTypesList = Arrays.asList(
        new rosidl_generator_java.msg.BasicTypes[] {basicTypes, basicTypes, basicTypes});
    unbounded_seq.setBasicTypesValues(basicTypesList);
    assertEquals(basicTypesList, unbounded_seq.getBasicTypesValues());
    rosidl_generator_java.msg.Constants constants = new rosidl_generator_java.msg.Constants();
    List constantsList = Arrays.asList(
        new rosidl_generator_java.msg.Constants[] {constants, constants, constants});
    unbounded_seq.setConstantsValues(constantsList);
    assertEquals(constantsList, unbounded_seq.getConstantsValues());
    rosidl_generator_java.msg.Defaults defaults = new rosidl_generator_java.msg.Defaults();
    List defaultsList = Arrays.asList(
        new rosidl_generator_java.msg.Defaults[] {defaults, defaults, defaults});
    unbounded_seq.setDefaultsValues(defaultsList);
    assertEquals(defaultsList, unbounded_seq.getDefaultsValues());

    assertEquals(42, unbounded_seq.getAlignmentCheck());
  }

  @Test
  public final void testBasicTypesService() {
    rosidl_generator_java.srv.BasicTypes_Request basicTypesRequest =
      new rosidl_generator_java.srv.BasicTypes_Request();
    rosidl_generator_java.srv.BasicTypes_Response basicTypesResponse =
      new rosidl_generator_java.srv.BasicTypes_Response();
    // Set request fields
    boolean expectedBool1 = true;
    basicTypesRequest.setBoolValue(expectedBool1);
    byte expectedByte1 = 123;
    basicTypesRequest.setByteValue(expectedByte1);
    byte expectedChar1 = 'a';
    basicTypesRequest.setCharValue(expectedChar1);
    float expectedFloat1 = 12.34f;
    basicTypesRequest.setFloat32Value(expectedFloat1);
    double expectedDouble1 = 12.34;
    basicTypesRequest.setFloat64Value(expectedDouble1);
    byte expectedInt81 = 123;
    basicTypesRequest.setInt8Value(expectedInt81);
    short expectedInt161 = 1230;
    basicTypesRequest.setInt16Value(expectedInt161);
    int expectedInt321 = 123000;
    basicTypesRequest.setInt32Value(expectedInt321);
    long expectedInt641 = 42949672960L;
    basicTypesRequest.setInt64Value(expectedInt641);

    // Set response fields
    boolean expectedBool2 = false;
    basicTypesResponse.setBoolValue(expectedBool2);
    byte expectedByte2 = -42;
    basicTypesResponse.setByteValue(expectedByte2);
    byte expectedChar2 = ' ';
    basicTypesResponse.setCharValue(expectedChar2);
    float expectedFloat2 = -43.21f;
    basicTypesResponse.setFloat32Value(expectedFloat2);
    double expectedDouble2 = -43.21;
    basicTypesResponse.setFloat64Value(expectedDouble2);
    byte expectedInt82 = -42;
    basicTypesResponse.setInt8Value(expectedInt82);
    short expectedInt162 = -420;
    basicTypesResponse.setInt16Value(expectedInt162);
    int expectedInt322 = -42000;
    basicTypesResponse.setInt32Value(expectedInt322);
    long expectedInt642 = -4200000L;
    basicTypesResponse.setInt64Value(expectedInt642);

    // Get request fields
    assertEquals(expectedBool1, basicTypesRequest.getBoolValue());
    assertEquals(expectedByte1, basicTypesRequest.getByteValue());
    assertEquals(expectedChar1, basicTypesRequest.getCharValue());
    assertEquals(expectedFloat1, basicTypesRequest.getFloat32Value(), 0.01f);
    assertEquals(expectedDouble1, basicTypesRequest.getFloat64Value(), 0.01);
    assertEquals(expectedInt81, basicTypesRequest.getInt8Value());
    assertEquals(expectedInt161, basicTypesRequest.getInt16Value());
    assertEquals(expectedInt321, basicTypesRequest.getInt32Value());
    assertEquals(expectedInt641, basicTypesRequest.getInt64Value());

    // Get response fields
    assertEquals(expectedBool2, basicTypesResponse.getBoolValue());
    assertEquals(expectedByte2, basicTypesResponse.getByteValue());
    assertEquals(expectedChar2, basicTypesResponse.getCharValue());
    assertEquals(expectedFloat2, basicTypesResponse.getFloat32Value(), 0.01f);
    assertEquals(expectedDouble2, basicTypesResponse.getFloat64Value(), 0.01);
    assertEquals(expectedInt82, basicTypesResponse.getInt8Value());
    assertEquals(expectedInt162, basicTypesResponse.getInt16Value());
    assertEquals(expectedInt322, basicTypesResponse.getInt32Value());
    assertEquals(expectedInt642, basicTypesResponse.getInt64Value());
  }
}
