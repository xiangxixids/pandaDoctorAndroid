package com.chunyuAdvisory.fragment;

import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.aidl.paderservice;
import com.chunyuAdvisory.bean.Problem_List;
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
 * Created by Administrator on 15-6-15.
 */
public class ConsultDoctorFragment extends Fragment {
    private WebView mwebView;
    private ConsultDoctorFragment mconsultDoctorFragment;
    private String mPath;
    private String phone;
    private String problem_list_json;
    private Problem_List problem_list;
    //全局用户信息
    private SharedPreferences user;
    private paderservice mpaderservice;


    public ConsultDoctorFragment Instants(String url) {
        mconsultDoctorFragment = new ConsultDoctorFragment();
        Bundle bundle = new Bundle();
        bundle.putString("URL", url);
        mconsultDoctorFragment.setArguments(bundle);
        return mconsultDoctorFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consultdoctor, null);
        mwebView = (WebView) view.findViewById(R.id.consulte_doctor_webview);
        mwebView.setWebViewClient(new PandaDoctorWebViewClient());
        mwebView.getSettings().setJavaScriptEnabled(true);
        user = getActivity().getSharedPreferences(PandaGlobalName.UserInfo.info, 0);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = new Bundle();
        bundle = mconsultDoctorFragment.getArguments();
        mPath = bundle.getString("URL");
        phone = user.getString(PandaGlobalName.UserInfo.phone, "");
        mwebView.loadUrl(StaticVar.getAskServiceUrl(phone));

        StaticVar.isConsult = true;
        bindService(getActivity());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StaticVar.isConsult = false;
        try {
            mpaderservice.SaveContentSize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class PandaDoctorWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


    }


    private void bindService(Context context) {
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mpaderservice = paderservice.Stub.asInterface(iBinder);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        Intent intent = new Intent();
        intent.setAction("com.NotifyCationService.notifyCationService");
        getActivity().getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
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
