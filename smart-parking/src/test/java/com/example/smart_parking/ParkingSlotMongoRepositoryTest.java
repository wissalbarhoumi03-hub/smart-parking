package com.example.smart_parking;

import static org.assertj.core.api.Assertions.*;

import java.net.InetSocketAddress;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ParkingSlotMongoRepositoryTest {

    private static MongoServer server;
    private static InetSocketAddress serverAddress;

    private MongoClient client;
    private ParkingSlotMongoRepository parkingSlotRepository;
    private MongoCollection<Document> slotCollection;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        server = new MongoServer(new MemoryBackend());
        serverAddress = server.bind();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        server.shutdown();
    }

    @Before
    public void setUp() throws Exception {
        client = new MongoClient(new ServerAddress(serverAddress));
        parkingSlotRepository = new ParkingSlotMongoRepository(client);
        MongoDatabase database = client.getDatabase(
            ParkingSlotMongoRepository.PARKING_DB_NAME);
        // make sure we always start with a clean database
        database.drop();
        slotCollection = database.getCollection(
            ParkingSlotMongoRepository.SLOT_COLLECTION_NAME);
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void testFindAllWhenDatabaseIsEmpty() {
        assertThat(parkingSlotRepository.findAll()).isEmpty();
    }
    
    @Test
    public void testFindAllWhenDatabaseIsNotEmpty() {
        addTestSlotToDatabase("1", false);
        addTestSlotToDatabase("2", true);
        assertThat(parkingSlotRepository.findAll())
            .containsExactly(
                new ParkingSlot("1"),
                new ParkingSlot("2"));
    }

    private void addTestSlotToDatabase(String id, boolean occupied) {
        slotCollection.insertOne(
            new Document()
                .append("id", id)
                .append("occupied", occupied));
    }
    
    @Test
    public void testFindByIdNotFound() {
        assertThat(parkingSlotRepository.findById("1"))
            .isNull();
    }

    @Test
    public void testFindByIdFound() {
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

    private List<ParkingSlot> readAllSlotsFromDatabase() {
        return StreamSupport
            .stream(slotCollection.find().spliterator(), false)
            .map(d -> new ParkingSlot("" + d.get("id")))
            .collect(Collectors.toList());
    }

}