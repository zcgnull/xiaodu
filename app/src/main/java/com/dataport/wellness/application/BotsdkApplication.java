package com.dataport.wellness.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.baidu.duer.botsdk.BotSdk;
import com.baidu.duer.botsdk.util.HeartBeatReporter;
import com.dataport.wellness.BuildConfig;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.BotSDKUtils;
import com.dataport.wellness.http.RequestHandler;
import com.dataport.wellness.http.RequestServer;
import com.dataport.wellness.http.SSLSocketClient;
import com.dataport.wellness.http.glide.GlideApp;
import com.dataport.wellness.utils.BotConstants;
import com.dataport.wellness.utils.ContextUtil;
import com.hjq.http.EasyConfig;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.http.request.HttpRequest;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * 建议在Application中执行BotSDK初始化动作
 */
public class BotsdkApplication extends Application {

    // 管理多页面Bot的全部Activity,在退出意图收到的时候，finish掉所有的activity
    private static final List<WeakReference<Activity>> activities = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtil.setContext(this);
        /** 接入益智乐园的App,需要删掉下面这一行。保证益智乐园的付费等逻辑正常执行，不知道益智乐园是啥的，不要删掉这一行 */
        HeartBeatReporter.getInstance().setShouldUploadHeartBeatByApp(false);
        // 初始化BotSDK
        BotSdk.getInstance().init(this);
        // 打开BotSDK的Log开关，开发阶段建议打开Log开关，便于排查问题
        BotSdk.enableLog(BuildConfig.DEBUG);

        /*
         * 旧版本在线注册接口.在线校验支持所有系统版本。缺点：速度稍慢，依赖网络
         * TODO 替换自己的注册信息
         */
        String random1 = BotConstants.RANDOM1_PREFIX + Math.random();
        String random2 = BotConstants.RANDOM2_PREFIX + Math.random();
        // 在线校验示例
        BotSdk.getInstance().register(BotMessageListener.getInstance(), BotConstants.BOTID,
                random1, BotSDKUtils.sign(random1), random2, BotSDKUtils.sign(random2));

        /*
         * 1.46.0版本开始，支持离线校验。依赖系统版本大于等于1.46
         * 离线校验不依赖网络，速度更快。支持无网情况进行验证。但是需要依赖系统版本大于等于Sp46
         * 离线校验，需要在技能平台，打开离线校验开关，下载License文件，打包到assets/dueros/目录内
         * sdk会读取license文件，进行离线校验。
         * NOTE：离线校验支持版本较少（>=46),如非必要，不要走离线校验！！！
         */
        // 离线校验示例
        // BotSdk.getInstance().register(BotMessageListener.getInstance(), BotConstants.BOTID, BotSDKUtils.getAppKey());

        registerActivityLifecycleCallbacks(new ActivityContollor());

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(),SSLSocketClient.platformTrustManager())
                .hostnameVerifier((hostname, session) -> SSLSocketClient.hostnameVerifier(hostname,session))
                .build();

        EasyConfig.with(okHttpClient)
                // 是否打印日志
                .setLogEnabled(BuildConfig.DEBUG)
                // 设置服务器配置（必须设置）
                .setServer(new RequestServer())
                // 设置请求处理策略（必须设置）
                .setHandler(new RequestHandler(this))
                // 设置请求重试次数
                .setRetryCount(3)
                // 添加全局请求参数
                //.addParam("token", "6666666")
                // 添加全局请求头
                //.addHeader("time", "20191030")
//                .setInterceptor((api, params, headers) -> {
//                    // 添加全局请求头
//                    headers.put("token", "66666666666");
//                    headers.put("deviceOaid", UmengClient.getDeviceOaid());
//                    headers.put("versionName", AppConfig.getVersionName());
//                    headers.put("versionCode", String.valueOf(AppConfig.getVersionCode()));
//                    // 添加全局请求参数
//                    // params.put("6666666", "6666666");
//                })
                .setInterceptor(new IRequestInterceptor() {
                    @Override
                    public void interceptArguments(@NonNull HttpRequest<?> httpRequest, @NonNull HttpParams params, @NonNull HttpHeaders headers) {
                        headers.put("Authorization", "Bearer " + BotConstants.HTTP_TOKEN);
                    }
                })
                // 启用配置
                .into();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 清理所有图片内存缓存
        GlideApp.get(this).onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // 根据手机内存剩余情况清理图片内存缓存
        GlideApp.get(this).onTrimMemory(level);
    }

    public static void exitApp() {
        for (WeakReference<Activity> ref : activities) {
            Activity activity = ref.get();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    private static class ActivityContollor implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            activities.add(new WeakReference<Activity>(activity));
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            for (WeakReference ref : activities) {
                if (ref.get() == activity) {
                    activities.remove(ref);
                    return;
                }
            }
        }
    }
}
