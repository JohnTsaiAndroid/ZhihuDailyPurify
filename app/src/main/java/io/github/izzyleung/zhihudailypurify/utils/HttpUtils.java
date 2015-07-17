package io.github.izzyleung.zhihudailypurify.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {

    static AsyncHttpClient client = new AsyncHttpClient();

    /**
     * 获得某网址的页面源代码
     * @param url
     * @return
     */
    public static String getUrlStr(String url){
        final String [] result = {"error"};
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    result[0] = new String(responseBody,"UTF-8");
                    Log.d("cj","length:"+result[0].length()+result[0]);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
       return result[0];
    }

    public static String getHtml(final Context context,final String url){

        String parseUrl = url.replace("//","").replace("/", "");
        Log.d("cj","parseUrl"+parseUrl);
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(parseUrl, Context.MODE_PRIVATE);

        String strUrl = sharedPreferences.getString(url,null);
        if(strUrl==null){
            return downloadHtml(context,url);
        }else{
            return strUrl;
        }
    }

    private static String downloadHtml(Context context, String strUrl) {

        String parseUrl = strUrl.replace("//","").replace("/","");
        Log.d("cj","parseUrl"+parseUrl);

        SharedPreferences sharedPreferences = context
                .getSharedPreferences(parseUrl, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        InputStream is = null;
        OutputStream os = null;

        HttpURLConnection conn = null;

        try {
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            if (conn.getResponseCode()!=HttpURLConnection.HTTP_OK) {
                return "error";
            }

            if(!checkSDCard()){
                return "nosdcard";
            }

            is = conn.getInputStream();

            final File file = new File(Environment.getExternalStorageDirectory().getPath(),parseUrl+".html");
            os = new FileOutputStream(file);

            byte data[] = new byte[4096];
            int count;
            while ((count = is.read(data))!=-1){
                os.write(data,0,count);
            }
            editor.putString(strUrl,file.getAbsolutePath()).apply();
            return file.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }finally {
                try {
                    if(os!=null)
                         os.close();
                    if(is!=null)
                        is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(conn!=null)
                conn.disconnect();

        }
    }
    public static boolean checkSDCard(){
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}
