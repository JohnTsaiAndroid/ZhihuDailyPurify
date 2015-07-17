package io.github.izzyleung.zhihudailypurify.utils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;

/**
 * Created by JohnTsai(mailto:johnnydtsai@gmail.com) on 15/7/16.
 */
public class HtmlParser {

    private static final String TAG = "HtmlParser";

    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String baseUrl = "http://www.zhihu.com/";

    private String filePath;

    public HtmlParser(String filePath){
        this.filePath = filePath;
    }
    public String getcontent(){
        File html = new File(filePath);
        if(!html.exists()){
            return "error";
        }
        try {
            Document document = Jsoup.parse(html,"UTF-8",baseUrl);
            Element title = document.select(TITLE).first();
            Element body = document.select(BODY).first();
            Element answer1 = body.getElementById("zh-single-question-page");
            Element answer2 = answer1.select("div").first();
            Element answer3 = answer2.select("div").first();
            Element answer4 = answer3.getElementById("zh-question-answer-wrap");
            Element answer5 = answer4.select("div").first();
            Element answer6 = answer5.getElementsByClass("zm-item-rich-text").first();

            Log.d(TAG, "title" + title.text() + "answer:" + answer6.html());
            return title.data();
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
}
