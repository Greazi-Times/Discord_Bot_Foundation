package com.greazi.discordbotfoundation.mysql.query.column;

import com.greazi.discordbotfoundation.mysql.query.action.IAction;

public class TinyInteger extends Numeric {

    public TinyInteger(IAction<IColumn> action, java.lang.String name) {
        super(action, name, false, false, 0);
    }

    public TinyInteger(IAction<IColumn> action, java.lang.String name, java.lang.Integer length) {
        super(action, name, false, false, length);
    }

    @Override
    public java.lang.String toString() {
        return this.action.getPrefix() + name + " TINYINT" + (nullable ? " NULL" : " NOT NULL") + (unique ? " UNIQUE" : "") + (length > 0 ? "(" + length + ")" : "") + (primary ? " PRIMARY KEY" : "") + (increment ? " AUTO_INCREMENT" : "") + (defaultValue != null ? " DEFAULT " + (!nativeDefault ? "'" + defaultValue + "'" : defaultValue) : "");
    }
}
