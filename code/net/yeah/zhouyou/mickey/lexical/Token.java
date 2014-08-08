package net.yeah.zhouyou.mickey.lexical;

public class Token {

    public static enum Type {
        NUM, // 数字
        FUN, // 函数名
        VAR, // 变更名
        ADD, // +
        SUB, // -
        MUL, // *
        DIV, // /
        LBT, // (
        RBT, // )
        COMMA, // ,
    }

    private Type tokenType;
    private Object content;

    public Token(Type tokenType, Object content) {
        this.tokenType = tokenType;
        this.content = content;
    }

    public Token(Type tokenType) {
        this.tokenType = tokenType;
    }

    public Type getTokenType() {
        return tokenType;
    }

    public Object getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenType=" + tokenType +
                (content == null ? "" : ", content=" + content) +
                '}';
    }
}
