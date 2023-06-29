package com.dataport.wellness.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.dataport.wellness.R;

public class SpeechActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);


    }

    private void initSpeech() {
        EventManager asr = EventManagerFactory.create(this, "asr");
    }
}
