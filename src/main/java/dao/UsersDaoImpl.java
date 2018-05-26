package dao;

import db_service.Executor;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersDaoImpl implements UsersDao {
    private static final Logger logger = LogManager.getLogger("C3PODataSource");
    private Executor executor;

    public UsersDaoImpl(Connection connection) {
        this.executor = new Executor(connection);
    }

    @Override
    public User get(long userId) throws SQLException {
        return executor.executeQuery("select * from users where user_id=?", result -> {
            result.next();
            return new User(result.getLong("user_id"), result.getString(2));
        }, userId);
    }

    @Override
    public long getUserId(String name) throws SQLException {
        return executor.executeQuery("select user_id from users where user_name=?", result -> {
            result.next();
            return result.getLong(1);
        }, name);
    }

    @Override
    public List<User> getAllEntries() throws SQLException {
        return executor.executeQuery("select * from users", result -> {
            ArrayList<User> users = new ArrayList<>();
            while (result.next()) {
                User user = new User(result.getLong("user_id"), result.getString("user_name"));
                users.add(user);
            }
            return users;
        });
    }

    @Override
    public void createUser(String name) throws SQLException {
        executor.executeUpdate("insert into users (user_name) values (?)", name);
    }

    @Override
    public void createTable() throws SQLException {
        executor.executeUpdate("create table if not exists users (" +
                "user_id bigint auto_increment, " +
                "user_name varchar(256), " +
                "primary key (user_id))");
        logger.info("Table users (" +
                "user_id bigint primary key, " +
                "user_name varchar(256)) successfully created");
    }

    @Override
    public void dropTable() throws SQLException {
        executor.executeUpdate("drop table if exists users");
        logger.info("Table users no longer exists");
    }
}

