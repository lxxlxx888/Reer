package com.rssreader.mrlu.myrssreader.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Switch;

import com.jaeger.library.StatusBarUtil;
import com.rssreader.mrlu.myrssreader.R;

import butterknife.BindView;

import static com.rssreader.mrlu.myrssreader.R.color.appBaseColor;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.tb_nightMode)
    Switch tbNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, getResources().getColor(appBaseColor), 0);
        setContentView(R.layout.activity_settings);




        //从sp文件中取出isScrollToRead,判断是否显示初始页
        SharedPreferences sharedPreferences = getSharedPreferences("sp", Context.MODE_PRIVATE);
        boolean isScrollToRead = sharedPreferences.getBoolean("isScrollToRead", false);

//        if (isScrollToRead) {
//
//            tbNightMode.setChecked(true);
//        } else {
//            tbNightMode.setChecked(false);
//        }

    }
}
