package com.greazi.discordbotfoundation.mysql.query.column;

import com.greazi.discordbotfoundation.mysql.query.action.IAction;

public class Integer extends Numeric {

    public Integer(IAction<IColumn> action, java.lang.String name) {
        super(action, name, false, false, 11);
    }

    public Integer(IAction<IColumn> action, java.lang.String name, java.lang.Integer length) {
        super(action, name, false, false, length);
    }

    @Override
    public java.lang.String toString() {
        return this.action.getPrefix() + name + " INT" + (length > 0 ? "(" + length + ")" : "") + (nullable ? " NULL" : " NOT NULL") + (unique ? " UNIQUE" : "") + (primary ? " PRIMARY KEY" : "") + (increment ? " AUTO_INCREMENT" : "") + (defaultValue != null ? " DEFAULT " + (!nativeDefault ? "'" + defaultValue + "'" : defaultValue) : "");
    }
}
