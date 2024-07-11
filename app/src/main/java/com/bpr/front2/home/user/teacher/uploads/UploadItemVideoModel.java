package com.bpr.front2.home.user.teacher.uploads;

import androidx.lifecycle.ViewModel;

public class UploadItemVideoModel extends ViewModel {
    private UploadItem uploadItem;

    public void setUploadItem(UploadItem uploadItem) {
        this.uploadItem = uploadItem;
    }

    public UploadItem getUploadItem() {
        return uploadItem;
    }
}
