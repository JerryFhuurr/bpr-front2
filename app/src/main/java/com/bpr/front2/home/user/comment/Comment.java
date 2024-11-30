package com.bpr.front2.home.user.comment;

import java.sql.Timestamp;

public class Comment {
    private int commentId;
    // ↓ these are info from video (not sender!!)
    private int userId;
    private int courseId;
    private int resId;
    private int roleId;
    // ↑
    private int senderId;
    private String senderName;
    private String commentText;
    private long commentTime;
    private float commentScore;


    public int getCommentId() {
        return this.commentId;
    }
    public int getUserId() {
        return this.userId;
    }
    public int getCourseId() {
        return this.courseId;
    }
    public int getRoleId() {
        return this.roleId;
    }
    public int getSenderId() {
        return this.senderId;
    }
    public String getCommentText() {
        return this.commentText;
    }

    public int getResId() {
        return resId;
    }

    public String getSenderName() {
        return senderName;
    }

    public long getCommentTime() {
        return this.commentTime;
    }
    public float getCommentScore() {
        return this.commentScore;
    }
    public void setCommentId(final int commentId) {
        this.commentId = commentId;
    }
    public void setUserId(final int userId) {
        this.userId = userId;
    }
    public void setCourseId(final int courseId) {
        this.courseId = courseId;
    }
    public void setRoleId(final int roleId) {
        this.roleId = roleId;
    }
    public void setSenderId(final int senderId) {
        this.senderId = senderId;
    }
    public void setCommentText(final String commentText) {
        this.commentText = commentText;
    }
    public void setCommentTime(final long commentTime) {
        this.commentTime = commentTime;
    }
    public void setCommentScore(final float commentScore) {
        this.commentScore = commentScore;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
