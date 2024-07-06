package com.bpr.front2.home.user.teacher.uploadPage;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class FileViewModel extends ViewModel {
    private ArrayList<UploadFileItem> items = new ArrayList<>();

    public void setFileItem(String uploadTitle) {
        UploadFileItem item1 = new UploadFileItem();
        item1.setFileName("testName");
        item1.setId(1);
        item1.setType("video");
        item1.setSize(1.0f);

        UploadFileItem item2 = new UploadFileItem();
        item2.setFileName("testName 2");
        item2.setId(2);
        item2.setType("file");
        item2.setSize(1.0f);

        items.add(item1);
        items.add(item2);
    }

    public ArrayList<UploadFileItem> getFileItem() {
        //TODO 应该是传一个列表回去
        return items;
    }
}
