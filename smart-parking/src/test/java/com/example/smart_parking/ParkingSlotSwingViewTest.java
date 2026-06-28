package com.example.smart_parking;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.fixture.JButtonFixture;

@RunWith(GUITestRunner.class)
public class ParkingSlotSwingViewTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private ParkingSlotSwingView parkingSlotSwingView;

    @Override
    protected void onSetUp() {
        GuiActionRunner.execute(() -> {
            parkingSlotSwingView = new ParkingSlotSwingView();
            return parkingSlotSwingView;
        });
        window = new FrameFixture(robot(), parkingSlotSwingView);
        window.show();
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
    
    
}