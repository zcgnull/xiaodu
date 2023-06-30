package com.dataport.wellness.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.dataport.wellness.R;

public class SpeechActivity extends BaseActivity implements EventListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        initSpeech();
    }

    private void initSpeech() {
        EventManager asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this);
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)){
            // 引擎就绪，可以说话，一般在收到此事件后通过UI通知用户可以说话了
        }
        if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)){
            // 一句话的临时结果，最终结果及语义结果
        }
    }
}
