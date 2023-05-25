package com.dataport.wellness.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baidu.duer.botsdk.BotIntent;
import com.baidu.duer.botsdk.BotSdk;
import com.dataport.wellness.R;
import com.dataport.wellness.activity.BaseActivity;
import com.dataport.wellness.activity.ServiceOrderActivity;
import com.dataport.wellness.activity.camera.CameraActivity;
import com.dataport.wellness.activity.dialog.BaseDialog;
import com.dataport.wellness.activity.dialog.MenuDialog;
import com.dataport.wellness.api.QueryBinderApi;
import com.dataport.wellness.api.ServiceTabApi;
import com.dataport.wellness.api.TokenApi;
import com.dataport.wellness.api.WellNessApi;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.http.HttpData;
import com.dataport.wellness.utils.BotConstants;
import com.dataport.wellness.utils.IntentDecodeUtil;
import com.google.android.material.tabs.TabLayout;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.sunfusheng.marqueeview.MarqueeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, IBotIntentCallback {

    private static final String TAG = "MainActivity";
    private LinearLayout lnBinder, lnService;
    private MarqueeView marqueeView;
    private TextView tvBinder;

    private int binderId;
    private List<QueryBinderApi.Bean.ListDTO> binderList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_porttal_new);
        findViewById(R.id.ln_binder).setOnClickListener(this);
        findViewById(R.id.ln_service).setOnClickListener(this);
        findViewById(R.id.ln_device).setOnClickListener(this);
        tvBinder = findViewById(R.id.tv_binder);
        marqueeView = findViewById(R.id.marqueeView);

        String deviceId = getIntent().getStringExtra("device");
        String apiAccesstoken = getIntent().getStringExtra("apiAccesstoken");
        WellNessApi.getDeviceSn("842ab60c61b81df1cc6fe68c15580f47", "jjJyKMunH6xErNeZ/OqxFzHRuQg88Fsxig2YEwIfQkJUfIdd7ghFnqzBhkquDc6/raQRNdf8p+ZXZtOyCjly36ec05DMJxHi3JAaTu83BS6vPC5SpDpyRnraliBlfTRMCiN03gRZnu/5DzH96aJkZwVXWwjRjqZKsn5zPx4Jls23mWnOsny72tJaMOfKixsrhpAPhDvzQd4V6dn8gj8j5NB5fbuyxv12RSOY0sSHJ8YzkrWBJfflYV0T7KaPn1Rw7K/jmRGVHuBOutCM/VtcuMxC/y/bP2s4VWFROkNxTPavCV9klDT4KDACv6PQZV5zuYF4uP1HRprDXnZW6uTOmNr0khzCiI85mEIgsqKTcw6g1rhR1P7s4bwH3Q3zZDx9DFhft2EXOBAUKvCozxlTKSnYAsaVf6VoXluWmQ/Sun8=");
        BotMessageListener.getInstance().addCallback(this);

        getToken();
        List<String> messages = new ArrayList<>();
        messages.add("滚动播放当前页面语音交互指令");
        messages.add("滚动播放当前页面语音交互指令");
        marqueeView.startWithList(messages);
    }

    private void getToken() {
        EasyHttp.post(this)
                .api(new TokenApi("jkgl01", "123456", "password", "aaa", "password"))
                .request(new HttpCallback<TokenApi.Bean>(this) {

                    @Override
                    public void onSucceed(TokenApi.Bean result) {
                        BotConstants.HTTP_TOKEN = result.getAccess_token();
                        getPerson();
                    }
                });
    }

    private void getPerson() {
        EasyHttp.get(this)
                .api(new QueryBinderApi("9966801040895652"))
                .request(new HttpCallback<HttpData<QueryBinderApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<QueryBinderApi.Bean> result) {
                        if (result.getCode().equals("00000")) {
                            binderList = result.getData().getList();
                            if (binderList.size() > 0){
                                tvBinder.setText(binderList.get(0).getBinderName() + "(" + binderList.get(0).getRelation() + ")");
                                binderId = binderList.get(0).getBinderId();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ln_binder:
                if (binderList.size() > 0){
                    showPop();
                }
                break;
            case R.id.ln_service:
                intent = new Intent(this, ServiceOrderActivity.class);
                startActivity(intent);
                break;
            case R.id.ln_device:
                intent = new Intent(this, DeviceActivity.class);
                intent.putExtra("binderId", binderId);
                startActivity(intent);
                break;
        }
    }

    private void showPop(){
        // 底部选择框
        List<String> data = new ArrayList<>();
        for (QueryBinderApi.Bean.ListDTO bean : binderList) {
            data.add(bean.getBinderName());
        }
        new MenuDialog.Builder(this)
                .setList(data)
                .setListener(new MenuDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, int position, String string) {
                        tvBinder.setText(string + "(" + binderList.get(position).getRelation() + ")");
                        binderId = binderList.get(position).getBinderId();
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                    }
                })
                .show();
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
        if ("app_list_select_item".equals(intent.name)) {
            Log.d(TAG, "decodeSeekIntentSlot: " + IntentDecodeUtil.getSlotNumberValueBySlotName(intent.slots, "app_list_select_item_number"));
        }
    }

    @Override
    public void onClickLink(String url, HashMap<String, String> paramMap) {

    }

    @Override
    public void onHandleScreenNavigatorEvent(int event) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BotMessageListener.getInstance().removeCallback(this);
    }
}
