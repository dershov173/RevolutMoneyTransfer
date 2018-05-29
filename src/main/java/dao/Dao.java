package dao;

import java.sql.SQLException;
import java.util.List;

public interface Dao{
    void createTable() throws SQLException;
    void dropTable() throws SQLException;
}
