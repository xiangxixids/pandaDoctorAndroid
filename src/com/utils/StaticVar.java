package com.utils;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 15-6-16.
 */
public class StaticVar {
    public static boolean isConsult = false;
    public static boolean isCamera = false;
    public static boolean ask = false;
    public static boolean ON_LINE_FLAG = true;
    public static boolean isOpenActivity = false;
    public static boolean issend = false;
    public static String OFFLINE_ASK_SERVICE_HOST = "http://test.chunyu.me/ehr/ask_service";
    public static String OFFLINE_PROBLEM_LIST_HOST = "http://test.chunyu.me/ehr/ask_data_problem_list";
    public static String OFFLINE_PROBLEM_DETAIL_HOST = "http://test.chunyu.me/ehr/ask_data_problem_detail";
    public static String OFFLINE_APP_KEY = "826d220ba272a191";
    public static String OFFLINE_USER_ID = "test";
    public static String OFFLINE_TIMESTAMP = "1433409653466";
    public static List<Activity> activityList = new ArrayList<Activity>();

    //正式环境KEY

    public static String ONLINE_ASK_SERVICE_HOST = "http://www.chunyuyisheng.com/ehr/ask_service";
    public static String ONLINE_PROBLEM_LIST_HOST = "http://www.chunyuyisheng.com/ehr/ask_data_problem_list";
    public static String ONLINE_PROBLEM_DETAIL_HOST = "http://www.chunyuyisheng.com/ehr/ask_data_problem_detail";
    public static String ONLINE_APP_KEY = "c348ab305d8bce7ef5555d6594cc2c6e";

    //TODO?为什么测试环境的时间戳和UserId要写死。。。囧

    /**
     * 第三方登录获取的用户参数
     */

    /**
     * getNewAskWarn 获得春雨回复提醒
     */

    public static String getNewAskWarn(String userId, String problem_id) {
        StringBuffer url = new StringBuffer();
        //是否是正式环境
        if (ON_LINE_FLAG) {
            long t = System.currentTimeMillis();
            url.append(ONLINE_ASK_SERVICE_HOST);
            url.append("/?app_key=");
            url.append(MD5Util.encryptToMD5(ONLINE_APP_KEY + t + userId, true));
            url.append("&user_id=");
            url.append(userId);
            url.append("&timestamp=");
            url.append(t);
            url.append("&problem_id=");
            url.append(problem_id);
            Log.e("url", url.toString());
            return url.toString();
        }
        url.append(OFFLINE_ASK_SERVICE_HOST);
        url.append("/?app_key=");
        url.append(OFFLINE_APP_KEY);
        url.append("&user_id=");
        url.append(OFFLINE_USER_ID);
        url.append("&timestamp=");
        url.append(OFFLINE_TIMESTAMP);
        return url.toString();

    }


    /**
     * getAskServiceUrl 获得春雨咨询服务Url
     *
     * @param userId
     * @return
     */


    public static String getAskServiceUrl(String userId) {
        StringBuffer url = new StringBuffer();
        //是否是正式环境
        if (ON_LINE_FLAG) {
            long t = System.currentTimeMillis();
            url.append(ONLINE_ASK_SERVICE_HOST);
            url.append("/?app_key=");
            url.append(MD5Util.encryptToMD5(ONLINE_APP_KEY + t + userId, true));
            url.append("&user_id=");
            url.append(userId);
            url.append("&timestamp=");
            url.append(t);
            return url.toString();
        }
        url.append(OFFLINE_ASK_SERVICE_HOST);
        url.append("/?app_key=");
        url.append(OFFLINE_APP_KEY);
        url.append("&user_id=");
        url.append(OFFLINE_USER_ID);
        url.append("&timestamp=");
        url.append(OFFLINE_TIMESTAMP);
        return url.toString();
    }


    /**
     * getHistoryProblemListUrl 历史问题记录url
     *
     * @param userId
     * @return
     */
    public static String getHistoryProblemListUrl(String userId) {
        StringBuffer url = new StringBuffer();
        //是否是正式环境
        if (ON_LINE_FLAG) {
            long t = System.currentTimeMillis();
            url.append(ONLINE_PROBLEM_LIST_HOST);
            url.append("/?app_key=");
            url.append(MD5Util.encryptToMD5(ONLINE_APP_KEY + t + userId, true));
            url.append("&user_id=");
            url.append(userId);
            url.append("&timestamp=");
            url.append(t);
            return url.toString();
        }
        url.append(OFFLINE_PROBLEM_LIST_HOST);
        url.append("/?app_key=");
        url.append(OFFLINE_APP_KEY);
        url.append("&user_id=");
        url.append(OFFLINE_USER_ID);
        url.append("&timestamp=");
        url.append(OFFLINE_TIMESTAMP);
        return url.toString();
    }

    /**
     * getProblemDetailUrl 问题详情URL
     *
     * @param userId
     * @return
     */
    public static String getProblemDetailUrl(String userId, String problemId) {
        StringBuffer url = new StringBuffer();
        //是否是正式环境
        if (ON_LINE_FLAG) {
            long t = System.currentTimeMillis();
            url.append(ONLINE_PROBLEM_DETAIL_HOST);
            url.append("/?app_key=");
            url.append(MD5Util.encryptToMD5(ONLINE_APP_KEY + t + userId, true));
            url.append("&user_id=");
            url.append(userId);
            url.append("&timestamp=");
            url.append(t);
            url.append("&problem_id=");
            url.append(problemId);
            return url.toString();
        }

        url.append(OFFLINE_PROBLEM_DETAIL_HOST);
        url.append("/?app_key=");
        url.append(OFFLINE_APP_KEY);
        url.append("&user_id=");
        url.append(OFFLINE_USER_ID);
        url.append("&timestamp=");
        url.append(OFFLINE_TIMESTAMP);
        url.append("&problem_id=");
        url.append(problemId);
        return url.toString();
    }

}


