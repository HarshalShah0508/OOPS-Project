import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Window extends JPanel {
    private Point initialClick;
    private boolean isDragging = false;
    private Point originalLocation;
    private static final int WINDOW_WIDTH = 40;
    private static final int WINDOW_HEIGHT = 10;
    private int rotation = 0; // 0 for horizontal, 90 for vertical
    
    public Window() {
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
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
                getParent().setComponentZOrder(Window.this, 0);
                requestFocusInWindow();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
                if (!validateWindowPlacement()) {
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
    
    private void rotate() {
        rotation = (rotation + 90) % 180;
        int temp = getWidth();
        setSize(getHeight(), temp);
        repaint();
    }
    
    private boolean validateWindowPlacement() {
        Room currentRoom = findCurrentRoom();
        
        if (currentRoom == null) {
            showError("Window must be placed on a room's wall!");
            return false;
        }

        // Check if window is between two rooms
        if (isBetweenRooms()) {
            showError("Windows cannot be placed between rooms!");
            return false;
        }

        // Validate window position on wall
        if (!isOnWall(currentRoom)) {
            showError("Window must be placed on a wall!");
            return false;
        }

        // Check for overlap with other windows or doors
        if (overlapsWithComponents()) {
            showError("Window cannot overlap with other windows or doors!");
            return false;
        }

        return true;
    }

    private Room findCurrentRoom() {
        Container parent = getParent();
        Rectangle windowBounds = getBounds();
        
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
        Rectangle windowBounds = getBounds();
        Rectangle roomBounds = room.getBounds();
        int tolerance = 5; // pixels of tolerance for window placement

        if (rotation == 0) { // Horizontal window
            // Check if window is on top or bottom wall
            boolean onTopWall = Math.abs(windowBounds.y - roomBounds.y) <= tolerance;
            boolean onBottomWall = Math.abs(windowBounds.y - (roomBounds.y + roomBounds.height)) <= tolerance;
            boolean withinWidth = windowBounds.x >= roomBounds.x - tolerance && 
                                windowBounds.x + windowBounds.width <= roomBounds.x + roomBounds.width + tolerance;
            
            return (onTopWall || onBottomWall) && withinWidth;
        } else { // Vertical window
            // Check if window is on left or right wall
            boolean onLeftWall = Math.abs(windowBounds.x - roomBounds.x) <= tolerance;
            boolean onRightWall = Math.abs(windowBounds.x - (roomBounds.x + roomBounds.width)) <= tolerance;
            boolean withinHeight = windowBounds.y >= roomBounds.y - tolerance && 
                                 windowBounds.y + windowBounds.height <= roomBounds.y + roomBounds.height + tolerance;
            
            return (onLeftWall || onRightWall) && withinHeight;
        }
    }

    private boolean isBetweenRooms() {
        Container parent = getParent();
        Rectangle windowBounds = getBounds();
        Room firstRoom = null;
        
        for (Component comp : parent.getComponents()) {
            if (comp instanceof Room) {
                Room room = (Room) comp;
                if (isOnWall(room)) {
                    if (firstRoom == null) {
                        firstRoom = room;
                    } else {
                        // If we find a second room that the window is on, it's between rooms
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean overlapsWithComponents() {
        Container parent = getParent();
        Rectangle windowBounds = getBounds();
        int tolerance = 5;
        
        for (Component comp : parent.getComponents()) {
            if ((comp instanceof Window || comp instanceof Door) && comp != this) {
                Rectangle otherBounds = comp.getBounds();
                // Add tolerance to overlap check
                Rectangle expandedBounds = new Rectangle(
                    otherBounds.x - tolerance,
                    otherBounds.y - tolerance,
                    otherBounds.width + (2 * tolerance),
                    otherBounds.height + (2 * tolerance)
                );
                if (windowBounds.intersects(expandedBounds)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Window Position", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Create dashed stroke for window
        float[] dash = {5.0f};
        BasicStroke dashedStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, 
            BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        
        g2d.setStroke(dashedStroke);
        g2d.setColor(Color.BLACK);
        
        if (rotation == 0) {
            // Horizontal window
            g2d.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
        } else {
            // Vertical window
            g2d.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
        }
    }
}
