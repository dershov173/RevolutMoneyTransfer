package server_resources;

import dao.AccountsDaoImpl;
import db_service.C3P0DataSource;
import exceptions.DBException;
import exceptions.TransactionNotAllowedException;
import org.json.JSONArray;
import org.json.JSONException;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import services.AccountService;
import services.AccountServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;

public class AllAccountsResource extends ServerResource {

    private final AccountService service;
    private static final Connection conn = C3P0DataSource.getInstance().getH2Connection();

    public AllAccountsResource() {
        service = new AccountServiceImpl(new AccountsDaoImpl(conn));
    }

    @Post
    @Override
    public Representation post(Representation data){
        String msg;
        long userId = Long.parseLong(getAttribute("user_id"));
        try {
            JsonRepresentation rep = new JsonRepresentation(data);
            BigDecimal amount = new BigDecimal(rep.getJsonObject().getDouble("amount"));
            service.createAccount(userId, amount);

            msg = "The account associated with user_id=" + userId + "successfully created \n";
            setStatus(Status.SUCCESS_OK);
            return new StringRepresentation(msg, MediaType.TEXT_PLAIN);
        } catch (IOException | JSONException | DBException e) {
            throw new RuntimeException(e);
        } catch (TransactionNotAllowedException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            msg = e.getMessage();
            return new StringRepresentation(msg, MediaType.TEXT_PLAIN);
        }
    }

    @Get
    @Override
    public Representation get() {
        JSONArray response = null;
        try {
            response = new JSONArray(service.getAllAccounts());
        } catch (DBException e) {
            e.printStackTrace();
        }
        return new JsonRepresentation(response);
    }
}
