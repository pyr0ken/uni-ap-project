package hotel.ui;

import hotel.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private final MainFrame mainFrame;
    private final JPanel contentCards;
    private final CardLayout contentLayout;

    private JLabel lblUserInitials;
    private JLabel lblUserName;
    private JLabel lblUserCredit;
    private final Map<String, JButton> navigationButtons = new LinkedHashMap<>();

    private RoomsPanel roomsPanel;
    private BookingsPanel bookingsPanel;
    private CreditPanel creditPanel;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_PRIMARY);

        contentLayout = new CardLayout();
        contentCards = new JPanel(contentLayout);
        contentCards.setBackground(Theme.BG_PRIMARY);

        initComponent();
    }

    private void initComponent() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.NAV_BACKGROUND);
        sidebar.setPreferredSize(new Dimension(252, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 16, 18, 16));

        sidebar.add(createBrandPanel());
        sidebar.add(createProfilePanel());
        sidebar.add(Box.createVerticalStrut(24));

        JLabel lblNavigation = new JLabel("NAVIGATION");
        lblNavigation.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblNavigation.setForeground(Theme.NAV_MUTED);
        lblNavigation.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 0));
        lblNavigation.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblNavigation);

        JButton btnRooms = createNavButton("ROOMS", "Find a Room", NavIcon.Type.ROOMS, false);
        JButton btnBookings = createNavButton("BOOKINGS", "My Reservations", NavIcon.Type.BOOKINGS, false);
        JButton btnCredit = createNavButton("CREDIT", "Account Credit", NavIcon.Type.CREDIT, false);
        JButton btnLogout = createNavButton("LOGOUT", "Sign Out", NavIcon.Type.LOGOUT, true);

        sidebar.add(btnRooms);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnBookings);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnCredit);
        sidebar.add(Box.createVerticalGlue());

        JSeparator separator = new JSeparator();
        separator.setForeground(Theme.NAV_HOVER);
        separator.setBackground(Theme.NAV_HOVER);
        separator.setMaximumSize(new Dimension(220, 1));
        sidebar.add(separator);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(2));

        add(sidebar, BorderLayout.WEST);

        roomsPanel = new RoomsPanel(mainFrame, this);
        bookingsPanel = new BookingsPanel(mainFrame, this);
        creditPanel = new CreditPanel(mainFrame, this);

        contentCards.add(roomsPanel, "ROOMS");
        contentCards.add(bookingsPanel, "BOOKINGS");
        contentCards.add(creditPanel, "CREDIT");
        add(contentCards, BorderLayout.CENTER);

        btnRooms.addActionListener(e -> switchContent("ROOMS"));
        btnBookings.addActionListener(e -> {
            bookingsPanel.refreshData();
            switchContent("BOOKINGS");
        });
        btnCredit.addActionListener(e -> {
            creditPanel.refreshData();
            switchContent("CREDIT");
        });
        btnLogout.addActionListener(e -> mainFrame.logout());

        refreshUserProfile();
        switchContent("ROOMS");
    }

    private JPanel createBrandPanel() {
        JPanel brandPanel = new JPanel(new BorderLayout(12, 0));
        brandPanel.setBackground(Theme.NAV_BACKGROUND);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(22, 4, 18, 4));
        brandPanel.setMaximumSize(new Dimension(220, 86));
        brandPanel.setPreferredSize(new Dimension(220, 86));
        brandPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Theme.RoundedPanel brandMark = new Theme.RoundedPanel(10, Theme.WARNING, false);
        brandMark.setPreferredSize(new Dimension(44, 44));
        JLabel lblBrandMark = new JLabel("GL", JLabel.CENTER);
        lblBrandMark.setFont(Theme.FONT_BODY_BOLD);
        lblBrandMark.setForeground(Color.WHITE);
        brandMark.add(lblBrandMark, BorderLayout.CENTER);
        brandPanel.add(brandMark, BorderLayout.WEST);

        JPanel brandCopy = new JPanel();
        brandCopy.setLayout(new BoxLayout(brandCopy, BoxLayout.Y_AXIS));
        brandCopy.setBackground(Theme.NAV_BACKGROUND);

        JLabel lblBrand = new JLabel("GRAND LUXE");
        lblBrand.setFont(Theme.FONT_SUBTITLE);
        lblBrand.setForeground(Theme.NAV_TEXT);
        lblBrand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBrandMeta = new JLabel("HOTEL & RESIDENCE");
        lblBrandMeta.setFont(Theme.FONT_CAPTION);
        lblBrandMeta.setForeground(Theme.NAV_MUTED);
        lblBrandMeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandCopy.add(Box.createVerticalGlue());
        brandCopy.add(lblBrand);
        brandCopy.add(Box.createVerticalStrut(3));
        brandCopy.add(lblBrandMeta);
        brandCopy.add(Box.createVerticalGlue());
        brandPanel.add(brandCopy, BorderLayout.CENTER);

        return brandPanel;
    }

    private JPanel createProfilePanel() {
        Theme.RoundedPanel profilePanel = new Theme.RoundedPanel(10, Theme.NAV_SURFACE, false);
        profilePanel.setLayout(new BorderLayout(12, 0));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        profilePanel.setMaximumSize(new Dimension(220, 86));
        profilePanel.setPreferredSize(new Dimension(220, 86));
        profilePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblUserInitials = new JLabel("", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.WARNING);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(Theme.FONT_SUBTITLE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        lblUserInitials.setPreferredSize(new Dimension(48, 48));
        profilePanel.add(lblUserInitials, BorderLayout.WEST);

        JPanel userDetails = new JPanel();
        userDetails.setLayout(new BoxLayout(userDetails, BoxLayout.Y_AXIS));
        userDetails.setOpaque(false);

        lblUserName = new JLabel("");
        lblUserName.setFont(Theme.FONT_BODY_BOLD);
        lblUserName.setForeground(Theme.NAV_TEXT);
        lblUserName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCreditCaption = new JLabel("Available credit");
        lblCreditCaption.setFont(Theme.FONT_CAPTION);
        lblCreditCaption.setForeground(Theme.NAV_MUTED);
        lblCreditCaption.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblUserCredit = new JLabel("");
        lblUserCredit.setFont(Theme.FONT_BODY_BOLD);
        lblUserCredit.setForeground(new Color(169, 218, 193));
        lblUserCredit.setAlignmentX(Component.LEFT_ALIGNMENT);

        userDetails.add(lblUserName);
        userDetails.add(Box.createVerticalStrut(3));
        userDetails.add(lblCreditCaption);
        userDetails.add(Box.createVerticalStrut(1));
        userDetails.add(lblUserCredit);
        profilePanel.add(userDetails, BorderLayout.CENTER);

        return profilePanel;
    }

    private JButton createNavButton(String key, String text, NavIcon.Type iconType, boolean danger) {
        JButton button = new SidebarButton(text);
        button.setFont(Theme.FONT_BODY_BOLD);
        button.setForeground(danger ? Theme.NAV_DANGER : Theme.NAV_MUTED);
        button.setBackground(Theme.NAV_BACKGROUND);
        button.setIcon(new NavIcon(iconType));
        button.setIconTextGap(13);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 46));
        button.setPreferredSize(new Dimension(220, 46));
        button.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty("danger", danger);
        navigationButtons.put(key, button);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                if (!Boolean.TRUE.equals(button.getClientProperty("active"))) {
                    button.setBackground(danger ? new Color(84, 55, 50) : Theme.NAV_HOVER);
                    button.setForeground(danger ? Theme.NAV_DANGER : Theme.NAV_TEXT);
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                if (!Boolean.TRUE.equals(button.getClientProperty("active"))) {
                    button.setBackground(Theme.NAV_BACKGROUND);
                    button.setForeground(danger ? Theme.NAV_DANGER : Theme.NAV_MUTED);
                }
            }
        });

        return button;
    }

    public void switchContent(String cardName) {
        contentLayout.show(contentCards, cardName);
        navigationButtons.forEach((key, button) -> {
            boolean active = key.equals(cardName);
            boolean danger = Boolean.TRUE.equals(button.getClientProperty("danger"));
            button.putClientProperty("active", active);
            button.setBackground(active ? Theme.NAV_HOVER : Theme.NAV_BACKGROUND);
            button.setForeground(active ? Theme.NAV_TEXT : (danger ? Theme.NAV_DANGER : Theme.NAV_MUTED));
            button.repaint();
        });
    }

    public void refreshUserProfile() {
        User user = mainFrame.getCurrentUser();
        if (user != null) {
            String fn = (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) ? user.getFirstName().trim() : user.getUsername();
            String ln = (user.getLastName() != null && !user.getLastName().trim().isEmpty()) ? user.getLastName().trim() : "";
            String fnInitial = fn.substring(0, 1).toUpperCase();
            String lnInitial = !ln.isEmpty() ? ln.substring(0, 1).toUpperCase() : "";
            String initials = fnInitial + lnInitial;
            String fullName = user.getFirstName() + " " + user.getLastName();
            lblUserInitials.setText(initials);
            lblUserName.setText(fullName.trim());
            lblUserName.setToolTipText(fullName.trim());
            lblUserCredit.setText("$" + user.getCredit().toPlainString());
        }
    }

    public RoomsPanel getRoomsPanel() {
        return roomsPanel;
    }

    public BookingsPanel getBookingsPanel() {
        return bookingsPanel;
    }

    private static class SidebarButton extends JButton {
        SidebarButton(String text) {
            super(text);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            boolean active = Boolean.TRUE.equals(getClientProperty("active"));
            if (active || !getBackground().equals(Theme.NAV_BACKGROUND)) {
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
            if (active) {
                g2.setColor(Theme.NAV_ACTIVE);
                g2.fillRoundRect(0, 9, 4, getHeight() - 18, 4, 4);
            }
            if (hasFocus()) {
                g2.setColor(Theme.NAV_ACTIVE);
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
            }
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class NavIcon implements Icon {
        enum Type { ROOMS, BOOKINGS, CREDIT, LOGOUT }

        private final Type type;

        NavIcon(Type type) {
            this.type = type;
        }

        @Override
        public int getIconWidth() {
            return 19;
        }

        @Override
        public int getIconHeight() {
            return 19;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(component.getForeground());
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (type) {
                case ROOMS:
                    g2.drawLine(2, 5, 2, 16);
                    g2.drawLine(2, 12, 17, 12);
                    g2.drawLine(17, 9, 17, 16);
                    g2.drawRoundRect(4, 8, 13, 4, 2, 2);
                    g2.drawLine(2, 16, 2, 17);
                    g2.drawLine(17, 16, 17, 17);
                    break;
                case BOOKINGS:
                    g2.drawRoundRect(4, 3, 12, 14, 3, 3);
                    g2.drawRoundRect(7, 1, 6, 4, 2, 2);
                    g2.drawLine(7, 9, 13, 9);
                    g2.drawLine(7, 13, 12, 13);
                    break;
                case CREDIT:
                    g2.drawRoundRect(2, 5, 15, 11, 3, 3);
                    g2.drawLine(3, 8, 16, 8);
                    g2.fillOval(12, 11, 2, 2);
                    break;
                case LOGOUT:
                    g2.drawRoundRect(2, 3, 8, 14, 2, 2);
                    g2.drawLine(8, 10, 17, 10);
                    g2.drawLine(14, 7, 17, 10);
                    g2.drawLine(14, 13, 17, 10);
                    break;
            }
            g2.dispose();
        }
    }
}
