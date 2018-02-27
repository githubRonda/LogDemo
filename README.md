
本Demo中集成了两个模块:


## 1. KLog,一个功能强大的日志输出工具

https://github.com/ZhaoKaiQiang/KLog

## 2. 自定义抓捕崩溃日志并上传

https://github.com/DrJia/AndroidLogCollector
(这个仅仅是用于学习使用, 实际开发中可以使用腾讯的 bugly, 功能很强大)


使用也很简单, 即在Application#onCreate()中注册一下即可

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
	         * @param c
	         * @param upload_url
	         * @param params
	         * @param isDebug 为true的话, 会打印LogHelper封装的log, 并且也会在sdcard/Android/data/<application package>/files/目录下保存log
	         */
	        LogCollector.init(getApplicationContext(), "", null, true);
	    }
	}
