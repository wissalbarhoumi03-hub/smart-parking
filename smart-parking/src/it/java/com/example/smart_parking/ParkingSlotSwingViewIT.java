package com.example.smart_parking;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

import static org.assertj.swing.timing.Pause.pause;
import static org.assertj.swing.timing.Timeout.timeout;
import org.assertj.swing.timing.Condition;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(GUITestRunner.class)
public class ParkingSlotSwingViewIT extends AssertJSwingJUnitTestCase {

    private static MongoServer server;
    private static InetSocketAddress serverAddress;

    private MongoClient mongoClient;
    private FrameFixture window;
    private ParkingSlotSwingView parkingSlotSwingView;
    private ParkingService parkingService;
    private ParkingSlotMongoRepository parkingSlotRepository;

    @BeforeClass
    public static void setupServer() {
        server = new MongoServer(new MemoryBackend());
        serverAddress = server.bind();
    }

    @AfterClass
    public static void shutdownServer() {
        server.shutdown();
    }

    @Override
    protected void onSetUp() {
        mongoClient = new MongoClient(new ServerAddress(serverAddress));
        parkingSlotRepository = new ParkingSlotMongoRepository(mongoClient);
        for (ParkingSlot slot : parkingSlotRepository.findAll()) {
            parkingSlotRepository.delete(slot.getId());
        }
        GuiActionRunner.execute(() -> {
            parkingSlotSwingView = new ParkingSlotSwingView();
            parkingService = new ParkingService(
                parkingSlotRepository,
                (id, event) -> {},
                parkingSlotSwingView);
            parkingSlotSwingView.setParkingService(parkingService);
            return parkingSlotSwingView;
        });
        window = new FrameFixture(robot(), parkingSlotSwingView);
        window.show();
    }

    @Override
    protected void onTearDown() {
        mongoClient.close();
    }
    
    @Test @org.assertj.swing.annotation.GUITest
    public void testAllSlots() {
        ParkingSlot slot1 = new ParkingSlot("1");
        ParkingSlot slot2 = new ParkingSlot("2");
        parkingSlotRepository.save(slot1);
        parkingSlotRepository.save(slot2);
        GuiActionRunner.execute(() ->
            parkingService.allSlots());
        assertThat(window.list().contents())
            .containsExactly(slot1.toString(), slot2.toString());
    }
    
    @Test @org.assertj.swing.annotation.GUITest
    public void testAddButtonSuccess() {
        window.textBox("idTextBox").enterText("1");
        window.textBox("nameTextBox").enterText("test");
        window.button(JButtonMatcher.withText("Add")).click();
        await().atMost(5, SECONDS).untilAsserted(() ->
            assertThat(window.list().contents())
                .containsExactly(new ParkingSlot("1").toString())
        );
    }
    
    @Test @org.assertj.swing.annotation.GUITest
    public void testAddButtonError() {
        parkingSlotRepository.save(new ParkingSlot("1"));
        window.textBox("idTextBox").enterText("1");
        window.textBox("nameTextBox").enterText("test");
        window.button(JButtonMatcher.withText("Add")).click();
        pause(new Condition("Error label updated") {
            @Override
            public boolean test() {
                return window.label("errorMessageLabel").text()
                    .equals("Already existing slot with id 1: "
                        + new ParkingSlot("1"));
            }
        }, timeout(5000));
        assertThat(window.list().contents()).isEmpty();
        window.label("errorMessageLabel")
            .requireText("Already existing slot with id 1: "
                + new ParkingSlot("1"));
    }
    
    @Test @org.assertj.swing.annotation.GUITest
    public void testMarkOccupiedButtonSuccess() {
        GuiActionRunner.execute(() ->
            parkingService.addSlot(new ParkingSlot("1")));
        GuiActionRunner.execute(() ->
            parkingSlotSwingView.getListSlots().setSelectedIndex(0));
        window.button(JButtonMatcher.withText("Mark Occupied")).click();
        await().atMost(5, SECONDS).untilAsserted(() ->
            assertThat(window.list().contents()).isEmpty()
        );
    }
    
    @Test @org.assertj.swing.annotation.GUITest
    public void testMarkOccupiedButtonError() {
        ParkingSlot slot = new ParkingSlot("1");
        GuiActionRunner.execute(() ->
            parkingSlotSwingView.getListSlotsModel().addElement(slot));
        GuiActionRunner.execute(() ->
            parkingSlotSwingView.getListSlots().setSelectedIndex(0));
        window.button(JButtonMatcher.withText("Mark Occupied")).click();
        assertThat(window.list().contents())
            .containsExactly(slot.toString());
        window.label("errorMessageLabel")
            .requireText(" ");
    }
}