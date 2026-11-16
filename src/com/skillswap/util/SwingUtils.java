package com.skillswap.util;

import com.skillswap.model.User;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SwingUtils {
    public static final Color PRIMARY = new Color(129, 140, 248);
    public static final Color PRIMARY_DARK = new Color(99, 102, 241);
    public static final Color BACKGROUND = new Color(15, 23, 42);
    public static final Color CARD = new Color(30, 41, 59);
    public static final Color BORDER_COLOR = new Color(51, 65, 85);
    public static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color DANGER = new Color(239, 68, 68);

    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);
    public static final Font SUBTITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BOLD_FONT = new Font("SansSerif", Font.BOLD, 14);

    public static final Border CARD_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
    );

    public static final Border INPUT_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
    );

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        int res = JOptionPane.showConfirmDialog(parent, message, "Confirm Action", JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    public static void centerFrame(Window window) {
        window.setLocationRelativeTo(null);
    }

    public static JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(BOLD_FONT);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    public static JTextField createStyledTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(BODY_FONT);
        tf.setBackground(BACKGROUND);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(INPUT_BORDER);
        return tf;
    }

    public static JPasswordField createStyledPasswordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setFont(BODY_FONT);
        pf.setBackground(BACKGROUND);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(TEXT_PRIMARY);
        pf.setBorder(INPUT_BORDER);
        return pf;
    }

    public static JTextArea createStyledTextArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setFont(BODY_FONT);
        ta.setBackground(BACKGROUND);
        ta.setForeground(TEXT_PRIMARY);
        ta.setCaretColor(TEXT_PRIMARY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(INPUT_BORDER);
        return ta;
    }

    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    public static void styleTable(JTable table) {
        table.setBackground(CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(BODY_FONT);
        table.setRowHeight(30);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(PRIMARY_DARK);
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(BACKGROUND);
        header.setForeground(PRIMARY);
        header.setFont(BOLD_FONT);
        header.setPreferredSize(new Dimension(header.getWidth(), 34));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(CARD);
        renderer.setForeground(TEXT_PRIMARY);
    }

    public static int calculateMatchScore(User current, User other) {
        int score = 0;
        Set<String> myNeeds = parseSkills(current.getSkillsToLearn());
        Set<String> mySkills = parseSkills(current.getSkills());
        Set<String> theirSkills = parseSkills(other.getSkills());
        Set<String> theirNeeds = parseSkills(other.getSkillsToLearn());

        for (String need : myNeeds) {
            if (theirSkills.contains(need)) {
                score += 40;
                break;
            }
        }
        for (String skill : mySkills) {
            if (theirNeeds.contains(skill)) {
                score += 40;
                break;
            }
        }
        if (current.getLearningMode() != null &&
            current.getLearningMode().equalsIgnoreCase(other.getLearningMode())) {
            score += 20;
        }
        return score;
    }

    private static Set<String> parseSkills(String raw) {
        Set<String> set = new HashSet<>();
        if (raw == null || raw.trim().isEmpty()) return set;
        Arrays.stream(raw.split(","))
              .map(s -> s.trim().toLowerCase())
              .filter(s -> !s.isEmpty())
              .forEach(set::add);
        return set;
    }
}
