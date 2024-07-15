package com.bpr.front2.handler;

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
}
