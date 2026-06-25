package com.example.smart_parking;

import java.util.Objects;

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
    
    
    @Override
	public int hashCode() {
		return Objects.hash(id);
	}

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParkingSlot other = (ParkingSlot) obj;
		return Objects.equals(id, other.id);
	}

}