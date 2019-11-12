package com.w3engineers.core.util.helper;

public class TimeConverter {
    public static String ConvertSecondsToHourMinute(int seconds)
    {
      String time="";
        int p1 = seconds % 60;
        int p2 = seconds / 60;
        int p3 = p2 % 60;
        p2 = p2 / 60;
        time=time+p2 + ":" + p3 + ":" + p1;
        return time;
    }
}
