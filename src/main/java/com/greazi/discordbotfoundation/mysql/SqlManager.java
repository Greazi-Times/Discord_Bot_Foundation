package com.greazi.discordbotfoundation.mysql;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.meta.jaxb.*;
import org.jooq.meta.jaxb.Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class SqlManager {

    private Connection conn = null;
    private DSLContext dslContext = null;

    private final String host = SimpleSettings.Database.Host();
    private final String db = SimpleSettings.Database.Database();
    private final String url = "jdbc:mysql://"+host+"/"+db;
    private final String userName = SimpleSettings.Database.Username();
    private final String password = SimpleSettings.Database.Password();

    public SqlManager(){
        if (!SimpleSettings.Database.Enabled()) {
            Common.log("MySQL system Disabled!");
            return;
        }
        Common.log("MYSQL system Enabled! Starting up MYSQL system");

        try (Connection tempConn = DriverManager.getConnection(url, userName, password)) {
            this.conn = tempConn;
            this.dslContext = DSL.using(this.conn, SQLDialect.MYSQL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (this.conn != null) {
            Common.log("Successfully connected to MySQL database!");
        } else {
            Common.log("Failed to connect to MySQL database!");
        }
    }

    public void codeGenerator(){
        new org.jooq.meta.jaxb.Configuration()
                .withJdbc(new Jdbc()
                        .withDriver("com.mysql.jdbc.Driver")
                        .withUrl(url)
                        .withUser(userName)
                        .withPassword(password)
                )
                .withGenerator(
                        new Generator()
                                .withDatabase(
                                        new Database()
                                                .withName(db)
                                                .withIncludes(".*")
                                                .withExcludes("" +
                                                        "UNUSED_TABLE                # This table (unqualified name) should not be generated" +
                                                        "| PREFIX_.*                   # Objects with a given prefix should not be generated" +
                                                        "| SECRET_SCHEMA\\.SECRET_TABLE # This table (qualified name) should not be generated" +
                                                        "| SECRET_ROUTINE              # This routine (unqualified name) ..." +
                                                        ""
                                                )
                                                .withInputSchema("[your database schema / owner / name]")
                                )
                                .withTarget(
                                        new Target()
                                                .withPackageName("com.greazi.discordbotfoundation.mysql")
                                                .withDirectory("src/main/java/com/greazi/discordbotfoundation/mysql/generated")
                                )
                );
    }

    public Connection getConnection(){
        return this.conn;
    }

    public DSLContext getDslContext() {
        return dslContext;
    }

}
