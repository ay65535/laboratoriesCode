/**
 * Copyright 2009 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.utility;

import java.io.DataInput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * Utility class that handles byte arrays, conversions to/from other types,
 * comparisons, hash code generation, manufacturing keys for HashMaps or
 * HashSets, etc.
 */
public class Bytes {
	  static final String UTF8_ENCODING = "UTF-8";


  /**
   * Size of boolean in bytes
   */
  public static final int SIZEOF_BOOLEAN = Byte.SIZE/Byte.SIZE;

  /**
   * Size of byte in bytes
   */
  public static final int SIZEOF_BYTE = SIZEOF_BOOLEAN;

  /**
   * Size of char in bytes
   */
  public static final int SIZEOF_CHAR = Character.SIZE/Byte.SIZE;

  /**
   * Size of double in bytes
   */
  public static final int SIZEOF_DOUBLE = Double.SIZE/Byte.SIZE;

  /**
   * Size of float in bytes
   */
  public static final int SIZEOF_FLOAT = Float.SIZE/Byte.SIZE;

  /**
   * Size of int in bytes
   */
  public static final int SIZEOF_INT = Integer.SIZE/Byte.SIZE;

  /**
   * Size of long in bytes
   */
  public static final int SIZEOF_LONG = Long.SIZE/Byte.SIZE;

  /**
   * Size of short in bytes
   */
  public static final int SIZEOF_SHORT = Short.SIZE/Byte.SIZE;


  /**
   * Estimate of size cost to pay beyond payload in jvm for instance of byte [].
   * Estimate based on study of jhat and jprofiler numbers.
   */
  // JHat says BU is 56 bytes.
  // SizeOf which uses java.lang.instrument says 24 bytes. (3 longs?)
  public static final int ESTIMATED_HEAP_TAX = 16;

  /**
   * Read byte-array from data input.
   * logic,
   * @param in Input to read from.
   * @return byte array read off <code>in</code>
   * @throws IOException io error
   */
  public static byte[] readByteArray(DataInput in, int len) throws IOException {
    if (len < 0) {
      throw new NegativeArraySizeException(Integer.toString(len));
    }
    byte [] result = new byte[len];
    in.readFully(result, 0, len);
    return result;
  }


  /**
   * Put bytes at the specified byte array position.
   * @param tgtBytes the byte array
   * @param tgtOffset position in the array
   * @param srcBytes byte to write out
   * @param srcOffset
   * @param srcLength
   * @return incremented offset
   */
  public static int putBytes(byte[] tgtBytes, int tgtOffset, byte[] srcBytes,
      int srcOffset, int srcLength) {
    System.arraycopy(srcBytes, srcOffset, tgtBytes, tgtOffset, srcLength);
    return tgtOffset + srcLength;
  }

  /**
   * Write a single byte out to the specified byte array position.
   * @param bytes the byte array
   * @param offset position in the array
   * @param b byte to write out
   * @return incremented offset
   */
  public static int putByte(byte[] bytes, int offset, byte b) {
    bytes[offset] = b;
    return offset + 1;
  }

  /**
   * Returns a new byte array, copied from the passed ByteBuffer.
   * @param bb A ByteBuffer
   * @return the byte array
   */
  public static byte[] toBytes(ByteBuffer bb) {
    int length = bb.limit();
    byte [] result = new byte[length];
    System.arraycopy(bb.array(), bb.arrayOffset(), result, 0, length);
    return result;
  }



  public static String toStringBinary(final byte [] b) {
    return toStringBinary(b, 0, b.length);
  }

