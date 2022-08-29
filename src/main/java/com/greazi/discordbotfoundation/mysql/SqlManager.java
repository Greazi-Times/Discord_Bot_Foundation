package com.greazi.discordbotfoundation.mysql;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class SqlManager {

    private static HikariDataSource dataSource;
    private DSLContext dslContext = null;
    private final String host = SimpleSettings.Database.Host();
    private final String db = SimpleSettings.Database.Database();
    private final String url = "jdbc:mysql://"+host+"/"+db;
    private final String userName = SimpleSettings.Database.Username();
    private final String password = SimpleSettings.Database.Password();

		Debugger.debug("Database", url + " " + userName + " " + password + " || " + SimpleSettings.Database.Link());

        final HikariConfig config = new HikariConfig();
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(15);
        config.setJdbcUrl(url);
        config.setUsername(userName);
        config.setPassword(password);

        try{
            dataSource = new HikariDataSource(config);
            dslContext = DSL.using(dataSource, SQLDialect.MYSQL);
            Common.log("MYSQL system Started!");
        }catch (Exception e){
            Debugger.printStackTrace(e);
        }

        if (!dataSource.isClosed()) {
            Common.log("Successfully connected to MySQL database!");
        } else {
            Common.log("Failed to connect to MySQL database!");
        }
    }

			Debugger.debug("Database", "Database information; " + tempConn.getClientInfo());
		} catch (final Exception e) {
			Common.throwError(e, "Error while getting the connection to the database");
		}
	}

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    public DSLContext getDslContext() {
        return dslContext;
    }

}
