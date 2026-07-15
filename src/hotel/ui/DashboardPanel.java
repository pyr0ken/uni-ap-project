package hotel.ui;

import hotel.model.User;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private final MainFrame mainFrame;
    private final JPanel contentCards;
    private final CardLayout contentLayout;
    
    private JLabel lblUserInitials;
    private JLabel lblUserName;
    private JLabel lblUserCredit;

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
        // --- 1. LEFT SIDEBAR PANEL ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.BG_SECONDARY);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER_COLOR));

        // Profile Section
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(Theme.BG_SECONDARY);
        profilePanel.setMaximumSize(new Dimension(250, 200));
        profilePanel.setPreferredSize(new Dimension(250, 200));
        
        GridBagConstraints pGbc = new GridBagConstraints();
        pGbc.gridx = 0;
        pGbc.fill = GridBagConstraints.CENTER;
        pGbc.insets = new Insets(5, 5, 5, 5);

        // Circular Initials Avatar
        lblUserInitials = new JLabel("", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Theme.TEXT_PRIMARY);
                g2.setFont(Theme.FONT_TITLE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        lblUserInitials.setPreferredSize(new Dimension(70, 70));
        pGbc.gridy = 0;
        profilePanel.add(lblUserInitials, pGbc);

        lblUserName = new JLabel("", JLabel.CENTER);
        lblUserName.setFont(Theme.FONT_SUBTITLE);
        lblUserName.setForeground(Theme.TEXT_PRIMARY);
        pGbc.gridy = 1;
        pGbc.insets = new Insets(10, 5, 2, 5);
        profilePanel.add(lblUserName, pGbc);

        lblUserCredit = new JLabel("", JLabel.CENTER);
        lblUserCredit.setFont(Theme.FONT_BODY_BOLD);
        lblUserCredit.setForeground(Theme.SUCCESS);
        pGbc.gridy = 2;
        pGbc.insets = new Insets(2, 5, 5, 5);
        profilePanel.add(lblUserCredit, pGbc);

        sidebar.add(profilePanel);
        sidebar.add(Box.createVerticalStrut(20));

        // Navigation Buttons
        JButton btnRooms = createNavButton("🛏️  Rooms & Search");
        JButton btnBookings = createNavButton("📋  My Reservations");
        JButton btnCredit = createNavButton("💰  Credit Refill");
        JButton btnLogout = createNavButton("🚪  Logout");

        sidebar.add(btnRooms);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnBookings);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnCredit);
        
        sidebar.add(Box.createVerticalGlue()); // Push logout to bottom
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(30));

        add(sidebar, BorderLayout.WEST);

        // --- 2. RIGHT CONTENT PANELS ---
        roomsPanel = new RoomsPanel(mainFrame, this);
        bookingsPanel = new BookingsPanel(mainFrame, this);
        creditPanel = new CreditPanel(mainFrame, this);

        contentCards.add(roomsPanel, "ROOMS");
        contentCards.add(bookingsPanel, "BOOKINGS");
        contentCards.add(creditPanel, "CREDIT");

        add(contentCards, BorderLayout.CENTER);

        // Button Actions
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

        // Refresh initial user info
        refreshUserProfile();
        switchContent("ROOMS");
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_BODY_BOLD);
        btn.setForeground(Theme.TEXT_SECONDARY);
        btn.setBackground(Theme.BG_SECONDARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(Theme.BG_TERTIARY);
                btn.setForeground(Theme.TEXT_PRIMARY);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Theme.BG_SECONDARY);
                btn.setForeground(Theme.TEXT_SECONDARY);
            }
        });

        return btn;
    }

    public void switchContent(String cardName) {
        contentLayout.show(contentCards, cardName);
    }

    public void refreshUserProfile() {
        User user = mainFrame.getCurrentUser();
        if (user != null) {
            String initials = (user.getFirstName().substring(0, 1) + user.getLastName().substring(0, 1)).toUpperCase();
            lblUserInitials.setText(initials);
            lblUserName.setText(user.getFirstName() + " " + user.getLastName());
            lblUserCredit.setText("$" + user.getCredit().toPlainString());
        }
    }

    public RoomsPanel getRoomsPanel() {
        return roomsPanel;
    }

    public BookingsPanel getBookingsPanel() {
        return bookingsPanel;
    }
}
