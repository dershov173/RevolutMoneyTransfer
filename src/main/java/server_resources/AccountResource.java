package server_resources;

import exceptions.DBException;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import services.AccountService;
import services.AccountServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;

public class AccountResource extends ServerResource {

    private final AccountService service;

    public AccountResource() {
        this.service = new AccountServiceImpl();
    }

    @Put
    @Override
    public Representation put(Representation data){
        long accountId = Long.parseLong(getAttribute("account_id"));
        try {
            JsonRepresentation rep = new JsonRepresentation(data);
            BigDecimal amount = new BigDecimal(rep.getJsonObject().getDouble("amount"));
            if (service.updateAmount(accountId, amount)){
                setStatus(Status.SUCCESS_OK);
                JSONObject response = new JSONObject(service.getAccount(accountId));
                return new JsonRepresentation(response);
            } else {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return new StringRepresentation("Account balance cannot be negative", MediaType.TEXT_PLAIN);
            }
        } catch (IOException | JSONException | DBException e) {
            setStatus(Status.SERVER_ERROR_INTERNAL);
            throw new RuntimeException(e);
        }
    }
}
