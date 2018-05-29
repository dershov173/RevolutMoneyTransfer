package load_testing;


import dao.AccountsDaoImpl;
import dao.TransactionDaoImpl;
import db_service.C3P0DataSource;
import exceptions.TransactionNotAllowedException;
import services.TransactionService;
import services.TransactionServiceImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class LoadTester {
    private static final long NUM_EXECUTORS = 2000;
    private static final Connection conn = C3P0DataSource.getInstance().getH2Connection();
    private static final long firstAccountId = 1;
    private static final long secondAccountId = 2;

    public static void main(String[] args) throws InterruptedException, ExecutionException, SQLException {
        final int oldVersion1 = new AccountsDaoImpl(conn).get(firstAccountId).getVersion();
        final int oldVersion2 = new AccountsDaoImpl(conn).get(secondAccountId).getVersion();

        ExecutorService executorService = Executors.newFixedThreadPool((int) NUM_EXECUTORS);


        List<Future<Long>> results = executorService.invokeAll(formCollectionOfOperations());
        for(Future<Long> result : results){
            result.get();
        }
        executorService.shutdown();
        final int newVersion1 = new AccountsDaoImpl(conn).get(firstAccountId).getVersion();
        final int newVersion2 = new AccountsDaoImpl(conn).get(secondAccountId).getVersion();

        assert newVersion1 == oldVersion1 + NUM_EXECUTORS;
        assert newVersion2 == oldVersion2 + NUM_EXECUTORS;
        System.out.println("main ends");

    }

    private static List<Callable<Long>> formCollectionOfOperations(){
        List <Callable<Long>> operations = new ArrayList<>();
        for (int i = 0; i < NUM_EXECUTORS; i++) {
            operations.add(() ->{
                final AccountsDaoImpl accountsDao = new AccountsDaoImpl(conn);
                TransactionService transactionService = new TransactionServiceImpl(
                        new TransactionDaoImpl(conn));

                long threadId = Thread.currentThread().getId();
                long fromAccountId = threadId % 2 == 0? firstAccountId:secondAccountId;
                long toAccountId = 1;//threadId % 2 == 0? secondAccountId:firstAccountId;

                BigDecimal valueToTransfer = new BigDecimal(1);
                try {
                    transactionService.commitTransaction(valueToTransfer, fromAccountId, toAccountId);
                } catch (TransactionNotAllowedException e){
                    System.out.println(e.getMessage());
                }
                BigDecimal amount = accountsDao.get(1).getAmount();
                assert amount.compareTo(BigDecimal.ZERO) > 0;
                return threadId % NUM_EXECUTORS;
            });
        }

        return operations;
    }
}
