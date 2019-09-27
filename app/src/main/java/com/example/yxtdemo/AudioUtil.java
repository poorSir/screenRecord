package com.example.yxtdemo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author szh
 * @Date 2019-09-17
 * @Description
 */
public class AudioUtil {
    private static List<RecordListener> recordListeners = new ArrayList<>();

    public static void addRecordListener(RecordListener mRecordListener){
        recordListeners.add(mRecordListener);
    }
    public static void clearAllListener(){
        recordListeners.clear();
    }

    public static void startAudio(){
        if(recordListeners.size()>0){
            for(RecordListener listener:recordListeners ){
                listener.onStartRecord();
            }
        }
    }
    public static void stopAudio(String stopTip){
        if(recordListeners.size()>0){
            for(RecordListener listener:recordListeners ){
                listener.onStopRecord(stopTip);
            }
        }
    }
    public static void pauseAudio(){
        if(recordListeners.size()>0){
            for(RecordListener listener:recordListeners ){
                listener.onPauseRecord();
            }
        }
    }
    public static void resumeAudio(){
        if(recordListeners.size()>0){
            for(RecordListener listener:recordListeners ){
                listener.onResumeRecord();
            }
        }
    }
    public static void recording(int  time){
        if(recordListeners.size()>0){
            for(RecordListener listener:recordListeners ){
                listener.onRecording(time);
            }
        }
    }

    public interface RecordListener{
        void onStartRecord();
        void onPauseRecord();
        void onResumeRecord();
        void onStopRecord(String stopTip);
        void onRecording(int time);
    }
}
