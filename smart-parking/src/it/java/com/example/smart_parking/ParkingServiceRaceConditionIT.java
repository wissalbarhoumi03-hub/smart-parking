package com.example.smart_parking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class ParkingServiceRaceConditionIT {

    @ClassRule
    public static final MongoDBContainer mongo =
        new MongoDBContainer("mongo:5");

    private MongoClient client;
    private ParkingSlotRepository parkingSlotRepository;
    private AutoCloseable closeable;

    @Before
    public void setUp() {
        client = new MongoClient(
            new ServerAddress(mongo.getHost(), mongo.getFirstMappedPort()));
        MongoDatabase database = client.getDatabase("parking");
        database.drop();
        com.mongodb.client.MongoCollection<org.bson.Document> slotCollection =
            database.getCollection("slot");
        slotCollection.createIndex(
            com.mongodb.client.model.Indexes.ascending("id"),
            new com.mongodb.client.model.IndexOptions().unique(true));
        parkingSlotRepository = new ParkingSlotMongoRepository(client);
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testAddSlotConcurrent() {
        ParkingSlot slot = new ParkingSlot("1");
        List<Thread> threads = IntStream.range(0, 10)
            .mapToObj(i -> new Thread(() -> {
                try {
                    new ParkingService(
                        parkingSlotRepository,
                        mock(ParkingEventLogger.class),
                        mock(ParkingSlotView.class))
                        .addSlot(slot);
                } catch (MongoWriteException e) {
                    e.printStackTrace();
                }
            }))
            .peek(t -> t.start())
            .collect(Collectors.toList());
        await().atMost(10, SECONDS)
            .until(() -> threads.stream().noneMatch(t -> t.isAlive()));
        assertThat(parkingSlotRepository.findAll())
            .containsExactly(slot);
    }
}