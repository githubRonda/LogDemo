package com.ronda.logdemo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Ronda on 2017/12/11.
 *
 * 设备参数相关的工具类
 */

public class DeviceInfoUtils {

    private static final String TAG = DeviceInfoUtils.class.getName();

//    public static boolean isNetworkConnected(Context context) {
//        if (context != null) {
//            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
//            if (mNetworkInfo != null) {
//                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
//            }
//        }
//        return false;
//    }
//
//    public static boolean isWifiConnected(Context context) {
//        if (context != null) {
//            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//            if (mWiFiNetworkInfo != null) {
//                return mWiFiNetworkInfo.isAvailable() && mWiFiNetworkInfo.isConnected();
//            }
//        }
//        return false;
//    }

//
//    /**
//     * 获取程序外部(sd)的目录
//     *
//     * @param context
//     * @return
//     */
//    public static File getExternalDir(Context context , String dirName) {
//        final String cacheDir = "/Android/data/" + context.getPackageName()+ "/";
//        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + cacheDir + dirName + "/");
//    }
//
//    /**
//     * 判断是否有sd卡
//     * @return
//     */
//    public static boolean isSDcardExsit() {
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            return true;
//        }
//        return false;
//    }



    /**
     * 获取当前时间
     * @return
     */
    public static String getCurrentTime(){
        long currentTime = System.currentTimeMillis();
        Date date = new Date(currentTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = sdf.format(date);
        return time;
    }

    /**
     * 获取Version
     * @param c
     * @return
     */
    public static String getVerName(Context c){
        PackageManager pm = c.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(c.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error while collect package info", e);
            e.printStackTrace();
            return "error";
        }
        if(pi == null){
            return "error1";
        }
        String versionName = pi.versionName;
        if(versionName == null){
            return "not set";
        }
        return versionName;
    }

    /**
     * 获取VersionCode
     * @param c
     * @return
     */
    public static String getVerCode(Context c){
        PackageManager pm = c.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(c.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error while collect package info", e);
            e.printStackTrace();
            return "error";
        }
        if(pi == null){
            return "error1";
        }
        int versionCode = pi.versionCode;

        return String.valueOf(versionCode);
    }


    /**
     * 获得独一无二的Psuedo ID
     * @return
     */
    public static String getUniquePsuedoID() {
        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位

        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }


    /**
     * 检查是否在清单文件中声明的对应的权限
     * @param context
     * @return
     */
    public static boolean hasPermission(Context context) {
        if (context != null) {
            boolean b1 = context
                    .checkCallingOrSelfPermission("android.permission.INTERNET") == PackageManager.PERMISSION_GRANTED;//
            boolean b2 = context.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED;
            boolean b3 = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED;
            boolean b4 = context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == PackageManager.PERMISSION_GRANTED;
            boolean b5 = context.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") == PackageManager.PERMISSION_GRANTED;

            if(!b1 || !b2 || !b3 || !b4 || !b5){
                Log.d(TAG, "没有添加权限");
                Toast.makeText(context.getApplicationContext(), "没有添加权限", Toast.LENGTH_SHORT).show();
            }
            return b1 && b2 && b3 && b4 && b5;
        }
        return false;
    }

    /**
     * 获取 deviceId, androidId, serialNo 的md5加密的字符串
     * 获取DeviceId 需要android.permission.READ_PHONE_STATE 权限
     * @param context
     * @return
     */
    public static String getMid(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId(); //Requires Permission: android.permission.READ_PHONE_STATE
        String AndroidID = android.provider.Settings.System.getString(context.getContentResolver(), "android_id");
        String serialNo = getDeviceSerialForMid2();
        String m2 = getMD5Str("" + imei + AndroidID + serialNo);
        return m2;
    }


    /**
     * 获取设备序列号
     * 在模拟器上运行获取的为空字符串
     * @return
     */
    private static String getDeviceSerialForMid2() {
        String serial = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception ignored) {
        }
        return serial;
    }

    /**
     * md5加密
     * @param str
     * @return
     */
    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }


        return md5StrBuff.toString();
    }
}
