package com.bpr.front2.home.user.history;

public class History {
    private int hId;
    private int userId;
    private int courseId;
    private int resId;
    private int roleId;
    private int watcherId;
    private long watchTime;

    public int getHId() {
        return this.hId;
    }

    public int getUserId() {
        return this.userId;
    }

    public int getCourseId() {
        return this.courseId;
    }

    public int getResId() {
        return this.resId;
    }

    public int getRoleId() {
        return this.roleId;
    }

    public int getWatcherId() {
        return this.watcherId;
    }

    public long getWatchTime() {
        return this.watchTime;
    }

    public void setHId(final int hId) {
        this.hId = hId;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    public void setCourseId(final int courseId) {
        this.courseId = courseId;
    }

    public void setResId(final int resId) {
        this.resId = resId;
    }

    public void setRoleId(final int roleId) {
        this.roleId = roleId;
    }

    public void setWatcherId(final int watcherId) {
        this.watcherId = watcherId;
    }

    public void setWatchTime(final long watchTime) {
        this.watchTime = watchTime;
    }
}
