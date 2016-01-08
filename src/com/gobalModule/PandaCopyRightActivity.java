package com.gobalModule;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.pandadoctor.pandadoctor.R;

public class PandaCopyRightActivity extends Activity {

    public String content;
    public WebView webView;
    public ProgressBar progressBar;
    private ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_panda_copy_right);

//        ActionBar bar = getActionBar();
//        bar.setTitle("免责声明");

        webView = (WebView) findViewById(R.id.panda_copy_right_webView);
        progressBar = (ProgressBar) findViewById(R.id.panda_copy_right_progressBar);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN"
                + "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
                + "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
                + "<title>熊猫医生-免责声明</title></head><body>"
                + "<script src=\"http://www.13xm.com/js/jquery-1.9.0.min.js\">"
                + "</script><script src=\"http://www.13xm.com/js/jquery-migrate-1.0.0.min.js\">"
                + "</script><link rel=\"stylesheet\" href=\"http://www.13xm.com/css/panda1.css\">"
                + "<link rel=\"stylesheet\" href=\"http://www.13xm.com/css/panda2.css\">"
                + "<link rel=\"stylesheet\" href=\"http://www.13xm.com/css/panda3.css\">"
                + "<div ><div style=\"width:310px;margin:auto\"><p >"
                + "<h2 align=\"center\">熊猫医生免责声明 </h2></p>"
                + "<ul><li>1、本网站所载的文/图等内容均出于为公众传播有益健康的信息之目的，均为志愿者医生、知识库机器人爬虫、网站用户提供建立，我们只保证尽力做到内容客观、正确，但不对其科学性、严肃性等作任何形式的保证。如其他媒体、网络或个人从本网下载使用须自负版权等法律责任。</li>"
                + "<br><li>2、对于已经授权本站独家使用提供给本站资料的版权所有人的文章、图片等资料，如需转载使用，需取得本站和版权所有人的同意。 </li>"
                + "<br><li>3、本网站所有信息仅供参考，不做诊断、用药和使用的根据，因理解偏差导致耽误治疗或其他情况，本站不承担任何责任。 </li>"
                + "<br><li>4、 本网站致力于提供正确、完整的健康资讯，但不保证信息的正确性和完整性，且不对因信息的不正确或遗漏导致的任何损失或损害承担责任。本站所提供的任何医药资讯和结论，仅供参考，不能替代医生和其他医务人员的建议，如自行使用本网资料发生偏差，本站概不负责，亦不负任何法律责任。 </li>"
                + "<br><li>5、用户为各种化验单提供解析答案均视为自愿无偿提供，本站不负责内容正确性和版权问题,所有产生的可能纠纷与本站无关。 </li>"
                + "<br><li>6、凡以任何方式注册、登陆本网站或直接、间接使用本网站资料者，视为自愿接受本网站声明的约束。 </li>"
                + "<br><li>7、一切最终解释权归熊猫医生网。 </li></ul></div></div></body></html>";

        webView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.loadData(content, "text/html; charset=UTF-8", null);

    }
}
