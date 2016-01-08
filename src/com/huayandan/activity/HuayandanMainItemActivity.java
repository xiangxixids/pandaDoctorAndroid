package com.huayandan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.huayandan.adapter.HuanyandanMainItemActivityAdapter;
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
import java.util.List;
import java.util.Map;

public class HuayandanMainItemActivity extends Activity {

    private ArrayList<String> mList;
    private ArrayList<JSONObject> mDataList = new ArrayList<JSONObject>();
    //分类英文名
    private String sortEnglishName;
    //id
    private String recordId;

    private ProgressBar progressBar;
    private ListView listView;

    private ImageView back;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_huayandan_main_item);
        StaticVar.activityList.add(this);

        Intent intent = getIntent();
        sortEnglishName = intent.getStringExtra(PandaGlobalName.SORT_ENGLISH_NAME);
        recordId = intent.getStringExtra(PandaGlobalName.RECORD_ID);

//        ActionBar actionBar = getActionBar();
//        actionBar.setTitle(sortEnglishName);

        progressBar = (ProgressBar) findViewById(R.id.huayandan_main_item_progressBar);
        listView = (ListView) findViewById(R.id.huayandan_main_item_listView);

        title = (TextView) findViewById(R.id.title);
        title.setText(sortEnglishName);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                JSONObject mobject = mDataList.get(position);
                try {
                    String assayName = mobject.getString(PandaGlobalName.ASSAY_NAME);
                    String reocrdId = mobject.getString(PandaGlobalName.RECORD_ID);
                    String isNeedOcr = mobject.getString(PandaGlobalName.IS_NEED_OCR).toString();
                    Log.i(PandaGlobalName.TAG, "sortEnglishName:" + sortEnglishName + ", reocrdId:" + reocrdId);
                    Intent intent = new Intent();
                    intent.putExtra(PandaGlobalName.POSITION, position);
                    intent.putExtra(PandaGlobalName.ASSAY_NAME, assayName);
                    intent.putExtra(PandaGlobalName.RECORD_ID, reocrdId);
                    intent.putExtra(PandaGlobalName.IS_NEED_OCR, isNeedOcr);
                    intent.setClass(HuayandanMainItemActivity.this, HuayandanDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });


        // get data from server and fill the listview
        GetMainItemTask task = new GetMainItemTask(this, this.mDataList);
        task.execute();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.huayandan_main_item, menu);
        return true;
    }

    class GetMainItemTask extends AsyncTask<Void, Integer, Integer> {

        private Context context;
        private ArrayList<JSONObject> mDataList;

        public GetMainItemTask(Context context, ArrayList<JSONObject> mDataList) {
            this.context = context;
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
                URL url = new URL(PandaGlobalName.getAssayListForAppUrl(recordId));
                Log.i(PandaGlobalName.TAG, "URL: " + url);
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
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            HuanyandanMainItemActivityAdapter adapter = new HuanyandanMainItemActivityAdapter(this.context, this.mDataList);
            listView.setAdapter(adapter);
        }

    }


}
