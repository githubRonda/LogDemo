package com.ronda.logdemo;

import android.app.Application;
import android.os.Environment;

import com.socks.library.KLog;

/**
 * Created by Ronda on 2017/12/11.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * @param isShowLog
         * @param tag
         */
        KLog.init(true, "Liu");

        /**
         *
         * @param c
         * @param upload_url
         * @param params
         * @param isDebug 为true的话, 会打印LogHelper封装的log, 并且也会在sdcard/Android/data/<application package>/files/目录下保存log
         */
        LogCollector.init(getApplicationContext(), "", null, true);
    }
}
