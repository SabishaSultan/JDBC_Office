package office;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.Scanner;

public enum Option {
    AddEmployee {
        String getText() {
            return this.ordinal() + ".Добавить сотрудника";
        }
        void action() {
            Scanner sc = new Scanner(System.in);
            System.out.println("Введите его id:");
            int id=sc.nextInt();
            System.out.println("Введите его имя:");
            String name=sc.next();
            System.out.println("Введите id отдела:");
            int depid=sc.nextInt();
            Service.addEmployee(new Employee(id,name,depid));
        }
    },
    DeleteEmployee {
        String getText() {
            return this.ordinal() + ".Удалить сотрудника";
        }

        void action() {
            Scanner sc = new Scanner(System.in);
            System.out.println("Введите его id:");
            int id=sc.nextInt();
            Service.removeEmployee(new Employee(id,"",0));
        }
    },
    AddDepartment {
        String getText() {
            return this.ordinal() + ".Добавить отдел";
        }

        void action() {
            Scanner sc = new Scanner(System.in);
            System.out.println("Введите его id:");
            int id=sc.nextInt();
            System.out.println("Введите его название:");
            String name=sc.next();
            Service.addDepartment(new Department(id,name));
        }
    },
    DeleteDepartment {
        String getText() {
            return this.ordinal() + ".Удалить отдел";
        }

        void action() {
            Scanner sc = new Scanner(System.in);
            System.out.println("Введите его id:");
            int id=sc.nextInt();
            Service.removeDepartment(new Department(id,""));
        }
    },
    CheckEmployeesAfterDepartmentDelete {
        String getText() {
            return this.ordinal() + ".Проверить сотрудников после удаления отдела";
        }
        void action() {
            Scanner sc = new Scanner(System.in);
            System.out.println("Введите ID отдела для удаления:");
            int id=sc.nextInt();
            Service.removeDepartment(new Department(id,""));
            // Проверяем сотрудников после удаления отдела
            try(Connection con = DriverManager.getConnection("jdbc:h2:.\\JDBC_Office")){
                Statement stm = con.createStatement();
                //ResultSet rs= stm.executeQuery("Select Employee.ID, Employee.Name,Department.Name as DepName from Employee join Department on Employee.DepartmentID=Department.ID");
                ResultSet rs= stm.executeQuery("Select Employee.ID, Employee.Name,Employee.DepartmentID as DepName from Employee");
                System.out.println("------------------------------------");
                ResultSetMetaData metaData= rs.getMetaData();
                while(rs.next()){
                    System.out.println(rs.getInt("ID")+"\t"+rs.getString("NAME")+"\t"+rs.getString("DepName"));
                }
                System.out.println("------------------------------------");
            }catch (SQLException e) {
                System.out.println(e);
            }
        }
    },
    UPDATE_EMPLOYEE_DEPARTMENT {
        String getText() {
            return this.ordinal() + ". Установить департамент для сотрудника Ann в HR";
        }
        void action() {
//            Scanner sc = new Scanner(System.in);
//            System.out.println("Введите ID сотрудника:");
//            int id=sc.nextInt();
//            System.out.println("Введите его имя:");
//            String name=sc.next();
//            System.out.println("Введите id отдела:");
//            int depid=sc.nextInt();
//            Service.addEmployee(new Employee(id,name,depid));
            try (Connection con = DriverManager.getConnection("jdbc:h2:.\\JDBC_Office")){
        Statement stm = con.createStatement();
                 ResultSet rs = stm.executeQuery("SELECT ID FROM Employee WHERE NAME = 'Ann'");
                if (rs.next()) {
                int id = rs.getInt("ID");
                stm.executeUpdate("UPDATE Employee SET DepartmentID = 3 WHERE ID = " + id);
                System.out.println("Департамент для сотрудника с ID " + id + " установлен в HR.");
            } else {
                System.out.println("Сотрудник с именем Ann не найден.");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
},
    CORRECT_EMPLOYEE_NAMES { // 2. Исправить имена сотрудников
        String getText() {
            return this.ordinal() + ". Исправить имена сотрудников";
        }

        void action() {
            try (Connection con = DriverManager.getConnection("jdbc:h2:.\\JDBC_Office");
                 Statement stm = con.createStatement()) {
                ResultSet rs = stm.executeQuery("SELECT ID, NAME FROM Employee");
                int correctedCount = 0;

                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String name = rs.getString("NAME");
                    if (Character.isLowerCase(name.charAt(0))) {
                        String correctedName = name.substring(0, 1).toUpperCase() + name.substring(1);
                        stm.executeUpdate("UPDATE Employee SET NAME = '" + correctedName + "' WHERE ID = " + id);
                        correctedCount++;
                    }
                }
                System.out.println("Количество исправленных имён: " + correctedCount);

            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    },
    COUNT_IT_EMPLOYEES { // 3. Количество сотрудников в IT-отделе
        String getText() {
            return this.ordinal() + ". Показать количество сотрудников в IT-отделе";
        }

        void action() {
            try (Connection con = DriverManager.getConnection("jdbc:h2:.\\JDBC_Office");
                 Statement stm = con.createStatement()) {

                ResultSet rs = stm.executeQuery("SELECT COUNT(*) AS count FROM Employee WHERE DepartmentID = 2");
                if (rs.next()) {
                    int countIT = rs.getInt("count");
                    System.out.println("Количество сотрудников в IT-отделе: " + countIT);
                }

            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    },
    EXIT {
        String getText() {
            return this.ordinal() + ". Выход";
        }

        void action() {
            System.out.println("Выход из программы.");
        }
    },
    CLEAR_DB {
        String getText() {
            return this.ordinal() + ".Сбросить базу данных";
        }

        void action() {
            Service.createDB();
        }

    },
    PRINT_DEPS {
        String getText() {
            return this.ordinal() + ".Вывести на экран все отделы";
        }

        void action() {
            try(Connection con = DriverManager.getConnection("jdbc:h2:.\\JDBC_Office")){
                Statement stm = con.createStatement(
                        //"Select ID, NAME as txt from Department where name like ?",
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE
                );
                //String str="A%";
                ResultSet rs= stm.executeQuery("Select ID, NAME as txt from Department");
                //stm.setString(1,str);
                //ResultSet rs=stm.executeQuery();
                System.out.println("------------------------------------");
                while(rs.next()){
                    System.out.println(rs.getInt("ID")+"\t"+rs.getString("name"));
                }
                System.out.println("------------------------------------");
            }catch (SQLException e) {
                System.out.println(e);
            }
        }
    },
    PRINT_EMPLOYEES {
        String getText() {
            return this.ordinal() + ".Вывести на экран всех сотрудников";
        }

        void action() {
            try(Connection con = DriverManager.getConnection("jdbc:h2:.\\JDBC_Office")){
                Statement stm = con.createStatement();
                //ResultSet rs= stm.executeQuery("Select Employee.ID, Employee.Name,Department.Name as DepName from Employee join Department on Employee.DepartmentID=Department.ID");
                ResultSet rs= stm.executeQuery("Select Employee.ID, Employee.Name,Employee.DepartmentID as DepName from Employee");
                System.out.println("------------------------------------");
                ResultSetMetaData metaData= rs.getMetaData();
                while(rs.next()){
                    System.out.println(rs.getInt("ID")+"\t"+rs.getString("NAME")+"\t"+rs.getString("DepName"));
                }
                System.out.println("------------------------------------");
            }catch (SQLException e) {
                System.out.println(e);
            }
        }   
    },;

    Scanner sc = new Scanner(System.in);
    abstract String getText();
    abstract void action();
}