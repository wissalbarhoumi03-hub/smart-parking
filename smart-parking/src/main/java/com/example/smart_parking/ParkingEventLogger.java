package com.example.smart_parking;

public interface ParkingEventLogger {
    void log(String slotId, String eventType);
}