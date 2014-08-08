package net.yeah.zhouyou.mickey.semantic;

import net.yeah.zhouyou.mickey.context.Context;
import net.yeah.zhouyou.mickey.lexical.DFA;
import net.yeah.zhouyou.mickey.syntax.SyntaxParser;
import net.yeah.zhouyou.mickey.syntax.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.yeah.zhouyou.mickey.execute.ASM.*;

public class SemanticParser {

    public static String[] parse(TreeNode syntaxTree) {
        List<String> asm = parseExp(syntaxTree);
        asm.add(print);
        return asm.toArray(new String[asm.size()]);
    }

    private static List<String> parseExp(TreeNode syntaxTree) {
        if (syntaxTree.getType() != TreeNode.Type.EXP)
            throw new RuntimeException();
        List<TreeNode> children = syntaxTree.getSubNodes();
        TreeNode term = children.get(0);
        List<String> res;
        TreeNode op = null;

        if (children.size() > 1) {
            op = children.get(1);
            res = parseExp(children.get(2));
        } else res = new ArrayList<>();

        res.addAll(parseTerm(term));

        if (op != null) {
            if (op.getType() == TreeNode.Type.ADD) res.add(add);
            else if (op.getType() == TreeNode.Type.SUB) res.add(sub);
            else throw new RuntimeException();
        }
        return res;
    }

    private static List<String> parseTerm(TreeNode syntaxTree) {
        if (syntaxTree.getType() != TreeNode.Type.TERM)
            throw new RuntimeException();
        List<TreeNode> children = syntaxTree.getSubNodes();
        TreeNode factor = children.get(0);
        List<String> res;
        TreeNode op = null;

        if (children.size() > 1) {
            op = children.get(1);
            res = parseTerm(children.get(2));
        } else res = new ArrayList<>();

        res.addAll(parseFactor(factor));

        if (op != null) {
            if (op.getType() == TreeNode.Type.MUL) res.add(mul);
            else if (op.getType() == TreeNode.Type.DIV) res.add(div);
            else throw new RuntimeException();
        }
        return res;
    }

    private static List<String> parseFactor(TreeNode syntaxTree) {
        if (syntaxTree.getType() != TreeNode.Type.FACTOR)
            throw new RuntimeException();
        TreeNode tn = syntaxTree.getSubNodes().get(0);
        List<String> res = new ArrayList<>();
        if (tn.getType() == TreeNode.Type.VAR_NAME) {
            res.add("store");
            res.add(Context.getInstance().getValue((String) tn.getContent()).toString());
        } else if (tn.getType() == TreeNode.Type.NUM) {
            res.add("store");
            res.add(tn.getContent().toString());
        } else if (tn.getType() == TreeNode.Type.FUN_CALL)
            res = parseFunCall(tn);
        else if (tn.getType() == TreeNode.Type.EXP)
            res = parseExp(tn);
        else throw new RuntimeException();
        return res;
    }

    private static List<String> parseFunCall(TreeNode syntaxTree) {
        if (syntaxTree.getType() != TreeNode.Type.FUN_CALL)
            throw new RuntimeException();
        List<TreeNode> tnl = syntaxTree.getSubNodes();
        TreeNode functionName = tnl.get(0);
        List<String> res = parseParams(tnl.get(1));
        res.add((String) functionName.getContent());
        return res;
    }

    private static List<String> parseParams(TreeNode syntaxTree) {
        if (syntaxTree.getType() != TreeNode.Type.PARAMS)
            throw new RuntimeException();
        List<TreeNode> params = syntaxTree.getSubNodes();
        TreeNode exp = params.get(0);
        List<String> res;
        if (params.size() > 1) res = parseParams(params.get(1));
        else res = new ArrayList<>();

        res.addAll(parseExp(exp));
        return res;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.asList(parse(SyntaxParser.parse(DFA.parse("1-2")))));
        System.out.println(Arrays.asList(parse(SyntaxParser.parse(DFA.parse("1-2*3")))));
        System.out.println(Arrays.asList(parse(SyntaxParser.parse(DFA.parse("(10 + pow(2, 3)) * sqrt(4) - 1")))));
    }
}
