package com.dataport.wellness.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.duer.bot.BotMessageProtocol;
import com.baidu.duer.bot.directive.payload.JsonUtil;
import com.baidu.duer.bot.event.payload.LinkClickedEventPayload;
import com.baidu.duer.botsdk.BotIntent;
import com.baidu.duer.botsdk.BotSdk;
import com.dataport.wellness.R;
import com.dataport.wellness.activity.dialog.BaseDialog;
import com.dataport.wellness.activity.dialog.MenuDialog;
import com.dataport.wellness.api.DeviceInfoApi;
import com.dataport.wellness.api.QueryBinderApi;
import com.dataport.wellness.api.ServiceTabApi;
import com.dataport.wellness.api.TokenApi;
import com.dataport.wellness.api.WeatherAddressApi;
import com.dataport.wellness.api.WellNessApi;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.http.HttpData;
import com.dataport.wellness.http.HttpIntData;
import com.dataport.wellness.utils.BotConstants;
import com.dataport.wellness.utils.IntentDecodeUtil;
import com.dataport.wellness.utils.TimeUtil;
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
    private TextView tvBinder, tvWeather, tvC, tvTime, tvDate, tvPlace;

    private int binderId;
    private String location;
    private List<QueryBinderApi.Bean.ListDTO> binderList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_porttal_new);
        findViewById(R.id.ln_binder).setOnClickListener(this);
        findViewById(R.id.ln_service).setOnClickListener(this);
        findViewById(R.id.ln_device).setOnClickListener(this);
        findViewById(R.id.rl_weather).setOnClickListener(this);
        findViewById(R.id.rl_date).setOnClickListener(this);
        findViewById(R.id.ln_wellness).setOnClickListener(this);
        findViewById(R.id.ln_bottom).setOnClickListener(this);
        findViewById(R.id.ln_use_medical).setOnClickListener(this);
        findViewById(R.id.ln_doctor).setOnClickListener(this);
        findViewById(R.id.ln_video).setOnClickListener(this);
        tvWeather = findViewById(R.id.tv_weather);
        tvC = findViewById(R.id.tv_c);
        tvTime = findViewById(R.id.tv_time);
        tvDate = findViewById(R.id.tv_date);
        tvPlace = findViewById(R.id.tv_place);
        tvPlace.setOnClickListener(this);
        tvBinder = findViewById(R.id.tv_binder);
        marqueeView = findViewById(R.id.marqueeView);

        tvDate.setText(TimeUtil.getInstance().getMainDate());
        tvTime.setText(TimeUtil.getInstance().getMainTime());

        BotMessageListener.getInstance().addCallback(this);
        //获取deviceId,apiAccesstoken,用于向后台获取设备sn
