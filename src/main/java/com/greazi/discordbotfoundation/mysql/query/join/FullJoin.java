package com.greazi.discordbotfoundation.mysql.query.join;

import com.greazi.discordbotfoundation.mysql.query.Select;

public class FullJoin extends Join {

    public FullJoin(Select query, String table) {
        super(query, table);
    }

    @Override
    public String getPrefix() {
        return "FULL JOIN";
    }
}
