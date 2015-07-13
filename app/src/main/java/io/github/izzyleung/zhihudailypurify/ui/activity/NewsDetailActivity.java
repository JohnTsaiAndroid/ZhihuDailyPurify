package io.github.izzyleung.zhihudailypurify.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import io.github.izzyleung.zhihudailypurify.R;

/**
 * Created by JohnTsai(mailto:johnnydtsai@gmail.com) on 15/7/13.
 */
public class NewsDetailActivity extends BaseActivity{

    private WebView webView;
    String questionUrl;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutResID = R.layout.activity_newsdetail;
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        questionUrl = intent.getStringExtra("questionUrl");
        imageUrl = intent.getStringExtra("imageUrl");

        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(questionUrl);


    }
}
