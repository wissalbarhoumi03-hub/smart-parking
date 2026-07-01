package com.example.smart_parking;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(GUITestRunner.class)
public class ModelViewControllerIT extends AssertJSwingJUnitTestCase {

    @ClassRule
    public static final MongoDBContainer mongo =
        new MongoDBContainer("mongo:5");

    private MongoClient mongoClient;
    private FrameFixture window;
    private ParkingService parkingService;
    private ParkingSlotMongoRepository parkingSlotRepository;
    private ParkingSlotSwingView parkingSlotSwingView;

    @Override
    protected void onSetUp() {
        mongoClient = new MongoClient(
            new ServerAddress(
                mongo.getHost(),
                mongo.getFirstMappedPort()));
        parkingSlotRepository = new ParkingSlotMongoRepository(mongoClient);
        for (ParkingSlot slot : parkingSlotRepository.findAll()) {
            parkingSlotRepository.delete(slot.getId());
        }
        window = new FrameFixture(robot(), GuiActionRunner.execute(() -> {
        	parkingSlotSwingView = new ParkingSlotSwingView();
            parkingService = new ParkingService(
                parkingSlotRepository,
                (id, event) -> {},
                parkingSlotSwingView);
            parkingSlotSwingView.setParkingService(parkingService);
            return parkingSlotSwingView;
        }));
        window.show();
    }

    @Override
    protected void onTearDown() {
        mongoClient.close();
    }

    @Test
    public void testAddSlot() {
        window.textBox("idTextBox").enterText("1");
        window.textBox("nameTextBox").enterText("test");
        window.button(JButtonMatcher.withText("Add")).click();
        await().atMost(5, SECONDS).untilAsserted(() ->
            assertThat(parkingSlotRepository.findById("1"))
                .isEqualTo(new ParkingSlot("1"))
        );
    }

    @Test
    public void testMarkOccupied() {
        parkingSlotRepository.save(new ParkingSlot("99"));
        GuiActionRunner.execute(
            () -> parkingService.allSlots());
        GuiActionRunner.execute(() ->
            parkingSlotSwingView.getListSlots().setSelectedIndex(0));
        window.button(JButtonMatcher.withText("Mark Occupied")).click();
        await().atMost(5, SECONDS).untilAsserted(() ->
            assertThat(parkingSlotRepository.findById("99"))
                .isNull()
        );
    }
}