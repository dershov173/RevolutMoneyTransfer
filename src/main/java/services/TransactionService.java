package services;

import exceptions.AccountNotFoundException;
import exceptions.DBException;
import exceptions.TransactionNotAllowedException;
import model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    Transaction getTransaction(long transactionId) throws DBException;
    List<Transaction> getAllTransactions() throws DBException;
    void commitTransaction(BigDecimal amount, long fromAccountId, long toAccountId) throws DBException, AccountNotFoundException, TransactionNotAllowedException;
}
