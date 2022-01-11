package com.greazi.discordbotfoundation.mysql;

import com.greazi.discordbotfoundation.mysql.query.IQuery;
import com.greazi.discordbotfoundation.mysql.table.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SQL implements ISQL {

    protected Connection connection = null;

    protected boolean connected = false;

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public Table table(String name) {
        return new Table(this, name);
    }
    
    @Override
    public PreparedStatement prepare(IQuery query) throws SQLException {
        return this.connection.prepareStatement(query.toQuery());
    }
}
