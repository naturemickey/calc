package net.yeah.zhouyou.mickey.execute;

import net.yeah.zhouyou.mickey.util.DbgInfo;
import net.yeah.zhouyou.mickey.util.NumberUtil;

import java.util.*;

/**
 * Created by MICKEY on 14-2-4.
 * <p>
 * op_code  operand remark
 * -------------------------------------------------------------------------------------------------------------
 * store    number  把number放入栈顶
 * add              从栈顶取出两个数相加，并把结果压回栈中
 * sub              从栈顶取出一个数做为被减数，再取一个做为减数，相减之后的结果入栈
 * mul              从栈顶取出两个数相乘，并把结果入栈
 * div              从栈顶取出一个数做为除法的分子，再取出一个做为除法的分母，相除的结果入栈
 * pow              从栈顶取出一个数做为底，再取出一个做为幂，计算结果入栈
 * sqrt             从栈顶取出一个数，把这个数开平方后的结果入栈
 * print            打印栈顶的数字
 * -------------------------------------------------------------------------------------------------------------
 * 说明：
 * 1.只有store一个命令有操作数，其它命令操作的数据都从栈中取。
 * 2.操作数为64位，为开发简单，以Double.doubleToLongBits方法返回的位来表示。
 * 3.汇编形式的store的操作数以的double形式存在，转为字节码时以64位存在。
 * 4.汇编形式的文本形式为:op_code [operand] op_code [operand] op_code [operand]……
 */
public class ASM {

    private static Map<String, Byte> asmToByte = new HashMap<>();
    private static Map<Byte, String> byteToAsm = new HashMap<>();

    public static final byte STORE = 0b0000_0001;
    public static final byte ADD = 0b00000010;
    public static final byte SUB = 0b00000100;
    public static final byte MUL = 0b00001000;
    public static final byte DIV = 0b00010000;
    public static final byte POW = 0b00100000;
    public static final byte SQRT = 0b01000000;
    public static final byte PRINT = (byte) 0b10000000;

    public static final String store = "store";
    public static final String add = "add";
    public static final String sub = "sub";
    public static final String mul = "mul";
    public static final String div = "div";
    public static final String pow = "pow";
    public static final String sqrt = "sqrt";
    public static final String print = "print";

    static {
        asmToByte.put(store, STORE);
        asmToByte.put(add, ADD);
        asmToByte.put(sub, SUB);
        asmToByte.put(mul, MUL);
        asmToByte.put(div, DIV);
        asmToByte.put(pow, POW);
        asmToByte.put(sqrt, SQRT);
        asmToByte.put(print, PRINT);

        byteToAsm.put(STORE, store);
        byteToAsm.put(ADD, add);
        byteToAsm.put(SUB, sub);
        byteToAsm.put(MUL, mul);
        byteToAsm.put(DIV, div);
        byteToAsm.put(POW, pow);
        byteToAsm.put(SQRT, sqrt);
        byteToAsm.put(PRINT, print);
    }

    public static byte[] encode(String asm) {
        return encode(asm.split("\\s+"));
    }

    public static byte[] encode(String[] asm) {
        List<Byte> list = new ArrayList<>();
        for (int i = 0; i < asm.length; ++i) {
            String s = asm[i];
            Byte b = asmToByte.get(s);
            if (b == null)
                throw new RuntimeException(s);
            list.add(b);
            if (b == STORE) {
                i += 1;
                s = asm[i];
                byte[] bytes = NumberUtil.doubleToBytes(Double.parseDouble(s));
                for (byte b1 : bytes)
                    list.add(b1);
            }
        }
        int len = list.size();
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; ++i)
            bytes[i] = list.get(i);
        return bytes;
    }

    public static byte[] encode(List<String> asm) {
        return encode(asm.toArray(new String[asm.size()]));
    }

    public static String decode(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            byte b = bytes[i];
            String s = byteToAsm.get(b);
            if (s == null)
                throw new RuntimeException();
            if (i > 0)
                sb.append('\n');
            sb.append(s);
            if (b == STORE) {
                sb.append(' ');
                i += 1;
                sb.append(NumberUtil.bytesToDouble(bytes, i));
                i += 7;
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String asm = "store 1 print store -1 add";
        byte[] bytes = encode(asm);
        System.out.println(byteArrayToString(bytes));
        System.out.println(decode(bytes));
    }

    public static String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            for(int i =7 ; i >= 0; --i){
                int x = (b & (1 << i))>> i;
                sb.append(x);
            }
        }
        return sb.toString();
    }
}
