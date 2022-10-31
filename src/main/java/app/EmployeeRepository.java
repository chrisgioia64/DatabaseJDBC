package app;

import app.dao.EmployeeDao;
import app.dao.EmployeeDaoImpl;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

public class EmployeeRepository {

    @Getter
    private EmployeeDao dao;
    private Connection connection;

    public EmployeeRepository(Connection connection) {
        this.connection = connection;
        this.dao = new EmployeeDaoImpl(connection);
    }

    public void commitTransaction() throws SQLException {
        this.connection.commit();
    }

    public void rollback() throws SQLException {
        this.connection.rollback();
    }

}
