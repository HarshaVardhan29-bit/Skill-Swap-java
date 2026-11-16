package com.skillswap.view;

import com.skillswap.dao.ExchangeRequestDAO;
import com.skillswap.model.ExchangeRequest;
import com.skillswap.model.User;
import com.skillswap.util.SwingUtils;
import javax.swing.*;
import java.awt.*;

public class SendRequestDialog extends JDialog {
    private JTextField offeringField, requestingField;
    private JTextArea messageArea;
    private final User currentUser;
    private final User targetUser;
    private final ExchangeRequestDAO requestDAO = new ExchangeRequestDAO();

    public SendRequestDialog(Frame owner, User currentUser, User targetUser) {
        super(owner, "Send Skill Exchange Request", true);
        this.currentUser = currentUser;
        this.targetUser = targetUser;

        setSize(460, 400);
        SwingUtils.centerFrame(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SwingUtils.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JLabel targetInfo = SwingUtils.createLabel(
                "To: " + targetUser.getName() + " (Knows: " + targetUser.getSkills() + ")",
                SwingUtils.BOLD_FONT, SwingUtils.PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(targetInfo, gbc);
        gbc.gridwidth = 1;

        offeringField = SwingUtils.createStyledTextField(20);
        offeringField.setText(currentUser.getSkills());
        requestingField = SwingUtils.createStyledTextField(20);
        requestingField.setText(targetUser.getSkills());
        messageArea = SwingUtils.createStyledTextArea(4, 20);
        messageArea.setText("Hi " + targetUser.getName() + ", would love to swap skills with you!");

        addRow(panel, gbc, 1, "Skill You Offer *:", offeringField);
        addRow(panel, gbc, 2, "Skill You Request *:", requestingField);
        addRow(panel, gbc, 3, "Personal Message:", new JScrollPane(messageArea));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setOpaque(false);
        JButton cancelBtn = SwingUtils.createStyledButton("Cancel", SwingUtils.CARD);
        JButton sendBtn = SwingUtils.createStyledButton("Send Request", SwingUtils.PRIMARY_DARK);

        cancelBtn.addActionListener(e -> dispose());
        sendBtn.addActionListener(e -> sendRequest());
        btns.add(cancelBtn);
        btns.add(sendBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(btns, gbc);
        add(panel);
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int r, String lbl, Component c) {
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.35;
        p.add(SwingUtils.createLabel(lbl, SwingUtils.BOLD_FONT, SwingUtils.TEXT_PRIMARY), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        p.add(c, gbc);
    }

    private void sendRequest() {
        String off = offeringField.getText().trim();
        String req = requestingField.getText().trim();
        if (off.isEmpty() || req.isEmpty()) {
            SwingUtils.showError(this, "Both offering and requesting skills are required.");
            return;
        }

        ExchangeRequest er = new ExchangeRequest();
        er.setFromUserId(currentUser.getId());
        er.setToUserId(targetUser.getId());
        er.setSkillOffering(off);
        er.setSkillRequesting(req);
        er.setMessage(messageArea.getText().trim());

        if (requestDAO.createRequest(er)) {
            SwingUtils.showSuccess(this, "Exchange request sent to " + targetUser.getName() + "!");
            dispose();
        } else {
            SwingUtils.showError(this, "Failed to send exchange request.");
        }
    }
}
