package com.greazi.discordbotfoundation.mysql.query.column;

import com.greazi.discordbotfoundation.mysql.query.action.IAction;

public class Boolean extends Column {

    protected java.lang.String defaultValue;

    public Boolean(IAction<IColumn> action, java.lang.String name) {
        super(action, name, false);
    }

    /**
     * Set default value.
     * @param value Default value.
     * @return Column object.
     */
    public Boolean defaultValue(java.lang.String value) {
        this.defaultValue = value;

        return this;
    }

    @Override
    public java.lang.String toString() {
        return this.action.getPrefix() + name + " BOOLEAN" + (nullable ? " " : " NOT NULL ") + (defaultValue != null ? " DEFAULT " + (!nativeDefault ? "'" + defaultValue + "'" : defaultValue) : "");
    }
}
