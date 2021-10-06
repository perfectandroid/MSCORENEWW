package com.creativethoughts.iscore;

import android.app.ProgressDialog;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.creativethoughts.iscore.Helper.Common;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Viewstatement extends AppCompatActivity {
    String TAG="Webview";
    ProgressDialog progressDialog;
    String url;
    String attachedfile;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

      //  wv1=(WebView)findViewById(R.id.webView);
       // wv1.setWebViewClient(new MyBrowser());

       // attachedfile = Common.getBaseUrl()+"/Mscore/Statement/ASD7.pdf";
        attachedfile = getIntent().getStringExtra("docx");
       // wv1.loadUrl(url);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setDomStorageEnabled(true);
        /*webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);*/
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);


        progressDialog = new ProgressDialog(Viewstatement.this, R.style.Progress);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
        progressDialog.setMessage("Loading please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(getResources()
                .getDrawable(R.drawable.progress));
        progressDialog.show();
        Log.e("akn","image"+attachedfile);
//        if(attachedfile.indexOf(".jpg")!=-1||attachedfile.indexOf(".jpeg")!=-1||attachedfile.indexOf(".png")!=-1||attachedfile.indexOf(".gif")!=-1) {
//            webView.setWebViewClient(new SSLTolerentWebViewClient());
//            webView.loadUrl(attachedfile);
//        }else {
//            String url = "http://docs.google.com/viewer?embedded=true&url=" + attachedfile;
//            webView.setWebViewClient(new SSLTolerentWebViewClient());
//            webView.loadUrl(url);
//        }
        String url = "http://docs.google.com/viewer?embedded=true&url=" + attachedfile;
        webView.setWebViewClient(new SSLTolerentWebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (webView.getProgress() == 100) {
                    progressDialog.dismiss();
                    webView.setVisibility(View.VISIBLE);
                }
            }
        });
        webView.loadUrl(url);
        new Handler().postDelayed(new Runnable() {
            public void run() {
              //  progressDialog.dismiss();
            }
        }, 3000);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class SSLTolerentWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }

}
