import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;



public class TFrame extends JFrame implements ActionListener{

    JButton room_btn;
    JButton door_btn;
    JButton window_btn;
    JButton furniture_btn;
    JButton save_btn;
    JButton load_btn;
    JPanel roomDialogBox, roomDialogBox2;
    JPanel furnitureDialogBox;
    Color roomColor;
    JButton bedroom_btn, kitchen_btn, bathroom_btn, drawingArea_btn, submit_btn;
    JTextField width_input, height_input;
    int width,height;
    JButton bed_btn, toilet_btn, table_btn, sofa_btn, sink_btn;
    private Room selectedRoom;
    ArrayList<JPanel> rooms = new ArrayList<>();
    ArrayList<JPanel> furnitures = new ArrayList<>();
    JLayeredPane layeredPane;
    

    TFrame(){

        //Buttons
        room_btn = new JButton("+ Add Room");
        room_btn.setBounds(55,70,215,55);
        room_btn.setFocusable(false);
        room_btn.setBackground(new Color(0xefefef));
        room_btn.setFont(new java.awt.Font("Helvetica Neue", 0, 22));
        room_btn.setOpaque(true);
        room_btn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0x434343), 5));
        room_btn.addActionListener(e -> roomDialogBox.setVisible(true));

        door_btn = new JButton("+ Add Door");
        door_btn.setBounds(55,136,215,55);
        door_btn.setFocusable(false);
        door_btn.setBackground(new Color(0xefefef));
        door_btn.setFont(new java.awt.Font("Helvetica Neue", 0, 22));
        door_btn.setOpaque(true);
        door_btn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0x434343), 5));
        door_btn.addActionListener(this);

        window_btn = new JButton("+ Add Window");
        window_btn.setBounds(55,204,215,55);
        window_btn.setFocusable(false);
        window_btn.setBackground(new Color(0xefefef));
        window_btn.setFont(new java.awt.Font("Helvetica Neue", 0, 22));
        window_btn.setOpaque(true);
        window_btn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0x434343), 5));
        window_btn.addActionListener(e -> {
            Window window = new Window();
            window.setLocation(600, 600); // Initial position
            this.add(window);
            this.revalidate();
            this.repaint();
        });

        furniture_btn = new JButton("+ Add Furniture");
        furniture_btn.setBounds(55,272,215,55);
        furniture_btn.setFocusable(false);
        furniture_btn.setBackground(new Color(0xefefef));
        furniture_btn.setFont(new java.awt.Font("Helvetica Neue", 0, 22)); 
        furniture_btn.setOpaque(true);
        furniture_btn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0x434343), 5));
        furniture_btn.addActionListener(e -> {
            furnitureDialogBox.setVisible(true);
        });

        save_btn = new JButton("Save Plan");
        save_btn.setBounds(55,540,215,55);
        save_btn.setFocusable(false);
        save_btn.setBackground(new Color(0x71b340));
        save_btn.setForeground(new Color(0x003e20));
        save_btn.setFont(new java.awt.Font("Helvetica Neue", 1, 22));
        save_btn.setOpaque(true);
        save_btn.setContentAreaFilled(true);
        save_btn.addActionListener(this);

        load_btn = new JButton("Load Plan");
        load_btn.setBounds(55,607,215,55);
        load_btn.setFocusable(false);
        load_btn.setBackground(new Color(0x40baff));
        load_btn.setForeground(new Color(0x00285b));
        load_btn.setFont(new java.awt.Font("Helvetica Neue", 1, 22));
        load_btn.setOpaque(true);
        load_btn.setContentAreaFilled(true);
        load_btn.addActionListener(this);

        //Room Dialog Box #1
        JLabel rHeading = new JLabel("Room Type");
        rHeading.setBounds(10,100,500,260);
        rHeading.setHorizontalAlignment(JLabel.LEFT);
        rHeading.setVerticalAlignment(JLabel.TOP);
        rHeading.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        rHeading.setForeground(Color.WHITE);

        bedroom_btn = createTextButton(10,130,130,40,"Bedroom", new Color(0x4be24b));
        bathroom_btn = createTextButton(150,130,130,40,"Bathroom", new Color(0x1f93ff));
        drawingArea_btn = createTextButton(10,180,130,40,"Drawing Area", new Color(0xffff65));
        kitchen_btn = createTextButton(150,180,130,40,"Kitchen", new Color(0x4ff6961));
        bedroom_btn.addActionListener(this);
        bathroom_btn.addActionListener(this);
        kitchen_btn.addActionListener(this);
        drawingArea_btn.addActionListener(this);

        JButton rClose = new JButton("x");
        rClose.setBounds(282,5,15,15);
        rClose.setBackground(Color.RED);
        rClose.setForeground(Color.WHITE);
        rClose.addActionListener(e -> roomDialogBox.setVisible(false));
        rClose.setFocusable(false);
        rClose.setBorder(BorderFactory.createEmptyBorder());
        rClose.setOpaque(true);

        roomDialogBox = new JPanel();
        roomDialogBox.setBounds(960,0,320,720);
        roomDialogBox.setVisible(false);
        roomDialogBox.setBorder(BorderFactory.createEmptyBorder());
        roomDialogBox.setLayout(null);
        roomDialogBox.setBackground(new Color(0x5a5858));
        //roomDialogBox.setOpaque(true);
        roomDialogBox.add(rHeading);
        roomDialogBox.add(rClose);
        roomDialogBox.add(bedroom_btn);
        roomDialogBox.add(bathroom_btn);
        roomDialogBox.add(kitchen_btn);
        roomDialogBox.add(drawingArea_btn);

        //Room Dialog Box #2
        JLabel rHeading2 = new JLabel("Dimensions");
        rHeading2.setBounds(10,40,500,260);
        rHeading2.setHorizontalAlignment(JLabel.LEFT);
        rHeading2.setVerticalAlignment(JLabel.TOP);
        rHeading2.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        rHeading2.setForeground(Color.WHITE);

        JLabel width_text = new JLabel("Width");
        width_text.setBounds(15, 65, 300, 15);
        width_text.setHorizontalAlignment(JLabel.LEFT);
        width_text.setVerticalAlignment(JLabel.TOP);
        width_text.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        width_text.setForeground(Color.WHITE);

        JLabel height_text = new JLabel("Height");
        height_text.setBounds(15, 135, 300, 15);
        height_text.setHorizontalAlignment(JLabel.LEFT);
        height_text.setVerticalAlignment(JLabel.TOP);
        height_text.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        height_text.setForeground(Color.WHITE);

        width_input = createTextbox(10, 88, 250,40,new Color(0xdedede));
        height_input = createTextbox(10, 153, 250, 40, new Color(0xdedede));

        submit_btn = createTextButton(10, 630, 290, 40, "Submit", new Color(0x71b340));
        submit_btn.addActionListener(this);

        JButton rClose2 = new JButton("x");
        rClose2.setBounds(282,5,15,15);
        rClose2.setBackground(Color.RED);
        rClose2.setForeground(Color.WHITE);
        rClose2.addActionListener(e -> roomDialogBox2.setVisible(false));
        rClose2.setFocusable(false);
        rClose2.setBorder(BorderFactory.createEmptyBorder());
        rClose2.setOpaque(true);

        roomDialogBox2 = new JPanel();
        roomDialogBox2.setBounds(960,0,320,720);
        roomDialogBox2.setVisible(false);
        roomDialogBox2.setBorder(BorderFactory.createEmptyBorder());
        roomDialogBox2.setLayout(null);
        roomDialogBox2.add(rClose2);
        roomDialogBox2.setBackground(new Color(0x5a5858));
        roomDialogBox2.add(submit_btn);
        roomDialogBox2.add(rHeading2);
        roomDialogBox2.add(width_input);
        roomDialogBox2.add(height_input);
        roomDialogBox2.add(height_text);
        roomDialogBox2.add(width_text);

        //Furniture Dialog Box
        JLabel fHeading = new JLabel("Furniture Dialog Box");
        fHeading.setBounds(0,100,320,240); 
        fHeading.setHorizontalAlignment(JLabel.CENTER);
        fHeading.setVerticalAlignment(JLabel.TOP);
        fHeading.setForeground(Color.WHITE);
        
        bed_btn = createImageButton(50, 155, 65, 65, new ImageIcon("Bed.png"));
        bed_btn.addActionListener(e -> {
            createFurniture(new ImageIcon("Bed.png"), "Bed");
            furnitureDialogBox.setVisible(false);
        });
        sofa_btn = createImageButton(130, 155, 65, 65, new ImageIcon("Sofa.png"));
        sofa_btn.addActionListener(e -> {
            createFurniture(new ImageIcon("Sofa.png"), "Sofa");
            furnitureDialogBox.setVisible(false);
        });
        sink_btn = createImageButton(210, 155, 65, 65, new ImageIcon("Sink.png"));
        sink_btn.addActionListener(e -> {
            createFurniture(new ImageIcon("Sink.png"), "Sink");
            furnitureDialogBox.setVisible(false);
        });
        toilet_btn = createImageButton(85, 240, 65, 65, new ImageIcon("Toilet.png"));
        toilet_btn.addActionListener(e -> {
            createFurniture(new ImageIcon("Toilet.png"), "Toilet");
            furnitureDialogBox.setVisible(false);
        });
        table_btn = createImageButton(165, 240, 65, 65, new ImageIcon("Table.png"));
        table_btn.addActionListener(e -> {
            createFurniture(new ImageIcon("Table.png"), "Table");
            furnitureDialogBox.setVisible(false);
        });

        JButton fClose = new JButton("x");
        fClose.setBounds(288,5,15,15);
        fClose.setBackground(Color.RED);
        fClose.setForeground(Color.white);
        fClose.addActionListener(e -> furnitureDialogBox.setVisible(false));
        fClose.setBorder(BorderFactory.createEmptyBorder());
        fClose.setFocusable(false);
        fClose.setOpaque(true);

        furnitureDialogBox = new JPanel();
        furnitureDialogBox.setVisible(false);
        furnitureDialogBox.setBounds(960,0,320,720);
        furnitureDialogBox.setBorder(BorderFactory.createEtchedBorder());
        furnitureDialogBox.setLayout(null);
        furnitureDialogBox.setBackground(new Color(0x5a5858));
        furnitureDialogBox.add(fHeading);
        furnitureDialogBox.add(fClose);
        furnitureDialogBox.add(bed_btn);
        furnitureDialogBox.add(sink_btn);
        furnitureDialogBox.add(table_btn);
        furnitureDialogBox.add(toilet_btn);
        furnitureDialogBox.add(sofa_btn);
        

        //Control Panel
        JPanel panel = new JPanel();
        panel.setBounds(960,0,320,720);
        panel.setBackground(new Color(0x5a5858));
        panel.setLayout(null);
        panel.add(room_btn);
        panel.add(door_btn);
        panel.add(window_btn);
        panel.add(furniture_btn);
        panel.add(save_btn);
        panel.add(load_btn);

        //Layered Pane
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1600, 900);
        layeredPane.setOpaque(false);
        
        layeredPane.add(panel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(roomDialogBox, JLayeredPane.POPUP_LAYER);
        layeredPane.add(roomDialogBox2, JLayeredPane.POPUP_LAYER);
        layeredPane.add(furnitureDialogBox, JLayeredPane.POPUP_LAYER);
        
        //Frame
        this.setTitle("2D Floor Planner");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setSize(1280,720);
        this.setLocationRelativeTo(null);
        this.getContentPane().setBackground(new Color(0xfcfcfc));
        this.setLayout(null);
        this.add(layeredPane);
        this.setVisible(true);
    }

    private JButton createTextButton(int x, int y, int width, int height, String text, Color bgcolor){
        JButton button = new JButton(text);
        button.setBounds(x,y,width,height);
        button.setBackground(bgcolor);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        return button;
    }
    private JTextField createTextbox(int x,int y, int width, int height, Color color){
        JTextField textBox = new JTextField();
        textBox.setBounds(x,y,width,height);
        textBox.setBackground(color);
        return textBox;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==bedroom_btn || e.getSource() == bathroom_btn || e.getSource() == kitchen_btn || e.getSource() == drawingArea_btn){
            JButton button = (JButton) e.getSource();
            roomDialogBox.setVisible(false);
            roomColor = button.getBackground();
            roomDialogBox2.setVisible(true);
        }
        if(e.getSource()==submit_btn){
            width = Integer.parseInt(width_input.getText());
            height = Integer.parseInt(height_input.getText());
            roomDialogBox2.setVisible(false);
            JPanel room = createRoom(width, height, roomColor);
            room.setVisible(true);
            this.add(room);
            this.revalidate();
            this.repaint();
            rooms.add(room);
        }
        SaveLoad saveLoad = new SaveLoad(this);
    
        if(e.getSource() == save_btn){
            saveLoad.savePlan();
        }
        if(e.getSource() == load_btn){
            saveLoad.loadPlan();
        }
        if (e.getSource() == door_btn) {
            Door door = new Door();
                door.setLocation(670, 600); // Initial position
                this.add(door);
                this.revalidate();
                this.repaint();
        }
    }
    private void highlightSelectedRoom(Room selectedRoom) {
        // Reset borders of all rooms
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof Room) {
                ((Room) comp).setBorder(BorderFactory.createLineBorder(Color.black, 5));
            }
        }
        
        // Highlight the selected room
        selectedRoom.setBorder(BorderFactory.createLineBorder(Color.blue, 5));
    }
    

    private Room createRoom(int width, int height, Color color) {
    Room room = new Room();
    room.setBounds(0, 0, width, height);
    room.setBackground(color);
    room.setVisible(true);
    room.setOpaque(true);
    room.setBorder(BorderFactory.createLineBorder(Color.black, 5));
    
    room.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            selectedRoom = room;
            highlightSelectedRoom(room);
            layeredPane.moveToFront(room);
        }
    });
    
    // Add to layered pane instead of frame directly
    layeredPane.add(room, JLayeredPane.DEFAULT_LAYER);
    return room;
}

    
    private JButton createImageButton(int x,int y, int width, int height, ImageIcon image){
        JButton button = new JButton();
        button.setBounds(x,y,width,height);
        button.setFocusable(false);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder());
        Image scaledImage = image.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        button.setIcon(scaledIcon);
        return button;
    }
    private void createFurniture(ImageIcon image, String type) {
        Furniture furniture = new Furniture(image, type);
        
        // Find initial position (you can modify this as needed)
        furniture.setBounds(860,620,furniture.getWidth(),furniture.getHeight());
        // Add to frame
        this.add(furniture);
        this.revalidate();
        this.repaint();
    }   
}
