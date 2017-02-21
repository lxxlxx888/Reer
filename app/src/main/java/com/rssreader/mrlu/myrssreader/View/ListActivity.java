package com.rssreader.mrlu.myrssreader.View;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.rssreader.mrlu.myrssreader.Model.RSSFeed;
import com.rssreader.mrlu.myrssreader.Model.RSSHandler;
import com.rssreader.mrlu.myrssreader.Model.RSSItem;
import com.rssreader.mrlu.myrssreader.R;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public final String RSS_URL = "http://free.apprcn.com/category/ios/feed/";
    public final String tag = "RSSReader";
    private RSSFeed feed = null;

    InputStream is;
    OkHttpClient mOkHttpClient;
    InputSource isc;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            try {

                //新建SAX--xml解析工厂类
                SAXParserFactory factory = SAXParserFactory.newInstance();

                SAXParser parser = factory.newSAXParser();

                XMLReader reader = parser.getXMLReader();

                RSSHandler rssHander = new RSSHandler();

                Response response = (Response) msg.obj;
                System.out.println(response.body().string());

                is = response.body().byteStream();

                Log.i("间隔", "请求执行完成");

                try {
                    if (is != null) {
                        isc = new InputSource(is);

                        Log.e("IS", "IS转换完成");

                        Log.e("IS", isc.toString());

                        reader.parse(isc);

                        feed = rssHander.getFeed();

                    } else {
                        Log.e("is", "is为空");
                    }

                    showListView();
                } catch (Exception e) {
                    Log.e("is转换", e.getMessage());
                }

            } catch (Exception e) {
                Log.e("hander", e.toString());
            }
        }


    };


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_list);

            getFeed(RSS_URL);


        }

        //获取feed
        private void getFeed(String urlString) {
            try {

                final URL url = new URL(urlString);

//            //新建SAX--xml解析工厂类
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//
//            SAXParser parser = factory.newSAXParser();
//
//            XMLReader reader = parser.getXMLReader();
//
//            RSSHandler rssHander = new RSSHandler();


                new Thread() {
                    @Override
                    public void run() {
                        ///okhttp3 网络连接

                        mOkHttpClient = new OkHttpClient();
                        Request.Builder requestBuilder = new Request.Builder().url(url);

                        //可以省略，默认是GET请求
                        requestBuilder.method("GET", null);
                        Request request = requestBuilder.build();
                        Call mcall = mOkHttpClient.newCall(request);
                        mcall.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("netWorkFailure", e.toString());

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                if (response.isSuccessful()) {


                                    Log.i("responeSuccess", "返回成功了");

                                    String responeNode = response.body().string();
                                    Log.i("body", "str4---" + responeNode);


                                    Message msg = Message.obtain();
                                    msg.obj = response;
//                                    msg.what = 1;//区分哪一个线程发送的消息
                                    handler.sendMessage(msg);

//                                    is = response.body().byteStream();


                                } else {
                                    System.out.println("返回失败");
                                }
                            }

                        });

                    }
                }.start();

//            SystemClock.sleep(2000);

//            Log.i("间隔", "请求执行完成");
//
//            try {
//                if (is != null) {
//                    isc = new InputSource(is);
//
//                    Log.e("IS", "IS转换完成");
//                } else {
//                    Log.e("is", "is为空");
//                }
//
//
//                reader.parse(isc);
//
//
//                Log.e("saxJiexi", "chenggong");
//            } catch (SAXException e) {
//                Log.i("SAX", e.toString());
//            }
//            return rssHander.getFeed();

            } catch (Exception e) {
                Log.e("e", e.toString());
            }
        }

        //列表显示获取的RSS
        private void showListView() {
            ListView itemlist = (ListView) findViewById(R.id.lv_rssList);
            if (feed == null) {
                setTitle("访问的RSS无效");
                return;
            } else {
                setTitle(tag);
            }

            SimpleAdapter adapter = new SimpleAdapter(this, feed.getAllItemsForListView(),
                    android.R.layout.simple_list_item_2, new String[]{
                    RSSItem.TITLE, RSSItem.PUBDATE
            },
                    new int[]{
                            android.R.id.text1, android.R.id.text2
                    });

            itemlist.setAdapter(adapter);
            itemlist.setOnItemClickListener(this);
            itemlist.setSelection(0);
        }

        //处理列表的单击事件
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            Intent itemIntent = new Intent(this, ShowDescriptionActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("title", feed.getItem(position).getTitle());
            bundle.putString("description", feed.getItem(position).getTitle());
            bundle.putString("link", feed.getItem(position).getTitle());
            bundle.putString("pubdate", feed.getItem(position).getTitle());

            itemIntent.putExtra("android.intent.extra.rssItem", bundle);

            startActivityForResult(itemIntent, 0);
        }


//        class MyTask extends AsyncTask<URL, Void, String> {
//
//            @Override
//            protected String doInBackground(URL... params) {
//
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//
//            }
//        }

    }