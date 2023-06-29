package com.dataport.wellness.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.duer.bot.BotMessageProtocol;
import com.baidu.duer.bot.directive.payload.JsonUtil;
import com.baidu.duer.bot.event.payload.LinkClickedEventPayload;
import com.baidu.duer.botsdk.BotIntent;
import com.baidu.duer.botsdk.BotSdk;
import com.dataport.wellness.R;
import com.dataport.wellness.activity.dialog.BaseDialog;
import com.dataport.wellness.activity.dialog.MenuDialog;
import com.dataport.wellness.api.smalldu.DeviceInfoApi;
import com.dataport.wellness.api.smalldu.DeviceTokenApi;
import com.dataport.wellness.api.smalldu.GuideDataApi;
import com.dataport.wellness.api.health.QueryBinderApi;
import com.dataport.wellness.api.health.TokenApi;
import com.dataport.wellness.api.smalldu.WeatherAddressApi;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.http.HttpData;
import com.dataport.wellness.http.HttpIntData;
import com.dataport.wellness.http.glide.GlideApp;
import com.dataport.wellness.utils.BotConstants;
import com.dataport.wellness.utils.TimeUtil;
import com.hjq.http.EasyHttp;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.http.request.HttpRequest;
import com.sunfusheng.marqueeview.MarqueeView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, IBotIntentCallback {

    private static final String TAG = "MainActivity";
    private LinearLayout lnBinder, lnService;
    private MarqueeView marqueeView;
    private TextView tvBinder, tvWeather, tvC, tvTime, tvDate, tvPlace, tvNoBind, tvNoAuth;
    private RelativeLayout rlSuccess, rlFail, rlFailSecond;
    private ImageView ivQr;

    private long binderId;
    private String binderIdCard;
    private String location;
    private List<QueryBinderApi.Bean.ListDTO> binderList = new ArrayList<>();

    private boolean timeFlag = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme); // 设置为原主题
        setContentView(R.layout.activity_porttal_new);
        rlSuccess = findViewById(R.id.rl_success);
        rlFail = findViewById(R.id.rl_fail);
        rlFailSecond = findViewById(R.id.rl_fail1);
        ivQr = findViewById(R.id.iv_qr);
        tvNoBind = findViewById(R.id.tv_nobind);
        tvNoAuth = findViewById(R.id.tv_no_auth);
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
//        tvTime.setText(TimeUtil.getInstance().getMainTime());

        //获取deviceId,apiAccesstoken,用于向后台获取设备sn
