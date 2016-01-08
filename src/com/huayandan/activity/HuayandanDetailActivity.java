package com.huayandan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.huayandan.adapter.HuanyandanDetailActivityAdapter;
import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;
import com.utils.StaticVar;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HuayandanDetailActivity extends Activity {

    private ListView listView;
    private Button takePhotoBtn;
    private String recordId;
    private String assayName;
    private String isNeedOcr = PandaGlobalName.TRUE;
    private Context context;
    private Handler mHandler;
    private static final int TAKE_BIG_PICTURE = 2;
    private static final int CROP_BIG_PICTURE = 3;
    private ArrayList<JSONObject> mDataList = new ArrayList<JSONObject>();
    private String tempName = "workupload.jpg";

    String result = null;

    private ImageView back;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_huayandan_detail);
        StaticVar.activityList.add(this);

        context = this;

        assayName = getIntent().getStringExtra(PandaGlobalName.ASSAY_NAME);
        recordId = getIntent().getStringExtra(PandaGlobalName.RECORD_ID);
        isNeedOcr = getIntent().getStringExtra(PandaGlobalName.IS_NEED_OCR);

//        ActionBar actionBar = getActionBar();
//        actionBar.setTitle(assayName);

        listView = (ListView) findViewById(R.id.huayandan_detail_listView);
        takePhotoBtn = (Button) findViewById(R.id.huayandan_detail_button);
//        if (isNeedOcr.equalsIgnoreCase(PandaGlobalName.TRUE)) {
//            takePhotoBtn.setText("一键拍照");
//        } else {
//            takePhotoBtn.setText("填化验单");
//        }
        title = (TextView) findViewById(R.id.title);
        title.setText(assayName);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        takePhotoBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (isNeedOcr.equalsIgnoreCase(PandaGlobalName.TRUE)) {
                    Message msg = new Message();
                    msg.what = TAKE_BIG_PICTURE;
                    mHandler.sendMessage(msg);
                } else {
                    try {
                        Log.i(PandaGlobalName.TAG, "assayName:" + assayName + ", RCRD_ID:" + recordId);
                        Intent intent = new Intent();
                        intent.putExtra(PandaGlobalName.ASSAY_NAME, assayName);
                        intent.putExtra(PandaGlobalName.RECORD_ID, recordId);
                        intent.setClass(HuayandanDetailActivity.this, HuayandanFillActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        });

        GetDetailItemTask task = new GetDetailItemTask(this, this.mDataList);
        task.execute();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case TAKE_BIG_PICTURE:
                        // call phone camera here
                        //Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap
                        Uri imageUri = Uri.fromFile(new File(PandaGlobalName.getPicSavePath(), tempName));
                        File file = new File(PandaGlobalName.getPicSavePath(), tempName);
                        if (file.exists())
                            file.delete(); // 如果有, 先删除, 避免影响后续图片写入
                        //  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Intent intent = new Intent(HuayandanDetailActivity.this, HuayandanCameraActivity.class);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, TAKE_BIG_PICTURE);//or TAKE_SMALL_PICTURE

                        break;
                    default:
                        return;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (StaticVar.isCamera == true) {

            File file = new File(PandaGlobalName.getPicSavePath(), "workupload.jpg");
            Intent intent = new Intent();
            intent.setClass(HuayandanDetailActivity.this, HuayandanResultActivity.class);
            intent.putExtra(PandaGlobalName.IMGURL, "workupload.jpg");// pass the imgurl
            intent.putExtra(PandaGlobalName.ASSAY_NAME, assayName);// pass the assayName
            intent.putExtra(PandaGlobalName.IS_NEED_OCR, "True"); // tell the result , we come from ocr
            intent.putExtra(PandaGlobalName.RECORD_ID, recordId);
            startActivity(intent);
            StaticVar.isCamera = false;

        }
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case TAKE_BIG_PICTURE:
//            	File file = new File(Environment.getExternalStorageDirectory(), "workupload.jpg");
//            	if(file.exists()){
//            		Toast.makeText(getApplicationContext(), "take pic result comming", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent();
//                    intent.setClass(HuayandanDetailActivity.this, HuayandanResultActivity.class);
//                    intent.putExtra(PandaGlobalName.IMGURL, "workupload.jpg");// pass the imgurl
//                    intent.putExtra(PandaGlobalName.ASSAY_NAME, assayName);// pass the assayName
//                    intent.putExtra(PandaGlobalName.IS_NEED_OCR, "True"); // tell the result , we come from ocr
//                    intent.putExtra(PandaGlobalName.RECORD_ID, recordId);
//                    startActivity(intent);
//            	}
//                break;
//            case CROP_BIG_PICTURE:
//                Toast.makeText(getApplicationContext(), "CROP_BIG_PICTURE done", Toast.LENGTH_LONG).show();
//                break;
//            default:
//                return;
//        }
//    }
    class GetDetailItemTask extends AsyncTask<Void, Integer, Integer> {

        private Context context;
        private ArrayList<JSONObject> mDataList;

        public GetDetailItemTask(Context context, ArrayList<JSONObject> mDataList) {
            this.context = context;
            this.mDataList = mDataList;
        }

		/*
         * UI 线程中, 在doInBackground 之前
		 * */

        @Override
        protected void onPreExecute() {
            listView.setVisibility(View.GONE);
        }

        /*
         * 后台线程中
         * */
        @Override
        protected Integer doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            BufferedReader in = null;
            String result = "";
            try {
                URL url = new URL(PandaGlobalName.getAssayItemsListForAppUrl(recordId));
                Log.i(PandaGlobalName.TAG, "huayandan detail url:" + url);
                URLConnection connection = url.openConnection();
                connection.connect();
                // 获取所有响应头字段
                Map<String, List<String>> map = connection.getHeaderFields();
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }

                //JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    mDataList.add(jsonArray.getJSONObject(i));
                }
                Log.i(PandaGlobalName.TAG, "Huayandan pager Json data parser finished");

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }


        /*
         * 运行在ui 线程中, 在doInBackground之后
         * */
        @Override
        protected void onPostExecute(Integer integer) {
            listView.setVisibility(View.VISIBLE);
            HuanyandanDetailActivityAdapter adapter = new HuanyandanDetailActivityAdapter(this.context, this.mDataList);
            listView.setAdapter(adapter);
        }

    }
}
