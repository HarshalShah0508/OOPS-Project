package com.harshalshah.floorplanner.model;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;

/** Serializable snapshot of one door. {@code rotation} is 0 (horizontal) or 90 (vertical). */
public record DoorState(Point location, Dimension size, int rotation) implements Serializable {
}
