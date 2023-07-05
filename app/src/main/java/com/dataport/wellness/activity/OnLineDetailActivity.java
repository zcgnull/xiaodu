package com.dataport.wellness.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baidu.duer.bot.BotMessageProtocol;
import com.baidu.duer.botsdk.BotIntent;
import com.baidu.duer.botsdk.BotSdk;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.dataport.wellness.R;
import com.dataport.wellness.api.health.AdviceDoctorApi;
import com.dataport.wellness.api.health.IMLoginApi;
import com.dataport.wellness.api.health.JudgeAdviceApi;
import com.dataport.wellness.api.health.OnlineDoctorApi;
import com.dataport.wellness.api.health.OnlineDoctorV2Api;
import com.dataport.wellness.api.health.StartRecordApi;
import com.dataport.wellness.api.health.TurnOffApi;
import com.dataport.wellness.api.health.TurnOnApi;
import com.dataport.wellness.api.old.ServiceTabApi;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.http.HttpData;
import com.dataport.wellness.http.glide.GlideApp;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.sunfusheng.marqueeview.MarqueeView;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.interfaces.TUICallback;
import com.tencent.qcloud.tuicore.interfaces.TUILoginListener;
import com.tencent.qcloud.tuikit.TUICommonDefine;
import com.tencent.qcloud.tuikit.tuicallengine.TUICallDefine;
import com.tencent.qcloud.tuikit.tuicallengine.TUICallEngine;
import com.tencent.qcloud.tuikit.tuicallengine.TUICallObserver;
import com.tencent.qcloud.tuikit.tuicallkit.TUICallKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OnLineDetailActivity extends BaseActivity implements IBotIntentCallback {

    private static final String TAG = "OnLineDetailActivity";
    private OnlineDoctorV2Api.Bean.DoctorListDTO data;
    private long binderId;
    private String doctorTimId;
    private String binderTimId;
    private String type;
    private int delay = 0;

    private ImageView head;
    private TextView docName, docCompany, docDep, docType, buyNum, content;
    private LinearLayout yy, sp;
    private MarqueeView marqueeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_detail);
        findViewById(R.id.ln_back).setOnClickListener(v -> finish());
        binderId = getIntent().getLongExtra("binderId", 0);
        data = (OnlineDoctorV2Api.Bean.DoctorListDTO) getIntent().getSerializableExtra("data");
        getIMLogin(binderId, data.getId());
        initView();
        initData();
        yy.setOnClickListener(v -> {
            judgeAdvice("3", data.getId(), binderId);
        });
        sp.setOnClickListener(v -> {
            judgeAdvice("4", data.getId(), binderId);
        });
        List<String> messages = new ArrayList<>();
        messages.add("请试试对我说：“小度小度，语音咨询”");
        messages.add("请试试对我说：“小度小度，视频咨询”");
        marqueeView = findViewById(R.id.marqueeView);
        marqueeView.startWithList(messages);
        marqueeView.setOnItemClickListener((position, textView) -> {
            BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
            BotSdk.getInstance().speakRequest(messages.get(position));
        });
    }

    private void initView() {
        head = findViewById(R.id.iv_head);
        docName = findViewById(R.id.tv_name);
        docCompany = findViewById(R.id.tv_company);
        docDep = findViewById(R.id.tv_dep);
        docType = findViewById(R.id.tv_type);
        buyNum = findViewById(R.id.tv_num);
        yy = findViewById(R.id.btn_yy);
        sp = findViewById(R.id.btn_sp);
        content = findViewById(R.id.tv_context);
        if (data.isStartVoice()) {
            yy.setVisibility(View.VISIBLE);
        }
        if (data.isStartVideo()) {
            sp.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        GlideApp.with(this)
                .load(data.getDoctorUrl())
                .transition(DrawableTransitionOptions.withCrossFade(factory))
                .transform(new RoundedCorners((int) this.getResources().getDimension(R.dimen.dp_10)))
                .into(head);
        docName.setText(data.getDoctorName());
        docCompany.setText("机构：" + data.getInstitutionName());
        docDep.setText("科室：" + data.getDeptName());
        docType.setText("职称：" + data.getTitleName());
        buyNum.setText("已服务次数:" + data.getServiceNum());
        content.setText(data.getGoodat() == null ? "暂无" : data.getGoodat());
        TUICallEngine.createInstance(OnLineDetailActivity.this).addObserver(observer);
    }

    @Override
    public void handleIntent(BotIntent intent, String customData) {
        if ("app_consultation_video".equals(intent.name)) {//视频咨询
            judgeAdvice("4", data.getId(), binderId);
        } else if ("app_consultation_audio".equals(intent.name)) {//语音咨询
            judgeAdvice("3", data.getId(), binderId);
        } else if ("hangup".equals(intent.name)) {
            hangUp();
        } else {
            BotSdk.getInstance().speakRequest("我没有听清，请再说一遍");
        }
    }

    private TUICallObserver observer = new TUICallObserver() {
        @Override
        public void onCallBegin(TUICommonDefine.RoomId roomId, TUICallDefine.MediaType callMediaType, TUICallDefine.Role callRole) {
            Log.i(TAG, "onCallBegin!");
            turnOn(type);
            statService(delay);
        }

        public void onCallEnd(TUICommonDefine.RoomId roomId, TUICallDefine.MediaType callMediaType, TUICallDefine.Role callRole, long totalTime) {
            Log.i(TAG, "onCallEnd!");
            turnOff(type);
        }

        public void onUserNetworkQualityChanged(List<TUICommonDefine.NetworkQualityInfo> networkQualityList) {
            Log.i(TAG, "onUserNetworkQualityChanged!");
        }
    };

    private void judgeAdvice(String serviceCode, long doctorId, long binderId) {
        type = serviceCode;
        EasyHttp.get(this)
                .api(new JudgeAdviceApi(serviceCode, doctorId, binderId))
                .request(new HttpCallback<JudgeAdviceApi.Bean>(this) {

                    @Override
                    public void onSucceed(JudgeAdviceApi.Bean result) {
                        if (result.getCode().equals("00000")) {
                            if (result.getInfo().getIsExist().equals("1")) {
                                delay = Integer.valueOf(result.getInfo().getRemainingDuration());
                                if (type.equals("3")) {
                                    TUICallKit.createInstance(OnLineDetailActivity.this).call(doctorTimId, TUICallDefine.MediaType.Audio);
                                } else {
                                    TUICallKit.createInstance(OnLineDetailActivity.this).call(doctorTimId, TUICallDefine.MediaType.Video);
                                }
                            } else {
                                adviceDoctor(serviceCode, binderId, doctorId);
                            }
                        }
                    }
                });
    }

    private void adviceDoctor(String serviceCode, long binderId, long doctorId) {
        EasyHttp.get(this)
                .api(new AdviceDoctorApi(serviceCode, binderId, doctorId))
                .request(new HttpCallback<AdviceDoctorApi.Bean>(this) {

                    @Override
                    public void onSucceed(AdviceDoctorApi.Bean result) {
                        if (result.getCode().equals("00000")) {
                            startRecord(result.getInfo().getAdviceUseRecordId());
                            if (result.getInfo().getIsLimit().equals("1")) {
                                delay = result.getInfo().getLimitDuration();
                            }
                        }
                    }
                });
    }

    private void startRecord(int adviceUseRecordId) {
        EasyHttp.get(this)
                .api(new StartRecordApi(adviceUseRecordId, "2"))
                .request(new HttpCallback<HttpData>(this) {

                    @Override
                    public void onSucceed(HttpData result) {
                        if (result.getCode().equals("00000")) {
                            if (type.equals("3")) {
                                TUICallKit.createInstance(OnLineDetailActivity.this).call(doctorTimId, TUICallDefine.MediaType.Audio);
                            } else {
                                TUICallKit.createInstance(OnLineDetailActivity.this).call(doctorTimId, TUICallDefine.MediaType.Video);
                            }
                        }
                    }
                });
    }

    private void turnOn(String serviceCode) {
        EasyHttp.get(this)
                .api(new TurnOnApi(binderTimId, doctorTimId, serviceCode))
                .request(new HttpCallback<HttpData>(this) {

                    @Override
                    public void onSucceed(HttpData result) {
                    }
                });
    }

    private void turnOff(String serviceCode) {
        EasyHttp.get(this)
                .api(new TurnOffApi(binderTimId, doctorTimId, serviceCode))
                .request(new HttpCallback<HttpData>(this) {

                    @Override
                    public void onSucceed(HttpData result) {
                    }
                });
    }

    private void getIMLogin(long binderId, long doctorId) {
        EasyHttp.get(this)
                .api(new IMLoginApi(binderId, doctorId))
                .request(new HttpCallback<HttpData<IMLoginApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<IMLoginApi.Bean> result) {
                        if (result.getCode().equals("00000")) {
                            doctorTimId = result.getData().getTimDoctor().getDoctorTimId();
                            binderTimId = result.getData().getTimBinder().getBinderTimId();
                        }
                    }
                });
    }

    private void hangUp() {
        TUICallEngine.createInstance(OnLineDetailActivity.this).hangup(new TUICommonDefine.Callback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "hangup!");
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void statService(int delay) {
        ScheduledExecutorService mService = Executors.newScheduledThreadPool(1);
        mService.schedule(new Runnable() {
            @Override
            public void run() {
                hangUp();
            }
        }, delay, TimeUnit.SECONDS);
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
    protected void onPause() {
        super.onPause();
        BotMessageListener.getInstance().clearCallback();
        observer = null;
    }

}
