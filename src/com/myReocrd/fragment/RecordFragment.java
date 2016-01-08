package com.myReocrd.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import com.chunyuAdvisory.activity.RecordFreeConsultActivity;
import com.gobalModule.PandaLoginActivity;
import com.myReocrd.activity.RecordActivity;
import com.myReocrd.adapter.RecordFragmentAdapter;
import com.myReocrd.bean.ListItem;
import com.pandadoctor.pandadoctor.R;
import com.utils.UtilTool;

import java.util.ArrayList;

/**
 * 病历Fragment的界面
 *
 * @author virgo
 */
public class RecordFragment extends Fragment {

    public static final String TIME = "按照时间划分";
    public static final String RECORD = "按照化验单划分";
    private ListView mListView;
    private ImageView myconsult;
    // 声明数组链表，其装载的类型是ListItem(封装了一个Drawable和一个String的类)
    private ArrayList<ListItem> mList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.record_fragment_layout, container,
                false);

        mListView = (ListView) rootView.findViewById(R.id.listview);
        myconsult = (ImageView) rootView.findViewById(R.id.myconsult);

        myconsult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检查网络以及是否登陆
                if (UtilTool.netWorkCheck(getActivity().getApplicationContext())) {
                    FragmentActivity f = getActivity();
                    if (UtilTool.isLogin(f)) {
                        Intent intent = new Intent(getActivity(), RecordFreeConsultActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(f, PandaLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //检查网络
                if (UtilTool.netWorkCheck(getActivity().getApplicationContext())) {
                    FragmentActivity f = getActivity();
                    if (UtilTool.isLogin(f)) {
                        String title = mList.get(position).getTitle();
                        Intent intent = new Intent();
                        intent.putExtra("title", title);
                        intent.setClass(getActivity(), RecordActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(f, PandaLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
        // 获取Resources对象
        Resources res = getActivity().getResources();
        mList = new ArrayList<ListItem>();
        // 初始化data，装载八组数据到数组链表mList中
        ListItem item = new ListItem();
        item.setImage(res.getDrawable(R.drawable.ofm_video_icon));
        item.setTitle(TIME);
        mList.add(item);

        item = new ListItem();
        item.setImage(res.getDrawable(R.drawable.ofm_photo_icon));
        item.setTitle(RECORD);
        mList.add(item);

        // 获取MainListAdapter对象
        RecordFragmentAdapter adapter = new RecordFragmentAdapter(getActivity(), mList);

        // 将MainListAdapter对象传递给ListView视图
        mListView.setAdapter(adapter);
        return rootView;
    }


}
