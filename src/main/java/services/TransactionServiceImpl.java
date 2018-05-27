package services;

import dao.AccountsDao;
import dao.AccountsDaoImpl;
import dao.TransactionDaoImpl;
import dao.TransactionsDao;
import db_service.C3P0DataSource;
import exceptions.AccountNotFoundException;
import exceptions.DBException;
import exceptions.TransactionNotAllowedException;
import model.Account;
import model.Transaction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TransactionServiceImpl implements TransactionService {
    private final AccountsDao accountsDao;
    private final TransactionsDao transactionsDao;

    public TransactionServiceImpl(TransactionsDao transactionsDao, AccountsDao accountsDao) {
        this.transactionsDao = transactionsDao;
        this.accountsDao = accountsDao;
    }

    @Override
    public Transaction getTransaction(long transactionId) throws DBException {
        try {
            return transactionsDao.get(transactionId);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public List<Transaction> getAllTransactions() throws DBException {
        try {
            return transactionsDao.getAllEntries();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public void commitTransaction(BigDecimal amount, long fromAccountId, long toAccountId) throws DBException, AccountNotFoundException, TransactionNotAllowedException {
        if(fromAccountId == toAccountId){
            throw new TransactionNotAllowedException("You cannot transfer money between the same accounts");
        }

        try {
            Account fromAccount = accountsDao.get(fromAccountId);
            Account toAccount = accountsDao.get(toAccountId);
            if (fromAccount == null || toAccount == null){
                throw new AccountNotFoundException("Account not found");
            }

            final BigDecimal calculatedValue = fromAccount.getAmount().add(amount.negate());

            if (amount.compareTo(BigDecimal.ZERO) < 0){
                throw new TransactionNotAllowedException("Only positive amounts of money can be transferred");
            }

            if (calculatedValue.compareTo(BigDecimal.ZERO) < 0){
                throw new TransactionNotAllowedException("Sender with id = " + fromAccountId +
                        "has not enough money to transfer");
            }

            transferMoney(fromAccount, toAccount, amount);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private void transferMoney(Account fromAccount, Account toAccount, BigDecimal amount) throws SQLException {
        try(Connection connection = C3P0DataSource.getInstance().getH2Connection()) {
            connection.setAutoCommit(false);
            long fromAccountAccountId = fromAccount.getAccountId();
            long toAccountAccountId = toAccount.getAccountId();

            final AccountsDao accountsDao = new AccountsDaoImpl(connection);
            final TransactionDaoImpl transactionDao = new TransactionDaoImpl(connection);

            accountsDao.updateAmount(fromAccountAccountId, fromAccount.getAmount().add(amount.negate()));
            accountsDao.updateAmount(toAccountAccountId, toAccount.getAmount().add(amount));
            transactionDao.createTable();
            transactionDao.commitTransaction(amount, fromAccountAccountId, toAccountAccountId);
            connection.setAutoCommit(true);
        }
    }
}
