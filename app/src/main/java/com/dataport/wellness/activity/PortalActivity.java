package com.dataport.wellness.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.baidu.duer.botsdk.BotIntent;
import com.baidu.duer.botsdk.BotSdk;
import com.dataport.wellness.R;
import com.dataport.wellness.activity.camera.CameraActivity;
import com.dataport.wellness.api.health.QueryBinderApi;
import com.dataport.wellness.api.health.TokenApi;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.http.HttpData;
import com.dataport.wellness.utils.BotConstants;
import com.dataport.wellness.utils.IntentDecodeUtil;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;

import java.util.HashMap;
import java.util.List;

public class PortalActivity extends BaseActivity implements View.OnClickListener, IBotIntentCallback {
//    public class PortalActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PortalHome";
    private ImageButton servicerBtn, orderBtn, deviceBtn;
    private ImageView imageDuer;
    private RecyclerView duerHelp;
    private EditText echoZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        /*初始帮助提示*/
        setContentView(R.layout.activity_portal);
        orderBtn = findViewById(R.id.orderBtn);
        deviceBtn = findViewById(R.id.deviceBtn);
        servicerBtn = findViewById(R.id.servicerBtn);
        imageDuer = findViewById(R.id.imageDuer);
        duerHelp = findViewById(R.id.portalHelpRecycler);
        imageDuer.setOnClickListener(this);
        orderBtn.setOnClickListener(this);
        deviceBtn.setOnClickListener(this);
        servicerBtn.setOnClickListener(this);

  /*     echoZone = findViewById(R.id.displayDemo);
        echoZone.setText("created");*/
        //获取deviceId,apiAccesstoken,用于向后台获取设备sn
        String deviceId = getIntent().getStringExtra("device");
        String apiAccesstoken = getIntent().getStringExtra("apiAccesstoken");


        //  super.onAttach(context);
        BotMessageListener.getInstance().addCallback(this);
        getToken();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BotMessageListener.getInstance().removeCallback(this);
    }

    @Override
    public void onClick(View v) {
        Log.i("message", String.valueOf(v.getId()));
        switch (v.getId()) {

            case R.id.orderBtn:
                Intent intent = new Intent(this, ServiceOrderActivity.class);
                startActivity(intent);
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
            case R.id.imageDuer:
                BotSdk.getInstance().speakRequest("我是语音交互助手");
                break;

        }

    }

    private void getToken() {
        EasyHttp.post(this)
                .api(new TokenApi("jkgl01", "123456", "password", "aaa", "password"))
                .request(new HttpCallback<TokenApi.Bean>(this) {

                    @Override
                    public void onSucceed(TokenApi.Bean result) {
                        BotConstants.HTTP_TOKEN = result.getAccess_token();
//                        Log.d(TAG, "onSucceed: "+ result);
                        getPerson();
                    }
                });
    }

    private void getPerson() {
        EasyHttp.get(this)
                .api(new QueryBinderApi("9966801040895652"))
                .request(new HttpCallback<HttpData<List<QueryBinderApi.Bean>>>(this) {

                    @Override
                    public void onSucceed(HttpData<List<QueryBinderApi.Bean>> result) {
                        Log.d(TAG, "onSucceed: "+ result);
                    }
                });
    }

    @Override
    public void handleIntent(BotIntent intent, String customData) {
        // name意图标识 slots插槽
        String intentResult = getString(R.string.result_intent) + intent.name + ",slots:" + intent.slots;
        Log.d(TAG, "handleIntent: " + intentResult);
        if ("app_home_serveorder".equals(intent.name)) {//服务订购
            Intent serviceIntent = new Intent(this, ServiceOrderActivity.class);
            startActivity(serviceIntent);
        }
        if ("app_list_select_item".equals(intent.name)){
//            IntentDecodeUtil.decodeSeekIntentSlot(intent.slots);
            Log.d(TAG, "decodeSeekIntentSlot: " +  IntentDecodeUtil.getSlotNumberValueBySlotName(intent.slots, "app_list_select_item_number"));
        }


//        echoZone.setText(String.format(intentResult, intent.name, intent.slots));


    }

    @Override
    public void onClickLink(String url, HashMap<String, String> paramMap) {
    }

    @Override
    public void onHandleScreenNavigatorEvent(int event) {

    }
}