package com.greazi.discordbotfoundation.mysql.query.column;

import com.greazi.discordbotfoundation.mysql.query.action.IAction;

public class Date extends Column {

    protected java.lang.String defaultValue;

    public Date(IAction<IColumn> action, java.lang.String name) {
        super(action, name, false);
    }

    /**
     * Set default value.
     * @param value Default value.
     * @return Column object.
     */
    public Date defaultValue(java.lang.String value) {
        this.defaultValue = value;

        return this;
    }

    @Override
    public java.lang.String toString() {
        return this.action.getPrefix() + name + " DATE" + (nullable ? " " : " NOT NULL ") + (defaultValue != null ? " DEFAULT " + (!nativeDefault ? "'" + defaultValue + "'" : defaultValue) : "");
    }
}
