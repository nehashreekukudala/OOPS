// Ticket.java
package model;

public class Ticket {
    private String ticketId;
    private String eventId;
    private String attendeeId;
    private String ticketType;
    private double price;
    private boolean isBooked;
    private boolean isUsed;
    private String qrCode;
    private String serialNumber;
    
    public Ticket(String ticketId, String eventId, String attendeeId, String ticketType, 
                 double price, boolean isBooked, boolean isUsed, String qrCode, String serialNumber) {
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.attendeeId = attendeeId;
        this.ticketType = ticketType;
        this.price = price;
        this.isBooked = isBooked;
        this.isUsed = isUsed;
        this.qrCode = qrCode;
        this.serialNumber = serialNumber;
    }
    
    // Getters and setters
    public String getTicketId() { return ticketId; }
    public String getEventId() { return eventId; }
    public String getAttendeeId() { return attendeeId; }
    public String getTicketType() { return ticketType; }
    public double getPrice() { return price; }
    public boolean isBooked() { return isBooked; }
    public boolean isUsed() { return isUsed; }
    public String getQrCode() { return qrCode; }
    public String getSerialNumber() { return serialNumber; }
}
