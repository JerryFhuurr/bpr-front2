package com.bpr.front2.home.user.teacher.uploads;

import androidx.annotation.NonNull;

public class UploadItem {
    public int resId;
    public int courseId;
    public int userId;
    public int roleId;
    public float resScore;
    public String resTitle;
    public String resDescription;
    public String fileUrl;
    public String fileName;
    public String fileNameDownload;
    public long fileSize;
    public String type;

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
