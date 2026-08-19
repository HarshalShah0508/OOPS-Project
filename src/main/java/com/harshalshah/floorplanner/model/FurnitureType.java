package com.harshalshah.floorplanner.model;

/**
 * The furniture pieces the planner can place. The original version of this
 * app identified furniture by loading a PNG named after the piece (e.g.
 * {@code "Bed.png"}) — those image files were never actually part of the
 * repository, so every furniture button silently rendered a blank icon and
 * every placed piece was invisible. Replacing string-typed lookups with
 * this enum lets {@code IconFactory} draw each piece procedurally instead,
 * which removes that missing-asset failure mode entirely.
 */
public enum FurnitureType {
    BED("Bed"),
    SOFA("Sofa"),
    SINK("Sink"),
    TOILET("Toilet"),
    TABLE("Table");

    private final String label;

    FurnitureType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
