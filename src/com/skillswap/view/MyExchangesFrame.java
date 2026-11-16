package com.skillswap.view;

import com.skillswap.dao.BookingDAO;
import com.skillswap.model.Booking;
import com.skillswap.model.User;
import com.skillswap.util.SwingUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MyExchangesFrame extends JFrame {
    private final User currentUser;
    private JTable exchangeTable;
    private List<Booking> completedBookings;
    private final BookingDAO bookingDAO = new BookingDAO();

    public MyExchangesFrame(User currentUser) {
        this.currentUser = currentUser;
        setTitle("SkillSwap - Completed Exchanges & Ratings");
        setSize(860, 480);
        SwingUtils.centerFrame(this);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(SwingUtils.BACKGROUND);

        exchangeTable = new JTable();
        SwingUtils.styleTable(exchangeTable);
        add(new JScrollPane(exchangeTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottom.setBackground(SwingUtils.CARD);
        JButton rateBtn = SwingUtils.createStyledButton("Rate Session ★", SwingUtils.PRIMARY_DARK);
        rateBtn.addActionListener(e -> openRateDialog());
        bottom.add(rateBtn);
        add(bottom, BorderLayout.SOUTH);

        loadExchanges();
    }

    private void loadExchanges() {
        completedBookings = bookingDAO.getBookingsByUser(currentUser.getId()).stream()
                .filter(b -> "completed".equalsIgnoreCase(b.getStatus()))
                .collect(Collectors.toList());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Session Title", "Partner Name", "Skill", "Date", "Status", "Rating"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Booking b : completedBookings) {
            String partner = (b.getLearnerId() == currentUser.getId()) ? b.getTutorName() : b.getLearnerName();
            String ratingStr = b.getRatingValue() > 0 ? b.getRatingValue() + " / 5 ★" : "Not Rated";
            model.addRow(new Object[]{b.getSessionTitle(), partner, b.getSkill(), b.getCreatedAt(), b.getStatus().toUpperCase(), ratingStr});
        }
        exchangeTable.setModel(model);
    }

    private void openRateDialog() {
        int row = exchangeTable.getSelectedRow();
        if (row < 0) {
            SwingUtils.showError(this, "Please select a completed session to rate.");
            return;
        }
        Booking selected = completedBookings.get(row);
        if (selected.getRatingValue() > 0) {
            SwingUtils.showError(this, "This session has already been rated.");
            return;
        }
        new RateSessionDialog(this, selected, this::loadExchanges).setVisible(true);
    }
}
