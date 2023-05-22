package com.dataport.wellness.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.duer.botsdk.BotIntent;
import com.baidu.duer.botsdk.BotSdk;
import com.dataport.wellness.R;
import com.dataport.wellness.activity.audio.AudioDemoActivity;
import com.dataport.wellness.activity.camera.CameraActivity;
import com.dataport.wellness.api.QueryCommodityApi;
import com.dataport.wellness.api.WellNessApi;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.http.HttpData;
import com.dataport.wellness.utils.BotConstants;
import com.dataport.wellness.utils.IntentDecodeUtil;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;

import java.util.HashMap;

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

        Log.i(TAG, "device=======" + deviceId);
        Log.i(TAG, "device=======apiAccesstoken=======" + apiAccesstoken);
        WellNessApi.getDeviceSn("842ab60c61b81df1cc6fe68c15580f47", "jjJyKMunH6xErNeZ/OqxFzHRuQg88Fsxig2YEwIfQkJUfIdd7ghFnqzBhkquDc6/raQRNdf8p+ZXZtOyCjly36ec05DMJxHi3JAaTu83BS6vPC5SpDpyRnraliBlfTRMCiN03gRZnu/5DzH96aJkZwVXWwjRjqZKsn5zPx4Jls23mWnOsny72tJaMOfKixsrhpAPhDvzQd4V6dn8gj8j5NB5fbuyxv12RSOY0sSHJ8YzkrWBJfflYV0T7KaPn1Rw7K/jmRGVHuBOutCM/VtcuMxC/y/bP2s4VWFROkNxTPavCV9klDT4KDACv6PQZV5zuYF4uP1HRprDXnZW6uTOmNr0khzCiI85mEIgsqKTcw6g1rhR1P7s4bwH3Q3zZDx9DFhft2EXOBAUKvCozxlTKSnYAsaVf6VoXluWmQ/Sun8=");
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

    private void queryCommodity() {
        EasyHttp.get(this)
                .api(new QueryCommodityApi("", "DESC", "39.93545", "119.59964", 1, 10))
                .request(new HttpCallback<HttpData<QueryCommodityApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<QueryCommodityApi.Bean> result) {

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