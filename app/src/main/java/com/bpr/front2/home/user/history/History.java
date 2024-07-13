package com.bpr.front2.home.user.history;

public class History {
    private int hId;
    private int userId;
    private int courseId;
    private int videoId;
    private int roleId;
    private int watcherId;
    private long watchTime;
    private String upName;
    private String videoTitle;

    public int getHId() {
        return this.hId;
    }

    public int getUserId() {
        return this.userId;
    }

    public int getCourseId() {
        return this.courseId;
    }

    public int getVideoId() {
        return this.videoId;
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

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getUpName() {
        return upName;
    }

    public void setUpName(String upName) {
        this.upName = upName;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
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

    public void setVideoId(final int videoId) {
        this.videoId = videoId;
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
