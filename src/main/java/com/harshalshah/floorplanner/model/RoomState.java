package com.harshalshah.floorplanner.model;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;

/** Serializable snapshot of one room, independent of any Swing component. */
public record RoomState(Point location, Dimension size, RoomType type) implements Serializable {
}
