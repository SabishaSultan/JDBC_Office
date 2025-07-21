package office;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

import static office.Service.createDB;

public class Office {

    public static void main(String[] args) {
        try (Connection con = DriverManager.getConnection("jdbc:h2:~\\JDBC_Office");
             Statement stm = con.createStatement()) {

            Option opt = Option.AddDepartment;
            Scanner sc = new Scanner(System.in);

            while (!opt.equals(Option.EXIT)) {
                System.out.println("Введите число:");
                for (Option o : Option.values()) System.out.println(o.getText());
                opt = Option.values()[sc.nextInt()];
                opt.action();
            }

//            // 1. Найдите ID сотрудника с именем Ann и установите его департамент в HR
//            ResultSet rs = stm.executeQuery("SELECT ID FROM Employee WHERE NAME = 'ann'");
//            if (rs.next()) {
//                int id = rs.getInt("ID");
//                stm.executeUpdate("UPDATE Employee SET DepartmentID = 3 WHERE ID = " + id);
//                System.out.println("Департамент сотрудника с ID " + id + " установлен в HR.");
//            }
//            // 2. Проверьте имена всех сотрудников и исправьте их на большие буквы
//            ResultSet rsEmployees = stm.executeQuery("SELECT ID, NAME FROM Employee");
//            int correctedCount = 0;
//            while (rsEmployees.next()) {
//                int id = rsEmployees.getInt("ID");
//                String name = rsEmployees.getString("NAME");
//                if (!Character.isUpperCase(name.charAt(0))) {
//                    String correctedName = name.substring(0, 1).toUpperCase() + name.substring(1);
//                    stm.executeUpdate("UPDATE Employee SET NAME = '" + correctedName + "' WHERE ID = " + id);
//                    correctedCount++;
//                }
//            }
//            System.out.println("Количество исправленных имён: " + correctedCount);
//
//            // 3. Выведите на экран количество сотрудников в IT-отделе
//            ResultSet rsIT = stm.executeQuery("SELECT COUNT(*) AS count FROM Employee WHERE DepartmentID = 2");
//            if (rsIT.next()) {
//                int countIT = rsIT.getInt("count");
//                System.out.println("Количество сотрудников в IT-отделе: " + countIT);
//            }

        } catch (SQLException e) {
            System.out.println("Ошибка SQL: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public static void checkEmployeesAfterDepartmentDelete(int departmentId){
        try (Connection con = DriverManager.getConnection("jdbc:h2:~\\JDBC_Office");
             Statement statement = con.createStatement()) {
            // Получим сотрудников из удаленного отдела
            ResultSet rs = statement.executeQuery("SELECT * FROM Employee WHERE DepartmentID = " + departmentId);

            // Проверяем, есть ли сотрудники в результате
            if (!rs.next()) {
                System.out.println("Все сотрудники из отдела с ID " + departmentId + " успешно удалены.");
            } else {
                System.out.println("Сотрудники из отдела с ID " + departmentId + " все еще существуют:");
                do {
                    System.out.println("Employee ID: " + rs.getInt("id") + ", Name: " + rs.getString("NAME"));
                } while (rs.next());
            }
        } catch (Exception e) {
            System.out.println("Ошибка при проверке сотрудников: " + e.getMessage());
        }
    }
}
