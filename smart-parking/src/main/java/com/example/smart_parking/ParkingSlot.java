package com.example.smart_parking;

public class ParkingSlot {

    private String id;
    private boolean occupied;

    public ParkingSlot(String id) {
        this.id = id;
        this.occupied = false;
    }

    public String getId() {
        return id;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

}