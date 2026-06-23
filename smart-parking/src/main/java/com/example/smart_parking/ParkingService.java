package com.example.smart_parking;

public class ParkingService {

    private ParkingSlotRepository parkingSlotRepository;
    private ParkingEventLogger parkingEventLogger;

    public ParkingService(ParkingSlotRepository parkingSlotRepository, ParkingEventLogger parkingEventLogger) {
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingEventLogger = parkingEventLogger;
    }

    public boolean markAsOccupied(String slotId) {
        ParkingSlot slot = parkingSlotRepository.findById(slotId);
        if (slot == null) {
            return false;
        }
        if (slot.isOccupied()) {
            return false;
        }
        slot.setOccupied(true);
        parkingEventLogger.log(slotId, "OCCUPIED");
        return true;
    }

}