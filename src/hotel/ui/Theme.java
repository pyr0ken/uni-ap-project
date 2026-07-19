package hotel.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Theme {
    // Warm, high-contrast hospitality palette.
    public static final Color BG_PRIMARY = new Color(247, 244, 238);     // linen
    public static final Color BG_SECONDARY = new Color(255, 253, 249);   // ivory
    public static final Color BG_TERTIARY = new Color(232, 226, 216);    // stone

    public static final Color ACCENT = new Color(31, 93, 78);            // forest
    public static final Color ACCENT_HOVER = new Color(43, 119, 98);
    public static final Color ACCENT_SOFT = new Color(224, 236, 229);
    public static final Color NAV_BACKGROUND = new Color(27, 58, 50);
    public static final Color NAV_HOVER = new Color(41, 80, 69);
    public static final Color NAV_TEXT = new Color(248, 246, 240);
    public static final Color NAV_SURFACE = new Color(34, 70, 61);
    public static final Color NAV_ACTIVE = new Color(73, 163, 137);
    public static final Color NAV_MUTED = new Color(180, 201, 190);
    public static final Color NAV_DANGER = new Color(246, 178, 171);

    public static final Color SUCCESS = new Color(45, 125, 94);
    public static final Color DANGER = new Color(181, 72, 65);
    public static final Color WARNING = new Color(190, 112, 53);        // terracotta

    public static final Color TEXT_PRIMARY = new Color(35, 39, 35);
    public static final Color TEXT_SECONDARY = new Color(88, 96, 86);
    public static final Color TEXT_MUTED = new Color(124, 130, 119);

    public static final Color BORDER_COLOR = new Color(213, 207, 196);

    // Fonts
    public static final Font FONT_TITLE_LARGE = new Font("SansSerif", Font.BOLD, 28);
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 21);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_CAPTION = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_RATING = new Font("SansSerif", Font.PLAIN, 18);

    // Common Borders
    public static final Border PANEL_BORDER = BorderFactory.createLineBorder(BORDER_COLOR, 1, true);

    public static void installDefaults() {
        UIManager.put("Panel.background", BG_PRIMARY);
        UIManager.put("Viewport.background", BG_PRIMARY);
        UIManager.put("ScrollPane.background", BG_PRIMARY);
        UIManager.put("TextField.background", BG_SECONDARY);
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground", TEXT_PRIMARY);
        UIManager.put("ComboBox.background", BG_SECONDARY);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", ACCENT);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("OptionPane.background", BG_SECONDARY);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
    }
    
    public static void styleButton(JButton button, Color bgColor, Color fgColor) {
        Color resolvedForeground = fgColor;
        if (fgColor.equals(TEXT_PRIMARY)
                && (bgColor.equals(ACCENT) || bgColor.equals(SUCCESS) || bgColor.equals(DANGER))) {
            resolvedForeground = Color.WHITE;
        }
        final Color normalForeground = resolvedForeground;
        button.setFont(FONT_BODY_BOLD);
        button.setBackground(bgColor);
        button.setForeground(normalForeground);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1, true),
                BorderFactory.createEmptyBorder(9, 18, 9, 18)
        ));
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.isEnabled()) return;
                if (bgColor.equals(ACCENT)) {
                    button.setBackground(ACCENT_HOVER);
                } else {
                    button.setBackground(bgColor.equals(BG_TERTIARY) ? BG_TERTIARY.brighter() : bgColor.brighter());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(bgColor);
            }
        });
        button.addPropertyChangeListener("enabled", e -> {
            if (button.isEnabled()) {
                button.setBackground(bgColor);
                button.setForeground(normalForeground);
            } else {
                button.setBackground(BG_TERTIARY);
                button.setForeground(TEXT_MUTED);
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
        UIManager.put("ComboBox.background", BG_SECONDARY);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", ACCENT);
        UIManager.put("ComboBox.selectionForeground", TEXT_PRIMARY);
    }

    public static class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;
        private Color borderColor = Theme.BORDER_COLOR;
        private boolean showBorder = true;

        public RoundedPanel(int radius, Color bg) {
            super(new BorderLayout());
            this.cornerRadius = radius;
            this.backgroundColor = bg;
            setOpaque(false);
        }

        public RoundedPanel(int radius, Color bg, boolean showBorder) {
            super(new BorderLayout());
            this.cornerRadius = radius;
            this.backgroundColor = bg;
            this.showBorder = showBorder;
            setOpaque(false);
        }

        public void setBorderColor(Color color) {
            this.borderColor = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (backgroundColor != null) {
                graphics.setColor(backgroundColor);
            } else {
                graphics.setColor(getBackground());
            }
            graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
            
            if (showBorder) {
                graphics.setColor(borderColor);
                graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
            }
        }
    }
}
