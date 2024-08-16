package com.bpr.front2.home.user.course;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.bpr.front2.handler.HttpUtils;
import com.bpr.front2.home.user.teacher.uploads.UploadItem;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CourseViewModel extends ViewModel {

    private CourseItem courseItem;

    public void setCourseItem(CourseItem courseItem) {
        this.courseItem = courseItem;
    }

    public CourseItem getCourseItem() {
        return courseItem;
    }


}
