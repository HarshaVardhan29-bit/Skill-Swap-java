package com.skillswap.view;

import com.skillswap.dao.*;
import com.skillswap.model.*;
import com.skillswap.util.SwingUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {
    private User currentUser;
    private JLabel welcomeLabel, sessionsCountLbl, bookingsCountLbl, matchesCountLbl;
    private JTable announcementTable;

    private final SessionDAO sessionDAO = new SessionDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final UserDAO userDAO = new UserDAO();
    private final AnnouncementDAO announcementDAO = new AnnouncementDAO();

    public DashboardFrame(User user) {
        this.currentUser = user;
        setTitle("SkillSwap - Dashboard");
        setSize(1020, 660);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SwingUtils.centerFrame(this);
        setLayout(new BorderLayout());

        setupMenuBar();
        setupSidebar();
        setupCenterPanel();
        refreshDashboardData();
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem refreshItem = new JMenuItem("Refresh Dashboard");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");

        refreshItem.addActionListener(e -> refreshDashboardData());
        logoutItem.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(refreshItem);
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu profileMenu = new JMenu("Profile");
        JMenuItem editProfileItem = new JMenuItem("Edit Profile");
        editProfileItem.addActionListener(e -> {
            new EditProfileDialog(this, currentUser, updated -> {
                this.currentUser = updated;
                refreshDashboardData();
            }).setVisible(true);
        });
        profileMenu.add(editProfileItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About SkillSwap");
        aboutItem.addActionListener(e -> SwingUtils.showSuccess(this,
                "SkillSwap Desktop v1.0\nPeer-to-Peer Student Learning & Skill Exchange Platform."));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(profileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void setupSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SwingUtils.CARD);
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, SwingUtils.BORDER_COLOR));

        JLabel brand = SwingUtils.createLabel("  SkillSwap Hub", SwingUtils.TITLE_FONT, SwingUtils.PRIMARY);
        brand.setBorder(BorderFactory.createEmptyBorder(20, 10, 25, 10));
        sidebar.add(brand);

        addNavButton(sidebar, "Dashboard", e -> refreshDashboardData());
        addNavButton(sidebar, "Find Matches", e -> new FindMatchesFrame(currentUser).setVisible(true));
        addNavButton(sidebar, "My Requests", e -> new MyRequestsFrame(currentUser).setVisible(true));
        addNavButton(sidebar, "My Exchanges", e -> new MyExchangesFrame(currentUser).setVisible(true));
        addNavButton(sidebar, "My Sessions", e -> new MySessionsFrame(currentUser).setVisible(true));
        addNavButton(sidebar, "Leaderboard", e -> new LeaderboardFrame().setVisible(true));

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = SwingUtils.createStyledButton("Logout", SwingUtils.DANGER);
        logoutBtn.setMaximumSize(new Dimension(190, 38));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        sidebar.add(logoutBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        add(sidebar, BorderLayout.WEST);
    }

    private void addNavButton(JPanel sidebar, String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(SwingUtils.BOLD_FONT);
        btn.setForeground(SwingUtils.TEXT_PRIMARY);
        btn.setBackground(SwingUtils.CARD);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        sidebar.add(btn);
    }

    private void setupCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(15, 15));
        center.setBackground(SwingUtils.BACKGROUND);
        center.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        welcomeLabel = SwingUtils.createLabel("Welcome back, " + currentUser.getName() + "!", SwingUtils.TITLE_FONT, SwingUtils.TEXT_PRIMARY);
        JButton createSessionBtn = SwingUtils.createStyledButton("+ Create New Session", SwingUtils.PRIMARY_DARK);
        createSessionBtn.addActionListener(e -> {
            new CreateSessionDialog(this, currentUser, this::refreshDashboardData).setVisible(true);
        });
        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(createSessionBtn, BorderLayout.EAST);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);

        sessionsCountLbl = SwingUtils.createLabel("0", SwingUtils.TITLE_FONT, SwingUtils.PRIMARY);
        bookingsCountLbl = SwingUtils.createLabel("0", SwingUtils.TITLE_FONT, SwingUtils.SUCCESS);
        matchesCountLbl = SwingUtils.createLabel("0", SwingUtils.TITLE_FONT, new Color(250, 204, 21));

        statsPanel.add(createStatCard("My Active Sessions", sessionsCountLbl));
        statsPanel.add(createStatCard("Total Bookings", bookingsCountLbl));
        statsPanel.add(createStatCard("Compatible Peers", matchesCountLbl));

        JPanel northWrapper = new JPanel(new BorderLayout(0, 20));
        northWrapper.setOpaque(false);
        northWrapper.add(topBar, BorderLayout.NORTH);
        northWrapper.add(statsPanel, BorderLayout.CENTER);
        center.add(northWrapper, BorderLayout.NORTH);

        JPanel annPanel = new JPanel(new BorderLayout(0, 10));
        annPanel.setBackground(SwingUtils.CARD);
        annPanel.setBorder(SwingUtils.CARD_BORDER);

        JLabel annTitle = SwingUtils.createLabel("Campus Announcements", SwingUtils.SUBTITLE_FONT, SwingUtils.PRIMARY);
        annPanel.add(annTitle, BorderLayout.NORTH);

        announcementTable = new JTable();
        SwingUtils.styleTable(announcementTable);
        annPanel.add(new JScrollPane(announcementTable), BorderLayout.CENTER);

        center.add(annPanel, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(5, 10));
        card.setBackground(SwingUtils.CARD);
        card.setBorder(SwingUtils.CARD_BORDER);
        card.add(SwingUtils.createLabel(title, SwingUtils.BODY_FONT, SwingUtils.TEXT_SECONDARY), BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void refreshDashboardData() {
        welcomeLabel.setText("Welcome back, " + currentUser.getName() + "!");

        int offeringCount = sessionDAO.getSessionsByUser(currentUser.getId(), "offering").size();
        int requestingCount = sessionDAO.getSessionsByUser(currentUser.getId(), "requesting").size();
        sessionsCountLbl.setText(String.valueOf(offeringCount + requestingCount));

        int bookingCount = bookingDAO.getBookingsByUser(currentUser.getId()).size();
        bookingsCountLbl.setText(String.valueOf(bookingCount));

        long matchCount = userDAO.getAllUsers().stream()
                .filter(u -> u.getId() != currentUser.getId())
                .filter(u -> SwingUtils.calculateMatchScore(currentUser, u) >= 40)
                .count();
        matchesCountLbl.setText(String.valueOf(matchCount));

        DefaultTableModel model = new DefaultTableModel(new String[]{"Title", "Announcement", "Posted At"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        List<Announcement> announcements = announcementDAO.getRecentAnnouncements(10);
        for (Announcement a : announcements) {
            model.addRow(new Object[]{a.getTitle(), a.getContent(), a.getCreatedAt()});
        }
        announcementTable.setModel(model);
    }
}
