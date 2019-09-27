package com.example.yxtdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.yxtdemo.CWEntity.CW;
import com.example.yxtdemo.CWEntity.CWACT;
import com.example.yxtdemo.CWEntity.CWPage;
import com.example.yxtdemo.CWEntity.TextUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author szh
 * @Date 2019-09-24
 * @Description
 */
public class RecordPlayerActivity extends AppCompatActivity {
    private RecordPlayerService recordPlayerService;
    private String title;
    private ServiceConnection serviceConnection;
    private TextView currentTime;
    private TextView totalTime;
    private SeekBar progressBar;
    private int position;
    private int duration;
    private ForbitLayoutManager forbitLayoutManager;
    private RecyclerView recyclerView;
    private PagerSnapHelper mPagerSnapHelper;
    private DemoAdapter adapter;
    private List<ScreenRecordEntity> data = new ArrayList<>();
    private CW cw;
    private boolean isSeek;
    private List<View> recyclerViewList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_play);
        title = getIntent().getStringExtra("title");
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);
        progressBar = findViewById(R.id.progress);
        recyclerView = findViewById(R.id.recyclerView);
        forbitLayoutManager = new ForbitLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        forbitLayoutManager.setCanScrollHorizon(false);
        recyclerView.setLayoutManager(forbitLayoutManager);
        mPagerSnapHelper = new PagerSnapHelper();
        mPagerSnapHelper.attachToRecyclerView(recyclerView);
        cw = CWFileUtil.read(AppConfig.filePath + File.separator + title + File.separator + "data.cw");
        convertCwData(cw);
        adapter = new DemoAdapter(this, data);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerViewPageChangeListenerHelper(mPagerSnapHelper, new RecyclerViewPageChangeListenerHelper.OnPageChangeListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            }

            @Override
            public void onPageSelected(int position1) {

            }
        }));
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isSeek && recordPlayerService != null) {
                    int seconds =(int) (1.0 * progress / 100 * duration);
                    recordPlayerService.seek(seconds);
                    convertCWACT(cw, seconds,true);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeek = false;
            }
        });
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(position == recyclerViewList.size()){
                    View view = forbitLayoutManager.findViewByPosition(position);
                    recyclerViewList.add(view);
                }
                if(recyclerViewList.size() == data.size()){
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        startConnectService();

    }

    private void convertCwData(CW cw) {
        List<CWPage> cwPages = cw.getPAGES();
        for (CWPage cwPage : cwPages) {
            ScreenRecordEntity screenRecordEntity = new ScreenRecordEntity();
            screenRecordEntity.setCanDraw(false);
            if (TextUtil.isEmpty(cwPage.getBackground().getRgba())) {
                screenRecordEntity.setType("1");
                screenRecordEntity.setPath(AppConfig.filePath + File.separator + title + File.separator + cwPage.getBackground().getUrl());
            } else {
                screenRecordEntity.setType("0");
            }
            data.add(screenRecordEntity);
        }
    }

    private void startConnectService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                RecordPlayerService.RecordPlayerBinder recordPlayerBinder = (RecordPlayerService.RecordPlayerBinder) service;
                recordPlayerService = recordPlayerBinder.getRecordPlayerService();
                recordPlayerService.setMediaPlayerListener(listener);
                recordPlayerService.start(AppConfig.filePath + File.separator + title + File.separator + "audio.mp3");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intent = new Intent(this, RecordPlayerService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private RecordPlayerService.MediaPlayerListener listener = new RecordPlayerService.MediaPlayerListener() {

        @Override
        public void prepare(int seconds) {
            duration = seconds / 1000;
            totalTime.setText(TimeUtil.convertTime(duration));
        }

        @Override
        public void playing(int seconds) {
            if (seconds > duration) {
                seconds = duration;
            }
            currentTime.setText(TimeUtil.convertTime(seconds));
            progressBar.setProgress((int) (1.0 * seconds / duration * 100));
            convertCWACT(cw, seconds,false);
        }

        @Override
        public void completion() {

        }
    };

    private void convertCWACT(CW cw, int seconds,boolean isSeek) {
        List<CWACT> cwacts = cw.getACT();
        if(isSeek){
            position =0;
            forbitLayoutManager.scrollToPosition(position);
            forbitLayoutManager.setStackFromEnd(true);
            for(int i=0;i<recyclerViewList.size();i++){
                View view = recyclerViewList.get(i);
                if(view!=null){
                    SimpleDoodleView doodleView = view.findViewById(R.id.doodleView);
                    doodleView.clear();
                }
            }
        }
        for (CWACT cwact : cwacts) {
            int time = cwact.getTime();
            if(isSeek?time > seconds:time != seconds){
                continue;
            }
            if ("switch".equals(cwact.getAction())) {
                position = cwact.getCwSwitch().getIndex();
                forbitLayoutManager.scrollToPosition(position);
                forbitLayoutManager.setStackFromEnd(true);
            } else if ("line".equals(cwact.getAction())) {
                if(position>recyclerViewList.size()-1){
                    continue;
                }
                View view = recyclerViewList.get(position);
                if(view!=null){
                    SimpleDoodleView doodleView = view.findViewById(R.id.doodleView);
                    doodleView.setDrawPath(cwact.getLine());
                }
            } else if ("clear".equals(cwact.getAction())) {
                if(position>recyclerViewList.size()-1){
                    continue;
                }
                View view = recyclerViewList.get(position);
                if(view!=null){
                    SimpleDoodleView doodleView = view.findViewById(R.id.doodleView);
                    doodleView.clear();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
