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

import static org.junit.Assert.assertArrayEquals;
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
  public class ListFixtureData {
    public boolean[] boolArr = new boolean[]{true, false, true};
    public List<Boolean> boolList = Arrays.asList(true, false, true);
    public boolean[] boolArrShort = new boolean[]{false};
    public List<Boolean> boolListShort = Arrays.asList(false);
    public byte[] byteArr = new byte[]{(byte) 0, (byte) 1, (byte) 255};
    public List<Byte> byteList = Arrays.asList((byte) 0, (byte) 1, (byte) 255);
    public byte[] byteArrShort = new byte[]{1};
    public List<Byte> byteListShort = Arrays.asList((byte) 1);
    public byte[] charArr = new byte[]{(byte) ' ', (byte) 'a', (byte) 'Z'};
    public List<Byte> charList = Arrays.asList(new Byte((byte) ' '), new Byte((byte) 'a'), new Byte((byte) 'Z'));
    public byte[] charArrShort = new byte[]{(byte) 'z', (byte) 'A'};
    public List<Byte> charListShort = Arrays.asList(new Byte((byte) 'z'), new Byte((byte) 'A'));
    public float[] float32Arr = new float[]{0.0f, -1.125f, 1.125f};
    public List<Float> float32List = Arrays.asList(0.0f, -1.125f, 1.125f);
    public float[] float32ArrShort = new float[]{1.125f, -1.125f};
    public List<Float> float32ListShort = Arrays.asList(1.125f, -1.125f);
    public double[] float64Arr = new double[]{0.0, -3.1415, 3.1415};
    public List<Double> float64List = Arrays.asList(0.0, -3.1415, 3.1415);
    public double[] float64ArrShort = new double[]{3.1415, -3.1415};
    public List<Double> float64ListShort = Arrays.asList(3.1415, -3.1415);
    public byte[] int8Arr = new byte[]{0, -128, 127};
    public List<Byte> int8List = Arrays.asList((byte) 0, (byte) -128, (byte) 127);
    public byte[] int8ArrShort = new byte[]{127, -128};
    public List<Byte> int8ListShort = Arrays.asList((byte) 127, (byte) -128);
    public byte[] uint8Arr = new byte[]{(byte) 0, (byte) 1, (byte) 255};
    public List<Byte> uint8List = Arrays.asList((byte) 0, (byte) 1, (byte) 255);
    public byte[] uint8ArrShort = new byte[]{(byte) 255, (byte) 1};
    public List uint8ListShort = Arrays.asList((byte) 255, (byte) 1);
    public short[] int16Arr = new short[]{0, -32768, 32767};
    public List<Short> int16List = Arrays.asList((short) 0, (short) -32768, (short) 32767);
    public short[] int16ArrShort = new short[]{32767, -32768};
    public List<Short> int16ListShort = Arrays.asList((short) 32767, (short) -32768);
    public short[] uint16Arr = new short[]{(short) 0, (short) 1, (short) 65535};
    public List<Short> uint16List = Arrays.asList((short) 0, (short) 1, (short) 65535);
    public short[] uint16ArrShort = new short[]{(short) 0, (short) 1, (short) 65535};
    public List<Short> uint16ListShort = Arrays.asList((short) 0, (short) 1, (short) 65535);
    public int[] int32Arr = new int[]{0, -2147483648, 2147483647};
    public List<Integer> int32List = Arrays.asList(0, -2147483648, 2147483647);
    public int[] int32ArrShort = new int[]{2147483647, -2147483648};
    public List<Integer> int32ListShort = Arrays.asList(2147483647, -2147483648);
    public int[] uint32Arr = new int[]{0, 1, (int) 4294967295L};
    public List<Integer> uint32List = Arrays.asList(0, 1, (int) 4294967295L);
    public int[] uint32ArrShort = new int[]{(int) 4294967295L, 1};
    public List<Integer> uint32ListShort = Arrays.asList((int) 4294967295L, 1);
    public long[] int64Arr = new long[]{0L, -9223372036854775808L, 9223372036854775807L};
    public List<Long> int64List = Arrays.asList(0L, -9223372036854775808L, 9223372036854775807L);
    public long[] int64ArrShort = new long[]{0L, -9223372036854775808L, 9223372036854775807L};
    public List<Long> int64ListShort = Arrays.asList(0L, -9223372036854775808L, 9223372036854775807L);
    public long[] uint64Arr = new long[]{0L, 1L, -1L};
    public List<Long> uint64List = Arrays.asList(0L, 1L, -1L);
    public long[] uint64ArrShort = new long[]{0L, 1L, -1L};
    public List<Long> uint64ListShort = Arrays.asList(0L, 1L, -1L);
    public String[] stringArr = new String[]{"", "min value", "max_value"};
    public List stringList = Arrays.asList(stringArr);
    public String[] stringArrShort = new String[]{"max_value", ""};
    public List stringListShort = Arrays.asList(stringArrShort);
    private rosidl_generator_java.msg.BasicTypes basicTypes = new rosidl_generator_java.msg.BasicTypes();
    public rosidl_generator_java.msg.BasicTypes[] basicTypesArr =
        new rosidl_generator_java.msg.BasicTypes[] {basicTypes, basicTypes, basicTypes};
    public List<rosidl_generator_java.msg.BasicTypes> basicTypesList = Arrays.asList(basicTypesArr);
    public rosidl_generator_java.msg.BasicTypes[] basicTypesArrShort =
        new rosidl_generator_java.msg.BasicTypes[] {basicTypes};
    public List<rosidl_generator_java.msg.BasicTypes> basicTypesListShort = Arrays.asList(basicTypesArrShort);
    public rosidl_generator_java.msg.BasicTypes[] basicTypesArrLong =
        new rosidl_generator_java.msg.BasicTypes[] {basicTypes, basicTypes, basicTypes, basicTypes};
    public List<rosidl_generator_java.msg.BasicTypes> basicTypesListLong = Arrays.asList(basicTypesArrLong);
    private rosidl_generator_java.msg.Constants constants = new rosidl_generator_java.msg.Constants();
    public rosidl_generator_java.msg.Constants[] constantsArr =
        new rosidl_generator_java.msg.Constants[] {constants, constants, constants};
    public List<rosidl_generator_java.msg.Constants> constantsList = Arrays.asList(constantsArr);
    public rosidl_generator_java.msg.Constants[] constantsArrShort =
        new rosidl_generator_java.msg.Constants[] {constants};
    public List<rosidl_generator_java.msg.Constants> constantsListShort = Arrays.asList(constantsArrShort);
    public rosidl_generator_java.msg.Constants[] constantsArrLong =
        new rosidl_generator_java.msg.Constants[] {constants, constants, constants, constants};
    public List<rosidl_generator_java.msg.Constants> constantsListLong = Arrays.asList(constantsArrLong);
    private rosidl_generator_java.msg.Defaults defaults = new rosidl_generator_java.msg.Defaults();
    public rosidl_generator_java.msg.Defaults[] defaultsArr =
        new rosidl_generator_java.msg.Defaults[] {defaults, defaults, defaults};
    public List<rosidl_generator_java.msg.Defaults> defaultsList = Arrays.asList(defaultsArr);
    public rosidl_generator_java.msg.Defaults[] defaultsArrShort =
        new rosidl_generator_java.msg.Defaults[] {defaults};
    public List<rosidl_generator_java.msg.Defaults> defaultsListShort = Arrays.asList(defaultsArrShort);
    public rosidl_generator_java.msg.Defaults[] defaultsArrLong =
        new rosidl_generator_java.msg.Defaults[] {defaults, defaults, defaults, defaults};
    public List<rosidl_generator_java.msg.Defaults> defaultsListLong = Arrays.asList(defaultsArrLong);
  }

  @BeforeClass
  public static void setupOnce() {
    try
    {
      // Configure log4j. Doing this dynamically so that Android does not complain about missing
      // the log4j JARs, SLF4J uses Android's native logging mechanism instead.
      Class c = Class.forName("org.apache.log4j.BasicConfigurator");
      Method m = c.getDeclaredMethod("configure", (Class<?>[]) null);
      Object o = m.invoke(null, (Object[]) null);
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
    ListFixtureData fixture = new ListFixtureData();

    // This value should not change and is asserted at end of test
    arrays.setAlignmentCheck(42);

    // Test setting/getting fixed length arrays of primitive types
    arrays.setBoolValues(fixture.boolList);
    assertArrayEquals(fixture.boolArr, arrays.getBoolValues());
    assertEquals(fixture.boolList, arrays.getBoolValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setBoolValues(new boolean[]{true, false, true, false}));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setBoolValues(Arrays.asList(true, false, true, false)));
    arrays.setByteValues(fixture.byteList);
    assertArrayEquals(fixture.byteArr, arrays.getByteValues());
    assertEquals(fixture.byteList, arrays.getByteValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setByteValues(new byte[]{(byte) 1, (byte) 2}));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setByteValues(Arrays.asList((byte) 1, (byte) 2)));
    arrays.setCharValues(fixture.charList);
    assertArrayEquals(fixture.charArr, arrays.getCharValues());
    assertEquals(fixture.charList, arrays.getCharValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setCharValues(new byte[]{(byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd'}));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setCharValues(Arrays.asList((byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd')));
    arrays.setFloat32Values(fixture.float32List);
    assertTrue(Arrays.equals(fixture.float32Arr, arrays.getFloat32Values()));
    assertEquals(fixture.float32List, arrays.getFloat32ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setFloat32Values(new float[]{1.0f, 2.0f}));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setFloat32Values(Arrays.asList(1.0f, 2.0f)));
    arrays.setFloat64Values(fixture.float64List);
    assertTrue(Arrays.equals(fixture.float64Arr, arrays.getFloat64Values()));
    assertEquals(fixture.float64List, arrays.getFloat64ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setFloat64Values(new double[]{1.0, 2.0, 3.0, 4.0}));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setFloat64Values(Arrays.asList(1.0, 2.0, 3.0, 4.0)));
    arrays.setInt8Values(fixture.int8List);
    assertArrayEquals(fixture.int8Arr, arrays.getInt8Values());
    assertEquals(fixture.int8List, arrays.getInt8ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () ->arrays.setInt8Values(new byte[]{(byte) 1, (byte) 2}));
    assertThrows(IllegalArgumentException.class,
      () ->arrays.setInt8Values(Arrays.asList((byte) 1, (byte) 2)));
    arrays.setUint8Values(fixture.uint8List);
    assertArrayEquals(fixture.uint8Arr, arrays.getUint8Values());
    assertEquals(fixture.uint8List, arrays.getUint8ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setUint8Values(new byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4}));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setUint8Values(Arrays.asList((byte) 1, (byte) 2, (byte) 3, (byte) 4)));
    arrays.setInt16Values(fixture.int16List);
    assertTrue(Arrays.equals(fixture.int16Arr, arrays.getInt16Values()));
    assertEquals(fixture.int16List, arrays.getInt16ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setInt16Values(new short[]{(short) 1, (short) 2}));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setInt16Values(Arrays.asList((short) 1, (short) 2)));
    arrays.setUint16Values(fixture.uint16List);
    assertTrue(Arrays.equals(fixture.uint16Arr, arrays.getUint16Values()));
    assertEquals(fixture.uint16List, arrays.getUint16ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setUint16Values(new short[]{(short) 1, (short) 2, (short) 3, (short) 4}));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setUint16Values(Arrays.asList((short) 1, (short) 2, (short) 3, (short) 4)));
    arrays.setInt32Values(fixture.int32List);
    assertArrayEquals(fixture.int32Arr, arrays.getInt32Values());
    assertEquals(fixture.int32List, arrays.getInt32ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setInt32Values(new int[]{1, 2}));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setInt32Values(Arrays.asList(1, 2)));
    arrays.setUint32Values(fixture.uint32List);
    assertArrayEquals(fixture.uint32Arr, arrays.getUint32Values());
    assertEquals(fixture.uint32List, arrays.getUint32ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setUint32Values(new int[]{1, 2, 3, 4}));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setUint32Values(Arrays.asList(1, 2, 3, 4)));
    arrays.setInt64Values(fixture.int64List);
    assertArrayEquals(fixture.int64Arr, arrays.getInt64Values());
    assertEquals(fixture.int64List, arrays.getInt64ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setInt64Values(new long[]{1L, 2L}));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setInt64Values(Arrays.asList(1L, 2L)));
    arrays.setUint64Values(fixture.uint64List);
    assertArrayEquals(fixture.uint64Arr, arrays.getUint64Values());
    assertEquals(fixture.uint64List, arrays.getUint64ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () ->arrays.setUint64Values(new long[]{1L, 2L, 3L, 4L}));
    assertThrows(IllegalArgumentException.class,
      () ->arrays.setUint64Values(Arrays.asList(1L, 2L, 3L, 4L)));

    // Test setting/getting fixed length arrays of strings
    arrays.setStringValues(fixture.stringList);
    assertArrayEquals(fixture.stringArr, arrays.getStringValues());
    assertEquals(fixture.stringList, arrays.getStringValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setStringValues(new String[]{"too", "few"}));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setStringValues(Arrays.asList("too", "few")));

    // Test setting/getting fixed length arrays of nested types
    arrays.setBasicTypesValues(fixture.basicTypesList);
    assertArrayEquals(fixture.basicTypesArr, arrays.getBasicTypesValues());
    assertEquals(fixture.basicTypesList, arrays.getBasicTypesValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setBasicTypesValues(fixture.basicTypesArrShort));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setBasicTypesValues(fixture.basicTypesListShort));
    arrays.setConstantsValues(fixture.constantsList);
    assertArrayEquals(fixture.constantsArr, arrays.getConstantsValues());
    assertEquals(fixture.constantsList, arrays.getConstantsValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setConstantsValues(fixture.constantsArrShort));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setConstantsValues(fixture.constantsListShort));
    arrays.setDefaultsValues(fixture.defaultsList);
    assertArrayEquals(fixture.defaultsArr, arrays.getDefaultsValues());
    assertEquals(fixture.defaultsList, arrays.getDefaultsValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setDefaultsValues(fixture.defaultsArrShort));
    assertThrows(IllegalArgumentException.class,
      () -> arrays.setDefaultsValues(fixture.defaultsListShort));

    assertEquals(42, arrays.getAlignmentCheck());
  }

  @Test
  public final void testBoundedSequences() {
    rosidl_generator_java.msg.BoundedSequences bounded_seq = new rosidl_generator_java.msg.BoundedSequences();
    ListFixtureData fixture = new ListFixtureData();

    // This value should not change and is asserted at end of test
    bounded_seq.setAlignmentCheck(42);

    // Test setting/getting fixed length bounded_seq of primitive types
    bounded_seq.setBoolValues(fixture.boolList);
    assertArrayEquals(fixture.boolArr, bounded_seq.getBoolValues());
    assertEquals(fixture.boolList, bounded_seq.getBoolValuesAsList());
    bounded_seq.setBoolValues(fixture.boolListShort);
    assertArrayEquals(fixture.boolArrShort, bounded_seq.getBoolValues());
    assertEquals(fixture.boolListShort, bounded_seq.getBoolValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setBoolValues(new boolean[]{true, false, true, false}));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setBoolValues(Arrays.asList(true, false, true, false)));
    bounded_seq.setByteValues(fixture.byteList);
    assertArrayEquals(fixture.byteArr, bounded_seq.getByteValues());
    assertEquals(fixture.byteList, bounded_seq.getByteValuesAsList());
    bounded_seq.setByteValues(fixture.byteListShort);
    assertArrayEquals(fixture.byteArrShort, bounded_seq.getByteValues());
    assertEquals(fixture.byteListShort, bounded_seq.getByteValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setByteValues(new byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4}));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setByteValues(Arrays.asList((byte) 1, (byte) 2, (byte) 3, (byte) 4)));
    bounded_seq.setCharValues(fixture.charList);
    assertArrayEquals(fixture.charArr, bounded_seq.getCharValues());
    assertEquals(fixture.charList, bounded_seq.getCharValuesAsList());
    bounded_seq.setCharValues(fixture.charListShort);
    assertArrayEquals(fixture.charArrShort, bounded_seq.getCharValues());
    assertEquals(fixture.charListShort, bounded_seq.getCharValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setCharValues(new byte[]{(byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd'}));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setCharValues(Arrays.asList((byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd')));
    bounded_seq.setFloat32Values(fixture.float32List);
    assertTrue(Arrays.equals(fixture.float32Arr, bounded_seq.getFloat32Values()));
    assertEquals(fixture.float32List, bounded_seq.getFloat32ValuesAsList());
    bounded_seq.setFloat32Values(fixture.float32ListShort);
    assertTrue(Arrays.equals(fixture.float32ArrShort, bounded_seq.getFloat32Values()));
    assertEquals(fixture.float32ListShort, bounded_seq.getFloat32ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setFloat32Values(new float[]{1.0f, 2.0f, 3.0f, 4.0f}));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setFloat32Values(Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f)));
    bounded_seq.setFloat64Values(fixture.float64List);
    assertTrue(Arrays.equals(fixture.float64Arr, bounded_seq.getFloat64Values()));
    assertEquals(fixture.float64List, bounded_seq.getFloat64ValuesAsList());
    bounded_seq.setFloat64Values(fixture.float64ListShort);
    assertTrue(Arrays.equals(fixture.float64ArrShort, bounded_seq.getFloat64Values()));
    assertEquals(fixture.float64ListShort, bounded_seq.getFloat64ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setFloat64Values(Arrays.asList(1.0, 2.0, 3.0, 4.0)));
    bounded_seq.setInt8Values(fixture.int8List);
    assertArrayEquals(fixture.int8Arr, bounded_seq.getInt8Values());
    assertEquals(fixture.int8List, bounded_seq.getInt8ValuesAsList());
    bounded_seq.setInt8Values(fixture.int8ListShort);
    assertArrayEquals(fixture.int8ArrShort, bounded_seq.getInt8Values());
    assertEquals(fixture.int8ListShort, bounded_seq.getInt8ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setInt8Values(new byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4}));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setInt8Values(Arrays.asList((byte) 1, (byte) 2, (byte) 3, (byte) 4)));
    bounded_seq.setUint8Values(fixture.uint8List);
    assertArrayEquals(fixture.uint8Arr, bounded_seq.getUint8Values());
    assertEquals(fixture.uint8List, bounded_seq.getUint8ValuesAsList());
    bounded_seq.setUint8Values(fixture.uint8ListShort);
    assertArrayEquals(fixture.uint8ArrShort, bounded_seq.getUint8Values());
    assertEquals(fixture.uint8ListShort, bounded_seq.getUint8ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setUint8Values(new byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4}));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setUint8Values(Arrays.asList((byte) 1, (byte) 2, (byte) 3, (byte) 4)));
    bounded_seq.setInt16Values(fixture.int16List);
    assertTrue(Arrays.equals(fixture.int16Arr, bounded_seq.getInt16Values()));
    assertEquals(fixture.int16List, bounded_seq.getInt16ValuesAsList());
    bounded_seq.setInt16Values(fixture.int16ListShort);
    assertTrue(Arrays.equals(fixture.int16ArrShort, bounded_seq.getInt16Values()));
    assertEquals(fixture.int16ListShort, bounded_seq.getInt16ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setInt16Values(new short[]{(short) 1, (short) 2, (short) 3, (short) 4}));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setInt16Values(Arrays.asList((short) 1, (short) 2, (short) 3, (short) 4)));
    bounded_seq.setUint16Values(fixture.uint16List);
    assertTrue(Arrays.equals(fixture.uint16Arr, bounded_seq.getUint16Values()));
    assertEquals(fixture.uint16List, bounded_seq.getUint16ValuesAsList());
    bounded_seq.setUint16Values(fixture.uint16ListShort);
    assertTrue(Arrays.equals(fixture.uint16ArrShort, bounded_seq.getUint16Values()));
    assertEquals(fixture.uint16ListShort, bounded_seq.getUint16ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setUint16Values(new short[]{(short) 1, (short) 2, (short) 3, (short) 4}));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setUint16Values(Arrays.asList((short) 1, (short) 2, (short) 3, (short) 4)));
    bounded_seq.setInt32Values(fixture.int32List);
    assertArrayEquals(fixture.int32Arr, bounded_seq.getInt32Values());
    assertEquals(fixture.int32List, bounded_seq.getInt32ValuesAsList());
    bounded_seq.setInt32Values(fixture.int32ListShort);
    assertArrayEquals(fixture.int32ArrShort, bounded_seq.getInt32Values());
    assertEquals(fixture.int32ListShort, bounded_seq.getInt32ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setInt32Values(new int[]{1, 2, 3, 4}));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setInt32Values(Arrays.asList(1, 2, 3, 4)));
    bounded_seq.setUint32Values(fixture.uint32List);
    assertArrayEquals(fixture.uint32Arr, bounded_seq.getUint32Values());
    assertEquals(fixture.uint32List, bounded_seq.getUint32ValuesAsList());
    bounded_seq.setUint32Values(fixture.uint32ListShort);
    assertArrayEquals(fixture.uint32ArrShort, bounded_seq.getUint32Values());
    assertEquals(fixture.uint32ListShort, bounded_seq.getUint32ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setUint32Values(new int[]{1, 2, 3, 4}));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setUint32Values(Arrays.asList(1, 2, 3, 4)));
    bounded_seq.setInt64Values(fixture.int64List);
    assertArrayEquals(fixture.int64Arr, bounded_seq.getInt64Values());
    assertEquals(fixture.int64List, bounded_seq.getInt64ValuesAsList());
    bounded_seq.setInt64Values(fixture.int64ListShort);
    assertArrayEquals(fixture.int64ArrShort, bounded_seq.getInt64Values());
    assertEquals(fixture.int64ListShort, bounded_seq.getInt64ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setInt64Values(new long[]{1L, 2L, 3L, 4L}));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setInt64Values(Arrays.asList(1L, 2L, 3L, 4L)));
    bounded_seq.setUint64Values(fixture.uint64List);
    assertArrayEquals(fixture.uint64Arr, bounded_seq.getUint64Values());
    assertEquals(fixture.uint64List, bounded_seq.getUint64ValuesAsList());
    bounded_seq.setUint64Values(fixture.uint64ListShort);
    assertArrayEquals(fixture.uint64ArrShort, bounded_seq.getUint64Values());
    assertEquals(fixture.uint64ListShort, bounded_seq.getUint64ValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setUint64Values(new long[]{1L, 2L, 3L, 4L}));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setUint64Values(Arrays.asList(1L, 2L, 3L, 4L)));

    // Test setting/getting fixed length bounded_seq of strings
    bounded_seq.setStringValues(fixture.stringList);
    assertArrayEquals(fixture.stringArr, bounded_seq.getStringValues());
    assertEquals(fixture.stringList, bounded_seq.getStringValuesAsList());
    bounded_seq.setStringValues(fixture.stringListShort);
    assertArrayEquals(fixture.stringArrShort, bounded_seq.getStringValues());
    assertEquals(fixture.stringListShort, bounded_seq.getStringValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setStringValues(new String[]{"too", "many", "values", "!"}));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setStringValues(Arrays.asList("too", "many", "values", "!")));

    // Test setting/getting fixed length bounded_seq of nested types
    bounded_seq.setBasicTypesValues(fixture.basicTypesList);
    assertArrayEquals(fixture.basicTypesArr, bounded_seq.getBasicTypesValues());
    assertEquals(fixture.basicTypesList, bounded_seq.getBasicTypesValuesAsList());
    bounded_seq.setBasicTypesValues(fixture.basicTypesListShort);
    assertArrayEquals(fixture.basicTypesArrShort, bounded_seq.getBasicTypesValues());
    assertEquals(fixture.basicTypesListShort, bounded_seq.getBasicTypesValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setBasicTypesValues(fixture.basicTypesArrLong));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setBasicTypesValues(fixture.basicTypesListLong));
    bounded_seq.setConstantsValues(fixture.constantsList);
    assertArrayEquals(fixture.constantsArr, bounded_seq.getConstantsValues());
    assertEquals(fixture.constantsList, bounded_seq.getConstantsValuesAsList());
    bounded_seq.setConstantsValues(fixture.constantsListShort);
    assertArrayEquals(fixture.constantsArrShort, bounded_seq.getConstantsValues());
    assertEquals(fixture.constantsListShort, bounded_seq.getConstantsValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setConstantsValues(fixture.constantsArrLong));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setConstantsValues(fixture.constantsListLong));
    bounded_seq.setDefaultsValues(fixture.defaultsList);
    assertArrayEquals(fixture.defaultsArr, bounded_seq.getDefaultsValues());
    assertEquals(fixture.defaultsList, bounded_seq.getDefaultsValuesAsList());
    bounded_seq.setDefaultsValues(fixture.defaultsListShort);
    assertArrayEquals(fixture.defaultsArrShort, bounded_seq.getDefaultsValues());
    assertEquals(fixture.defaultsListShort, bounded_seq.getDefaultsValuesAsList());
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setDefaultsValues(fixture.defaultsArrLong));
    assertThrows(IllegalArgumentException.class,
      () -> bounded_seq.setDefaultsValues(fixture.defaultsListLong));

    assertEquals(42, bounded_seq.getAlignmentCheck());
  }

  @Test
  public final void testUnboundedSequences() {
    rosidl_generator_java.msg.UnboundedSequences unbounded_seq = new rosidl_generator_java.msg.UnboundedSequences();
    ListFixtureData fixture = new ListFixtureData();

    // This value should not change and is asserted at end of test
    unbounded_seq.setAlignmentCheck(42);

    // Test setting/getting fixed length unbounded_seq of primitive types
    unbounded_seq.setBoolValues(fixture.boolList);
    assertArrayEquals(fixture.boolArr, unbounded_seq.getBoolValues());
    assertEquals(fixture.boolList, unbounded_seq.getBoolValuesAsList());
    unbounded_seq.setByteValues(fixture.byteList);
    assertArrayEquals(fixture.byteArr, unbounded_seq.getByteValues());
    assertEquals(fixture.byteList, unbounded_seq.getByteValuesAsList());
    unbounded_seq.setCharValues(fixture.charList);
    assertArrayEquals(fixture.charArr, unbounded_seq.getCharValues());
    assertEquals(fixture.charList, unbounded_seq.getCharValuesAsList());
    unbounded_seq.setFloat32Values(fixture.float32List);
    assertTrue(Arrays.equals(fixture.float32Arr, unbounded_seq.getFloat32Values()));
    assertEquals(fixture.float32List, unbounded_seq.getFloat32ValuesAsList());
    unbounded_seq.setFloat64Values(fixture.float64List);
    assertTrue(Arrays.equals(fixture.float64Arr, unbounded_seq.getFloat64Values()));
    assertEquals(fixture.float64List, unbounded_seq.getFloat64ValuesAsList());
    unbounded_seq.setInt8Values(fixture.int8List);
    assertArrayEquals(fixture.int8Arr, unbounded_seq.getInt8Values());
    assertEquals(fixture.int8List, unbounded_seq.getInt8ValuesAsList());
    unbounded_seq.setUint8Values(fixture.uint8List);
    assertArrayEquals(fixture.uint8Arr, unbounded_seq.getUint8Values());
    assertEquals(fixture.uint8List, unbounded_seq.getUint8ValuesAsList());
    unbounded_seq.setInt16Values(fixture.int16List);
    assertTrue(Arrays.equals(fixture.int16Arr, unbounded_seq.getInt16Values()));
    assertEquals(fixture.int16List, unbounded_seq.getInt16ValuesAsList());
    unbounded_seq.setUint16Values(fixture.uint16List);
    assertTrue(Arrays.equals(fixture.uint16Arr, unbounded_seq.getUint16Values()));
    assertEquals(fixture.uint16List, unbounded_seq.getUint16ValuesAsList());
    unbounded_seq.setInt32Values(fixture.int32List);
    assertArrayEquals(fixture.int32Arr, unbounded_seq.getInt32Values());
    assertEquals(fixture.int32List, unbounded_seq.getInt32ValuesAsList());
    unbounded_seq.setUint32Values(fixture.uint32List);
    assertArrayEquals(fixture.uint32Arr, unbounded_seq.getUint32Values());
    assertEquals(fixture.uint32List, unbounded_seq.getUint32ValuesAsList());
    unbounded_seq.setInt64Values(fixture.int64List);
    assertArrayEquals(fixture.int64Arr, unbounded_seq.getInt64Values());
    assertEquals(fixture.int64List, unbounded_seq.getInt64ValuesAsList());
    unbounded_seq.setUint64Values(fixture.uint64List);
    assertArrayEquals(fixture.uint64Arr, unbounded_seq.getUint64Values());
    assertEquals(fixture.uint64List, unbounded_seq.getUint64ValuesAsList());

    // Test setting/getting fixed length unbounded_seq of strings
    unbounded_seq.setStringValues(fixture.stringList);
    assertArrayEquals(fixture.stringArr, unbounded_seq.getStringValues());
    assertEquals(fixture.stringList, unbounded_seq.getStringValuesAsList());

    // Test setting/getting fixed length unbounded_seq of nested types
    unbounded_seq.setBasicTypesValues(fixture.basicTypesList);
    assertArrayEquals(fixture.basicTypesArr, unbounded_seq.getBasicTypesValues());
    assertEquals(fixture.basicTypesList, unbounded_seq.getBasicTypesValuesAsList());
    unbounded_seq.setConstantsValues(fixture.constantsList);
    assertArrayEquals(fixture.constantsArr, unbounded_seq.getConstantsValues());
    assertEquals(fixture.constantsList, unbounded_seq.getConstantsValuesAsList());
    unbounded_seq.setDefaultsValues(fixture.defaultsList);
    assertArrayEquals(fixture.defaultsArr, unbounded_seq.getDefaultsValues());
    assertEquals(fixture.defaultsList, unbounded_seq.getDefaultsValuesAsList());

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
