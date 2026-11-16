package com.skillswap.view;

import com.skillswap.dao.UserDAO;
import com.skillswap.model.User;
import com.skillswap.util.SwingUtils;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class EditProfileDialog extends JDialog {
    private final User currentUser;
    private final Consumer<User> onSave;
    private JTextField nameField, studentIdField, departmentField, preferredTimeField;
    private JComboBox<String> semesterBox, learningModeBox;
    private JTextArea skillsArea, skillsToLearnArea, bioArea;
    private final UserDAO userDAO = new UserDAO();

    public EditProfileDialog(Frame owner, User currentUser, Consumer<User> onSave) {
        super(owner, "Edit Profile", true);
        this.currentUser = currentUser;
        this.onSave = onSave;

        setSize(540, 580);
        SwingUtils.centerFrame(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SwingUtils.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        nameField = SwingUtils.createStyledTextField(18);
        nameField.setText(currentUser.getName());
        studentIdField = SwingUtils.createStyledTextField(18);
        studentIdField.setText(currentUser.getStudentId());
        departmentField = SwingUtils.createStyledTextField(18);
        departmentField.setText(currentUser.getDepartment());
        preferredTimeField = SwingUtils.createStyledTextField(18);
        preferredTimeField.setText(currentUser.getPreferredTime());

        semesterBox = new JComboBox<>(new String[]{"1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th"});
        semesterBox.setSelectedItem(currentUser.getSemester());

        learningModeBox = new JComboBox<>(new String[]{"Online", "In-Person", "Hybrid"});
        learningModeBox.setSelectedItem(currentUser.getLearningMode());

        skillsArea = SwingUtils.createStyledTextArea(2, 18);
        skillsArea.setText(currentUser.getSkills());
        skillsToLearnArea = SwingUtils.createStyledTextArea(2, 18);
        skillsToLearnArea.setText(currentUser.getSkillsToLearn());
        bioArea = SwingUtils.createStyledTextArea(3, 18);
        bioArea.setText(currentUser.getBio());

        int r = 0;
        addRow(panel, gbc, r++, "Full Name *:", nameField);
        addRow(panel, gbc, r++, "Student ID:", studentIdField);
        addRow(panel, gbc, r++, "Department:", departmentField);
        addRow(panel, gbc, r++, "Semester:", semesterBox);
        addRow(panel, gbc, r++, "Learning Mode:", learningModeBox);
        addRow(panel, gbc, r++, "Preferred Time:", preferredTimeField);
        addRow(panel, gbc, r++, "My Skills:", new JScrollPane(skillsArea));
        addRow(panel, gbc, r++, "Skills to Learn:", new JScrollPane(skillsToLearnArea));
        addRow(panel, gbc, r++, "Bio:", new JScrollPane(bioArea));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setOpaque(false);
        JButton cancelBtn = SwingUtils.createStyledButton("Cancel", SwingUtils.CARD);
        JButton saveBtn = SwingUtils.createStyledButton("Save Changes", SwingUtils.PRIMARY_DARK);

        cancelBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> saveChanges());
        btns.add(cancelBtn);
        btns.add(saveBtn);

        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        panel.add(btns, gbc);

        add(new JScrollPane(panel));
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int r, String lbl, Component c) {
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        p.add(SwingUtils.createLabel(lbl, SwingUtils.BOLD_FONT, SwingUtils.TEXT_PRIMARY), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        p.add(c, gbc);
    }

    private void saveChanges() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            SwingUtils.showError(this, "Name cannot be empty.");
            return;
        }
        currentUser.setName(name);
        currentUser.setStudentId(studentIdField.getText().trim());
        currentUser.setDepartment(departmentField.getText().trim());
        currentUser.setSemester((String) semesterBox.getSelectedItem());
        currentUser.setLearningMode((String) learningModeBox.getSelectedItem());
        currentUser.setPreferredTime(preferredTimeField.getText().trim());
        currentUser.setSkills(skillsArea.getText().trim());
        currentUser.setSkillsToLearn(skillsToLearnArea.getText().trim());
        currentUser.setBio(bioArea.getText().trim());

        if (userDAO.updateProfile(currentUser)) {
            SwingUtils.showSuccess(this, "Profile updated!");
            if (onSave != null) onSave.accept(currentUser);
            dispose();
        } else {
            SwingUtils.showError(this, "Failed to update profile.");
        }
    }
}
