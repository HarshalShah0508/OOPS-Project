package com.harshalshah.floorplanner.ui;

import com.harshalshah.floorplanner.model.FurnitureType;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/** A draggable, rotatable piece of furniture, rendered via {@link IconFactory}. */
public class FurniturePanel extends JPanel {

    private static final int SIZE = 65;

    private final FurnitureType furnitureType;
    private final BufferedImage icon;
    private Point initialClick;
    private boolean isDragging = false;
    private boolean isRotating = false;
    private double rotation = 0; // degrees

    public FurniturePanel(FurnitureType furnitureType) {
        this.furnitureType = furnitureType;
        this.icon = IconFactory.draw(furnitureType, SIZE, SIZE);
        setSize(SIZE, SIZE);
        setPreferredSize(new Dimension(SIZE, SIZE));
        setOpaque(false);
        setupMouseListeners();
        setupKeyListener();
    }

    public FurnitureType getFurnitureType() {
        return furnitureType;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double degrees) {
        rotation = degrees;
        repaint();
    }

    private void setupKeyListener() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'r' || e.getKeyChar() == 'R') {
                    rotation = (rotation + 90) % 360;
                    repaint();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }

    private void setupMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                isDragging = !SwingUtilities.isRightMouseButton(e);
                isRotating = SwingUtilities.isRightMouseButton(e);
                getParent().setComponentZOrder(FurniturePanel.this, 0);
                requestFocusInWindow();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
                isRotating = false;
                rotation = Math.round(rotation / 90.0) * 90.0;
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    dragTo(e);
                } else if (isRotating) {
                    rotateTo(e);
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void dragTo(MouseEvent e) {
        int xOffset = e.getX() - initialClick.x;
        int yOffset = e.getY() - initialClick.y;

        Container parent = getParent();
        int maxX = parent.getWidth() - UiConstants.CONTROL_PANEL_WIDTH - getWidth();
        int newX = Math.max(0, Math.min(getX() + xOffset, maxX));
        int newY = Math.max(0, Math.min(getY() + yOffset, parent.getHeight() - getHeight()));

        setLocation(newX, newY);
        getParent().repaint();
    }

    private void rotateTo(MouseEvent e) {
        Point center = new Point(getWidth() / 2, getHeight() / 2);
        rotation = Math.toDegrees(Math.atan2(e.getY() - center.y, e.getX() - center.x));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform original = g2d.getTransform();
        g2d.rotate(Math.toRadians(rotation), getWidth() / 2.0, getHeight() / 2.0);
        g2d.drawImage(icon, 0, 0, getWidth(), getHeight(), this);
        g2d.setTransform(original);
    }
}
