package com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.pandadoctor.pandadoctor.R;
import com.utils.StaticVar;

import java.util.Set;

/**
 * Created by Administrator on 15-7-23.
 */
public class NotifycationActivity extends Activity {
    private WebView webView;
    private ProgressBar pd;
    private ImageView back;
    private TextView title;

    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notifycation_layout);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if (StaticVar.isOpenActivity == false) {
                    Intent intent = new Intent(NotifycationActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        title = (TextView) findViewById(R.id.title);
        title.setText("健康讲堂");


        Bundle bun = getIntent().getExtras();
        if (bun != null) {
            Set<String> keySet = bun.keySet();
            for (String key : keySet) {
                mUrl = bun.getString(key);
                Log.e("sss", mUrl);
            }
        }

        pd = (ProgressBar) findViewById(R.id.progressbar);
        webView = (WebView) findViewById(R.id.webview);

        webView.setWebViewClient(new PandaDoctorWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new MyWebViewClient());

        if (mUrl != null) {
            webView.loadUrl(mUrl);
        }


    }


    private class PandaDoctorWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            pd.setVisibility(View.VISIBLE);
            mUrl = url;
            view.loadUrl(url);
            return true;
        }


    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            pd.setProgress(newProgress);
            if (newProgress == 100) {
                pd.setVisibility(View.GONE);
            }
        }
    }
}
