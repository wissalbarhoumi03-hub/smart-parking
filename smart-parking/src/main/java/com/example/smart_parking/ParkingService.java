package com.example.smart_parking;
public class ParkingService {

    private ParkingSlotRepository parkingSlotRepository;
    private ParkingEventLogger parkingEventLogger;
    private ParkingSlotView parkingSlotView;

    public ParkingService(ParkingSlotRepository parkingSlotRepository, 
                          ParkingEventLogger parkingEventLogger,
                          ParkingSlotView parkingSlotView) {
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingEventLogger = parkingEventLogger;
        this.parkingSlotView = parkingSlotView;
    }

    public void addSlot(ParkingSlot slot) {
        ParkingSlot existingSlot = parkingSlotRepository.findById(slot.getId());
        if (existingSlot != null) {
            parkingSlotView.showError("Already existing slot with id " + slot.getId(), existingSlot);
            return;
        }
        parkingSlotRepository.save(slot);
        parkingSlotView.slotAdded(slot);
    }
    
    public void allSlots() {
        parkingSlotView.showAllSlots(parkingSlotRepository.findAll());
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
        parkingSlotView.slotRemoved(slot);
        return true;
    }
}