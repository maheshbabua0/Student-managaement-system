import java.sql.*;
import java.util.*;

public class StudentDAO {

    public void addStudent(Student student) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO students (id, name, department, age) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, student.getId());
        stmt.setString(2, student.getName());
        stmt.setString(3, student.getDepartment());
        stmt.setInt(4, student.getAge());
        stmt.executeUpdate();
        conn.close();
    }

    public List<Student> getAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM students");
        while (rs.next()) {
            Student s = new Student(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("department"),
                rs.getInt("age")
            );
            list.add(s);
        }
        conn.close();
        return list;
    }
}
