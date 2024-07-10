package com.bpr.front2.handler;

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
}
