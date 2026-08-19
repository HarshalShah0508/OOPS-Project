package com.harshalshah.floorplanner.model;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;

/** Serializable snapshot of one placed piece of furniture. */
public record FurnitureState(Point location, Dimension size, FurnitureType type, double rotation)
        implements Serializable {
}
