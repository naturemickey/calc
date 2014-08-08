package net.yeah.zhouyou.mickey.lexical;

import net.yeah.zhouyou.mickey.context.FunctionInfo;

import java.util.*;

public class DFA {

    private static final int TERMINAL_FLG = 0x8000_0000;

    private static interface State {
        int START = 0x0001;

        int INT = 0x0002 | TERMINAL_FLG;
        int POINT = 0x0004;
        int NUMBER = 0x0008 | TERMINAL_FLG;

        int ID = 0x0010 | TERMINAL_FLG;

        int ADD = 0x0020 | TERMINAL_FLG;
        int SUB = 0x0040 | TERMINAL_FLG;
        int MUL = 0x0080 | TERMINAL_FLG;
        int DIV = 0x0100 | TERMINAL_FLG;

        int LBT = 0x0200 | TERMINAL_FLG;
        int RBT = 0x0400 | TERMINAL_FLG;

        int COMMA = 0x0800 | TERMINAL_FLG;
    }

    public static List<Token> parse(String exp) {
        Node currentNode = startNode;
        int currentIdx = 0;
        char[] chars = exp.toCharArray();
        List<Token> res = new ArrayList<>();
        for (int i = 0; i < chars.length; ) {
            char c = chars[i];
            while (c == ' ' || c == '\t')
                c = chars[++i];

            Node node = currentNode.path(c);
            if (node != null) {
                currentNode = node;
                i += 1;
            } else {
                getToken(currentNode, currentIdx, chars, res, i);

                currentIdx = i;
                currentNode = startNode;
            }
        }
        getToken(currentNode, currentIdx, chars, res, chars.length);
        return res;
    }

    private static void getToken(Node currentNode, int currentIdx, char[] chars, List<Token> res, int i) {
        String s = String.valueOf(chars, currentIdx, i - currentIdx).trim();
        if ((currentNode.state & TERMINAL_FLG) == 0)
            throw new RuntimeException("ERROR[DFA] NOT TERMINAL CHARACTER");
        Token token = null;
        switch (currentNode.state) {
            case State.INT:
            case State.NUMBER:
                token = new Token(Token.Type.NUM, Double.valueOf(s));
                break;
            case State.ID:
                if (FunctionInfo.isFunctionName(s)) token = new Token(Token.Type.FUN, s);
                else token = new Token(Token.Type.VAR, s);
                break;
            case State.ADD:
                token = new Token(Token.Type.ADD);
                break;
            case State.SUB:
                token = new Token(Token.Type.SUB);
                break;
            case State.MUL:
                token = new Token(Token.Type.MUL);
                break;
            case State.DIV:
                token = new Token(Token.Type.DIV);
                break;
            case State.LBT:
                token = new Token(Token.Type.LBT);
                break;
            case State.RBT:
                token = new Token(Token.Type.RBT);
                break;
            case State.COMMA:
                token = new Token(Token.Type.COMMA);
        }
        if (token == null)
            throw new RuntimeException("ERROR[DFA] CAN NOT GET TOKEN");
        res.add(token);
    }


    private static final Node startNode = new Node(State.START);

    private static class Node {
        int state;
        Map<Character, Node> paths = new HashMap<>();

        Node(int state) {
            this.state = state;
        }

        void addPath(Character c, Node to) {
            this.paths.put(c, to);
        }

        Node path(Character c) {
            return this.paths.get(c);
        }

        void addPath(Character[] cs, Node to) {
            Arrays.stream(cs).forEach(c -> this.paths.put(c, to));
        }
    }

    static {
        Character[] letters = {'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        Character[] numbers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        Node intNode = new Node(State.INT);
        Node pointNode = new Node(State.POINT);
        Node numberNode = new Node(State.NUMBER);
        Node idNode = new Node(State.ID);

        Node addNode = new Node(State.ADD);
        Node subNode = new Node(State.SUB);
        Node mulNode = new Node(State.MUL);
        Node divNode = new Node(State.DIV);
        Node lbtNode = new Node(State.LBT);
        Node rbtNode = new Node(State.RBT);
        Node commaNode = new Node(State.COMMA);

        startNode.addPath(numbers, intNode);
        startNode.addPath(letters, idNode);
        startNode.addPath('.', pointNode);
        startNode.addPath('+', addNode);
        startNode.addPath('-', subNode);
        startNode.addPath('*', mulNode);
        startNode.addPath('/', divNode);
        startNode.addPath('(', lbtNode);
        startNode.addPath(')', rbtNode);
        startNode.addPath(',', commaNode);

        intNode.addPath(numbers, intNode);
        intNode.addPath('.', numberNode);
        pointNode.addPath(numbers, numberNode);
        numberNode.addPath(numbers, numberNode);
        idNode.addPath(letters, idNode);
        idNode.addPath(numbers, idNode);
    }

    public static void main(String[] args) {
        System.out.println(parse("((a)+b)*pow(2,1.5)"));
        System.out.println(parse("pow(a+2, 2)"));
        System.out.println(parse("1 abc +"));
    }
}
