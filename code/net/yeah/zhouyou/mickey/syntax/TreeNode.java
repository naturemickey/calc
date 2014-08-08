package net.yeah.zhouyou.mickey.syntax;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    public static enum Type {
        ADD, SUB, MUL, DIV, NUM, FUN_NAME, VAR_NAME, FACTOR, FUN_CALL, PARAMS, TERM, EXP,
    }

    private Type type;
    private Object content;
    private List<TreeNode> subNodes = new ArrayList<>();

    public TreeNode(Type type, Object content) {
        this.type = type;
        this.content = content;
    }

    public TreeNode(Type type) {
        this.type = type;
    }

    public List<TreeNode> getSubNodes() {
        return this.subNodes;
    }

    public Type getType() {
        return this.type;
    }

    public Object getContent() {
        return this.content;
    }

    @Override
    public String toString() {
        return "{" + type +
                (content == null ? "" : ", " + content) +
                (subNodes.size() == 0 ? "" : ", " + subNodes) +
                '}';
    }
}
