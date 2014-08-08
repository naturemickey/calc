package net.yeah.zhouyou.mickey.util;

import java.lang.reflect.Array;

public class DbgInfo {

    private static final boolean open = false;

    public static void print(Object... info) {
        if (!open) return;
        printIm(info);
    }

    public static void printIm(Object... info) {
        System.out.print("[");
        if (info == null)
            System.out.println("null");
        else {
            for (int i = 0; i < info.length; ++i) {
                if (i != 0) System.out.print(' ');
                Object v = info[i];
                if (v == null) System.out.print("null");
                else if (v.getClass().isArray()) {
                    int len = Array.getLength(v);
                    Object[] os = new Object[len];
                    for (int i1 = 0; i1 < len; ++i1)
                        os[i1] = Array.get(v, i1);
                    print(os);
                } else System.out.print(v);
            }
        }
        System.out.print("]");
    }

    public static void println(Object... info) {
        if (!open) return;
        println(info);
        System.out.println();
    }

    public static void printlnIm(Object... info) {
        printIm(info);
        System.out.println();
    }

    public static void main(String[] args) {

    }
}
