 package hotel.ui;

import hotel.exception.HotelException;
import hotel.model.Reservation;
import hotel.model.Room;
import hotel.model.Room.RoomType;
import hotel.service.HotelService;
import hotel.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoomsPanel extends JPanel {
    private final MainFrame mainFrame;
    private final DashboardPanel dashboardPanel;
    private final HotelService service;

    // Search Controls
    private JTextField txtCheckIn;
    private JTextField txtCheckOut;
    private JComboBox<String> cbRoomType;
    private JComboBox<String> cbMinRating;
    private JTextField txtMinPrice;
    private JTextField txtMaxPrice;
    private JSpinner spinGuests;
    private JLabel lblSubtitle;

    // Amenities Checkboxes
    private JCheckBox chkTV;
    private JCheckBox chkInternet;
    private JCheckBox chkFridge;
    private JCheckBox chkKitchen;
    private JCheckBox chkJacuzzi;
    private JCheckBox chkBalcony;

    // Room List Container
    private JPanel listContainer;
    private JScrollPane scrollPane;

    // Multi-Booking selections
    private final Set<String> selectedRoomNumbers = new HashSet<>();
    private JLabel lblSelectedCount;
    private JLabel lblEstimatedTotal;
    private JButton btnBookSelected;

    public RoomsPanel(MainFrame mainFrame, DashboardPanel dashboardPanel) {
        this.mainFrame = mainFrame;
        this.dashboardPanel = dashboardPanel;
        this.service = mainFrame.getService();

        setLayout(new BorderLayout());
        setBackground(Theme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponent();
        searchRooms(); // Load initial room list
    }

    private void initComponent() {
        // --- 1. TOP HEADER & FILTER BAR ---
        JPanel headerPanel = new JPanel(new BorderLayout(0, 14));
        headerPanel.setBackground(Theme.BG_PRIMARY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Theme.BG_PRIMARY);

        JLabel lblTitle = new JLabel("Explore Rooms & Book Stays");
        lblTitle.setFont(Theme.FONT_TITLE_LARGE);
        lblTitle.setForeground(Theme.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(3));
        lblSubtitle = new JLabel("Compare available rooms, amenities, and total stay prices.");
        lblSubtitle.setFont(Theme.FONT_BODY);
        lblSubtitle.setForeground(Theme.TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(lblSubtitle);
        headerPanel.add(titlePanel, BorderLayout.NORTH);

        // Filter Grid Panel
        Theme.RoundedPanel filterGrid = new Theme.RoundedPanel(10, Theme.BG_SECONDARY);
        filterGrid.setLayout(new GridBagLayout());
        filterGrid.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.weightx = 1.0;

        // Row 0: Dates & Type
        gbc.gridx = 0; gbc.gridy = 0;
        filterGrid.add(createLabel("Check-in Date (YYYY-MM-DD)"), gbc);
        
        txtCheckIn = new JTextField(LocalDate.now().toString());
        Theme.styleTextField(txtCheckIn);
        gbc.gridx = 0; gbc.gridy = 1;
        filterGrid.add(txtCheckIn, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        filterGrid.add(createLabel("Check-out Date (YYYY-MM-DD)"), gbc);

        txtCheckOut = new JTextField(LocalDate.now().plusDays(2).toString());
        Theme.styleTextField(txtCheckOut);
        gbc.gridx = 1; gbc.gridy = 1;
        filterGrid.add(txtCheckOut, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        filterGrid.add(createLabel("Room Type"), gbc);

        cbRoomType = new JComboBox<>(new String[]{"All Types", "Single", "Double", "Suite"});
        Theme.styleComboBox(cbRoomType);
        gbc.gridx = 2; gbc.gridy = 1;
        filterGrid.add(cbRoomType, gbc);

        // Row 1: Prices & Guests & Min Rating
        gbc.gridx = 0; gbc.gridy = 2;
        filterGrid.add(createLabel("Min Price ($)"), gbc);

        txtMinPrice = new JTextField();
        Theme.styleTextField(txtMinPrice);
        gbc.gridx = 0; gbc.gridy = 3;
        filterGrid.add(txtMinPrice, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        filterGrid.add(createLabel("Max Price ($)"), gbc);

        txtMaxPrice = new JTextField();
        Theme.styleTextField(txtMaxPrice);
        gbc.gridx = 1; gbc.gridy = 3;
        filterGrid.add(txtMaxPrice, gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        filterGrid.add(createLabel("Number of Guests"), gbc);

        spinGuests = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinGuests.getEditor();
        editor.getTextField().setBackground(Theme.BG_SECONDARY);
        editor.getTextField().setForeground(Theme.TEXT_PRIMARY);
        editor.getTextField().setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        spinGuests.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        gbc.gridx = 2; gbc.gridy = 3;
        filterGrid.add(spinGuests, gbc);

        // Row 2: Minimum Rating Filter
        gbc.gridx = 0; gbc.gridy = 4;
        filterGrid.add(createLabel("Minimum Rating"), gbc);

        cbMinRating = new JComboBox<>(new String[]{"All Ratings", "4.0+ Stars ★★★★", "3.0+ Stars ★★★", "2.0+ Stars ★★", "1.0+ Star ★"});
        Theme.styleComboBox(cbMinRating);
        gbc.gridx = 0; gbc.gridy = 5;
        filterGrid.add(cbMinRating, gbc);

        // Row 3: Amenities Title
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 3;
        filterGrid.add(createLabel("Amenities"), gbc);
        gbc.gridwidth = 1;

        // Row 4: Amenities Checkboxes Panel
        JPanel amenitiesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        amenitiesPanel.setBackground(Theme.BG_SECONDARY);
        
        chkTV = createCheckBox("TV");
        chkInternet = createCheckBox("Internet");
        chkFridge = createCheckBox("Refrigerator");
        chkKitchen = createCheckBox("Kitchen");
        chkJacuzzi = createCheckBox("Jacuzzi");
        chkBalcony = createCheckBox("Balcony");

        amenitiesPanel.add(chkTV);
        amenitiesPanel.add(chkInternet);
        amenitiesPanel.add(chkFridge);
        amenitiesPanel.add(chkKitchen);
        amenitiesPanel.add(chkJacuzzi);
        amenitiesPanel.add(chkBalcony);

        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        filterGrid.add(amenitiesPanel, gbc);
        gbc.gridwidth = 1;

        // Search Button
        JPanel filterActions = new JPanel(new GridLayout(1, 2, 8, 0));
        filterActions.setBackground(Theme.BG_SECONDARY);
        JButton btnReset = new JButton("Reset");
        Theme.styleButton(btnReset, Theme.BG_TERTIARY, Theme.TEXT_PRIMARY);
        btnReset.addActionListener(e -> resetFilters());

        JButton btnSearch = new JButton("Search Rooms");
        Theme.styleButton(btnSearch, Theme.ACCENT, Theme.TEXT_PRIMARY);
        btnSearch.addActionListener(e -> {
            selectedRoomNumbers.clear();
            searchRooms();
        });
        
        filterActions.add(btnReset);
        filterActions.add(btnSearch);
        gbc.gridx = 2; gbc.gridy = 7;
        filterGrid.add(filterActions, gbc);

        headerPanel.add(filterGrid, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. ROOMS GRID DISPLAY CONTAINER ---
        listContainer = new JPanel();
        listContainer.setLayout(new GridBagLayout());
        listContainer.setBackground(Theme.BG_PRIMARY);
        listContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        scrollPane = new JScrollPane(listContainer);
        scrollPane.setBackground(Theme.BG_PRIMARY);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. BOTTOM FOOTER FOR MULTI-BOOKING ---
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Theme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JPanel summaryPanel = new JPanel(new GridLayout(2, 1, 5, 2));
        summaryPanel.setBackground(Theme.BG_SECONDARY);
        
        lblSelectedCount = new JLabel("Selected Rooms: None");
        lblSelectedCount.setFont(Theme.FONT_BODY_BOLD);
        lblSelectedCount.setForeground(Theme.TEXT_PRIMARY);
        
        lblEstimatedTotal = new JLabel("Estimated Total: $0.00");
        lblEstimatedTotal.setFont(Theme.FONT_SUBTITLE);
        lblEstimatedTotal.setForeground(Theme.SUCCESS);

        summaryPanel.add(lblSelectedCount);
        summaryPanel.add(lblEstimatedTotal);
        footer.add(summaryPanel, BorderLayout.WEST);

        btnBookSelected = new JButton("Confirm Booking (0 Rooms)");
        Theme.styleButton(btnBookSelected, Theme.SUCCESS, Theme.TEXT_PRIMARY);
        btnBookSelected.setEnabled(false);
        btnBookSelected.addActionListener(e -> executeBooking());
        footer.add(btnBookSelected, BorderLayout.EAST);

        add(footer, BorderLayout.SOUTH);
    }

    private void resetFilters() {
        txtCheckIn.setText(LocalDate.now().toString());
        txtCheckOut.setText(LocalDate.now().plusDays(2).toString());
        cbRoomType.setSelectedIndex(0);
        if (cbMinRating != null) cbMinRating.setSelectedIndex(0);
        txtMinPrice.setText("");
        txtMaxPrice.setText("");
        spinGuests.setValue(1);
        chkTV.setSelected(false);
        chkInternet.setSelected(false);
        chkFridge.setSelected(false);
        chkKitchen.setSelected(false);
        chkJacuzzi.setSelected(false);
        chkBalcony.setSelected(false);
        selectedRoomNumbers.clear();
        searchRooms();
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BODY_BOLD);
        label.setForeground(Theme.TEXT_SECONDARY);
        return label;
    }

    private JCheckBox createCheckBox(String text) {
        JCheckBox box = new JCheckBox(text);
        box.setFont(Theme.FONT_BODY);
        box.setForeground(Theme.TEXT_PRIMARY);
        box.setBackground(Theme.BG_SECONDARY);
        box.setFocusPainted(false);
        return box;
    }

    public void searchRooms() {
        listContainer.removeAll();

        // Update Subtitle with Hotel Overall Average Rating
        if (lblSubtitle != null) {
            double overallAvg = service.getOverallAverageRating();
            int totalReviews = service.getTotalReviewsCount();
            if (totalReviews > 0) {
                lblSubtitle.setText("Compare available rooms, amenities, and stay prices.  •  Overall Guest Rating: ★ " +
                        String.format("%.1f", overallAvg) + " / 5.0 (" + totalReviews + " review" + (totalReviews == 1 ? "" : "s") + ")");
            } else {
                lblSubtitle.setText("Compare available rooms, amenities, and total stay prices.");
            }
        }

        // 1. Read input values
        LocalDate checkIn = null;
        LocalDate checkOut = null;
        try {
            checkIn = LocalDate.parse(txtCheckIn.getText().trim());
            checkOut = LocalDate.parse(txtCheckOut.getText().trim());
        } catch (DateTimeParseException e) {
            lblEstimatedTotal.setText("Error: Invalid Date Format");
            lblEstimatedTotal.setForeground(Theme.DANGER);
            JOptionPane.showMessageDialog(this, "Please enter dates in YYYY-MM-DD format.", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!checkIn.isAfter(LocalDate.now().minusDays(1))) {
            lblEstimatedTotal.setText("Error: Check-in cannot be in the past");
            lblEstimatedTotal.setForeground(Theme.DANGER);
            JOptionPane.showMessageDialog(this, "Check-in date cannot be in the past.", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!checkOut.isAfter(checkIn)) {
            lblEstimatedTotal.setText("Error: Check-out must be after Check-in");
            lblEstimatedTotal.setForeground(Theme.DANGER);
            JOptionPane.showMessageDialog(this, "Check-out date must be after Check-in date.", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RoomType type = null;
        String typeSel = (String) cbRoomType.getSelectedItem();
        if (typeSel != null && !typeSel.equals("All Types")) {
            type = RoomType.fromString(typeSel);
        }

        Double minRating = null;
        if (cbMinRating != null && cbMinRating.getSelectedIndex() > 0) {
            switch (cbMinRating.getSelectedIndex()) {
                case 1: minRating = 4.0; break;
                case 2: minRating = 3.0; break;
                case 3: minRating = 2.0; break;
                case 4: minRating = 1.0; break;
            }
        }

        BigDecimal minPrice = null;
        if (!txtMinPrice.getText().trim().isEmpty()) {
            try {
                minPrice = new BigDecimal(txtMinPrice.getText().trim());
            } catch (NumberFormatException ignored) {}
        }

        BigDecimal maxPrice = null;
        if (!txtMaxPrice.getText().trim().isEmpty()) {
            try {
                maxPrice = new BigDecimal(txtMaxPrice.getText().trim());
            } catch (NumberFormatException ignored) {}
        }

        List<String> amenities = new ArrayList<>();
        if (chkTV.isSelected()) amenities.add("TV");
        if (chkInternet.isSelected()) amenities.add("Internet");
        if (chkFridge.isSelected()) amenities.add("Refrigerator");
        if (chkKitchen.isSelected()) amenities.add("Kitchen");
        if (chkJacuzzi.isSelected()) amenities.add("Jacuzzi");
        if (chkBalcony.isSelected()) amenities.add("Balcony");

        int guestCount = (int) spinGuests.getValue();

        // Fetch matched available rooms
        List<Room> matchedRooms = service.getAvailableRooms(checkIn, checkOut, type, minPrice, maxPrice, amenities, guestCount, minRating);

        // 2. Build Grid layout of rooms
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int cols = 1;
        int row = 0;
        int col = 0;

        if (matchedRooms.isEmpty()) {
            JLabel lblNoRooms = new JLabel("No rooms match your filter criteria or dates.", JLabel.CENTER);
            lblNoRooms.setFont(Theme.FONT_SUBTITLE);
            lblNoRooms.setForeground(Theme.TEXT_SECONDARY);
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.gridwidth = 1;
            listContainer.add(lblNoRooms, gbc);
        } else {
            for (Room room : matchedRooms) {
                gbc.gridx = col;
                gbc.gridy = row;
                gbc.gridwidth = 1;

                JPanel card = createRoomCard(room, checkIn, checkOut);
                listContainer.add(card, gbc);

                col++;
                if (col >= cols) {
                    col = 0;
                    row++;
                }
            }
        }

        updateFooterCalculation(checkIn, checkOut);
        
        listContainer.revalidate();
        listContainer.repaint();
    }

    private JPanel createRoomCard(Room room, LocalDate checkIn, LocalDate checkOut) {
        Theme.RoundedPanel card = new Theme.RoundedPanel(16, Theme.BG_SECONDARY);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Image Label (West)
        JLabel lblImage = new JLabel();
        lblImage.setPreferredSize(new Dimension(130, 100));
        ImageLoader.loadRoomImage(lblImage, room.getType());
        card.add(lblImage, BorderLayout.WEST);

        // Details Panel (Center)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Theme.BG_SECONDARY);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        // Header: Room Number & Type badge
        JPanel headerLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerLine.setBackground(Theme.BG_SECONDARY);
        headerLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblRoomNum = new JLabel("Room " + room.getRoomNumber() + "  ");
        lblRoomNum.setFont(Theme.FONT_SUBTITLE);
        lblRoomNum.setForeground(Theme.TEXT_PRIMARY);
        
        JLabel lblBadge = new JLabel("  " + room.getType().getDisplayName() + "  ");
        lblBadge.setFont(Theme.FONT_CAPTION);
        lblBadge.setForeground(Color.WHITE);
        lblBadge.setBackground(Theme.ACCENT);
        lblBadge.setOpaque(true);
        lblBadge.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        headerLine.add(lblRoomNum);
        headerLine.add(lblBadge);
        detailsPanel.add(headerLine);
        detailsPanel.add(Box.createVerticalStrut(5));

        // Average Rating Display
        double avgRating = service.getRoomAverageRating(room.getRoomNumber());
        List<hotel.model.Review> reviews = service.getRoomReviews(room.getRoomNumber());
        JLabel lblRating = new JLabel();
        lblRating.setFont(Theme.FONT_BODY_BOLD);
        lblRating.setForeground(Theme.WARNING);
        lblRating.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (reviews.isEmpty()) {
            lblRating.setText("★ New Room (No ratings yet)");
        } else {
            lblRating.setText(String.format("★ %.1f / 5.0 (%d review%s)", avgRating, reviews.size(), reviews.size() == 1 ? "" : "s"));
        }
        detailsPanel.add(lblRating);
        detailsPanel.add(Box.createVerticalStrut(5));

        // Capacity
        JLabel lblCap = new JLabel("Capacity: " + room.getCapacity() + " guests");
        lblCap.setFont(Theme.FONT_BODY);
        lblCap.setForeground(Theme.TEXT_SECONDARY);
        lblCap.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(lblCap);
        detailsPanel.add(Box.createVerticalStrut(8));

        // Amenities chips
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        chips.setBackground(Theme.BG_SECONDARY);
        chips.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String amenity : room.getAmenities()) {
            JLabel chip = new JLabel(amenity);
            chip.setFont(Theme.FONT_CAPTION);
            chip.setForeground(Theme.TEXT_SECONDARY);
            chip.setBackground(Theme.BG_TERTIARY);
            chip.setOpaque(true);
            chip.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            chips.add(chip);
        }
        detailsPanel.add(chips);

        card.add(detailsPanel, BorderLayout.CENTER);

        // Booking Action Panel (East)
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(Theme.BG_SECONDARY);
        GridBagConstraints aGbc = new GridBagConstraints();
        aGbc.gridx = 0;
        aGbc.fill = GridBagConstraints.HORIZONTAL;

        // Display individual cost for the period
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal totalCost = service.calculateTotalCost(room, checkIn, checkOut);
        
        JLabel lblPriceNight = new JLabel("$" + room.getPricePerNight().toPlainString() + " / night", JLabel.RIGHT);
        lblPriceNight.setFont(Theme.FONT_BODY);
        lblPriceNight.setForeground(Theme.TEXT_SECONDARY);
        aGbc.gridy = 0;
        actionPanel.add(lblPriceNight, aGbc);

        JLabel lblTotalPrice = new JLabel("$" + totalCost.toPlainString(), JLabel.RIGHT);
        lblTotalPrice.setFont(Theme.FONT_TITLE);
        lblTotalPrice.setForeground(Theme.SUCCESS);
        aGbc.gridy = 1;
        aGbc.insets = new Insets(2, 0, 10, 0);
        actionPanel.add(lblTotalPrice, aGbc);

        // Add Selection Checkbox for Multi-Booking
        JCheckBox selectBox = new JCheckBox("Select Room");
        selectBox.setFont(Theme.FONT_BODY_BOLD);
        selectBox.setForeground(Theme.ACCENT);
        selectBox.setBackground(Theme.BG_SECONDARY);
        selectBox.setFocusPainted(false);
        selectBox.setSelected(selectedRoomNumbers.contains(room.getRoomNumber()));
        
        selectBox.addActionListener(e -> {
            if (selectBox.isSelected()) {
                selectedRoomNumbers.add(room.getRoomNumber());
            } else {
                selectedRoomNumbers.remove(room.getRoomNumber());
            }
            updateFooterCalculation(checkIn, checkOut);
        });

        aGbc.gridy = 2;
        aGbc.insets = new Insets(0, 0, 0, 0);
        actionPanel.add(selectBox, aGbc);

        card.add(actionPanel, BorderLayout.EAST);

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

    private void updateFooterCalculation(LocalDate checkIn, LocalDate checkOut) {
        if (selectedRoomNumbers.isEmpty()) {
            lblSelectedCount.setText("Selected Rooms: None");
            lblEstimatedTotal.setText("Estimated Total: $0.00");
            lblEstimatedTotal.setForeground(Theme.SUCCESS);
            btnBookSelected.setText("Confirm Booking (0 Rooms)");
            btnBookSelected.setEnabled(false);
            return;
        }

        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            lblSelectedCount.setText("Selected Rooms: " + new ArrayList<>(selectedRoomNumbers));
            lblEstimatedTotal.setText("Error: Invalid Stay Dates");
            lblEstimatedTotal.setForeground(Theme.DANGER);
            btnBookSelected.setText("Confirm Booking (" + selectedRoomNumbers.size() + " Rooms)");
            btnBookSelected.setEnabled(false);
            return;
        }

        BigDecimal total = BigDecimal.ZERO;
        List<String> list = new ArrayList<>(selectedRoomNumbers);
        for (String roomNum : list) {
            Room room = service.getAllRooms().stream()
                    .filter(r -> r.getRoomNumber().equals(roomNum))
                    .findFirst().orElse(null);
            if (room != null) {
                total = total.add(service.calculateTotalCost(room, checkIn, checkOut));
            }
        }

        lblSelectedCount.setText("Selected Rooms: " + list);
        
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double discountRate = service.calculateDiscountRate(checkIn, checkOut);
        
        StringBuilder detailsStr = new StringBuilder();
        detailsStr.append("Estimated Total: $").append(total.toPlainString());
        if (discountRate > 0) {
            detailsStr.append(" (Includes ").append((int)(discountRate * 100)).append("% Discount!)");
        }
        
        lblEstimatedTotal.setText(detailsStr.toString());
        lblEstimatedTotal.setForeground(Theme.SUCCESS);
        btnBookSelected.setText("Confirm Booking (" + selectedRoomNumbers.size() + " Rooms)");
        btnBookSelected.setEnabled(true);
    }

    private void executeBooking() {
        if (selectedRoomNumbers.isEmpty()) return;

        LocalDate checkIn = null;
        LocalDate checkOut = null;
        try {
            checkIn = LocalDate.parse(txtCheckIn.getText().trim());
            checkOut = LocalDate.parse(txtCheckOut.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid dates (YYYY-MM-DD).", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int guestCount = (int) spinGuests.getValue();
        List<String> roomNums = new ArrayList<>(selectedRoomNumbers);
        String username = mainFrame.getCurrentUser().getUsername();

        // Dialog Confirmation
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to book room(s) " + roomNums + " for " +
                        ChronoUnit.DAYS.between(checkIn, checkOut) + " nights?",
                "Confirm Reservation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                List<Reservation> created = service.makeReservations(username, roomNums, checkIn, checkOut, guestCount);
                
                // Successful Booking
                StringBuilder successMsg = new StringBuilder("🎉 Reservation successful! Invoices generated:\n");
                for (Reservation r : created) {
                    successMsg.append("- ID: ").append(r.getReservationId())
                            .append(" (Room ").append(r.getRoomNumber()).append(")\n");
                }
                
                JOptionPane.showMessageDialog(this, successMsg.toString(), "Reservation Completed", JOptionPane.INFORMATION_MESSAGE);

                // Update UI elements
                dashboardPanel.refreshUserProfile();
                selectedRoomNumbers.clear();
                searchRooms(); // Refresh rooms list to reflect booking
                
                // Show booking list
                dashboardPanel.getBookingsPanel().refreshData();
                dashboardPanel.switchContent("BOOKINGS");

            } catch (HotelException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
