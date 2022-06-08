package com.greazi.discordbotfoundation.mysql.query;

import com.greazi.discordbotfoundation.mysql.table.ITable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class ExecuteStatement<T extends IQuery> extends Query<T> {

    public ExecuteStatement(ITable table) {
        super(table);
    }

    @Override
    public PreparedStatement executePrepare() throws SQLException {
        this.execute();

        return prepare;
    }

    /**
     * Execute query.
     * @return A boolean.
     * @throws SQLException Exception when something goes wrong.
     */
    public Boolean execute() throws SQLException {
        return this.executeStatement();
    }
}