//        if (getIntent().getStringExtra("device") != null) {
//            JSONObject jsonObject = JSON.parseObject(getIntent().getStringExtra("device"));
//            String deviceId = (String) jsonObject.get("deviceId");
//            String apiAccesstoken = getIntent().getStringExtra("apiAccesstoken");
//        }
//        Log.d(TAG, "onCreate: " + Build.SERIAL);
        try {
            Field serialField = Build.class.getDeclaredField("SERIAL");
            // 将字段设置为可访问，以便反射调用
            serialField.setAccessible(true);
            // 获取SERIAL字段的值
            String serialNumber = (String) serialField.get(null);
            // 输出SERIAL号
            Log.d("Serial Number", "Serial Number: " + serialNumber);
            getDeviceInfo(serialNumber);
//            getDeviceInfo("950745EAV663360209E9");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Toast.makeText(this, "获取本机SN失败", Toast.LENGTH_LONG).show();
        }
        List<String> messages = new ArrayList<>();
        messages.add("请试试对我说：“小度小度，打开服务订购”");
        messages.add("请试试对我说：“小度小度，打开健康监测”");
        messages.add("请试试对我说：“小度小度，打开康养管家”");
        messages.add("请试试对我说：“小度小度，打开家庭医生”");
        messages.add("请试试对我说：“小度小度，打开在线咨询”");
        messages.add("请试试对我说：“小度小度，打开亲友视频”");
        marqueeView.startWithList(messages);
        marqueeView.setOnItemClickListener((position, textView) -> {
            BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
            BotSdk.getInstance().speakRequest(messages.get(position));
        });

    }

    private void getDeviceInfo(String sn) {
        try {
            EasyHttp.get(this)
                    .api(new DeviceInfoApi(sn))
                    .request(new HttpCallback<HttpIntData<DeviceInfoApi.Bean>>(this) {

                        @Override
                        public void onSucceed(HttpIntData<DeviceInfoApi.Bean> result) {
                            if (result.getCode() == 0) {
                                SharedPreferences sharedPreferences = getSharedPreferences("XD", MODE_PRIVATE);
                                String token = sharedPreferences.getString("XD_TOKEN", null);
                                Log.d(TAG, "SharedPreferencesToken: " + token);
                                if (null == token) {
                                    long tenantId = result.getData().isInWarehouse() ? result.getData().getTenantId() : 1;
                                    getDeviceToken(String.valueOf(tenantId), result.getData().isInWarehouse(), result.getData().getSn());
                                } else {
                                    BotConstants.DEVICE_TOKEN = token;
                                    if (!result.getData().isInWarehouse()) {//未授权
                                        getGuideData("noAuth");
                                    } else {
                                        getToken(result.getData().getSn());
                                    }
                                }
                            } else {
                                Toast.makeText(MainActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void getDeviceToken(String tenantId, boolean inWarehouse, String sn) {
        BotConstants.TENANT_ID = tenantId;
        EasyHttp.post(this)
                .interceptor(new IRequestInterceptor() {
                    @Override
                    public void interceptArguments(@NonNull HttpRequest<?> httpRequest, @NonNull HttpParams params, @NonNull HttpHeaders headers) {
                        headers.put("tenant-id", BotConstants.TENANT_ID);
                    }
                })
                .api(new DeviceTokenApi("client_credentials", "", "dueros_client", "Mqd7Wk9WRmUHBRyMj3Twz4jUeJ"))
                .request(new HttpCallback<HttpIntData<DeviceTokenApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpIntData<DeviceTokenApi.Bean> result) {
                        if (result.getCode() == 0) {
                            BotConstants.DEVICE_TOKEN = "Bearer " + result.getData().getAccess_token();
                            SharedPreferences preferences = getSharedPreferences("XD", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("XD_TOKEN", BotConstants.DEVICE_TOKEN);
                            editor.commit();
                            if (!inWarehouse) {//未授权
                                getGuideData("noAuth");
                            } else {
                                getToken(sn);
                            }
                        }
                    }
                });
    }

    private void getToken(String sn) {
        EasyHttp.post(this)
                .api(new TokenApi("xiaodu01", "ZQPXP3rkZx7ZLzPK", "client_credentials"))
                .request(new HttpCallback<TokenApi.Bean>(this) {

                    @Override
                    public void onSucceed(TokenApi.Bean result) {
                        BotConstants.HTTP_TOKEN = result.getAccess_token();
                        getPerson(sn);
                    }
                });
    }

    private void getPerson(String sn) {
        EasyHttp.get(this)
                .api(new QueryBinderApi(sn))
                .request(new HttpCallback<HttpData<QueryBinderApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<QueryBinderApi.Bean> result) {
                        if (result.getCode().equals("00000")) {
                            binderList = result.getData().getList();
                            if (binderList.size() > 0) {
                                rlSuccess.setVisibility(View.VISIBLE);
                                tvBinder.setText(binderList.get(0).getBinderName() + "(" + binderList.get(0).getRelation() + ")");
                                binderId = binderList.get(0).getBinderId();
                                binderIdCard = binderList.get(0).getBinderIdcard();
                                String getAddress = binderList.get(0).getBinderProvince() + binderList.get(0).getBinderCity() + binderList.get(0).getBinderDistrict() + binderList.get(0).getBinderAddress();
                                String address = binderList.get(0).getBinderCity() + binderList.get(0).getBinderDistrict();
                                tvPlace.setText(address);
                                getWeatherAddress(getAddress);
                            } else {
                                getGuideData("noBind");
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

    private void getGuideData(String steerType) {
        EasyHttp.get(this)
                .interceptor(new IRequestInterceptor() {
                    @Override
                    public void interceptArguments(@NonNull HttpRequest<?> httpRequest, @NonNull HttpParams params, @NonNull HttpHeaders headers) {
                        headers.put("tenant-id", BotConstants.TENANT_ID);
                        headers.put("login_user_type", "3");
                        headers.put("Authorization", BotConstants.DEVICE_TOKEN);
                    }
                })
                .api(new GuideDataApi(steerType))
                .request(new HttpCallback<HttpIntData<GuideDataApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpIntData<GuideDataApi.Bean> result) {
                        if (result.getCode() == 0) {
                            if (steerType.equals("noBind")) {
                                rlFail.setVisibility(View.VISIBLE);
                                tvNoBind.setText(result.getData().getSteerDesc());
                                GlideApp.with(MainActivity.this)
                                        .load(result.getData().getSteerImg())
                                        .into(ivQr);
                                BotSdk.getInstance().speakRequest("您的设备还没有绑定，绑定步骤，" + result.getData().getSteerDesc());
                            } else if (steerType.equals("noAuth")) {
                                rlFailSecond.setVisibility(View.VISIBLE);
                                tvNoAuth.setText(result.getData().getSteerDesc());
                                BotSdk.getInstance().speakRequest("您的设备未授权使用康养管家，康养管家功能介绍，" + result.getData().getSteerDesc());
                            }
                        } else if (result.getCode() == 401 || result.getCode() == 403) {
                            refreshToken(steerType);
                        }
                    }
                });
    }

    private void refreshToken(String steerType) {
        EasyHttp.post(this)
                .interceptor(new IRequestInterceptor() {
                    @Override
                    public void interceptArguments(@NonNull HttpRequest<?> httpRequest, @NonNull HttpParams params, @NonNull HttpHeaders headers) {
                        headers.put("tenant-id", BotConstants.TENANT_ID);
                    }
                })
                .api(new DeviceTokenApi("client_credentials", "", "dueros_client", "Mqd7Wk9WRmUHBRyMj3Twz4jUeJ"))
                .request(new HttpCallback<HttpIntData<DeviceTokenApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpIntData<DeviceTokenApi.Bean> result) {
                        if (result.getCode() == 0) {
                            BotConstants.DEVICE_TOKEN = "Bearer " + result.getData().getAccess_token();
                            SharedPreferences preferences = getSharedPreferences("XD", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("XD_TOKEN", BotConstants.DEVICE_TOKEN);
                            editor.commit();
                            getGuideData(steerType);
                        }
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
//                intent = new Intent(this, SpeechActivity.class);
                intent.putExtra("binderId", binderId);
                startActivity(intent);
                break;
            case R.id.tv_place:
                BotSdk.getInstance().speakRequest(tvPlace.getText().toString() + tvWeather.getText().toString() + tvC.getText().toString());
                break;
            case R.id.rl_weather:
                BotSdk.getInstance().speakRequest(tvPlace.getText().toString() + tvWeather.getText().toString() + tvC.getText().toString());
                break;
            case R.id.rl_date:
                BotSdk.getInstance().speakRequest("当前时间" + tvDate.getText().toString() + tvTime.getText().toString());
                break;
            case R.id.ln_wellness:
                toModel(BotConstants.OPEN_WELL_NESS_URL);
                break;
            case R.id.ln_use_medical:
//                toModel(BotConstants.OPEN_MEDICATION_URL);
                intent = new Intent(this, OnLineActivity.class);
                intent.putExtra("idCard", binderIdCard);
                intent.putExtra("binderId", binderId);
                startActivity(intent);
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
            data.add(bean.getBinderName() + "(" + bean.getRelation() + ")");
        }
        new MenuDialog.Builder(this)
                .setList(data)
                .setListener(new MenuDialog.OnListener<String>() {

                    @Override
                    public void onSelected(BaseDialog dialog, int position, String string) {
                        tvBinder.setText(string);
                        binderId = binderList.get(position).getBinderId();
                        binderIdCard = binderList.get(position).getBinderIdcard();
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
        LinkClickedEventPayload linkClickedEventPayload = new LinkClickedEventPayload();
        linkClickedEventPayload.url = url;
        BotSdk.getInstance().sendEvent(
                "LinkClicked",
                "ai.dueros.device_interface.screen",
                true,
                JsonUtil.toJson(linkClickedEventPayload)
        );
        /*//这个也好使
        LinkClickedEventPayload payload = new LinkClickedEventPayload();
        payload.url = "dueros://8dcbd6d2-f434-3c9a-41d4-dde55b54a6ca/urlProxy?from=DBP_APK&token=0a5082003c7080bc2b9ed44d706f92f8";
        BotSdk.getInstance().uploadLinkClickedEvent(payload);*/
    }

    @Override
    public void handleIntent(BotIntent intent, String customData) {
        Intent activityIntent;
        // name意图标识 slots插槽
        String intentResult = getString(R.string.result_intent) + intent.name + ",slots:" + intent.slots;
        Log.d(TAG, "handleIntent: " + intentResult);
        if ("app_home".equals(intent.name)) {
            if ("app_home_serveorder".equals(intent.slots.get(0).name)) {
                activityIntent = new Intent(this, ServiceOrderActivity.class);
                activityIntent.putExtra("location", location);
                startActivity(activityIntent);
            } else if ("app_home_device".equals(intent.slots.get(0).name)) {
                activityIntent = new Intent(this, DeviceActivity.class);
                activityIntent.putExtra("binderId", binderId);
                startActivity(activityIntent);
            } else if ("app_home_wellness".equals(intent.slots.get(0).name)) {
                toModel(BotConstants.OPEN_WELL_NESS_URL);
            } else if ("app_home_doctor".equals(intent.slots.get(0).name)) {
                toModel(BotConstants.OPEN_FAMILY_DOCTOR_URL);
            } else if ("app_home_consultation".equals(intent.slots.get(0).name)) {
//            toModel(BotConstants.OPEN_MEDICATION_URL);
                activityIntent = new Intent(this, OnLineActivity.class);
                activityIntent.putExtra("idCard", binderIdCard);
                activityIntent.putExtra("binderId", binderId);
                startActivity(activityIntent);
            } else if ("app_home_contact".equals(intent.slots.get(0).name)) {
                toModel(BotConstants.OPEN_CONTACTS_URL);
            } else {
                BotSdk.getInstance().speakRequest("我没有听清，请再说一遍");
            }
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


    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 0) {
                Calendar instance = Calendar.getInstance();
                int hour = instance.get(Calendar.HOUR_OF_DAY);
                int minute = instance.get(Calendar.MINUTE);
                int second = instance.get(Calendar.SECOND);
                if (second < 10) {
                    tvTime.setText(hour + ":" + minute + ":0" + second);
                } else {
                    tvTime.setText(hour + ":" + minute + ":" + second);
                }
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        BotMessageListener.getInstance().addCallback(this);
        Log.d(TAG, "handleIntent: onResume");
        timeFlag = true;
        new Thread() {
            @Override
            public void run() {
                while (timeFlag) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what = 0;  //消息(一个整型值)
                    handler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                }
            }
        }.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        BotMessageListener.getInstance().clearCallback();
        Log.d(TAG, "handleIntent: onPause");
        timeFlag = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "handleIntent: onDestroy");
        BotMessageListener.getInstance().removeCallback(this);
        timeFlag = false;
    }
}
