package net.yeah.zhouyou.mickey.execute;

import net.yeah.zhouyou.mickey.lexical.DFA;
import net.yeah.zhouyou.mickey.lexical.Token;
import net.yeah.zhouyou.mickey.semantic.SemanticParser;
import net.yeah.zhouyou.mickey.syntax.SyntaxParser;
import net.yeah.zhouyou.mickey.syntax.TreeNode;
import net.yeah.zhouyou.mickey.util.DbgInfo;

import java.util.List;

public class Calculator {
    private static Calculator ourInstance = new Calculator();

    public static Calculator getInstance() {
        return ourInstance;
    }

    private Calculator() {
    }

    public void execute(String line) {
        if (line == null || !line.startsWith("calc "))
            throw new RuntimeException();
        line = line.substring(5);
        DbgInfo.println(line);
        List<Token> tokenList = DFA.parse(line);
        TreeNode tree = SyntaxParser.parse(tokenList);
        String[] asm = SemanticParser.parse(tree);
        byte[] bytes = ASM.encode(asm);
        new VM().execute(bytes);
    }
}
