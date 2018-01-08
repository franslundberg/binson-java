package org.binson.lowlevel;

import java.io.UnsupportedEncodingException;

/**
 * Low-level class that converts between low-level types
 * (short, int, long, double, String) to raw bytes and back.
 * Little-endian byte-order is used.
 * 
 * @author Frans Lundberg
 */
public class Bytes {
    public static String utf8ToString(byte[] bytes) {
        String result = null;
        try {
            result = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error("never happens for compliant JVM", e);
        }
        return result;
    }
    
    public static byte[] stringToUtf8(String s) {
        try {
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error("never happens", e);
        }
    }

    public static final short bytesToShortLE(byte[] arr, int offset) {
        int result = (arr[offset++] & 0x00ff);
        result |= (arr[offset++] & 0x00ff) << 8;
        return (short) result;
    }
    
    public static final int bytesToIntLE(byte[] arr, int offset) {
        int i = offset;
        int result = (arr[i++] & 0x00ff);
        result |= (arr[i++] & 0x00ff) << 8;
        result |= (arr[i++] & 0x00ff) << 16;
        result |= (arr[i] & 0x00ff) << 24;
        return result;
    }
    
    public static final long bytesToLongLE(byte[] arr, int offset) {
        int i = offset;
        long result = (arr[i++] & 0x000000ffL);
        result |= (arr[i++] & 0x000000ffL) << 8;
        result |= (arr[i++] & 0x000000ffL) << 16;
        result |= (arr[i++] & 0x000000ffL) << 24;
        result |= (arr[i++] & 0x000000ffL) << 32;
        result |= (arr[i++] & 0x000000ffL) << 40;
        result |= (arr[i++] & 0x000000ffL) << 48;
        result |= (arr[i]   & 0x000000ffL) << 56;
        return result;
    }
    
    public static double bytesToDoubleLE(byte[] arr, int offset) {
        long myLong = bytesToLongLE(arr, offset);
        return Double.longBitsToDouble(myLong);
    }

    public static final void shortToBytesLE(int value, byte[] arr, int offset) {
        int i = offset;
        arr[i++] = (byte) value;
        arr[i++] = (byte) (value >>> 8);
    }

    public static final void intToBytesLE(int value, byte[] arr, int offset) {
        arr[offset++] = (byte) value;
        arr[offset++] = (byte) (value >>> 8);
        arr[offset++] = (byte) (value >>> 16);
        arr[offset] = (byte) (value >>> 24);
    }

    public static final void longToBytesLE(final long value, final byte[] arr, int offset) {
        int i = offset;
        arr[i++] = (byte) value;
        arr[i++] = (byte) (value >>> 8);
        arr[i++] = (byte) (value >>> 16);
        arr[i++] = (byte) (value >>> 24);
        arr[i++] = (byte) (value >>> 32);
        arr[i++] = (byte) (value >>> 40);
        arr[i++] = (byte) (value >>> 48);
        arr[i]   = (byte) (value >>> 56);
    }
    
    public static final void doubleToBytesLE(double value, byte[] arr, int offset) {
        long bits = Double.doubleToRawLongBits(value);
        longToBytesLE(bits, arr, 1);
    }
    
    public static final int unsigned(byte b) {
        return b >= 0 ? b : b + 256;
    }
}
