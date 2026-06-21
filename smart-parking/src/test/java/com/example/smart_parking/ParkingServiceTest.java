package com.example.smart_parking;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ParkingServiceTest {

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    @Mock
    private ParkingEventLogger parkingEventLogger;

    @InjectMocks
    private ParkingService parkingService;

    private AutoCloseable closeable;

    @Before
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void testMarkAsOccupiedWhenSlotDoesNotExist() {
        when(parkingSlotRepository.findById("slot1")).thenReturn(null);

        boolean result = parkingService.markAsOccupied("slot1");

        assertThat(result).isFalse();
    }

    @Test
    public void testMarkAsOccupiedWhenSlotExists() {
        ParkingSlot slot = new ParkingSlot("slot1");
        when(parkingSlotRepository.findById("slot1")).thenReturn(slot);

        boolean result = parkingService.markAsOccupied("slot1");

        assertThat(result).isTrue();
        verify(parkingEventLogger).log("slot1", "OCCUPIED");
    }

    @Test
    public void testSlotSetOccupiedIsCalledWhenMarkingAsOccupied() {
        ParkingSlot slot = spy(new ParkingSlot("slot1"));
        when(parkingSlotRepository.findById("slot1")).thenReturn(slot);

        parkingService.markAsOccupied("slot1");

        verify(slot).setOccupied(true);
    }

    @Test
    public void testMarkAsOccupiedWhenLoggerThrowsException() {
        ParkingSlot slot = spy(new ParkingSlot("slot1"));
        when(parkingSlotRepository.findById("slot1")).thenReturn(slot);
        doThrow(new RuntimeException())
            .when(parkingEventLogger).log(anyString(), anyString());

        assertThatThrownBy(() -> parkingService.markAsOccupied("slot1"))
            .isInstanceOf(RuntimeException.class);

        verify(slot).setOccupied(true);
    }

}