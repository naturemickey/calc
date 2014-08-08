package net.yeah.zhouyou.mickey.execute;

import net.yeah.zhouyou.mickey.util.NumberUtil;

import java.util.ArrayDeque;
import java.util.Deque;

import static net.yeah.zhouyou.mickey.execute.ASM.*;

public class VM {
    private Deque<Double> stack = new ArrayDeque<>();

    public void execute(byte[] bytes) {
        for (int i = 0; i < bytes.length; ++i) {
            byte b = bytes[i];
            switch (b) {
                case STORE:
                    i += 1;
                    stack.push(NumberUtil.bytesToDouble(bytes, i));
                    i += 7;
                    break;
                case ADD:
                    stack.push(stack.pop() + stack.pop());
                    break;
                case SUB:
                    stack.push(stack.pop() - stack.pop());
                    break;
                case MUL:
                    stack.push(stack.pop() * stack.pop());
                    break;
                case DIV:
                    stack.push(stack.pop() / stack.pop());
                    break;
                case POW:
                    stack.push(Math.pow(stack.pop(), stack.pop()));
                    break;
                case SQRT:
                    stack.push(Math.sqrt(stack.pop()));
                    break;
                case PRINT:
                    System.out.println(stack.getFirst());
                    break;
                default:
                    throw new RuntimeException("ERROR[VM] ERROR BITS");
            }
        }
    }

    public static void main(String[] args) {
        String asm = "store 3 " +
                "store 2 " +
                "mul " +
                "store 1 " +
                "sub " +
                "print";

        new VM().execute(ASM.encode(asm));

        //(10 + pow(2, 3)) * sqrt(4) - 1
        new VM().execute(ASM.encode("store 1 " +
                "store 4 " +
                "sqrt " +
                "store 3 " +
                "store 2 " +
                "pow " +
                "store 10 " +
                "add " +
                "mul " +
                "sub " +
                "print "));
        new VM().execute(ASM.encode("store 3 " +
                "store 2 " +
                "store 1 " +
                "add " +
                "mul " +
                "print"));
    }
}
