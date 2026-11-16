package com.skillswap.view;

import com.skillswap.dao.BookingDAO;
import com.skillswap.model.Booking;
import com.skillswap.model.Session;
import com.skillswap.util.SwingUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SessionBookingsDialog extends JDialog {
    private final Session session;
    private final Runnable onUpdate;
    private JTable bookingTable;
    private List<Booking> bookingList;
    private final BookingDAO bookingDAO = new BookingDAO();

    public SessionBookingsDialog(Frame owner, Session session, Runnable onUpdate) {
        super(owner, "Bookings for: " + session.getTitle(), true);
        this.session = session;
        this.onUpdate = onUpdate;

        setSize(700, 420);
        SwingUtils.centerFrame(this);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(SwingUtils.BACKGROUND);

        bookingTable = new JTable();
        SwingUtils.styleTable(bookingTable);
        add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(SwingUtils.CARD);

        JButton confirmBtn = SwingUtils.createStyledButton("Confirm", SwingUtils.PRIMARY_DARK);
        JButton completeBtn = SwingUtils.createStyledButton("Complete", SwingUtils.SUCCESS);
        JButton cancelBtn = SwingUtils.createStyledButton("Cancel Booking", SwingUtils.DANGER);

        confirmBtn.addActionListener(e -> changeStatus("confirmed"));
        completeBtn.addActionListener(e -> changeStatus("completed"));
        cancelBtn.addActionListener(e -> changeStatus("cancelled"));

        btns.add(confirmBtn);
        btns.add(completeBtn);
        btns.add(cancelBtn);
        add(btns, BorderLayout.SOUTH);

        loadBookings();
    }

    private void loadBookings() {
        bookingList = bookingDAO.getBookingsBySession(session.getId());
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Learner Name", "Email", "Status", "Booked Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Booking b : bookingList) {
            model.addRow(new Object[]{b.getLearnerName(), b.getLearnerEmail(), b.getStatus().toUpperCase(), b.getCreatedAt()});
        }
        bookingTable.setModel(model);
    }

    private void changeStatus(String status) {
        int row = bookingTable.getSelectedRow();
        if (row < 0) {
            SwingUtils.showError(this, "Please select a booking first.");
            return;
        }
        Booking b = bookingList.get(row);
        if (bookingDAO.updateBookingStatus(b.getId(), status)) {
            SwingUtils.showSuccess(this, "Booking updated to " + status + ".");
            loadBookings();
            if (onUpdate != null) onUpdate.run();
        }
    }
}
