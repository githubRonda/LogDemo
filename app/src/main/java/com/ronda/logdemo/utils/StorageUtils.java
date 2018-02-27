package com.ronda.logdemo.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.ronda.logdemo.LogHelper;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Ronda on 2017/12/11.
 *
 * 本地日志保存, 删除相关的工具类
 */

public class StorageUtils {

    private static final String TAG = StorageUtils.class.getName();

    public static final String LOG_SUFFIX = ".log";

    private static final String CHARSET = "UTF-8";

    private static StorageUtils sInstance;

    private Context mContext;

    private StorageUtils(Context ctx) {
        mContext = ctx.getApplicationContext();
    }

    public static synchronized StorageUtils getInstance(Context ctx) {
        if (ctx == null) {
            LogHelper.e(TAG, "Context is null");
            return null;
        }
        if (sInstance == null) {
            sInstance = new StorageUtils(ctx);
        }
        return sInstance;
    }

    public File getUploadLogFile(){
        File dir = mContext.getFilesDir();
        File logFile = new File(dir, DeviceInfoUtils.getMid(mContext) + LOG_SUFFIX);
        if(logFile.exists()){
            return logFile;
        }else{
            return null;
        }
    }

    public boolean deleteUploadLogFile(){
        File dir = mContext.getFilesDir();
        File logFile = new File(dir, DeviceInfoUtils.getMid(mContext) + LOG_SUFFIX);
        return logFile.delete();
    }

    /**
     * 保存日志到 /data/data/<application package>/files 目录
     * @param logMsg
     * @return
     */
    public boolean saveLogFile2Internal(String logMsg) {
        try {
            File dir = mContext.getFilesDir();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Log.d("Liu", "dir: "+dir);
            File logFile = new File(dir, DeviceInfoUtils.getMid(mContext) + LOG_SUFFIX);
            FileOutputStream fos = new FileOutputStream(logFile , true);
            fos.write(logMsg.getBytes(CHARSET));
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.e(TAG, "saveLogFile2Internal failed!");
            return false;
        }
        return true;
    }

    /**
     * 保存日志到 sdcard/Android/data/<application package>/files/
     *
     * @param logMsg
     * @param isAppend
     * @return
     */
    public boolean saveLogFile2SDcard(String logMsg, boolean isAppend) {
        if (!isSDcardExsit()) {
            LogHelper.e(TAG, "sdcard not exist");
            return false;
        }
        try {
            File logDir = getExternalLogDir();
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            File logFile = new File(logDir, DeviceInfoUtils.getMid(mContext) + LOG_SUFFIX);

            LogHelper.d(TAG, logFile.getPath());

            FileOutputStream fos = new FileOutputStream(logFile , isAppend);
            fos.write(logMsg.getBytes(CHARSET));
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "saveLogFile2SDcard failed!");
            return false;
        }
        return true;
    }

    /**
     * 获取 sdcard/Android/data/<application package>/files/Log 目录
     * @return
     */
    private File getExternalLogDir() {
        File logDir = getExternalDir(mContext, "Log");
        LogHelper.d(TAG, logDir.getPath());
        return logDir;
    }

    /**
     * 获取程序外部(sd)的目录
     *
     * @param context
     * @return
     */
    public static File getExternalDir(Context context , String dirName) {
        final String cacheDir = "/Android/data/" + context.getPackageName()+ "/";
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + cacheDir + dirName + "/");
    }

    /**
     * 判断是否有sd卡
     * @return
     */
    public static boolean isSDcardExsit() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
