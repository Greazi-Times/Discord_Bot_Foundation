package com.greazi.discordbotfoundation.mysql.query;

import com.greazi.discordbotfoundation.mysql.table.ITable;

public class Drop extends ExecuteStatement<Drop> {

    public Drop(ITable table) {
        super(table);
    }

    @Override
    public String toQuery() {
        return "DROP TABLE " + this.table;
    }
}
