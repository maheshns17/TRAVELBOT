package com.pmaptechnotech.travelbot.logics;


import com.pmaptechnotech.travelbot.models.User;

/**
 * Created by Admin on 1/7/2018.
 */

public class U {
    public static User user;
    public static String USER_NAME = "";
    public static String USER_EMAIL = "";
    public static String USER_MOBILE = "";
    public static String ENTER_PASSWORD = "";
    public static String USER_SOURCE = "";
    public static String USER_DEST = "";
    public static String USER_SOURCE_CODE = "";
    public static String USER_DEST_CODE = "";
    public static String USER_JOURNEY_DATE = "";

    public static String getMonthFromName(String month) {
        switch (month.toUpperCase()) {
            case "JANUARY":
                return "01";
            case "FEBRUARY":
                return "02";
            case "MARCH":
                return "03";
            case "APRIL":
                return "04";
            case "MAY":
                return "05";
            case "JUN":
                return "06";
            case "JULY":
                return "07";
            case "AUGUST":
                return "08";
            case "SEPTEMBER":
                return "09";
            case "OCTOBER":
                return "10";
            case "NOVEMBER":
                return "11";
            case "DECEMBER":
                return "12";

        }
        return "02";
    }

}
