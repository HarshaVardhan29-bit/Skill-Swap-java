package com.skillswap.view;

import com.skillswap.dao.SessionDAO;
import com.skillswap.model.Session;
import com.skillswap.model.User;
import com.skillswap.util.SwingUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MySessionsFrame extends JFrame {
    private final User currentUser;
    private JTabbedPane tabbedPane;
    private JTable offeringTable, requestingTable;
    private List<Session> offeringList, requestingList;
    private final SessionDAO sessionDAO = new SessionDAO();

    public MySessionsFrame(User currentUser) {
        this.currentUser = currentUser;
        setTitle("SkillSwap - My Sessions");
        setSize(900, 520);
        SwingUtils.centerFrame(this);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(SwingUtils.BOLD_FONT);

        offeringTable = new JTable();
        SwingUtils.styleTable(offeringTable);
        requestingTable = new JTable();
        SwingUtils.styleTable(requestingTable);

        tabbedPane.addTab("Offering Help (Teaching)", new JScrollPane(offeringTable));
        tabbedPane.addTab("Requesting Help (Learning)", new JScrollPane(requestingTable));
        add(tabbedPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(SwingUtils.CARD);

        JButton viewBookingsBtn = SwingUtils.createStyledButton("View Bookings", SwingUtils.PRIMARY_DARK);
        JButton createBtn = SwingUtils.createStyledButton("Create New", SwingUtils.SUCCESS);
        JButton editBtn = SwingUtils.createStyledButton("Edit", SwingUtils.CARD);
        JButton deleteBtn = SwingUtils.createStyledButton("Delete", SwingUtils.DANGER);

        viewBookingsBtn.addActionListener(e -> openBookingsDialog());
        createBtn.addActionListener(e -> new CreateSessionDialog(this, currentUser, this::loadSessions).setVisible(true));
        editBtn.addActionListener(e -> editSelectedSession());
        deleteBtn.addActionListener(e -> deleteSelectedSession());

        btnPanel.add(viewBookingsBtn);
        btnPanel.add(createBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.SOUTH);

        loadSessions();
    }

    private void loadSessions() {
        offeringList = sessionDAO.getSessionsByUser(currentUser.getId(), "offering");
        offeringTable.setModel(buildModel(offeringList));

        requestingList = sessionDAO.getSessionsByUser(currentUser.getId(), "requesting");
        requestingTable.setModel(buildModel(requestingList));
    }

    private DefaultTableModel buildModel(List<Session> sessions) {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Title", "Skill", "Scheduled At", "Status", "Bookings"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Session s : sessions) {
            model.addRow(new Object[]{s.getTitle(), s.getSkill(), s.getScheduledAt(), s.getStatus().toUpperCase(), s.getBookingCount()});
        }
        return model;
    }

    private Session getSelectedSession() {
        boolean isOffering = tabbedPane.getSelectedIndex() == 0;
        JTable activeTable = isOffering ? offeringTable : requestingTable;
        List<Session> activeList = isOffering ? offeringList : requestingList;
        int row = activeTable.getSelectedRow();
        if (row < 0) {
            SwingUtils.showError(this, "Please select a session first.");
            return null;
        }
        return activeList.get(row);
    }

    private void openBookingsDialog() {
        Session s = getSelectedSession();
        if (s != null) {
            new SessionBookingsDialog(this, s, this::loadSessions).setVisible(true);
        }
    }

    private void editSelectedSession() {
        Session s = getSelectedSession();
        if (s != null) {
            new CreateSessionDialog(this, currentUser, s, this::loadSessions).setVisible(true);
        }
    }

    private void deleteSelectedSession() {
        Session s = getSelectedSession();
        if (s != null && SwingUtils.confirm(this, "Delete session '" + s.getTitle() + "'?")) {
            if (sessionDAO.deleteSession(s.getId())) {
                loadSessions();
            }
        }
    }
}
