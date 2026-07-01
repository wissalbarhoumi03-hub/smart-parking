package com.example.smart_parking;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class App implements Callable<Void> {

    @Option(names = { "--mongo-host" }, description = "MongoDB host address")
    private String mongoHost = "localhost";

    @Option(names = { "--mongo-port" }, description = "MongoDB host port")
    private int mongoPort = 27017;

    @Option(names = { "--db-name" }, description = "Database name")
    private String databaseName = ParkingSlotMongoRepository.PARKING_DB_NAME;

    @Option(names = { "--db-collection" }, description = "Collection name")
    private String collectionName = ParkingSlotMongoRepository.SLOT_COLLECTION_NAME;

    public static void main(String[] args) {
        new CommandLine(new App()).execute(args);
    }

    @Override
    public Void call() throws Exception {
        EventQueue.invokeLater(() -> {
            try {
                ParkingSlotMongoRepository parkingSlotRepository =
                    new ParkingSlotMongoRepository(
                        new MongoClient(new ServerAddress(mongoHost, mongoPort)),
                        databaseName, collectionName);
                ParkingSlotSwingView parkingSlotSwingView = new ParkingSlotSwingView();
                ParkingService parkingService = new ParkingService(
                    parkingSlotRepository,
                    (id, event) -> {},
                    parkingSlotSwingView);
                parkingSlotSwingView.setParkingService(parkingService);
                parkingSlotSwingView.setVisible(true);
                parkingService.allSlots();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return null;
    }
}