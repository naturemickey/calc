package net.yeah.zhouyou.mickey.util;

import java.util.Arrays;

public class NumberUtil {

    public static byte[] doubleToBytes(double d) {
        return longToBytes(Double.doubleToLongBits(d));
    }

    public static double bytesToDouble(byte[] bytes) {
        return Double.longBitsToDouble(bytesToLong(bytes));
    }

    public static double bytesToDouble(byte[] bytes, int offset) {
        return Double.longBitsToDouble(bytesToLong(bytes, offset));
    }

    public static byte[] longToBytes(long bits) {
        byte[] res = new byte[8];
        for (int i = 0; i < 8; ++i) {
            res[i] = (byte) ((bits >> (i * 8)) & 0xff);
        }
        return res;
    }

    public static long bytesToLong(byte[] bytes) {
        return bytesToLong(bytes, 0);
    }

    public static long bytesToLong(byte[] bytes, int offset) {
        long bits = 0;
        for (int i = 0; i < 8; ++i) {
            bits |= ((0xffl & bytes[i + offset]) << (i * 8));
        }
        return bits;
    }

    public static void main(String[] args) {
        testLong(Long.MIN_VALUE + 1);
        testLong(1);
        testDouble(1);

        System.out.println(Double.longBitsToDouble(Double.doubleToLongBits(1)));
    }

    static void testLong(long l) {
        byte[] bytes = longToBytes(l);

        Byte[] bs = new Byte[bytes.length];
        for (int i = 0; i < bs.length; ++i) bs[i] = bytes[i];

        System.out.println(l);
        System.out.println(Arrays.asList(bs));
        System.out.println(bytesToLong(bytes));
    }

    static void testDouble(double d) {
        byte[] bytes = doubleToBytes(d);

        Byte[] bs = new Byte[bytes.length];
        for (int i = 0; i < bs.length; ++i) bs[i] = bytes[i];

        System.out.println(d);
        System.out.println(Double.doubleToLongBits(d));
        System.out.println(Arrays.asList(bs));
        System.out.println(bytesToLong(bytes));
        System.out.println(bytesToDouble(bytes));
    }
}
