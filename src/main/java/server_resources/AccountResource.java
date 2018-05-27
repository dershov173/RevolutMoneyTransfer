package server_resources;

import dao.AccountsDaoImpl;
import db_service.C3P0DataSource;
import exceptions.AccountNotFoundException;
import exceptions.DBException;
import exceptions.TransactionNotAllowedException;
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
import java.sql.Connection;

public class AccountResource extends ServerResource {

    private final AccountService service;
    private static final Connection conn = C3P0DataSource.getInstance().getH2Connection();

    public AccountResource() {
        this.service = new AccountServiceImpl(new AccountsDaoImpl(conn));
    }

    @Put
    @Override
    public Representation put(Representation data){
        long accountId = Long.parseLong(getAttribute("account_id"));
        try {
            JsonRepresentation rep = new JsonRepresentation(data);
            BigDecimal amount = new BigDecimal(rep.getJsonObject().getDouble("amount"));
            service.updateAmount(accountId, amount);
            setStatus(Status.SUCCESS_OK);
            JSONObject response = new JSONObject(service.getAccount(accountId));
            return new JsonRepresentation(response);
        } catch (IOException | JSONException | DBException e) {
            setStatus(Status.SERVER_ERROR_INTERNAL);
            throw new RuntimeException(e);
        } catch (TransactionNotAllowedException | AccountNotFoundException e){
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return new StringRepresentation(e.getMessage(), MediaType.TEXT_PLAIN);
        }
    }
}
