package com.huayandan.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;

import java.io.File;


public class ImageShower extends Activity {
	
	private ImageView imgView;
	private String imgUrl;
	
	/**
	 * 自定义的ImageView控制，可对图片进行多点触控缩放和拖动
	 */
	private ZoomImageView zoomImageView;

	/**
	 * 待展示的图片
	 */
	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageshower);
		
		zoomImageView = (ZoomImageView) findViewById(R.id.zoom_image_view);
		imgUrl = getIntent().getStringExtra(PandaGlobalName.IMGURL);
		// 取出图片路径，并解析成Bitmap对象，然后在ZoomImageView中显示
		if(imgUrl!=null)
		{
            File file = new File(PandaGlobalName.getPicSavePath(), imgUrl);
            Uri uri = Uri.fromFile(file);
    		bitmap = decodeUriAsBitmap(uri);
    		zoomImageView.setImageBitmap(bitmap);
		}
		
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 记得将Bitmap对象回收掉
		if (bitmap != null) {
			bitmap.recycle();
		}
	}
	
	private Bitmap decodeUriAsBitmap(Uri uri) {

        Bitmap bitmap = null;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 2;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri),null,bitmapOptions);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish();
		return true;
	}

}
