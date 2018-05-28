package db_service;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Creates jdbc connection pool
 * Using one connection for each entity to maintain all read-only queries
 * To support transactions it is strongly recommended to create new instance of connection
 */
public class C3P0DataSource {
    private static final Logger logger = LogManager.getLogger("C3PODataSource");
    private static C3P0DataSource dataSource;
    private ComboPooledDataSource comboPooledDataSource;

    private C3P0DataSource() {
        try {
            Class.forName("org.h2.Driver");
            comboPooledDataSource = new ComboPooledDataSource();
            comboPooledDataSource
                    .setDriverClass("org.h2.Driver");
            comboPooledDataSource
                    .setJdbcUrl("jdbc:h2:./h2db;LOCK_TIMEOUT=10000;LOCK_MODE=3");
            comboPooledDataSource.setUser("dershov");
            comboPooledDataSource.setPassword("");
            logger.info("Datasource to jbdc url = jdbc:h2:./h2db successfully established");
        } catch (PropertyVetoException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized C3P0DataSource getInstance() {
        if (dataSource == null)
            dataSource = new C3P0DataSource();
        return dataSource;
    }

    public Connection getH2Connection () {
        Connection conn = null;
        try {
            conn = comboPooledDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.info("New connection" + conn + " to database was created");
        return conn;
    }
}
