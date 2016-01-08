package com.huayandan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 15-6-26.
 */
public class HuanyandanFillActivityAdapter extends BaseAdapter {


    // 作为动态载入的layout
    private LayoutInflater mInflater;
    private ArrayList<JSONObject> mDataList;
    private ArrayList<String> mResultDataList;
    private ArrayList<String> mCheckItemList;
    private Context context;
    private String recordId;


    public HuanyandanFillActivityAdapter(Context context, ArrayList<JSONObject> mDataList, ArrayList<String> mResultDataList, ArrayList<String> mCheckItemList, String recordId) {
        // 从context 中生成layoutinflater
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mDataList = mDataList;
        this.mResultDataList = mResultDataList;
        this.mCheckItemList = mCheckItemList;
        this.recordId = recordId;
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
        final int mPostion = position;
        // 记录listview 滚动状态
        Log.i(PandaGlobalName.TAG, "getView " + position + " " + convertView);

        // 这里没有对list data 进行优化, 需要注意.
        // 无差别创建listview
        convertView = mInflater.inflate(R.layout.huayandan_fill_item, null);
        holder.title = (TextView) convertView.findViewById(R.id.huayandan_fill_listview_ItemTitle);
        holder.mSwitch = (Switch) convertView.findViewById(R.id.huayandan_fill_switch);

        try {
            JSONObject object = mDataList.get(position);
            holder.title.setText(mDataList.get(position).getString(PandaGlobalName.ASSAY_ITEM_THUMBNAIL_NAME));
            holder.mSwitch.setTextOff("-");
            holder.mSwitch.setTextOn("+");
            holder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        mResultDataList.set(mPostion, "1");
                    } else {
                        mResultDataList.set(mPostion, "0");
                    }
                }

            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            holder.title.setText("haha");
        }
        return convertView;
    }

    public final class ViewHolder {
        public TextView title;
        public Switch mSwitch;
    }

}
