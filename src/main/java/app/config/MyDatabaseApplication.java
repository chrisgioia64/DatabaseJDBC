package app.config;

import app.EmployeeRepository;
import app.utils.MyConnectionProxy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Log4j2
public class MyDatabaseApplication {

    @Getter
    @Setter
    private DatabaseProperties prop;

    public EmployeeRepository getEmployeeRepository(boolean autocommit) {
        Connection connection = null;
        try {
            connection = createConnection();
            connection.setAutoCommit(autocommit);
            return new EmployeeRepository(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public EmployeeRepository getEmployeeRepository() {
        return getEmployeeRepository(true);
    }

    public Connection createConnection() throws SQLException {
        String url = prop.getProperty(DatabaseProperties.URL);
        String user = prop.getProperty(DatabaseProperties.USER);
        String password = prop.getProperty(DatabaseProperties.PASSWORD);
        Connection connection = DriverManager.getConnection(url, user, password);
        log.info("Connection successfully created for db url: " + url);
        return new MyConnectionProxy(connection);
    }

}