//        JSONObject jsonObject =  JSON.parseObject(getIntent().getStringExtra("device"));
//        String deviceId= (String) jsonObject.get("deviceId");
//        String apiAccesstoken = getIntent().getStringExtra("apiAccesstoken");
//        getDeviceInfo(deviceId, apiAccesstoken);

        getToken();
        List<String> messages = new ArrayList<>();
        messages.add("请试试对我说：“小度小度，打开服务订购”");
        messages.add("请试试对我说：“小度小度，打开智能设备”");
        messages.add("请试试对我说：“小度小度，打开康养管家”");
        messages.add("请试试对我说：“小度小度，打开家庭医生”");
        messages.add("请试试对我说：“小度小度，打开用药助手”");
        messages.add("请试试对我说：“小度小度，打开亲友视频”");
        marqueeView.startWithList(messages);
        marqueeView.setOnItemClickListener((position, textView) -> {
            BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
            BotSdk.getInstance().speakRequest(messages.get(position));
        });
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
                            if (binderList.size() > 0) {
                                tvBinder.setText(binderList.get(0).getBinderName() + "(" + binderList.get(0).getRelation() + ")");
                                binderId = binderList.get(0).getBinderId();
                                String getAddress = binderList.get(0).getBinderProvince() + binderList.get(0).getBinderCity() + binderList.get(0).getBinderDistrict() + binderList.get(0).getBinderAddress();
                                String address = binderList.get(0).getBinderCity() + binderList.get(0).getBinderDistrict();
                                tvPlace.setText(address);
                                getWeatherAddress(getAddress);
                            }
                        }
                    }
                });
    }

    private void getWeatherAddress(String address) {
        EasyHttp.get(this)
                .api(new WeatherAddressApi(address))
                .request(new HttpCallback<HttpIntData<WeatherAddressApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpIntData<WeatherAddressApi.Bean> result) {
                        if (result.getCode() == 0) {
                            tvWeather.setText(result.getData().getWeatherInfoRespVo().get(0).getWeather());
                            tvC.setText(result.getData().getWeatherInfoRespVo().get(0).getTemperature() + "℃");
                            location = result.getData().getLocationInfoRespVo().get(0).getLocation();
                        }
                    }
                });
    }

    private void getDeviceInfo(String deviceId, String apiAccesstoken) {
        EasyHttp.get(this)
                .api(new DeviceInfoApi(deviceId, apiAccesstoken))
                .request(new HttpCallback<HttpIntData<DeviceInfoApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpIntData<DeviceInfoApi.Bean> result) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
        Intent intent;
        switch (v.getId()) {
            case R.id.ln_binder:
                if (binderList.size() > 0) {
                    showPop();
                }
                break;
            case R.id.ln_service:
                intent = new Intent(this, ServiceOrderActivity.class);
                intent.putExtra("location", location);
                startActivity(intent);
                break;
            case R.id.ln_device:
                intent = new Intent(this, DeviceActivity.class);
                intent.putExtra("binderId", binderId);
                startActivity(intent);
                break;
            case R.id.tv_place:
                BotSdk.getInstance().speakRequest(tvPlace.getText().toString());
                break;
            case R.id.rl_weather:
                BotSdk.getInstance().speakRequest(tvWeather.getText().toString() + tvC.getText().toString());
                break;
            case R.id.rl_date:
                BotSdk.getInstance().speakRequest("当前时间" + tvDate.getText().toString() + tvTime.getText().toString());
                break;
            case R.id.ln_wellness:
                toModel(BotConstants.OPEN_WELL_NESS_URL);
                break;
            case R.id.ln_use_medical:
                toModel(BotConstants.OPEN_MEDICATION_URL);
                break;
            case R.id.ln_doctor:
                toModel(BotConstants.OPEN_FAMILY_DOCTOR_URL);
                break;
            case R.id.ln_video:
                toModel(BotConstants.OPEN_CONTACTS_URL);
                break;
            case R.id.ln_bottom:
                String speck = marqueeView.getMessages().get(marqueeView.getPosition()).toString();
                BotSdk.getInstance().speakRequest(speck);
                break;

        }
    }

    private void showPop() {
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
                        String getAddress = binderList.get(position).getBinderProvince() + binderList.get(position).getBinderCity() + binderList.get(position).getBinderDistrict() + binderList.get(position).getBinderAddress();
                        String address = binderList.get(position).getBinderCity() + binderList.get(position).getBinderDistrict();
                        tvPlace.setText(address);
                        getWeatherAddress(getAddress);
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                    }
                })
                .show();
    }

    private void toModel(String url) {
        BotSdk.getInstance().sendEvent(
                "LinkClicked",
                "ai.dueros.device_interface.screen",
                true,
                JsonUtil.toJson(new LinkClickedEventPayload().url = url)
        );
    }

    @Override
    public void handleIntent(BotIntent intent, String customData) {
        Intent activityIntent;
        // name意图标识 slots插槽
        String intentResult = getString(R.string.result_intent) + intent.name + ",slots:" + intent.slots;
        Log.d(TAG, "handleIntent: " + intentResult);
        if ("app_home_serveorder".equals(intent.name)) {//服务订购
            BotMessageListener.getInstance().clearCallback();
            activityIntent = new Intent(this, ServiceOrderActivity.class);
            activityIntent.putExtra("location", location);
            startActivity(activityIntent);
        } else if ("app_home_device".equals(intent.name)) {
            BotMessageListener.getInstance().clearCallback();
            activityIntent = new Intent(this, DeviceActivity.class);
            activityIntent.putExtra("binderId", binderId);
            startActivity(activityIntent);
        } else if ("app_home_wellness".equals(intent.name)) {
            toModel(BotConstants.OPEN_WELL_NESS_URL);
        } else if ("app_home_doctor".equals(intent.name)) {
            toModel(BotConstants.OPEN_FAMILY_DOCTOR_URL);
        } else if ("app_home_medication_assistant".equals(intent.name)) {
            toModel(BotConstants.OPEN_MEDICATION_URL);
        } else if ("app_home_contact".equals(intent.name)) {
            toModel(BotConstants.OPEN_CONTACTS_URL);
        } else {
//            BotSdk.getInstance().speak("我没有听清，请再说一遍", true);
            BotSdk.getInstance().speakRequest("我没有听清，请再说一遍");
        }
    }

    @Override
    public void onClickLink(String url, HashMap<String, String> paramMap) {

    }

    @Override
    public void onHandleScreenNavigatorEvent(int event) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        BotMessageListener.getInstance().addCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BotMessageListener.getInstance().removeCallback(this);
    }
}
