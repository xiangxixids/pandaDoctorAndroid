package com.myReocrd.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.chunyuAdvisory.activity.RecordFreeConsultActivity;
import com.huayandan.activity.ImageShower;
import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;
import com.utils.StaticVar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class RecordDetailActivity extends Activity {

    private WebView webView;
    private String mUrl;
    private TextView textView;
    private ImageView imageView;
    private String newPicName;
    private ImageView back;
    private TextView title;
    private Button freetext2;

    private ProgressDialog progressDialog;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StaticVar.activityList.add(this);


        Intent intent = getIntent();
        String assayId = intent.getStringExtra(PandaGlobalName.ASSAY_ID);
        String assayResult = intent.getStringExtra(PandaGlobalName.ASSAY_RESULT);
        String assayName = intent.getStringExtra(PandaGlobalName.ASSAY_NAME);
        String gmtCreate = intent.getStringExtra(PandaGlobalName.GMT_CREATE);

        AssetManager assets = getAssets();
        InputStream is = null;

        try {
            is = assets.open("14.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        SharedPreferences user = getSharedPreferences(PandaGlobalName.UserInfo.info, 0);
        String tel = user.getString(PandaGlobalName.UserInfo.phone, "");
        newPicName = PandaGlobalName.getPicNewName(gmtCreate, tel, assayId);

        File file = new File(PandaGlobalName.getPicSavePath(), newPicName);
        Uri uri;
        Bitmap bitmap;
        if (file.exists()) // 如果存在的话, 就显示拍照的图片
        {

            setContentView(R.layout.record_result_activity);
            webView = (WebView) findViewById(R.id.record_result_web_view);
            imageView = (ImageView) findViewById(R.id.record_detail_activity_text_img);
            textView = (TextView) findViewById(R.id.record_detail_activity_text);

            uri = Uri.fromFile(file);
            bitmap = decodeUriAsBitmap(uri);
            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent();
                    intent.setClass(RecordDetailActivity.this, ImageShower.class);
                    intent.putExtra(PandaGlobalName.IMGURL, newPicName);
                    startActivity(intent);
                }

            });
            imageView.setImageBitmap(bitmap);
            textView.setText(assayName + " " + gmtCreate);
            webView = (WebView) findViewById(R.id.record_result_web_view);

        } else {    // 否则, 就显示pandadoctor的logo
            BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeStream(is, null, options);
            setContentView(R.layout.record_result_activity_no_img);
            webView = (WebView) findViewById(R.id.record_result_web_view_no_img);
        }
        context = this;
        webView.setWebViewClient(new PandaDoctorWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        title = (TextView) findViewById(R.id.title);
        title.setText(assayName);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        freetext2 = (Button) findViewById(R.id.freetext2);
        freetext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticVar.ask = true;
                Intent intent = new Intent(RecordDetailActivity.this, RecordFreeConsultActivity.class);
                startActivity(intent);
                finish();
            }
        });


//    	Uri uri = Uri.fromFile();
//    	Bitmap bitmap = decodeUriAsBitmap(uri);

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);

        if (mUrl == null) {
            mUrl = PandaGlobalName.getResultHistoryForAppUrl(assayResult, assayId);
        }
//        webView.loadUrl(mUrl);
//        webView.reload();
        loadUrl(mUrl);
        super.onCreate(savedInstanceState);
    }

    public void loadUrl(String url) {
        if (webView != null) {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            });
            webView.loadUrl(url);
            progressDialog = ProgressDialog.show(context, "", "页面加载中, 请稍后...");
            webView.reload();
        }
    }

    private Bitmap decodeUriAsBitmap(Uri uri) {

        Bitmap bitmap = null;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 2;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, bitmapOptions);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;

    }


    private class PandaDoctorWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mUrl = url;
            view.loadUrl(url);
            return true;
        }
    }


}
