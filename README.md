# 2D Floor Planner

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org)
[![Swing](https://img.shields.io/badge/UI-Java%20Swing-blue?style=flat-square)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org)
[![JUnit 5](https://img.shields.io/badge/Tests-JUnit%205-25A162?style=flat-square&logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](LICENSE)

A desktop Java Swing app for sketching a house floor plan: drag out
color-coded rooms, place doors, windows, and furniture, and save the whole
layout to disk. Built as an OOP coursework project; rebuilt here into a
proper Maven project with a real package structure, unit tests, and CI —
and with several bugs from the original single-file version actually fixed,
not just reformatted.

## Features

- **Rooms** — four types (Bedroom, Bathroom, Kitchen, Drawing Area), each
  with its own color, dragged to any position, blocked from overlapping
  each other, and magnetized (snapped) flush against a neighbor when
  dropped nearby
- **Doors & windows** — draggable, rotatable (double-click or press `R`),
  and validated against the room they're dropped on: a door or window must
  actually sit on a wall, windows can't straddle two rooms or overlap each
  other, and bedrooms/bathrooms are required to have a door connecting
  them to the rest of the plan
- **Furniture** — five pieces (bed, sofa, sink, toilet, table), draggable
  and rotatable (right-click-drag to rotate freely, snaps to 90° on
  release)
- **Save / load** — the entire plan (rooms, doors, windows, furniture)
  serializes to a `.floorplan` file and reloads exactly as it was

## What changed from the original version

This started as seven flat `.java` files with no build tool, no tests, and
no package structure. Restructuring it surfaced several real bugs that
formatting alone wouldn't have caught:

| Issue | Fix |
|---|---|
| Furniture icons loaded via `new ImageIcon("Bed.png")`, but the PNGs were never in the repo — every furniture button and every placed piece rendered blank | Icons are now drawn procedurally in [`IconFactory`](src/main/java/com/harshalshah/floorplanner/ui/IconFactory.java) with Java2D — no external image files to go missing |
| `Room.roomType` was declared but never assigned by any caller, so `getRoomType()` always returned `null` — silently breaking the "bedrooms/bathrooms need a connecting door" rule | `RoomPanel` now takes its `RoomType` in the constructor; the rule actually fires |
| `SaveLoad` persisted rooms, furniture, and doors — but never windows. Any window on the canvas silently vanished on the next load | `FloorPlanDocument` includes windows; round-trip is covered by a unit test |
| Rooms were added to a `JLayeredPane` inside one method, then immediately re-added directly to the frame in the caller — since a Swing component can only have one parent, the second call silently undid the first, making the layered-pane structure a no-op | Every canvas item is added to `layeredPane` exactly once, in exactly one place |
| Wall-placement and room-adjacency checks were near-identical logic, copy-pasted between `Door` and `Window` (~20 lines each, kept in sync by hand) | Consolidated into [`Geometry`](src/main/java/com/harshalshah/floorplanner/geometry/Geometry.java), a Swing-free utility class — the version doors and windows both call is unit tested directly |
| `new SaveLoad(this)` was constructed on *every* button click, not just Save/Load | Built once, reused |
| A furniture class named `Window` shadowed `java.awt.Window` wherever both were in scope | Renamed to `WindowPanel` (and `Room`/`Door`/`Furniture` to `RoomPanel`/`DoorPanel`/`FurniturePanel`, for the same reason and for consistency) |

## Architecture

```
com.harshalshah.floorplanner
├── Main                    Entry point
├── model/                  Plain data — no Swing dependency
│   ├── RoomType, FurnitureType
│   └── RoomState, DoorState, WindowState, FurnitureState, FloorPlanDocument
├── geometry/
│   └── Geometry             Overlap / wall-placement / adjacency / snapping — pure functions, unit tested
├── ui/
│   ├── MainFrame             Window, control panel, dialogs
│   ├── RoomPanel, DoorPanel, WindowPanel, FurniturePanel
│   └── IconFactory           Procedural furniture icons (Java2D)
└── io/
    └── FloorPlanRepository   Save/load orchestration; read/write are unit tested independently of Swing
```

The `model` and `geometry` packages have no dependency on `javax.swing` —
that's what makes them unit-testable without a display. `ui` depends on
both; `io` depends on `ui` (it needs to construct `RoomPanel` etc. when
loading) and `model`.

## Quick Start

Requires JDK 17+ and Maven.

```bash
git clone https://github.com/HarshalShah0508/OOPS-Project.git
cd OOPS-Project
mvn package
java -jar target/floorplanner-1.0.0.jar
```

Or run directly without building a jar:

```bash
mvn exec:java
```

### Controls

- **Add Room** → pick a type → enter width/height → click and drag to
  reposition
- **Add Door** / **Add Window** → drag onto a room's wall; double-click or
  press `R` while focused to rotate
- **Add Furniture** → pick a piece → drag to place; right-click-drag to
  rotate freely, release to snap to 90°
- **Save Plan** / **Load Plan** → `.floorplan` files via a standard file
  chooser

## Testing

```bash
mvn test
```

- `GeometryTest` — overlap detection, wall-placement (horizontal and
  vertical), room-adjacency-through-a-door, and snap-target calculation,
  all against plain `Rectangle`s, no Swing involved
- `FloorPlanRepositoryTest` — a `FloorPlanDocument` (rooms, doors, windows,
  furniture) round-trips through actual disk serialization unchanged

CI (`.github/workflows/ci.yml`) runs `mvn verify` on every push and PR.

## Limitations

- No undo/redo
- Door/window wall-validation is tolerance-based (±5px) rather than
  constraint-solved, so very fast drags can occasionally misjudge a valid
  placement
- `.floorplan` files are plain Java serialization — portable across
  versions of this app, not a general interchange format

## License

MIT — see [LICENSE](LICENSE).
