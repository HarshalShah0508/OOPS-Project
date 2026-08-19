package com.harshalshah.floorplanner.model;

import java.io.Serializable;
import java.util.List;

/**
 * The full contents of a saved {@code .floorplan} file: every room, door,
 * window, and piece of furniture on the canvas, independent of the Swing
 * components used to display them.
 */
public record FloorPlanDocument(
        List<RoomState> rooms,
        List<DoorState> doors,
        List<WindowState> windows,
        List<FurnitureState> furniture
) implements Serializable {

    private static final long serialVersionUID = 1L;
}
