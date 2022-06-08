package com.greazi.discordbotfoundation.mysql.query.join;

import com.greazi.discordbotfoundation.mysql.query.Select;

public class RightJoin extends Join {

    public RightJoin(Select query, String table) {
        super(query, table);
    }

    @Override
    public String getPrefix() {
        return "RIGHT JOIN";
    }
}
