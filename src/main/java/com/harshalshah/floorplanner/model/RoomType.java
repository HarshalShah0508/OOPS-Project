package com.harshalshah.floorplanner.model;

import java.awt.Color;

/**
 * The four room categories the planner supports. Each carries its own
 * display label and canonical fill color, so the "what color is a
 * bedroom" decision lives in exactly one place instead of being
 * duplicated between the room-type picker and whatever renders the room.
 */
public enum RoomType {
    BEDROOM("Bedroom", new Color(0x4be24b)),
    BATHROOM("Bathroom", new Color(0x1f93ff)),
    DRAWING_AREA("Drawing Area", new Color(0xffff65)),
    KITCHEN("Kitchen", new Color(0x4ff6961)),
    /**
     * Only ever assigned to plans saved by an older version of this app,
     * before room type was persisted — see {@code FloorPlanRepository}.
     */
    UNKNOWN("Room", Color.LIGHT_GRAY);

    private final String label;
    private final Color color;

    RoomType(String label, Color color) {
        this.label = label;
        this.color = color;
    }

    public String label() {
        return label;
    }

    public Color color() {
        return color;
    }

    /** Doors are required to connect these room types to the rest of the plan. */
    public boolean requiresAdjacentRoom() {
        return this == BEDROOM || this == BATHROOM;
    }
}
