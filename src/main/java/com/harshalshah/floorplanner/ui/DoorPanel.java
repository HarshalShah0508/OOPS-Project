package com.harshalshah.floorplanner.ui;

import com.harshalshah.floorplanner.geometry.Geometry;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** A draggable, rotatable door that must be placed on a room's wall. */
public class DoorPanel extends JPanel {

    private static final int DOOR_WIDTH = 40;
    private static final int DOOR_HEIGHT = 10;

    private Point initialClick;
    private Point originalLocation;
    private boolean isDragging = false;
    private int rotation = 0; // 0 = horizontal, 90 = vertical

    public DoorPanel() {
        setSize(DOOR_WIDTH, DOOR_HEIGHT);
        setBackground(new Color(0xfcfcfc));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setFocusable(true);
        setupMouseListeners();
        setupKeyboardListener();
    }

    public int getRotation() {
        return rotation;
    }

    public void rotate() {
        rotation = (rotation + 90) % 180;
        int width = getWidth();
        setSize(getHeight(), width);
        repaint();
    }

    private void setupKeyboardListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'r' || e.getKeyChar() == 'R') {
                    rotate();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }

    private void setupMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                originalLocation = getLocation();
                isDragging = true;
                getParent().setComponentZOrder(DoorPanel.this, 0);
                requestFocusInWindow();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
                if (!validatePlacement()) {
                    setLocation(originalLocation);
                }
                getParent().repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    int xOffset = e.getX() - initialClick.x;
                    int yOffset = e.getY() - initialClick.y;
                    setLocation(getX() + xOffset, getY() + yOffset);
                    getParent().repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    rotate();
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private boolean validatePlacement() {
        RoomPanel currentRoom = findWallRoom();

        if (currentRoom == null) {
            showError("Door must be placed on a wall!");
            return false;
        }

        if (currentRoom.getRoomType().requiresAdjacentRoom() && !connectsToAnotherRoom(currentRoom)) {
            showError(currentRoom.getRoomType().label()
                    + "s must have a door connecting to another room!");
            return false;
        }

        return true;
    }

    private RoomPanel findWallRoom() {
        boolean vertical = rotation != 0;
        for (Component comp : getParent().getComponents()) {
            if (comp instanceof RoomPanel room
                    && Geometry.isOnWall(getBounds(), room.getBounds(), vertical, Geometry.WALL_TOLERANCE_PX)) {
                return room;
            }
        }
        return null;
    }

    private boolean connectsToAnotherRoom(RoomPanel currentRoom) {
        boolean vertical = rotation != 0;
        Container parent = getParent();
        for (Component comp : parent.getComponents()) {
            if (comp instanceof RoomPanel other && other != currentRoom
                    && Geometry.connectsRooms(getBounds(), currentRoom.getBounds(), other.getBounds(),
                            vertical, Geometry.WALL_TOLERANCE_PX)) {
                return true;
            }
        }
        return false;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), message,
                "Invalid Door Position", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
}
