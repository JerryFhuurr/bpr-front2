package com.bpr.front2.home.user.teacher.uploads;

public class UploadItem {
    //TODO 等后端完成后修改
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

    @Override
    public String toString() {
        return "UploadItem{" +
                "videoId=" + videoId +
                ", videoTitle='" + videoTitle + '\'' +
                ", courseId=" + courseId +
                ", userId=" + userId +
                ", roleId=" + roleId +
                ", videoScore=" + videoScore +
                ", videoDescription='" + videoDescription + '\'' +
                ", videoPath='" + videoPath + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", videoFileName='" + videoFileName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", videoFileDownload='" + videoFileDownload + '\'' +
                ", fileNameDownload='" + fileNameDownload + '\'' +
                '}';
    }
}
