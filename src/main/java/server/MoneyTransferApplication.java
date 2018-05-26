package server;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import server_resources.*;

public class MoneyTransferApplication extends Application {
    @Override
    public synchronized Restlet createInboundRoot() {
        Router router = new Router(getContext());
        router.attach("/users", AllUsersResource.class);
        router.attach("/users/{user_id}", SingleUserResource.class);
        router.attach("/users/{user_id}/accounts", AllAccountsResource.class);
        router.attach("/users/{user_id}/accounts/{account_id}", AccountResource.class);
        router.attach("/transactions", AllTransactionsResource.class);

        return router;
    }
}
