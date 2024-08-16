package com.bpr.front2.handler;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

public class ActivityManager extends Application {
    private List<Activity> activityList = new LinkedList<Activity>();
    private static ActivityManager instance;

    private ActivityManager() {
    }

    public static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // add Activity to container
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void removeActivity(Activity a) {
        for (Activity activity : activityList) {
            if (activity == a) {
                activityList.remove(activity);
            }
        }
    }

    // loop all Activity and finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        activityList.clear();
    }
}
