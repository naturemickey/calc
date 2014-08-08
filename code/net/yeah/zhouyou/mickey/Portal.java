package net.yeah.zhouyou.mickey;

import net.yeah.zhouyou.mickey.context.Context;
import net.yeah.zhouyou.mickey.execute.Calculator;

import java.util.Scanner;

public class Portal {

    private static final String lineStart = "CALC> ";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print(lineStart);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line != null) {
                    line = line.trim();
                    if (line.length() != 0) {
                        if ("exit".equals(line) || "bye".equals(line))
                            break;

                        try {
                            if (line.startsWith("set "))
                                Context.getInstance().parseSet(line);
                            else if (line.startsWith("calc ")) {
                                Calculator.getInstance().execute(line);
                            } else throw new RuntimeException("ERROR COMMAND");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                System.out.print(lineStart);
            }
        }
    }

}
