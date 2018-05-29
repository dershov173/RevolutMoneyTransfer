package load_testing;

import dao.AccountsDao;
import dao.AccountsDaoImpl;
import db_service.C3P0DataSource;
import services.AccountService;
import services.AccountServiceImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class VersioningTester {
    private static final int NUM_EXECUTORS = 10000;
    private static final Connection conn = C3P0DataSource.getInstance().getH2Connection();
    public static final int ACCOUNT_ID = 1;

    public static void testVersioning() throws SQLException, ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_EXECUTORS);
        AccountsDao accountsDao = new AccountsDaoImpl(conn);
        accountsDao.createAccount(ACCOUNT_ID, new BigDecimal(10));
        final BigDecimal oldBalance = accountsDao.get(ACCOUNT_ID).getAmount();
        System.out.println(oldBalance);

        List<Future<Integer>> results = executorService.invokeAll(formCollectionOfOperations());
        for(Future<Integer> result : results){
            result.get();
        }
        executorService.shutdown();

        final BigDecimal newBalance = accountsDao.get(ACCOUNT_ID).getAmount();
        System.out.println(newBalance);

        assert newBalance.equals(oldBalance.add(new BigDecimal(NUM_EXECUTORS)));
        System.out.println("versioning successful");
//        accountsDao.createAccount(3, new BigDecimal(100));
//        accountsDao.updateAmount(3, new BigDecimal(150), 2);

    }

    private static List<Callable<Integer>> formCollectionOfOperations() throws SQLException {
        List <Callable<Integer>> operations = new ArrayList<>();
        for (int i = 0; i < NUM_EXECUTORS; i++){
            operations.add(() -> {
                try (Connection conn = C3P0DataSource.getInstance().getH2Connection()) {
                    final AccountService service = new AccountServiceImpl(new AccountsDaoImpl(conn));
                    service.updateAmount(ACCOUNT_ID, new BigDecimal(1));
                    return 0;
                }
            });
        }

        return operations;
    }

}
