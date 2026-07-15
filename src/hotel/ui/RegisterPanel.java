package hotel.ui;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblError;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(Theme.BG_PRIMARY);

        initComponent();
    }

    private void initComponent() {
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Register Card Panel
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Theme.BG_SECONDARY);
        card.setBorder(BorderFactory.createCompoundBorder(
                Theme.PANEL_BORDER,
                BorderFactory.createEmptyBorder(30, 45, 30, 45)
        ));
        card.setPreferredSize(new Dimension(480, 560));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.insets = new Insets(6, 0, 6, 0);
        cardGbc.gridx = 0;

        // Logo & Title
        JLabel lblTitle = new JLabel("Create Account", JLabel.CENTER);
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.WARNING);
        cardGbc.gridy = 0;
        card.add(lblTitle, cardGbc);

        JLabel lblSubtitle = new JLabel("Join Grand Luxe Hotel & Residence", JLabel.CENTER);
        lblSubtitle.setFont(Theme.FONT_CAPTION);
        lblSubtitle.setForeground(Theme.TEXT_SECONDARY);
        cardGbc.gridy = 1;
        cardGbc.insets = new Insets(0, 0, 20, 0);
        card.add(lblSubtitle, cardGbc);

        // Fields
        cardGbc.insets = new Insets(4, 0, 4, 0);

        // Grid split for First Name & Last Name
        JPanel namePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        namePanel.setBackground(Theme.BG_SECONDARY);
        
        JPanel fnWrapper = new JPanel(new BorderLayout(0, 4));
        fnWrapper.setBackground(Theme.BG_SECONDARY);
        JLabel lblFn = new JLabel("First Name");
        lblFn.setFont(Theme.FONT_BODY_BOLD);
        lblFn.setForeground(Theme.TEXT_SECONDARY);
        txtFirstName = new JTextField();
        Theme.styleTextField(txtFirstName);
        fnWrapper.add(lblFn, BorderLayout.NORTH);
        fnWrapper.add(txtFirstName, BorderLayout.CENTER);
        
        JPanel lnWrapper = new JPanel(new BorderLayout(0, 4));
        lnWrapper.setBackground(Theme.BG_SECONDARY);
        JLabel lblLn = new JLabel("Last Name");
        lblLn.setFont(Theme.FONT_BODY_BOLD);
        lblLn.setForeground(Theme.TEXT_SECONDARY);
        txtLastName = new JTextField();
        Theme.styleTextField(txtLastName);
        lnWrapper.add(lblLn, BorderLayout.NORTH);
        lnWrapper.add(txtLastName, BorderLayout.CENTER);
        
        namePanel.add(fnWrapper);
        namePanel.add(lnWrapper);
        
        cardGbc.gridy = 2;
        card.add(namePanel, cardGbc);

        // Username
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(Theme.FONT_BODY_BOLD);
        lblUsername.setForeground(Theme.TEXT_SECONDARY);
        cardGbc.gridy = 3;
        card.add(lblUsername, cardGbc);

        txtUsername = new JTextField();
        Theme.styleTextField(txtUsername);
        cardGbc.gridy = 4;
        card.add(txtUsername, cardGbc);

        // Password
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(Theme.FONT_BODY_BOLD);
        lblPassword.setForeground(Theme.TEXT_SECONDARY);
        cardGbc.gridy = 5;
        card.add(lblPassword, cardGbc);

        txtPassword = new JPasswordField();
        Theme.styleTextField(txtPassword);
        cardGbc.gridy = 6;
        card.add(txtPassword, cardGbc);

        // Error message label
        lblError = new JLabel(" ", JLabel.CENTER);
        lblError.setFont(Theme.FONT_CAPTION);
        lblError.setForeground(Theme.DANGER);
        cardGbc.gridy = 7;
        cardGbc.insets = new Insets(5, 0, 5, 0);
        card.add(lblError, cardGbc);

        // Register Button
        JButton btnRegister = new JButton("Register");
        Theme.styleButton(btnRegister, Theme.ACCENT, Theme.TEXT_PRIMARY);
        cardGbc.gridy = 8;
        cardGbc.insets = new Insets(10, 0, 10, 0);
        card.add(btnRegister, cardGbc);

        // Back to Login Link
        JButton btnBack = new JButton("Already have an account? Sign In");
        btnBack.setFont(Theme.FONT_CAPTION);
        btnBack.setForeground(Theme.ACCENT);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardGbc.gridy = 9;
        cardGbc.insets = new Insets(5, 0, 0, 0);
        card.add(btnBack, cardGbc);

        // Actions
        btnRegister.addActionListener(e -> performRegister());
        btnBack.addActionListener(e -> mainFrame.showLogin());

        // Center card in panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(card, gbc);
    }

    private void performRegister() {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please fill out all fields.");
            return;
        }

        if (username.length() < 3 || password.length() < 4) {
            lblError.setText("Username >= 3 & Password >= 4 chars.");
            return;
        }

        boolean success = mainFrame.getService().registerUser(username, password, firstName, lastName);
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showLogin();
        } else {
            lblError.setText("Username is already taken.");
        }
    }

    public void clearFields() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        lblError.setText(" ");
    }
}
