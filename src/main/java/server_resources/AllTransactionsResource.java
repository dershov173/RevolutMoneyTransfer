package server_resources;

import dao.AccountsDaoImpl;
import dao.TransactionDaoImpl;
import db_service.C3P0DataSource;
import exceptions.AccountNotFoundException;
import exceptions.DBException;
import exceptions.TransactionNotAllowedException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import services.TransactionService;
import services.TransactionServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;

public class AllTransactionsResource extends ServerResource {
    private static final Connection conn = C3P0DataSource.getInstance().getH2Connection();
    private final TransactionService service;

    public AllTransactionsResource() {
        service = new TransactionServiceImpl(new TransactionDaoImpl(conn), new AccountsDaoImpl(conn));
    }

    @Post
    @Override
    public Representation post(Representation data) {
        try {
            JsonRepresentation rep = new JsonRepresentation(data);
            JSONObject transaction = rep.getJsonObject();
            BigDecimal amount = new BigDecimal(transaction.getDouble("amount"));
            long fromAccountId = transaction.getLong("from_account_id");
            long toAccountId = transaction.getLong("to_account_id");
            String msg;
            try {
                service.commitTransaction(amount, fromAccountId, toAccountId);
                setStatus(Status.SUCCESS_OK);
                msg = "The task of transfer money successfully completed";
            } catch (TransactionNotAllowedException | AccountNotFoundException e){
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                msg = e.getMessage();
            }

            return new StringRepresentation(msg, MediaType.TEXT_PLAIN);

        } catch (JSONException | IOException | DBException e) {
            setStatus(Status.SERVER_ERROR_INTERNAL);
            throw new RuntimeException(e);
        }
    }

    @Get
    @Override
    public Representation get() {
        JSONArray response = null;
        try {
            response = new JSONArray(service.getAllTransactions());
        } catch (DBException e) {
            e.printStackTrace();
        }
        return new JsonRepresentation(response);
    }
}
