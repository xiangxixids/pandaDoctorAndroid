package com.huayandan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.huayandan.adapter.HuanyandanFillActivityAdapter;
import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;
import com.utils.StaticVar;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HuayandanFillActivity extends Activity {

    private ListView listView;
    private Button confirmBtn;
    //化验单中文名称
    private String assayName;
    //id
    private String recordId;

    private ArrayList<JSONObject> mDataList = new ArrayList<JSONObject>();

    private ArrayList<String> mResultDataList = new ArrayList<String>();
    private ArrayList<String> mCheckItemList = new ArrayList<String>();

    private ImageView back;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_huayandan_fill);
        StaticVar.activityList.add(this);

        assayName = getIntent().getStringExtra(PandaGlobalName.ASSAY_NAME);
        recordId = getIntent().getStringExtra(PandaGlobalName.RECORD_ID);

//        ActionBar actionBar = getActionBar();
//        actionBar.setTitle(assayName);

        confirmBtn = (Button) findViewById(R.id.huayandan_fill_button);
        listView = (ListView) findViewById(R.id.huayandan_fill_listView);

        title = (TextView) findViewById(R.id.title);
        title.setText(assayName);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    String checkItems = mCheckItemList.toString().replace("[", "").replace("]", "").replace(" ", "");
                    String resultList = mResultDataList.toString().replace("[", "").replace("]", "").replace(" ", "");
                    Log.i(PandaGlobalName.TAG, "resultList:" + resultList + ", checkItems:" + checkItems);
                    String url = PandaGlobalName.getAssayResultForAppUrl(recordId, checkItems, resultList);
                    Log.i(PandaGlobalName.TAG, "url:" + url);
                    Intent intent = new Intent();
                    intent.putExtra(PandaGlobalName.ASSAY_NAME, assayName);
                    intent.putExtra(PandaGlobalName.RECORD_ID, recordId);
                    intent.putExtra(PandaGlobalName.URL, url);
                    intent.setClass(HuayandanFillActivity.this, HuayandanResultActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });

        GetFillItemTask task = new GetFillItemTask(this, this.mDataList, this.mResultDataList, this.mCheckItemList, this.recordId);
        task.execute();

    }


    class GetFillItemTask extends AsyncTask<Void, Integer, Integer> {

        private Context context;
        private ArrayList<JSONObject> mDataList;
        private ArrayList<String> mResultDataList;
        private ArrayList<String> mCheckItemList;
        private String recordId;

        public GetFillItemTask(Context context, ArrayList<JSONObject> mDataList, ArrayList<String> mResultDataList, ArrayList<String> mCheckItemList, String recordId) {
            this.context = context;
            this.mDataList = mDataList;
            this.mResultDataList = mResultDataList;
            this.mCheckItemList = mCheckItemList;
            this.recordId = recordId;
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
                    JSONObject object = jsonArray.getJSONObject(i);
                    mDataList.add(object);
                    mResultDataList.add("0");
                    mCheckItemList.add(object.getString(PandaGlobalName.RECORD_ID));
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
            HuanyandanFillActivityAdapter adapter = new HuanyandanFillActivityAdapter(this.context, this.mDataList, this.mResultDataList, this.mCheckItemList, this.recordId);
            listView.setAdapter(adapter);
        }

    }

}
