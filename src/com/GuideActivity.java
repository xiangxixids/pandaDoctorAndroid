package com;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import com.pandadoctor.pandadoctor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 15-7-24.
 */
public class GuideActivity extends Activity {
    private ViewPager guidepage;

    private List<View> mList = new ArrayList<View>();
    private Vibrator mvibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide_layout);

        initView();
        setData(this);
        setGuidepage();

        new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }).start();

    }

    public void initView() {
        guidepage = (ViewPager) findViewById(R.id.guidepager);
    }

    public void setData(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.guide_item_layout, null);
        ImageView image = (ImageView) view.findViewById(R.id.itemimg);
//        Animation shake = AnimationUtils.loadAnimation(GuideActivity.this,
//                R.anim.shark_transfer_accounts);
//        image.startAnimation(shake);
//        mvibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        long[] pattern = { 100, 400, 100, 400 }; // 停止 开启 停止 开启
//        mvibrator.vibrate(pattern, -1);

        image.setImageResource(R.drawable.boot);
        mList.add(view);
    }

    public void setGuidepage() {
        GuidePagerAdapter guidePagerAdapter = new GuidePagerAdapter(mList);
        guidepage.setAdapter(guidePagerAdapter);

    }

    public class GuidePagerAdapter extends PagerAdapter {
        private List<View> mlist = new ArrayList<View>();

        public GuidePagerAdapter(List<View> list) {
            mlist = list;
        }

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return true;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mlist.get(position));
            return mlist.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            container.removeView(mlist.get(position));
        }
    }
}
