package synchronization;

import dao.AccountsDaoImpl;
import dao.Dao;
import dao.TransactionDaoImpl;
import dao.UsersDaoImpl;
import db_service.C3P0DataSource;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ComplexExecutionChecker {
    private static final Connection conn = C3P0DataSource.getInstance().getH2Connection();

    @Test
    public void test() throws SQLException, ExecutionException, InterruptedException {
        testVersioning();
        testLoading();
        dropTables();
    }

    private static void testLoading() throws SQLException, ExecutionException, InterruptedException {
        dropTables();
        createTestTables();
        LoadTester.testTransactions();
    }

    private static void testVersioning() throws SQLException, ExecutionException, InterruptedException {
        dropTables();
        createTestTables();
        VersioningTester.testVersioning();
    }

    private static List<Dao> produceListOfDaos() throws SQLException {
        List<Dao> availableTables =  new ArrayList<>();



        availableTables.add(new UsersDaoImpl(conn));
        availableTables.add(new AccountsDaoImpl(conn));
        availableTables.add(new TransactionDaoImpl(conn));

        return availableTables;
    }

    private static void dropTables() throws SQLException {
        List<Dao> availableTables = produceListOfDaos();

        for (Dao availableTable : availableTables) {
            availableTable.dropTable();
        }
    }

    private static void createTestTables() throws SQLException {
        List<Dao> availableTables = produceListOfDaos();

        for (Dao availableTable : availableTables) {
            availableTable.createTable();
        }

        UsersDaoImpl usersDao = new UsersDaoImpl(conn);
        usersDao.createUser("Paha Zdor");
        usersDao.createUser("Miha Levashov");
        usersDao.createUser("Griga Pavlov");
    }
}
