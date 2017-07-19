package com.rssreader.mrlu.myrssreader.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rssreader.mrlu.myrssreader.Model.Rss.RSSFeed;
import com.rssreader.mrlu.myrssreader.Model.Rss.RSSHandler;
import com.rssreader.mrlu.myrssreader.Model.Sqlite.SQLiteHandle;
import com.rssreader.mrlu.myrssreader.R;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class InputRssLinkActivity extends AppCompatActivity {


    private EditText mEtRssLink;
    private ImageView mIvRssSearch;

    private RSSFeed feed = null;
    private InputSource isc;
    private RequestQueue mRequestQueue;
    public int rssItemCount = 0;

    public InputRssLinkActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_rss_link);
        initView();

        mIvRssSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("过程打印", "search已点击");

                final String link = mEtRssLink.getText().toString();

                Log.i("rssLink打印", link);

                //异步请求feed数据，并插入到数据库
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getFeed(link);
                    }
                }).start();

                //跳转到主界面
                Intent intent = new Intent(InputRssLinkActivity.this, mainView.class);
                startActivity(intent);

            }

            //region getfeed部分
            //获取feed
            private void getFeed(final String urlString) {

                try {

                    //新建SAX--xml解析工厂类
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser parser = factory.newSAXParser();
                    final XMLReader reader = parser.getXMLReader();
                    final RSSHandler rssHander = new RSSHandler();
                    reader.setContentHandler(rssHander);

                    //Volley请求xml部分
                    mRequestQueue = Volley.newRequestQueue(getBaseContext());
                    StringRequest mStringRequest = new StringRequest(urlString,
                            new com.android.volley.Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.i("respone:", response);


                                    Log.i("间隔", "请求执行完成");

                                    //转换respone由InputStream为InputSource类型
                                    InputStream is = new ByteArrayInputStream(response.getBytes());
                                    try {

                                        if (is != null) {
                                            isc = new InputSource(is);

                                            Log.i("IS", "IS转换完成");

                                            Log.i("IS", isc.toString());

                                            reader.parse(isc);
                                            feed = rssHander.getFeed();

                                            //累加各个feed的item数
//                                            rssItemCount += feed.Count();

                                            if (feed == null) {
                                                Log.e("feed", "feed为空");
                                            } else {
                                                Log.i("恭喜！", "feed通过");

                                                System.out.println("---------/n" + feed.Count() + "/n------");

                                                try {

                                                    SQLiteHandle sqLiteHandle = new SQLiteHandle(InputRssLinkActivity.this);
                                                    sqLiteHandle.insertFeed(feed.getName(), feed.getFeedDescription(), urlString);

                                                    sqLiteHandle.dbClose();

                                                    sqLiteHandle = null;
                                                } catch (Exception e) {
                                                    Log.e("sqllite插入问题", e.getMessage());
                                                }
                                            }
                                        } else
                                            Log.e("is转化问题", "is为空");

                                    } catch (IOException e) {
                                        Log.e("IO错误", e.getMessage());
                                    } catch (SAXException e) {
                                        Log.e("sax错误", e.getMessage());
                                    }
                                }
                            },

                            new com.android.volley.Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Log.e("respone error", "respone错误");
                                    Log.e("error", error.getMessage());
                                }
                            }

                    );

                    mRequestQueue.add(mStringRequest);

                } catch (Exception e) {
                    Log.e("获取feed", e.getMessage());

                }
            }

        });
    }

    private void initView() {
        mEtRssLink = (EditText) findViewById(R.id.et_rssLink);
        mIvRssSearch = (ImageView) findViewById(R.id.iv_rss_search);
    }

}
