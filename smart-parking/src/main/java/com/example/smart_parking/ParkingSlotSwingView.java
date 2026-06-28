package com.example.smart_parking;

import java.awt.EventQueue;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ParkingSlotSwingView extends JFrame implements ParkingSlotView {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtId;
    private JTextField txtName;
    private JButton btnAdd;
    private JList<ParkingSlot> listSlots;
    private DefaultListModel<ParkingSlot> listSlotsModel;
    private JButton btnMarkOccupied;

    DefaultListModel<ParkingSlot> getListSlotsModel() {
        return listSlotsModel;
    }
    
    JList<ParkingSlot> getListSlots() {
        return listSlots;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ParkingSlotSwingView frame = new ParkingSlotSwingView();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ParkingSlotSwingView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Parking Slot Monitor");
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
        gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
        gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        JLabel lblId = new JLabel("id");
        GridBagConstraints gbc_lblId = new GridBagConstraints();
        gbc_lblId.insets = new Insets(0, 0, 5, 5);
        gbc_lblId.anchor = GridBagConstraints.EAST;
        gbc_lblId.gridx = 1;
        gbc_lblId.gridy = 0;
        contentPane.add(lblId, gbc_lblId);

        txtId = new JTextField();
        txtId.setName("idTextBox");
        GridBagConstraints gbc_txtId = new GridBagConstraints();
        gbc_txtId.insets = new Insets(0, 0, 5, 5);
        gbc_txtId.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtId.gridx = 2;
        gbc_txtId.gridy = 0;
        contentPane.add(txtId, gbc_txtId);
        txtId.setColumns(10);

        JLabel lblName = new JLabel("name");
        GridBagConstraints gbc_lblName = new GridBagConstraints();
        gbc_lblName.anchor = GridBagConstraints.EAST;
        gbc_lblName.insets = new Insets(0, 0, 5, 5);
        gbc_lblName.gridx = 1;
        gbc_lblName.gridy = 1;
        contentPane.add(lblName, gbc_lblName);

        txtName = new JTextField();
        txtName.setName("nameTextBox");
        GridBagConstraints gbc_txtName = new GridBagConstraints();
        gbc_txtName.insets = new Insets(0, 0, 5, 5);
        gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtName.gridx = 2;
        gbc_txtName.gridy = 1;
        contentPane.add(txtName, gbc_txtName);
        txtName.setColumns(10);

        KeyAdapter btnAddEnabler = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                btnAdd.setEnabled(
                    !txtId.getText().trim().isEmpty() &&
                    !txtName.getText().trim().isEmpty()
                );
            }
        };
        txtId.addKeyListener(btnAddEnabler);
        txtName.addKeyListener(btnAddEnabler);

        btnAdd = new JButton("Add");
        btnAdd.setEnabled(false);
        GridBagConstraints gbc_btnAdd = new GridBagConstraints();
        gbc_btnAdd.insets = new Insets(0, 0, 5, 5);
        gbc_btnAdd.gridx = 2;
        gbc_btnAdd.gridy = 2;
        contentPane.add(btnAdd, gbc_btnAdd);

        listSlotsModel = new DefaultListModel<>();
        listSlots = new JList<>(listSlotsModel);
        listSlots.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSlots.setName("slotList");
        GridBagConstraints gbc_list = new GridBagConstraints();
        gbc_list.insets = new Insets(0, 0, 5, 5);
        gbc_list.fill = GridBagConstraints.BOTH;
        gbc_list.gridx = 2;
        gbc_list.gridy = 3;
        contentPane.add(listSlots, gbc_list);

        btnMarkOccupied = new JButton("Mark Occupied");
        btnMarkOccupied.setEnabled(false);
        GridBagConstraints gbc_btnMarkOccupied = new GridBagConstraints();
        gbc_btnMarkOccupied.insets = new Insets(0, 0, 5, 5);
        gbc_btnMarkOccupied.gridx = 2;
        gbc_btnMarkOccupied.gridy = 4;
        contentPane.add(btnMarkOccupied, gbc_btnMarkOccupied);

        listSlots.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                btnMarkOccupied.setEnabled(listSlots.getSelectedIndex() != -1);
            }
        });
        
        JLabel lblErrorMessage = new JLabel(" ");
        lblErrorMessage.setName("errorMessageLabel");
        GridBagConstraints gbc_lblErrorMessage = new GridBagConstraints();
        gbc_lblErrorMessage.insets = new Insets(0, 0, 0, 5);
        gbc_lblErrorMessage.gridx = 2;
        gbc_lblErrorMessage.gridy = 5;
        contentPane.add(lblErrorMessage, gbc_lblErrorMessage);
    }

    @Override
    public void showAllSlots(List<ParkingSlot> slots) {
        // TODO Auto-generated method stub
    }

    @Override
    public void showError(String message, ParkingSlot slot) {
        // TODO Auto-generated method stub
    }

    @Override
    public void slotAdded(ParkingSlot slot) {
        // TODO Auto-generated method stub
    }

    @Override
    public void slotRemoved(ParkingSlot slot) {
        // TODO Auto-generated method stub
    }
}