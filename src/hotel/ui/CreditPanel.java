package hotel.ui;

import hotel.service.HotelService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class CreditPanel extends JPanel {
    private final MainFrame mainFrame;
    private final DashboardPanel dashboardPanel;
    private final HotelService service;

    private JLabel lblBalance;
    private JTextField txtAmount;
    private JTextField txtCardNumber;
    private JTextField txtExpiry;
    private JTextField txtCVV;
    private JTextField txtCardName;
    private JLabel lblError;

    public CreditPanel(MainFrame mainFrame, DashboardPanel dashboardPanel) {
        this.mainFrame = mainFrame;
        this.dashboardPanel = dashboardPanel;
        this.service = mainFrame.getService();

        setLayout(new GridBagLayout());
        setBackground(Theme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponent();
        refreshData();
    }

    private void initComponent() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Left Panel: Credit Display Card
        JPanel displayCard = new JPanel(new GridBagLayout());
        displayCard.setBackground(Theme.BG_SECONDARY);
        displayCard.setBorder(BorderFactory.createCompoundBorder(
                Theme.PANEL_BORDER,
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        displayCard.setPreferredSize(new Dimension(350, 400));

        GridBagConstraints dGbc = new GridBagConstraints();
        dGbc.gridx = 0;
        dGbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel lblDisplayTitle = new JLabel("Your Account Credit", JLabel.CENTER);
        lblDisplayTitle.setFont(Theme.FONT_SUBTITLE);
        lblDisplayTitle.setForeground(Theme.TEXT_SECONDARY);
        dGbc.gridy = 0;
        dGbc.insets = new Insets(0, 0, 15, 0);
        displayCard.add(lblDisplayTitle, dGbc);

        JLabel lblWallet = new JLabel("$", JLabel.CENTER);
        lblWallet.setFont(new Font("SansSerif", Font.BOLD, 62));
        lblWallet.setForeground(Theme.WARNING);
        dGbc.gridy = 1;
        displayCard.add(lblWallet, dGbc);

        lblBalance = new JLabel("$0.00", JLabel.CENTER);
        lblBalance.setFont(new Font("SansSerif", Font.BOLD, 36));
        lblBalance.setForeground(Theme.SUCCESS);
        dGbc.gridy = 2;
        dGbc.insets = new Insets(15, 0, 15, 0);
        displayCard.add(lblBalance, dGbc);

        JLabel lblMeta = new JLabel("Use your balance to confirm bookings instantly.", JLabel.CENTER);
        lblMeta.setFont(Theme.FONT_CAPTION);
        lblMeta.setForeground(Theme.TEXT_MUTED);
        dGbc.gridy = 3;
        displayCard.add(lblMeta, dGbc);

        gbc.gridx = 0; gbc.gridy = 0;
        add(displayCard, gbc);

        // Right Panel: Simulated Payment Form Card
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Theme.BG_SECONDARY);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                Theme.PANEL_BORDER,
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        formCard.setPreferredSize(new Dimension(480, 400));

        GridBagConstraints fGbc = new GridBagConstraints();
        fGbc.fill = GridBagConstraints.HORIZONTAL;
        fGbc.insets = new Insets(6, 0, 6, 0);
        fGbc.weightx = 1.0;
        fGbc.gridx = 0;

        JLabel lblFormTitle = new JLabel("Add Account Credit");
        lblFormTitle.setFont(Theme.FONT_SUBTITLE);
        lblFormTitle.setForeground(Theme.WARNING);
        fGbc.gridy = 0;
        fGbc.insets = new Insets(0, 0, 15, 0);
        formCard.add(lblFormTitle, fGbc);
        
        fGbc.insets = new Insets(4, 0, 4, 0);

        // Amount to add
        JLabel lblAmount = new JLabel("Amount to Add ($)");
        lblAmount.setFont(Theme.FONT_BODY_BOLD);
        lblAmount.setForeground(Theme.TEXT_SECONDARY);
        fGbc.gridy = 1;
        formCard.add(lblAmount, fGbc);

        txtAmount = new JTextField();
        Theme.styleTextField(txtAmount);
        fGbc.gridy = 2;
        formCard.add(txtAmount, fGbc);

        // Cardholder Name
        JLabel lblCardName = new JLabel("Cardholder Name");
        lblCardName.setFont(Theme.FONT_BODY_BOLD);
        lblCardName.setForeground(Theme.TEXT_SECONDARY);
        fGbc.gridy = 3;
        formCard.add(lblCardName, fGbc);

        txtCardName = new JTextField();
        Theme.styleTextField(txtCardName);
        fGbc.gridy = 4;
        formCard.add(txtCardName, fGbc);

        // Card Number
        JLabel lblCardNumber = new JLabel("Card Number");
        lblCardNumber.setFont(Theme.FONT_BODY_BOLD);
        lblCardNumber.setForeground(Theme.TEXT_SECONDARY);
        fGbc.gridy = 5;
        formCard.add(lblCardNumber, fGbc);

        txtCardNumber = new JTextField("1234-5678-9012-3456");
        Theme.styleTextField(txtCardNumber);
        fGbc.gridy = 6;
        formCard.add(txtCardNumber, fGbc);

        // Split Row for Expiry & CVV
        JPanel cardInfoRow = new JPanel(new GridLayout(1, 2, 10, 0));
        cardInfoRow.setBackground(Theme.BG_SECONDARY);

        JPanel expWrap = new JPanel(new BorderLayout(0, 4));
        expWrap.setBackground(Theme.BG_SECONDARY);
        JLabel lblExp = new JLabel("Expiry Date (MM/YY)");
        lblExp.setFont(Theme.FONT_BODY_BOLD);
        lblExp.setForeground(Theme.TEXT_SECONDARY);
        txtExpiry = new JTextField("12/29");
        Theme.styleTextField(txtExpiry);
        expWrap.add(lblExp, BorderLayout.NORTH);
        expWrap.add(txtExpiry, BorderLayout.CENTER);

        JPanel cvvWrap = new JPanel(new BorderLayout(0, 4));
        cvvWrap.setBackground(Theme.BG_SECONDARY);
        JLabel lblCvv = new JLabel("CVV");
        lblCvv.setFont(Theme.FONT_BODY_BOLD);
        lblCvv.setForeground(Theme.TEXT_SECONDARY);
        txtCVV = new JTextField("321");
        Theme.styleTextField(txtCVV);
        cvvWrap.add(lblCvv, BorderLayout.NORTH);
        cvvWrap.add(txtCVV, BorderLayout.CENTER);

        cardInfoRow.add(expWrap);
        cardInfoRow.add(cvvWrap);

        fGbc.gridy = 7;
        formCard.add(cardInfoRow, fGbc);

        // Error log label
        lblError = new JLabel(" ", JLabel.CENTER);
        lblError.setFont(Theme.FONT_CAPTION);
        lblError.setForeground(Theme.DANGER);
        fGbc.gridy = 8;
        fGbc.insets = new Insets(5, 0, 5, 0);
        formCard.add(lblError, fGbc);

        // Process button
        JButton btnPay = new JButton("Add Credit");
        Theme.styleButton(btnPay, Theme.ACCENT, Theme.TEXT_PRIMARY);
        fGbc.gridy = 9;
        fGbc.insets = new Insets(10, 0, 0, 0);
        formCard.add(btnPay, fGbc);

        gbc.gridx = 1; gbc.gridy = 0;
        add(formCard, gbc);

        // Submit action
        btnPay.addActionListener(e -> processRecharge());
    }

    public void refreshData() {
        if (mainFrame.getCurrentUser() != null) {
            lblBalance.setText("$" + mainFrame.getCurrentUser().getCredit().toPlainString());
        }
    }

    private void processRecharge() {
        String amountStr = txtAmount.getText().trim();
        String name = txtCardName.getText().trim();
        String number = txtCardNumber.getText().trim();
        String expiry = txtExpiry.getText().trim();
        String cvv = txtCVV.getText().trim();

        if (amountStr.isEmpty() || name.isEmpty() || number.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
            lblError.setText("Please fill out all payment details.");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
            if (amount.signum() <= 0) {
                lblError.setText("Amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            lblError.setText("Invalid amount format. Use digits (e.g. 150.0).");
            return;
        }

        String username = mainFrame.getCurrentUser().getUsername();
        service.addCredit(username, amount);
        
        lblError.setText(" ");
        txtAmount.setText("");
        
        JOptionPane.showMessageDialog(this,
                String.format("Transaction Successful!\n\n$%s has been successfully charged to card ending in %s.",
                        amount.toPlainString(), number.substring(Math.max(0, number.length() - 4))),
                "Payment Completed", JOptionPane.INFORMATION_MESSAGE);

        dashboardPanel.refreshUserProfile();
        refreshData();
    }
}
