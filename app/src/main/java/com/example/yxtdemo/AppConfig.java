package com.example.yxtdemo;

import android.os.Environment;

import java.io.File;

/**
 * @Author szh
 * @Date 2019-09-23
 * @Description
 */
public class AppConfig {
    public static final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "yxtdemo" ;
    public static final String filePathTemp = filePath+File.separator + "temp";
}
