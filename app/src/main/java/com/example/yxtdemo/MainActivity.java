package com.example.yxtdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * @Author szh
 * @Date 2019-09-24
 * @Description
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button record;
    private Button recordList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        record = findViewById(R.id.record);
        recordList = findViewById(R.id.recordList);
        record.setOnClickListener(this);
        recordList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record:
                Intent intent = new Intent(this,DemoActivity.class);
                startActivity(intent);
                break;
            case R.id.recordList:
                Intent recordlistIntent = new Intent(this,RecordListActivity.class);
                startActivity(recordlistIntent);
                break;
        }
    }
}
