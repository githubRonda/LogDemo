package com.ronda.logdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ronda.logdemo.utils.Logger;
import com.socks.library.KLog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click1(View view) {
//        try {
//            int a = 1 / 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            KLog.e("==============================================================");
//
//            StackTraceElement[] stackTrace = e.getStackTrace();
//            for (StackTraceElement stackTraceElement : stackTrace) {
//                //stackTraceElement.toString()
//                KLog.d(stackTraceElement.toString());
//                KLog.w("++++++++");
//            }
//
//            KLog.e("==============================================================");
//        }

        KLog.e("Thread.currentThread(): "+Thread.currentThread().getId());
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            KLog.w("i = "+ i+", "+ stackTrace[i].getClassName());
        }
    }

    public void click2(View view) {
        Logger.testGetCallerName();

    }
}
