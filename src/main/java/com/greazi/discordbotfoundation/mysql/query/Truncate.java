package com.greazi.discordbotfoundation.mysql.query;

import com.greazi.discordbotfoundation.mysql.table.ITable;

public class Truncate extends ExecuteStatement<Truncate> {

    public Truncate(ITable table) {
        super(table);
    }

    @Override
    public String toQuery() {
        return "TRUNCATE TABLE " + this.table;
    }
}
