package com.greazi.discordbotfoundation.mysql.query.column;

import com.greazi.discordbotfoundation.mysql.query.action.IAction;

public class LongText extends Column {

    protected java.lang.String defaultValue;

    public LongText(IAction<IColumn> action, java.lang.String name) {
        super(action, name, false);
    }

    /**
     * Set default value.
     * @param value Default value.
     * @return Column object.
     */
    public LongText defaultValue(java.lang.String value) {
        this.defaultValue = value;

        return this;
    }

    @Override
    public java.lang.String toString() {
        return this.action.getPrefix() + name + " LONGTEXT" + (nullable ? " " : " NOT NULL ") + (defaultValue != null ? " DEFAULT " + (!nativeDefault ? "'" + defaultValue + "'" : defaultValue) : "");
    }
}
