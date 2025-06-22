import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class Furniture extends JPanel {
    private ImageIcon image;
    private Point initialClick;
    private boolean isDragging = false;
    private Point originalLocation;
    private String furnitureType;
    private double rotation = 0; // in degrees
    private static final int CONTROL_PANEL_WIDTH = 320;
    private boolean isRotating = false;

    public Furniture(ImageIcon image, String type) {
        Image scaledImage = image.getImage().getScaledInstance(65,65 , Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        this.image = scaledIcon;
        this.furnitureType = type;
        
        // Set panel size to match image size
        setSize(65,65);
        setPreferredSize(new Dimension(65,65));
        
        // Make panel transparent
        setOpaque(false);
        setupMouseListeners();
        setupKeyListener();
    }

    private void setupKeyListener() {
        // Make the panel focusable to receive key events
        setFocusable(true);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'R' || e.getKeyChar() == 'r') {
                    // Rotate 90 degrees clockwise
                    rotation = (rotation + 90) % 360;
                    repaint();
                }
            }
        });

        // Add mouse listener to request focus when clicked
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow(); // Request focus when furniture is clicked
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
                // Bring furniture to front when dragging
                getParent().setComponentZOrder(Furniture.this, 0);
                
                // Check if right mouse button is clicked for rotation
                if (SwingUtilities.isRightMouseButton(e)) {
                    isRotating = true;
                    isDragging = false;
                }
                
                // Request focus when furniture is selected
                requestFocusInWindow();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
                isRotating = false;
                
                //Snap rotation to 90-degree increments
                rotation = Math.round(rotation / 90.0) * 90.0;
                repaint();
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
                    
                    // Calculate maximum allowed X position
                    int maxX = parent.getWidth() - CONTROL_PANEL_WIDTH - getWidth();
                    
                    // Ensure furniture stays within bounds
                    int newX = Math.max(0, Math.min(thisX + xOffset, maxX));
                    int newY = Math.max(0, Math.min(thisY + yOffset, 
                        parent.getHeight() - getHeight()));
                    
                    setLocation(newX, newY);
                    getParent().repaint();
                } else if (isRotating) {
                    // Calculate rotation based on mouse movement
                    Point center = new Point(getWidth() / 2, getHeight() / 2);
                    double angle = Math.toDegrees(Math.atan2(
                        e.getY() - center.y,
                        e.getX() - center.x
                    ));
                    rotation = angle;
                    repaint();
                }
            }
        };
        
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }
    
    private boolean isInControlPanel() {
        Container parent = getParent();
        int rightBoundary = parent.getWidth() - CONTROL_PANEL_WIDTH;
        return (getLocation().x + getWidth() > rightBoundary);
    }
    
    private boolean isOutsideRoom() {
        Container parent = getParent();
        Rectangle furnitureBounds = getBounds();
        boolean insideAnyRoom = false;
        
        // Check if furniture is completely inside any room
        for (Component comp : parent.getComponents()) {
            if (comp instanceof Room) {
                Rectangle roomBounds = comp.getBounds();
                if (roomBounds.contains(furnitureBounds)) {
                    insideAnyRoom = true;
                    break;
                }
            }
        }
        
        return !insideAnyRoom;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);
            
        // Create transform for rotation
        AffineTransform old = g2d.getTransform();
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(rotation), getWidth() / 2, getHeight() / 2);
        g2d.transform(at);
        
        // Draw the image
        g2d.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), this);
        
        // Restore original transform
        g2d.setTransform(old);
    }
    
    // Getters
    public String getFurnitureType() {
        return furnitureType;
    }
    
    public double getRotation() {
        return rotation;
    }
    public void setRotation(double degrees) {
        rotation = degrees;
    }
}