  public static String toStringBinary(final byte [] b, int off, int len) {
    StringBuilder result = new StringBuilder();
    try {
      String first = new String(b, off, len, "ISO-8859-1");
      for (int i = 0; i < first.length() ; ++i ) {
        int ch = first.charAt(i) & 0xFF;
        if ( (ch >= '0' && ch <= '9')
            || (ch >= 'A' && ch <= 'Z')
            || (ch >= 'a' && ch <= 'z')
            || ch == ','
            || ch == '_'
            || ch == '-'
            || ch == ':'
            || ch == ' '
            || ch == '<'
            || ch == '>'
            || ch == '='
            || ch == '/'
            || ch == '.') {
          result.append(first.charAt(i));
        } else {
          result.append(String.format("\\x%02X", ch));
        }
      }
    } catch ( UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return result.toString();
  }

  private static boolean isHexDigit(char c) {
    return
        (c >= 'A' && c <= 'F') ||
        (c >= '0' && c <= '9');
  }

  /**
   * Takes a ASCII digit in the range A-F0-9 and returns
   * the corresponding integer/ordinal value.
   * @param ch  The hex digit.
   * @return The converted hex value as a byte.
   */
  public static byte toBinaryFromHex(byte ch) {
    if ( ch >= 'A' && ch <= 'F' )
      return (byte) ((byte)10 + (byte) (ch - 'A'));
    // else
    return (byte) (ch - '0');
  }

  public static byte [] toBytesBinary(String in) {
    // this may be bigger than we need, but lets be safe.
    byte [] b = new byte[in.length()];
    int size = 0;
    for (int i = 0; i < in.length(); ++i) {
      char ch = in.charAt(i);
      if (ch == '\\') {
        // begin hex escape:
        char next = in.charAt(i+1);
        if (next != 'x') {
          // invalid escape sequence, ignore this one.
          b[size++] = (byte)ch;
          continue;
        }
        // ok, take next 2 hex digits.
        char hd1 = in.charAt(i+2);
        char hd2 = in.charAt(i+3);

        // they need to be A-F0-9:
        if ( ! isHexDigit(hd1) ||
            ! isHexDigit(hd2) ) {
          // bogus escape code, ignore:
          continue;
        }
        // turn hex ASCII digit -> number
        byte d = (byte) ((toBinaryFromHex((byte)hd1) << 4) + toBinaryFromHex((byte)hd2));

        b[size++] = d;
        i += 3; // skip 3
      } else {
        b[size++] = (byte) ch;
      }
    }
    // resize:
    byte [] b2 = new byte[size];
    System.arraycopy(b, 0, b2, 0, size);
    return b2;
  }

  /**
   * Converts a string to a UTF-8 byte array.
   * @param s
   * @return the byte array
   */
  public static byte[] toBytes(String s) {
    if (s == null) {
      throw new IllegalArgumentException("string cannot be null");
    }
    byte [] result = null;
    try {
      result = s.getBytes(UTF8_ENCODING);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Convert a boolean to a byte array.
   * @param b
   * @return <code>b</code> encoded in a byte array.
   */
  public static byte [] toBytes(final boolean b) {
    byte [] bb = new byte[1];
    bb[0] = b? (byte)-1: (byte)0;
    return bb;
  }

  /**
   * @param b
   * @return True or false.
   */
  public static boolean toBoolean(final byte [] b) {
    if (b == null || b.length > 1) {
      throw new IllegalArgumentException("Array is wrong size");
    }
    return b[0] != (byte)0;
  }

  /**
   * Convert a long value to a byte array
   * @param val
   * @return the byte array
   */
  public static byte[] toBytes(long val) {
    byte [] b = new byte[8];
    for(int i=7;i>0;i--) {
      b[i] = (byte)(val);
      val >>>= 8;
    }
    b[0] = (byte)(val);
    return b;
  }

  /**
   * Converts a byte array to a long value
   * @param bytes
   * @return the long value
   */
  public static long toLong(byte[] bytes) {
    return toLong(bytes, 0);
  }

  /**
   * Converts a byte array to a long value
   * @param bytes
   * @param offset
   * @return the long value
   */
  public static long toLong(byte[] bytes, int offset) {
    return toLong(bytes, offset, SIZEOF_LONG);
  }

  /**
   * Converts a byte array to a long value
   * @param bytes
   * @param offset
   * @param length
   * @return the long value
   */
  public static long toLong(byte[] bytes, int offset, final int length) {
    if (bytes == null || length != SIZEOF_LONG ||
        (offset + length > bytes.length)) {
      return -1L;
    }
    long l = 0;
    for(int i = offset; i < (offset + length); i++) {
      l <<= 8;
      l ^= (long)bytes[i] & 0xFF;
    }
    return l;
  }

  /**
   * Put a long value out to the specified byte array position.
   * @param bytes the byte array
   * @param offset position in the array
   * @param val long to write out
   * @return incremented offset
   */
  public static int putLong(byte[] bytes, int offset, long val) {
    if (bytes == null || (bytes.length - offset < SIZEOF_LONG)) {
      return offset;
    }
    for(int i=offset+7;i>offset;i--) {
      bytes[i] = (byte)(val);
      val >>>= 8;
    }
    bytes[offset] = (byte)(val);
    return offset + SIZEOF_LONG;
  }

  /**
   * Presumes float encoded as IEEE 754 floating-point "single format"
   * @param bytes
   * @return Float made from passed byte array.
   */
  public static float toFloat(byte [] bytes) {
    return toFloat(bytes, 0);
  }

  /**
   * Presumes float encoded as IEEE 754 floating-point "single format"
   * @param bytes
   * @param offset
   * @return Float made from passed byte array.
   */
  public static float toFloat(byte [] bytes, int offset) {
    int i = toInt(bytes, offset);
    return Float.intBitsToFloat(i);
  }

  /**
   * @param bytes
   * @param offset
   * @param f
   * @return New offset in <code>bytes</bytes>
   */
  public static int putFloat(byte [] bytes, int offset, float f) {
    int i = Float.floatToRawIntBits(f);
    return putInt(bytes, offset, i);
  }

  /**
   * @param f
   * @return the float represented as byte []
   */
  public static byte [] toBytes(final float f) {
    // Encode it as int
    int i = Float.floatToRawIntBits(f);
    return Bytes.toBytes(i);
  }

  /**
   * @param bytes
   * @return Return double made from passed bytes.
   */
  public static double toDouble(final byte [] bytes) {
    return toDouble(bytes, 0);
  }

  /**
   * @param bytes
   * @param offset
   * @return Return double made from passed bytes.
   */
  public static double toDouble(final byte [] bytes, final int offset) {
    long l = toLong(bytes, offset);
    return Double.longBitsToDouble(l);
  }

  /**
   * @param bytes
   * @param offset
   * @param d
   * @return New offset into array <code>bytes</code>
   */
  public static int putDouble(byte [] bytes, int offset, double d) {
    long l = Double.doubleToLongBits(d);
    return putLong(bytes, offset, l);
  }

  /**
   * @param d
   * @return the double represented as byte []
   */
  public static byte [] toBytes(final double d) {
    // Encode it as a long
    long l = Double.doubleToRawLongBits(d);
    return Bytes.toBytes(l);
  }

  /**
   * Convert an int value to a byte array
   * @param val
   * @return the byte array
   */
  public static byte[] toBytes(int val) {
    byte [] b = new byte[4];
    for(int i = 3; i > 0; i--) {
      b[i] = (byte)(val);
      val >>>= 8;
    }
    b[0] = (byte)(val);
    return b;
  }

  /**
   * Converts a byte array to an int value
   * @param bytes
   * @return the int value
   */
  public static int toInt(byte[] bytes) {
    return toInt(bytes, 0);
  }

  /**
   * Converts a byte array to an int value
   * @param bytes
   * @param offset
   * @return the int value
   */
  public static int toInt(byte[] bytes, int offset) {
    return toInt(bytes, offset, SIZEOF_INT);
  }

  /**
   * Converts a byte array to an int value
   * @param bytes
   * @param offset
   * @param length
   * @return the int value
   */
  public static int toInt(byte[] bytes, int offset, final int length) {
    if (bytes == null || length != SIZEOF_INT ||
        (offset + length > bytes.length)) {
      return -1;
    }
    int n = 0;
    for(int i = offset; i < (offset + length); i++) {
      n <<= 8;
      n ^= bytes[i] & 0xFF;
    }
    return n;
  }

  /**
   * Put an int value out to the specified byte array position.
   * @param bytes the byte array
   * @param offset position in the array
   * @param val int to write out
   * @return incremented offset
   */
  public static int putInt(byte[] bytes, int offset, int val) {
    if (bytes == null || (bytes.length - offset < SIZEOF_INT)) {
      return offset;
    }
    for(int i= offset+3; i > offset; i--) {
      bytes[i] = (byte)(val);
      val >>>= 8;
    }
    bytes[offset] = (byte)(val);
    return offset + SIZEOF_INT;
  }

  /**
   * Convert a short value to a byte array
   * @param val
   * @return the byte array
   */
  public static byte[] toBytes(short val) {
    byte[] b = new byte[SIZEOF_SHORT];
    b[1] = (byte)(val);
    val >>= 8;
    b[0] = (byte)(val);
    return b;
  }

  /**
   * Converts a byte array to a short value
   * @param bytes
   * @return the short value
   */
  public static short toShort(byte[] bytes) {
    return toShort(bytes, 0);
  }

  /**
   * Converts a byte array to a short value
   * @param bytes
   * @param offset
   * @return the short value
   */
  public static short toShort(byte[] bytes, int offset) {
    return toShort(bytes, offset, SIZEOF_SHORT);
  }

  /**
   * Converts a byte array to a short value
   * @param bytes
   * @param offset
   * @param length
   * @return the short value
   */
  public static short toShort(byte[] bytes, int offset, final int length) {
    if (bytes == null || length != SIZEOF_SHORT ||
        (offset + length > bytes.length)) {
      return -1;
    }
    short n = 0;
    n ^= bytes[offset] & 0xFF;
    n <<= 8;
    n ^= bytes[offset+1] & 0xFF;
    return n;
  }

  /**
   * Put a short value out to the specified byte array position.
   * @param bytes the byte array
   * @param offset position in the array
   * @param val short to write out
   * @return incremented offset
   */
  public static int putShort(byte[] bytes, int offset, short val) {
    if (bytes == null || (bytes.length - offset < SIZEOF_SHORT)) {
      return offset;
    }
    bytes[offset+1] = (byte)(val);
    val >>= 8;
    bytes[offset] = (byte)(val);
    return offset + SIZEOF_SHORT;
  }

  /**
   * Convert a char value to a byte array
   *
   * @param val
   * @return the byte array
   */
  public static byte[] toBytes(char val) {
    byte[] b = new byte[SIZEOF_CHAR];
    b[1] = (byte) (val);
    val >>= 8;
    b[0] = (byte) (val);
    return b;
  }

  /**
   * Converts a byte array to a char value
   *
   * @param bytes
   * @return the char value
   */
  public static char toChar(byte[] bytes) {
    return toChar(bytes, 0);
  }


  /**
   * Converts a byte array to a char value
   *
   * @param bytes
   * @param offset
   * @return the char value
   */
  public static char toChar(byte[] bytes, int offset) {
    return toChar(bytes, offset, SIZEOF_CHAR);
  }

  /**
   * Converts a byte array to a char value
   *
   * @param bytes
   * @param offset
   * @param length
   * @return the char value
   */
  public static char toChar(byte[] bytes, int offset, final int length) {
    if (bytes == null || length != SIZEOF_CHAR ||
      (offset + length > bytes.length)) {
      return (char)-1;
    }
    char n = 0;
    n ^= bytes[offset] & 0xFF;
    n <<= 8;
    n ^= bytes[offset + 1] & 0xFF;
    return n;
  }

  /**
   * Put a char value out to the specified byte array position.
   *
   * @param bytes  the byte array
   * @param offset position in the array
   * @param val    short to write out
   * @return incremented offset
   */
  public static int putChar(byte[] bytes, int offset, char val) {
    if (bytes == null || (bytes.length - offset < SIZEOF_CHAR)) {
      return offset;
    }
    bytes[offset + 1] = (byte) (val);
    val >>= 8;
    bytes[offset] = (byte) (val);
    return offset + SIZEOF_CHAR;
  }

  /**
   * Convert a char array value to a byte array
   *
   * @param val
   * @return the byte array
   */
  public static byte[] toBytes(char[] val) {
    byte[] bytes = new byte[val.length * 2];
    putChars(bytes,0,val);
    return bytes;
  }

  /**
   * Converts a byte array to a char array value
   *
   * @param bytes
   * @return the char value
   */
  public static char[] toChars(byte[] bytes) {
    return toChars(bytes, 0, bytes.length);
  }


  /**
   * Converts a byte array to a char array value
   *
   * @param bytes
   * @param offset
   * @return the char value
   */
  public static char[] toChars(byte[] bytes, int offset) {
    return toChars(bytes, offset, bytes.length-offset);
  }

  /**
   * Converts a byte array to a char array value
   *
   * @param bytes
   * @param offset
   * @param length
   * @return the char value
   */
  public static char[] toChars(byte[] bytes, int offset, final int length) {
    int max = offset + length;
    if (bytes == null || (max > bytes.length) || length %2 ==1) {
      return null;
    }

    char[] chars = new char[length / 2];
    for (int i = 0, j = offset; i < chars.length && j < max; i++, j += 2) {
      char c = 0;
      c ^= bytes[j] & 0xFF;
      c <<= 8;
      c ^= bytes[j + 1] & 0xFF;
      chars[i] = c;
    }
    return chars;
  }

  /**
   * Put a char array value out to the specified byte array position.
   *
   * @param bytes  the byte array
   * @param offset position in the array
   * @param val    short to write out
   * @return incremented offset
   */
  public static int putChars(byte[] bytes, int offset, char[] val) {
    int max = val.length * 2 + offset;
    if (bytes == null || (bytes.length < max)) {
      return offset;
    }
    for (int i=0,j=offset; i<val.length && j<max;i++, j+=2){
      char c = val[i];
      bytes[j + 1] = (byte) (c);
      bytes[j] = (byte) (c >>>8);
    }

    return offset + SIZEOF_CHAR;
  }


  /**
   * Convert a BigDecimal value to a byte array
   *
   * @param val
   * @return the byte array
   */
  public static byte[] toBytes(BigDecimal val) {
    byte[] valueBytes = val.unscaledValue().toByteArray();
    byte[] result = new byte[valueBytes.length + SIZEOF_INT];
    int offset = putInt(result, 0, val.scale());
    putBytes(result, offset, valueBytes, 0, valueBytes.length);
    return result;
  }

  /**
   * Converts a byte array to a BigDecimal
   *
   * @param bytes
   * @return the char value
   */
  public static BigDecimal toBigDecimal(byte[] bytes) {
    return toBigDecimal(bytes, 0, bytes.length);
  }


  /**
   * Converts a byte array to a BigDecimal value
   *
   * @param bytes
   * @param offset
   * @return the char value
   */
  public static BigDecimal toBigDecimal(byte[] bytes, int offset) {
    return toBigDecimal(bytes, offset, bytes.length);
  }

  /**
   * Converts a byte array to a BigDecimal value
   *
   * @param bytes
   * @param offset
   * @param length
   * @return the char value
   */
  public static BigDecimal toBigDecimal(byte[] bytes, int offset, final int length) {
    if (bytes == null || length < SIZEOF_INT + 1 ||
      (offset + length > bytes.length)) {
      return null;
    }

    int scale = toInt(bytes, 0);
    byte[] tcBytes = new byte[length - SIZEOF_INT];
    System.arraycopy(bytes, SIZEOF_INT, tcBytes, 0, length - SIZEOF_INT);
    return new BigDecimal(new BigInteger(tcBytes), scale);
  }

  /**
   * Put a BigDecimal value out to the specified byte array position.
   *
   * @param bytes  the byte array
   * @param offset position in the array
   * @param val    BigDecimal to write out
   * @return incremented offset
   */
  public static int putBigDecimal(byte[] bytes, int offset, BigDecimal val) {
    if (bytes == null) {
      return offset;
    }

    byte[] valueBytes = val.unscaledValue().toByteArray();
    byte[] result = new byte[valueBytes.length + SIZEOF_INT];
    offset = putInt(result, offset, val.scale());
    return putBytes(result, offset, valueBytes, 0, valueBytes.length);
  }
  /**
   * @param left
   * @param right
   * @return 0 if equal, < 0 if left is less than right, etc.
   */
  public static int compareTo(final byte [] left, final byte [] right) {
    return compareTo(left, 0, left.length, right, 0, right.length);
  }

  /**
   * @param b1
   * @param b2
   * @param s1 Where to start comparing in the left buffer
   * @param s2 Where to start comparing in the right buffer
   * @param l1 How much to compare from the left buffer
   * @param l2 How much to compare from the right buffer
   * @return 0 if equal, < 0 if left is less than right, etc.
   */
  public static int compareTo(byte[] b1, int s1, int l1,
      byte[] b2, int s2, int l2) {
    // Bring WritableComparator code local
    int end1 = s1 + l1;
    int end2 = s2 + l2;
    for (int i = s1, j = s2; i < end1 && j < end2; i++, j++) {
      int a = (b1[i] & 0xff);
      int b = (b2[j] & 0xff);
      if (a != b) {
        return a - b;
      }
    }
    return l1 - l2;
  }

  /**
   * @param left
   * @param right
   * @return True if equal
   */
  public static boolean equals(final byte [] left, final byte [] right) {
    // Could use Arrays.equals?
    return left == null && right == null? true:
      (left == null || right == null || (left.length != right.length))? false:
        compareTo(left, right) == 0;
  }


  /**
   * @param a
   * @param b
   * @param c
   * @return New array made from a, b and c
   */
  public static byte [] add(final byte [] a, final byte [] b, final byte [] c) {
    byte [] result = new byte[a.length + b.length + c.length];
    System.arraycopy(a, 0, result, 0, a.length);
    System.arraycopy(b, 0, result, a.length, b.length);
    System.arraycopy(c, 0, result, a.length + b.length, c.length);
    return result;
  }

  /**
   * @param a
   * @param length
   * @return First <code>length</code> bytes from <code>a</code>
   */
  public static byte [] head(final byte [] a, final int length) {
    if(a.length < length) return null;
    byte [] result = new byte[length];
    System.arraycopy(a, 0, result, 0, length);
    return result;
  }

  /**
   * @param a
   * @param length
   * @return Last <code>length</code> bytes from <code>a</code>
   */
  public static byte [] tail(final byte [] a, final int length) {
    if(a.length < length) return null;
    byte [] result = new byte[length];
    System.arraycopy(a, a.length - length, result, 0, length);
    return result;
  }



 
  /**
   * @param t
   * @return Array of byte arrays made from passed array of Text
   */
  public static byte [][] toByteArrays(final String [] t) {
    byte [][] result = new byte[t.length][];
    for (int i = 0; i < t.length; i++) {
      result[i] = Bytes.toBytes(t[i]);
    }
    return result;
  }

  /**
   * @param column
   * @return A byte array of a byte array where first and only entry is
   * <code>column</code>
   */
  public static byte [][] toByteArrays(final String column) {
    return toByteArrays(toBytes(column));
  }

  /**
   * @param column
   * @return A byte array of a byte array where first and only entry is
   * <code>column</code>
   */
  public static byte [][] toByteArrays(final byte [] column) {
    byte [][] result = new byte[1][];
    result[0] = column;
    return result;
  }



  /**
   * Bytewise binary increment/deincrement of long contained in byte array
   * on given amount.
   *
   * @param value - array of bytes containing long (length <= SIZEOF_LONG)
   * @param amount value will be incremented on (deincremented if negative)
   * @return array of bytes containing incremented long (length == SIZEOF_LONG)
   * @throws IOException - if value.length > SIZEOF_LONG
   */
  public static byte [] incrementBytes(byte[] value, long amount)
  throws IOException {
    byte[] val = value;
    if (val.length < SIZEOF_LONG) {
      // Hopefully this doesn't happen too often.
      byte [] newvalue;
      if (val[0] < 0) {
        byte [] negativeValue = {-1, -1, -1, -1, -1, -1, -1, -1};
        newvalue = negativeValue;
      } else {
        newvalue = new byte[SIZEOF_LONG];
      }
      System.arraycopy(val, 0, newvalue, newvalue.length - val.length,
        val.length);
      val = newvalue;
    } else if (val.length > SIZEOF_LONG) {
      throw new IllegalArgumentException("Increment Bytes - value too big: " +
        val.length);
    }
    if(amount == 0) return val;
    if(val[0] < 0){
      return binaryIncrementNeg(val, amount);
    }
    return binaryIncrementPos(val, amount);
  }

  /* increment/deincrement for positive value */
  private static byte [] binaryIncrementPos(byte [] value, long amount) {
    long amo = amount;
    int sign = 1;
    if (amount < 0) {
      amo = -amount;
      sign = -1;
    }
    for(int i=0;i<value.length;i++) {
      int cur = ((int)amo % 256) * sign;
      amo = (amo >> 8);
      int val = value[value.length-i-1] & 0x0ff;
      int total = val + cur;
      if(total > 255) {
        amo += sign;
        total %= 256;
      } else if (total < 0) {
        amo -= sign;
      }
      value[value.length-i-1] = (byte)total;
      if (amo == 0) return value;
    }
    return value;
  }

  /* increment/deincrement for negative value */
  private static byte [] binaryIncrementNeg(byte [] value, long amount) {
    long amo = amount;
    int sign = 1;
    if (amount < 0) {
      amo = -amount;
      sign = -1;
    }
    for(int i=0;i<value.length;i++) {
      int cur = ((int)amo % 256) * sign;
      amo = (amo >> 8);
      int val = ((~value[value.length-i-1]) & 0x0ff) + 1;
      int total = cur - val;
      if(total >= 0) {
        amo += sign;
      } else if (total < -256) {
        amo -= sign;
        total %= 256;
      }
      value[value.length-i-1] = (byte)total;
      if (amo == 0) return value;
    }
    return value;
  }
}