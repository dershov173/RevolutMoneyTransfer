package dao;

import model.User;

import java.sql.SQLException;
import java.util.List;

public interface UsersDao extends Dao {
    User get(long userId) throws SQLException;
    List<User> getAllEntries() throws SQLException;
    @Override
    void createTable() throws SQLException;
    @Override
    void dropTable() throws SQLException;

    long getUserId(String name) throws SQLException;
    void createUser(String name) throws SQLException;
}
