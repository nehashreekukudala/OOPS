// AttendeeDashboardFrame.java
package view;

import dao.EventDAO;
import dao.TicketDAO;
import dao.UserDAO;
import model.Event;
import model.Ticket;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AttendeeDashboardFrame extends JFrame {
    private User currentUser;
    private EventDAO eventDAO;
    private TicketDAO ticketDAO;
    
    private JTabbedPane tabbedPane;
    private JTable eventsTable;
    private DefaultTableModel eventsTableModel;
    private JTable ticketsTable;
    private DefaultTableModel ticketsTableModel;
    
    public AttendeeDashboardFrame(User user) {
        this.currentUser = user;
        eventDAO = new EventDAO();
        ticketDAO = new TicketDAO();
        initializeUI();
        loadEvents();
        loadTickets();
    }
    
    private void initializeUI() {
        setTitle("Event Management System - Attendee Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(70, 130, 180));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + " (Attendee)");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Logout");
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Browse Events Tab
        JPanel eventsPanel = createEventsPanel();
        tabbedPane.addTab("Browse Events", eventsPanel);
        
        // My Tickets Tab
        JPanel ticketsPanel = createTicketsPanel();
        tabbedPane.addTab("My Tickets", ticketsPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        logoutButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }
    
    private JPanel createEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Events table
        String[] columns = {"Event ID", "Name", "Date", "Time", "Venue", "Price", "Available Tickets"};
        eventsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventsTable = new JTable(eventsTableModel);
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh");
        JButton bookButton = new JButton("Book Ticket");
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(bookButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        refreshButton.addActionListener(e -> loadEvents());
        bookButton.addActionListener(e -> bookTicket());
        
        return panel;
    }
    
    private JPanel createTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Tickets table
        String[] columns = {"Ticket ID", "Event", "Type", "Price", "Status", "Check-in Time"};
        ticketsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ticketsTable = new JTable(ticketsTableModel);
        JScrollPane scrollPane = new JScrollPane(ticketsTable);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(refreshButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        refreshButton.addActionListener(e -> loadTickets());
        
        return panel;
    }
    
    private void loadEvents() {
        eventsTableModel.setRowCount(0);
        List<Event> events = eventDAO.getAllEvents();
        
        for (Event event : events) {
            // Only show events with available tickets
            if (event.getAvailableTickets() > 0) {
                eventsTableModel.addRow(new Object[]{
                    event.getEventId(),
                    event.getName(),
                    event.getDate(),
                    event.getTime(),
                    event.getVenueId(), // In production, get venue name
                    String.format("$%.2f", event.getTicketPrice()),
                    event.getAvailableTickets()
                });
            }
        }
    }
    
    private void loadTickets() {
        ticketsTableModel.setRowCount(0);
        List<Ticket> tickets = ticketDAO.getTicketsByAttendee(currentUser.getUserId());
        
        for (Ticket ticket : tickets) {
            Event event = getEventById(ticket.getEventId());
            String eventName = event != null ? event.getName() : "Unknown Event";
            String status = ticket.isUsed() ? "Used" : "Valid";
            String checkInTime = ticket.isUsed() ? "Checked In" : "Not Checked In";
            
            ticketsTableModel.addRow(new Object[]{
                ticket.getTicketId(),
                eventName,
                ticket.getTicketType(),
                String.format("$%.2f", ticket.getPrice()),
                status,
                checkInTime
            });
        }
    }
    
    private void bookTicket() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to book!");
            return;
        }
        
        String eventId = (String) eventsTableModel.getValueAt(selectedRow, 0);
        String eventName = (String) eventsTableModel.getValueAt(selectedRow, 1);
        double price = Double.parseDouble(eventsTableModel.getValueAt(selectedRow, 5).toString().replace("$", ""));
        
        // Show ticket type selection
        String[] options = {"Digital Ticket", "Physical Ticket"};
        int choice = JOptionPane.showOptionDialog(this,
            "Select ticket type for: " + eventName + "\nPrice: $" + price,
            "Book Ticket",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == -1) return; // User cancelled
        
        String ticketType = choice == 0 ? "Digital" : "Physical";
        
        // Simulate payment
        int paymentConfirm = JOptionPane.showConfirmDialog(this,
            "Confirm payment of $" + price + " for " + ticketType + " ticket?",
            "Payment Confirmation",
            JOptionPane.YES_NO_OPTION);
        
        if (paymentConfirm == JOptionPane.YES_OPTION) {
            // Generate ticket
            String ticketId = ticketDAO.generateUniqueTicketId();
            String qrCode = ticketType.equals("Digital") ? ticketDAO.generateQRCode() : null;
            String serialNumber = ticketType.equals("Physical") ? ticketDAO.generateSerialNumber() : null;
            
            Ticket ticket = new Ticket(ticketId, eventId, currentUser.getUserId(), ticketType, price, true, false, qrCode, serialNumber);
            
            if (ticketDAO.bookTicket(ticket)) {
                // Update available tickets
                Event event = getEventById(eventId);
                if (event != null) {
                    eventDAO.updateAvailableTickets(eventId, event.getAvailableTickets() - 1);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Ticket booked successfully!\n" +
                    "Ticket ID: " + ticketId + "\n" +
                    (qrCode != null ? "QR Code: " + qrCode : "Serial Number: " + serialNumber));
                
                loadEvents();
                loadTickets();
                tabbedPane.setSelectedIndex(1); // Switch to My Tickets tab
            } else {
                JOptionPane.showMessageDialog(this, "Failed to book ticket!");
            }
        }
    }
    
    private Event getEventById(String eventId) {
        List<Event> events = eventDAO.getAllEvents();
        for (Event event : events) {
            if (event.getEventId().equals(eventId)) {
                return event;
            }
        }
        return null;
    }
}
