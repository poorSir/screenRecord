package com.example.yxtdemo;

import android.content.Intent;
import android.graphics.Point;

import com.example.yxtdemo.CWEntity.CW;
import com.example.yxtdemo.CWEntity.CWACT;
import com.example.yxtdemo.CWEntity.CWBackground;
import com.example.yxtdemo.CWEntity.CWLine;
import com.example.yxtdemo.CWEntity.CWSwitch;
import com.google.gson.Gson;
import com.example.yxtdemo.CWEntity.CWPage;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * @Author szh
 * @Date 2019-09-23
 * @Description
 */
public class CWFileUtil {
    public static StringBuilder actStringBuilder = new StringBuilder();
    public  static  int mRecordSeconds;

    public static void write(List<ScreenRecordEntity> data) {
        File file = new File(AppConfig.filePathTemp + File.separator + "data.cw");
        try {
            Writer wr = new FileWriter(file, true);
            wr.write("#VERSION:1.0.0");
            wr.write("\n");
            wr.write("#RESOLUTION:[" + CommonUtil.getScreenWidth() + "," + CommonUtil.getScreenHeight() + "]");
            wr.write("\n");
            wr.write("#AUDIO:audio.mp3");
            wr.write("\n");
            List<CWPage> PAGES = new ArrayList<>();
            for (ScreenRecordEntity screenRecordEntity : data) {
                CWPage cwPage = new CWPage();
                CWBackground background;
                if ("0".equals(screenRecordEntity.getType())) {
                    background = new CWBackground();
                    background.setRgba("255,255,255,1");
                } else {
                    background = new CWBackground();
                    File file1 = new File(screenRecordEntity.getPath());
                    background.setUrl(file1.getName());
                }
                cwPage.setBackground(background);
                PAGES.add(cwPage);
            }
            Gson gson = new Gson();
            wr.write("#PAGES:" + gson.toJson(PAGES));
            wr.write("\n\n");
            wr.write(actStringBuilder.toString());
            wr.write("\n");
            wr.close();
            actStringBuilder = new StringBuilder();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static  void setSeconds(int seconds) {
        mRecordSeconds = seconds;
    }

    public static void writeACTLine(List<Point> points, int width, String color) {
        int len = points.size();
        if (len == 0) {
            return;
        }
        int minute = 0, second = 0;
        if (mRecordSeconds >= 60) {
            minute = mRecordSeconds / 60;
            second = mRecordSeconds % 60;
        } else {
            second = mRecordSeconds;
        }
        String timeTip = minute + ":" + (second < 10 ? "0" + second : second + "") + ".000";
        actStringBuilder.append("#ACT:" + timeTip + ",line");
        actStringBuilder.append("\n");
        actStringBuilder.append("{\"width\":" + width + ",\"color\":\""+ color + "\",\"points\":[");
        for (int i = 0; i < len; i++) {
            actStringBuilder.append((i == 0 ? "[" : ",[") + points.get(i).x + "," + points.get(i).y + "]");
        }
        actStringBuilder.append("]}");
        actStringBuilder.append("\n");

    }

    public static void writeACTSwitch(int index) {
        int minute = 0, second = 0;
        if (mRecordSeconds >= 60) {
            minute = mRecordSeconds / 60;
            second = mRecordSeconds % 60;
        } else {
            second = mRecordSeconds;
        }
        String timeTip = minute + ":" + (second < 10 ? "0" + second : second + "") + ".000";
        actStringBuilder.append("#ACT:" + timeTip + ",switch");
        actStringBuilder.append("\n");
        actStringBuilder.append("{\"index\":"+index+"}");
        actStringBuilder.append("\n");
    }

    public static void writeACTClear() {
        int minute = 0, second = 0;
        if (mRecordSeconds >= 60) {
            minute = mRecordSeconds / 60;
            second = mRecordSeconds % 60;
        } else {
            second = mRecordSeconds;
        }
        String timeTip = minute + ":" + (second < 10 ? "0" + second : second + "") + ".000";
        actStringBuilder.append("#ACT:" + timeTip + ",clear");
        actStringBuilder.append("\n");
    }
    public static CW read(String path){
        File file = new File(path);
        CW cw = new CW();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String result;
            String[] splitResult;
            Gson gson = new Gson();
            List<CWACT> cwacts = new ArrayList<>();
            while((result=bufferedReader.readLine())!=null){
                if(result.contains("VERSION")){
                    splitResult = result.split("\\:");
                    cw.setVERSION(splitResult[1]);
                }else if(result.contains("RESOLUTION")){
                    splitResult = result.split("\\:");
                    String[] screen = splitResult[1].split("\\,");
                    List<Integer> integers = new ArrayList<>();
                    integers.add(Integer.valueOf(screen[0].substring(1)));
                    integers.add(Integer.valueOf(screen[1].substring(0,screen[1].length()-1)));
                    cw.setRESOLUTION(integers);
                }else if(result.contains("AUDIO")){
                    splitResult = result.split("\\:");
                    cw.setAUDIO(splitResult[1]);
                }else if(result.contains("PAGES")){
                    List<CWPage> cwPages;
                    CWPage[] cwPage = gson.fromJson(result.substring(7),CWPage[].class);
                    cwPages = Arrays.asList(cwPage);
                    cw.setPAGES(cwPages);
                }else if (result.contains("ACT")){
                    splitResult = result.split("\\,");
                    CWACT cwact = new CWACT();
                    cwact.setTime(TimeUtil.convertTime(splitResult[0].substring(5)));
                    cwact.setAction(splitResult[1]);
                    if("line".equals(splitResult[1])){
                        result=bufferedReader.readLine();
                        cwact.setLine(gson.fromJson(result,CWLine.class));
                    }else if("switch".equals(splitResult[1])){
                        result=bufferedReader.readLine();
                        cwact.setCwSwitch(gson.fromJson(result, CWSwitch.class));
                    }
                    cwacts.add(cwact);
                }
            }
            cw.setACT(cwacts);
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cw;
    }
}
