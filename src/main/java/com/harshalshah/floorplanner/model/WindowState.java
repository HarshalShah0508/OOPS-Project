package com.harshalshah.floorplanner.model;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;

/**
 * Serializable snapshot of one window. {@code rotation} is 0 (horizontal)
 * or 90 (vertical).
 *
 * <p>The original save/load code captured rooms, furniture, and doors but
 * never windows — anything placed as a window silently vanished on the
 * next load. This type exists so windows round-trip too.
 */
public record WindowState(Point location, Dimension size, int rotation) implements Serializable {
}
