package com.myReocrd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.gobalModule.PandaLoginActivity;
import com.myReocrd.adapter.RecordActivityAdapter;
import com.myReocrd.fragment.RecordFragment;
import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;
import com.utils.StaticVar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class RecordActivity extends Activity {
    public static final String TIME_TITLE = "拍单时间";
    public static final String HUA_YAN_DAN = "化验单";
    private TextView titleOne;
    private TextView titleTwo;
    private ArrayList<JSONObject> mDataList = new ArrayList<JSONObject>();
    private ListView listView;
    private ProgressBar progressBar;

    private ImageView back;
    private TextView title;
    private Button freetext2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // TODO Auto-generated method stub
        setContentView(R.layout.record_activity);
        StaticVar.activityList.add(this);
        String title1 = getIntent().getStringExtra("title");
        progressBar = (ProgressBar) findViewById(R.id.recordActivity_progressBar);
        //顶部两个title

        title = (TextView) findViewById(R.id.title);
        title.setText(title1);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        listView = (ListView) findViewById(R.id.record_activity_listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub

                Log.i(PandaGlobalName.TAG, "click position:" + position);
                JSONObject mobject = mDataList.get(position);

                try {
                    Intent intent = new Intent();
                    //获得所属化验单
                    String assayId = mobject.getString(PandaGlobalName.ASSAY_ID);
                    String assayName = mobject.getString(PandaGlobalName.ASSAY_NAME);
                    String gmtCreate = mobject.getString(PandaGlobalName.GMT_CREATE);
                    //缩略结果
                    String assayResult = mobject.getString(PandaGlobalName.ASSAY_RESULT);

                    intent.putExtra(PandaGlobalName.ASSAY_ID, assayId);
                    intent.putExtra(PandaGlobalName.ASSAY_NAME, assayName);
                    intent.putExtra(PandaGlobalName.GMT_CREATE, gmtCreate);
                    intent.putExtra(PandaGlobalName.ASSAY_RESULT, assayResult);
                    intent.setClass(RecordActivity.this, RecordDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });

        titleOne = (TextView) findViewById(R.id.recordActivity_title_one);
        titleTwo = (TextView) findViewById(R.id.recordActivity_title_two);
        boolean isByTime = true;
        if (RecordFragment.TIME.equals(title)) {
            titleOne.setText(TIME_TITLE);
            titleTwo.setText(HUA_YAN_DAN);
            isByTime = true;
        } else if (RecordFragment.RECORD.equals(title)) {
            titleOne.setText(HUA_YAN_DAN);
            titleTwo.setText(TIME_TITLE);
            isByTime = false;
        }
        //异步加载list数据
        GetRecordItemTask task = new GetRecordItemTask(this, isByTime, this.mDataList);
        task.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class GetRecordItemTask extends AsyncTask<Void, Integer, Integer> {

        private Context context;
        private boolean isByTime;
        private ArrayList<JSONObject> mDataList;

        public GetRecordItemTask(Context context, boolean isByTime, ArrayList<JSONObject> mDataList) {
            this.context = context;
            this.isByTime = isByTime;
            this.mDataList = mDataList;
        }

		/*
         * UI 线程中, 在doInBackground 之前
		 * */

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
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
                SharedPreferences user = getSharedPreferences(PandaGlobalName.UserInfo.info, 0);
                String tel = user.getString(PandaGlobalName.UserInfo.phone, "");
                String token = user.getString(PandaGlobalName.UserInfo.token, "");
                String encryptKey = user.getString(PandaGlobalName.UserInfo.encryptKey, "");
                String urlStr = "";
                if (isByTime) {
                    urlStr = PandaGlobalName.getUserHistoryForAppUrl(tel, token, encryptKey);
                } else {
                    urlStr = PandaGlobalName.getUserHistoryByTypeForAppUrl(tel, token, encryptKey);
                }
                URL url = new URL(urlStr);
                Log.i(PandaGlobalName.TAG, "huayandan detail url:" + url);
                URLConnection connection = url.openConnection();
                connection.connect();
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }

                JSONObject jsonResult = new JSONObject(result);
                Boolean bo = jsonResult.getBoolean("success");
                if (jsonResult == null || bo == false) {
                    Intent intent = new Intent();
                    intent.setClass(this.context, PandaLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                JSONObject resultHistory = jsonResult.getJSONObject("result");
                JSONArray jsonArray = resultHistory.getJSONArray("userHistoryList");
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
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            RecordActivityAdapter adapter = new RecordActivityAdapter(this.context, this.isByTime, this.mDataList);
            listView.setAdapter(adapter);
        }

    }


}
