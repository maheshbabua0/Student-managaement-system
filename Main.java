import java.sql.SQLException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        StudentDAO dao = new StudentDAO();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Add Student\n2. View All\n3. Exit");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.print("Enter ID: ");
                    String id = sc.next();
                    sc.nextLine();  
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();  
                    System.out.print("Enter Department: ");
                    String dept = sc.next();
                    System.out.print("Enter Age: ");
                    int age = sc.nextInt();
                    Student s = new Student(id, name, dept, age);
                    dao.addStudent(s);
                    System.out.println("Student Added!");
                    break;
                case 2:
                    List<Student> students = dao.getAllStudents();
                    for (Student st : students) {
                        System.out.println(st);
                    }
                    break;
                case 3:
                    System.exit(0);
            }
        }
    }
}
