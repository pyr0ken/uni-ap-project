package hotel.ui;

import hotel.model.Reservation;
import hotel.model.User;
import hotel.service.HotelService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private final HotelService service;
    private User currentUser;
    private final JPanel cards;
    private final CardLayout cardLayout;

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private DashboardPanel dashboardPanel;

    public MainFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        Theme.installDefaults();

        this.service = new HotelService();
        this.cardLayout = new CardLayout();
        this.cards = new JPanel(cardLayout);

        setTitle("Grand Luxe | Hotel Reservations");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_PRIMARY);
        
        initViews();
        showLogin();
    }

    private void initViews() {
        cards.setBackground(Theme.BG_PRIMARY);
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        
        cards.add(loginPanel, "LOGIN");
        cards.add(registerPanel, "REGISTER");
        
        add(cards);
    }

    public HotelService getService() {
        return service;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void showLogin() {
        loginPanel.clearFields();
        cardLayout.show(cards, "LOGIN");
    }

    public void showRegister() {
        registerPanel.clearFields();
        cardLayout.show(cards, "REGISTER");
    }

    public void loginSuccessful(User user) {
        this.currentUser = user;
        
        // Setup dashboard panel now that we have logged in user
        dashboardPanel = new DashboardPanel(this);
        cards.add(dashboardPanel, "DASHBOARD");
        cardLayout.show(cards, "DASHBOARD");

        // Trigger reminders popup
        checkLoginReminders();
    }

    public void logout() {
        this.currentUser = null;
        if (dashboardPanel != null) {
            cards.remove(dashboardPanel);
            dashboardPanel = null;
        }
        showLogin();
    }

    private void checkLoginReminders() {
        if (currentUser == null) return;
        List<Reservation> reminders = service.getCheckInReminders(currentUser.getUsername());
        if (!reminders.isEmpty()) {
            StringBuilder message = new StringBuilder("⏰ Upcoming Check-in Reminders:\n\n");
            for (Reservation r : reminders) {
                message.append("• Room ").append(r.getRoomNumber())
                       .append(" checking in on ").append(r.getCheckInDate())
                       .append(" (Total Guests: ").append(r.getTotalGuests()).append(")\n");
            }
            JOptionPane.showMessageDialog(this, message.toString(), "Stay Reminder", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
