package app.dao;

import app.model.Employee;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Log4j2
public class EmployeeDaoImpl implements EmployeeDao {

    private Connection connection;

    public static final String TABLE_NAME = "employees";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_SALARY = "salary";

    public EmployeeDaoImpl(Connection connection) {
        this.connection = connection;
    }

    private static Employee createEmployee(ResultSet resultSet) throws SQLException {
        String firstName = resultSet.getString(COLUMN_FIRST_NAME);
        String lastName = resultSet.getString(COLUMN_LAST_NAME);
        double salary = resultSet.getDouble(COLUMN_SALARY);
        int id = resultSet.getInt(COLUMN_ID);
        Employee employee = Employee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .id(id)
                .salary(salary).build();
        return employee;
    }

    @Override
    public boolean create(Employee employee) throws SQLException {
        String sql = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
                TABLE_NAME, COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_SALARY);
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, employee.getFirstName());
        statement.setString(2, employee.getLastName());
        statement.setDouble(3, employee.getSalary());
        boolean b = statement.execute();
        return b;
    }

    @Override
    public Employee getEmployee(int employeeId) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?",
                TABLE_NAME, COLUMN_ID);
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, employeeId);
        ResultSet resultSet = statement.executeQuery();
        boolean hasNext = resultSet.next();
        if (hasNext) {
            return createEmployee(resultSet);
        }
        return null;
    }

    @Override
    public List<Employee> getAllEmployees() throws SQLException {
        String sql = String.format("SELECT * FROM %s", TABLE_NAME);
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        List<Employee> employees = new LinkedList<>();
        while (resultSet.next()) {
            Employee emp = createEmployee(resultSet);
            employees.add(emp);
        }
//        connection.commit();
        return employees;
    }

    @Override
    public boolean updateEmployee(Employee employee) throws SQLException {
        String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ? WHERE %s = ?",
            TABLE_NAME, COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_SALARY, COLUMN_ID);
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, employee.getFirstName());
        statement.setString(2, employee.getLastName());
        statement.setDouble(3, employee.getSalary());
        statement.setInt(4, employee.getId());
        int numUpdates = statement.executeUpdate();
        return numUpdates == 1;
    }

    @Override
    public boolean deleteEmployee(int employeeId) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE %s = ?",
                TABLE_NAME, COLUMN_ID);
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, employeeId);
        int numUpdates = statement.executeUpdate();
        return numUpdates == 1;
    }

    @Override
    public boolean clearAllEntries() throws SQLException {
        String sql = String.format("TRUNCATE %s", TABLE_NAME);
        PreparedStatement statement = connection.prepareStatement(sql);
        int numUpdates = statement.executeUpdate();
        return true;
    }

    @Override
    public List<Employee> getEmployeesWithSalaryHigher(double salary) throws SQLException {
        CallableStatement myCall = connection.prepareCall("{call salary_higher(?)}");
        myCall.setDouble(1, salary);
        myCall.execute();
        ResultSet resultSet = myCall.getResultSet();
        List<Employee> employees = new LinkedList<>();
        while (resultSet.next()) {
            Employee emp = createEmployee(resultSet);
            employees.add(emp);
        }
        return employees;
    }
}
