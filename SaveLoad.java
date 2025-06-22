import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaveLoad {
    private TFrame tframe;

    public SaveLoad(TFrame tframe) {
        this.tframe = tframe;
    }

    public void savePlan() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Floor Plan");
        int userSelection = fileChooser.showSaveDialog(tframe);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().endsWith(".floorplan")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".floorplan");
            }

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileToSave))) {
                // Create a FloorPlanData object containing rooms, furniture, and doors
                FloorPlanData floorPlanData = new FloorPlanData(
                    extractRoomsData(),
                    extractFurnitureData(),
                    extractDoorsData()  // Added doors data extraction
                );
                out.writeObject(floorPlanData);
                JOptionPane.showMessageDialog(tframe, "Floor plan saved successfully!", 
                    "Save Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(tframe, "Error saving floor plan: " + ex.getMessage(), 
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadPlan() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Floor Plan");
        int userSelection = fileChooser.showOpenDialog(tframe);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();

            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileToLoad))) {
                FloorPlanData floorPlanData = (FloorPlanData) in.readObject();
                recreatePlan(floorPlanData);
                JOptionPane.showMessageDialog(tframe, "Floor plan loaded successfully!", 
                    "Load Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(tframe, "Error loading floor plan: " + ex.getMessage(), 
                    "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private List<RoomData> extractRoomsData() {
        List<RoomData> roomsData = new ArrayList<>();
        for (Component comp : tframe.getContentPane().getComponents()) {
            if (comp instanceof Room) {
                Room room = (Room) comp;
                roomsData.add(new RoomData(
                    room.getLocation(), 
                    room.getSize(), 
                    room.getBackground()
                ));
            }
        }
        return roomsData;
    }

    private List<FurnitureData> extractFurnitureData() {
        List<FurnitureData> furnitureData = new ArrayList<>();
        for (Component comp : tframe.getContentPane().getComponents()) {
            if (comp instanceof Furniture) {
                Furniture furniture = (Furniture) comp;
                furnitureData.add(new FurnitureData(
                    furniture.getLocation(),
                    furniture.getSize(),
                    furniture.getFurnitureType(),
                    furniture.getRotation()
                ));
            }
        }
        return furnitureData;
    }

    // New method to extract door data
    private List<DoorData> extractDoorsData() {
        List<DoorData> doorsData = new ArrayList<>();
        for (Component comp : tframe.getContentPane().getComponents()) {
            if (comp instanceof Door) {
                Door door = (Door) comp;
                doorsData.add(new DoorData(
                    door.getLocation(),
                    door.getSize(),
                    door.getRotation()  // Assuming you've added a getRotation() method to Door class
                ));
            }
        }
        return doorsData;
    }

    private void recreatePlan(FloorPlanData floorPlanData) {
        // Clear existing components
        Component[] components = tframe.getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof Room || comp instanceof Furniture || comp instanceof Door) {
                tframe.remove(comp);
            }
        }

        // Recreate rooms
        for (RoomData roomData : floorPlanData.roomsData) {
            Room room = new Room();
            room.setBounds(roomData.location.x, roomData.location.y, 
                         roomData.size.width, roomData.size.height);
            room.setBackground(roomData.color);
            room.setVisible(true);
            room.setOpaque(true);
            room.setBorder(BorderFactory.createLineBorder(Color.black, 5));
            tframe.add(room);
        }

        // Recreate furniture
        for (FurnitureData furnitureData : floorPlanData.furnitureData) {
            ImageIcon image = new ImageIcon(furnitureData.type + ".png");
            Furniture furniture = new Furniture(image, furnitureData.type);
            furniture.setBounds(furnitureData.location.x, furnitureData.location.y,
                              furnitureData.size.width, furnitureData.size.height);
            furniture.setRotation(furnitureData.rotation);
            tframe.add(furniture);
        }

        // Recreate doors
        for (DoorData doorData : floorPlanData.doorsData) {
            Door door = new Door();
            door.setBounds(doorData.location.x, doorData.location.y,
                         doorData.size.width, doorData.size.height);
            // If door was rotated, rotate it
            if (doorData.rotation == 90) {
                door.rotate();
            }
            tframe.add(door);
        }

        tframe.revalidate();
        tframe.repaint();
    }

    // Existing serializable classes
    private static class RoomData implements Serializable {
        Point location;
        Dimension size;
        Color color;

        RoomData(Point location, Dimension size, Color color) {
            this.location = location;
            this.size = size;
            this.color = color;
        }
    }

    private static class FurnitureData implements Serializable {
        Point location;
        Dimension size;
        String type;
        double rotation;

        FurnitureData(Point location, Dimension size, String type, double rotation) {
            this.location = location;
            this.size = size;
            this.type = type;
            this.rotation = rotation;
        }
    }

    // New serializable class for door data
    private static class DoorData implements Serializable {
        Point location;
        Dimension size;
        int rotation;  // 0 for horizontal, 90 for vertical

        DoorData(Point location, Dimension size, int rotation) {
            this.location = location;
            this.size = size;
            this.rotation = rotation;
        }
    }

    private static class FloorPlanData implements Serializable {
        List<RoomData> roomsData;
        List<FurnitureData> furnitureData;
        List<DoorData> doorsData;  // Added door data list

        FloorPlanData(List<RoomData> roomsData, List<FurnitureData> furnitureData, List<DoorData> doorsData) {
            this.roomsData = roomsData;
            this.furnitureData = furnitureData;
            this.doorsData = doorsData;
        }
    }
}
 
