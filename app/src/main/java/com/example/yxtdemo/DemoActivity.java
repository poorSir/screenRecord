package com.example.yxtdemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author szh
 * @Date 2019-09-17
 * @Description
 */
public class DemoActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button;
    private Button stopButton;
    private TextView tv_time;
    private Button addEmpty;
    private Button addImage;
    private Button strokeWidth;
    private Button drawColor;
    private Button clear;
    private RecyclerView recyclerView;
    private Button startDraw;
    private TextView page;
    public static final int PERMISSION_RECORD =123;
    public static final int TAKECAMERA = 100;
    public static final int TAKEALBUM =101;
    private ServiceConnection mServiceConnection;
    private AudioService audioService;
    private List<ScreenRecordEntity> data = new ArrayList<>();
    private DemoAdapter adapter;
    private ForbitLayoutManager forbitLayoutManager;
    private int position;
    private  AlertDialog.Builder builder;
    private File photoFile;
    private PagerSnapHelper mPagerSnapHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        button = findViewById(R.id.button);
        stopButton = findViewById(R.id.stopButton);
        tv_time = findViewById(R.id.tv_time);
        addEmpty = findViewById(R.id.addEmpty);
        addImage = findViewById(R.id.addImage);
        startDraw = findViewById(R.id.startDraw);
        strokeWidth = findViewById(R.id.strokeWidth);
        drawColor = findViewById(R.id.drawColor);
        clear = findViewById(R.id.clear);
        page = findViewById(R.id.page);
        strokeWidth.setOnClickListener(this);
        drawColor.setOnClickListener(this);
        startDraw.setOnClickListener(this);
        addEmpty.setOnClickListener(this);
        addImage.setOnClickListener(this);
        button.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        clear.setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerView);
        CommonUtil.init(this);
        FileUtil.deleteFile(new File(AppConfig.filePathTemp),true);
        forbitLayoutManager =new ForbitLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(forbitLayoutManager);
        mPagerSnapHelper = new PagerSnapHelper();
        mPagerSnapHelper.attachToRecyclerView(recyclerView);
        adapter = new DemoAdapter(this,data);
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
                position =position1;
                CWFileUtil.writeACTSwitch(position1);
                refreshPage();
            }
        }));
        startAudioService();
        AudioUtil.addRecordListener(recordListener);
    }
    private void refreshPage(){
        if(data.size()>0){
            page.setText(position+1+"/"+data.size());
        }else{
            page.setText("");
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtil.deleteFile(new File(AppConfig.filePathTemp),true);
        unbindService(mServiceConnection);
        AudioUtil.clearAllListener();
    }
    AudioUtil.RecordListener recordListener = new AudioUtil.RecordListener() {
        @Override
        public void onStartRecord() {
            button.setText("暂停录制");
        }

        @Override
        public void onPauseRecord() {
            button.setText("继续录制");
        }

        @Override
        public void onResumeRecord() {
            button.setText("暂停录制");
        }

        @Override
        public void onStopRecord(String stopTip) {
            CWFileUtil.write(data);
            button.setText("开始录制");
            finish();
        }

        @Override
        public void onRecording(int mRecordSeconds) {
            CWFileUtil.setSeconds(mRecordSeconds);
            tv_time.setText(TimeUtil.convertTime(mRecordSeconds));
        }
    };

    private void startAudioService(){
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AudioService.AudioBinder audioBinder = ( AudioService.AudioBinder) service;
                audioService = audioBinder.getAudioService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        Intent intent = new Intent(this, AudioService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

    }
    public void checkPermission(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission =
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                            + ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            + ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                //动态申请
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_RECORD);
                return;
            }
            audioService.startAudio();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_RECORD) {
            int checkPermission =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                            + ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            + ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "没有权限", Toast.LENGTH_LONG).show();
            } else {
                audioService.startAudio();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent dataResult) {
        super.onActivityResult(requestCode, resultCode, dataResult);
        if(resultCode == RESULT_OK){
            if(requestCode == TAKECAMERA){
                ScreenRecordEntity imageRecordEntity =new ScreenRecordEntity();
                imageRecordEntity.setType("1");
                imageRecordEntity.setPath(photoFile.getAbsolutePath());
                data.add(imageRecordEntity);
                adapter.notifyDataSetChanged();
                refreshPage();
            }else if(requestCode == TAKEALBUM){
                Glide.with(this)
                        .load(dataResult.getData().toString())
                        .asBitmap()
                        .toBytes()
                        .into(new SimpleTarget<byte[]>() {
                            @Override
                            public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                                    photoFile = new File(AppConfig.filePathTemp);
                                    if(!photoFile.exists()){
                                        photoFile.mkdirs();
                                    }
                                    photoFile = new File(photoFile.getAbsolutePath()+ File.separator+System.currentTimeMillis()+".jpg");
                                try {
                                    FileOutputStream output = new FileOutputStream(photoFile);
                                    output.write(resource);
                                    //将bytes写入到输出流中
                                    output.close();
                                    ScreenRecordEntity imageRecordEntity =new ScreenRecordEntity();
                                    imageRecordEntity.setType("1");
                                    imageRecordEntity.setPath(photoFile.getAbsolutePath());
                                    data.add(imageRecordEntity);
                                    adapter.notifyDataSetChanged();
                                    refreshPage();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.startDraw:
                if("开始绘制".equals(startDraw.getText())){
                    if(data.size()>0){
                        data.get(position).setCanDraw(true);
                        forbitLayoutManager.setCanScrollHorizon(false);
                        startDraw.setText("暂停绘制");
                    }

                }else{
                    if(data.size()>0){
                        data.get(position).setCanDraw(false);
                        forbitLayoutManager.setCanScrollHorizon(true);
                        startDraw.setText("开始绘制");
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.addEmpty:
                ScreenRecordEntity screenRecordEntity =new ScreenRecordEntity();
                screenRecordEntity.setType("0");
                data.add(screenRecordEntity);
                adapter.notifyDataSetChanged();
                refreshPage();
                break;
            case R.id.addImage:
                final String[] items = {"拍照", "本地图片"};
                builder = new AlertDialog.Builder(this)
                        .setTitle("素材来源")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (takePictureIntent.resolveActivity(DemoActivity.this.getPackageManager()) != null) {
                                        //创建文件
                                            photoFile = new File(AppConfig.filePathTemp);
                                            if(!photoFile.exists()){
                                                photoFile.mkdirs();
                                            }
                                            photoFile = new File(photoFile.getAbsolutePath()+ File.separator+System.currentTimeMillis()+".jpg");
                                        //存入照片
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                        startActivityForResult(takePictureIntent, TAKECAMERA);
                                    }
                                }else if(i==1){
                                    Intent intentPic = new Intent(Intent.ACTION_GET_CONTENT);
                                    intentPic.addCategory(Intent.CATEGORY_OPENABLE);
                                    intentPic.setType("image/*");
                                    startActivityForResult(intentPic, TAKEALBUM);
                                }

                            }
                        });
                builder.create().show();
                break;
            case R.id.button:
                if(audioService!=null){
                    if(audioService.isFinish()){
                        checkPermission(DemoActivity.this);
                    }else{
                        if(audioService.isRunning()){
                            audioService.pauseAuido();
                        }else{
                            audioService.resumeAudio();
                        }

                    }
                }
                break;
            case R.id.stopButton:
                final EditText editText = new EditText(this);
                builder = new AlertDialog.Builder(this).setTitle("保存文件名").setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if("".equals(editText.getText().toString())){
                                    Toast.makeText(DemoActivity.this,"文件名不能为空",Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if(FileUtil.isExist(AppConfig.filePath+File.separator+editText.getText().toString())){
                                    Toast.makeText(DemoActivity.this,"文件夹已存在",Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if(audioService!=null){
                                    audioService.stopAudio(editText.getText().toString());
                                    button.setText("开始录制");
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                break;
            case R.id.clear:
                if(data.size()==0){
                    return;
                }
                //获取当前选中的itemView
                 View view = forbitLayoutManager.findViewByPosition(position);
                SimpleDoodleView doodleView =view.findViewById(R.id.doodleView);
                doodleView.clear();
                CWFileUtil.writeACTClear();
                break;
            case R.id.strokeWidth:
                if(data.size()==0){
                    return;
                }
                final String[] widthItems = {"10", "20","30","40"};
                builder = new AlertDialog.Builder(this)
                        .setTitle("素材来源")
                        .setItems(widthItems, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                View view = forbitLayoutManager.findViewByPosition(position);
                                SimpleDoodleView doodleView =view.findViewById(R.id.doodleView);
                                doodleView.setStrokeWidth(Integer.valueOf(widthItems[which]));
                            }
                        });
                builder.create().show();
                break;
            case R.id.drawColor:
                if(data.size()==0){
                    return;
                }
                final String[] drawColorItems = {"红","绿","蓝"};
                final int[] colors = {Color.RED,Color.GREEN,Color.BLUE};
                builder = new AlertDialog.Builder(this)
                        .setTitle("素材来源")
                        .setItems(drawColorItems, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                View view = forbitLayoutManager.findViewByPosition(position);
                                SimpleDoodleView doodleView =view.findViewById(R.id.doodleView);
                                doodleView.setDrawColor(colors[which]);
                            }
                        });
                builder.create().show();
                break;
        }
    }
}
