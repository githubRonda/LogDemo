package com.ronda.logdemo;

import android.content.Context;
import android.util.Log;

import com.ronda.logdemo.capture.CrashExceptionHandler;
import com.ronda.logdemo.http.HttpParameters;
import com.ronda.logdemo.http.UploadLogManager;
import com.ronda.logdemo.utils.DeviceInfoUtils;
import com.ronda.logdemo.utils.NetUtils;

/**
 * Created by Ronda on 2017/12/11.
 *
 * 崩溃日志收集者. 用于在Application中进行初始化,
 */

public class LogCollector {
    private static final String TAG = LogCollector.class.getName();

    private static String Upload_Url;

    private static Context mContext;

    private static boolean isInit = false;

    private static HttpParameters mParams;

    /**
     *
     * @param c
     * @param upload_url
     * @param params
     * @param isDebug 为true的话, 会打印LogHelper封装的log, 并且也会在sdcard/Android/data/<application package>/files/目录下保存log
     */
    public static void init(Context c , String upload_url , HttpParameters params, boolean isDebug){

        if(c == null){
            return;
        }

        if(isInit){
            return;
        }


        mContext = c;
        Upload_Url = upload_url;
        mParams = params;

        LogHelper.isShowLog = isDebug;

        CrashExceptionHandler crashExceptionHandler = CrashExceptionHandler.getInstance(c);
        crashExceptionHandler.init();

        isInit = true;
    }

    public static void upload(boolean isWifiOnly){
        if(mContext == null || Upload_Url == null){
            Log.d(TAG, "please check if init() or not");
            return;
        }
        if(!NetUtils.isNetworkConnected(mContext)){
            return;
        }

        boolean isWifiMode = NetUtils.isWifiConnected(mContext);

        if(isWifiOnly && !isWifiMode){
            return;
        }

        UploadLogManager.getInstance(mContext).uploadLogFile(Upload_Url, mParams);
    }

//    public static void setDebugMode(boolean isDebug){
//        LogHelper.isShowLog = isDebug;
//    }

}
