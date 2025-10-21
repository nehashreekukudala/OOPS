// IDGenerator.java
package util;

import java.util.UUID;

public class IDGenerator {
    public static String generateUserId() {
        return "USER" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
    
    public static String generateEventId() {
        return "EVT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
    
    public static String generateVenueId() {
        return "VEN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
    
    public static String generateTicketId() {
        return "TKT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
