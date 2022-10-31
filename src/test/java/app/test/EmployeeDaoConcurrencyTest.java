package app.test;

import app.EmployeeRepository;
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@Log4j2
public class EmployeeDaoConcurrencyTest {

    private MyDatabaseApplication app1;
    private EmployeeRepository rep1, rep2, rep3;
    private EmployeeDao dao1, dao2, dao3;
    private Employee emp1, emp2, emp3, emp4;

    @BeforeClass
    public void beforeClass() throws SQLException {
        log.info("Setting up variables for test class : " + EmployeeDaoConcurrencyTest.class.getName());
        app1 = DatabaseApplicationFactory.createApp(UnitTestDatabaseConfiguration.class);
        rep1 = app1.getEmployeeRepository(true);
        rep2 = app1.getEmployeeRepository(false);
        rep3 = app1.getEmployeeRepository(false);
        dao1 = rep1.getDao();
        dao2 = rep2.getDao();
        dao3 = rep3.getDao();
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

    /**
     * Sequential add, check, and removal
     */
    @Test
    public void testIsolation1() throws SQLException {
        // Employee 1 added and committed by transaction 1
        // Transaction 2 retrieves all rows
        // Transaction 1 clears all rows, and transaction 2 verifies no rows are present

        dao1.create(emp1);
        List<Employee> employees = dao2.getAllEmployees();
        assertEquals(1, employees.size());
        rep2.commitTransaction();

        dao1.clearAllEntries();
        assertEquals(0, dao2.getAllEmployees().size());
    }

    /**
     * Dirty reads should not happen
     * Dirty reads happen when a transaction 1 makes a modification (e.g. UPDATE)
     *  without committing and a transaction 2 gets the uncommitted modification
     */
    @Test
    public void testIsolation2() throws SQLException {
        // Tests that dirty reads do not happen

        // Add some employees without committing the transaction
        dao2.create(emp1);
        dao2.create(emp2);

        // Retrieve all employees shouldn't retrieve anything
        List<Employee> list = dao3.getAllEmployees();
        assertEquals(0, list.size());
    }

    /**
     * Non-repeatable reads should not happen for the default isolation
     * level of MySQL which is REPEATABLE READ
     * A non-repeatable read is similar to a dirty read except that transaction 1
     * has been committed.
     * A non-repeatable read is when transaction 1 and transaction 2 are interleaved,
     * and transaction makes a modification and then commits, and transaction 2
     * retrieves the row that was modified and sees that the row has been modified
     * by transaction 1.
     */
    @Test
    public void testIsolation3() throws SQLException {
        // Nonrepeatable reads should not happen
        dao1.create(emp1);

        assertEquals(dao2.getEmployee(1), emp1);

        emp1.setFirstName("George");
        dao1.updateEmployee(emp1);

        assertEquals(dao2.getEmployee(1).getFirstName(), "Chris");
    }

    /**
     * Well, according to the default MySQL database isolation level (REPEATABLE READ),
     * phantom reads should be present, but in this case, they are not.
     * @throws SQLException
     */
    @Test
    public void testIsolation4() throws SQLException {
        List<Employee> list1 = dao2.getAllEmployees();
        assertEquals(0, list1.size());

        // Created and committed
        dao1.create(emp1);

        // Dao2 is still in the same transaction
        List<Employee> list2 = dao2.getAllEmployees();
        assertEquals(1, list2.size());

        dao1.create(emp2);

        list2 = dao2.getAllEmployees();
        // transaction 2 has not been committed but we should get phantom read from employee 2
        assertEquals(2, list2.size());
    }

    @Test
    public void testLocking1() throws SQLException {
        // Employee 2 is being used by transaction 2,
        // and transaction 3 is trying to clear all rows
        // which results in a timeout because employee 2 is being locked
        dao1.create(emp1);

        dao2.create(emp2);
        Employee employee2 = dao3.getEmployee(2);
        assertNull(employee2);

        // this command times out
        dao3.clearAllEntries();
    }

    @Test
    public void testLocking2() throws SQLException {
        // Employee 2 was added by transaction 2 but not yet committed
        // and transaction 3 is trying to obtain the id associated with employee 2
        // but there's no row associated with employee 2 in transaction 3
        // because transaction 2 has not yet been committed

        dao1.create(emp1);

        dao2.create(emp2);
        Employee employee2 = dao3.getEmployee(2);
        assertNull(employee2);

        assertEquals(emp1, dao3.getEmployee(1));
        assertNull(dao3.getEmployee(2));
    }


    @Test
    public void testLocking3() throws SQLException {
        // Employee 2 has been added by transaction 2, and
        // Transaction 3 is doing a read-only operation across all rows of the table
        dao1.create(emp1);

        dao2.create(emp2);
        Employee employee2 = dao3.getEmployee(2);
        assertNull(employee2);

        dao3.getAllEmployees();
    }

}
