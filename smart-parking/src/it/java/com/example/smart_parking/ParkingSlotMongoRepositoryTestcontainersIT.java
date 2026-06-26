package com.example.smart_parking;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ParkingSlotMongoRepositoryTestcontainersIT {

    @SuppressWarnings("rawtypes")
    @ClassRule
    public static final GenericContainer mongo =
        new GenericContainer("mongo:5")
            .withExposedPorts(27017);

    private MongoClient client;
    private ParkingSlotMongoRepository parkingSlotRepository;
    private MongoCollection<Document> slotCollection;

    @Before
    public void setup() {
        client = new MongoClient(
            new ServerAddress(
                mongo.getHost(),
                mongo.getMappedPort(27017)));
        parkingSlotRepository = new ParkingSlotMongoRepository(client);
        MongoDatabase database = client.getDatabase(
            ParkingSlotMongoRepository.PARKING_DB_NAME);
        database.drop();
        slotCollection = database.getCollection(
            ParkingSlotMongoRepository.SLOT_COLLECTION_NAME);
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testFindAll() {
        addTestSlotToDatabase("1", false);
        addTestSlotToDatabase("2", true);
        assertThat(parkingSlotRepository.findAll())
            .containsExactly(
                new ParkingSlot("1"),
                new ParkingSlot("2"));
    }

    @Test
    public void testFindById() {
        addTestSlotToDatabase("1", false);
        addTestSlotToDatabase("2", true);
        assertThat(parkingSlotRepository.findById("2"))
            .isEqualTo(new ParkingSlot("2"));
    }

    @Test
    public void testSave() {
        ParkingSlot slot = new ParkingSlot("1");
        parkingSlotRepository.save(slot);
        assertThat(readAllSlotsFromDatabase())
            .containsExactly(slot);
    }

    @Test
    public void testDelete() {
        addTestSlotToDatabase("1", false);
        parkingSlotRepository.delete("1");
        assertThat(readAllSlotsFromDatabase())
            .isEmpty();
    }

    private void addTestSlotToDatabase(String id, boolean occupied) {
        slotCollection.insertOne(
            new Document()
                .append("id", id)
                .append("occupied", occupied));
    }

    private List<ParkingSlot> readAllSlotsFromDatabase() {
        return StreamSupport
            .stream(slotCollection.find().spliterator(), false)
            .map(d -> new ParkingSlot("" + d.get("id")))
            .collect(Collectors.toList());
    }

}