import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Room extends JPanel {
    private Point initialClick;
    private boolean isDragging = false;
    private Point originalLocation;
    private static final int CONTROL_PANEL_WIDTH = 320; // Width of the control panel
    private static final int SNAP_DISTANCE = 10; // Distance for snapping rooms

    public Room() {
        setupMouseListeners();
    }
    public enum RoomType {
        BEDROOM,
        BATHROOM,
        KITCHEN,
        DRAWING_AREA
    }
    private RoomType roomType;

public RoomType getRoomType() {
    return roomType;
}

    private void setupMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                originalLocation = getLocation();
                isDragging = true;
                getParent().setComponentZOrder(Room.this, 0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
                if (checkOverlap() || isInControlPanel()) {
                    JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(Room.this),
                        "Invalid position! Rooms cannot overlap.",
                        "Position Error",
                        JOptionPane.ERROR_MESSAGE);
                    setLocation(originalLocation);
                } else {
                    snapToOtherRooms(); // Check for snapping after releasing
                }
                getParent().repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    // Calculate new position
                    int thisX = getLocation().x;
                    int thisY = getLocation().y;

                    // Move component
                    int xOffset = e.getX() - initialClick.x;
                    int yOffset = e.getY() - initialClick.y;

                    // Get parent container dimensions
                    Container parent = getParent();

                    // Calculate maximum allowed X position (control panel boundary)
                    int maxX = parent.getWidth() - CONTROL_PANEL_WIDTH - getWidth();

                    // Ensure room stays within bounds and away from control panel
                    int newX = Math.max(0, Math.min(thisX + xOffset, maxX));
                    int newY = Math.max(0, Math.min(thisY + yOffset,
                            parent.getHeight() - getHeight()));

                    setLocation(newX, newY);
                    getParent().repaint();
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private boolean isInControlPanel() {
        // Check if room extends into control panel area
        Container parent = getParent();
        int rightBoundary = parent.getWidth() - CONTROL_PANEL_WIDTH;
        return (getLocation().x + getWidth() > rightBoundary);
    }

    private boolean checkOverlap() {
        Container parent = getParent();
        Rectangle thisRect = getBounds();

        for (Component comp : parent.getComponents()) {
            if (comp instanceof Room && comp != this) {
                Rectangle otherRect = comp.getBounds();
                if (thisRect.intersects(otherRect)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void snapToOtherRooms() {
        Container parent = getParent();
        Rectangle thisRect = getBounds();

        for (Component comp : parent.getComponents()) {
            if (comp instanceof Room && comp != this) {
                Rectangle otherRect = comp.getBounds();

                // Snap to right side
                if (Math.abs(thisRect.x + thisRect.width - otherRect.x) < SNAP_DISTANCE &&
                    thisRect.y < otherRect.y + otherRect.height &&
                    thisRect.y + thisRect.height > otherRect.y) {
                    setLocation(otherRect.x - thisRect.width, thisRect.y);
                }
                // Snap to left side
                else if (Math.abs(otherRect.x + otherRect.width - thisRect.x) < SNAP_DISTANCE &&
                    thisRect.y < otherRect.y + otherRect.height &&
                    thisRect.y + thisRect.height > otherRect.y) {
                    setLocation(otherRect.x + otherRect.width, thisRect.y);
                }
                // Snap to bottom side
                else if (Math.abs(thisRect.y + thisRect.height - otherRect.y) < SNAP_DISTANCE &&
                    thisRect.x < otherRect.x + otherRect.width &&
                    thisRect.x + thisRect.width > otherRect.x) {
                    setLocation(thisRect.x, otherRect.y - thisRect.height);
                }
                // Snap to top side
                else if (Math.abs(otherRect.y + otherRect.height - thisRect.y) < SNAP_DISTANCE &&
                    thisRect.x < otherRect.x + otherRect.width &&
                    thisRect.x + thisRect.width > otherRect.x) {
                    setLocation(thisRect.x, otherRect.y + otherRect.height);
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Additional painting code can be added here if needed
    }
    
}
