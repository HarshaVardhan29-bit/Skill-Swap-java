package com.skillswap.view;

import com.skillswap.dao.UserDAO;
import com.skillswap.model.User;
import com.skillswap.util.SwingUtils;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("SkillSwap - Login");
        setSize(450, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        SwingUtils.centerFrame(this);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(SwingUtils.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;

        JLabel title = SwingUtils.createLabel("SkillSwap", SwingUtils.TITLE_FONT, SwingUtils.PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        mainPanel.add(title, gbc);

        JLabel subtitle = SwingUtils.createLabel("Peer-to-Peer Student Skill Exchange", SwingUtils.BODY_FONT, SwingUtils.TEXT_SECONDARY);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        mainPanel.add(subtitle, gbc);

        gbc.gridy = 2;
        mainPanel.add(SwingUtils.createLabel("Email Address:", SwingUtils.BOLD_FONT, SwingUtils.TEXT_PRIMARY), gbc);

        emailField = SwingUtils.createStyledTextField(20);
        emailField.setText("alice@uni.edu");
        gbc.gridy = 3;
        mainPanel.add(emailField, gbc);

        gbc.gridy = 4;
        mainPanel.add(SwingUtils.createLabel("Password:", SwingUtils.BOLD_FONT, SwingUtils.TEXT_PRIMARY), gbc);

        passwordField = SwingUtils.createStyledPasswordField(20);
        passwordField.setText("pass123");
        gbc.gridy = 5;
        mainPanel.add(passwordField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setOpaque(false);
        JButton loginBtn = SwingUtils.createStyledButton("Login", SwingUtils.PRIMARY_DARK);
        JButton registerBtn = SwingUtils.createStyledButton("Register", SwingUtils.CARD);

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> {
            dispose();
            new RegisterFrame().setVisible(true);
        });

        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        gbc.gridy = 6;
        mainPanel.add(btnPanel, gbc);

        add(mainPanel);
        getRootPane().setDefaultButton(loginBtn);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            SwingUtils.showError(this, "Please enter both email and password.");
            return;
        }

        User user = userDAO.login(email, password);
        if (user != null) {
            dispose();
            new DashboardFrame(user).setVisible(true);
        } else {
            SwingUtils.showError(this, "Invalid email or password.");
        }
    }
}
