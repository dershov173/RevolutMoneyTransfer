package services;

import exceptions.DBException;
import model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    Account getAccount(long accountId) throws DBException;
    List<Account> getAllAccounts() throws DBException;
    boolean updateAmount(long accountId, BigDecimal updateValue) throws DBException;
    long createAccount(long userId, BigDecimal initialAmount) throws DBException;
}
