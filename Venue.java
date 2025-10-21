// Venue.java
package model;

public class Venue {
    private String venueId;
    private String name;
    private String location;
    private int capacity;
    
    public Venue(String venueId, String name, String location, int capacity) {
        this.venueId = venueId;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
    }
    
    // Getters and setters
    public String getVenueId() { return venueId; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
}
