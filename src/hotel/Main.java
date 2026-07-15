package hotel;

import hotel.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Failed to start the application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
