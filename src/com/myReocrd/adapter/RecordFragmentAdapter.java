package com.myReocrd.adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.myReocrd.bean.ListItem;
import com.myReocrd.bean.ListItemView;
import com.pandadoctor.pandadoctor.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 15-6-26.
 */
public class RecordFragmentAdapter extends BaseAdapter {
    private ArrayList<ListItem> mList;
    private FragmentActivity activity;

    public RecordFragmentAdapter(FragmentActivity activity, ArrayList<ListItem> mList) {
        this.activity = activity;
        this.mList = mList;
    }

    /**
     * 返回item的个数
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    /**
     * 返回item的内容
     */
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mList.get(position);
    }

    /**
     * 返回item的id
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /**
     * 返回item的视图
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView listItemView;

        // 初始化item view
        if (convertView == null) {
            // 通过LayoutInflater将xml中定义的视图实例化到一个View中
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.list_item, null);

            // 实例化一个封装类ListItemView，并实例化它的两个域
            listItemView = new ListItemView();
            listItemView.imageView = (ImageView) convertView
                    .findViewById(R.id.image);
            listItemView.textView = (TextView) convertView
                    .findViewById(R.id.title);

            // 将ListItemView对象传递给convertView
            convertView.setTag(listItemView);
        } else {
            // 从converView中获取ListItemView对象
            listItemView = (ListItemView) convertView.getTag();
        }

        // 获取到mList中指定索引位置的资源
        Drawable img = mList.get(position).getImage();
        String title = mList.get(position).getTitle();

        // 将资源传递给ListItemView的两个域对象
        listItemView.imageView.setImageDrawable(img);
        listItemView.textView.setText(title);

        // 返回convertView对象
        return convertView;
    }
}
