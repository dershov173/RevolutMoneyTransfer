package services;

import dao.AccountsDao;
import dao.TransactionsDao;
import exceptions.AccountNotFoundException;
import exceptions.DBException;
import exceptions.TransactionNotAllowedException;
import model.Account;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionServiceImplTest {

    @Test(expected = TransactionNotAllowedException.class)
    public void testNegativeAmount() throws DBException, TransactionNotAllowedException, AccountNotFoundException, SQLException {
        AccountsDao accountsDao = mock(AccountsDao.class);
        TransactionsDao transactionsDao = mock(TransactionsDao.class);

        TransactionServiceImpl service = new TransactionServiceImpl(transactionsDao, accountsDao);
        when(accountsDao.get(1)).thenReturn(new Account(1, 1, new BigDecimal(0), 1));
        when(accountsDao.get(2)).thenReturn(new Account(2, 2, new BigDecimal(200), 1));

        service.commitTransaction(new BigDecimal(-100), 1, 2);

    }

    @Test(expected = TransactionNotAllowedException.class)
    public void notEnoughMoneyToTransfer() throws AccountNotFoundException, SQLException, DBException, TransactionNotAllowedException {
        AccountsDao accountsDao = mock(AccountsDao.class);
        TransactionsDao transactionsDao = mock(TransactionsDao.class);

        TransactionServiceImpl service = new TransactionServiceImpl(transactionsDao, accountsDao);
        when(accountsDao.get(1)).thenReturn(new Account(1, 1, new BigDecimal(0), 1));
        when(accountsDao.get(2)).thenReturn(new Account(2, 2, new BigDecimal(200), 1));

        service.commitTransaction(new BigDecimal(100), 1, 2);

    }

    @Test(expected = TransactionNotAllowedException.class)
    public void transferBetweenSameAccounts() throws AccountNotFoundException, SQLException, DBException, TransactionNotAllowedException {
        AccountsDao accountsDao = mock(AccountsDao.class);
        TransactionsDao transactionsDao = mock(TransactionsDao.class);

        TransactionServiceImpl service = new TransactionServiceImpl(transactionsDao, accountsDao);
        when(accountsDao.get(1)).thenReturn(new Account(1, 1, new BigDecimal(1000), 1));

        service.commitTransaction(new BigDecimal(100), 1, 1);
    }

}