package com.bpr.front2.home.user.teacher.uploadPage;

import androidx.annotation.NonNull;

public class UploadFileItem {
    //TODO 后端完成后修改
    private int id;
    private String fileName;
    private String type;
    private float size;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getSize() {
        return size;
    }

    @NonNull
    @Override
    public String toString() {
        return "UploadFileItem{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                '}';
    }
}
