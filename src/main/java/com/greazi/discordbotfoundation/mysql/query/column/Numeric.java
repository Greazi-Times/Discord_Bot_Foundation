package com.greazi.discordbotfoundation.mysql.query.column;

import com.greazi.discordbotfoundation.mysql.query.action.IAction;

public abstract class Numeric extends Column {

    protected java.lang.Boolean unsigned;

    protected java.lang.Integer length;

    protected java.lang.Boolean primary = false;

    protected java.lang.Boolean unique = false;

    protected java.lang.Boolean increment = false;

    protected java.lang.Integer defaultValue;

    public Numeric(IAction<IColumn> action, java.lang.String name, java.lang.Boolean nullable, java.lang.Boolean unsigned, java.lang.Integer length) {
        super(action, name, nullable);

        this.unsigned = unsigned;

        this.length = length;
    }

    /**
     * Enable UNSIGNED.
     * @return  Column object.
     */
    public IColumn unsigned() {
        this.unsigned = true;

        return this;
    }

    /**
     * Enable PRIMARY KEY.
     * @return Column object.
     */
    public Numeric primary() {
        this.primary = true;

        return this;
    }

    /**
     * Enable UNIQUE KEY.
     * @return Column object.
     */
    public Numeric unique() {
        this.unique = true;

        return this;
    }

    /**
     * Enable AUTO INCREMENT.
     * @return Column object.
     */
    public Numeric increment() {
        this.increment = true;

        return this;
    }

    /**
     * Set default value.
     * @param value Default value.
     * @return Column object.
     */
    public Numeric defaultValue(java.lang.Integer value) {
        this.defaultValue = value;

        return this;
    }
}
