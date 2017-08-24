package com.rssreader.mrlu.myrssreader.Controller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.app.MAppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.rssreader.mrlu.myrssreader.R;
import com.rssreader.mrlu.myrssreader.Test.unReadFragment11;
import com.rssreader.mrlu.myrssreader.Controller.fragment.starredFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static com.rssreader.mrlu.myrssreader.R.color.md_teal_a700_color_code;

public class mainView extends MAppCompatActivity implements View.OnClickListener {

    //声明ViewPager
    private ViewPager mNoScrollViewPager;
    //适配器
    private FragmentPagerAdapter mAdapter;
    //装载Fragment的集合
    private List<Fragment> mFragments;
    //四个Tab对应的布局
    private LinearLayout mTabUnread;
    private LinearLayout mTabStarred;
    //四个Tab对应的ImageButton
    private ImageButton mImgWeixin;
    private ImageButton mImgFrd;

    String rssUrl;

    //0为day，1为night
    public static int Swith_Mode = 1;

    public ImageView nightSwith;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //取消actionBar
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);

        super.onCreate(savedInstanceState);

//        Intent intent = getIntent();
//        int result = intent.get

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        StatusBarUtil.setColor(this, getResources().getColor(md_teal_a700_color_code), 0);

        //ButterKnife绑定
        ButterKnife.bind(this);

        Window window = getWindow();

        mNoScrollViewPager = (ViewPager) findViewById(R.id.id_noviewpager);
        nightSwith = (ImageView) findViewById(R.id.iv_night_swith);
        ImageView imgAdd = (ImageView) findViewById(R.id.iv_add);
        ImageView ivUpdate = (ImageView) findViewById(R.id.iv_update);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_feedList);
        setSupportActionBar(toolbar);

        nightSwith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击切换日间/夜间图标
                switch (Swith_Mode) {
                    case 0:
                        nightSwith.setImageResource(R.drawable.swich_day);
                        Swith_Mode = 1;
//                        mainView_test.this.getApplication().setTheme(R.style.NightTheme);
//                        recreate();
                        Toast.makeText(mainView.this, "已切换为夜间模式", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        nightSwith.setImageResource(R.drawable.swich_night);
                        Swith_Mode = 0;
//                        mainView_test.this.getApplication().setTheme(R.style.DayTheme);
//                        recreate();
                        Toast.makeText(mainView.this, "已切换为日间模式", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        //处理点击add按钮点击事件
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainView.this, InputRssLinkActivity.class);
                startActivityForResult(intent, 1);
//                finish();
            }
        });

        //处理toolbar的menu的点击事件
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent intent = new Intent();
                switch (item.getOrder()){
                    case 1:
                        intent.setClass(mainView.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(mainView.this, AboutActivity.class);
                        startActivity(intent);
                        break;
                }

                return true;
            }
        });

        initView();//初始化控件
        initEvent();//初始化事件
        initData();//初始化数据
        selectTab(0);
    }

    private void initData() {
        mFragments = new ArrayList<>();
        //将两个Fragment加入集合中
        mFragments.add(new unReadFragment11());
        mFragments.add(new starredFragment());

        //初始化适配器
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {//从集合中获取对应位置的Fragment
                return mFragments.get(position);
            }

            @Override
            public int getCount() {//获取集合中Fragment的总数
                return mFragments.size();
            }

        };

        //不要忘记设置ViewPager的适配器
        mNoScrollViewPager.setAdapter(mAdapter);
        //设置ViewPager的切换监听
        mNoScrollViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            //页面滚动事件
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //页面选中事件
            @Override
            public void onPageSelected(int position) {
                //设置position对应的集合中的Fragment
                mNoScrollViewPager.setCurrentItem(position);
                resetImgs();
                selectTab(position);
            }

            @Override
            //页面滚动状态改变事件
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initEvent() {
        //设置两个Tab的点击事件
        mTabUnread.setOnClickListener(this);
        mTabStarred.setOnClickListener(this);
    }

    //初始化控件
    private void initView() {
        mNoScrollViewPager = (ViewPager) findViewById(R.id.id_noviewpager);

        mTabUnread = (LinearLayout) findViewById(R.id.id_tab_weixin);
        mTabStarred = (LinearLayout) findViewById(R.id.id_tab_frd);

        mImgWeixin = (ImageButton) findViewById(R.id.id_tab_weixin_img);
        mImgFrd = (ImageButton) findViewById(R.id.id_tab_frd_img);
    }

    @Override
    public void onClick(View v) {
        //先将两个ImageButton置为灰色
        resetImgs();

        //根据点击的object不同来处理点击事件
        switch (v.getId()) {

            //两个tab
            case R.id.id_tab_weixin:
                selectTab(0);
                break;
            case R.id.id_tab_frd:
                selectTab(1);
                break;
        }
    }

    private void selectTab(int i) {
        //根据点击的Tab设置对应的ImageButton为绿色
        switch (i) {
            case 0:
                //mImgWeixin.setImageResource(R.drawable.feed_read);
                mTabStarred.setBackgroundColor(Color.parseColor("#393a3f"));
                mTabUnread.setBackgroundColor(getResources().getColor(md_teal_a700_color_code));
                break;
            case 1:
                //EventBus发送消息
//                EventBus.getDefault().post(rssUrl);
                //mImgFrd.setImageResource(R.drawable.long_press_starred);
                mTabUnread.setBackgroundColor(Color.parseColor("#393a3f"));
                mTabStarred.setBackgroundColor(getResources().getColor(md_teal_a700_color_code));
                break;
        }
        //设置当前点击的Tab所对应的页面
        mNoScrollViewPager.setCurrentItem(i);
    }

    //将两个ImageButton设置为灰色
    private void resetImgs() {
        mTabStarred.setBackgroundColor(Color.parseColor("#393a3f"));
        mTabStarred.setBackgroundColor(Color.parseColor("#393a3f"));

    }

    //创建Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rsslist_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    //双击back健退出
    private int sum = 0;
    @Override
    public void onBackPressed() {
        Log.i("onBackPressed", "点击了back键");
        Toast.makeText(mainView.this, "再按一次退出Reer", Toast.LENGTH_SHORT);

        sum++;
        Log.i("onBackPressed", "sum = " + sum);
        switch (sum){
            case 1:
                Toast.makeText(mainView.this, "再按一次退出Reer", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                finish();
                break;
            default:
                break;

        }

    }

}

