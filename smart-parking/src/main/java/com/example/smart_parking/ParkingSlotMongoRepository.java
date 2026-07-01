package com.example.smart_parking;

import java.util.Collections;
import java.util.List;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ParkingSlotMongoRepository implements ParkingSlotRepository {

    public static final String PARKING_DB_NAME = "parking";
    public static final String SLOT_COLLECTION_NAME = "slot";

    private MongoCollection<Document> slotCollection;

    public ParkingSlotMongoRepository(MongoClient client) {
        slotCollection = client
            .getDatabase(PARKING_DB_NAME)
            .getCollection(SLOT_COLLECTION_NAME);
    }
    
    public ParkingSlotMongoRepository(MongoClient client,
            String databaseName, String collectionName) {
        slotCollection = client
            .getDatabase(databaseName)
            .getCollection(collectionName);
    }

    @Override
    public List<ParkingSlot> findAll() {
        return StreamSupport
            .stream(slotCollection.find().spliterator(), false)
            .map(this::fromDocumentToParkingSlot)
            .collect(Collectors.toList());
    }

    private ParkingSlot fromDocumentToParkingSlot(Document d) {
        ParkingSlot slot = new ParkingSlot("" + d.get("id"));
        slot.setOccupied((boolean) d.get("occupied"));
        return slot;
    }

    @Override
    public ParkingSlot findById(String id) {
        Document d = slotCollection
            .find(Filters.eq("id", id))
            .first();
        if (d != null)
            return fromDocumentToParkingSlot(d);
        return null;
    }

    @Override
    public void save(ParkingSlot slot) {
        slotCollection.insertOne(
            new Document()
                .append("id", slot.getId())
                .append("occupied", slot.isOccupied()));
    }

    @Override
    public void delete(String id) {
        slotCollection.deleteOne(Filters.eq("id", id));
    }

}