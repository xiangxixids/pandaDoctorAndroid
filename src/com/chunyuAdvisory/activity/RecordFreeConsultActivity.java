package com.chunyuAdvisory.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.chunyuAdvisory.fragment.RecordFreeConsultFragment;
import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;
import com.utils.StaticVar;

/**
 * Created by Administrator on 15-6-13.
 */
public class RecordFreeConsultActivity extends FragmentActivity {
    private ListView freeconsultlist;
    private ImageView back;
    private TextView title, close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_record_freeconsult_layout);
        StaticVar.activityList.add(this);

        freeconsultlist = (ListView) findViewById(R.id.freeconsultlist);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.consulttitle);
        close = (TextView) findViewById(R.id.close);

        RecordFreeConsultFragment recordFreeConsultFragment = new RecordFreeConsultFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.freeconsultframe, recordFreeConsultFragment)
                .commit();
        initlistener();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void initlistener() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences user = getSharedPreferences(PandaGlobalName.UserInfo.info, MODE_PRIVATE);
                SharedPreferences.Editor editor = user.edit();
                editor.putString(PandaGlobalName.UserInfo.login, PandaGlobalName.UserInfo.notLogin);
                editor.commit();
                // 提交记录结束
                Toast.makeText(RecordFreeConsultActivity.this, "注销成功", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(PandaGlobalName.BROADCAST_UPDATE_MENU);
                intent1.putExtra(PandaGlobalName.UserInfo.name, "11111");
                sendBroadcast(intent1);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getText().toString().equals("免费提问")) {
                    finish();
                } else if (title.getText().toString().equals("春雨医生")) {
                    title.setText("免费提问");
                    RecordFreeConsultFragment recordFreeConsultFragment = new RecordFreeConsultFragment();
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.freeconsultframe, recordFreeConsultFragment)
                            .commit();
                }
            }
        });
    }
}
