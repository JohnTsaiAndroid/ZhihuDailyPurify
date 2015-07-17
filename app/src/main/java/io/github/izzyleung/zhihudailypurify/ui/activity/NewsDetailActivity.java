package io.github.izzyleung.zhihudailypurify.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import io.github.izzyleung.zhihudailypurify.R;
import io.github.izzyleung.zhihudailypurify.utils.HtmlParser;
import io.github.izzyleung.zhihudailypurify.utils.HttpUtils;

/**
 * Created by JohnTsai(mailto:johnnydtsai@gmail.com) on 15/7/13.
 */
public class NewsDetailActivity extends BaseActivity{

    private Context context;

    private String TAG = NewsDetailActivity.class.getName();
    private WebView webView;
    String questionUrl;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        context = this;
        layoutResID = R.layout.activity_newsdetail;
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        questionUrl = intent.getStringExtra("questionUrl");
        imageUrl = intent.getStringExtra("imageUrl");

        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(questionUrl);
        Log.d(TAG,questionUrl);

        GetDetailTask task = new GetDetailTask();
        try {
            String htmlPath = task.execute(questionUrl).get();
            Log.d(TAG, htmlPath);
            HtmlParser parser = new HtmlParser(htmlPath);
            parser.getcontent();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class GetDetailTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            return HttpUtils.getHtml(context,url);
        }
    }
}
