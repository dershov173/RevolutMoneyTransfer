package services;

import exceptions.DBException;
import model.User;

import java.util.List;

public interface UserService {
    User getUser(long id) throws DBException;
    List<User> getAllUsers() throws DBException;
    long addUser(String name) throws DBException;
}
