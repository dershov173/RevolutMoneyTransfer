package server_resources;

import exceptions.DBException;
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

public class AllAccountsResource extends ServerResource {

    private final AccountService service;

    public AllAccountsResource() {
        service = new AccountServiceImpl();
    }

    @Post
    @Override
    public Representation post(Representation data){
        long userId = Long.parseLong(getAttribute("user_id"));
        BigDecimal amount = new BigDecimal(0);
        try {
            JsonRepresentation rep = new JsonRepresentation(data);
            amount = new BigDecimal(rep.getJsonObject().getDouble("amount"));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        try {
            service.createAccount(userId, amount);
        } catch (DBException e) {
            e.printStackTrace();
        }

        String msg = "The account associated with user_id=" + userId + "successfully created \n";

        setStatus(Status.SUCCESS_OK);
        return new StringRepresentation(msg, MediaType.TEXT_PLAIN);


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
