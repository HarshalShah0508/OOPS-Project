package com.harshalshah.floorplanner.ui;

import com.harshalshah.floorplanner.io.FloorPlanRepository;
import com.harshalshah.floorplanner.model.FurnitureType;
import com.harshalshah.floorplanner.model.RoomType;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

/**
 * The application window: a control panel of tools on the right, a
 * {@link JLayeredPane} canvas for the floor plan on the left.
 *
 * <p>Renamed from the original {@code TFrame} and rebuilt around one
 * structural fix: every canvas item (room, door, window, furniture) is
 * added to {@code layeredPane} and nowhere else. The original mixed
 * {@code layeredPane.add(...)} inside item-creation helpers with a
 * separate, later {@code this.add(...)} on the frame itself — and because
 * a Swing component can only have one parent, the second call silently
 * reparented every item onto the frame's content pane, making the
 * layered-pane structure a no-op. Room selection highlighting
 * (`getContentPane().getComponents()`) happened to still work only
 * because of that same accidental reparenting.
 */
public class MainFrame extends JFrame {

    private final JLayeredPane layeredPane = new JLayeredPane();
    private final FloorPlanRepository repository = new FloorPlanRepository(this, layeredPane);

    private JPanel roomTypeDialog;
    private JPanel dimensionDialog;
    private JPanel furnitureDialog;

    private RoomType pendingRoomType;
    private RoomPanel selectedRoom;

    public MainFrame() {
        JPanel controlPanel = buildControlPanel();
        roomTypeDialog = buildRoomTypeDialog();
        dimensionDialog = buildDimensionDialog();
        furnitureDialog = buildFurnitureDialog();

        layeredPane.setBounds(0, 0, 1600, 900);
        layeredPane.setOpaque(false);
        layeredPane.add(controlPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(roomTypeDialog, JLayeredPane.POPUP_LAYER);
        layeredPane.add(dimensionDialog, JLayeredPane.POPUP_LAYER);
        layeredPane.add(furnitureDialog, JLayeredPane.POPUP_LAYER);

        setTitle("2D Floor Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0xfcfcfc));
        setLayout(null);
        add(layeredPane);
        setVisible(true);
    }

    // ---------------------------------------------------------------
    // Control panel
    // ---------------------------------------------------------------

    private JPanel buildControlPanel() {
        JButton roomBtn = actionButton(55, 70, "+ Add Room", new Color(0xefefef));
        roomBtn.addActionListener(e -> roomTypeDialog.setVisible(true));

        JButton doorBtn = actionButton(55, 136, "+ Add Door", new Color(0xefefef));
        doorBtn.addActionListener(e -> placeOnCanvas(new DoorPanel(), 670, 600));

        JButton windowBtn = actionButton(55, 204, "+ Add Window", new Color(0xefefef));
        windowBtn.addActionListener(e -> placeOnCanvas(new WindowPanel(), 600, 600));

        JButton furnitureBtn = actionButton(55, 272, "+ Add Furniture", new Color(0xefefef));
        furnitureBtn.addActionListener(e -> furnitureDialog.setVisible(true));

        JButton saveBtn = actionButton(55, 540, "Save Plan", new Color(0x71b340));
        saveBtn.setForeground(new Color(0x003e20));
        saveBtn.addActionListener(e -> repository.save());

        JButton loadBtn = actionButton(55, 607, "Load Plan", new Color(0x40baff));
        loadBtn.setForeground(new Color(0x00285b));
        loadBtn.addActionListener(e -> repository.load());

        JPanel panel = new JPanel();
        panel.setBounds(960, 0, 320, 720);
        panel.setBackground(new Color(0x5a5858));
        panel.setLayout(null);
        panel.add(roomBtn);
        panel.add(doorBtn);
        panel.add(windowBtn);
        panel.add(furnitureBtn);
        panel.add(saveBtn);
        panel.add(loadBtn);
        return panel;
    }

    private JButton actionButton(int x, int y, String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 215, 55);
        button.setFocusable(false);
        button.setBackground(bgColor);
        button.setFont(new Font("Helvetica Neue", Font.PLAIN, 22));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(new Color(0x434343), 5));
        return button;
    }

    // ---------------------------------------------------------------
    // Room type + dimensions dialogs
    // ---------------------------------------------------------------

    private JPanel buildRoomTypeDialog() {
        JPanel dialog = popupPanel();

        JLabel heading = dialogHeading("Room Type", 10, 100);

        JButton bedroom = dialogButton(10, 130, RoomType.BEDROOM);
        JButton bathroom = dialogButton(150, 130, RoomType.BATHROOM);
        JButton drawingArea = dialogButton(10, 180, RoomType.DRAWING_AREA);
        JButton kitchen = dialogButton(150, 180, RoomType.KITCHEN);

        dialog.add(heading);
        dialog.add(closeButton(e -> dialog.setVisible(false)));
        dialog.add(bedroom);
        dialog.add(bathroom);
        dialog.add(kitchen);
        dialog.add(drawingArea);
        return dialog;
    }

