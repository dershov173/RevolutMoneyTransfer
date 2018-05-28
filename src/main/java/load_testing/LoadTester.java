package load_testing;


import dao.AccountsDaoImpl;
import dao.TransactionDaoImpl;
import db_service.C3P0DataSource;
import exceptions.TransactionNotAllowedException;
import services.TransactionService;
import services.TransactionServiceImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class LoadTester {
    private static final long NUM_EXECUTORS = 1000;
    private static final Connection conn = C3P0DataSource.getInstance().getH2Connection();
    private static final long sumOfCounters = NUM_EXECUTORS*(NUM_EXECUTORS-1)/2;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool((int) NUM_EXECUTORS);

        List<Future<Long>> results = executorService.invokeAll(formCollectionOfOperations());
        long counters = 0;
        for(Future<Long> result : results){
            counters +=result.get();
        }
        executorService.shutdown();
        assert counters == sumOfCounters;
        System.out.println("main ends");

    }

    private static List<Callable<Long>> formCollectionOfOperations(){
        List <Callable<Long>> operations = new ArrayList<>();
        for (int i = 0; i < NUM_EXECUTORS; i++) {
            operations.add(() ->{
                final AccountsDaoImpl accountsDao = new AccountsDaoImpl(conn);
                TransactionService transactionService = new TransactionServiceImpl(
                        new TransactionDaoImpl(conn),
                        accountsDao);

                long threadId = Thread.currentThread().getId();
                long fromAccountId = threadId % 2 == 0? 1:2;
                long toAccountId = threadId % 2 == 0? 2:1;
                BigDecimal valueToTransfer = new BigDecimal(10);
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
