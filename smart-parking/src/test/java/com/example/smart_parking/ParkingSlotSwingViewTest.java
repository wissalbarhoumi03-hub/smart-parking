package com.example.smart_parking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.timeout;

@RunWith(GUITestRunner.class)
public class ParkingSlotSwingViewTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private ParkingSlotSwingView parkingSlotSwingView;
    private static final int TIMEOUT = 5000;

    @Mock
    private ParkingService parkingService;

    private AutoCloseable closeable;

    @Override
    protected void onSetUp() {
        closeable = MockitoAnnotations.openMocks(this);
        GuiActionRunner.execute(() -> {
            parkingSlotSwingView = new ParkingSlotSwingView();
            parkingSlotSwingView.setParkingService(parkingService);
            return parkingSlotSwingView;
        });
        window = new FrameFixture(robot(), parkingSlotSwingView);
        window.show();
    }

    @Override
    protected void onTearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void test() {
        // just to check the setup works
    }

    @Test @GUITest
    public void testControlsInitialStates() {
        window.label(JLabelMatcher.withText("id"));
        window.textBox("idTextBox").requireEnabled();
        window.label(JLabelMatcher.withText("name"));
        window.textBox("nameTextBox").requireEnabled();
        window.button(JButtonMatcher.withText("Add")).requireDisabled();
        window.list("slotList");
        window.button(JButtonMatcher.withText("Mark Occupied")).requireDisabled();
        window.label("errorMessageLabel").requireText(" ");
    }

    @Test
    public void testWhenIdAndNameAreNonEmptyThenAddButtonShouldBeEnabled() {
        window.textBox("idTextBox").enterText("1");
        window.textBox("nameTextBox").enterText("test");
        window.button(JButtonMatcher.withText("Add")).requireEnabled();
    }

    @Test
    public void testWhenEitherIdOrNameAreBlankThenAddButtonShouldBeDisabled() {
        JTextComponentFixture idTextBox = window.textBox("idTextBox");
        JTextComponentFixture nameTextBox = window.textBox("nameTextBox");

        idTextBox.enterText("1");
        nameTextBox.enterText(" ");
        window.button(JButtonMatcher.withText("Add")).requireDisabled();

        idTextBox.setText("");
        nameTextBox.setText("");

        idTextBox.enterText(" ");
        nameTextBox.enterText("test");
        window.button(JButtonMatcher.withText("Add")).requireDisabled();
    }

    @Test
    public void testMarkOccupiedButtonShouldBeEnabledOnlyWhenASlotIsSelected() {
        GuiActionRunner.execute(() ->
            parkingSlotSwingView.getListSlotsModel().addElement(new ParkingSlot("1")));
        GuiActionRunner.execute(() ->
            parkingSlotSwingView.getListSlots().setSelectedIndex(0));
        JButtonFixture markOccupiedButton =
            window.button(JButtonMatcher.withText("Mark Occupied"));
        markOccupiedButton.requireEnabled();
        GuiActionRunner.execute(() ->
            parkingSlotSwingView.getListSlots().clearSelection());
        markOccupiedButton.requireDisabled();
    }

    @Test
    public void testShowAllSlotsShouldAddSlotDescriptionsToTheList() {
        ParkingSlot slot1 = new ParkingSlot("1");
        ParkingSlot slot2 = new ParkingSlot("2");
        GuiActionRunner.execute(() ->
            parkingSlotSwingView.showAllSlots(Arrays.asList(slot1, slot2))
        );
        String[] listContents = window.list().contents();
        assertThat(listContents)
            .containsExactly(slot1.toString(), slot2.toString());
    }

    @Test
    public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
        ParkingSlot slot = new ParkingSlot("1");
        parkingSlotSwingView.showError("error message", slot);
        window.label("errorMessageLabel")
            .requireText("error message: " + slot);
    }

    @Test
    public void testSlotAddedShouldAddTheSlotToTheListAndResetTheErrorLabel() {
        ParkingSlot slot = new ParkingSlot("1");
        parkingSlotSwingView.slotAdded(new ParkingSlot("1"));
        String[] listContents = window.list().contents();
        assertThat(listContents).containsExactly(slot.toString());
        window.label("errorMessageLabel").requireText(" ");
    }

    @Test
    public void testSlotRemovedShouldRemoveTheSlotFromTheListAndResetTheErrorLabel() {
        ParkingSlot slot1 = new ParkingSlot("1");
        ParkingSlot slot2 = new ParkingSlot("2");
        GuiActionRunner.execute(() -> {
            DefaultListModel<ParkingSlot> listSlotsModel =
                parkingSlotSwingView.getListSlotsModel();
            listSlotsModel.addElement(slot1);
            listSlotsModel.addElement(slot2);
        });
        parkingSlotSwingView.slotRemoved(new ParkingSlot("1"));
        String[] listContents = window.list().contents();
        assertThat(listContents).containsExactly(slot2.toString());
        window.label("errorMessageLabel").requireText(" ");
    }
    
    @Test
    public void testAddButtonShouldDelegateToParkingServiceAddSlot() {
        window.textBox("idTextBox").enterText("1");
        window.textBox("nameTextBox").enterText("test");
        window.button(JButtonMatcher.withText("Add")).click();
        verify(parkingService, timeout(TIMEOUT))
            .addSlot(new ParkingSlot("1"));
    }
    
    @Test
    public void testMarkOccupiedButtonShouldDelegateToParkingServiceMarkAsOccupied() {
        ParkingSlot slot1 = new ParkingSlot("1");
        ParkingSlot slot2 = new ParkingSlot("2");
        GuiActionRunner.execute(() -> {
            parkingSlotSwingView.getListSlotsModel().addElement(slot1);
            parkingSlotSwingView.getListSlotsModel().addElement(slot2);
        });
        GuiActionRunner.execute(() ->
            parkingSlotSwingView.getListSlots().setSelectedIndex(1));
        window.button(JButtonMatcher.withText("Mark Occupied")).click();
        verify(parkingService, timeout(TIMEOUT))
            .markAsOccupied(slot2.getId());
    }
}