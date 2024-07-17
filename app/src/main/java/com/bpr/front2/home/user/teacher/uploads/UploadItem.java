package com.bpr.front2.home.user.teacher.uploads;

import androidx.annotation.NonNull;

public class UploadItem {
    public int videoId;
    public String videoTitle;
    public int courseId;
    public int userId;
    public int roleId;
    public float videoScore;
    public String videoDescription;
    public String videoPath;
    public String fileUrl;
    public String videoFileName;
    public String fileName;
    public String videoFileDownload;
    public String fileNameDownload;
    public long videoSize;
    public long fileSize;

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
