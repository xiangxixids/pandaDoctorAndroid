package com.huayandan.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.MainActivity;
import com.chunyuAdvisory.activity.RecordFreeConsultActivity;
import com.gobalModule.PandaLoginActivity;
import com.google.gson.Gson;
import com.hanvon.HWCloudManager;
import com.hanvon.utils.StringUtil;
import com.huayandan.bean.CheckItem;
import com.pandadoctor.pandadoctor.R;
import com.utils.PandaGlobalName;
import com.utils.StaticVar;
import com.utils.UtilTool;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HuayandanResultActivity extends Activity {

    private WebView webView;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private ImageView imageView, back;
    private Button ask;
    private LayoutInflater layoutInflater;
    private TextView textView;

    private String mURL;
    private String content;
    private String assayName;
    private String imgUrl;
    private Uri imageUri;

    private Context context;
    private Handler mHandler;

    private static final int HAN_WANG_PROCESS = 1; // 汉王识别过程
    private static final int GET_HUAYANDAN_DETAIL_PROCESS = 2; // 获取化验单所有项目过程
    private static final int MATCH_HUAYANDAN_PROCESS = 3; // 匹配化验单过程
    private static final int SAVE_SUCCESS = 4; // 化验单保存成功
    private static final int SAVE_FAILED = 5; // 化验单保存成功
    private static final int RENAME_PROCESS = 6;

    private static final String DATETIME = "datetime";

    String picPath = null;
    String result = null;
    private HWCloudManager hwCloudManagerText;
    private List<String> totalList = new ArrayList<String>();// 防止空引用
    private String recordId;

    private ArrayList<JSONObject> mDataList = new ArrayList<JSONObject>();
    private ArrayList<String> mResultDataList = new ArrayList<String>();
    private ArrayList<String> mCheckItemList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        StaticVar.activityList.add(this);

        imgUrl = getIntent().getStringExtra(PandaGlobalName.IMGURL);
        if (imgUrl != null) {
            setContentView(R.layout.activity_huayandan_result);
        } else {
            setContentView(R.layout.activity_huayandan_result_no_img);
        }

        context = this;
        layoutInflater = LayoutInflater.from(this);
        /**
         * your_android_key  是您在开发者中心申请的android_key 并 申请了云文本识别服务
         * 开发者中心：http://developer.hanvon.com/
         */
        hwCloudManagerText = new HWCloudManager(this, PandaGlobalName.HAN_WANG_KEY);

        webView = (WebView) findViewById(R.id.huayandan_result_webView);
        progressBar = (ProgressBar) findViewById(R.id.huayandan_result_progressBar);
        imageView = (ImageView) findViewById(R.id.huayandan_result_imageView);
        back = (ImageView) findViewById(R.id.back);
        ask = (Button) findViewById(R.id.huayandan_ask_chunyu);
        //默认看不到保存病历按钮，只有当解读结果展示后才保存
        ask.setVisibility(View.GONE);
        textView = (TextView) findViewById(R.id.huayandan_result_textView_title);

        assayName = getIntent().getStringExtra(PandaGlobalName.ASSAY_NAME);

        if (textView != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String dateString = df.format(new Date());
            textView.setText(assayName + " " + dateString);
        }

        recordId = getIntent().getStringExtra(PandaGlobalName.RECORD_ID);

        String is_need_ocr = getIntent().getStringExtra(PandaGlobalName.IS_NEED_OCR);

        ask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (UtilTool.isLogin(HuayandanResultActivity.this)) {
                    SharedPreferences user = getSharedPreferences(PandaGlobalName.UserInfo.info, 0);
                    String tel = user.getString(PandaGlobalName.UserInfo.phone, "");
                    String token = user.getString(PandaGlobalName.UserInfo.token, "");
                    String encryptKey = user.getString(PandaGlobalName.UserInfo.encryptKey, "");
                    String res = getResultFromUrl(mURL);
                    String urlStr = PandaGlobalName.insertUserHistoryForAppUrl(tel, res, recordId, token, encryptKey);
                    InsertHistoryTask task = new InsertHistoryTask(urlStr);
                    task.execute();

                } else {
                    Intent intent = new Intent();
                    intent.setClass(HuayandanResultActivity.this, PandaLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        });

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        ActionBar actionBar = getActionBar();
//        actionBar.setTitle("化验单结果");

        if (imageView != null) {
            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent();
                    intent.setClass(HuayandanResultActivity.this, ImageShower.class);
                    intent.putExtra(PandaGlobalName.IMGURL, imgUrl);
                    startActivity(intent);
                }

            });
        }
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HAN_WANG_PROCESS:
                        Toast.makeText(getApplicationContext(), "正在对图像进行处理...", Toast.LENGTH_LONG).show();
                        Uri imageUri = Uri.fromFile(new File(PandaGlobalName.getPicSavePath(), "workupload.jpg"));
                        Bitmap bitmap = decodeUriAsBitmap(imageUri);
                        // 处理图片, 调用汉王进行处理
                        DiscernThread discernThread = new DiscernThread(bitmap);
                        new Thread(discernThread).start();
                        break;
                    case GET_HUAYANDAN_DETAIL_PROCESS:
                        Toast.makeText(getApplicationContext(), "正在解读化验单...", Toast.LENGTH_LONG).show();
                        GetAllItemTask task = new GetAllItemTask(context, mDataList, mResultDataList, mCheckItemList, recordId);
                        task.execute();
                        break;
                    case MATCH_HUAYANDAN_PROCESS:
                        mURL = matchResult(totalList, mDataList, mResultDataList, mCheckItemList, recordId);
                        String url = mURL;
                        //webView.loadUrl(mURL);
                        loadUrl(mURL);
                        webView.setVisibility(View.VISIBLE);
                        //展示保存按钮
                        ask.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        break;
                    case SAVE_SUCCESS:
                        Toast.makeText(getApplicationContext(), "保存化验单成功!", Toast.LENGTH_LONG).show();
                        if (imgUrl != null) // 说明图片是存在的, 这里需要保存图片.
                        {
                            SharedPreferences user = getSharedPreferences(PandaGlobalName.UserInfo.info, 0);
                            String tel = user.getString(PandaGlobalName.UserInfo.phone, "");
                            String token = user.getString(PandaGlobalName.UserInfo.token, "");
                            String encryptKey = user.getString(PandaGlobalName.UserInfo.encryptKey, "");
                            String mUrl = PandaGlobalName.getUserHistoryForAppUrl(tel, token, encryptKey);
                            GetHistoryTask mtask = new GetHistoryTask(mUrl);
                            mtask.execute();

                        }
                        break;
                    case SAVE_FAILED:
                        Toast.makeText(getApplicationContext(), "保存化验单失败!", Toast.LENGTH_LONG).show();
                        break;
                    case RENAME_PROCESS:
                        Bundle data = msg.getData();
                        String gmtTime = data.getString(DATETIME);
                        SharedPreferences user = getSharedPreferences(PandaGlobalName.UserInfo.info, 0);
                        String tel = user.getString(PandaGlobalName.UserInfo.phone, "");
                        String newPicName = PandaGlobalName.getPicNewName(gmtTime, tel, recordId);
                        File file = new File(PandaGlobalName.getPicSavePath(), imgUrl);
                        boolean renameSuccess = file.renameTo(new File(PandaGlobalName.getPicSavePath(), newPicName));
                        if (renameSuccess) {
                            //Toast.makeText(getApplicationContext(), "renameSuccess:"+renameSuccess, Toast.LENGTH_LONG).show();
                        }

                        break;
                    default:
                        break;
                }
            }
        };

        AssetManager assets = getAssets();
        InputStream is = null;
        try {
            is = assets.open("14.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri uri;
        Bitmap bitmap;
        if (imgUrl != null) {
            File file = new File(PandaGlobalName.getPicSavePath(), imgUrl);
            imageUri = Uri.fromFile(file);
            uri = Uri.fromFile(file);
            bitmap = decodeUriAsBitmap(uri);
            imageView.setImageBitmap(bitmap);

        } else {    // 否则, 就显示pandadoctor的logo
            BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeStream(is, null, options);
        }


        try {
            is = assets.open("14.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (is_need_ocr != null && is_need_ocr.equalsIgnoreCase("True")) {
            // 1. 一个是拍照图片已经生成, 用来进行识别
            // 2. 必须获取当前化验单项所有需要的内容, 具体每一个化验单项
            // 3. 汉王识别结果与化验单内容匹配, 并且组合成具体的url出来
            // 4. url发送给服务器, 开始去下载化验单分析结果
            // 5. 所有流程因为会出现在各种不同的线程中, 因此采用handler控制流程
            // 接收从拍照页面进来的值.
            imgUrl = getIntent().getStringExtra(PandaGlobalName.IMGURL);
            // 生成uri, 准备交给汉王识别
            imageUri = Uri.fromFile(new File(PandaGlobalName.getPicSavePath(), imgUrl));
            Message msg = new Message();
            msg.what = HAN_WANG_PROCESS; // 进入阶段1
            mHandler.sendMessage(msg);
        } else {
            mURL = getIntent().getStringExtra(PandaGlobalName.URL);
            //webView.loadUrl(mURL);
            loadUrl(mURL);
            webView.setVisibility(View.VISIBLE);
            //展示保存按钮
            ask.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

    }

    public void loadUrl(String url) {
        if (webView != null) {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            });
            webView.loadUrl(url);
            progressDialog = ProgressDialog.show(context, "", "页面加载中, 请稍后...");
            webView.reload();
        }
    }

    // huolongyema 修改这里
    private String matchResult(List<String> totalList, ArrayList<JSONObject> mDataList, ArrayList<String> mResultDataList, ArrayList<String> mCheckItemList, String assayId) {
        // totalResult 这是汉王识别的原始结果
        // mDataList   这是从服务器获得的当前化验项目的detail 内容
        // mResultDataList 这是保存结果的, 顺序和mDataList一致, 初始化为0, 所以当进入这个方法时, 他全部是0
        // mCheckItemList 这是在拼url时, 化验单的RCRD_id , 顺序和mResultDataList 一致. 顺序不要修改.
        // 这里开始做匹配过程.
        for (String ocrResult : totalList) {
            for (JSONObject object : mDataList) {
                int i = mDataList.indexOf(object);
                Gson gson = new Gson();
                //使用和web一样的类,更方便
                CheckItem checkItem = gson.fromJson(object.toString(), CheckItem.class);
//                //找到该行
                if (ocrResult.contains(checkItem.getItemName()) ||
                        ocrResult.contains(checkItem.getShortName()) ||
                        ocrResult.contains(checkItem.getEngName()) ||
                        ocrResult.contains(checkItem.getEngShortName())) {
                    String[] ocrStr = ocrResult.split(" ");
                    ArrayList<String> testList = new ArrayList<String>();
                    int index = 0;
                    for (int m = 0; m < ocrStr.length; m++) {
                        //.号有点异常的要替换掉
                        ocrStr[m] = ocrStr[m].replace("．", ".");
                        //空格替换掉
                        ocrStr[m] = ocrStr[m].replace(" ", "");
                        //剃掉
                        ocrStr[m] = ocrStr[m].replace("\r\n", "");
                        //踢掉空格等垃圾数据
                        if (ocrStr[m] != null && !"".equals(ocrStr[m])) {
                            testList.add(ocrStr[m]);
                        }
                        //找到化验单名称所在位置
                        if (ocrStr[m].contains(checkItem.getItemName()) || ocrStr[m].contains(checkItem.getShortName())
                                || ocrStr[m].contains(checkItem.getEngName()) || ocrStr[m].contains(checkItem.getEngShortName())) {
                            index = testList.indexOf(ocrStr[m]);
                        }
                    }
                    if (testList.size() == 0) {
                        continue;
                    }
                    //必须从该项名称开始往后找
                    for (int j = index; j < testList.size(); j++) {
                        //寻找测定值
                        //把该行的每列拿出来看看，是否能转成数字，一般来讲第一个数字就是就是测定值，但不排除有出现多个数字的可能(比如ocr把向上向下箭头解读返回1)
                        //理论上来讲只要这个是数字一般都会是"测定值"这个属性，注意trim掉空格防止ocr本身解析误差，且应当在当前索引位置的3位之内
                        if (isNumeric(testList.get(j).trim()) && j <= index + 3) {
                            //测定值已经有了
                            if (checkItem.getCeding() != null) {
                                //新的值不是1，即不是ocr将箭头误解为1，那么认为新的数字才是测定值
                                if (Float.parseFloat(testList.get(j)) != 1) {
                                    checkItem.setCeding(Float.parseFloat(testList.get(j)));
                                }
                            } else {
                                //如果是数字那么认为找到了这个检查项的测定值
                                checkItem.setCeding(Float.parseFloat(testList.get(j)));
                            }
                        }
                        //寻找参考范围
                        String[] lowhight = {""};
                        //通常认为参考范围也是至少是在第三个位置以后
                        if (j >= index + 2 && j < index + 5) {
                            //判断是不是上下限，通过"-"连接的，如20-70
                            if (testList.get(j).contains("一")) {
                                lowhight = testList.get(j).split("一");
                            } else if (testList.get(j).contains("一一")) {
                                lowhight = testList.get(j).split("一一");
                            } else if (testList.get(j).contains("--")) {
                                lowhight = testList.get(j).split("--");
                            } else if (testList.get(j).contains("-")) {
                                lowhight = testList.get(j).split("-");
                            } else if (testList.get(j).contains("~")) {  //也有可能是通过"~"关联，如20~70
                                lowhight = testList.get(j).split("~");
                            } else if (testList.get(j).contains(">")) {//也有可能是>如>20，变成20-10000
                                lowhight = testList.get(j).split(">");
                                if (lowhight.length == 2) {
                                    lowhight[0] = lowhight[1];
                                    lowhight[1] = "10000";
                                }
                            } else if (testList.get(j).contains("<")) {//也有可能<10，变成0-10   这两种情况基本不会发生，当然不排除这种可能，先做好预防
                                lowhight = testList.get(j).split("<");
                                if (lowhight.length == 2) {
                                    lowhight[0] = "0";
                                }
                            }
                            if (lowhight.length == 2) {
                                lowhight[0] = specialStrFilter(lowhight[0]);
                                lowhight[1] = specialStrFilter(lowhight[1]);
                                //判断此项的上下限，并认为找到了赋值
                                if (isNumeric(lowhight[0]) && isNumeric(lowhight[1])) {
                                    checkItem.setLowUnit((Float.parseFloat(lowhight[0])));
                                    checkItem.setHighUnit((Float.parseFloat(lowhight[1])));
                                }
                            }
                        }
                        if (checkItem.getCeding() != null && checkItem.getLowUnit() != null && checkItem.getHighUnit() != null) {
                            if (checkItem.getCeding() < checkItem.getLowUnit()) {
                                mResultDataList.set(i, "3");
                            } else if (checkItem.getCeding() > checkItem.getHighUnit()) {
                                mResultDataList.set(i, "1");
                            }
                        }
                    }
                }
            }
        }
        String res = stringJion(mResultDataList, ",");
        String checkItemIds = stringJion(mCheckItemList, ",");
        return PandaGlobalName.getAssayResultForAppUrl(assayId, checkItemIds, res);
    }

    /**
     * 专门对化验单的高低数值特殊字符处理
     *
     * @param str
     * @return
     */
    private String specialStrFilter(String str) {
        //ocr通常会把0识别成o，替换回去
        str = str.trim().replace("o", "0");
        //ocr通常会把0识别成O，替换回去
        str = str.replace("O", "0");
        str = str.replace("。", "0");
        //ocr通常会把1识别成】，替换回去
        str = str.replace("】", "1");
        //ocr通常会把1识别成【，替换回去
        str = str.replace("【", "1");
        //可能会有冒号
        str = str.replace(":", "");
        return str;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.huayandan_result_menu, menu);
        return true;
    }

    private Bitmap decodeUriAsBitmap(Uri uri) {

        Bitmap bitmap = null;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 4;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, bitmapOptions);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * 监听Back键按下事件,方法1:
     * 注意:
     * super.onBackPressed()会自动调用finish()方法,关闭
     * 当前Activity.
     * 若要屏蔽Back键盘,注释该行代码即可
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        initPopupWindow();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (UtilTool.isLogin(this)) {
                    SharedPreferences user = getSharedPreferences(PandaGlobalName.UserInfo.info, 0);
                    String tel = user.getString(PandaGlobalName.UserInfo.phone, "");
                    String token = user.getString(PandaGlobalName.UserInfo.token, "");
                    String encryptKey = user.getString(PandaGlobalName.UserInfo.encryptKey, "");
                    String res = getResultFromUrl(mURL);
                    String urlStr = PandaGlobalName.insertUserHistoryForAppUrl(tel, res, recordId, token, encryptKey);
                    InsertHistoryTask task = new InsertHistoryTask(urlStr);
                    task.execute();

                } else {
                    Intent intent = new Intent();
                    intent.setClass(HuayandanResultActivity.this, PandaLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                return true;
            case R.id.action_back_to_top:
                Intent intent = new Intent(HuayandanResultActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getResultFromUrl(String url) {
        // TODO Auto-generated method stub
        if (url.length() >= 2) {
            String res = "";
            res = url.split("res=")[1];
            return res;
        }

        return null;
    }

    private void initPopupWindow() {

        Builder builder = new Builder(context);
        builder.setMessage("是否要直接返回首页?");
        builder.setTitle("提示");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "返回首页", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(HuayandanResultActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        builder.setNeutralButton("返回上一层", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "返回上一层", Toast.LENGTH_LONG).show();
                HuayandanResultActivity.this.finish();
            }
        });
        builder.create().show();
    }

    public class DiscernThread implements Runnable {
        private Bitmap bitmap;

        public DiscernThread(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        public void run() {
            try {
                /**
                 * 调用汉王云文本识别方法
                 */
                totalList = picProcess(bitmap);
                // 汉王识别结束, 开始进入阶段二
                Message msg = new Message();
                msg.what = GET_HUAYANDAN_DETAIL_PROCESS; // 进入阶段2
                mHandler.sendMessage(msg);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    /**
     * 图像处理，如何保证拍出来的照片是水平的？
     * 现在我们技术还没发做倾斜校正，拍照时给用户一个水平参考线，而且只支持用户横着拍
     * 本算法经测试，只适合化验单列表项内容（原因：这些行有文字的行像素总和不会相差太大）
     */
    public List<String> picProcess(Bitmap bmp) {
        List<String> resultList = new ArrayList<String>();
        try {
            //Bitmap bmp =BitmapFactory.decodeFile(picPath);
            //Bitmap bmp =BitmapFactory.decodeUriAsBitmap();
//			Matrix matrix = new Matrix();
//			matrix.reset();
//			//默认逆时针旋转90，因为要求用户是横着拍
//			matrix.setRotate(-90);
//			bmp = Bitmap.createBitmap(bmp,0,0, bmp.getWidth(), bmp.getHeight(),matrix, true);
//			saveMyBitmap("xuanzhuan", bmp);
            Mat rgbMat = Utils.bitmapToMat(bmp);
            //用完立马释放内存，必须这么做，不然下一次再读入会出现内存溢出的情况
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
            }
            Size newSize = getNewSize(rgbMat.rows(), rgbMat.cols());

            if (newSize != null) {
                Imgproc.resize(rgbMat, rgbMat, newSize);
            }
            int cols = rgbMat.cols();
            int rows = rgbMat.rows();
            Mat grayMat = new Mat(rows, cols, CvType.CV_8UC1, new Scalar(0));
            Mat binaryMat = new Mat(rows, cols, CvType.CV_8UC1, new Scalar(0));
            Mat grayCut = new Mat(rows, cols, CvType.CV_8UC1, new Scalar(0));


            //将彩色图像数据转换为灰度图像数据并存储到grayMat中
            Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);
            //立马释放内存
            rgbMat.release();

            //先二值化，使用OTSU算法，效果会很好,使用OTSU那么第三个参数将会不生效
            Imgproc.threshold(grayMat, binaryMat, 150, 255, Imgproc.THRESH_OTSU);
            //黑白反转
            Imgproc.threshold(binaryMat, binaryMat, 150, 255, Imgproc.THRESH_BINARY_INV);
            int channels = binaryMat.channels();

            double min = 10000d;
            double max = 0d;

            int total = cols * rows * channels;
            byte[] b = new byte[total];
            //一次性读出，将mat变成java数组，处理效率更高。里边的值只会是-1和0，-1表示255灰度（即反转后的黑色）
            binaryMat.get(0, 0, b);
            //每行灰度总值
            double[] lineTotalGray = new double[rows];
            int yNum = 0;
            for (int k = 0; k < total; k++) {
                byte in = b[k];
                if (in == -1) {
                    lineTotalGray[yNum] = lineTotalGray[yNum] + 255;
                }
                //多维数组变成一维数组后，按宽来统计，准备换行
                if (k != 0 && k % cols == 0) {
                    if (lineTotalGray[yNum] < min) {
                        min = lineTotalGray[yNum];
                    }
                    if (lineTotalGray[yNum] > max) {
                        max = lineTotalGray[yNum];
                    }
                    ++yNum;
                }
            }


            double average = (max + min) / 2;
//			//找到切割点
            ArrayList<Integer> yCut = new ArrayList<Integer>();
            for (int y = 0; y < rows; y++) {
                if (y + 3 < rows) {
                    //本次的寻找的切割点必须在上一个切割点10个像素以外
                    if (yCut != null && yCut.size() > 0 && (y - yCut.get(yCut.size() - 1) < 15)) {
                        continue;
                    }
                    if ((lineTotalGray[y] == min) && lineTotalGray[y + 1] > min && lineTotalGray[y + 2] > min && lineTotalGray[y + 3] > min) {
                        yCut.add(y);
                        continue;
                    }
                    //一半的一半
                    if ((lineTotalGray[y] > min) && (lineTotalGray[y] < average / 2) && (lineTotalGray[y + 1] > min) && (lineTotalGray[y + 1] < average / 2)
                            && (lineTotalGray[y + 2] > min) && (lineTotalGray[y + 2] < average / 2) && (lineTotalGray[y + 3] > average / 2)) {
                        yCut.add(y);
                        continue;
                    }
                }
            }
            //增加顶部，保证最后一行被切割
            yCut.add(rows);

            List<Bitmap> list = new ArrayList<Bitmap>();
            //剪切
            for (int k = 0; k < yCut.size() - 1; k++) {
                int cutHeight = yCut.get(k + 1) - yCut.get(k);
                //高度低于10的区域不剪切
                if (cutHeight < 10) {
                    continue;
                }
                grayCut = grayMat.submat(yCut.get(k), yCut.get(k + 1), 0, cols);
                Mat fourGray = change2Four(grayCut);
                Bitmap cutMap = Bitmap.createBitmap(fourGray.width(), fourGray.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(fourGray, cutMap);
                list.add(cutMap);
                // 测试切分的图片, 所有的bitmap 图片
                saveMyBitmap("" + k, cutMap);
            }
            for (Bitmap cutMap : list) {
                result = hwCloudManagerText.textLanguage("chns", cutMap);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String code = (String) jsonObject.get("code");
                    //识别成功
                    if (code != null && code.equals("0")) {
                        String textResult = (String) jsonObject.get("textResult");
                        if (StringUtil.isEmpty(textResult) || "\r\n".equals(textResult)) {
                            continue;
                        }
                        //替换掉无用数据
                        textResult.replace("\r\n", "");
                        resultList.add(textResult);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            grayMat.release();
            binaryMat.release();
            grayCut.release();
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 准备压缩后的大小
     */
    public Size getNewSize(int h, int w) {
        if (h <= 1500 && w <= 1500) {
            return null;
        } else {
            float rateH = (float) h / (float) 1500;
            float rateW = (float) w / (float) 1500;
            if (rateH > rateW) {
                h = (int) (h / rateH);
                w = (int) (w / rateH);
            } else {
                h = (int) (h / rateW);
                w = (int) (w / rateW);
            }
        }
        return (new Size(w, h));

    }

    public void saveMyBitmap(String bitName, Bitmap mBitmap) {
        File f = new File("/sdcard/" + bitName + ".jpg");
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block

        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Mat change2Four(Mat oneChannelMat) {
        List<Mat> oneChannelMatList = new ArrayList<Mat>();//list类表用于存放单通道的Mat
        oneChannelMatList.add(oneChannelMat);  //添加4个单通道的Mat，A通道
        oneChannelMatList.add(oneChannelMat);//G通道
        oneChannelMatList.add(oneChannelMat);//判断后确定是B。

        //第四个通道我建立了一个新的单通道Mat（Mat没有内容），结果对图片效果无影响，所以判断最后一个通道是a通道
        oneChannelMatList.add(new Mat(oneChannelMat.rows(), oneChannelMat.cols(), CvType.CV_8UC1, new Scalar(0)));//a通道

        //创建一个4通道的Mat
        Mat fourChannelsMat = new Mat(oneChannelMat.rows(), oneChannelMat.cols(), CvType.CV_8UC4, new Scalar(0));
        //将四个单通道融合成一个4通道图片
        Core.merge(oneChannelMatList, fourChannelsMat);

        return fourChannelsMat;

    }

    class GetHistoryTask extends AsyncTask<Void, Integer, String> {

        private String url;
        private String res;
        private String datetime;

        public GetHistoryTask(String url) {
            this.url = url;
        }

		/*
         * UI 线程中, 在doInBackground 之前
		 * */

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        /*
         * 后台线程中
         * */
        @Override
        protected String doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            BufferedReader in = null;
            String result = "";
            try {
                URL url = new URL(this.url);
                Log.i(PandaGlobalName.TAG, "huayandan history url:" + url);
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

                JSONObject jsonObject = new JSONObject(result);

                String gmtCreate = jsonObject.getJSONObject("result").getJSONArray("userHistoryList").getJSONObject(0).getString("gmtCreate");

//                this.res = jsonObject.getString("success");
                this.datetime = gmtCreate;

                Log.i(PandaGlobalName.TAG, "Huayandan pager Json data parser finished");


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
        protected void onPostExecute(String integer) {
            progressBar.setVisibility(View.GONE);
            Message msg = new Message();
            msg.what = RENAME_PROCESS;
            Bundle data = new Bundle();
            data.putString(DATETIME, this.datetime);
            msg.setData(data);
            mHandler.sendMessage(msg);
        }

    }

    class InsertHistoryTask extends AsyncTask<Void, Integer, Integer> {

        private String url;
        private String res;

        public InsertHistoryTask(String url) {
            this.url = url;
        }

		/*
         * UI 线程中, 在doInBackground 之前
		 * */

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
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
                URL url = new URL(this.url);
                Log.i(PandaGlobalName.TAG, "huayandan history url:" + url);
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

                JSONObject jsonObject = new JSONObject(result);
                //JSONArray jsonArray = new JSONArray(result);

                this.res = jsonObject.getString("success");

                Log.i(PandaGlobalName.TAG, "Huayandan pager Json data parser finished");

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
            progressBar.setVisibility(View.GONE);
            Message msg = new Message();
            if (this.res.equalsIgnoreCase("true")) {
                msg.what = SAVE_SUCCESS;
                View view1 = layoutInflater.inflate(R.layout.contract_dialog, null);
                TextView freeconsult = (TextView) view1.findViewById(R.id.freeconsult);
                TextView backfirst = (TextView) view1.findViewById(R.id.backfirst);

                freeconsult.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StaticVar.ask = true;
                        Intent intent = new Intent(HuayandanResultActivity.this, RecordFreeConsultActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                backfirst.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HuayandanResultActivity.this, MainActivity.class);
                        startActivity(intent);
                        for (Activity a : StaticVar.activityList) {
                            a.finish();
                        }

                    }
                });
                ask.setVisibility(View.GONE);
                final Dialog dialog = new AlertDialog.Builder(HuayandanResultActivity.this).create();

                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setContentView(view1);
                dialog.getWindow().setLayout(Dp2Px(HuayandanResultActivity.this, 260),
                        Dp2Px(HuayandanResultActivity.this, 100));

            } else {
            }
            mHandler.sendMessage(msg);
        }

    }

    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    class GetAllItemTask extends AsyncTask<Void, Integer, Integer> {

        private Context context;
        private ArrayList<JSONObject> mDataList;
        private ArrayList<String> mResultDataList;
        private ArrayList<String> mCheckItemList;
        private String recordId;

        public GetAllItemTask(Context context, ArrayList<JSONObject> mDataList, ArrayList<String> mResultDataList, ArrayList<String> mCheckItemList, String recordId) {
            this.context = context;
            this.mDataList = mDataList;
            this.mResultDataList = mResultDataList;
            this.mCheckItemList = mCheckItemList;
            this.recordId = recordId;
        }

		/*
         * UI 线程中, 在doInBackground 之前
		 * */

        @Override
        protected void onPreExecute() {
            //listView.setVisibility(View.GONE);
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
                URL url = new URL(PandaGlobalName.getAssayItemsListForAppUrl(recordId));
                Log.i(PandaGlobalName.TAG, "huayandan detail url:" + url);
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

                //JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    mDataList.add(object);
                    mResultDataList.add("0");
                    mCheckItemList.add(object.getString(PandaGlobalName.RECORD_ID));
                }

                Log.i(PandaGlobalName.TAG, "Huayandan pager Json data parser finished");

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
            //listView.setVisibility(View.VISIBLE);
            //HuanyandanFillActivityAdapter adapter = new HuanyandanFillActivityAdapter(this.context, this.mDataList, this.mResultDataList, this.mCheckItemList, this.recordId);
            //listView.setAdapter(adapter);
            Message msg = new Message();
            msg.what = MATCH_HUAYANDAN_PROCESS; // 进入阶段3
            mHandler.sendMessage(msg);
        }

    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[\\d.]+");
        Matcher isNum = pattern.matcher(str);

        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 拼接
     *
     * @param list
     * @param jionFlag
     * @return
     */
    private static String stringJion(ArrayList<String> list, String jionFlag) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            stringBuffer.append(list.get(i));
            if (i != (list.size() - 1)) {
                stringBuffer.append(jionFlag);
            }
        }
        return stringBuffer.toString();
    }
}
