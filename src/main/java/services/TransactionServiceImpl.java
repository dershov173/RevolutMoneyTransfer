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
import java.util.Optional;

import static java.sql.Connection.TRANSACTION_SERIALIZABLE;

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

        if (amount.compareTo(BigDecimal.ZERO) < 0){
            throw new TransactionNotAllowedException("Only positive amounts of money can be transferred");
        }

        transferMoney(fromAccountId, toAccountId, amount);

    }

    private void transferMoney(long fromAccountId, long toAccountId, BigDecimal amount) throws TransactionNotAllowedException, AccountNotFoundException, DBException {
        try(Connection connection = C3P0DataSource.getInstance().getH2Connection()) {
            connection.setTransactionIsolation(TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);

            final AccountService accountService = new AccountServiceImpl(new AccountsDaoImpl(connection));
            final TransactionDaoImpl transactionDao = new TransactionDaoImpl(connection);

            Account fromAccount = accountsDao.get(fromAccountId);
            Account toAccount = accountsDao.get(toAccountId);

            accountService.updateAmount(fromAccountId, fromAccount.getAmount().add(amount.negate()));
            accountService.updateAmount(toAccountId, toAccount.getAmount().add(amount));


            transactionDao.createTable();
            transactionDao.commitTransaction(amount, fromAccountId, toAccountId);
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }
}
