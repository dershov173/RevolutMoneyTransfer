package db_service;

import exceptions.AccountNotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultHandler<T> {
    T handle(ResultSet result) throws SQLException;
}
