package app.test;

import app.config.DatabaseApplicationFactory;
import app.config.MyDatabaseApplication;
import app.config.UnitTestDatabaseConfiguration;
import app.dao.EmployeeDao;
import app.model.Employee;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.List;

import static org.testng.Assert.*;

@Log4j2
public class EmployeeDaoTest {

    private MyDatabaseApplication app1, app2;
    private EmployeeDao dao1, dao2;
    private Employee emp1, emp2, emp3, emp4;

    @BeforeClass
    public void beforeClass() throws SQLException {
        log.info("Setting up variables for test class : " + EmployeeDaoTest.class.getName());
        app1 = DatabaseApplicationFactory.createApp(UnitTestDatabaseConfiguration.class);
        app2 = DatabaseApplicationFactory.createApp(UnitTestDatabaseConfiguration.class);
        dao1 = app1.getEmployeeRepository().getDao();
        dao2 = app2.getEmployeeRepository().getDao();
        emp1 = Employee.builder().firstName("Chris").lastName("Gioia")
                .salary(50000).build();
        emp2 = Employee.builder().firstName("John").lastName("Gioia")
                .salary(80000).build();
        emp3 = Employee.builder().firstName("Gerald").lastName("Albright")
                .salary(70000).build();
        emp4 = Employee.builder().firstName("Tony").lastName("Tonucci")
                .salary(40000).build();
    }

    @BeforeMethod
    public void before() throws SQLException {
        dao1.clearAllEntries();
    }

    @Test
    public void testNoEntries() throws SQLException {
       log.info("No entries");
       List<Employee> employees = dao1.getAllEmployees();
       assertEquals(employees.size(), 0);
    }

    @Test
    public void testAddOneEntry() throws SQLException {
        dao1.create(emp1);

        List<Employee> employees = dao1.getAllEmployees();
        assertEquals(1, employees.size());
        Employee employee = employees.get(0);
        assertEquals(employee.getFirstName(), emp1.getFirstName());
        assertEquals(employee.getLastName(), emp1.getLastName());
        assertEquals(employee.getSalary(), emp1.getSalary());
        assertEquals(employee.getId(), 1);
    }

    @Test
    public void testAddOneEntryMultipleConnections() throws SQLException {
        dao1.create(emp1);

        List<Employee> employees = dao2.getAllEmployees();
        assertEquals(1, employees.size());
        assertEquals(employees.get(0), emp1);
    }

    @Test
    public void testGetById() throws SQLException {
        dao1.create(emp1);
        dao1.create(emp2);

        List<Employee> employees = dao1.getAllEmployees();
        assertEquals(2, employees.size());
        Employee e = dao1.getEmployee(1);
        assertEquals(emp1, e);
        e = dao1.getEmployee(2);
        assertEquals(emp2, e);
        e = dao1.getEmployee(3);
        assertNull(e);
    }

    @Test
    public void testUpdate() throws SQLException {
        dao1.create(emp1);
        dao1.create(emp2);

        Employee e = dao1.getEmployee(1);
        e.setSalary(65000);
        e.setFirstName("Christopher");
        dao1.updateEmployee(e);

        Employee e2 = dao1.getEmployee(1);
        assertEquals(e2, e);
        assertEquals(e2.getId(), e.getId());
    }

    @Test
    public void testDelete() throws SQLException {
        dao1.create(emp1);
        dao1.create(emp2);
        dao1.create(emp3);

        assertEquals(dao1.getAllEmployees().size(), 3);
        assertFalse(dao1.deleteEmployee(4));
        assertEquals(dao1.getAllEmployees().size(), 3);
        assertTrue(dao1.deleteEmployee(3));
        assertEquals(dao1.getAllEmployees().size(), 2);
        assertNull(dao1.getEmployee(3));
        assertEquals(dao1.getEmployee(1), emp1);
        assertEquals(dao1.getEmployee(2), emp2);
    }

    @Test
    public void testStoredProcedure() throws SQLException {
        dao1.create(emp1);
        dao1.create(emp2);
        dao1.create(emp3);
        dao1.create(emp4);

        List<Employee> employees = dao1.getEmployeesWithSalaryHigher(0);
        assertEquals(employees.size(), 4);
        employees = dao1.getEmployeesWithSalaryHigher(75000);
        assertEquals(employees.size(), 1);
        assertEquals(employees.get(0), emp2);
    }
}
