package services;

import dao.AccountsDao;
import dao.AccountsDaoImpl;
import db_service.C3P0DataSource;
import exceptions.DBException;
import model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AccountServiceImpl implements AccountService {

    private static final Connection conn = C3P0DataSource.getInstance().getH2Connection();

    @Override
    public Account getAccount(long accountId) throws DBException {
        try {
            return (new AccountsDaoImpl(conn).get(accountId));
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public List<Account> getAllAccounts() throws DBException {
        try {
            return (new AccountsDaoImpl(conn).getAllEntries());
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public boolean updateAmount(long accountId, BigDecimal updateValue) throws DBException { ;
        AccountsDao dao = new AccountsDaoImpl(conn);
        try {
            final Account accountToUpdate = dao.get(accountId);
            if (accountToUpdate == null) {
                return false;
            }
            synchronized (accountToUpdate){
                try (Connection connection = C3P0DataSource.getInstance().getH2Connection()) {
                    BigDecimal calculatedValue = accountToUpdate.getAmount().add(updateValue);
                    if (calculatedValue.compareTo(BigDecimal.ZERO) < 0) {
                        return false;
                    } else {
                        new AccountsDaoImpl(connection).updateAmount(accountId, calculatedValue);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
        return true;
    }

    @Override
    public long createAccount(long userId, BigDecimal initialAmount) throws DBException {
        try (Connection connection = C3P0DataSource.getInstance().getH2Connection()) {
            try {
                connection.setAutoCommit(false);
                AccountsDao dao = new AccountsDaoImpl(connection);
                dao.createTable();
                dao.createAccount(userId, initialAmount);
                connection.commit();
                return dao.getAccountId(userId);
            } catch (SQLException e) {
                connection.rollback();
                throw new DBException(e);
            }
        } catch (SQLException e){
            throw new DBException(e);
        }
    }


}