    private JButton dialogButton(int x, int y, RoomType type) {
        JButton button = new JButton(type.label());
        button.setBounds(x, y, 130, 40);
        button.setBackground(type.color());
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addActionListener(e -> {
            pendingRoomType = type;
            roomTypeDialog.setVisible(false);
            dimensionDialog.setVisible(true);
        });
        return button;
    }

    private JPanel buildDimensionDialog() {
        JPanel dialog = popupPanel();

        JLabel heading = dialogHeading("Dimensions", 10, 40);

        JLabel widthLabel = fieldLabel("Width", 15, 65);
        JLabel heightLabel = fieldLabel("Height", 15, 135);

        JTextField widthInput = new JTextField();
        widthInput.setBounds(10, 88, 250, 40);
        widthInput.setBackground(new Color(0xdedede));

        JTextField heightInput = new JTextField();
        heightInput.setBounds(10, 153, 250, 40);
        heightInput.setBackground(new Color(0xdedede));

        JButton submit = new JButton("Submit");
        submit.setBounds(10, 630, 290, 40);
        submit.setBackground(new Color(0x71b340));
        submit.setFocusable(false);
        submit.setBorder(BorderFactory.createEmptyBorder());
        submit.addActionListener(e -> {
            if (!createRoomFromInputs(widthInput.getText(), heightInput.getText())) {
                return;
            }
            dimensionDialog.setVisible(false);
            widthInput.setText("");
            heightInput.setText("");
        });

        dialog.add(closeButton(e -> dialog.setVisible(false)));
        dialog.add(submit);
        dialog.add(heading);
        dialog.add(widthInput);
        dialog.add(heightInput);
        dialog.add(heightLabel);
        dialog.add(widthLabel);
        return dialog;
    }

    private boolean createRoomFromInputs(String widthText, String heightText) {
        int width;
        int height;
        try {
            width = Integer.parseInt(widthText.trim());
            height = Integer.parseInt(heightText.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Width and height must be whole numbers.",
                    "Invalid Dimensions", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (width <= 0 || height <= 0) {
            JOptionPane.showMessageDialog(this, "Width and height must be greater than zero.",
                    "Invalid Dimensions", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        RoomPanel room = new RoomPanel(pendingRoomType, width, height);
        room.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectRoom(room);
            }
        });
        layeredPane.add(room, JLayeredPane.DEFAULT_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
        return true;
    }

    private void selectRoom(RoomPanel room) {
        for (Component comp : layeredPane.getComponents()) {
            if (comp instanceof RoomPanel other) {
                other.setSelected(other == room);
            }
        }
        selectedRoom = room;
        layeredPane.moveToFront(room);
    }

    // ---------------------------------------------------------------
    // Furniture dialog
    // ---------------------------------------------------------------

    private JPanel buildFurnitureDialog() {
        JPanel dialog = popupPanel();

        JLabel heading = new JLabel("Furniture", javax.swing.SwingConstants.CENTER);
        heading.setBounds(0, 100, 320, 40);
        heading.setForeground(Color.WHITE);

        dialog.add(heading);
        dialog.add(closeButton(e -> dialog.setVisible(false)));
        dialog.add(furnitureButton(50, 155, FurnitureType.BED));
        dialog.add(furnitureButton(130, 155, FurnitureType.SOFA));
        dialog.add(furnitureButton(210, 155, FurnitureType.SINK));
        dialog.add(furnitureButton(85, 240, FurnitureType.TOILET));
        dialog.add(furnitureButton(165, 240, FurnitureType.TABLE));
        return dialog;
    }

    private JButton furnitureButton(int x, int y, FurnitureType type) {
        JButton button = new JButton(new javax.swing.ImageIcon(IconFactory.draw(type, 65, 65)));
        button.setBounds(x, y, 65, 65);
        button.setFocusable(false);
        button.setBackground(Color.WHITE);
        button.setToolTipText(type.label());
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addActionListener(e -> {
            FurniturePanel piece = new FurniturePanel(type);
            placeOnCanvas(piece, 860, 620);
            furnitureDialog.setVisible(false);
        });
        return button;
    }

    // ---------------------------------------------------------------
    // Shared dialog/UI helpers
    // ---------------------------------------------------------------

    private void placeOnCanvas(Component component, int x, int y) {
        component.setLocation(x, y);
        layeredPane.add(component, JLayeredPane.DEFAULT_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    private JPanel popupPanel() {
        JPanel panel = new JPanel();
        panel.setBounds(960, 0, 320, 720);
        panel.setVisible(false);
        panel.setBorder(BorderFactory.createEmptyBorder());
        panel.setLayout(null);
        panel.setBackground(new Color(0x5a5858));
        return panel;
    }

    private JLabel dialogHeading(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 280, 30);
        label.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JLabel fieldLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 300, 15);
        label.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JButton closeButton(java.awt.event.ActionListener onClose) {
        JButton close = new JButton("x");
        close.setBounds(282, 5, 15, 15);
        close.setBackground(Color.RED);
        close.setForeground(Color.WHITE);
        close.setFocusable(false);
        close.setBorder(BorderFactory.createEmptyBorder());
        close.addActionListener(onClose);
        return close;
    }
}
