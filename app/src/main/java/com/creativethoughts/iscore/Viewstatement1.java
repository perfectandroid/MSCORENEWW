package com.creativethoughts.iscore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Viewstatement1 extends AppCompatActivity {
    String TAG = "Webview";
    ProgressDialog progressDialog;
    String url;
    String attachedfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        final Activity activity = this;


        attachedfile = getIntent().getStringExtra("docx");
        // wv1.loadUrl(url);
       // WebView webView = (WebView) findViewById(R.id.webView);
       /// webView.getSettings().setJavaScriptEnabled(true);
       // webView.loadUrl(attachedfile);
   /*     WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);


       *//* progressDialog = new ProgressDialog(Viewstatement.this, R.style.Progress);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
        progressDialog.setMessage("Loading please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(getResources()
                .getDrawable(R.drawable.progress));
        progressDialog.show();*//*
        Log.e("akn","image"+attachedfile);
//        if(attachedfile.indexOf(".jpg")!=-1||attachedfile.indexOf(".jpeg")!=-1||attachedfile.indexOf(".png")!=-1||attachedfile.indexOf(".gif")!=-1) {
//            webView.setWebViewClient(new SSLTolerentWebViewClient());
//            webView.loadUrl(attachedfile);
//        }else {
//            String url = "http://docs.google.com/viewer?embedded=true&url=" + attachedfile;
//            webView.setWebViewClient(new SSLTolerentWebViewClient());
//            webView.loadUrl(url);
//        }
        String url1 = "http://docs.google.com/viewer?embedded=true&url=" + attachedfile;
        //webView.setWebViewClient(new SSLTolerentWebViewClient());
        webView.setWebViewClient(new SSLTolerentWebViewClient(){

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }
            public void onLoadResource (WebView view, String url) {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setMessage("Loading please wait...");
                    progressDialog.show();
                }
            }
            public void onPageFinished(WebView view, String url) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
                                 });
        webView.loadUrl(url);

      *//*  new Handler().postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        }, 3000);
*//*
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
    }*/
    }
}
