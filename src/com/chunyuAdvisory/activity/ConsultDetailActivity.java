package com.chunyuAdvisory.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.chunyuAdvisory.adapter.ConsultDetailAdapter;
import com.chunyuAdvisory.bean.Ask_Detail;
import com.chunyuAdvisory.bean.Ask_Detail_Item;
import com.google.gson.Gson;
import com.pandadoctor.pandadoctor.R;
import com.squareup.picasso.Picasso;
import com.utils.StaticVar;

/**
 * Created by Administrator on 15-6-26.
 */
public class ConsultDetailActivity extends Activity {
    private Ask_Detail_Item mask_detail_item;
    private ListView mlistview;
    private ImageView back, doctor_head;
    private TextView close, doctor_name, doctor_title, doctor_hospital, doctor_clinic, start_time, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_consultdetail_layout);
        StaticVar.activityList.add(this);

        initview();
        initListener();

        Intent intent = getIntent();
        String ask_detail_json = intent.getStringExtra("json");
        String head = intent.getStringExtra("head");

        Gson gson = new Gson();
        Ask_Detail ask_detail = gson.fromJson(ask_detail_json, Ask_Detail.class);
        Log.e("ask_detail_json", ask_detail_json);

        if (!ask_detail.getResult().equals("FAIL")) {
            mask_detail_item = ask_detail.getAsk_detail();

            title.setText(mask_detail_item.getDoctor_hospital());
            doctor_title.setText(mask_detail_item.getDoctor_title());
            doctor_clinic.setText(mask_detail_item.getDoctor_clinic());
            doctor_hospital.setText(mask_detail_item.getDoctor_hospital());
            doctor_name.setText(mask_detail_item.getDoctor_name());
            start_time.setText(mask_detail_item.getStart_time());
            Picasso.with(this).load(head).into(doctor_head);

            ConsultDetailAdapter consultDetailAdapter = new ConsultDetailAdapter(this);
            mlistview.setAdapter(consultDetailAdapter);
            mlistview.setSelection(mask_detail_item.getContents().size());
            consultDetailAdapter.upData(mask_detail_item, head);
        }
    }

    private void initview() {
        mlistview = (ListView) findViewById(R.id.consultdetail_list);
        back = (ImageView) findViewById(R.id.back);
        close = (TextView) findViewById(R.id.close);
        title = (TextView) findViewById(R.id.title);
        doctor_head = (ImageView) findViewById(R.id.doctor_head);
        doctor_name = (TextView) findViewById(R.id.doctor_name);
        doctor_clinic = (TextView) findViewById(R.id.doctor_clinic);
        doctor_hospital = (TextView) findViewById(R.id.doctor_hospital);
        doctor_title = (TextView) findViewById(R.id.doctor_title);
        start_time = (TextView) findViewById(R.id.start_time);
    }


    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
