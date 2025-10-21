// OrganizerDashboardFrame.java
package view;

import dao.EventDAO;
import dao.VenueDAO;
import model.Event;
import model.User;
import model.Venue;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.UUID;

public class OrganizerDashboardFrame extends JFrame {
    private User currentUser;
    private EventDAO eventDAO;
    private VenueDAO venueDAO;
    
    private JTabbedPane tabbedPane;
    private JTable eventsTable;
    private DefaultTableModel eventsTableModel;
    
    public OrganizerDashboardFrame(User user) {
        this.currentUser = user;
        eventDAO = new EventDAO();
        venueDAO = new VenueDAO();
        initializeUI();
        loadEvents();
    }
    
    private void initializeUI() {
        setTitle("Event Management System - Organizer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(70, 130, 180));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + " (Organizer)");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Logout");
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed pane for different functionalities
        tabbedPane = new JTabbedPane();
        
        // Events Management Tab
        JPanel eventsPanel = createEventsPanel();
        tabbedPane.addTab("My Events", eventsPanel);
        
        // Create Event Tab
        JPanel createEventPanel = createEventPanel();
        tabbedPane.addTab("Create Event", createEventPanel);
        
        // Venues Tab
        JPanel venuesPanel = createVenuesPanel();
        tabbedPane.addTab("Manage Venues", venuesPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Event listeners
        logoutButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }
    
    private JPanel createEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table for events
        String[] columns = {"Event ID", "Name", "Date", "Time", "Venue", "Price", "Total Tickets", "Available Tickets"};
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
        JButton viewReportButton = new JButton("View Report");
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewReportButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Event listeners
        refreshButton.addActionListener(e -> loadEvents());
        viewReportButton.addActionListener(e -> showEventReport());
        
        return panel;
    }
    
    private JPanel createEventPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField nameField = new JTextField();
        JTextArea descriptionArea = new JTextArea(3, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        JTextField dateField = new JTextField(); // In production, use JDatePicker
        JTextField timeField = new JTextField(); // In production, use JTimePicker
        JComboBox<Venue> venueComboBox = new JComboBox<>();
        JTextField priceField = new JTextField();
        JTextField totalTicketsField = new JTextField();
        
        JButton createButton = new JButton("Create Event");
        
        // Load venues
        List<Venue> venues = venueDAO.getAllVenues();
        for (Venue venue : venues) {
            venueComboBox.addItem(venue);
        }
        
        panel.add(new JLabel("Event Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionScroll);
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Time (HH:MM:SS):"));
        panel.add(timeField);
        panel.add(new JLabel("Venue:"));
        panel.add(venueComboBox);
        panel.add(new JLabel("Ticket Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Total Tickets:"));
        panel.add(totalTicketsField);
        panel.add(new JLabel());
        panel.add(createButton);
        
        createButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String description = descriptionArea.getText().trim();
                Date date = Date.valueOf(dateField.getText());
                Time time = Time.valueOf(timeField.getText());
                Venue selectedVenue = (Venue) venueComboBox.getSelectedItem();
                double price = Double.parseDouble(priceField.getText());
                int totalTickets = Integer.parseInt(totalTicketsField.getText());
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Event name is required!");
                    return;
                }
                
                // Check venue availability
                if (!eventDAO.isVenueAvailable(selectedVenue.getVenueId(), date, time)) {
                    JOptionPane.showMessageDialog(this, "Venue is not available at the selected date and time!");
                    return;
                }
                
                // Generate event ID
                String eventId = "EVT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                
                Event event = new Event(eventId, name, description, date, time, 
                                      selectedVenue.getVenueId(), currentUser.getUserId(), 
                                      price, totalTickets, totalTickets);
                
                if (eventDAO.createEvent(event)) {
                    JOptionPane.showMessageDialog(this, "Event created successfully!");
                    // Clear fields
                    nameField.setText("");
                    descriptionArea.setText("");
                    dateField.setText("");
                    timeField.setText("");
                    priceField.setText("");
                    totalTicketsField.setText("");
                    loadEvents();
                    tabbedPane.setSelectedIndex(0);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create event!");
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input! Please check your data.");
                ex.printStackTrace();
            }
        });
        
        return panel;
    }
    
    private JPanel createVenuesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField capacityField = new JTextField();
        JButton createButton = new JButton("Create Venue");
        
        formPanel.add(new JLabel("Venue Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Capacity:"));
        formPanel.add(capacityField);
        formPanel.add(new JLabel());
        formPanel.add(createButton);
        
        // Venues list
        DefaultTableModel venuesModel = new DefaultTableModel(new String[]{"Venue ID", "Name", "Location", "Capacity"}, 0);
        JTable venuesTable = new JTable(venuesModel);
        JScrollPane scrollPane = new JScrollPane(venuesTable);
        
        // Load venues
        loadVenuesTable(venuesModel);
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        createButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String location = locationField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText());
                
                if (name.isEmpty() || location.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields!");
                    return;
                }
                
                String venueId = "VEN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                Venue venue = new Venue(venueId, name, location, capacity);
                
                if (venueDAO.createVenue(venue)) {
                    JOptionPane.showMessageDialog(this, "Venue created successfully!");
                    nameField.setText("");
                    locationField.setText("");
                    capacityField.setText("");
                    loadVenuesTable(venuesModel);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create venue!");
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid capacity value!");
            }
        });
        
        return panel;
    }
    
    private void loadEvents() {
        eventsTableModel.setRowCount(0);
        List<Event> events = eventDAO.getEventsByOrganizer(currentUser.getUserId());
        
        for (Event event : events) {
            Venue venue = venueDAO.getVenueById(event.getVenueId());
            String venueName = venue != null ? venue.getName() : "Unknown";
            
            eventsTableModel.addRow(new Object[]{
                event.getEventId(),
                event.getName(),
                event.getDate(),
                event.getTime(),
                venueName,
                event.getTicketPrice(),
                event.getTotalTickets(),
                event.getAvailableTickets()
            });
        }
    }
    
    private void loadVenuesTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<Venue> venues = venueDAO.getAllVenues();
        
        for (Venue venue : venues) {
            model.addRow(new Object[]{
                venue.getVenueId(),
                venue.getName(),
                venue.getLocation(),
                venue.getCapacity()
            });
        }
    }
    
    private void showEventReport() {
        List<Event> events = eventDAO.getEventsByOrganizer(currentUser.getUserId());
        
        if (events.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No events found!");
            return;
        }
        
        StringBuilder report = new StringBuilder();
        report.append("Event Report for ").append(currentUser.getName()).append("\n\n");
        
        double totalRevenue = 0;
        int totalTicketsSold = 0;
        
        for (Event event : events) {
            int ticketsSold = event.getTotalTickets() - event.getAvailableTickets();
            double eventRevenue = ticketsSold * event.getTicketPrice();
            
            report.append("Event: ").append(event.getName()).append("\n");
            report.append("Tickets Sold: ").append(ticketsSold).append("/").append(event.getTotalTickets()).append("\n");
            report.append("Revenue: $").append(String.format("%.2f", eventRevenue)).append("\n");
            report.append("----------------------------\n");
            
            totalTicketsSold += ticketsSold;
            totalRevenue += eventRevenue;
        }
        
        report.append("\nTOTAL SUMMARY:\n");
        report.append("Total Tickets Sold: ").append(totalTicketsSold).append("\n");
        report.append("Total Revenue: $").append(String.format("%.2f", totalRevenue));
        
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Event Report", JOptionPane.INFORMATION_MESSAGE);
    }
}
