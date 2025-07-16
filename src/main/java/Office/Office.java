package office;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Office {

    public static void main(String[] args) {
        try (Connection con = DriverManager.getConnection("jdbc:h2:~\\JDBC_Office");
             Statement statement = con.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS Department (id INT PRIMARY KEY, name VARCHAR(255))");
            //statement.execute("INSERT INTO Department (id, name) VALUES (1, 'Ann')");
            Option opt = Option.AddDepartment;
            Scanner sc = new Scanner(System.in);

            while (!opt.equals(Option.EXIT)) {
                System.out.println("Введите число:");
                for (Option o : Option.values()) System.out.println(o.getText());
                opt = Option.values()[sc.nextInt()];
                opt.action();
            }
            // 1. Найдем ID сотрудника с именем Ann
            String query = "SELECT employeeId FROM Employee WHERE name = ?";
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, "Ann");
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int employeeId = resultSet.getInt("employeeId");
                    // Проверяем, есть ли еще такие сотрудники
                    if (!resultSet.next()) { // Если нет следующей строки, то это единственный сотрудник
                        // Установить департамент в HR (например, 1 - это ID департамента HR)
                        String updateQuery = "UPDATE Employee SET departmentId = ? WHERE employeeId = ?";
                        try (PreparedStatement updateStatement = con.prepareStatement(updateQuery)) {
                            updateStatement.setInt(1, 1); // Предположим, что 1 - это ID департамента HR
                            updateStatement.setInt(2, employeeId);
                            updateStatement.executeUpdate();
                            System.out.println("Департамент сотрудника Ann установлен в HR.");
                        }
                    } else {
                        System.out.println("Найдено несколько сотрудников с именем Ann.");
                    }
                } else {
                    System.out.println("Сотрудник с именем Ann не найден.");
                }
            }

            // 2. Исправим имена сотрудников с маленькой буквы
            String selectQuery = "SELECT employeeId, name FROM Employee";
            try (PreparedStatement selectStatement = con.prepareStatement(selectQuery);
                 ResultSet resultSet = selectStatement.executeQuery()) {

                int correctedCount = 0;
                while (resultSet.next()) {
                    int employeeId = resultSet.getInt("employeeId");
                    String name = resultSet.getString("name");
                    if (name.equals(name.toLowerCase())) { // Если имя написано с маленькой буквы
                        String correctedName = name.substring(0, 1).toUpperCase() + name.substring(1);
                        String updateNameQuery = "UPDATE Employee SET name = ? WHERE employeeId = ?";
                        try (PreparedStatement updateNameStatement = con.prepareStatement(updateNameQuery)) {
                            updateNameStatement.setString(1, correctedName);
                            updateNameStatement.setInt(2, employeeId);
                            updateNameStatement.executeUpdate();
                            correctedCount++;
                        }
                    }
                }
                System.out.println("Количество исправленных имен: " + correctedCount);
            }

            // 3. Выведем количество сотрудников в IT-отделе
            String countQuery = "SELECT COUNT(*) AS count FROM Employee WHERE departmentId = ?";
            try (PreparedStatement countStatement = con.prepareStatement(countQuery)) {
                countStatement.setInt(1, 2); // Предположим, что 2 - это ID департамента IT
                ResultSet countResultSet = countStatement.executeQuery();
                if (countResultSet.next()) {
                    int itCount = countResultSet.getInt("count");
                    System.out.println("Количество сотрудников в IT-отделе: " + itCount);
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static void checkEmployeesAfterDepartmentDelete(int departmentId){
        try (Connection con = DriverManager.getConnection("jdbc:h2:~\\JDBC_Office");
             Statement statement = con.createStatement()) {
            // Получим сотрудников из удаленного отдела
            ResultSet rs = statement.executeQuery("SELECT * FROM Employee WHERE departmentId = " + departmentId);

            // Проверяем, есть ли сотрудники в результате
            if (!rs.next()) {
                System.out.println("Все сотрудники из отдела с ID " + departmentId + " успешно удалены.");
            } else {
                System.out.println("Сотрудники из отдела с ID " + departmentId + " все еще существуют:");
                do {
                    System.out.println("Employee ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
                } while (rs.next());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
