package src;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class EventManager {
    
    // Add new event to database
    public boolean addEvent(String eventName, String eventDate) {
        String sql = "INSERT INTO events (event_name, event_date) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, eventName);
            pstmt.setString(2, eventDate);
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding event: " + e.getMessage(), 
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Get all events from database
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY event_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Event event = new Event(
                    rs.getInt("event_id"),
                    rs.getString("event_name"),
                    rs.getString("event_date")
                );
                events.add(event);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching events: " + e.getMessage(), 
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return events;
    }
    
    // Get events for dropdown
    public Event[] getEventsArray() {
        List<Event> eventsList = getAllEvents();
        return eventsList.toArray(new Event[0]);
    }
}