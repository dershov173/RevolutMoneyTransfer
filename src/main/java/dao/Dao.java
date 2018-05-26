package dao;

import java.sql.SQLException;
import java.util.List;

public interface Dao<T> {
    List<T> getAllEntries() throws SQLException;
    void createTable() throws SQLException;
    void dropTable() throws SQLException;
}
