package com.healthClassRoom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;

/**
 * 健康讲堂Fragment的界面
 *
 * @author virgo
 */
public class HealthFragment extends Fragment {

    WebView webView;
    private ProgressBar pd;
    String mUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.health_fragment, container,
                false);


        pd = (ProgressBar) rootView.findViewById(R.id.progressbar);
        webView = (WebView) rootView.findViewById(R.id.webview);

        webView.setWebViewClient(new PandaDoctorWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new MyWebViewClient());

        if (mUrl == null) {
            mUrl = PandaGlobalName.Server.mUrl;
        }
        webView.loadUrl(mUrl);

        return rootView;
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


