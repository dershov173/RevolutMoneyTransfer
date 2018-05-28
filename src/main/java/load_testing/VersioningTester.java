package load_testing;

import dao.AccountsDao;
import dao.AccountsDaoImpl;
import db_service.C3P0DataSource;
import model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class VersioningTester {
    private static final int NUM_EXECUTORS = 200;
    private static final Connection conn = C3P0DataSource.getInstance().getH2Connection();

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_EXECUTORS);


        List<Future<Integer>> results = executorService.invokeAll(formCollectionOfOperations());
        for(Future<Integer> result : results){
            result.get();
        }
        executorService.shutdown();
        System.out.println("main ends");

        AccountsDao accountsDao = new AccountsDaoImpl(conn);
//        accountsDao.createAccount(3, new BigDecimal(100));
//        accountsDao.updateAmount(3, new BigDecimal(150), 2);

        //System.out.println(accountsDao.get(3).getVersion());
    }

    private static List<Callable<Integer>> formCollectionOfOperations() throws SQLException {
        List <Callable<Integer>> operations = new ArrayList<>();
        for (int i = 0; i < NUM_EXECUTORS; i++){
            operations.add(() -> {
                try (Connection connection = C3P0DataSource.getInstance().getH2Connection()){
                    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                    AccountsDao accountsDao = new AccountsDaoImpl(connection);



                    int updatedRows = 0;
                    while (updatedRows == 0){
                        Account accountToUpdate = accountsDao.get(3);
                        updatedRows = accountsDao.updateAmount(3, new BigDecimal(10), accountToUpdate.getVersion());
                        System.out.println(accountToUpdate.getVersion());
                    }
                    return accountsDao.get(3).getVersion();
                }
            });
        }

        return operations;
    }

}
