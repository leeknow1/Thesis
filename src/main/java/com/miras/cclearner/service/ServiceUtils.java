package com.miras.cclearner.service;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceUtils {

    public static String splitString(String value) {
        String[] args = value.split(" --> ", 2);
        return args[1];
    }


    public static String getFormattedDate() {
        SimpleDateFormat formattedDate = new SimpleDateFormat("dd-MM-yyyy");
        return formattedDate.format(new Date());
    }

    public static String getFormattedDateWithTime() {
        SimpleDateFormat formattedDate = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        return formattedDate.format(new Date());
    }

    public static String getRequestName(String name){
        if(name.contains("-->")){
            return splitString(name);
        }
        return name;
    }
}
