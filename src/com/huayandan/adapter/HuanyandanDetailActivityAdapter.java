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
public class HuanyandanDetailActivityAdapter extends BaseAdapter {

    // 作为动态载入的layout
    private LayoutInflater mInflater;
    private ArrayList<JSONObject> mDataList;

    public HuanyandanDetailActivityAdapter(Context context, ArrayList<JSONObject> mDataList) {
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

    //		@Override
//		public boolean areAllItemsEnabled() {
//			return false;
//		}
//
    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder = new ViewHolder();
        // 记录listview 滚动状态
        Log.i(PandaGlobalName.TAG, "getView " + position + " " + convertView);

        // 这里没有对list data 进行优化, 需要注意.
        // 无差别创建listview
        convertView = mInflater.inflate(R.layout.huayandan_detail_item, null);
        holder.title = (TextView) convertView.findViewById(R.id.huayandan_detail_listview_ItemTitle);
        holder.subContent = (TextView) convertView.findViewById(R.id.huayandan_detail_listview_subcontent);

        try {
            JSONObject object = mDataList.get(position);
            holder.title.setText(mDataList.get(position).getString(PandaGlobalName.ASSAY_ITEM_THUMBNAIL_NAME));
            holder.subContent.setText(mDataList.get(position).getString(PandaGlobalName.SIGNIFICANCE));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            holder.title.setText("没有数据");
        }
        return convertView;
    }

    public final class ViewHolder {
        public TextView title;
        public TextView subContent;
    }


}
