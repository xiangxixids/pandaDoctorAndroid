package com.chunyuAdvisory.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.chunyuAdvisory.activity.ConsultDetailActivity;
import com.chunyuAdvisory.adapter.FreeConsultListAdapter;
import com.chunyuAdvisory.bean.Problem_List;
import com.google.gson.Gson;
import com.pandadoctor.pandadoctor.R;
import com.squareup.picasso.Picasso;
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
 * Created by Administrator on 15-6-14.
 */
public class RecordFreeConsultFragment extends Fragment {
    private TextView title, name;
    private Button freeText2;
    private ListView mListView;
    private String problem_list_json;
    private Problem_List problem_list;
    private ImageView headimg;
    //全局用户信息
    private SharedPreferences user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recordfreeconsult_layout, null);
        freeText2 = (Button) view.findViewById(R.id.freetext2);
        title = (TextView) getActivity().findViewById(R.id.consulttitle);
        name = (TextView) view.findViewById(R.id.freeconsultname);
        mListView = (ListView) view.findViewById(R.id.freeconsultlist);
        headimg = (ImageView) view.findViewById(R.id.headimg);
        initlistener();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = getActivity().getSharedPreferences(PandaGlobalName.UserInfo.info, 0);
        String userName = user.getString(PandaGlobalName.UserInfo.name, "");
        name.setText(userName);
        String imgurl = user.getString("headimg", "");
        if (!imgurl.equals("")) {
            Picasso.with(getActivity()).load(imgurl).into(headimg);
        }

        if (StaticVar.ask == true) {
            FreeConsult();
            StaticVar.ask = false;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (StaticVar.ask == false) {
            new AsyncTask<String, Void, String>() {

                @Override
                protected String doInBackground(String... strings) {
                    String phone = user.getString(PandaGlobalName.UserInfo.phone, "");
                    problem_list_json = parseUrl(StaticVar.getHistoryProblemListUrl(phone));
                    Gson gson = new Gson();
                    problem_list = gson.fromJson(problem_list_json, Problem_List.class);
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if (problem_list.getProblem_list() != null) {
                        FreeConsultListAdapter freeConsultListAdapter = new FreeConsultListAdapter(getActivity());
                        freeConsultListAdapter.upData(problem_list);
                        mListView.setAdapter(freeConsultListAdapter);
                    }
                }
            }.execute();
        }
    }

    public void initlistener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AsyncTask<String, Void, String>() {

                    @Override
                    protected String doInBackground(String... strings) {
                        String phone = user.getString(PandaGlobalName.UserInfo.phone, "");
                        String ask_detail_json = getUrl(StaticVar.getProblemDetailUrl
                                (phone, problem_list.getProblem_list().get(position).getProblem_id()));
                        return ask_detail_json;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (problem_list.getProblem_list().get(position).getStatus().equals("已关闭")) {
                            Intent intent = new Intent(getActivity(), ConsultDetailActivity.class);
                            intent.putExtra("json", s);
                            intent.putExtra("head", problem_list.getProblem_list().get(position).getAvatar());
                            getActivity().startActivity(intent);
                        } else {
                            FreeConsult();
                        }
                    }
                }.execute();

            }
        });

        freeText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FreeConsult();
            }
        });
    }

    public void FreeConsult() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String phone = user.getString(PandaGlobalName.UserInfo.phone, "");
                return parseUrl(StaticVar.getAskServiceUrl(phone));
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                ConsultDoctorFragment consultDoctorFragment = new ConsultDoctorFragment();
                consultDoctorFragment.Instants(s);
                getActivity().getSupportFragmentManager().
                        beginTransaction().replace(R.id.freeconsultframe, consultDoctorFragment).commit();
                title.setText("春雨医生");
            }
        }.execute();
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

    public String getUrl(String path) {
        URL url = null;
        BufferedReader br = null;
        InputStream in = null;
        StringBuffer sb = new StringBuffer();
        try {
            HttpGet getMethod = new HttpGet(path);
            HttpClient client = new DefaultHttpClient();
            //  HttpUriRequest request =
//			request.addHeader("User-Agent", UESRAGENT_PHONE);
            HttpResponse response = client.execute(getMethod);
            //  url=new URL(path);
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
