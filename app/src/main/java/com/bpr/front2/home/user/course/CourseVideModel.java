package com.bpr.front2.home.user.course;

import androidx.lifecycle.ViewModel;

public class CourseVideModel extends ViewModel {

    private CourseItem courseItem;

    public void setCourseItem(CourseItem courseItem) {
        this.courseItem = courseItem;
    }

    public CourseItem getCourseItem() {
        return courseItem;
    }

}
