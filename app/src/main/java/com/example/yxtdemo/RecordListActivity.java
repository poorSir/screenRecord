package com.example.yxtdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author szh
 * @Date 2019-09-24
 * @Description 录制列表
 */
public class RecordListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        File file = new File(AppConfig.filePath);
        File[] files = file.listFiles();
        final List<String> data =new ArrayList<>();
        for(File file1 :files){
            data.add(file1.getName());
        }
        final RecordListAdapter recordListAdapter  = new RecordListAdapter(this,data);
        recordListAdapter.setListener(new RecordListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(RecordListActivity.this,RecordPlayerActivity.class);
                intent.putExtra("title",data.get(position));
                startActivity(intent);
            }

            @Override
            public void OnItemLongClick(final int position) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecordListActivity.this)
                        .setTitle("是否删除该录屏")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FileUtil.deleteFile(new File(AppConfig.filePath+File.separator+data.get(position)),true);
                                data.remove(position);
                                recordListAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.create().show();
            }
        });
        recyclerView.setAdapter(recordListAdapter);
    }
}
