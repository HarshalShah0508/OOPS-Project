package com.harshalshah.floorplanner.ui;

import com.harshalshah.floorplanner.geometry.Geometry;
import com.harshalshah.floorplanner.model.RoomType;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A draggable, color-coded room on the canvas.
 *
 * <p>In the original version, {@code roomType} was declared but never
 * assigned by any caller — {@link #getRoomType()} always returned
 * {@code null}, which silently broke {@code Door}'s "bedrooms and
 * bathrooms need a connecting door" rule (it never triggered). This
 * version requires the type at construction time instead.
 */
public class RoomPanel extends JPanel {

    private final RoomType roomType;
    private Point initialClick;
    private Point originalLocation;
    private boolean isDragging = false;

    public RoomPanel(RoomType roomType, int width, int height) {
        this.roomType = roomType;
        setBounds(0, 0, width, height);
        setBackground(roomType.color());
        setOpaque(true);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        setupMouseListeners();
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setSelected(boolean selected) {
        setBorder(BorderFactory.createLineBorder(selected ? Color.BLUE : Color.BLACK, 5));
    }

    private void setupMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                originalLocation = getLocation();
                isDragging = true;
                getParent().setComponentZOrder(RoomPanel.this, 0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
                if (overlapsAnotherRoom() || extendsIntoControlPanel()) {
                    JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(RoomPanel.this),
                            "Invalid position! Rooms cannot overlap.",
                            "Position Error",
                            JOptionPane.ERROR_MESSAGE);
                    setLocation(originalLocation);
                } else {
                    snapToNeighbors();
                }
                getParent().repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isDragging) {
                    return;
                }
                int xOffset = e.getX() - initialClick.x;
                int yOffset = e.getY() - initialClick.y;

                Container parent = getParent();
                int maxX = parent.getWidth() - UiConstants.CONTROL_PANEL_WIDTH - getWidth();
                int newX = Math.max(0, Math.min(getX() + xOffset, maxX));
                int newY = Math.max(0, Math.min(getY() + yOffset, parent.getHeight() - getHeight()));

                setLocation(newX, newY);
                getParent().repaint();
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private boolean extendsIntoControlPanel() {
        int rightBoundary = getParent().getWidth() - UiConstants.CONTROL_PANEL_WIDTH;
        return getX() + getWidth() > rightBoundary;
    }

    private boolean overlapsAnotherRoom() {
        for (Rectangle other : siblingRoomBounds()) {
            if (Geometry.overlaps(getBounds(), other)) {
                return true;
            }
        }
        return false;
    }

    private void snapToNeighbors() {
        Point target = Geometry.snapTarget(getBounds(), siblingRoomBounds(), Geometry.SNAP_DISTANCE_PX);
        if (target != null) {
            setLocation(target);
        }
    }

    private List<Rectangle> siblingRoomBounds() {
        List<Rectangle> bounds = new ArrayList<>();
        for (Component comp : getParent().getComponents()) {
            if (comp instanceof RoomPanel && comp != this) {
                bounds.add(comp.getBounds());
            }
        }
        return bounds;
    }
}
