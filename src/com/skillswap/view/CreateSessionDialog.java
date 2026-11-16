package com.skillswap.view;

import com.skillswap.dao.SessionDAO;
import com.skillswap.model.Session;
import com.skillswap.model.User;
import com.skillswap.util.SwingUtils;
import javax.swing.*;
import java.awt.*;

public class CreateSessionDialog extends JDialog {
    private JTextField titleField, skillField, scheduledAtField;
    private JTextArea descriptionArea;
    private JRadioButton offeringRadio, requestingRadio;
    private final User currentUser;
    private final Session editingSession;
    private final Runnable onSuccess;
    private final SessionDAO sessionDAO = new SessionDAO();

    public CreateSessionDialog(Frame owner, User currentUser, Runnable onSuccess) {
        this(owner, currentUser, null, onSuccess);
    }

    public CreateSessionDialog(Frame owner, User currentUser, Session editingSession, Runnable onSuccess) {
        super(owner, editingSession == null ? "Create New Session" : "Edit Session", true);
        this.currentUser = currentUser;
        this.editingSession = editingSession;
        this.onSuccess = onSuccess;

        setSize(480, 440);
        SwingUtils.centerFrame(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SwingUtils.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        titleField = SwingUtils.createStyledTextField(20);
        skillField = SwingUtils.createStyledTextField(20);
        scheduledAtField = SwingUtils.createStyledTextField(20);
        scheduledAtField.setText("2026-11-25 17:00");
        descriptionArea = SwingUtils.createStyledTextArea(4, 20);

        offeringRadio = new JRadioButton("Offering Help (Teaching)", true);
        requestingRadio = new JRadioButton("Requesting Help (Learning)");
        offeringRadio.setBackground(SwingUtils.BACKGROUND);
        offeringRadio.setForeground(SwingUtils.TEXT_PRIMARY);
        requestingRadio.setBackground(SwingUtils.BACKGROUND);
        requestingRadio.setForeground(SwingUtils.TEXT_PRIMARY);

        ButtonGroup group = new ButtonGroup();
        group.add(offeringRadio);
        group.add(requestingRadio);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radioPanel.setOpaque(false);
        radioPanel.add(offeringRadio);
        radioPanel.add(requestingRadio);

        if (editingSession != null) {
            titleField.setText(editingSession.getTitle());
            skillField.setText(editingSession.getSkill());
            scheduledAtField.setText(editingSession.getScheduledAt());
            descriptionArea.setText(editingSession.getDescription());
            if ("requesting".equalsIgnoreCase(editingSession.getSessionType())) {
                requestingRadio.setSelected(true);
            }
        }

        int r = 0;
        addRow(panel, gbc, r++, "Session Type:", radioPanel);
        addRow(panel, gbc, r++, "Session Title *:", titleField);
        addRow(panel, gbc, r++, "Target Skill *:", skillField);
        addRow(panel, gbc, r++, "Scheduled Date/Time:", scheduledAtField);
        addRow(panel, gbc, r++, "Description:", new JScrollPane(descriptionArea));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setOpaque(false);
        JButton cancelBtn = SwingUtils.createStyledButton("Cancel", SwingUtils.CARD);
        JButton saveBtn = SwingUtils.createStyledButton(editingSession == null ? "Create Session" : "Update Session", SwingUtils.PRIMARY_DARK);

        cancelBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> saveSession());
        btns.add(cancelBtn);
        btns.add(saveBtn);

        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        panel.add(btns, gbc);
        add(panel);
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int r, String lbl, Component c) {
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        p.add(SwingUtils.createLabel(lbl, SwingUtils.BOLD_FONT, SwingUtils.TEXT_PRIMARY), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        p.add(c, gbc);
    }

    private void saveSession() {
        String title = titleField.getText().trim();
        String skill = skillField.getText().trim();
        if (title.isEmpty() || skill.isEmpty()) {
            SwingUtils.showError(this, "Title and Skill are required.");
            return;
        }

        Session s = editingSession != null ? editingSession : new Session();
        s.setTutorId(currentUser.getId());
        s.setTitle(title);
        s.setSkill(skill);
        s.setScheduledAt(scheduledAtField.getText().trim());
        s.setDescription(descriptionArea.getText().trim());
        s.setSessionType(offeringRadio.isSelected() ? "offering" : "requesting");
        s.setStatus("open");

        boolean ok = (editingSession == null) ? sessionDAO.createSession(s) : sessionDAO.updateSession(s);
        if (ok) {
            SwingUtils.showSuccess(this, "Session saved successfully!");
            if (onSuccess != null) onSuccess.run();
            dispose();
        } else {
            SwingUtils.showError(this, "Failed to save session.");
        }
    }
}
