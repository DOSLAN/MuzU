package com.muzu.explorer.service;

import javafx.util.Duration;

public class Formatter {

    public static String duration(Duration duration){
        int dh = (int)duration.toHours();
        int dm = (int)duration.toMinutes()%24;
        int ds = (int)duration.toSeconds()%60;
        String result = "";
        if (dh > 0) {
            result += dh + ":";
            if (dm < 10) result+="0";
        }
        result += dm + ":";
        if (ds < 10) result+="0";
        result+=ds;
        return result;
    }
}
