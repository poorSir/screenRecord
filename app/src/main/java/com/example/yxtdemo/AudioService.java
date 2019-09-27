package com.example.yxtdemo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author szh
 * @Date 2019-09-17
 * @Description
 */
public class AudioService extends Service implements Handler.Callback {
    private Handler mHandler;
    private MediaRecorder mMediaRecorder;
    private String mRecordFilePath;
    private File recordDictory;
    private boolean isRunning;
    private static final int MSG_TYPE_COUNT_DOWN = 110;
    private int mRecordSeconds;
    private int fileIndex;
    private boolean isFinish = true;

    public String getmRecordFilePath() {
        return mRecordFilePath;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isFinish() {
        return isFinish;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new AudioBinder();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_TYPE_COUNT_DOWN: {
                String str = null;
                boolean enough = FileUtil.getSDFreeMemory() / (1024 * 1024) < 4;
                if (enough) {
                    //空间不足，停止录屏
                    str = "存储空间不足";
                    stopAudio(null);
                    AudioUtil.stopAudio(str);
                    mRecordSeconds = 0;
                    break;
                }
                mRecordSeconds++;
                AudioUtil.recording(mRecordSeconds);
                mHandler.sendEmptyMessageDelayed(MSG_TYPE_COUNT_DOWN, 1000);
                break;
            }

        }
        return false;
    }

    public class AudioBinder extends Binder {
        public AudioService getAudioService() {
            return AudioService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(getMainLooper(), this);
        mMediaRecorder = new MediaRecorder();
    }

    public void startAudio() {
        isFinish = false;
        if (isRunning) {
            return;
        }
        if (recordDictory == null) {
            recordDictory = new File(AppConfig.filePathTemp);
        }
        if (!recordDictory.exists()) {
            recordDictory.mkdirs();
        }
        mRecordFilePath = recordDictory.getAbsolutePath() + File.separator + "audio-" + fileIndex + ".mp3";
        File recordFilePath= new File(mRecordFilePath);
        if(recordFilePath.exists()){
            recordFilePath.delete();
        }
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setOutputFile(mRecordFilePath);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isRunning = true;
            AudioUtil.startAudio();
            mHandler.sendEmptyMessageDelayed(MSG_TYPE_COUNT_DOWN, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopAudio(String newPath) {
        isRunning = false;
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
        }
        AudioUtil.stopAudio("");
        mHandler.removeMessages(MSG_TYPE_COUNT_DOWN);
        if(recordDictory!=null){
            composeAudio(recordDictory.getAbsolutePath() + File.separator + "audio.mp3");
            FileUtil.renameFile(recordDictory.getAbsolutePath(),AppConfig.filePath+File.separator+newPath);
        }
        mRecordSeconds = 0;
        recordDictory = null;
        isFinish = true;
    }

    public void composeAudio(String resultPath) {
        File resultFile = new File(resultPath);
        if(resultFile.exists()){
            resultFile.delete();
        }
        List<String> allFilePath = new ArrayList<>();
        File[] files = recordDictory.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".mp3")) {
                allFilePath.add(file.getAbsolutePath());
            }
        }
        FileUtil.uniteAMRFile(allFilePath,resultPath);
    }

    public void pauseAuido() {
        isRunning = false;
        mHandler.removeMessages(MSG_TYPE_COUNT_DOWN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mMediaRecorder.pause();
        } else {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mMediaRecorder = null;
        }

        AudioUtil.pauseAudio();
    }

    public void resumeAudio() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mMediaRecorder.resume();
            mHandler.sendEmptyMessageDelayed(MSG_TYPE_COUNT_DOWN, 1000);
        } else {
            fileIndex++;
            startAudio();
        }
        isRunning = true;
        AudioUtil.resumeAudio();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FileUtil.deleteFile(new File(AppConfig.filePathTemp),true);
    }
}
