package com;

import android.content.*;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Toast;
import com.aidl.paderservice;
import com.healthClassRoom.HealthFragment;
import com.huayandan.fragment.CaptureFragment;
import com.myReocrd.fragment.RecordFragment;
import com.pandadoctor.pandadoctor.R;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.UmengRegistrar;
import com.utils.PagerSlidingTabStrip;
import com.utils.PandaGlobalName;
import com.utils.StaticVar;

import java.lang.reflect.Field;

/**
 * Panda Doctor
 *
 * @author virgo
 */
public class MainActivity extends FragmentActivity {

    /**
     * 拍化验单界面的Fragment
     */
    private CaptureFragment capFragment;

    /**
     * 病历界面的Fragment
     */
    private RecordFragment recordFragment;

    /**
     * 健康讲堂界面的Fragment
     */
    private HealthFragment healthFragment;
    /**
     * PagerSlidingTabStrip的实例
     */
    private PagerSlidingTabStrip tabs;

    /**
     * 获取当前屏幕的密度
     */


    private DisplayMetrics dm;

    private String mUserName = "";
    private SharedPreferences user;
    private paderservice mpaderservice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        StaticVar.activityList.add(this);

        AnalyticsConfig.setAppkey(PandaGlobalName.YOU_MENG_KEY);
        String device_token = UmengRegistrar.getRegistrationId(this);
        Log.e("device_token", device_token);

        //   setOverflowShowingAlways();
        dm = getResources().getDisplayMetrics();
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabs.setViewPager(pager);
        setTabsValue();
//        registerMenuChangeBoardcast();

        //判断打开了多少次APP
        StaticVar.isOpenActivity = true;
        bindService(this);

    }

    private void bindService(Context context) {
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mpaderservice = paderservice.Stub.asInterface(iBinder);
                try {
                    mpaderservice.SendNotifyCation();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        Intent intent = new Intent();
        intent.setAction("com.NotifyCationService.notifyCationService");
        context.getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
//		// 设置Tab的分割线是透明的
//		tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        //按钮字体颜色默认为白色
        tabs.setTextColor(Color.parseColor("#FFFFFF"));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#FF6347"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabs.setSelectedTextColor(Color.parseColor("#FF6347"));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"拍化验单", "我的病历", "健康讲堂"};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (capFragment == null) {
                        capFragment = new CaptureFragment();
                    }
                    return capFragment;
                case 1:
                    if (recordFragment == null) {
                        recordFragment = new RecordFragment();
                    }
                    return recordFragment;
                case 2:
                    if (healthFragment == null) {
                        healthFragment = new HealthFragment();
                    }
                    return healthFragment;
                default:
                    return null;
            }
        }

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        MenuItem username = (MenuItem) menu.findItem(R.id.username);
//        if (mUserName.length() > 0) {
//            username.setTitle(mUserName);
//        }
//        return true;
//    }

    // 声明一个广播receiver
//    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            System.out.println(action.toString());
//            getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
//        }
//
//    };

    // 注册广播事件, 也就是action
//    public void registerMenuChangeBoardcast() {
//        IntentFilter myFilter = new IntentFilter();
//        myFilter.addAction(PandaGlobalName.BROADCAST_UPDATE_MENU);
//        registerReceiver(mBroadcastReceiver, myFilter);
//    }

//    @Override
//      public boolean onMenuOpened(int featureId, Menu menu) {
//        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
//            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
//                try {
//                    Method m = menu.getClass().getDeclaredMethod(
//                            "setOptionalIconsVisible", Boolean.TYPE);
//                    m.setAccessible(true);
//                    m.invoke(menu, true);
//                } catch (Exception e) {
//                }
//            }
//        }
//        return super.onMenuOpened(featureId, menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.username:
                // 记录已经全局登录了
                SharedPreferences user = getSharedPreferences(PandaGlobalName.UserInfo.info, MODE_PRIVATE);
                Editor editor = user.edit();
                editor.putString(PandaGlobalName.UserInfo.login, PandaGlobalName.UserInfo.notLogin);
                editor.commit();
                // 提交记录结束
                Toast.makeText(this, "注销成功", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(PandaGlobalName.BROADCAST_UPDATE_MENU);
                intent1.putExtra(PandaGlobalName.UserInfo.name, "11111");
                sendBroadcast(intent1);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StaticVar.isOpenActivity = false;
    }
}