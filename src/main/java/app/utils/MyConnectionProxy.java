package app.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyConnectionProxy extends A_ConnectionProxy {

    public MyConnectionProxy(Connection connection) {
        super(connection);
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        PreparedStatement statement = super.prepareStatement(sql);
        return new MyPreparedStatementProxy(statement);
    }
}
