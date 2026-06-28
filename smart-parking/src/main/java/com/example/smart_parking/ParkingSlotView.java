package com.example.smart_parking;

import java.util.List;

public interface ParkingSlotView {
    void showAllSlots(List<ParkingSlot> slots);
    void showError(String message, ParkingSlot slot);
    void slotAdded(ParkingSlot slot);
    void slotRemoved(ParkingSlot slot);
}