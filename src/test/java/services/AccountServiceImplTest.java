package services;

import dao.AccountsDao;
import exceptions.AccountNotFoundException;
import exceptions.DBException;
import exceptions.TransactionNotAllowedException;
import model.Account;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountServiceImplTest {

    @Test(expected = TransactionNotAllowedException.class)
    public void testNegativeAmount() throws DBException, TransactionNotAllowedException, SQLException, AccountNotFoundException {
        AccountsDao accountsDao = mock(AccountsDao.class);
        AccountServiceImpl accountService = new AccountServiceImpl(accountsDao);

        final Account account = new Account(1, 1, new BigDecimal(100), 1);
        when(accountsDao.get(1)).thenReturn(account);
        accountService.updateAmount(1, new BigDecimal(-1000));
    }

    @Test(expected = TransactionNotAllowedException.class)
    public void testAccountCreation() throws DBException, TransactionNotAllowedException {
        AccountsDao accountsDao = mock(AccountsDao.class);
        AccountServiceImpl accountService = new AccountServiceImpl(accountsDao);

        accountService.createAccount(1, new BigDecimal(-100));

    }
}