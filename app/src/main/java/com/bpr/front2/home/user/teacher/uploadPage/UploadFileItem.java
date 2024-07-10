package com.bpr.front2.home.user.teacher.uploadPage;

import androidx.annotation.NonNull;

public class UploadFileItem {
    //TODO 后端完成后修改
    private int videoId;
    private int courseId;
    private int userId;
    private int roleId;
    private float videoScore;
    private String videoTitle;
    private String videoDescription;
    private String videoPath;
    private String fileUrl;

    // Getter methods
    public int getVideoId() {
        return videoId;
    }

    public int getCourseId() {
        return courseId;
    }

    public int getUserId() {
        return userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public float getVideoScore() {
        return videoScore;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    // Setter methods
    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public void setVideoScore(float videoScore) {
        this.videoScore = videoScore;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "UploadFileItem{" +
                "videoId=" + videoId +
                ", courseId=" + courseId +
                ", userId=" + userId +
                ", roleId=" + roleId +
                ", videoScore=" + videoScore +
                ", videoTitle='" + videoTitle + '\'' +
                ", videoDescription='" + videoDescription + '\'' +
                ", videoPath='" + videoPath + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                '}';
    }
}
