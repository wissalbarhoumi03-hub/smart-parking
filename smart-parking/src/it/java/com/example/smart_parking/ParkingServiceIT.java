package com.example.smart_parking;

import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class ParkingServiceIT {

    @Mock
    private ParkingEventLogger eventLogger;
    
    @Mock
    private ParkingSlotView parkingSlotView;

    private ParkingSlotRepository repository;
    private ParkingService service;
    private AutoCloseable closeable;
    private MongoClient client;

    private static int mongoPort =
        Integer.parseInt(System.getProperty("mongo.port", "27017"));

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        client = new MongoClient(new ServerAddress("localhost", mongoPort));
        repository = new ParkingSlotMongoRepository(client);
        for (ParkingSlot slot : repository.findAll()) {
            repository.delete(slot.getId());
        }
        service = new ParkingService(repository, eventLogger, parkingSlotView );
    }

    @After
    public void tearDown() throws Exception {
        client.close();
        closeable.close();
    }

    @Test
    public void testMarkAsOccupied() {
        ParkingSlot slot = new ParkingSlot("1");
        repository.save(slot);
        service.markAsOccupied("1");
        verify(eventLogger).log("1", "OCCUPIED");
    }
}