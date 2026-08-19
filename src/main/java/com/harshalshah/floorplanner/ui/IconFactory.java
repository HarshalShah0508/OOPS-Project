package com.harshalshah.floorplanner.ui;

import com.harshalshah.floorplanner.model.FurnitureType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

/**
 * Draws each furniture glyph procedurally with Java2D rather than loading
 * an image file.
 *
 * <p>The original app loaded furniture icons via
 * {@code new ImageIcon("Bed.png")} — but those PNGs were never checked
 * into the repository. {@code ImageIcon} doesn't throw on a missing file;
 * it just silently produces a zero-size image, so every furniture button
 * and every placed piece rendered blank. Drawing the icons in code removes
 * the missing-asset dependency entirely and keeps the visuals in version
 * control as text, not binary blobs.
 */
public final class IconFactory {

    private static final Map<FurnitureType, Color> FILL_COLORS = new EnumMap<>(FurnitureType.class);

    static {
        FILL_COLORS.put(FurnitureType.BED, new Color(0xa7c7e7));
        FILL_COLORS.put(FurnitureType.SOFA, new Color(0xc9a7e7));
        FILL_COLORS.put(FurnitureType.SINK, new Color(0xa7e7d8));
        FILL_COLORS.put(FurnitureType.TOILET, new Color(0xe7e0a7));
        FILL_COLORS.put(FurnitureType.TABLE, new Color(0xd8b48c));
    }

    private IconFactory() {
    }

    public static BufferedImage draw(FurnitureType type, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(FILL_COLORS.get(type));
        g2d.setStroke(new BasicStroke(1.5f));

        switch (type) {
            case BED -> drawBed(g2d, width, height);
            case SOFA -> drawSofa(g2d, width, height);
            case SINK -> drawSink(g2d, width, height);
            case TOILET -> drawToilet(g2d, width, height);
            case TABLE -> drawTable(g2d, width, height);
        }

        g2d.dispose();
        return image;
    }

    private static void drawBed(Graphics2D g2d, int w, int h) {
        RoundRectangle2D frame = new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 8, 8);
        g2d.fill(frame);
        g2d.setColor(Color.DARK_GRAY);
        g2d.draw(frame);
        g2d.fillRoundRect(w / 6, 4, (int) (w * 0.66), h / 3, 6, 6);
    }

    private static void drawSofa(Graphics2D g2d, int w, int h) {
        g2d.fillRoundRect(2, h / 3, w - 4, (int) (h * 0.6), 10, 10);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRoundRect(2, h / 3, w - 4, (int) (h * 0.6), 10, 10);
        g2d.setColor(FILL_COLORS.get(FurnitureType.SOFA).darker());
        g2d.fillRoundRect(2, h / 3, w / 6, (int) (h * 0.6), 6, 6);
        g2d.fillRoundRect(w - w / 6 - 2, h / 3, w / 6, (int) (h * 0.6), 6, 6);
    }

    private static void drawSink(Graphics2D g2d, int w, int h) {
        g2d.fillOval(w / 6, h / 4, (int) (w * 0.66), (int) (h * 0.6));
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawOval(w / 6, h / 4, (int) (w * 0.66), (int) (h * 0.6));
        g2d.fillRect(w / 2 - 2, 2, 4, h / 4);
    }

    private static void drawToilet(Graphics2D g2d, int w, int h) {
        g2d.fillRoundRect(w / 4, 2, w / 2, h / 3, 4, 4);
        g2d.fillOval(w / 6, h / 3, (int) (w * 0.66), (int) (h * 0.6));
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawOval(w / 6, h / 3, (int) (w * 0.66), (int) (h * 0.6));
    }

    private static void drawTable(Graphics2D g2d, int w, int h) {
        g2d.fillRoundRect(4, 4, w - 8, h - 8, 4, 4);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRoundRect(4, 4, w - 8, h - 8, 4, 4);
        int legSize = Math.max(3, w / 10);
        g2d.fillRect(4, 4, legSize, legSize);
        g2d.fillRect(w - 4 - legSize, 4, legSize, legSize);
        g2d.fillRect(4, h - 4 - legSize, legSize, legSize);
        g2d.fillRect(w - 4 - legSize, h - 4 - legSize, legSize, legSize);
    }
}
