package server_resources;

import dao.UsersDaoImpl;
import db_service.C3P0DataSource;
import services.UserServiceImpl;
import exceptions.DBException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import services.UserService;

import java.sql.Connection;

public class SingleUserResource extends ServerResource {

    private static final Connection conn = C3P0DataSource.getInstance().getH2Connection();
    private final UserService service;

    public SingleUserResource() {
        service = new UserServiceImpl(new UsersDaoImpl(conn));
    }

    @Get("json")
    @Override
    public Representation get() {
        long userId = Long.parseLong(getAttribute("user_id"));
        JSONObject responce = null;
        try {
            responce = new JSONObject(service.getUser(userId));
        } catch (DBException e) {
            e.printStackTrace();
        }
        return new JsonRepresentation(responce);
    }

}
