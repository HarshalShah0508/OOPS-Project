package com.harshalshah.floorplanner.io;

import com.harshalshah.floorplanner.model.DoorState;
import com.harshalshah.floorplanner.model.FloorPlanDocument;
import com.harshalshah.floorplanner.model.FurnitureState;
import com.harshalshah.floorplanner.model.FurnitureType;
import com.harshalshah.floorplanner.model.RoomState;
import com.harshalshah.floorplanner.model.RoomType;
import com.harshalshah.floorplanner.model.WindowState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies a {@link FloorPlanDocument} survives a full write/read cycle
 * through {@code .floorplan} serialization — including windows, which the
 * original save/load implementation dropped silently (it never wrote them
 * out at all).
 */
class FloorPlanRepositoryTest {

    @Test
    void documentRoundTripsThroughDisk(@TempDir File tempDir) throws Exception {
        FloorPlanDocument original = new FloorPlanDocument(
                List.of(new RoomState(new Point(10, 20), new Dimension(300, 200), RoomType.BEDROOM)),
                List.of(new DoorState(new Point(10, 20), new Dimension(40, 10), 0)),
                List.of(new WindowState(new Point(50, 20), new Dimension(40, 10), 90)),
                List.of(new FurnitureState(new Point(30, 40), new Dimension(65, 65), FurnitureType.BED, 90.0))
        );

        File file = new File(tempDir, "test.floorplan");
        FloorPlanRepository.writeDocument(file, original);
        FloorPlanDocument loaded = FloorPlanRepository.readDocument(file);

        assertEquals(original, loaded);
        assertEquals(1, loaded.windows().size(), "window should have round-tripped, not been dropped");
    }

    @Test
    void emptyDocumentRoundTrips(@TempDir File tempDir) throws Exception {
        FloorPlanDocument empty = new FloorPlanDocument(List.of(), List.of(), List.of(), List.of());

        File file = new File(tempDir, "empty.floorplan");
        FloorPlanRepository.writeDocument(file, empty);
        FloorPlanDocument loaded = FloorPlanRepository.readDocument(file);

        assertEquals(empty, loaded);
    }
}
