package com.bpr.front2.home.user.course;

import androidx.lifecycle.ViewModel;

import com.bpr.front2.home.user.teacher.uploads.UploadItem;

import java.util.ArrayList;

public class CourseVideModel extends ViewModel {

    private CourseItem courseItem;
    private ArrayList<UploadItem> uploadItems = new ArrayList<>();

    public void setCourseItem(CourseItem courseItem) {
        this.courseItem = courseItem;
    }

    public CourseItem getCourseItem() {
        return courseItem;
    }

    public ArrayList<UploadItem> getUploadItems() {
        return uploadItems;
    }

    //TODO 后端完成后修改
    public void setUploadItems() {
        for (int i = 1; i <= 5; i++) {
            UploadItem item = new UploadItem();
            item.id = i;
            item.title = "Title " + i; // 设置标题，你可以根据需要修改
            uploadItems.add(item);
        }
    }
}
