package com.example.smart_parking;

import java.time.LocalDateTime;

public class ParkingEvent {

    private String slotId;
    private String eventType;
    private LocalDateTime timestamp;

    public ParkingEvent(String slotId, String eventType, LocalDateTime timestamp) {
        this.slotId = slotId;
        this.eventType = eventType;
        this.timestamp = timestamp;
    }

    public String getSlotId() {
        return slotId;
    }

    public String getEventType() {
        return eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}