package server_resources;

import dao.UsersDaoImpl;
import db_service.C3P0DataSource;
import services.UserServiceImpl;
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
import services.UserService;

import java.io.IOException;
import java.sql.Connection;

;

public class AllUsersResource extends ServerResource {
    private final UserService service;
    private static final Connection conn = C3P0DataSource.getInstance().getH2Connection();

    public AllUsersResource() {
        service = new UserServiceImpl(new UsersDaoImpl(conn));
    }

    @Post
    @Override
    public Representation post(Representation data){
        Status status = null;
        String msg = null;
        JsonRepresentation rep = null;
        String userName =null;
        try {
            rep = new JsonRepresentation(data);
            userName = rep.getJsonObject().getString("name");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        if (userName == null) {
            msg = "No user name was given.\n";
            status = Status.CLIENT_ERROR_BAD_REQUEST;
        }
        else {
            try {
                service.addUser(userName);
            } catch (DBException e) {
                e.printStackTrace();
            }
            msg = "The user with name'" + userName + "' has been added.\n";
            status = Status.SUCCESS_OK;
        }

        setStatus(status);
        return new StringRepresentation(msg, MediaType.TEXT_PLAIN);
    }

    @Get
    @Override
    public Representation get() {
        JSONArray response = null;
        try {
            response = new JSONArray(service.getAllUsers());
        } catch (DBException e) {
            e.printStackTrace();
        }
        return new JsonRepresentation(response);
    }
}
