package app.utils;

import lombok.extern.log4j.Log4j2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Log4j2
public class MyPreparedStatementProxy extends A_PreparedStatementProxy {

    public MyPreparedStatementProxy(PreparedStatement statement) {
        super(statement);
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        log.info("Query: " + this.statement.toString());
        return super.executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException {
        log.info("Update: " + this.statement.toString());
        return super.executeUpdate();
    }
}
