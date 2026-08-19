package com.harshalshah.floorplanner.geometry;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeometryTest {

    @Test
    void overlappingRectanglesAreDetected() {
        Rectangle a = new Rectangle(0, 0, 100, 100);
        Rectangle b = new Rectangle(50, 50, 100, 100);
        assertTrue(Geometry.overlaps(a, b));
    }

    @Test
    void adjacentRectanglesDoNotOverlap() {
        Rectangle a = new Rectangle(0, 0, 100, 100);
        Rectangle b = new Rectangle(100, 0, 100, 100);
        assertFalse(Geometry.overlaps(a, b));
    }

    @Test
    void itemOnTopWallIsOnWall() {
        Rectangle room = new Rectangle(100, 100, 200, 200);
        Rectangle door = new Rectangle(150, 98, 40, 10); // sitting on the top edge
        assertTrue(Geometry.isOnWall(door, room, false, Geometry.WALL_TOLERANCE_PX));
    }

    @Test
    void itemInsideRoomIsNotOnWall() {
        Rectangle room = new Rectangle(100, 100, 200, 200);
        Rectangle door = new Rectangle(150, 150, 40, 10); // dead center, nowhere near an edge
        assertFalse(Geometry.isOnWall(door, room, false, Geometry.WALL_TOLERANCE_PX));
    }

    @Test
    void itemOnLeftWallIsOnWallVertically() {
        Rectangle room = new Rectangle(100, 100, 200, 200);
        Rectangle window = new Rectangle(97, 150, 10, 40); // sitting on the left edge
        assertTrue(Geometry.isOnWall(window, room, true, Geometry.WALL_TOLERANCE_PX));
    }

    @Test
    void doorOnSharedWallConnectsBothRooms() {
        Rectangle roomA = new Rectangle(0, 0, 200, 200);
        Rectangle roomB = new Rectangle(200, 0, 200, 200); // shares roomA's right wall
        Rectangle door = new Rectangle(198, 80, 4, 40);    // straddling the shared wall

        assertTrue(Geometry.connectsRooms(door, roomA, roomB, true, Geometry.WALL_TOLERANCE_PX));
    }

    @Test
    void doorNotOnSharedWallDoesNotConnectRooms() {
        Rectangle roomA = new Rectangle(0, 0, 200, 200);
        Rectangle roomB = new Rectangle(500, 500, 200, 200); // far away, no shared wall
        Rectangle door = new Rectangle(198, 80, 4, 40);

        assertFalse(Geometry.connectsRooms(door, roomA, roomB, true, Geometry.WALL_TOLERANCE_PX));
    }

    @Test
    void snapTargetFindsNearbyRightEdge() {
        Rectangle moving = new Rectangle(195, 50, 100, 100);
        Rectangle neighbor = new Rectangle(300, 40, 100, 150);

        Point snapped = Geometry.snapTarget(moving, List.of(neighbor), Geometry.SNAP_DISTANCE_PX);

        assertEquals(new Point(200, 50), snapped);
    }

    @Test
    void snapTargetReturnsNullWhenNothingIsClose() {
        Rectangle moving = new Rectangle(0, 0, 100, 100);
        Rectangle neighbor = new Rectangle(1000, 1000, 100, 100);

        assertNull(Geometry.snapTarget(moving, List.of(neighbor), Geometry.SNAP_DISTANCE_PX));
    }
}
