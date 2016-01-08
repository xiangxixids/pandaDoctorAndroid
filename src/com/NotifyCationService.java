package com;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import com.aidl.paderservice;
import com.chunyuAdvisory.bean.Ask_Detail;
import com.chunyuAdvisory.bean.Ask_Detail_Item;
import com.chunyuAdvisory.bean.Problem_List;
import com.google.gson.Gson;
import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;
import com.utils.StaticVar;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 15-8-3.
 */
public class NotifyCationService extends Service {
    private MyPaderService myPaderService = new MyPaderService();
    private String islogin;
    private SharedPreferences user;
    private String problem_list_json;
    private Problem_List problem_list;
    private int contentsize;
    private String phone;
    private String savephone;

    @Override
    public IBinder onBind(Intent intent) {
        return myPaderService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        user = getSharedPreferences(PandaGlobalName.UserInfo.info, MODE_PRIVATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    islogin = user.getString(PandaGlobalName.UserInfo.login, "");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (islogin.equals(PandaGlobalName.UserInfo.hasLogin)) {
                        if (StaticVar.isConsult == false) {

                            sendNotifyCation();
                        }
                    }

                }
            }
        }).start();

    }

    class MyPaderService extends paderservice.Stub {
        @Override
        public void SendNotifyCation() throws RemoteException {

        }

        @Override
        public void SaveContentSize() throws RemoteException {
            saveContentSize();
        }
    }

    private void saveContentSize() {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... strings) {
                savephone = user.getString(PandaGlobalName.UserInfo.phone, "");
                problem_list_json = parseUrl(StaticVar.getHistoryProblemListUrl(phone));
                Gson gson = new Gson();
                problem_list = gson.fromJson(problem_list_json, Problem_List.class);
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (problem_list.getProblem_list() != null) {
                    for (int i = 0; i < problem_list.getProblem_list().size(); i++) {
                        if (problem_list.getProblem_list().get(i).getStatus().equals("已关闭")) {
                            continue;
                        } else {
                            getProblemDetailList(problem_list.getProblem_list().get(i).getProblem_id());
                        }
                    }
                }
            }
        }.execute();
    }

    private void getProblemDetailList(final String problem_id) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String ask_detail_json = parseUrl(StaticVar.getProblemDetailUrl(savephone, problem_id));
                Gson gson = new Gson();
                Ask_Detail ask_detail = gson.fromJson(ask_detail_json, Ask_Detail.class);
                Ask_Detail_Item ask_detail_item = ask_detail.getAsk_detail();
                int size = 0;
                if (ask_detail_item.getContents() != null && ask_detail_item.getContents().size() > 0) {
                    for (int i = 0; i < ask_detail_item.getContents().size(); i++) {
                        if (ask_detail_item.getContents().get(i).getTalker().equals("1")) {
                            size++;
                        } else {
                            continue;
                        }

                    }

                }

                return size + "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (!s.equals("0")) {
                    String phone = user.getString(PandaGlobalName.UserInfo.phone, "");
                    SharedPreferences.Editor editor = user.edit();
                    editor.putString(phone, s);
                    editor.commit();
                }
                Log.e("savesize", s);
            }
        }.execute();
    }

    private void sendNotifyCation() {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... strings) {
                phone = user.getString(PandaGlobalName.UserInfo.phone, "");
                problem_list_json = parseUrl(StaticVar.getHistoryProblemListUrl(phone));
                Gson gson = new Gson();
                problem_list = gson.fromJson(problem_list_json, Problem_List.class);
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (problem_list.getProblem_list() != null) {
                    for (int i = 0; i < problem_list.getProblem_list().size(); i++) {
                        if (problem_list.getProblem_list().get(i).getStatus().equals("已关闭")) {
                            continue;
                        } else {
                            String phone = user.getString(PandaGlobalName.UserInfo.phone, "");
                            contentsize = Integer.parseInt(user.getString(phone, "0"));
                            Log.e("contentsize", contentsize + "");
                            sendgetProblemDetailList(problem_list.getProblem_list().get(i).getProblem_id());
                        }
                    }
                }
            }
        }.execute();
    }

    private void sendgetProblemDetailList(final String problem_id) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                Log.e("phone", phone);
                String ask_detail_json = parseUrl(StaticVar.getProblemDetailUrl(phone, problem_id));
                Gson gson = new Gson();
                Ask_Detail ask_detail = gson.fromJson(ask_detail_json, Ask_Detail.class);
                Ask_Detail_Item ask_detail_item = ask_detail.getAsk_detail();
                int size = 0;
                if (ask_detail_item.getContents() != null && ask_detail_item.getContents().size() > 0) {
                    for (int i = 0; i < ask_detail_item.getContents().size(); i++) {
                        if (ask_detail_item.getContents().get(i).getTalker().equals("1")) {
                            size++;
                        } else {
                            continue;
                        }

                    }
                    return size + "";
                }
                return 0 + "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e("size", s);
                if (Integer.parseInt(s) > contentsize) {
                    sendNotify();
                }
            }
        }.execute();
    }

    private void sendNotify() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(NotifyCationService.this);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        builder.setSmallIcon(R.drawable.logo);
        builder.setWhen(200);
        builder.setTicker("您有新的回复，请点击查看！");
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.consult_notifycation_layout);
//
        Intent preintent = new Intent();
        preintent.setAction("com.NotifyCationService.sendnotifyCation");
        PendingIntent prependintent = PendingIntent.getBroadcast(getApplicationContext(), 0, preintent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.line, prependintent);
//
        Notification notification = builder.build();
        notification.contentView = remoteViews;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(0, notification);
        StaticVar.isConsult = true;
    }


    public String parseUrl(String path) {
        URL url = null;
        BufferedReader br = null;
        InputStream in = null;
        StringBuffer sb = new StringBuffer();
        try {
            HttpGet getMethod = new HttpGet(path);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(getMethod);
            in = response.getEntity().getContent();
            br = new BufferedReader(new InputStreamReader(in));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                sb = sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
