package com.huayandan.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.pandadoctor.pandadoctor.R;
import com.utils.StaticVar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;


public class HuayandanCameraActivity extends Activity {
    private Button test;
    private ImageView img, back, saveimg, line, camera_on;
    private SurfaceView surfaceView;
    private Camera camera;
    boolean iscamera_no = false;
    private Bundle bundle;
    private Camera.Parameters parameters = null;
    private LayoutInflater layoutInflater;
    private TextView cancel, save, bz;
    private RelativeLayout rel;
    private LinearLayout saveline;

    String imageFilePath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/mypicture.jpg";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_huayandan_camera);

        StaticVar.activityList.add(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//拍照过程屏幕一直处于高亮

        initview();
        initListener();

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        Log.e("wh", width + "  " + height);

        surfaceView.getHolder().setFixedSize(800, 1600);
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //   surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.setFocusable(true);
        surfaceView.getHolder().addCallback(new SurfaceCallback());


        File imageFile = new File(imageFilePath);
        if (imageFile.exists()) {
            imageFile.delete();
        }

    }

    public void initview() {
        layoutInflater = LayoutInflater.from(this);
        test = (Button) findViewById(R.id.test);
        back = (ImageView) findViewById(R.id.back);
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        saveimg = (ImageView) findViewById(R.id.saveimg);
        bz = (TextView) findViewById(R.id.bz);
        line = (ImageView) findViewById(R.id.line);

        saveline = (LinearLayout) findViewById(R.id.saveline);
        cancel = (TextView) findViewById(R.id.cancel);
        save = (TextView) findViewById(R.id.save);
        camera_on = (ImageView) findViewById(R.id.camera_on);
    }

    public void initListener() {
        camera_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iscamera_no == false) {
                    iscamera_no = true;
                    camera_on.setImageResource(R.drawable.camera_deng_off);
                    initCamera();
                } else {
                    iscamera_no = false;
                    camera_on.setImageResource(R.drawable.camera_deng_on);
                    initCamera();
                }
            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveline.setVisibility(View.VISIBLE);
                camera.takePicture(null, null, new MyPictureCallback());
                test.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
                bz.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                camera_on.setVisibility(View.GONE);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                StaticVar.isCamera = false;
                camera.stopPreview();
                camera.release();
                camera = null;


            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveline.setVisibility(View.GONE);
                test.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
                bz.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                camera_on.setVisibility(View.VISIBLE);
                camera.startPreview();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                StaticVar.isCamera = true;
            }
        });
    }


    private class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {

                camera = null;
                camera = Camera.open(); // 打开摄像头
                camera.setPreviewDisplay(surfaceHolder); // 设置用于显示拍照影像的SurfaceHolder对象
                camera.setDisplayOrientation(getPreviewDegree(HuayandanCameraActivity.this));
                camera.startPreview(); // 开始预览
                initCamera();
            } catch (IOException e) {
                // Log.e(TAG, e.toString());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {

                    if (success) {
                        initCamera();//实现相机的参数初始化
                        camera.cancelAutoFocus();
                    }
                }

            });

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (camera != null) {

                camera.stopPreview();
                camera.release();
                camera = null;

            }
        }
    }

    private void initCamera() {
        parameters = camera.getParameters();
        //  parameters.setPictureSize(surfaceView.getWidth(), surfaceView.getHeight());  // 部分定制手机，无法正常识别该方法。
        List<String> list = camera.getParameters().getSupportedFocusModes();
        for (String s : list) {
            // Log.e("st",s+"\n");
            if (s.equals("auto")) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);//1连续对焦
            }

            if (s.equals("continuous-picture")) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
            }
        }
        List<Camera.Size> listsize = parameters.getSupportedPictureSizes();
        for (Camera.Size s : listsize) {

            if (s.width > 1500) {
                parameters.setPictureSize(s.width, s.height);
                break;
            }
        }
        parameters.setPreviewFrameRate(20);  //设置每秒显示4帧
        parameters.setJpegQuality(100); // 设置照片质量
        parameters.setPictureFormat(PixelFormat.JPEG);
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        if (iscamera_no == false) {
//           List<String> lists= parameters.getSupportedFocusModes();
//            for ()

            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        camera.setParameters(parameters);
        camera.startPreview();
        camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上

    }

    //控制图像的正确显示方向
    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }

    }

    //实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            Log.e("Came_e", "图像出错");
        }
    }


    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    private final class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                bundle = new Bundle();
                bundle.putByteArray("bytes", data); //将图片字节数据保存在bundle当中，实现数据交换
                saveToSDCard(data); // 保存图片到sd卡中
//
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public static void saveToSDCard(byte[] data) throws IOException {


        String filename = "workupload.jpg";
        File fileFolder = new File(Environment.getExternalStorageDirectory() + "/pandadotor");
        Log.e("file", fileFolder.getAbsolutePath());
        if (!fileFolder.exists()) {
            fileFolder.mkdir();
        }
        File jpgFile = new File(fileFolder, filename);
        if (!jpgFile.exists()) {
            jpgFile.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
        outputStream.write(data); // 写入sd卡中
        outputStream.close(); // 关闭输出流


    }

    public Bitmap readImg() {
        Bitmap bitmap = null;
        String filename = "workupload.jpg";
        File fileFolder = new File(Environment.getExternalStorageDirectory(), filename);
        String filepath = Environment.getExternalStorageDirectory() + filename;
        if (fileFolder.exists()) {

            bitmap = BitmapFactory.decodeFile(filepath);

        }
        return bitmap;
    }


    public void saveBitmap(Bitmap bm) {

        File f = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/mypicture1.jpg");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.e("orientation", ExifInterface.ORIENTATION_TRANSPOSE + "\n" +
                    ExifInterface.ORIENTATION_TRANSVERSE + "\n" +
                    ExifInterface.ORIENTATION_UNDEFINED
            );

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("degree", degree + "");
        return degree;
    }

    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees == 0) {
            return b;
        }
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth(),
                    (float) b.getHeight());
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                        b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle(); // Android开发网再次提示Bitmap操作完应该显示的释放
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
// Android123建议大家如何出现了内存不足异常，最好return 原始的bitmap对象。.
            }
        }
        return b;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != camera) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
