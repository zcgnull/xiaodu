package com.dataport.wellness.utils;


import android.media.MediaRecorder;
import androidx.annotation.Nullable;

public class BotConstants {

    //头部信息
    public static String HTTP_TOKEN = "";
    public static String DEVICE_TOKEN = "";
    public static String TENANT_ID = "1";
    public static String SN;
    public static String BaiduSpeechAppKey;
    public static String BaiduSpeechSecretKey;
    public static String BaiduSpeechAppId;
    public static final String LOG_TAG_AUDIO = "AudioRecorder";
    public static final String LOG_TAG_CAMERA = "CameraDemo";
    public static final String LOG_TAG_BOTSDK = "BotSDKDemo";

    //健康管理后台地址
//    public static final String JK_URL = "http://10.20.5.21:8110/";
//    public static final String JK_URL = "http://10.20.5.19:8099/";
    public static String JK_URL = "";
//    public static final String JK_URL = "http://172.20.3.51:9110/";
    //养老信息后台地址
    public static String YZ_URL = "";
//    public static final String YL_APPID = "2023070316252737";
    public static final String YL_APPID = "2023072717252111";
//    public static final String YL_KEY = "adb8cd70158a4587a3526c3c525e285a";
    public static final String YL_KEY = "e80fc0668ad141009153e38c0072aad3";
    //小度服务端
//    public static final String XD_URL = "http://10.20.1.13:48080/";
//    public static final String XD_URL = "http://10.20.0.158:48080/";
//    public static final String XD_URL = "http://172.20.3.112:8080/";
    public static final String XD_URL = "https://dueros.dataport.com.cn/";
    //websocket地址
    public static final String WEB_SOCKET_URL = "wss://dueros.dataport.com.cn/";
//    public static final String WEB_SOCKET_URL = "ws://10.20.1.13:48080/";
//    public static final String WEB_SOCKET_URL = "wss://dueros.dataport.com.cn/";

    /**
     * BotID和签名key ,可以在自己的技能控制台看到相关信息：https://dueros.baidu.com/dbp/main/console
     */
    public static final String BOTID = "a3794284-893f-941a-010b-c4a854ab9bc3";
    public static final String SIGNATURE_KEY = "46ff50c2-5751-96a3-5c83-e89490fdeaf3";

    // 两个随机值的前缀，这里对格式不作要求
    public static final String RANDOM1_PREFIX = "random1";
    public static final String RANDOM2_PREFIX = "random2";

    // UIControl用到的路由常量
    public static final String CLICK_LOGIN_URL = "sdkdemo://clicklogin";
    public static final String CLICK_REGISTER_URL = "sdkdemo://clickregister";
    public static final String SELETC_TEST_URL = "sdkdemo://selecttest/";
    public static final String INPUT_TEST_URL = "sdkdemo://inputtest/";
    // 课程表 or 日程表页面
    public static final String OPEN_CANLENDAR_URL = "dueros://6402fc3a-7825-3147-b52e-c25911b6d03c/entity"
            + "/timerShow/allUsedList?startDay=MONTH_START_DAY&endDay=MONTH_END_DAY&selectedDay=TODAY&requestType"
            + "=SHOW&habitChannel=duyayaapk";

