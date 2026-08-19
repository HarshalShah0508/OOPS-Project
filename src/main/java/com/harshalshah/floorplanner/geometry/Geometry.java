package com.harshalshah.floorplanner.geometry;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

/**
 * Pure, Swing-free spatial logic for the floor planner.
 *
 * <p>In the original version of this app, wall-placement and
 * room-adjacency checks were copy-pasted almost verbatim between the
 * {@code Door} and {@code Window} components (each about 20 lines,
 * kept in sync by hand). Centralizing that logic here means it's
 * defined once, and — because it takes plain {@link Rectangle}s rather
 * than live Swing components — it can be unit tested without spinning
 * up a display, which the original per-component version could not be.
 */
public final class Geometry {

    /** Pixels of slack allowed when deciding whether a door/window sits on a wall. */
    public static final int WALL_TOLERANCE_PX = 5;

    /** Pixels within which a dragged room snaps flush against a neighbor. */
    public static final int SNAP_DISTANCE_PX = 10;

    private Geometry() {
    }

    public static boolean overlaps(Rectangle a, Rectangle b) {
        return a.intersects(b);
    }

    /**
     * True if {@code item} sits along one of {@code room}'s edges, within
     * {@code tolerance} pixels, and doesn't run past the room's corners.
     *
     * @param vertical true to test the left/right walls, false for top/bottom
     */
    public static boolean isOnWall(Rectangle item, Rectangle room, boolean vertical, int tolerance) {
        if (vertical) {
            boolean onLeftWall = Math.abs(item.x - room.x) <= tolerance;
            boolean onRightWall = Math.abs(item.x - (room.x + room.width)) <= tolerance;
            boolean withinHeight = item.y >= room.y - tolerance
                    && item.y + item.height <= room.y + room.height + tolerance;
            return (onLeftWall || onRightWall) && withinHeight;
        }
        boolean onTopWall = Math.abs(item.y - room.y) <= tolerance;
        boolean onBottomWall = Math.abs(item.y - (room.y + room.height)) <= tolerance;
        boolean withinWidth = item.x >= room.x - tolerance
                && item.x + item.width <= room.x + room.width + tolerance;
        return (onTopWall || onBottomWall) && withinWidth;
    }

    /**
     * True if {@code item} lies along a wall shared by both rooms — i.e. it
     * plausibly connects them. Used to require that bedrooms/bathrooms have
     * at least one door out.
     */
    public static boolean connectsRooms(Rectangle item, Rectangle roomA, Rectangle roomB,
                                         boolean vertical, int tolerance) {
        if (vertical) {
            boolean alignedWithA = Math.abs(item.x - roomA.x) <= tolerance
                    || Math.abs(item.x - (roomA.x + roomA.width)) <= tolerance;
            boolean alignedWithB = Math.abs(item.x - roomB.x) <= tolerance
                    || Math.abs(item.x - (roomB.x + roomB.width)) <= tolerance;
            boolean spansBoth = item.y + item.height >= Math.min(roomA.y, roomB.y)
                    && item.y <= Math.max(roomA.y + roomA.height, roomB.y + roomB.height);
            return alignedWithA && alignedWithB && spansBoth;
        }
        boolean alignedWithA = Math.abs(item.y - roomA.y) <= tolerance
                || Math.abs(item.y - (roomA.y + roomA.height)) <= tolerance;
        boolean alignedWithB = Math.abs(item.y - roomB.y) <= tolerance
                || Math.abs(item.y - (roomB.y + roomB.height)) <= tolerance;
        boolean spansBoth = item.x + item.width >= Math.min(roomA.x, roomB.x)
                && item.x <= Math.max(roomA.x + roomA.width, roomB.x + roomB.width);
        return alignedWithA && alignedWithB && spansBoth;
    }

    /**
     * If {@code moving}'s edge is within {@code snapDistance} of a
     * neighboring rectangle's opposing edge (and they overlap on the
     * perpendicular axis), returns the top-left point that would snap it
     * flush. Returns {@code null} if nothing is close enough to snap to.
     */
    public static Point snapTarget(Rectangle moving, List<Rectangle> others, int snapDistance) {
        for (Rectangle other : others) {
            boolean verticalOverlap = moving.y < other.y + other.height
                    && moving.y + moving.height > other.y;
            boolean horizontalOverlap = moving.x < other.x + other.width
                    && moving.x + moving.width > other.x;

            if (verticalOverlap) {
                if (Math.abs(moving.x + moving.width - other.x) < snapDistance) {
                    return new Point(other.x - moving.width, moving.y);
                }
                if (Math.abs(other.x + other.width - moving.x) < snapDistance) {
                    return new Point(other.x + other.width, moving.y);
                }
            }
            if (horizontalOverlap) {
                if (Math.abs(moving.y + moving.height - other.y) < snapDistance) {
                    return new Point(moving.x, other.y - moving.height);
                }
                if (Math.abs(other.y + other.height - moving.y) < snapDistance) {
                    return new Point(moving.x, other.y + other.height);
                }
            }
        }
        return null;
    }
}
