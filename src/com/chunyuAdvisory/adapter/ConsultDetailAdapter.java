package com.chunyuAdvisory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.chunyuAdvisory.bean.Ask_Contents;
import com.chunyuAdvisory.bean.Ask_Detail_Item;
import com.pandadoctor.pandadoctor.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 15-6-26.
 */
public class ConsultDetailAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Ask_Detail_Item mask_detail_item;
    private List<Ask_Contents> mlist = new ArrayList<Ask_Contents>();
    private Context mcontext;
    private String mhead;

    public ConsultDetailAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        mcontext = context;
    }

    public void upData(Ask_Detail_Item ask_detail_item, String head) {
        mask_detail_item = ask_detail_item;
        mlist = mask_detail_item.getContents();
        mhead = head;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (mlist.get(i).getTalker().equals("0")) {
            view = layoutInflater.inflate(R.layout.consultdetail_list_item_layout, null);
            TextView content = (TextView) view.findViewById(R.id.consultdetailcontent);
            content.setText(mlist.get(i).getContent());
            return view;
        } else if (mlist.get(i).getTalker().equals("1")) {
            view = layoutInflater.inflate(R.layout.consultdetail_list_hosipetal_layout, null);
            TextView content = (TextView) view.findViewById(R.id.consultdetailcontent);
            ImageView head = (ImageView) view.findViewById(R.id.head);
            content.setText(mlist.get(i).getContent());
            Picasso.with(mcontext).load(mhead).into(head);
            return view;
        }

        return null;
    }
}
