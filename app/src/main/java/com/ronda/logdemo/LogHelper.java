package com.ronda.logdemo;

import android.util.Log;

/**
 * Created by Ronda on 2017/12/11.
 */

public class LogHelper {

    public static boolean isShowLog = false;

    private static final int RETURN_NOLOG = -1;

    public static int i(String tag, String msg) {
        if (msg == null)
            msg = "";

        return isShowLog ? Log.i(tag, msg) : RETURN_NOLOG;
    }

    public static int d(String tag, String msg) {

        if (msg == null)
            msg = "";

        return isShowLog ? Log.d(tag, msg) : RETURN_NOLOG;
    }

    public static int e(String tag, String msg) {
        if (msg == null)
            msg = "";

        return isShowLog ? Log.e(tag, msg) : RETURN_NOLOG;
    }

    public static int w(String tag, String msg) {
        if (msg == null)
            msg = "";

        return isShowLog ? Log.w(tag, msg) : RETURN_NOLOG;
    }

    public static void setDebugMode(boolean isDebug){
        LogHelper.isShowLog = isDebug;
    }
}

