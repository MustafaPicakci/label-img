package com.application.db;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.sqlite.SQLiteConfig;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DB {
  private static final Logger logger = Logger.getLogger(DB.class);
  private static HikariDataSource datasource;

  public static Connection connect() throws SQLException, PropertyVetoException {
    SQLiteConfig config = new SQLiteConfig();
    config.enforceForeignKeys(true);
    Connection conn = null;
    try {

      conn = getDataSource().getConnection();

      return conn;
    } catch (Exception e) {
      logger.error("Sqlite connection failed", e);
    }
    return null;
  }

  public static DataSource getDataSource() {

    if (datasource == null) {
      HikariConfig config = new HikariConfig();

      config.setJdbcUrl("jdbc:sqlite:file:./resources/db/imgdb.db");
      config.setMaximumPoolSize(100);
      config.setAutoCommit(true);
      config.setConnectionTimeout(20000);
      config.setIdleTimeout(300000);
      config.setMinimumIdle(5);
      config.setMaxLifetime(1200000);
      config.setConnectionInitSql("PRAGMA foreign_keys=1");
      datasource = new HikariDataSource(config);
    }
    return datasource;
  }

  public static void setup() {
    List<String> creates =
        Arrays.asList(
            "CREATE TABLE if not exists images      (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, image BLOB NOT NULL UNIQUE, directory TEXT NOT NULL, name TEXT NOT NULL );",
            "CREATE TABLE if not exists tags        (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE , tags TEXT NOT NULL UNIQUE);",
            "Create Table if not exists directories (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, directory TEXT NOT NULL UNIQUE, is_active INTEGER DEFAULT 0 NOT NULL);",
            "CREATE TABLE if not exists image_tags  (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, record_id INTEGER NOT NULL, tag_id INTEGER NOT NULL,FOREIGN KEY(record_id) REFERENCES images(id) ON DELETE CASCADE, FOREIGN KEY(tag_id) REFERENCES tags(id) ON DELETE CASCADE );",
            "VACUUM main");

    try (Connection con = connect();
        Statement stmt = con.createStatement()) {
      creates.forEach(
          sql -> {
            try {
              stmt.execute(sql);
            } catch (Exception e) {
              logger.error("Db setup failed", e);
            }
          });
      con.close();
    } catch (Exception e) {
      logger.error("Db setup failed", e);
    }
  }
}
