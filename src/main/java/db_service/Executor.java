package db_service;

import exceptions.AccountNotFoundException;

import java.sql.*;

public class Executor {
    private final Connection conn;

    public Executor(Connection conn) {
        this.conn = conn;
    }

    public int executeUpdate(String update, Object ... params) throws SQLException {
        try(PreparedStatement statement = conn.prepareStatement(update)){
            for (int i = 1; i <= params.length; i++){
                statement.setObject(i, params[i-1]);
            }
            return statement.executeUpdate();
        }
    }

    public <T> T executeQuery(String query, ResultHandler<T> handler, Object ... params) throws SQLException{
        try(PreparedStatement statement = conn.prepareStatement(query)){
            for (int i = 1; i <= params.length; i++){
                statement.setObject(i, params[i-1]);
            }
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            return handler.handle(resultSet);
        }
    }
}
