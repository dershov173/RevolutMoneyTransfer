package dao;

import model.Account;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface AccountsDao extends Dao<Account> {
    Account get(long accountId) throws  SQLException;
    @Override
    List<Account> getAllEntries() throws SQLException;
    @Override
    void createTable() throws SQLException;
    @Override
    void dropTable() throws SQLException;

    Account getVersioningAccount(long accountId, int version) throws SQLException;

    int updateAmount(long accountId, BigDecimal newValue, int version) throws SQLException;
    void createAccount(long userId, BigDecimal amount) throws  SQLException;
}
