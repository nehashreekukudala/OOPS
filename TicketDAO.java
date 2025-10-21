// TicketDAO.java
package dao;

import model.Ticket;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketDAO {
    
    public boolean bookTicket(Ticket ticket) {
        String sql = "INSERT INTO tickets (ticket_id, event_id, attendee_id, ticket_type, price, is_booked, qr_code, serial_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ticket.getTicketId());
            stmt.setString(2, ticket.getEventId());
            stmt.setString(3, ticket.getAttendeeId());
            stmt.setString(4, ticket.getTicketType());
            stmt.setDouble(5, ticket.getPrice());
            stmt.setBoolean(6, true);
            stmt.setString(7, ticket.getQrCode());
            stmt.setString(8, ticket.getSerialNumber());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Ticket> getTicketsByAttendee(String attendeeId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE attendee_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, attendeeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tickets.add(new Ticket(
                    rs.getString("ticket_id"),
                    rs.getString("event_id"),
                    rs.getString("attendee_id"),
                    rs.getString("ticket_type"),
                    rs.getDouble("price"),
                    rs.getBoolean("is_booked"),
                    rs.getBoolean("is_used"),
                    rs.getString("qr_code"),
                    rs.getString("serial_number")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }
    
    public Ticket getTicketById(String ticketId) {
        String sql = "SELECT * FROM tickets WHERE ticket_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ticketId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Ticket(
                    rs.getString("ticket_id"),
                    rs.getString("event_id"),
                    rs.getString("attendee_id"),
                    rs.getString("ticket_type"),
                    rs.getDouble("price"),
                    rs.getBoolean("is_booked"),
                    rs.getBoolean("is_used"),
                    rs.getString("qr_code"),
                    rs.getString("serial_number")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean validateTicket(String ticketId, String verifierId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check ticket validity
            String checkSql = "SELECT * FROM tickets WHERE ticket_id = ? AND is_booked = true AND is_used = false";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, ticketId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                // Log failed check-in
                logCheckIn(conn, ticketId, verifierId, "Failed", "Invalid ticket");
                conn.commit();
                return false;
            }
            
            // Update ticket as used
            String updateSql = "UPDATE tickets SET is_used = true, check_in_time = CURRENT_TIMESTAMP WHERE ticket_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, ticketId);
            updateStmt.executeUpdate();
            
            // Log successful check-in
            logCheckIn(conn, ticketId, verifierId, "Success", null);
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void logCheckIn(Connection conn, String ticketId, String verifierId, String status, String reason) throws SQLException {
        String sql = "INSERT INTO check_ins (ticket_id, verifier_id, status, reason) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, ticketId);
        stmt.setString(2, verifierId);
        stmt.setString(3, status);
        stmt.setString(4, reason);
        stmt.executeUpdate();
    }
    
    public String generateUniqueTicketId() {
        return "TKT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public String generateQRCode() {
        return "QR_" + UUID.randomUUID().toString();
    }
    
    public String generateSerialNumber() {
        return "SN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
