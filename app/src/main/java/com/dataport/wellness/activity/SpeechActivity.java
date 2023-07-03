package com.dataport.wellness.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.dataport.wellness.R;
import com.dataport.wellness.utils.AuthUtil;
import com.dataport.wellness.utils.AutoCheck;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class SpeechActivity extends BaseActivity implements EventListener {

    private static final String TAG = "SpeechActivity";
    private EventManager asr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        initPermission();
        initSpeech();
    }

    private void initSpeech() {
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this);
        start();
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        Log.i(TAG, "onEvent!" + name);
        if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)){
            // 引擎就绪，可以说话，一般在收到此事件后通过UI通知用户可以说话了
        }
        if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)){
            // 一句话的临时结果，最终结果及语义结果
        }
    }

    private void start() {
        final String[] stLog = {""};
        Map<String, Object> params = AuthUtil.getParam();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event
        // 基于SDK集成2.1 设置识别参数
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        // params.put(SpeechConstant.NLU, "enable");
        // params.put(SpeechConstant.BDS_ASR_ENABLE_LONG_SPEECH, true);//长语音  优先级高于VAD_ENDPOINT_TIMEOUT
        // params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音

        // params.put(SpeechConstant.IN_FILE, "res:///com/baidu/android/voicedemo/16k_test.pcm");
        // params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        // params.put(SpeechConstant.PID, 1537); // 中文输入法模型，有逗号

        /* 语音自训练平台特有参数 */
        // params.put(SpeechConstant.PID, 8002);
        // 语音自训练平台特殊pid，8002：模型类似开放平台 1537  具体是8001还是8002，看自训练平台页面上的显示
        // params.put(SpeechConstant.LMID,1068);
        // 语音自训练平台已上线的模型ID，https://ai.baidu.com/smartasr/model
        // 注意模型ID必须在你的appId所在的百度账号下
        /* 语音自训练平台特有参数 */

        /* 测试InputStream*/
        // InFileStream.setContext(this);
        // params.put(SpeechConstant.IN_FILE,
        // "#com.baidu.aip.asrwakeup3.core.inputstream.InFileStream.createMyPipedInputStream()");

        // 请先使用如‘在线识别’界面测试和生成识别参数。 params同ActivityRecog类中myRecognizer.start(params);
        // 复制此段可以自动检测错误
        (new AutoCheck(getApplicationContext(), new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainErrorMessage();
                        Log.i(TAG, "AutoCheckMessage:" + message);
                    }
                }
            }
        }, false)).checkAsr(params);
        String json = null; // 可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
        Log.i(TAG, "输入参数:" + json);
    }

    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

}