    /**
     * ToDo:内容直达配置
     */
    //广场舞跳转
    public static final String OPEN_DANCE_URL = "dueros://8dcbd6d2-f434-3c9a-41d4-dde55b54a6ca/urlProxy?from=DBP_APK&token=0a5082003c7080bc2b9ed44d706f92f8";
    //电视直播
    public static final String OPEN_LIVE_TV_URL = "dueros://8dcbd6d2-f434-3c9a-41d4-dde55b54a6ca/urlProxy?from=DBP_APK&token=fc43a09fbc2542e1ecfa6cc1828f355d";
    //梨园戏曲
    public static final String OPEN_OPERA_URL = "dueros://8dcbd6d2-f434-3c9a-41d4-dde55b54a6ca/urlProxy?from=DBP_APK&token=46c81763a495f57bf0fb77bc992b5dc0";
    //新闻
    public static final String OPEN_NEWS_URL = "dueros://8dcbd6d2-f434-3c9a-41d4-dde55b54a6ca/urlProxy?from=DBP_APK&token=6abfb569831dcd1d015626283d7f3021";
    //测血糖
    public static final String OPEN_BLOOD_SUGAR_URL = "dueros://8dcbd6d2-f434-3c9a-41d4-dde55b54a6ca/urlProxy?from=DBP_APK&token=8b23422987352f66af8c92999b56d4f2";
    //测血压
    public static final String OPEN_BLOOD_PRESSURE_URL = "dueros://8dcbd6d2-f434-3c9a-41d4-dde55b54a6ca/urlProxy?from=DBP_APK&token=7d97c73a32ad3bce9b8047460d4ac2ff";
    //用药助手
    public static final String OPEN_MEDICATION_URL = "dueros://8dcbd6d2-f434-3c9a-41d4-dde55b54a6ca/urlProxy?from=DBP_APK&token=f04222f7cbf2504608a35447ca79e0b0";
    //健康医典
    public static final String OPEN_HEALTH_CODEX_URL = "dueros://8dcbd6d2-f434-3c9a-41d4-dde55b54a6ca/urlProxy?from=DBP_APK&token=eb3a2657930b2174613f56cacb800cdc";
    //通讯录
    public static final String OPEN_CONTACTS_URL = "dueros://8dcbd6d2-f434-3c9a-41d4-dde55b54a6ca/urlProxy?from=DBP_APK&token=fed4be846a2728889f7ab0b797f38f43";
    //通讯录-家庭医生
    public static final String OPEN_FAMILY_DOCTOR_URL = "dueros://8dcbd6d2-f434-3c9a-41d4-dde55b54a6ca/urlProxy?from=DBP_APK&token=d7a8d4b334e22d9a21d61dafbdf79029";
    //通讯录-康养管家
    public static final String OPEN_WELL_NESS_URL = "dueros://8dcbd6d2-f434-3c9a-41d4-dde55b54a6ca/urlProxy?from=DBP_APK&token=7bfbe7b28e773ed6e71ca31e1b064a49";
    // 自定义快进意图名称
    public static final String FAST_FORWARD_INTENT = "fast_forward_intent";
    // 系统预定义[数字] 槽位名称
    public static final String SYS_NUMBER = "sys.number";
    // 用户自定义词典[时间单位] 槽位名称
    public static final String TIME_UNIT = "time.unit";
    // 用户自定义词典值
    public static final String TIME_UNIT_SECOND = "second";
    public static final String TIME_UNIT_MINUTE = "minute";


    public static class Frequency {
        public static final int F16K = 16000;
        public static final int F22K = 22050;
        public static final int F11K = 11025;
        public static final int F64K = 64000;
    }

    public static class UiControlType {
        public static final String LINK = "link";
        public static final String SELECT = "select";
        public static final String INPUT = "input";
        public static final String BUTTON = "button";
    }

    /**
     * 音频源
     */
    public static class AudioSource {
        /**
         * 默认的音频信号。通过默认音频源获取的采集信号
         */
        public static final int DEFAULT = MediaRecorder.AudioSource.VOICE_COMMUNICATION;

        /**
         * 原始音频信号。小度正式向开发者开放原始音频录制能力。
         * 通过此音频源可获取麦克风采集的原始音频数据。
         * 开发者可自行选择算法处理信号以满足需求。
         */
        public static final int ORIGINAL = 1007;
    }

    /** 打开App的token字段 */
    @Nullable
    public static String token;

    /** 打开App的statistic字段。 正式工程中，可以创建一个单例holder存放 */
    @Nullable
    public static String statisticData;

}
