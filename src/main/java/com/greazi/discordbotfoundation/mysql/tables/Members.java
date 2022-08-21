package com.greazi.discordbotfoundation.mysql.tables;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.objects.mysql.SqlMember;
import org.jooq.Select;

import static org.jooq.impl.SQLDataType.BIGINT;
import static org.jooq.impl.SQLDataType.VARCHAR;

import static org.jooq.impl.SQLDataType.*;
import static org.jooq.impl.DSL.*;
import org.jooq.*;
import org.jooq.impl.*;

import java.util.List;

//----------------------------------------------------------
// Docs: https://www.jooq.org/doc/3.17/manual/sql-building/
//
// WARNING:
// Do not change tables after they have been created in the database!
// Alter tables instead.
//----------------------------------------------------------

public class Members {

    public static final DSLContext create = SimpleBot.getSqlManager().getDslContext();

    public static final String TABLE_NAME = "members";

    public Members(){
        createTable();
        alterTable();
    }

    public void createTable() {
        create.createTableIfNotExists(TABLE_NAME)
                .column("id", BIGINT)
                .column("discord_id", BIGINT)
                .column("nickname", VARCHAR).execute();
    }

    public void alterTable() {
        create.alterTable(TABLE_NAME)
                .addColumn(field(name("username"), VARCHAR))
                .execute();
    }

    public void add() {

    }

    public List<SqlMember> get() {
        Select<?> select = create.select().from(TABLE_NAME).where(field("id").eq(1));
        return select.fetch().into(SqlMember.class);
    }

}
