package net.yeah.zhouyou.mickey.syntax;

import net.yeah.zhouyou.mickey.context.FunctionInfo;
import net.yeah.zhouyou.mickey.lexical.DFA;
import net.yeah.zhouyou.mickey.lexical.Token;

import java.util.List;

/**
 * <pre>
 * %%
 *
 * add sub mul div , ( ) varName funName number
 *
 * %%
 *
 * exp      ->  term
 * exp      ->  term add exp
 * exp      ->  term sub exp
 * term     ->  factor
 * term     ->  factor mul term
 * term     ->  factor div term
 * factor   ->  varName
 * factor   ->  number
 * factor   ->  sub number
 * factor   ->  funCall
 * factor   ->  ( exp )
 * funCall  ->  funName ( params )
 * params   ->  exp
 * params   ->  exp , params
 *
 * %%
 * </pre>
 */
public class SyntaxParser {

    public static TreeNode parse(List<Token> tokenList) {
        return parseExp(tokenList, IntegerHolder.apply(0));
    }

    /**
     * exp      ->  term
     * exp      ->  term add exp
     * exp      ->  term sub exp
     */
    private static TreeNode parseExp(List<Token> tokenList, IntegerHolder offset) {
        TreeNode exp = new TreeNode(TreeNode.Type.EXP);
        TreeNode term = parseTerm(tokenList, offset);
        exp.getSubNodes().add(term);

        if (offset.integer < tokenList.size()) {
            Token t = tokenList.get(offset.integer);
            TreeNode op = null;
            if (t.getTokenType() == Token.Type.ADD) {
                op = new TreeNode(TreeNode.Type.ADD);
            } else if (t.getTokenType() == Token.Type.SUB) {
                op = new TreeNode(TreeNode.Type.SUB);
            }
            if (op != null) {
                exp.getSubNodes().add(op);
                offset.integer += 1;
                exp.getSubNodes().add(parseExp(tokenList, offset));
            }
        }
        return exp;
    }

    /**
     * term     ->  factor
     * term     ->  factor mul term
     * term     ->  factor div term
     */
    private static TreeNode parseTerm(List<Token> tokenList, IntegerHolder offset) {
        TreeNode term = new TreeNode(TreeNode.Type.TERM);
        term.getSubNodes().add(parseFactor(tokenList, offset));

        if (offset.integer < tokenList.size()) {
            Token t = tokenList.get(offset.integer);
            TreeNode op = null;
            if (t.getTokenType() == Token.Type.MUL) {
                op = new TreeNode(TreeNode.Type.MUL);
            } else if (t.getTokenType() == Token.Type.DIV) {
                op = new TreeNode(TreeNode.Type.DIV);
            }
            if (op != null) {
                term.getSubNodes().add(op);
                offset.integer += 1;
                term.getSubNodes().add(parseTerm(tokenList, offset));
            }
        }
        return term;
    }

    /**
     * factor   ->  varName
     * factor   ->  number
     * factor   ->  sub number
     * factor   ->  funCall
     * factor   ->  ( exp )
     */
    private static TreeNode parseFactor(List<Token> tokenList, IntegerHolder offset) {
        TreeNode factor = new TreeNode(TreeNode.Type.FACTOR);
        Token t = tokenList.get(offset.integer);
        if (t.getTokenType() == Token.Type.VAR) {
            factor.getSubNodes().add(new TreeNode(TreeNode.Type.VAR_NAME, t.getContent()));
        } else if (t.getTokenType() == Token.Type.NUM) {
            factor.getSubNodes().add(new TreeNode(TreeNode.Type.NUM, t.getContent()));
        } else if (t.getTokenType() == Token.Type.SUB) {
            t = tokenList.get(offset.integer += 1);
            factor.getSubNodes().add(new TreeNode(TreeNode.Type.NUM, -1 * (Double) t.getContent()));
        } else if (t.getTokenType() == Token.Type.FUN) {
            factor.getSubNodes().add(parseFunCall(tokenList, offset));
        } else if (t.getTokenType() == Token.Type.LBT) {
            offset.integer += 1;
            factor.getSubNodes().add(parseExp(tokenList, offset));
            Token rbt = tokenList.get(offset.integer);
            if (rbt.getTokenType() != Token.Type.RBT)
                throw new RuntimeException();
        } else throw new RuntimeException();
        offset.integer += 1;
        return factor;
    }

    /**
     * funCall  ->  funName ( params )
     */
    private static TreeNode parseFunCall(List<Token> tokenList, IntegerHolder offset) {
        TreeNode funCall = new TreeNode(TreeNode.Type.FUN_CALL);

        Token funName = tokenList.get(offset.integer);
        if (funName.getTokenType() == Token.Type.FUN)
            funCall.getSubNodes().add(new TreeNode(TreeNode.Type.FUN_NAME, funName.getContent()));
        else throw new RuntimeException("ERROR[PARSER] CANNOT GET FUNCTION NAME");

        offset.integer += 1;
        Token lbt = tokenList.get(offset.integer);
        if (lbt.getTokenType() != Token.Type.LBT)
            throw new RuntimeException("ERROR[PARSER] THERE IS NO '('");

        offset.integer += 1;
        TreeNode params = parseParams(tokenList, offset);
        if (getParamsCount(params) != FunctionInfo.getParamsCount((String) funName.getContent())) {
            System.out.println(params);
            throw new RuntimeException("ERROR[PARSER] PARAMETER'S COUNT IS NOT RIGHT");
        }
        funCall.getSubNodes().add(params);

        Token rbt = tokenList.get(offset.integer);
        if (rbt.getTokenType() != Token.Type.RBT)
            throw new RuntimeException("ERROR[PARSER] THERE IS NO ')'");

        return funCall;
    }

    private static int getParamsCount(TreeNode params) {
        if (params.getSubNodes().size() == 1) return 1;
        return 1 + getParamsCount(params.getSubNodes().get(1));
    }

    /**
     * params   ->  exp
     * params   ->  exp , params
     */
    private static TreeNode parseParams(List<Token> tokenList, IntegerHolder offset) {
        TreeNode params = new TreeNode(TreeNode.Type.PARAMS);
        TreeNode exp = parseExp(tokenList, offset);
        params.getSubNodes().add(exp);

        if (offset.integer < tokenList.size()) {
            Token t = tokenList.get(offset.integer);
            if (t.getTokenType() == Token.Type.COMMA) {
                offset.integer += 1;
                params.getSubNodes().add(parseParams(tokenList, offset));
            }
        }
        return params;
    }

    private static class IntegerHolder {
        int integer;

        static IntegerHolder apply(int integer) {
            IntegerHolder r = new IntegerHolder();
            r.integer = integer;
            return r;
        }
    }

    public static void main(String[] args) {
//        List<Token> tokenList = DFA.parse("a-b");
//        System.out.println(tokenList);
//        TreeNode tn = SyntaxParser.parse(tokenList);
//        System.out.println(tn);
//
//        tokenList = DFA.parse("pow(1.1,2.2)");
//        System.out.println(tokenList);
//        tn = SyntaxParser.parse(tokenList);
//        System.out.println(tn);
//
//        tokenList = DFA.parse("a/b");
//        System.out.println(tokenList);
//        tn = SyntaxParser.parse(tokenList);
//        System.out.println(tn);
//
//        tokenList = DFA.parse("(123.4)");
//        System.out.println(tokenList);
//        tn = SyntaxParser.parse(tokenList);
//        System.out.println(tn);

        System.out.println(SyntaxParser.parse(DFA.parse("pow(a+2, 2)")));
    }
}
