package services;

import dao.AccountsDao;
import dao.AccountsDaoImpl;
import db_service.C3P0DataSource;
import exceptions.AccountNotFoundException;
import exceptions.DBException;
import exceptions.TransactionNotAllowedException;
import model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AccountServiceImpl implements AccountService {

    private final AccountsDao accountsDao;

    public AccountServiceImpl(AccountsDao accountsDao) {
        this.accountsDao = accountsDao;
    }

    @Override
    public Account getAccount(long accountId) throws DBException, AccountNotFoundException {
        try {
            Account result = accountsDao.get(accountId);
            if (result == null){
                throw new AccountNotFoundException("Account with id=" + accountId +
                "not found");
            }
            return result;
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public List<Account> getAllAccountsForSpecifiedId(long userId) throws DBException {
        try {
            return (accountsDao.getAllEntriesForSpecifiedId(userId));
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public void updateAmount(long accountId, BigDecimal updateValue) throws DBException, TransactionNotAllowedException, AccountNotFoundException {
        int numberOfUpdatedRows = 0;
        while (numberOfUpdatedRows == 0){
            numberOfUpdatedRows = updateAccount(accountId, updateValue);
        }
    }

    @Override
    public void createAccount(long userId, BigDecimal initialAmount) throws DBException, TransactionNotAllowedException {
        if (initialAmount.compareTo(BigDecimal.ZERO) < 0){
            throw new TransactionNotAllowedException("The initial amount of money must be positive to create account");
        }
        try (Connection connection = C3P0DataSource.getInstance().getH2Connection()) {
            try {
                connection.setAutoCommit(false);
                AccountsDao dao = new AccountsDaoImpl(connection);
                dao.createTable();
                dao.createAccount(userId, initialAmount);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new DBException(e);
            }
        } catch (SQLException e){
            throw new DBException(e);
        }
    }

    private int updateAccount(long accountId, BigDecimal updateValue) throws TransactionNotAllowedException, DBException {
        try {
            final Account accountToUpdate = accountsDao.get(accountId);

            BigDecimal calculatedValue = accountToUpdate.getAmount().add(updateValue);
            if (calculatedValue.compareTo(BigDecimal.ZERO) < 0) {
                throw new TransactionNotAllowedException("The account with id="
                        + accountId + "has not enough money to perform transaction");
            } else {
                return accountsDao.updateAmount(accountId, calculatedValue, accountToUpdate.getVersion());
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }
}
