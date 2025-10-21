// VenueDAO.java
package dao;

import model.Venue;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenueDAO {
    
    public boolean createVenue(Venue venue) {
        String sql = "INSERT INTO venues (venue_id, name, location, capacity) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, venue.getVenueId());
            stmt.setString(2, venue.getName());
            stmt.setString(3, venue.getLocation());
            stmt.setInt(4, venue.getCapacity());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Venue> getAllVenues() {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venues ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                venues.add(new Venue(
                    rs.getString("venue_id"),
                    rs.getString("name"),
                    rs.getString("location"),
                    rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venues;
    }
    
    public Venue getVenueById(String venueId) {
        String sql = "SELECT * FROM venues WHERE venue_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, venueId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Venue(
                    rs.getString("venue_id"),
                    rs.getString("name"),
                    rs.getString("location"),
                    rs.getInt("capacity")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
