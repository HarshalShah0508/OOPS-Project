package com.harshalshah.floorplanner.io;

import com.harshalshah.floorplanner.model.DoorState;
import com.harshalshah.floorplanner.model.FloorPlanDocument;
import com.harshalshah.floorplanner.model.FurnitureState;
import com.harshalshah.floorplanner.model.RoomState;
import com.harshalshah.floorplanner.model.WindowState;
import com.harshalshah.floorplanner.ui.DoorPanel;
import com.harshalshah.floorplanner.ui.FurniturePanel;
import com.harshalshah.floorplanner.ui.RoomPanel;
import com.harshalshah.floorplanner.ui.WindowPanel;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads a floor plan to/from a {@code .floorplan} file (plain
 * Java serialization of a {@link FloorPlanDocument}).
 *
 * <p>The original {@code SaveLoad} class captured rooms, furniture, and
 * doors, but never windows — any window on the canvas silently vanished
 * on the next load. That's fixed here: windows round-trip like everything
 * else. The read/write logic is also split out as static methods that
 * operate purely on {@link FloorPlanDocument} and {@link File}, so it can
 * be unit tested without touching Swing.
 */
public class FloorPlanRepository {

    private static final String FILE_EXTENSION = ".floorplan";

    private final JFrame owner;
    private final Container canvas;

    public FloorPlanRepository(JFrame owner, Container canvas) {
        this.owner = owner;
        this.canvas = canvas;
    }

    public void save() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Floor Plan");
        if (chooser.showSaveDialog(owner) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(FILE_EXTENSION)) {
            file = new File(file.getAbsolutePath() + FILE_EXTENSION);
        }

        try {
            writeDocument(file, captureDocument());
            JOptionPane.showMessageDialog(owner, "Floor plan saved successfully!",
                    "Save Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(owner, "Error saving floor plan: " + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void load() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load Floor Plan");
        if (chooser.showOpenDialog(owner) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            applyDocument(readDocument(chooser.getSelectedFile()));
            JOptionPane.showMessageDialog(owner, "Floor plan loaded successfully!",
                    "Load Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(owner, "Error loading floor plan: " + ex.getMessage(),
                    "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void writeDocument(File file, FloorPlanDocument document) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(document);
        }
    }

    static FloorPlanDocument readDocument(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (FloorPlanDocument) in.readObject();
        }
    }

    private FloorPlanDocument captureDocument() {
        List<RoomState> rooms = new ArrayList<>();
        List<DoorState> doors = new ArrayList<>();
        List<WindowState> windows = new ArrayList<>();
        List<FurnitureState> furniture = new ArrayList<>();

        for (Component comp : canvas.getComponents()) {
            if (comp instanceof RoomPanel room) {
                rooms.add(new RoomState(room.getLocation(), room.getSize(), room.getRoomType()));
            } else if (comp instanceof DoorPanel door) {
                doors.add(new DoorState(door.getLocation(), door.getSize(), door.getRotation()));
            } else if (comp instanceof WindowPanel window) {
                windows.add(new WindowState(window.getLocation(), window.getSize(), window.getRotation()));
            } else if (comp instanceof FurniturePanel piece) {
                furniture.add(new FurnitureState(piece.getLocation(), piece.getSize(),
                        piece.getFurnitureType(), piece.getRotation()));
            }
        }

        return new FloorPlanDocument(rooms, doors, windows, furniture);
    }

    private void applyDocument(FloorPlanDocument document) {
        for (Component comp : canvas.getComponents().clone()) {
            if (comp instanceof RoomPanel || comp instanceof DoorPanel
                    || comp instanceof WindowPanel || comp instanceof FurniturePanel) {
                canvas.remove(comp);
            }
        }

        for (RoomState state : document.rooms()) {
            RoomPanel room = new RoomPanel(state.type(), state.size().width, state.size().height);
            room.setLocation(state.location());
            canvas.add(room);
        }

        for (DoorState state : document.doors()) {
            DoorPanel door = new DoorPanel();
            door.setBounds(state.location().x, state.location().y, state.size().width, state.size().height);
            if (state.rotation() == 90) {
                door.rotate();
            }
            canvas.add(door);
        }

        for (WindowState state : document.windows()) {
            WindowPanel window = new WindowPanel();
            window.setBounds(state.location().x, state.location().y, state.size().width, state.size().height);
            if (state.rotation() == 90) {
                window.rotate();
            }
            canvas.add(window);
        }

        for (FurnitureState state : document.furniture()) {
            FurniturePanel piece = new FurniturePanel(state.type());
            piece.setBounds(state.location().x, state.location().y, state.size().width, state.size().height);
            piece.setRotation(state.rotation());
            canvas.add(piece);
        }

        canvas.revalidate();
        canvas.repaint();
    }
}
