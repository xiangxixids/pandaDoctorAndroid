package com.gobalModule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PandaLoginActivity extends Activity {

    private Button loginBtn;
    private EditText usernameET;
    private EditText passwdET;
    private TextView registerTV;
    private Context context;
    private ImageView back, logo_qq, logo_sinaweibo, logo_wechat;
    private Platform platform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_panda_login);

        ShareSDK.initSDK(this);

//        ActionBar bar = getActionBar();
//        bar.setTitle("用户登录");
        initUI();
        initListener();

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        registerTV.setClickable(true);
        registerTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(PandaLoginActivity.this, PandaRegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String phone = usernameET.getText().toString();
                String passwd = passwdET.getText().toString();
                LoginTask task = new LoginTask(context, phone, passwd);
                task.execute();
            }
        });

    }

    private void initUI() {
        loginBtn = (Button) findViewById(R.id.panda_button_login_btn);
        usernameET = (EditText) findViewById(R.id.panda_login_editText_username);
        passwdET = (EditText) findViewById(R.id.panda_login_editText_passwd);
        registerTV = (TextView) findViewById(R.id.panda_login_textView_register);
        logo_qq = (ImageView) findViewById(R.id.logo_qq);
        logo_sinaweibo = (ImageView) findViewById(R.id.logo_sinaweibo);
        //  logo_wechat =(ImageView)findViewById(R.id.logo_wechat);

        context = this;
    }

    private void initListener() {
        logo_qq.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setLogo(context, "QQ");
            }
        });

        logo_sinaweibo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setLogo(context, "SinaWeibo");


            }
        });

//      logo_wechat.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setLogo(context,"Wechat");
//            }
//        });


    }

    private void setLogo(final Context context, String pintai) {
        platform = ShareSDK.getPlatform(context, pintai);
//        platform.SSOSetting(true);   //优先使用客户端登陆，为true时使用网页登陆
//        platform.showUser(null);   //为null表示使用当前账户登陆
        platform.authorize();

        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
                //
                SharedPreferences user = getSharedPreferences(PandaGlobalName.UserInfo.info, MODE_PRIVATE);
                Editor editor = user.edit();
                editor.putString(PandaGlobalName.UserInfo.login, PandaGlobalName.UserInfo.hasLogin);
                editor.putString(PandaGlobalName.UserInfo.phone, platform.getDb().getUserId());
                editor.putString("headimg", platform.getDb().getUserIcon());
                editor.putString(PandaGlobalName.UserInfo.name, platform.getDb().getUserName());
                editor.putString(PandaGlobalName.UserInfo.token, platform.getDb().getToken());
                editor.commit();
                // 提交记录结束
                // 发送广播出来
                finish();
                platform.removeAccount(true);


                Toast.makeText(context, "登录成功!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                // Toast.makeText(context,"验证登录失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                //  Toast.makeText(context,"注销第三方登录",Toast.LENGTH_SHORT).show();
            }
        });

    }

    class LoginTask extends AsyncTask<Void, Integer, Integer> {

        private Context context;
        private String phone;
        private String passwd;
        private JSONObject hasSuccess;
        private String errorMsg = "";


        public LoginTask(Context context, String phone, String passwd) {
            this.context = context;
            this.phone = phone;
            this.passwd = passwd;
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
                URL url = new URL(PandaGlobalName.getLoginForAppUrl(phone, passwd));
                Log.i(PandaGlobalName.TAG, "huayandan login url:" + url);
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
                hasSuccess = new JSONObject(result);

                Log.i(PandaGlobalName.TAG, "result pager Json data parser finished");

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

            try {
                Boolean bo = hasSuccess.getBoolean("success");
                if (bo == false) {
                    Toast.makeText(context, "登录失败: 请检查用户名和密码是否正确", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "登录成功!", Toast.LENGTH_LONG).show();
                    // 记录已经全局登录了
                    SharedPreferences user = getSharedPreferences(PandaGlobalName.UserInfo.info, MODE_PRIVATE);
                    Editor editor = user.edit();
                    editor.putString(PandaGlobalName.UserInfo.login, PandaGlobalName.UserInfo.hasLogin);
                    editor.putString(PandaGlobalName.UserInfo.phone, phone);
                    JSONObject jsonResult = hasSuccess.getJSONObject("result");
                    String token = jsonResult.getString("token");
                    String userName = jsonResult.getString("userName");
                    editor.putString(PandaGlobalName.UserInfo.name, userName);
                    editor.putString(PandaGlobalName.UserInfo.token, token);
                    String encryptKey = jsonResult.getString("encryptKey");
                    editor.putString(PandaGlobalName.UserInfo.encryptKey, encryptKey);
                    editor.commit();
                    // 提交记录结束
                    // 发送广播出来
                    Intent intent = new Intent(PandaGlobalName.BROADCAST_UPDATE_MENU);
                    intent.putExtra(PandaGlobalName.UserInfo.name, phone);
                    sendBroadcast(intent);
                    finish();
                }
            } catch (Exception e) {

            }


        }

    }

}
