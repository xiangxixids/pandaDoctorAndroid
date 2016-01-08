package com.huayandan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 15-6-26.
 */
public class HuanyandanMainItemActivityAdapter extends BaseAdapter {

    // 作为动态载入的layout
    private LayoutInflater mInflater;
    private ArrayList<JSONObject> mDataList;

    public HuanyandanMainItemActivityAdapter(Context context, ArrayList<JSONObject> mDataList) {
        // 从context 中生成layoutinflater
        this.mInflater = LayoutInflater.from(context);
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mDataList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder = new ViewHolder();
        // 记录listview 滚动状态
        Log.i(PandaGlobalName.TAG, "getView " + position + " " + convertView);

        // 这里没有对list data 进行优化, 需要注意.
        // 无差别创建listview
        convertView = mInflater.inflate(R.layout.huayandan_main_item, null);
        holder.title = (TextView) convertView.findViewById(R.id.huayandan_main_listview_ItemTitle);

        try {
            holder.title.setText(mDataList.get(position).getString(PandaGlobalName.ASSAY_NAME));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            holder.title.setText("haha");
        }
        return convertView;
    }

    public final class ViewHolder {
        public TextView title;
    }

}
