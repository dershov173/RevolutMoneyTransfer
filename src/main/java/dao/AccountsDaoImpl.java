package dao;

import db_service.Executor;
import model.Account;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountsDaoImpl  implements AccountsDao{
    private static final Logger logger = LogManager.getLogger("AccountsDaoImpl");
    private Executor executor;

    public AccountsDaoImpl(Connection conn) {
        this.executor = new Executor(conn);
    }

    @Override
    public Account get(long accountId) throws SQLException {
        final String query = "select * from accounts where account_id=?";
        return executor.executeQuery(query, result -> {
            if (result.next()) {
                return new Account(result.getLong("account_id"),
                        result.getLong("user_id"),
                        result.getBigDecimal("amount"),
                        result.getInt("version"));
            } else {
                return null;
            }
        }, accountId);
    }

    @Override
    public List<Account> getAllEntriesForSpecifiedId(long userId) throws SQLException {
        return executor.executeQuery("select * from accounts" +
                " where user_id = ?", result -> {
            ArrayList<Account> accounts = new ArrayList<>();
            while (result.next()) {
                Account account = new Account(result.getLong("account_id"),
                        result.getLong("user_id"),
                        result.getBigDecimal("amount"),
                        result.getInt("version"));
                accounts.add(account);
            }
            return accounts;
        }, userId);
    }

    @Override
    public int updateAmount(long accountId, BigDecimal newValue, int version) throws SQLException {
        return executor.executeUpdate("update accounts set amount=?, version=version+1" +
                " where account_id =? and version =?", newValue, accountId, version);
    }

    @Override
    public void createTable() throws SQLException {
        executor.executeUpdate("create table if not exists accounts (" +
                "account_id bigint auto_increment, " +
                "amount decimal, " +
                "user_id bigint not null, " +
                "version integer, " +
                "primary key(account_id), " +
                "foreign key(user_id) references users(user_id))");
        logger.info("Table accounts (account_id bigint primary key, " +
                "amount decimal, " +
                "user_id bigint) foreign key references users(user_id) successfully created");
    }

    @Override
    public void createAccount(long userId, BigDecimal amount) throws  SQLException{
        executor.executeUpdate("insert into accounts (user_id, amount, version) values (?,?,1)", userId, amount);
    }

    @Override
    public void dropTable() throws SQLException{
        executor.executeUpdate("drop table if exists accounts");
        logger.info("Table accounts no longer exists");
    }

}
