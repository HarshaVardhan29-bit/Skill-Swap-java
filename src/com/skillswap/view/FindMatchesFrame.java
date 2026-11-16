package com.skillswap.view;

import com.skillswap.dao.*;
import com.skillswap.model.*;
import com.skillswap.util.SwingUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FindMatchesFrame extends JFrame {
    private final User currentUser;
    private JTextField searchField;
    private JComboBox<String> modeFilter;
    private JTable matchTable;
    private List<User> displayedUsers = new ArrayList<>();

    private final UserDAO userDAO = new UserDAO();
    private final RatingDAO ratingDAO = new RatingDAO();
    private final SessionDAO sessionDAO = new SessionDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    public FindMatchesFrame(User currentUser) {
        this.currentUser = currentUser;
        setTitle("SkillSwap - Find Learning Partners");
        setSize(980, 560);
        SwingUtils.centerFrame(this);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(SwingUtils.BACKGROUND);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        topPanel.setBackground(SwingUtils.CARD);

        searchField = SwingUtils.createStyledTextField(22);
        modeFilter = new JComboBox<>(new String[]{"All Modes", "Online", "In-Person", "Hybrid"});
        JButton searchBtn = SwingUtils.createStyledButton("Search", SwingUtils.PRIMARY_DARK);
        searchBtn.addActionListener(e -> loadMatches());

        topPanel.add(SwingUtils.createLabel("Search Skill / Name:", SwingUtils.BOLD_FONT, SwingUtils.TEXT_PRIMARY));
        topPanel.add(searchField);
        topPanel.add(SwingUtils.createLabel("Mode:", SwingUtils.BOLD_FONT, SwingUtils.TEXT_PRIMARY));
        topPanel.add(modeFilter);
        topPanel.add(searchBtn);
        add(topPanel, BorderLayout.NORTH);

        matchTable = new JTable();
        SwingUtils.styleTable(matchTable);
        add(new JScrollPane(matchTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottomPanel.setBackground(SwingUtils.CARD);

        JButton viewProfileBtn = SwingUtils.createStyledButton("View Profile & Sessions", SwingUtils.CARD);
        JButton bookSessionBtn = SwingUtils.createStyledButton("Book Open Session", SwingUtils.SUCCESS);
        JButton sendReqBtn = SwingUtils.createStyledButton("Send Exchange Request", SwingUtils.PRIMARY_DARK);

        viewProfileBtn.addActionListener(e -> viewSelectedProfile());
        bookSessionBtn.addActionListener(e -> bookSelectedUserSession());
        sendReqBtn.addActionListener(e -> openSendRequestDialog());

        bottomPanel.add(viewProfileBtn);
        bottomPanel.add(bookSessionBtn);
        bottomPanel.add(sendReqBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        loadMatches();
    }

    private void loadMatches() {
        String query = searchField.getText().trim().toLowerCase();
        String selectedMode = (String) modeFilter.getSelectedItem();

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Name", "Skills", "Skills to Learn", "Learning Mode", "Match Score", "Department", "Rating"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        displayedUsers.clear();
        List<User> all = userDAO.getAllUsers();
        for (User u : all) {
            if (u.getId() == currentUser.getId()) continue;

            boolean matchesQuery = query.isEmpty()
                    || (u.getName() != null && u.getName().toLowerCase().contains(query))
                    || (u.getSkills() != null && u.getSkills().toLowerCase().contains(query))
                    || (u.getSkillsToLearn() != null && u.getSkillsToLearn().toLowerCase().contains(query));

            boolean matchesMode = "All Modes".equals(selectedMode)
                    || (u.getLearningMode() != null && u.getLearningMode().equalsIgnoreCase(selectedMode));

            if (matchesQuery && matchesMode) {
                displayedUsers.add(u);
                int score = SwingUtils.calculateMatchScore(currentUser, u);
                double avgRating = ratingDAO.getAverageRating(u.getId());
                model.addRow(new Object[]{
                    u.getName(),
                    u.getSkills(),
                    u.getSkillsToLearn(),
                    u.getLearningMode(),
                    score + "%",
                    u.getDepartment(),
                    avgRating > 0 ? String.format("%.1f ★", avgRating) : "New"
                });
            }
        }
        matchTable.setModel(model);
    }

    private User getSelectedUser() {
        int row = matchTable.getSelectedRow();
        if (row < 0) {
            SwingUtils.showError(this, "Please select a student from the table first.");
            return null;
        }
        return displayedUsers.get(row);
    }

    private void viewSelectedProfile() {
        User target = getSelectedUser();
        if (target == null) return;
        String info = String.format(
                "Name: %s (%s)\nDepartment: %s | Semester: %s\nMode: %s | Preferred Time: %s\n\nOffers: %s\nWants to Learn: %s\n\nBio:\n%s",
                target.getName(), target.getStudentId(), target.getDepartment(), target.getSemester(),
                target.getLearningMode(), target.getPreferredTime(), target.getSkills(), target.getSkillsToLearn(), target.getBio()
        );
        JOptionPane.showMessageDialog(this, info, target.getName() + "'s Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    private void bookSelectedUserSession() {
        User target = getSelectedUser();
        if (target == null) return;
        List<Session> openSessions = sessionDAO.getSessionsByUser(target.getId(), "offering");
        if (openSessions.isEmpty()) {
            SwingUtils.showError(this, target.getName() + " has no open teaching sessions right now. Try sending an Exchange Request!");
            return;
        }
        String[] choices = openSessions.stream().map(s -> s.getId() + ": " + s.getTitle() + " (" + s.getSkill() + ")").toArray(String[]::new);
        String chosen = (String) JOptionPane.showInputDialog(this, "Select a session to book:", "Book Session",
                JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        if (chosen != null) {
            int sessionId = Integer.parseInt(chosen.split(":")[0]);
            if (bookingDAO.createBooking(sessionId, currentUser.getId())) {
                SwingUtils.showSuccess(this, "Booking request sent to " + target.getName() + "!");
            } else {
                SwingUtils.showError(this, "You have already booked this session.");
            }
        }
    }

    private void openSendRequestDialog() {
        User target = getSelectedUser();
        if (target == null) return;
        new SendRequestDialog(this, currentUser, target).setVisible(true);
    }
}
