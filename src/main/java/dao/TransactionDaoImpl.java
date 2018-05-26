package dao;

import db_service.Executor;
import model.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDaoImpl implements TransactionsDao {
    private static final Logger logger = LogManager.getLogger("C3PODataSource");
    private final Executor executor;

    public TransactionDaoImpl(Connection conn) {
        executor = new Executor(conn);
    }

    @Override
    public Transaction get(long id) throws SQLException {
        final String query = "select * from transactions where transaction_id=?";
        return executor.executeQuery(query, result -> {
            result.next();
            return new Transaction(result.getLong("transaction_id"),
                    result.getBigDecimal("amount"),
                    result.getLong("from_account_id"),
                    result.getLong("to_account_id"));
        }, id);
    }

    @Override
    public List<Transaction> getAllEntries() throws SQLException {
        return executor.executeQuery("select * from transactions", result -> {
            ArrayList<Transaction> transactions = new ArrayList<>();
            while (result.next()){
                Transaction transaction = new Transaction(result.getLong("transaction_id"),
                        result.getBigDecimal("amount"),
                        result.getLong("from_account_id"),
                        result.getLong("to_account_id"));
                transactions.add(transaction);
            }
            return transactions;
        });
    }

    public void commitTransaction(BigDecimal amount, long fromAccountId, long toAccountId) throws SQLException {
        executor.executeUpdate("insert into transactions " +
                "(amount,from_account_id, to_account_id) values (?,?,?)",
                amount, fromAccountId, toAccountId);
    }

    @Override
    public void createTable() throws SQLException {
        executor.executeUpdate("create table if not exists transactions (" +
        "transaction_id bigint auto_increment," +
        "amount decimal," +
        "from_account_id bigint," +
        "to_account_id bigint," +
        "primary key (transaction_id)," +
        "foreign key (from_account_id) references accounts(account_id)," +
        "foreign key (to_account_id) references accounts(account_id))");
        logger.info("Table transactions (" +
                "transaction_id bigint primary key," +
                "amount decimal," +
                "from_account_id bigint foreign key references accounts(account_id)," +
                "to_account_id bigint foreign key references accounts(account_id)," +
                "successfully created");
    }

    @Override
    public void dropTable() throws SQLException {
        executor.executeUpdate("drop table if exists transactions");
        logger.info("Table transactions no longer exists");
    }
}
