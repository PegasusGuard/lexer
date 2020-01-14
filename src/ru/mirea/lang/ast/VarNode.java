package ru.mirea.lang.ast;

import ru.mirea.lang.Token;

public class VarNode extends ExprNode {

    public final Token id;
    public Integer value;

    public VarNode(Token id, Integer value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String toString() {
        return id.text;
    }
}
