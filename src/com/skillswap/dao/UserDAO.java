package com.skillswap.dao;

import com.skillswap.model.User;
import com.skillswap.util.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashed = rs.getString("password");
                    if (PasswordUtil.check(password, hashed)) {
                        return mapRow(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(User user) {
        String sql = "INSERT INTO users (name, email, password, student_id, department, semester, skills, skills_to_learn, learning_mode, preferred_time, bio) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail().trim());
            ps.setString(3, PasswordUtil.hash(user.getPassword()));
            ps.setString(4, user.getStudentId());
            ps.setString(5, user.getDepartment());
            ps.setString(6, user.getSemester());
            ps.setString(7, user.getSkills());
            ps.setString(8, user.getSkillsToLearn());
            ps.setString(9, user.getLearningMode());
            ps.setString(10, user.getPreferredTime());
            ps.setString(11, user.getBio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET name=?, student_id=?, department=?, semester=?, skills=?, skills_to_learn=?, learning_mode=?, preferred_time=?, bio=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getStudentId());
            ps.setString(3, user.getDepartment());
            ps.setString(4, user.getSemester());
            ps.setString(5, user.getSkills());
            ps.setString(6, user.getSkillsToLearn());
            ps.setString(7, user.getLearningMode());
            ps.setString(8, user.getPreferredTime());
            ps.setString(9, user.getBio());
            ps.setInt(10, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY name ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setStudentId(rs.getString("student_id"));
        u.setDepartment(rs.getString("department"));
        u.setSemester(rs.getString("semester"));
        u.setSkills(rs.getString("skills"));
        u.setSkillsToLearn(rs.getString("skills_to_learn"));
        u.setLearningMode(rs.getString("learning_mode"));
        u.setPreferredTime(rs.getString("preferred_time"));
        u.setBio(rs.getString("bio"));
        return u;
    }
}
