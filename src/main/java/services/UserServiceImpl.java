package services;

import dao.UsersDao;
import dao.UsersDaoImpl;
import db_service.C3P0DataSource;
import exceptions.DBException;
import model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {
    private static final Connection conn = C3P0DataSource.getInstance().getH2Connection();

    public User getUser(long id) throws DBException {
        try {
            return (new UsersDaoImpl(conn).get(id));
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public List<User> getAllUsers() throws DBException{
        try {
            return (new UsersDaoImpl(conn).getAllEntries());
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public long addUser(String name) throws DBException {
        try (Connection connection = C3P0DataSource.getInstance().getH2Connection()) {
            try {
                connection.setAutoCommit(false);
                UsersDao dao = new UsersDaoImpl(connection);
                dao.createTable();
                dao.createUser(name);
                connection.commit();
                return dao.getUserId(name);
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ignore) {
                }
                throw new DBException(e);
            }
        } catch (SQLException e){
            throw new DBException(e);
        }
    }

}
