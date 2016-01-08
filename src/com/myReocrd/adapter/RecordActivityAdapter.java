package com.myReocrd.adapter;

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
public class RecordActivityAdapter extends BaseAdapter {

    // 作为动态载入的layout
    private LayoutInflater mInflater;
    private boolean isByTime;
    private ArrayList<JSONObject> mDataList;

    public RecordActivityAdapter(Context context, boolean isByTime, ArrayList<JSONObject> mDataList) {
        // 从context 中生成layoutinflater
        this.mInflater = LayoutInflater.from(context);
        this.isByTime = isByTime;
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
        convertView = mInflater.inflate(R.layout.record_atcivity_item, null);
        holder.title = (TextView) convertView.findViewById(R.id.recordActivity_title);
        holder.subContent = (TextView) convertView.findViewById(R.id.recordActivity_subcontent);

        try {
            JSONObject o = mDataList.get(position);
            String gmtCreate = o.getString(PandaGlobalName.GMT_CREATE_STR);
            String assayName = o.getString(PandaGlobalName.ASSAY_NAME);
            boolean flag = o.getBoolean(PandaGlobalName.FLAG);
            if (isByTime) {
                if (flag) {
                    holder.title.setText(gmtCreate);
                }
                holder.subContent.setText(assayName);
            } else {
                if (flag) {
                    holder.title.setText(assayName);
                }
                holder.subContent.setText(gmtCreate);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            holder.title.setText("囧，数据异常!");
        }
        return convertView;
    }

    public final class ViewHolder {
        public TextView title;
        public TextView subContent;
    }


}
