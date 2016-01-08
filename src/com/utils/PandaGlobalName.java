package com.utils;

import java.net.URLEncoder;

import android.os.Environment;
import android.util.Base64;

public class PandaGlobalName {

    public static final String TAG = "pandaDoc";
    public static final String POSITION = "position";
    //化验单分类英文名
    public static final String SORT_ENGLISH_NAME = "subjectName";
    //化验单所属分类id
    public static final String SUBJECT_ID = "subjectId";
    //化验单中文名
    public static final String ASSAY_NAME = "assayName";
    //化验单对应的id
    public static final String ASSAY_ID = "assayId";

    //数据库id
    public static final String RECORD_ID = "id";
    //某项意义、含义
    public static final String SIGNIFICANCE = "significance";
    //化验单某检查单项名称
    public static final String ASSAY_ITEM_THUMBNAIL_NAME = "itemName";
    //
    public static final String ASSAY_RESULT = "result";
    //该化验单是否是ocr类化验单
    public static final String IS_NEED_OCR = "needOcr";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String URL = "url";
    public static final String GMT_CREATE = "gmtCreate";
    public static final String GMT_CREATE_STR = "gmtCreateStr";
    public static final String FLAG = "flag";
    public static final String BROADCAST_UPDATE_MENU = "UPDATE_MENU";
    public static final String YOU_MENG_KEY = "555c0f5b67e58e0497004fed";
    public static final String HAN_WANG_KEY = "ddafc56b-81b3-4605-b77b-f39ec6a152c4";

    public static final String IMGURL = "imgUrl";

    public class UserInfo {
        public static final String info = "user_info";
        public static final String name = "user_name";
        public static final String phone = "user_phone";
        public static final String token = "token";
        public static final String encryptKey = "encryptKey";
        public static final String passwd = "user_password";
        public static final String login = "login";
        public static final String hasLogin = "hasLogin";
        public static final String notLogin = "notLogin";
    }

    //获得化验单分类(9大分类)
    public static final String getAssaySortForAppUrl() {
        return Server.url + "/" + Server.paperSortForApp;
    }

    //查询某个分类下所有化验单
    public static final String getAssayListForAppUrl(String recordId) {
        return Server.url + "/" + Server.paperForApp + "?"
                + SUBJECT_ID + "=" + recordId;
    }

    //根据化验单id查询拥有的检查项
    public static final String getAssayItemsListForAppUrl(String recordId) {
        return Server.url + "/" + Server.checkItemsForApp + "?"
                + ASSAY_ID + "=" + recordId;
    }

    //根据化验单内容解读结果
    public static final String getAssayResultForAppUrl(String recordId, String checkItems, String resultList) {
        return Server.url + "/" + Server.resultForApp + "?"
                + ASSAY_ID + "=" + recordId
                + "&checkItemIds=" + checkItems
                + "&res=" + resultList;
    }

    //注册接口
    public static final String getRegisterForAppUrl(String phone, String passwd, String nickName) {
        //编码两次
        nickName = URLEncoder.encode(URLEncoder.encode(nickName));
        return Server.url + "/" + Server.registerForApp
                + "?tel=" + phone
                + "&passWord=" + passwd
                + "&userName=" + nickName;
    }

    //登陆接口
    public static final String getLoginForAppUrl(String phone, String passwd) {
        return Server.url + "/" + Server.loginForAPP
                + "?tel=" + phone
                + "&passWord=" + passwd;
    }

    //新增病历内容接口
    public static final String insertUserHistoryForAppUrl(String phone, String res, String recordId, String token, String encryptKey) {
        return Server.url + "/" + Server.insertUserHistory
                + "?tel=" + phone
                + "&res=" + res
                + "&token=" + token
                + "&encryptKey=" + encryptKey
                + "&" + ASSAY_ID + "=" + recordId;
    }

    //按时间排序获得用户的病历
    public static final String getUserHistoryForAppUrl(String phone, String token, String encryptKey) {
        return Server.url + "/" + Server.getUserHistory
                + "?tel=" + phone
                + "&token=" + token
                + "&encryptKey=" + encryptKey;
    }

    //按时间排序获得用户的病历
    public static final String getUserHistoryByTypeForAppUrl(String phone, String token, String encryptKey) {
        return Server.url + "/" + Server.getUserHistoryByType
                + "?tel=" + phone
                + "&token=" + token
                + "&encryptKey=" + encryptKey;
    }

    //获得结果
    public static final String getResultHistoryForAppUrl(String res, String assayId) {
        return Server.url + "/" + Server.resultHistoryForApp
                + "?res=" + res
                + "&assayId=" + assayId;
    }
    
    // 获取图片重命名的方法
    public static final String getPicNewName(String gmtTime, String tel, String checkItemId)
    {
    	return (gmtTime+tel+checkItemId).replace(" ", "").replace("-", "").replace(":", "")+".jpg";
    	//return Base64.encodeToString((gmtTime+tel+checkItemId).getBytes(), Base64.DEFAULT);
    }
    
    // 保存的图片路径获取
    public static final String getPicSavePath()
    {
    	return Environment.getExternalStorageDirectory() + "/pandadotor";
    }

    public class Server {
        public static final String url = "http://www.13xm.com";
        public static final String mUrl = "http://m.13xm.com";
        public static final String paperSortForApp = "paperSortForApp.html";
        public static final String paperForApp = "paperForApp.html";
        public static final String checkItemsForApp = "checkItemsForApp.html";
        public static final String resultForApp = "resultForApp.html";
        public static final String registerForApp = "registForAPP.html";
        public static final String loginForAPP = "loginForAPP.html";
        public static final String insertUserHistory = "insertUserHistory.html";
        public static final String getUserHistory = "getUserHistory.html";
        public static final String getUserHistoryByType = "getUserHistoryByType.html";
        public static final String resultHistoryForApp = "resultHisrtoryForApp.html";
        public static final String getAllArticle = "getAllArticle.html";
        public static final String getAllArticleByType = "getAllArticleByType.html";
        public static final String getArticleByIdForApp = "getArticleByIdForApp.html";
    }

}
