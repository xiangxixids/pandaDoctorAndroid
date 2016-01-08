package com.chunyuAdvisory.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chunyuAdvisory.bean.Problem_List;
import com.chunyuAdvisory.bean.Problem_List_Item;
import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 15-6-14.
 */
public class FreeConsultListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Problem_List_Item> mlist = new ArrayList<Problem_List_Item>();

    private Problem_List mproblem_list;
    private Context mcontext;
    private SharedPreferences user;

    public FreeConsultListAdapter(Context context) {
        if (context != null) {
            layoutInflater = LayoutInflater.from(context);
            mcontext = context;
        user = context.getSharedPreferences(PandaGlobalName.UserInfo.info, 0);
        }
    }

    public void upData(Problem_List problem_list) {

        mproblem_list = problem_list;
        mlist = mproblem_list.getProblem_list();
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        if (mlist != null) {
            return mlist.size();
        }
        return 0;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.freeconsult_list_item, null);
        TextView content = (TextView) view.findViewById(R.id.content);
        TextView state = (TextView) view.findViewById(R.id.state);
        final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);

        content.setText(mlist.get(i).getTitle());
        state.setText(mlist.get(i).getStatus());
        line.setTag(mlist.get(i).getProblem_id());

//        line.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new AsyncTask<String, Void, String>() {
//
//                    @Override
//                    protected String doInBackground(String... strings) {
//                        String phone = user.getString(PandaGlobalName.UserInfo.phone, "");
//                        String ask_detail_json = getUrl(StaticVar.getProblemDetailUrl(phone, mlist.get(i).getProblem_id()));
//                        return ask_detail_json;
//                    }
//
//                    @Override
//                    protected void onPostExecute(String s) {
//                        super.onPostExecute(s);
//                        if(mlist.get(i).getStatus().equals("已关闭")){
//                        Intent intent = new Intent(mcontext, ConsultDetailActivity.class);
//                        intent.putExtra("json", s);
//                        intent.putExtra("head", mlist.get(i).getAvatar());
//                        mcontext.startActivity(intent);
//                        }else {
//                            new AsyncTask<String, Void, String>() {
//                                @Override
//                                protected String doInBackground(String... strings) {
//                                    String phone = user.getString(PandaGlobalName.UserInfo.phone, "");
//                                    return parseUrl(StaticVar.getAskServiceUrl(phone));
//                                }
//
//                                @Override
//                                protected void onPostExecute(String s) {
//                                    super.onPostExecute(s);
//
//                                    ConsultDoctorFragment consultDoctorFragment = new ConsultDoctorFragment();
//                                    consultDoctorFragment.Instants(s);
//
//                                            beginTransaction().replace(R.id.freeconsultframe, consultDoctorFragment).commit();
//
//                                }
//                            }.execute();
//                        }
//                    }
//                }.execute();
//            }
//        });


        return view;
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
