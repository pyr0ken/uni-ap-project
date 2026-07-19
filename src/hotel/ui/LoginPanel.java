package hotel.ui;

import hotel.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblError;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(Theme.BG_PRIMARY);

        initComponent();
    }

    private void initComponent() {
        GridBagConstraints gbc = new GridBagConstraints();
        
        Theme.RoundedPanel card = new Theme.RoundedPanel(12, Theme.BG_SECONDARY);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(38, 48, 38, 48));
        card.setPreferredSize(new Dimension(440, 500));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.insets = new Insets(8, 0, 8, 0);
        cardGbc.gridx = 0;

        JLabel lblLogoIcon = new JLabel("WELCOME TO", JLabel.CENTER);
        lblLogoIcon.setFont(Theme.FONT_CAPTION);
        lblLogoIcon.setForeground(Theme.WARNING);
        cardGbc.gridy = 0;
        card.add(lblLogoIcon, cardGbc);

        JLabel lblTitle = new JLabel("GRAND LUXE", JLabel.CENTER);
        lblTitle.setFont(Theme.FONT_TITLE_LARGE);
        lblTitle.setForeground(Theme.ACCENT);
        cardGbc.gridy = 1;
        card.add(lblTitle, cardGbc);

        JLabel lblSubtitle = new JLabel("Sign in to plan your next stay", JLabel.CENTER);
        lblSubtitle.setFont(Theme.FONT_BODY);
        lblSubtitle.setForeground(Theme.TEXT_SECONDARY);
        cardGbc.gridy = 2;
        cardGbc.insets = new Insets(0, 0, 25, 0);
        card.add(lblSubtitle, cardGbc);

        // Fields
        cardGbc.insets = new Insets(6, 0, 6, 0);
        
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(Theme.FONT_BODY_BOLD);
        lblUsername.setForeground(Theme.TEXT_SECONDARY);
        cardGbc.gridy = 3;
        card.add(lblUsername, cardGbc);

        txtUsername = new JTextField();
        Theme.styleTextField(txtUsername);
        cardGbc.gridy = 4;
        card.add(txtUsername, cardGbc);

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

        // Login Button
        JButton btnLogin = new JButton("Sign In");
        Theme.styleButton(btnLogin, Theme.ACCENT, Theme.TEXT_PRIMARY);
        cardGbc.gridy = 8;
        cardGbc.insets = new Insets(15, 0, 10, 0);
        card.add(btnLogin, cardGbc);

        // Register Link Button
        JButton btnRegisterLink = new JButton("New to Grand Luxe? Create an account");
        btnRegisterLink.setFont(Theme.FONT_CAPTION);
        btnRegisterLink.setForeground(Theme.ACCENT);
        btnRegisterLink.setContentAreaFilled(false);
        btnRegisterLink.setBorderPainted(false);
        btnRegisterLink.setFocusPainted(false);
        btnRegisterLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardGbc.gridy = 9;
        cardGbc.insets = new Insets(5, 0, 0, 0);
        card.add(btnRegisterLink, cardGbc);

        // Actions
        btnLogin.addActionListener(e -> performLogin());
        txtPassword.addActionListener(e -> performLogin());
        btnRegisterLink.addActionListener(e -> mainFrame.showRegister());

        // Center card in panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(card, gbc);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please fill out all fields.");
            return;
        }

        User user = mainFrame.getService().loginUser(username, password);
        if (user != null) {
            lblError.setText(" ");
            mainFrame.loginSuccessful(user);
        } else {
            lblError.setText("Invalid username or password.");
        }
    }

    public void clearFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        lblError.setText(" ");
    }
}
