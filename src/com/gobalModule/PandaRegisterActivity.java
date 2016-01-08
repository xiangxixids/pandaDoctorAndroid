package com.gobalModule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;
import com.utils.UtilTool;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class PandaRegisterActivity extends Activity {

    public Button registerBtn;
    public TextView hasAccountTV;
    public TextView showCopyRightTV;
    public EditText phoneET;
    public EditText passwdET;
    public EditText nikiNameET;
    private ImageView back;

    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_panda_register);

//        ActionBar bar = getActionBar();
//        bar.setTitle("用户注册");

        context = this;

        registerBtn = (Button) findViewById(R.id.panda_register_button);
        hasAccountTV = (TextView) findViewById(R.id.panda_register_has_account_textView);
        showCopyRightTV = (TextView) findViewById(R.id.panda_register_copyright_textView1);
        phoneET = (EditText) findViewById(R.id.panda_register_phone_editText);
        passwdET = (EditText) findViewById(R.id.panda_register_passwd_editText);
        nikiNameET = (EditText) findViewById(R.id.panda_register_niki_name_editText);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        showCopyRightTV.setClickable(true);
        showCopyRightTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(PandaRegisterActivity.this, PandaCopyRightActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        });

        hasAccountTV.setClickable(true);
        hasAccountTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(PandaRegisterActivity.this, PandaLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String phone = phoneET.getText().toString().trim();
                String passwd = passwdET.getText().toString().trim();
                String nickName = nikiNameET.getText().toString().trim();
                // 校验phone
                if (!UtilTool.isMobileNO(phone)) {
                    Toast.makeText(context, "输入手机号错误", Toast.LENGTH_LONG).show();
                    return;
                }
                // 校验passwd
                if (passwd.length() > 20 || passwd.length() < 6) {
                    Toast.makeText(context, "密码长度必须大于6位小于20位", Toast.LENGTH_LONG).show();
                    return;
                }
                // 校验nikiname长度
                if (nickName.length() > 30 || nickName.length() < 1) {
                    Toast.makeText(context, "请输入1~30个字符作为昵称", Toast.LENGTH_LONG).show();
                    return;
                }

                RegisterTask task = new RegisterTask(context, phone, passwd, nickName);
                task.execute();
            }

        });
    }

    class RegisterTask extends AsyncTask<Void, Integer, Integer> {

        private Context context;
        private String phone;
        private String nickName;
        private String passwd;
        private String hasSuccess = "false";
        private String errorMsg = "";


        public RegisterTask(Context context, String phone, String passwd, String nickName) {
            this.context = context;
            this.phone = phone;
            this.passwd = passwd;
            this.nickName = nickName;
        }

		/*
         * UI 线程中, 在doInBackground 之前
		 * */

        @Override
        protected void onPreExecute() {
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
                URL url = new URL(PandaGlobalName.getRegisterForAppUrl(phone, passwd, nickName));
                Log.i(PandaGlobalName.TAG, "huayandan register url:" + url);
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

                JSONObject object = new JSONObject(result);
                if (object.getString("success").toString().equalsIgnoreCase("true")) {
                    hasSuccess = "true";
                } else {
                    JSONObject _map = object.getJSONObject("result");
                    errorMsg = _map.getString("message");
                }

                Log.i(PandaGlobalName.TAG, "Huayandan result pager Json data parser finished");

            } catch (Exception e) {
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

            if (hasSuccess.equalsIgnoreCase("false")) {
                Toast.makeText(context, "注册失败: " + errorMsg, Toast.LENGTH_LONG).show();
            }
            if (hasSuccess.equalsIgnoreCase("true")) {
                Toast.makeText(context, "注册成功!请重新登录.", Toast.LENGTH_LONG).show();
                finish();
            }


        }

    }

}
