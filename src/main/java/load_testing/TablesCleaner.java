package load_testing;

import dao.AccountsDaoImpl;
import dao.TransactionDaoImpl;
import dao.UsersDaoImpl;
import db_service.C3P0DataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class TablesCleaner {
    private static final Connection conn = C3P0DataSource.getInstance().getH2Connection();

    public static void main(String[] args) throws SQLException {
        new AccountsDaoImpl(conn).dropTable();
        new UsersDaoImpl(conn).dropTable();
        new TransactionDaoImpl(conn).dropTable();
    }
}
