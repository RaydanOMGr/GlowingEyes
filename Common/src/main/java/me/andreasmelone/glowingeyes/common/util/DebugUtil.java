package me.andreasmelone.glowingeyes.common.util;

public class DebugUtil {

    // Debug for int
    public static int debugValue(int i, String namespace) {
        System.out.printf("[%s DEBUG] %d%n", namespace, i);
        return i;
    }

    public static int debugValue(int i) {
        return debugValue(i, "DEFAULT");
    }

    // Debug for long
    public static long debugValue(long l, String namespace) {
        System.out.printf("[%s DEBUG] %d%n", namespace, l);
        return l;
    }

    public static long debugValue(long l) {
        return debugValue(l, "DEFAULT");
    }

    // Debug for double
    public static double debugValue(double d, String namespace) {
        System.out.printf("[%s DEBUG] %f%n", namespace, d);
        return d;
    }

    public static double debugValue(double d) {
        return debugValue(d, "DEFAULT");
    }

    // Debug for float
    public static float debugValue(float f, String namespace) {
        System.out.printf("[%s DEBUG] %f%n", namespace, f);
        return f;
    }

    public static float debugValue(float f) {
        return debugValue(f, "DEFAULT");
    }

    // Debug for boolean
    public static boolean debugValue(boolean b, String namespace) {
        System.out.printf("[%s DEBUG] %b%n", namespace, b);
        return b;
    }

    public static boolean debugValue(boolean b) {
        return debugValue(b, "DEFAULT");
    }

    // Debug for char
    public static char debugValue(char c, String namespace) {
        System.out.printf("[%s DEBUG] %c%n", namespace, c);
        return c;
    }

    public static char debugValue(char c) {
        return debugValue(c, "DEFAULT");
    }

    // Debug for byte
    public static byte debugValue(byte b, String namespace) {
        System.out.printf("[%s DEBUG] %d%n", namespace, b);
        return b;
    }

    public static byte debugValue(byte b) {
        return debugValue(b, "DEFAULT");
    }

    // Debug for short
    public static short debugValue(short s, String namespace) {
        System.out.printf("[%s DEBUG] %d%n", namespace, s);
        return s;
    }

    public static short debugValue(short s) {
        return debugValue(s, "DEFAULT");
    }

    // Debug for String
    public static String debugValue(String str, String namespace) {
        System.out.printf("[%s DEBUG] %s%n", namespace, str);
        return str;
    }

    public static String debugValue(String str) {
        return debugValue(str, "DEFAULT");
    }

    // Debug for Object
    public static <T> T debugValue(T obj, String namespace) {
        System.out.printf("[%s DEBUG] %s%n", namespace, obj);
        return obj;
    }

    public static <T> T debugValue(T obj) {
        return debugValue(obj, "DEFAULT");
    }

    // Debug hexadecimal for int
    public static int debugHexValue(int i, String namespace) {
        System.out.printf("[%s DEBUG HEX] 0x%s%n", namespace, Integer.toHexString(i));
        return i;
    }

    public static int debugHexValue(int i) {
        return debugHexValue(i, "DEFAULT");
    }

    // Debug hexadecimal for long
    public static long debugHexValue(long l, String namespace) {
        System.out.printf("[%s DEBUG HEX] 0x%s%n", namespace, Long.toHexString(l));
        return l;
    }

    public static long debugHexValue(long l) {
        return debugHexValue(l, "DEFAULT");
    }

    // Debug hexadecimal for short
    public static short debugHexValue(short s, String namespace) {
        System.out.printf("[%s DEBUG HEX] 0x%s%n", namespace, Integer.toHexString(Short.toUnsignedInt(s)));
        return s;
    }

    public static short debugHexValue(short s) {
        return debugHexValue(s, "DEFAULT");
    }

    // Debug hexadecimal for byte
    public static byte debugHexValue(byte b, String namespace) {
        System.out.printf("[%s DEBUG HEX] 0x%s%n", namespace, Integer.toHexString(Byte.toUnsignedInt(b)));
        return b;
    }

    public static byte debugHexValue(byte b) {
        return debugHexValue(b, "DEFAULT");
    }
}
