package com.harshalshah.floorplanner;

import com.harshalshah.floorplanner.ui.MainFrame;

import javax.swing.SwingUtilities;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
