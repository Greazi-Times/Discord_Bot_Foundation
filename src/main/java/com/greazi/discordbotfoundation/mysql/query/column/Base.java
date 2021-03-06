package com.greazi.discordbotfoundation.mysql.query.column;

import com.greazi.discordbotfoundation.mysql.query.action.IAction;

public class Base extends Column {

    protected java.lang.Integer length;

    protected java.lang.String content;

    public Base(IAction<IColumn> action, java.lang.String name, java.lang.String content) {
        super(action, name, true);

        this.name = name;

        this.content = content;
    }

    @Override
    public java.lang.String toString() {
        return this.action.getPrefix() + name + "  " + content;
    }
}
