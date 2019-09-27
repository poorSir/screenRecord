package com.example.yxtdemo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.io.IOException;

/**
 * @Author szh
 * @Date 2019-09-24
 * @Description
 */
public class RecordPlayerService extends Service implements MediaPlayer.OnPreparedListener, Handler.Callback, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    private MediaPlayerListener mediaPlayerListener;
    private Handler mHandler;
    private int seconds;
    private final int TIMESECONDS =100;

    public void setMediaPlayerListener(MediaPlayerListener mediaPlayerListener) {
        this.mediaPlayerListener = mediaPlayerListener;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mHandler = new Handler(getMainLooper(),this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            stop();
        }
    }
    public void onPause(){
        mediaPlayer.pause();
        mHandler.removeMessages(TIMESECONDS);
    }
    public void onResume(){
        mediaPlayer.start();
        mHandler.sendEmptyMessageDelayed(TIMESECONDS,1000);
    }
    public void seek(int seconds){
        this.seconds = seconds;
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            mHandler.sendEmptyMessageDelayed(TIMESECONDS,1000);
        }
        mediaPlayer.seekTo(seconds*1000);
    }
    public void stop(){
        mHandler.removeMessages(TIMESECONDS);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void start(String path){
        try {
            seconds =0;
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            if(mediaPlayerListener!=null){
                mediaPlayerListener.prepare(mediaPlayer.getDuration());
            }
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new RecordPlayerBinder();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mHandler.sendEmptyMessageDelayed(TIMESECONDS,1000);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case TIMESECONDS:
                seconds++;
                if(mediaPlayerListener!=null){
                    mediaPlayerListener.playing(seconds);
                }
                mHandler.sendEmptyMessageDelayed(TIMESECONDS,1000);
                break;
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mHandler.removeMessages(TIMESECONDS);
        if(mediaPlayerListener!=null){
            mediaPlayerListener.completion();
        }
    }

    class RecordPlayerBinder extends Binder{
        RecordPlayerService getRecordPlayerService(){
            return RecordPlayerService.this;
        }
    }
    interface MediaPlayerListener{
        void prepare(int seconds);
        void playing(int seconds);
        void completion();
    }
}
