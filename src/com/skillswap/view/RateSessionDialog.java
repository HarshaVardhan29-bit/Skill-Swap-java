package com.skillswap.view;

import com.skillswap.dao.RatingDAO;
import com.skillswap.model.Booking;
import com.skillswap.model.Rating;
import com.skillswap.util.SwingUtils;
import javax.swing.*;
import java.awt.*;

public class RateSessionDialog extends JDialog {
    private final Booking booking;
    private final Runnable onSuccess;
    private final JLabel[] stars = new JLabel[5];
    private JSlider ratingSlider;
    private JTextArea reviewArea;
    private final RatingDAO ratingDAO = new RatingDAO();

    public RateSessionDialog(Frame owner, Booking booking, Runnable onSuccess) {
        super(owner, "Rate Session: " + booking.getSessionTitle(), true);
        this.booking = booking;
        this.onSuccess = onSuccess;

        setSize(420, 360);
        SwingUtils.centerFrame(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SwingUtils.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;

        JPanel starPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        starPanel.setOpaque(false);
        for (int i = 0; i < 5; i++) {
            stars[i] = new JLabel("★");
            stars[i].setFont(new Font("SansSerif", Font.BOLD, 28));
            stars[i].setForeground(new Color(250, 204, 21));
            starPanel.add(stars[i]);
        }
        gbc.gridy = 0;
        panel.add(starPanel, gbc);

        ratingSlider = new JSlider(1, 5, 5);
        ratingSlider.setMajorTickSpacing(1);
        ratingSlider.setPaintTicks(true);
        ratingSlider.setPaintLabels(true);
        ratingSlider.setBackground(SwingUtils.BACKGROUND);
        ratingSlider.setForeground(SwingUtils.TEXT_PRIMARY);
        ratingSlider.addChangeListener(e -> updateStars(ratingSlider.getValue()));

        gbc.gridy = 1;
        panel.add(ratingSlider, gbc);

        gbc.gridy = 2;
        panel.add(SwingUtils.createLabel("Written Review:", SwingUtils.BOLD_FONT, SwingUtils.TEXT_PRIMARY), gbc);

        reviewArea = SwingUtils.createStyledTextArea(4, 20);
        gbc.gridy = 3;
        panel.add(new JScrollPane(reviewArea), gbc);

        JButton submitBtn = SwingUtils.createStyledButton("Submit Rating", SwingUtils.PRIMARY_DARK);
        submitBtn.addActionListener(e -> submitRating());
        gbc.gridy = 4;
        panel.add(submitBtn, gbc);

        add(panel);
    }

    private void updateStars(int count) {
        for (int i = 0; i < 5; i++) {
            stars[i].setForeground(i < count ? new Color(250, 204, 21) : SwingUtils.BORDER_COLOR);
        }
    }

    private void submitRating() {
        Rating r = new Rating();
        r.setBookingId(booking.getId());
        r.setRating(ratingSlider.getValue());
        r.setReview(reviewArea.getText().trim());

        if (ratingDAO.createRating(r)) {
            SwingUtils.showSuccess(this, "Thank you for your feedback!");
            if (onSuccess != null) onSuccess.run();
            dispose();
        } else {
            SwingUtils.showError(this, "Could not submit rating.");
        }
    }
}
