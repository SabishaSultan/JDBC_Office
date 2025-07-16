package office;

import org.junit.jupiter.api.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @BeforeAll
    public static void setup() {
        Service.createDB();
    }

    @Test
    public void testUpdateEmployeeDepartment() {
        // Найти ID сотрудника с именем Ann
        int employeeId = 2;
        try (Connection con = DriverManager.getConnection("jdbc:h2:.\\JDBC_Office");
             Statement stm = con.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT ID FROM Employee WHERE NAME = 'ann'");
            if (rs.next()) {
                employeeId = rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Установить департамент в HR (ID 3)
        try (Connection con = DriverManager.getConnection("jdbc:h2:.\\JDBC_Office");
             PreparedStatement stm = con.prepareStatement("UPDATE Employee SET DepartmentID = ? WHERE ID = ?")) {
            stm.setInt(1, 3); // HR
            stm.setInt(2, employeeId);
            stm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Проверить, что департамент обновился
        int departmentId = -1;
        try (Connection con = DriverManager.getConnection("jdbc:h2:.\\JDBC_Office");
             Statement stm = con.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT DepartmentID FROM Employee WHERE ID = " + employeeId);
            if (rs.next()) {
                departmentId = rs.getInt("DepartmentID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        assertEquals(3, departmentId, "Департамент для сотрудника Ann должен быть установлен в HR.");
    }

    @Test
    public void testCorrectEmployeeNames() {
        int correctedCount = 0;

        try (Connection con = DriverManager.getConnection("jdbc:h2:.\\JDBC_Office");
             Statement stm = con.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT ID, NAME FROM Employee");
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("NAME");
                if (Character.isLowerCase(name.charAt(0))) {
                    String correctedName = name.substring(0, 1).toUpperCase() + name.substring(1);
                    PreparedStatement updateStmt = con.prepareStatement("UPDATE Employee SET NAME = ? WHERE ID = ?");
                    updateStmt.setString(1, correctedName);
                    updateStmt.setInt(2, id);
                    updateStmt.executeUpdate();
                    correctedCount++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        assertTrue(correctedCount > 0, "Количество исправленных имён должно быть больше 0.");
    }

    @Test
    public void testCountITEmployees() {
        int countIT = 0;

        try (Connection con = DriverManager.getConnection("jdbc:h2:.\\JDBC_Office");
             Statement stm = con.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT COUNT(*) AS count FROM Employee WHERE DepartmentID = 2");
            if (rs.next()) {
                countIT = rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        assertEquals(2, countIT, "Количество сотрудников в IT-отделе должно быть равно 2.");
    }

    @Test
    public void testRemoveDepartmentAndEmployees() {
        int departmentIdToDelete = 1;

        // Сначала проверяем количество сотрудников в отделе перед удалением
        int initialEmployeeCount = getEmployeeCountByDepartment(departmentIdToDelete);
        assertTrue(initialEmployeeCount > 0, "Сначала должно быть больше 0 сотрудников в удаляемом отделе.");

        // Удаляем отдел
        Service.removeDepartment(new Department(departmentIdToDelete, ""));

        // Проверяем количество сотрудников в отделе после удаления
        int finalEmployeeCount = getEmployeeCountByDepartment(departmentIdToDelete);
        assertEquals(0, finalEmployeeCount, "После удаления отдела сотрудники должны быть удалены.");
    }

    private int getEmployeeCountByDepartment(int departmentId) {
        int count = 0;
        try (Connection con = DriverManager.getConnection("jdbc:h2:.\\JDBC_Office");
             PreparedStatement stm = con.prepareStatement("SELECT COUNT(*) AS count FROM Employee WHERE DepartmentID = ?")) {
            stm.setInt(1, departmentId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    @AfterAll
    public static void tearDown() {
        // Очистка базы данных или другие действия по завершению тестов
    }
}