// Event.java
package model;

import java.sql.Date;
import java.sql.Time;

public class Event {
    private String eventId;
    private String name;
    private String description;
    private Date date;
    private Time time;
    private String venueId;
    private String organizerId;
    private double ticketPrice;
    private int totalTickets;
    private int availableTickets;
    
    public Event(String eventId, String name, String description, Date date, Time time, 
                String venueId, String organizerId, double ticketPrice, int totalTickets, int availableTickets) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venueId = venueId;
        this.organizerId = organizerId;
        this.ticketPrice = ticketPrice;
        this.totalTickets = totalTickets;
        this.availableTickets = availableTickets;
    }
    
    // Getters and setters
    public String getEventId() { return eventId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Date getDate() { return date; }
    public Time getTime() { return time; }
    public String getVenueId() { return venueId; }
    public String getOrganizerId() { return organizerId; }
    public double getTicketPrice() { return ticketPrice; }
    public int getTotalTickets() { return totalTickets; }
    public int getAvailableTickets() { return availableTickets; }
}
