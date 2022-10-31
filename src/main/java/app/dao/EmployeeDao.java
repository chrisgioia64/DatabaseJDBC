package app.dao;

import app.model.Employee;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeDao {

    boolean create(Employee employee) throws SQLException;

    Employee getEmployee(int employeeId) throws SQLException;

    List<Employee> getAllEmployees() throws SQLException;

    boolean updateEmployee(Employee employee) throws SQLException;

    boolean deleteEmployee(int employeeId) throws SQLException;

    boolean clearAllEntries() throws SQLException;

    List<Employee> getEmployeesWithSalaryHigher(double salary) throws SQLException;

}
