package com.greazi.discordbotfoundation.mysql.query;

import com.greazi.discordbotfoundation.mysql.table.ITable;

public class Delete extends ExecuteUpdate<Delete> {

    public Delete(ITable table) {
        super(table);
    }

    @Override
    public String toQuery() {
        return "DELETE FROM " + this.table + (this.where != null ? " WHERE " + this.where : "") +
                (this.limit > 0 ? " LIMIT " + this.limit : "").trim();
    }
}
