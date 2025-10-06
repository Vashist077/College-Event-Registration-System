package src;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class StudentRegistrationManager {
    
    // Register student for an event
    public boolean registerStudent(String studentName, String studentEmail, int eventId) {
        // First check if student already exists
        int studentId = getOrCreateStudent(studentName, studentEmail);
        if (studentId == -1) return false;
        
        // Check if already registered for this event
        if (isStudentRegistered(studentId, eventId)) {
            JOptionPane.showMessageDialog(null, "Student is already registered for this event!", 
                                        "Registration Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Register student for event
        String sql = "INSERT INTO registrations (student_id, event_id, registration_date) VALUES (?, ?, CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, eventId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error registering student: " + e.getMessage(), 
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Get or create student in database
    private int getOrCreateStudent(String name, String email) {
        // First check if student exists
        String checkSql = "SELECT student_id FROM students WHERE student_email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("student_id");
            }
            
            // Student doesn't exist, create new one
            String insertSql = "INSERT INTO students (student_name, student_email) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, name);
                insertStmt.setString(2, email);
                
                int result = insertStmt.executeUpdate();
                if (result > 0) {
                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error managing student data: " + e.getMessage(), 
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return -1;
    }
    
    // Check if student is already registered for event
    private boolean isStudentRegistered(int studentId, int eventId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE student_id = ? AND event_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, eventId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking registration: " + e.getMessage());
        }
        
        return false;
    }
    
    // Get registered students for an event
    public List<Student> getRegisteredStudents(int eventId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.student_id, s.student_name, s.student_email " +
                    "FROM students s " +
                    "INNER JOIN registrations r ON s.student_id = r.student_id " +
                    "WHERE r.event_id = ? " +
                    "ORDER BY s.student_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Student student = new Student(
                    rs.getInt("student_id"),
                    rs.getString("student_name"),
                    rs.getString("student_email")
                );
                students.add(student);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching registered students: " + e.getMessage(), 
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return students;
    }
}
