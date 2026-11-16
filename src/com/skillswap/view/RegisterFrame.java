package com.skillswap.view;

import com.skillswap.dao.UserDAO;
import com.skillswap.model.User;
import com.skillswap.util.SwingUtils;
import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField nameField, emailField, studentIdField, departmentField, preferredTimeField;
    private JPasswordField passwordField;
    private JComboBox<String> semesterBox, learningModeBox;
    private JTextArea skillsArea, skillsToLearnArea, bioArea;
    private final UserDAO userDAO = new UserDAO();

    public RegisterFrame() {
        setTitle("SkillSwap - Student Registration");
        setSize(600, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SwingUtils.centerFrame(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(SwingUtils.BACKGROUND);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JLabel header = SwingUtils.createLabel("Create Student Account", SwingUtils.TITLE_FONT, SwingUtils.PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(header, gbc);
        gbc.gridwidth = 1;

        nameField = SwingUtils.createStyledTextField(18);
        emailField = SwingUtils.createStyledTextField(18);
        passwordField = SwingUtils.createStyledPasswordField(18);
        studentIdField = SwingUtils.createStyledTextField(18);
        departmentField = SwingUtils.createStyledTextField(18);
        preferredTimeField = SwingUtils.createStyledTextField(18);

        semesterBox = new JComboBox<>(new String[]{"1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th"});
        learningModeBox = new JComboBox<>(new String[]{"Online", "In-Person", "Hybrid"});

        skillsArea = SwingUtils.createStyledTextArea(2, 18);
        skillsToLearnArea = SwingUtils.createStyledTextArea(2, 18);
        bioArea = SwingUtils.createStyledTextArea(3, 18);

        int row = 1;
        addFormRow(formPanel, gbc, row++, "Full Name *:", nameField);
        addFormRow(formPanel, gbc, row++, "Email Address *:", emailField);
        addFormRow(formPanel, gbc, row++, "Password *:", passwordField);
        addFormRow(formPanel, gbc, row++, "Student ID:", studentIdField);
        addFormRow(formPanel, gbc, row++, "Department:", departmentField);
        addFormRow(formPanel, gbc, row++, "Semester:", semesterBox);
        addFormRow(formPanel, gbc, row++, "Learning Mode:", learningModeBox);
        addFormRow(formPanel, gbc, row++, "Preferred Time:", preferredTimeField);
        addFormRow(formPanel, gbc, row++, "My Skills (comma-sep):", new JScrollPane(skillsArea));
        addFormRow(formPanel, gbc, row++, "Skills to Learn:", new JScrollPane(skillsToLearnArea));
        addFormRow(formPanel, gbc, row++, "Short Bio:", new JScrollPane(bioArea));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setOpaque(false);
        JButton backBtn = SwingUtils.createStyledButton("Back to Login", SwingUtils.CARD);
        JButton submitBtn = SwingUtils.createStyledButton("Register", SwingUtils.PRIMARY_DARK);

        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        submitBtn.addActionListener(e -> handleRegister());

        btnPanel.add(backBtn);
        btnPanel.add(submitBtn);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        add(new JScrollPane(formPanel));
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(SwingUtils.createLabel(label, SwingUtils.BOLD_FONT, SwingUtils.TEXT_PRIMARY), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(comp, gbc);
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            SwingUtils.showError(this, "Name, Email, and Password are required.");
            return;
        }
        if (!email.contains("@")) {
            SwingUtils.showError(this, "Please enter a valid email address.");
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(pass);
        user.setStudentId(studentIdField.getText().trim());
        user.setDepartment(departmentField.getText().trim());
        user.setSemester((String) semesterBox.getSelectedItem());
        user.setLearningMode((String) learningModeBox.getSelectedItem());
        user.setPreferredTime(preferredTimeField.getText().trim());
        user.setSkills(skillsArea.getText().trim());
        user.setSkillsToLearn(skillsToLearnArea.getText().trim());
        user.setBio(bioArea.getText().trim());

        if (userDAO.register(user)) {
            SwingUtils.showSuccess(this, "Registration successful! Please log in.");
            dispose();
            new LoginFrame().setVisible(true);
        } else {
            SwingUtils.showError(this, "Registration failed. Email may already be registered.");
        }
    }
}
