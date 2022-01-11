package com.greazi.discordbotfoundation.mysql.query.join;

import com.greazi.discordbotfoundation.mysql.query.Select;

public class InnerJoin extends Join {

    public InnerJoin(Select query, String table) {
        super(query, table);
    }

    @Override
    public String getPrefix() {
        return "INNER JOIN";
    }
}
