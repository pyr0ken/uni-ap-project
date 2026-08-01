package hotel.ui;

import hotel.exception.HotelException;
import hotel.model.Reservation;
import hotel.model.Reservation.ReservationStatus;
import hotel.model.Review;
import hotel.model.Room;
import hotel.service.HotelService;
import hotel.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookingsPanel extends JPanel {
    private final MainFrame mainFrame;
    private final DashboardPanel dashboardPanel;
    private final HotelService service;

    private JComboBox<String> cbFilter;
    private JPanel bookingsList;
    private JScrollPane scrollPane;

    public BookingsPanel(MainFrame mainFrame, DashboardPanel dashboardPanel) {
        this.mainFrame = mainFrame;
        this.dashboardPanel = dashboardPanel;
        this.service = mainFrame.getService();

        setLayout(new BorderLayout());
        setBackground(Theme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponent();
        refreshData();
    }

    private void initComponent() {
        // Top Header and filter row
        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setBackground(Theme.BG_PRIMARY);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Theme.BG_PRIMARY);

        JLabel lblTitle = new JLabel("Your Reservations");
        lblTitle.setFont(Theme.FONT_TITLE_LARGE);
        lblTitle.setForeground(Theme.TEXT_PRIMARY);
        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(3));
        JLabel lblSubtitle = new JLabel("Manage upcoming stays, invoices, cancellations, and reviews.");
        lblSubtitle.setFont(Theme.FONT_BODY);
        lblSubtitle.setForeground(Theme.TEXT_SECONDARY);
        titlePanel.add(lblSubtitle);
        topPanel.add(titlePanel, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(Theme.BG_PRIMARY);
        
        JLabel lblFilter = new JLabel("Filter Status:");
        lblFilter.setFont(Theme.FONT_BODY_BOLD);
        lblFilter.setForeground(Theme.TEXT_SECONDARY);
        filterPanel.add(lblFilter);

        cbFilter = new JComboBox<>(new String[]{"All Bookings", "Active", "Completed", "Cancelled"});
        Theme.styleComboBox(cbFilter);
        cbFilter.addActionListener(e -> populateBookings());
        filterPanel.add(cbFilter);

        topPanel.add(filterPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Bookings container
        bookingsList = new JPanel();
        bookingsList.setLayout(new GridBagLayout());
        bookingsList.setBackground(Theme.BG_PRIMARY);

        scrollPane = new JScrollPane(bookingsList);
        scrollPane.setBackground(Theme.BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        // Run auto-complete before displaying to make sure past checkouts are up-to-date
        service.autoCompleteReservations();
        populateBookings();
    }

    private void populateBookings() {
        bookingsList.removeAll();
        
        String username = mainFrame.getCurrentUser().getUsername();
        List<Reservation> userRes = service.getUserReservations(username);

        String filter = (String) cbFilter.getSelectedItem();
        if (filter != null && !filter.equals("All Bookings")) {
            userRes = userRes.stream()
                    .filter(r -> r.getStatus().getDisplayName().equalsIgnoreCase(filter))
                    .collect(Collectors.toList());
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 8, 0);

        if (userRes.isEmpty()) {
            JLabel lblEmpty = new JLabel("No reservations match this status.", JLabel.CENTER);
            lblEmpty.setFont(Theme.FONT_SUBTITLE);
            lblEmpty.setForeground(Theme.TEXT_SECONDARY);
            gbc.gridy = 0;
            bookingsList.add(lblEmpty, gbc);
        } else {
            int row = 0;
            // Show latest bookings first
            for (int i = userRes.size() - 1; i >= 0; i--) {
                Reservation res = userRes.get(i);
                gbc.gridy = row++;
                JPanel card = createBookingCard(res);
                bookingsList.add(card, gbc);
            }
        }

        bookingsList.revalidate();
        bookingsList.repaint();
    }

    private JPanel createBookingCard(Reservation res) {
        Theme.RoundedPanel card = new Theme.RoundedPanel(16, Theme.BG_SECONDARY);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Image Label (West)
        JLabel lblImage = new JLabel();
        lblImage.setPreferredSize(new Dimension(130, 100));
        Room room = service.getAllRooms().stream()
                .filter(r -> r.getRoomNumber().equals(res.getRoomNumber()))
                .findFirst().orElse(null);
        Room.RoomType roomType = (room != null) ? room.getType() : Room.RoomType.SINGLE;
        ImageLoader.loadRoomImage(lblImage, roomType);
        card.add(lblImage, BorderLayout.WEST);

        // Details Panel (Center)
        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setBackground(Theme.BG_SECONDARY);
        details.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        // Title: ID and Status badge
        JPanel headerLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerLine.setBackground(Theme.BG_SECONDARY);
        headerLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblId = new JLabel(res.getReservationId() + "  ");
        lblId.setFont(Theme.FONT_SUBTITLE);
        lblId.setForeground(Theme.TEXT_PRIMARY);
        headerLine.add(lblId);

        JLabel lblBadge = new JLabel("  " + res.getStatus().getDisplayName() + "  ");
        lblBadge.setFont(Theme.FONT_CAPTION);
        lblBadge.setForeground(Color.WHITE);
        
        if (res.getStatus() == ReservationStatus.ACTIVE) {
            lblBadge.setBackground(Theme.ACCENT);
        } else if (res.getStatus() == ReservationStatus.COMPLETED) {
            lblBadge.setBackground(Theme.SUCCESS);
        } else {
            lblBadge.setBackground(Theme.DANGER);
        }
        lblBadge.setOpaque(true);
        lblBadge.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        headerLine.add(lblBadge);
        
        details.add(headerLine);
        details.add(Box.createVerticalStrut(6));

        // Room details & Stay dates
        JLabel lblRoom = new JLabel("Room " + res.getRoomNumber() + "  |  Check-in: " + res.getCheckInDate() + "  →  Check-out: " + res.getCheckOutDate());
        lblRoom.setFont(Theme.FONT_BODY);
        lblRoom.setForeground(Theme.TEXT_PRIMARY);
        lblRoom.setAlignmentX(Component.LEFT_ALIGNMENT);
        details.add(lblRoom);
        details.add(Box.createVerticalStrut(4));

        long nights = ChronoUnit.DAYS.between(res.getCheckInDate(), res.getCheckOutDate());
        JLabel lblMeta = new JLabel("Duration: " + nights + " nights  |  Guests: " + res.getTotalGuests() + "  |  Paid: $" + res.getTotalCost().toPlainString());
        lblMeta.setFont(Theme.FONT_CAPTION);
        lblMeta.setForeground(Theme.TEXT_SECONDARY);
        lblMeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        details.add(lblMeta);

        card.add(details, BorderLayout.CENTER);

        // Actions Panel (Right)
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBackground(Theme.BG_SECONDARY);
        actions.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_COLOR));

        JButton btnInvoice = new JButton("View Invoice");
        Theme.styleButton(btnInvoice, Theme.BG_TERTIARY, Theme.TEXT_PRIMARY);
        btnInvoice.addActionListener(e -> displayInvoice(res.getReservationId()));
        actions.add(btnInvoice);

        if (res.getStatus() == ReservationStatus.ACTIVE) {
            JButton btnCancel = new JButton("Cancel Booking");
            Theme.styleButton(btnCancel, Theme.DANGER, Theme.TEXT_PRIMARY);
            btnCancel.addActionListener(e -> cancelBooking(res));
            actions.add(btnCancel);
        } else if (res.getStatus() == ReservationStatus.COMPLETED) {
            JButton btnReview = new JButton("Rate & Review");
            Theme.styleButton(btnReview, Theme.WARNING, Theme.TEXT_PRIMARY);
            btnReview.addActionListener(e -> openReviewDialog(res));
            actions.add(btnReview);
        }

        card.add(actions, BorderLayout.SOUTH);

        // Card Hover highlight effects
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorderColor(Theme.ACCENT);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorderColor(Theme.BORDER_COLOR);
            }
        });

        return card;
    }

    private void displayInvoice(String reservationId) {
        String path = "invoices/invoice_" + reservationId + ".txt";
        String content = "";
        try {
            if (Files.exists(Paths.get(path))) {
                content = Files.readString(Paths.get(path));
            } else {
                content = "Invoice file not found on disk.";
            }
        } catch (IOException e) {
            content = "Error loading invoice: " + e.getMessage();
        }

        JTextArea textArea = new JTextArea(content);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(Theme.BG_PRIMARY);
        textArea.setForeground(Theme.TEXT_PRIMARY);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(500, 450));
        scroll.setBorder(null);

        JOptionPane.showMessageDialog(this, scroll, "Booking Invoice - " + reservationId, JOptionPane.PLAIN_MESSAGE);
    }

    private void cancelBooking(Reservation res) {
        long daysToCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), res.getCheckInDate());
        String refundPercent = (daysToCheckIn >= 2) ? "100%" : "50%";
        BigDecimal estRefund = (daysToCheckIn >= 2) ? res.getTotalCost() : res.getTotalCost().multiply(new BigDecimal("0.50"));

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Cancellation details:\n\n• Days to check-in: %d days\n• Refund entitlement: %s\n• Refund amount: $%s\n\nAre you sure you want to cancel?",
                        daysToCheckIn, refundPercent, estRefund.toPlainString()),
                "Cancel Reservation?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                BigDecimal refunded = service.cancelReservation(res.getReservationId());
                JOptionPane.showMessageDialog(this,
                        String.format("Successfully cancelled! $%s has been refunded to your account.", refunded.toPlainString()),
                        "Cancelled Successfully", JOptionPane.INFORMATION_MESSAGE);

                // Update UI elements
                dashboardPanel.refreshUserProfile();
                refreshData();
                dashboardPanel.getRoomsPanel().searchRooms(); // Refresh rooms to mark them available again

            } catch (HotelException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Cancellation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openReviewDialog(Reservation res) {
        Optional<Review> existingRev = service.getUserReviewForRoom(res.getRoomNumber(), res.getUsername());

        // Form layout for reviews
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_SECONDARY);
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        JLabel lblStar = new JLabel("Rating (1 - 5 stars):");
        lblStar.setFont(Theme.FONT_BODY_BOLD);
        lblStar.setForeground(Theme.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(lblStar, gbc);

        JComboBox<Integer> cbStars = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});
        Theme.styleComboBox(cbStars);
        if (existingRev.isPresent()) {
            cbStars.setSelectedItem(existingRev.get().getRating());
        }
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(cbStars, gbc);

        JLabel lblComment = new JLabel("Write your review:");
        lblComment.setFont(Theme.FONT_BODY_BOLD);
        lblComment.setForeground(Theme.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(lblComment, gbc);

        JTextArea txtComment = new JTextArea(4, 30);
        if (existingRev.isPresent()) {
            txtComment.setText(existingRev.get().getComment());
        }
        txtComment.setBackground(Theme.BG_PRIMARY);
        txtComment.setForeground(Theme.TEXT_PRIMARY);
        txtComment.setCaretColor(Theme.TEXT_PRIMARY);
        txtComment.setFont(Theme.FONT_BODY);
        txtComment.setLineWrap(true);
        txtComment.setWrapStyleWord(true);
        txtComment.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        
        JScrollPane textScroll = new JScrollPane(txtComment);
        textScroll.setBorder(null);
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(textScroll, gbc);

        String dialogTitle = existingRev.isPresent() ? "Update Review for Room " : "Leave Review for Room ";
        int option = JOptionPane.showConfirmDialog(this, form, dialogTitle + res.getRoomNumber(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            int rating = (int) cbStars.getSelectedItem();
            String comment = txtComment.getText().trim();
            if (comment.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please write a comment.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                service.addReview(res.getRoomNumber(), res.getUsername(), rating, comment);
                JOptionPane.showMessageDialog(this, "Thank you! Your review has been saved.", "Review Saved", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh rooms in case average ratings changed
                dashboardPanel.getRoomsPanel().searchRooms();
            } catch (HotelException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Review Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
