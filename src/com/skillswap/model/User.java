package com.skillswap.model;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String studentId;
    private String department;
    private String semester;
    private String skills;
    private String skillsToLearn;
    private String learningMode;
    private String preferredTime;
    private String bio;

    public User() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getSkillsToLearn() { return skillsToLearn; }
    public void setSkillsToLearn(String skillsToLearn) { this.skillsToLearn = skillsToLearn; }
    public String getLearningMode() { return learningMode; }
    public void setLearningMode(String learningMode) { this.learningMode = learningMode; }
    public String getPreferredTime() { return preferredTime; }
    public void setPreferredTime(String preferredTime) { this.preferredTime = preferredTime; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
