package com.example.smart_parking;

import java.util.List;

public interface ParkingSlotRepository {
    List<ParkingSlot> findAll();
    ParkingSlot findById(String id);
    void save(ParkingSlot slot);
    void delete(String id);
}