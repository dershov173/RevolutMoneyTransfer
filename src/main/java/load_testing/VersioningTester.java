package load_testing;

import dao.AccountsDao;
import dao.AccountsDaoImpl;
import db_service.C3P0DataSource;
import model.Account;
import services.AccountService;
import services.AccountServiceImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class VersioningTester {
    private static final int NUM_EXECUTORS = 200;
    private static final Connection conn = C3P0DataSource.getInstance().getH2Connection();
    public static final int ACCOUNT_ID = 3;

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_EXECUTORS);
        AccountsDao accountsDao = new AccountsDaoImpl(conn);
        final int oldVersion = accountsDao.get(ACCOUNT_ID).getVersion();

        List<Future<Integer>> results = executorService.invokeAll(formCollectionOfOperations());
        for(Future<Integer> result : results){
            result.get();
        }
        executorService.shutdown();


        final int newVersion = accountsDao.get(ACCOUNT_ID).getVersion();
        assert newVersion == oldVersion+NUM_EXECUTORS;
        System.out.println("main ends");
//        accountsDao.createAccount(3, new BigDecimal(100));
//        accountsDao.updateAmount(3, new BigDecimal(150), 2);

    }

    private static List<Callable<Integer>> formCollectionOfOperations() throws SQLException {
        List <Callable<Integer>> operations = new ArrayList<>();
        final AccountService service = new AccountServiceImpl(new AccountsDaoImpl(conn));
        for (int i = 0; i < NUM_EXECUTORS; i++){
            operations.add(() -> {
                System.out.println(service.getAccount(ACCOUNT_ID).getVersion());
                service.updateAmount(ACCOUNT_ID, new BigDecimal(10));
                return 0;
            });
        }

        return operations;
    }

}
