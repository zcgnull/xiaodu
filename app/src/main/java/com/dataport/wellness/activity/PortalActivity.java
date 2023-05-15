package com.dataport.wellness.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.baidu.duer.botsdk.BotIntent;
import com.dataport.wellness.R;
import com.dataport.wellness.activity.audio.AudioDemoActivity;
import com.dataport.wellness.activity.camera.CameraActivity;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.utils.BotConstants;
import com.dataport.wellness.utils.IntentDecodeUtil;

import java.util.HashMap;

public class PortalActivity extends AppCompatActivity implements View.OnClickListener, IBotIntentCallback {
//    public class PortalActivity extends AppCompatActivity implements View.OnClickListener {

        private static final String TAG = "PortalHome";
    private ImageButton orderBtn;
    private ImageButton deviceBtn;
    private ImageButton servicerBtn;

    private EditText echoZone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);
        orderBtn =findViewById(R.id.orderBtn);
        deviceBtn = findViewById(R.id.deviceBtn);
        servicerBtn = findViewById(R.id.servicerBtn);
        orderBtn.setOnClickListener(this);
        deviceBtn.setOnClickListener(this);
        servicerBtn.setOnClickListener(this);

       echoZone = findViewById(R.id.displayDemo);
        echoZone.setText("created");

        Log.i(TAG, "on fragment attach");
      //  super.onAttach(context);
        BotMessageListener.getInstance().addCallback(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BotMessageListener.getInstance().removeCallback(this);
    }

    @Override
    public void onClick(View v) {
        Log.i("message",String.valueOf(v.getId()));
        switch (v.getId()) {

            case R.id.orderBtn:
                Log.i(TAG, "trigger audio demo");
                Intent intentForAudio = new Intent(this, AudioDemoActivity.class);
                startActivity(intentForAudio);
                break;
            case R.id.deviceBtn:
                Intent intentForCamera = new Intent(this, CameraActivity.class);
                startActivity(intentForCamera);
                Log.i(TAG, "trigger camera demo");
                break;
            case R.id.servicerBtn:
                Log.i(TAG, "trigger botsdk demo and start local register");
                // 走离线校验
                Intent intentForBotSdk = new Intent(this, BotSdkDemoActivity.class);
                startActivity(intentForBotSdk);
                break;

        }

    }

    @Override
    public void handleIntent(BotIntent intent, String customData) {
//        String intentResult = getString(R.string.result_intent) + "\n指令名称:%s\n槽位信息：%s";
//        if (BotConstants.FAST_FORWARD_INTENT.equals(intent.name)) {
//            int seekNumber = IntentDecodeUtil.decodeSeekIntentSlot(intent.slots);
//            intentResult = intentResult + "\n拖动时长：" + seekNumber;
//        }
//
//        echoZone.setText(String.format(intentResult, intent.name, intent.slots));
//
//        Intent intentForBotSdk = new Intent(this, BotSdkDemoActivity.class);
//        startActivity(intentForBotSdk);
    }

    @Override
    public void onClickLink(String url, HashMap<String, String> paramMap){
    }

    @Override
    public void onHandleScreenNavigatorEvent(int event) {

    }
}