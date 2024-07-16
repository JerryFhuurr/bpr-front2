package com.bpr.front2.handler;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import java.time.LocalTime;
import java.util.regex.Pattern;

public class GeneralUtils {


    public static int getRoleId(String roleName) {
        switch (roleName) {
            case "admin":
                return 1;
            case "teacher":
                return 2;
            case "student":
                return 3;
            default:
                return 0;
        }
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.length() < 1 || email.length() > 256) {
            return false;
        }
        String pattern = "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$";
        return Pattern.matches(pattern, email);
    }

    public static String checkTime() {
        LocalTime currentTime = LocalTime.now();

        int hour = currentTime.getHour();
        Log.i(TAG, "hour:" + hour);
        if (hour >= 6 && hour < 12) {
            return "morning";
        } else if (hour >= 12 && hour < 19) {
            return "afternoon";
        } else if (hour >= 19 && hour < 23) {
            return "evening";
        } else {
            return "night";
        }
    }
}
