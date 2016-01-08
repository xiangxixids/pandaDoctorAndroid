package com.huayandan.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.huayandan.activity.HuayandanMainItemActivity;
import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;
import com.utils.UtilTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拍化验单Fragment的界面
 *
 * @author virgo
 */
public class CaptureFragment extends Fragment {
    private ArrayList<HashMap<String, Object>> mItemlist = new ArrayList<HashMap<String, Object>>();
    private ArrayList<JSONObject> mDatalist = new ArrayList<JSONObject>();
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.capture_fragment, container,
                false);

        GridView gridview = (GridView) rootView.findViewById(R.id.GrilView);
        context = getActivity();

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                //检查网络
                if (UtilTool.netWorkCheck(getActivity().getApplicationContext())) {
                    // 根据position 来选择具体某一个activity, 所有的activity 都采用一个模板. 然后添加相应数据进来.
                    try {
                        JSONObject mobject = mDatalist.get(position);
                        String sortEnglishName = mobject.getString(PandaGlobalName.SORT_ENGLISH_NAME);
                        String recordId = mobject.getString(PandaGlobalName.RECORD_ID);
                        Log.i(PandaGlobalName.TAG, "sortEnglishName:" + sortEnglishName + ", recordId:" + recordId);
                        Intent intent = new Intent();
                        intent.putExtra(PandaGlobalName.POSITION, position);
                        intent.putExtra(PandaGlobalName.SORT_ENGLISH_NAME, sortEnglishName);
                        intent.putExtra(PandaGlobalName.RECORD_ID, recordId);
                        intent.setClass(getActivity(), HuayandanMainItemActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(context, "亲，数据还没准备好哦！", Toast.LENGTH_LONG).show();
                        GetMainAppTask getMainAppTask = new GetMainAppTask(context);
                        getMainAppTask.execute();
                    }

                }
            }
        });

        GetMainAppTask getMainAppTask = new GetMainAppTask(context);
        getMainAppTask.execute();

        if (mItemlist.isEmpty()) {
            for (int i = 0; i < 9; i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("mImageView", R.drawable.gradview_1 + i);
                map.put("mTextView", getString(R.string.gridview1 + i));
                mItemlist.add(map);
            }
        }
        // 往list放HashMap数据,每个HashMap里有一个ImageView,TextView

        SimpleAdapter mAdaper = new SimpleAdapter(getActivity(), mItemlist,
                R.layout.activity_main_gridview, new String[]{"mImageView",
                "mTextView"}, new int[]{R.id.mImageView,
                R.id.mTextView});

        gridview.setAdapter(mAdaper);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    class GetMainAppTask extends AsyncTask<Void, Integer, Integer> {

		/*
         * UI 线程中, 在doInBackground 之前
		 * */
    	private Context context;
    	GetMainAppTask(Context context)
    	{
    		this.context = context;
    	}

        @Override
        protected void onPreExecute() {
            System.out.println("1111");
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
                //查询化验单分类
                URL url = new URL(PandaGlobalName.getAssaySortForAppUrl());
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
                    mDatalist.add(jsonArray.getJSONObject(i));
                }
                
                Log.i(PandaGlobalName.TAG, "Json data parser finished");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
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
            System.out.println("3333");
        }

    }

}
