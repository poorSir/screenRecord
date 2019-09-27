package com.example.yxtdemo;

/**
 * @Author szh
 * @Date 2019-09-25
 * @Description
 */
public class TimeUtil {
    /**
     * 秒->01:05
     * @param mRecordSeconds
     * @return
     */
    public static String convertTime(int mRecordSeconds){
        int minute = 0, second = 0;
        if (mRecordSeconds >= 60) {
            minute = mRecordSeconds / 60;
            second = mRecordSeconds % 60;
        } else {
            second = mRecordSeconds;
        }
        String timeTip = "0" + minute + ":" + (second < 10 ? "0" + second : second + "");
        return timeTip;
    }

    /**
     *01:05->秒
     * @param timeTip
     * @return
     */
    public static int convertTime(String timeTip){
        String[] split = timeTip.split("\\:");
        int minutes = Integer.valueOf(split[0]);
        int seconds = Integer.valueOf(split[1].substring(0,2));
        return minutes*60+seconds;
    }
}
