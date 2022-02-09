package com.creativethoughts.iscore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.creativethoughts.iscore.Helper.Config;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Webview extends AppCompatActivity {
    private WebView wv1;
    String url;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

     //   wv1=(WebView)findViewById(R.id.webView);
       // wv1.setWebViewClient(new MyBrowser());

        SharedPreferences pref =getApplicationContext().getSharedPreferences(Config.SHARED_PREF7, 0);
        String BASE_URL=pref.getString("baseurl", null);
         url = BASE_URL+"/Mscore/Statement/ASD7.pdf";
        wv1.loadUrl(url);

      /* wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv1.loadUrl(url);*/
    }

  /*  private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }*/
}
