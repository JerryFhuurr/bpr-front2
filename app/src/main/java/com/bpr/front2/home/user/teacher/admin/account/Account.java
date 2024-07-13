package com.bpr.front2.home.user.teacher.admin.account;

import java.time.LocalDate;

public class Account {
    private int userId;
    private int roleId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private LocalDate birth;
    private String role;

    public int getUserId() {
        return this.userId;
    }
    public int getRoleId() {
        return this.roleId;
    }
    public String getUsername() {
        return this.username;
    }
    public String getPassword() {
        return this.password;
    }
    public String getEmail() {
        return this.email;
    }
    public String getPhone() {
        return this.phone;
    }
    public LocalDate getBirth() {
        return this.birth;
    }
    public String getRole() {
        return this.role;
    }
    public void setUserId(final int userId) {
        this.userId = userId;
    }
    public void setRoleId(final int roleId) {
        this.roleId = roleId;
    }
    public void setUsername(final String username) {
        this.username = username;
    }
    public void setPassword(final String password) {
        this.password = password;
    }
    public void setEmail(final String email) {
        this.email = email;
    }
    public void setPhone(final String phone) {
        this.phone = phone;
    }
    public void setBirth(final LocalDate birth) {
        this.birth = birth;
    }
    public void setRole(final String role) {
        this.role = role;
    }
}
