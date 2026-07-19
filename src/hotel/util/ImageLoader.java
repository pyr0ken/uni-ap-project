package hotel.util;

import hotel.model.Room.RoomType;
import hotel.ui.Theme;

import javax.swing.*;
import java.awt.*;

public class ImageLoader {
    public static void loadRoomImage(JLabel label, RoomType type) {
        String imgPath;
        switch(type) {
            case SINGLE:
                imgPath = "assets/single.jpg";
                break;
            case DOUBLE:
                imgPath = "assets/double.jpg";
                break;
            case SUITE:
                imgPath = "assets/suite.jpg";
                break;
            default:
                imgPath = "assets/default.jpg";
                break;
        }

        label.setText("Loading...");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(Theme.TEXT_MUTED);
        label.setFont(Theme.FONT_CAPTION);
        label.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true));

        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                try {
                    java.io.File file = new java.io.File(imgPath);
                    if (file.exists()) {
                        java.awt.Image img = javax.imageio.ImageIO.read(file);
                        if (img != null) {
                            java.awt.Image scaled = img.getScaledInstance(130, 100, java.awt.Image.SCALE_SMOOTH);
                            return new ImageIcon(scaled);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load local image: " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        label.setText("");
                        label.setIcon(icon);
                        label.setBorder(null);
                    } else {
                        label.setText("No Image");
                    }
                } catch (Exception e) {
                    label.setText("No Image");
                }
            }
        };
        worker.execute();
    }
}
