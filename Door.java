// Door.java
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Door extends JPanel {
    private Point initialClick;
    private boolean isDragging = false;
    private Point originalLocation;
    private static final int DOOR_WIDTH = 40;
    private static final int DOOR_HEIGHT = 10;
    private int rotation = 0; // 0 for horizontal, 90 for vertical
    
    public Door() {
        setSize(DOOR_WIDTH, DOOR_HEIGHT);
        setBackground(new Color(0xfcfcfc));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setupMouseListeners();
        setupKeyboardListener();
        setFocusable(true);
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
                getParent().setComponentZOrder(Door.this, 0);
                requestFocusInWindow();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
                if (!validateDoorPlacement()) {
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
    
    public void rotate() {
        rotation = (rotation + 90) % 180;
        int temp = getWidth();
        setSize(getHeight(), temp);
        repaint();
    }
    
    private boolean validateDoorPlacement() {
        Room currentRoom = findCurrentRoom();
        
        if (currentRoom == null) {
            showError("Door must be placed on a room's wall!");
            return false;
        }

        // Check if it's a bedroom or bathroom
        if (currentRoom.getRoomType() == Room.RoomType.BEDROOM || 
            currentRoom.getRoomType() == Room.RoomType.BATHROOM) {
            
            // For bedrooms and bathrooms, ensure there's an adjacent room
            if (!hasAdjacentRoom(currentRoom)) {
                showError("Bedrooms and bathrooms must have doors connecting to other rooms!");
                return false;
            }
        }

        // Validate door position on wall
        if (!isOnWall(currentRoom)) {
            showError("Door must be placed on a wall!");
            return false;
        }

        return true;
    }

    private Room findCurrentRoom() {
        Container parent = getParent();
        Rectangle doorBounds = getBounds();
        
        for (Component comp : parent.getComponents()) {
            if (comp instanceof Room) {
                Room room = (Room) comp;
                if (isOnWall(room)) {
                    return room;
                }
            }
        }
        return null;
    }

    private boolean isOnWall(Room room) {
        Rectangle doorBounds = getBounds();
        Rectangle roomBounds = room.getBounds();
        int tolerance = 5; // pixels of tolerance for door placement

        if (rotation == 0) { // Horizontal door
            // Check if door is on top or bottom wall
            boolean onTopWall = Math.abs(doorBounds.y - roomBounds.y) <= tolerance;
            boolean onBottomWall = Math.abs(doorBounds.y - (roomBounds.y + roomBounds.height)) <= tolerance;
            boolean withinWidth = doorBounds.x >= roomBounds.x - tolerance && 
                                doorBounds.x + doorBounds.width <= roomBounds.x + roomBounds.width + tolerance;
            
            return (onTopWall || onBottomWall) && withinWidth;
        } else { // Vertical door
            // Check if door is on left or right wall
            boolean onLeftWall = Math.abs(doorBounds.x - roomBounds.x) <= tolerance;
            boolean onRightWall = Math.abs(doorBounds.x - (roomBounds.x + roomBounds.width)) <= tolerance;
            boolean withinHeight = doorBounds.y >= roomBounds.y - tolerance && 
                                 doorBounds.y + doorBounds.height <= roomBounds.y + roomBounds.height + tolerance;
            
            return (onLeftWall || onRightWall) && withinHeight;
        }
    }

    private boolean hasAdjacentRoom(Room currentRoom) {
        Container parent = getParent();
        Rectangle doorBounds = getBounds();
        
        for (Component comp : parent.getComponents()) {
            if (comp instanceof Room && comp != currentRoom) {
                Room otherRoom = (Room) comp;
                if (areRoomsConnectedByDoor(currentRoom, otherRoom)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean areRoomsConnectedByDoor(Room room1, Room room2) {
        Rectangle doorBounds = getBounds();
        Rectangle bounds1 = room1.getBounds();
        Rectangle bounds2 = room2.getBounds();
        int tolerance = 5;

        if (rotation == 0) { // Horizontal door
            return (Math.abs(doorBounds.y - bounds1.y) <= tolerance || 
                   Math.abs(doorBounds.y - (bounds1.y + bounds1.height)) <= tolerance) &&
                   (Math.abs(doorBounds.y - bounds2.y) <= tolerance || 
                   Math.abs(doorBounds.y - (bounds2.y + bounds2.height)) <= tolerance) &&
                   doorBounds.x + doorBounds.width >= Math.min(bounds1.x, bounds2.x) &&
                   doorBounds.x <= Math.max(bounds1.x + bounds1.width, bounds2.x + bounds2.width);
        } else { // Vertical door
            return (Math.abs(doorBounds.x - bounds1.x) <= tolerance || 
                   Math.abs(doorBounds.x - (bounds1.x + bounds1.width)) <= tolerance) &&
                   (Math.abs(doorBounds.x - bounds2.x) <= tolerance || 
                   Math.abs(doorBounds.x - (bounds2.x + bounds2.width)) <= tolerance) &&
                   doorBounds.y + doorBounds.height >= Math.min(bounds1.y, bounds2.y) &&
                   doorBounds.y <= Math.max(bounds1.y + bounds1.height, bounds2.y + bounds2.height);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Door Position", JOptionPane.ERROR_MESSAGE);
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
    public int getRotation() {
        return rotation;  // Using the existing rotation field
    }
}
