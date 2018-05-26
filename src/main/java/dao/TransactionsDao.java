package dao;

import model.Transaction;

import java.sql.SQLException;
import java.util.List;

public interface TransactionsDao extends Dao<Transaction> {
    Transaction get(long id) throws SQLException;
    @Override
    List<Transaction> getAllEntries() throws SQLException;
    @Override
    void createTable() throws SQLException;
    @Override
    void dropTable() throws SQLException;

}
