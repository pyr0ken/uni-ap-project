package hotel.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Theme {
    // Premium Slate Dark Mode Color Palette
    public static final Color BG_PRIMARY = new Color(15, 23, 42);     // #0f172a - Slate 900
    public static final Color BG_SECONDARY = new Color(30, 41, 59);    // #1e293b - Slate 800
    public static final Color BG_TERTIARY = new Color(51, 65, 85);     // #334155 - Slate 700
    
    public static final Color ACCENT = new Color(14, 165, 233);        // #0ea5e9 - Sky 500
    public static final Color ACCENT_HOVER = new Color(56, 189, 248);  // #38bdf8 - Sky 400
    
    public static final Color SUCCESS = new Color(16, 185, 129);       // #10b981 - Emerald 500
    public static final Color DANGER = new Color(239, 68, 68);         // #ef4444 - Red 500
    public static final Color WARNING = new Color(245, 158, 11);       // #f59e0b - Amber 500
    
    public static final Color TEXT_PRIMARY = new Color(248, 250, 252);  // #f8fafc - Slate 50
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184);// #94a3b8 - Slate 400
    public static final Color TEXT_MUTED = new Color(100, 116, 139);    // #64748b - Slate 500
    
    public static final Color BORDER_COLOR = new Color(51, 65, 85);     // #334155 - Slate 700

    // Fonts
    public static final Font FONT_TITLE_LARGE = new Font("SansSerif", Font.BOLD, 26);
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_CAPTION = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_RATING = new Font("SansSerif", Font.PLAIN, 18);

    // Common Borders
    public static final Border PANEL_BORDER = BorderFactory.createLineBorder(BORDER_COLOR, 1, true);
    
    public static void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setFont(FONT_BODY_BOLD);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (bgColor.equals(ACCENT)) {
                    button.setBackground(ACCENT_HOVER);
                } else {
                    button.setBackground(bgColor.brighter());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    public static void styleTextField(JTextField textField) {
        textField.setFont(FONT_BODY);
        textField.setBackground(BG_SECONDARY);
        textField.setForeground(TEXT_PRIMARY);
        textField.setCaretColor(TEXT_PRIMARY);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(FONT_BODY);
        comboBox.setBackground(BG_SECONDARY);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        // Simple dark mode rendering fallback
        UIManager.put("ComboBox.background", BG_SECONDARY);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", ACCENT);
        UIManager.put("ComboBox.selectionForeground", TEXT_PRIMARY);
    }
}
