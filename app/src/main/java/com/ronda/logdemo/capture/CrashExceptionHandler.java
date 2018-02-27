package com.ronda.logdemo.capture;

import android.content.Context;
import android.os.Build;
import android.util.Base64;

import com.ronda.logdemo.LogHelper;
import com.ronda.logdemo.utils.DeviceInfoUtils;
import com.ronda.logdemo.utils.StorageUtils;
import com.socks.library.KLog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;

/**
 * Created by Ronda on 2017/12/11.
 */

public class CrashExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashExceptionHandler.class.getName();
    private static final String CHARSET = "UTF-8";
    private static CrashExceptionHandler sInstance;
    private Context mContext;

    // 之所以这里又有一个UncaughtExceptionHandler, 是为了放行崩溃日志,让其在Logcat中输出, 否则就会因为崩溃日志在这里被捕获,logcat中就看不到了
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    String appVerName;
    String appVerCode;
    String OsVer;
    String vendor;
    String model;
    String psuedoId;

    private CrashExceptionHandler(Context c) {
        mContext = c.getApplicationContext();
        appVerName = "appVerName:" + DeviceInfoUtils.getVerName(mContext);
        appVerCode = "appVerCode:" + DeviceInfoUtils.getVerCode(mContext);
        OsVer = "OsVer:" + Build.VERSION.RELEASE;
        vendor = "vendor:" + Build.MANUFACTURER;
        model = "model:" + Build.MODEL;
        psuedoId = "PsuedoID:" + DeviceInfoUtils.getUniquePsuedoID();
    }

    /**
     * 单例
     *
     * @param c
     * @return
     */
    public static CrashExceptionHandler getInstance(Context c) {
        if (c == null) {
            LogHelper.e(TAG, "Context is null");
            return null;
        }
        if (sInstance == null) {
            synchronized (CrashExceptionHandler.class) {
                if (sInstance == null) {
                    sInstance = new CrashExceptionHandler(c);
                }
            }
        }
        return sInstance;
    }


    /**
     * 初始化
     */
    public void init() {

        if (mContext == null) {
            return;
        }

//        boolean b = DeviceInfoUtils.hasPermission(mContext);
//        if (!b) {
//            return;
//        }

        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();

        // 这个就是要在Application中执行
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 处理异常
        handleException(ex);

        ex.printStackTrace();

        if (mDefaultCrashHandler != null) {
            // 放行日志, 否则的话, logcat中是不会显示该崩溃日志的. 因为这个类已经捕获到了崩溃日志就不会继续往JVM中上报了
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            // System.exit(1);
        }
    }

    /**
     * 处理异常, 保存到本地
     *
     * @param ex
     */
    private void handleException(Throwable ex) {
        String s = fomatCrashInfo(ex);
        // String bes = fomatCrashInfoEncode(ex);
        LogHelper.d(TAG, s);
        // LogHelper.d(TAG, bes);
        // StorageUtils.getInstance(mContext).saveLogFile2Internal(bes);

        // StorageUtils.getInstance(mContext).saveLogFile2Internal(s);
        if (LogHelper.isShowLog) {
            StorageUtils.getInstance(mContext).saveLogFile2SDcard(s, true);
        }
    }

    /**
     * 格式化崩溃日志信息
     *
     * @param ex
     * @return
     */
    private String fomatCrashInfo(Throwable ex) {

        String lineSeparator = "\r\n";

        StringBuilder sb = new StringBuilder();
        String logTime = "logTime:" + DeviceInfoUtils.getCurrentTime();
        String exception = "exception:" + ex.toString();

        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);// 这样直接使用printStackTrace()把stack信息输入到流中对于较长的信息会有省略号

        /*StackTraceElement[] stackTrace = ex.getStackTrace(); // 获取异常时的stack信息, 然后一条条打印出来, 这样不会有省略号
        for (int i = 0; i < stackTrace.length; i++) {
            KLog.w("i = "+ i+", "+ stackTrace[i].getClassName());
        }*/

        //StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();// 普通的获取当前线程中的stack信息

        String dump = info.toString();
        String crashMD5 = "crashMD5:" + DeviceInfoUtils.getMD5Str(dump);
        String crashDump = "crashDump:" + "{" + dump + "}";
        printWriter.close();

        sb.append("&start---").append(lineSeparator);
        sb.append(logTime).append(lineSeparator);
        sb.append(appVerName).append(lineSeparator);
        sb.append(appVerCode).append(lineSeparator);
        sb.append(OsVer).append(lineSeparator);
        sb.append(vendor).append(lineSeparator);
        sb.append(model).append(lineSeparator);
        sb.append(psuedoId).append(lineSeparator);
        sb.append(exception).append(lineSeparator);
        sb.append(crashMD5).append(lineSeparator);
        sb.append(crashDump).append(lineSeparator);
        sb.append("&end---").append(lineSeparator).append(lineSeparator).append(lineSeparator);

        return sb.toString();

    }

    private String fomatCrashInfoEncode(Throwable ex) {

        String lineSeparator = "\r\n";

        StringBuilder sb = new StringBuilder();
        String logTime = "logTime:" + DeviceInfoUtils.getCurrentTime();

        String exception = "exception:" + ex.toString();

        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        String dump = info.toString();

        String crashMD5 = "crashMD5:"
                + DeviceInfoUtils.getMD5Str(dump);

        try {
            dump = URLEncoder.encode(dump, CHARSET);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String crashDump = "crashDump:" + "{" + dump + "}";
        printWriter.close();


        sb.append("&start---").append(lineSeparator);
        sb.append(logTime).append(lineSeparator);
        sb.append(appVerName).append(lineSeparator);
        sb.append(appVerCode).append(lineSeparator);
        sb.append(OsVer).append(lineSeparator);
        sb.append(vendor).append(lineSeparator);
        sb.append(model).append(lineSeparator);
        sb.append(psuedoId).append(lineSeparator);
        sb.append(exception).append(lineSeparator);
        sb.append(crashMD5).append(lineSeparator);
        sb.append(crashDump).append(lineSeparator);
        sb.append("&end---").append(lineSeparator).append(lineSeparator)
                .append(lineSeparator);

        String bes = Base64.encodeToString(sb.toString().getBytes(),
                Base64.NO_WRAP);

        return bes;

    }

}
