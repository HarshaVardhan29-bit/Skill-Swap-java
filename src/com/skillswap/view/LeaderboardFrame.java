package com.skillswap.view;

import com.skillswap.dao.RatingDAO;
import com.skillswap.util.SwingUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LeaderboardFrame extends JFrame {
    private final RatingDAO ratingDAO = new RatingDAO();

    public LeaderboardFrame() {
        setTitle("SkillSwap - Top Mentors Leaderboard");
        setSize(820, 480);
        SwingUtils.centerFrame(this);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(SwingUtils.BACKGROUND);

        JLabel header = SwingUtils.createLabel("  Top 10 Peer Mentors", SwingUtils.TITLE_FONT, SwingUtils.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        add(header, BorderLayout.NORTH);

        JTable table = new JTable();
        SwingUtils.styleTable(table);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Rank", "Name", "Skills", "Avg Rating", "Total Sessions", "Total Reviews"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Object[]> rows = ratingDAO.getLeaderboard(10);
        for (Object[] r : rows) {
            model.addRow(r);
        }
        table.setModel(model);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
