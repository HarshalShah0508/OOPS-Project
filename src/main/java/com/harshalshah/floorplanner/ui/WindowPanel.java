package com.harshalshah.floorplanner.ui;

import com.harshalshah.floorplanner.geometry.Geometry;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A draggable, rotatable window that must be placed on a room's outer
 * wall (not shared with another room, and not overlapping a door or
 * another window).
 *
 * <p>Named {@code WindowPanel} rather than {@code Window} — the original
 * class was called {@code Window}, which shadows {@code java.awt.Window}
 * whenever that package is star-imported alongside it, a landmine for
 * anyone extending this code later.
 */
public class WindowPanel extends JPanel {

    private static final int WINDOW_WIDTH = 40;
    private static final int WINDOW_HEIGHT = 10;

    private Point initialClick;
    private Point originalLocation;
    private boolean isDragging = false;
    private int rotation = 0; // 0 = horizontal, 90 = vertical

    public WindowPanel() {
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
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
                getParent().setComponentZOrder(WindowPanel.this, 0);
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
        boolean vertical = rotation != 0;
        RoomPanel wallRoom = null;
        RoomPanel secondRoom = null;

        for (Component comp : getParent().getComponents()) {
            if (comp instanceof RoomPanel room
                    && Geometry.isOnWall(getBounds(), room.getBounds(), vertical, Geometry.WALL_TOLERANCE_PX)) {
                if (wallRoom == null) {
                    wallRoom = room;
                } else {
                    secondRoom = room;
                }
            }
        }

        if (wallRoom == null) {
            showError("Window must be placed on a wall!");
            return false;
        }
        if (secondRoom != null) {
            showError("Windows cannot be placed between rooms!");
            return false;
        }
        if (overlapsWindowOrDoor()) {
            showError("Window cannot overlap with another window or door!");
            return false;
        }
        return true;
    }

    private boolean overlapsWindowOrDoor() {
        Rectangle expanded = new Rectangle(
                getX() - Geometry.WALL_TOLERANCE_PX,
                getY() - Geometry.WALL_TOLERANCE_PX,
                getWidth() + 2 * Geometry.WALL_TOLERANCE_PX,
                getHeight() + 2 * Geometry.WALL_TOLERANCE_PX);

        for (Component comp : getParent().getComponents()) {
            if (comp == this) {
                continue;
            }
            if ((comp instanceof WindowPanel || comp instanceof DoorPanel)
                    && Geometry.overlaps(expanded, comp.getBounds())) {
                return true;
            }
        }
        return false;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), message,
                "Invalid Window Position", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        float[] dash = {5.0f};
        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        g2d.setColor(Color.BLACK);

        if (rotation == 0) {
            g2d.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
        } else {
            g2d.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
        }
    }
}
