// EventDAO.java
package dao;

import model.Event;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    
    public boolean createEvent(Event event) {
        String sql = "INSERT INTO events (event_id, name, description, date, time, venue_id, organizer_id, ticket_price, total_tickets, available_tickets) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, event.getEventId());
            stmt.setString(2, event.getName());
            stmt.setString(3, event.getDescription());
            stmt.setDate(4, event.getDate());
            stmt.setTime(5, event.getTime());
            stmt.setString(6, event.getVenueId());
            stmt.setString(7, event.getOrganizerId());
            stmt.setDouble(8, event.getTicketPrice());
            stmt.setInt(9, event.getTotalTickets());
            stmt.setInt(10, event.getAvailableTickets());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY date, time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                events.add(new Event(
                    rs.getString("event_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("date"),
                    rs.getTime("time"),
                    rs.getString("venue_id"),
                    rs.getString("organizer_id"),
                    rs.getDouble("ticket_price"),
                    rs.getInt("total_tickets"),
                    rs.getInt("available_tickets")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
    
    public List<Event> getEventsByOrganizer(String organizerId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE organizer_id = ? ORDER BY date, time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, organizerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                events.add(new Event(
                    rs.getString("event_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("date"),
                    rs.getTime("time"),
                    rs.getString("venue_id"),
                    rs.getString("organizer_id"),
                    rs.getDouble("ticket_price"),
                    rs.getInt("total_tickets"),
                    rs.getInt("available_tickets")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
    
    public boolean isVenueAvailable(String venueId, Date date, Time time) {
        String sql = "SELECT COUNT(*) FROM events WHERE venue_id = ? AND date = ? AND time = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, venueId);
            stmt.setDate(2, date);
            stmt.setTime(3, time);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateAvailableTickets(String eventId, int newAvailable) {
        String sql = "UPDATE events SET available_tickets = ? WHERE event_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newAvailable);
            stmt.setString(2, eventId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
