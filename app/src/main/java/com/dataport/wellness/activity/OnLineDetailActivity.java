package com.dataport.wellness.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.baidu.duer.bot.BotMessageProtocol;
import com.baidu.duer.botsdk.BotIntent;
import com.baidu.duer.botsdk.BotSdk;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.dataport.wellness.R;
import com.dataport.wellness.api.health.AdviceDoctorApi;
import com.dataport.wellness.api.health.IMLoginApi;
import com.dataport.wellness.api.health.JudgeAdviceApi;
import com.dataport.wellness.api.health.OnlineDoctorV2Api;
import com.dataport.wellness.api.health.StartRecordApi;
import com.dataport.wellness.api.health.TurnOffApi;
import com.dataport.wellness.api.health.TurnOnApi;
import com.dataport.wellness.been.OnLineDoctorBean;
import com.dataport.wellness.been.OnlineDoctorListBean;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.http.HttpData;
import com.dataport.wellness.http.glide.GlideApp;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.sunfusheng.marqueeview.MarqueeView;
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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class OnLineDetailActivity extends BaseActivity implements IBotIntentCallback {

    private static final String TAG = "OnLineDetailActivity";
    private OnlineDoctorListBean data;
    private long binderId;
    private String doctorTimId;
    private String binderTimId;
    private String type;
    private int delay = 0;

    private ImageView head;
    private TextView docName, docCompany, docDep, docType, buyNum, content;
    private LinearLayout yy, sp;
    private MarqueeView marqueeView;
    private RelativeLayout rl_no_data;
    private String speck;
    private FinishActivityRecevier mRecevier;
    private boolean calling = false; //是否正在通话

    private int limit = 0; //是否限制时长
    private ScheduledExecutorService mService; //挂断倒计时用的线程池
    private ScheduledFuture<?> schedule; //挂断倒计时任务
    private String idCard;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_detail);
        findViewById(R.id.ln_back).setOnClickListener(v -> finish());
        binderId = getIntent().getLongExtra("binderId", 0);
        data = (OnlineDoctorListBean) getIntent().getSerializableExtra("data");
        idCard = getIntent().getStringExtra("idCard");
        getIMLogin(binderId, data.getId());
        initView();
        initData();
        yy.setOnClickListener(v -> {
            BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
            judgeAdvice("3", data.getId(), binderId);
        });
        sp.setOnClickListener(v -> {
            BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
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
        //注册广播
        mRecevier = new FinishActivityRecevier();
        registerFinishReciver();
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
        rl_no_data = findViewById(R.id.rl_no_data);
        if (data.isStartVoice()) {
            yy.setVisibility(View.VISIBLE);
        }
        if (data.isStartVideo()) {
            sp.setVisibility(View.VISIBLE);
        }
        findViewById(R.id.ln_voice).setOnClickListener(v -> {
            BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
            BotSdk.getInstance().speakRequest(speck);
        });
    }

    private void initData() {
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        if ("".equals(data.getDoctorUrl()) || data.getDoctorUrl() == null){
            head.setImageResource(R.drawable.default_doctor);
        } else {
            GlideApp.with(this)
                    .load(data.getDoctorUrl())
                    .skipMemoryCache(true)//禁用内存缓存功能
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不缓存任何内容
//                    .error(R.mipmap.default_doctor)
                    .transition(DrawableTransitionOptions.withCrossFade(factory))
                    .transform(new RoundedCorners((int) this.getResources().getDimension(R.dimen.dp_10)))
                    .into(head);
        }

        docName.setText(data.getDoctorName());
        docCompany.setText("机构：" + data.getInstitutionName());
        docDep.setText("科室：" + data.getDeptName());
        docType.setText("职称：" + data.getTitleName());
        buyNum.setText("已服务次数:" + data.getServiceNum());
        if (data.getGoodat() == null){
            content.setVisibility(View.GONE);
            rl_no_data.setVisibility(View.VISIBLE);
        }
        content.setText(data.getGoodat() == null ? "暂无" : data.getGoodat());
        TUICallEngine.createInstance(OnLineDetailActivity.this).addObserver(observer);
        speck = docName.getText().toString()+ "，" + (data.getInstitutionName().equals("") ? "" : docCompany.getText().toString()) + "，" +
                (data.getDeptName().equals("") ? "" : docDep.getText().toString()) +"，" + (data.getTitleName().equals("") ? "" : docType.getText().toString()) +"，" +
                (data.getGoodat() == null   ? "" : content.getText().toString());
        BotSdk.getInstance().speakRequest(speck);
    }

    @Override
    public void handleIntent(BotIntent intent, String customData) {
        if ("app_consultation_video".equals(intent.name)) {//视频咨询
            judgeAdvice("4", data.getId(), binderId);
        } else if ("app_consultation_audio".equals(intent.name)) {//语音咨询
            judgeAdvice("3", data.getId(), binderId);
        } else if ("hangup".equals(intent.name)) {
            hangUp(2);
        } else {
            BotSdk.getInstance().speakRequest("我没有听清，请再说一遍");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
        TUICallEngine.createInstance(OnLineDetailActivity.this).removeObserver(observer);
        if (schedule != null){
            schedule.cancel(true);
        }
        if (mService != null){
            mService.shutdown();
        }
        mService = null;
        data=null;
        observer=null;
        if (mRecevier != null) {
            unregisterReceiver(mRecevier);
        }
    }

    private TUICallObserver observer = new TUICallObserver() {
        @Override
        public void onCallBegin(TUICommonDefine.RoomId roomId, TUICallDefine.MediaType callMediaType, TUICallDefine.Role callRole) {
            Log.i(TAG, "onCallBegin!");
            turnOn(type);
            calling = true;
            if (limit == 1){
                statService(delay);
            }
        }

        public void onCallEnd(TUICommonDefine.RoomId roomId, TUICallDefine.MediaType callMediaType, TUICallDefine.Role callRole, long totalTime) {
            Log.i(TAG, "onCallEnd!");
            turnOff(type);
            calling = false;
            if (schedule != null){
                schedule.cancel(true);
                Log.d("zcg", "任务结束");
            }
            Log.d("zcg", "通话结束");
        }

        @Override
        public void onCallCancelled(String callerId) {
            super.onCallCancelled(callerId);
            calling = false;
        }

        public void onUserNetworkQualityChanged(List<TUICommonDefine.NetworkQualityInfo> networkQualityList) {
            Log.i(TAG, "onUserNetworkQualityChanged!");
        }
    };

    private void getOnlineDoctor() {
        EasyHttp.get(this)
                .api(new OnlineDoctorV2Api(idCard, 1, 10))
                .request(new HttpCallback<HttpData<OnLineDoctorBean>>(this) {

                    @Override
                    public void onSucceed(HttpData<OnLineDoctorBean> result) {
                        if (result.getData().getPublicReamin() == 0 && result.getData().getVioceReamin() == 0 && result.getData().getVideoReamin() == 0) {
                            finish();
                        } else {
                            int publicReamin = result.getData().getPublicReamin();
                            if (publicReamin == 0){
                                int vioceReamin = result.getData().getVioceReamin();
                                int videoReamin = result.getData().getVideoReamin();
                                if (videoReamin == 0){
                                    sp.setVisibility(View.GONE);
                                }
                                if (vioceReamin == 0){
                                    yy.setVisibility(View.GONE);
                                }
                                Log.d("zcg", vioceReamin + "   " + videoReamin);
                            }
                        }
                    }
                });
    }


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
                                limit = Integer.valueOf(result.getInfo().getIsLimit());
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
                            limit = Integer.valueOf(result.getInfo().getIsLimit());
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

    /**
     *
     * @param state 状态 1：自动挂断 2：小度挂断
     */
    private void hangUp(int state) {
        if (state == 1 && calling){
            TUICallEngine.createInstance(getApplicationContext()).hangup(new TUICommonDefine.Callback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "通话超过单次服务时长限制，系统自动挂断。", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "hangup!");
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
    }

    private void statService(int delay) {
        if (mService == null){
            mService = Executors.newScheduledThreadPool(1);
        }
        if (schedule == null){
            schedule = mService.schedule(() -> hangUp(1), delay, TimeUnit.SECONDS);
        } else {
            if (schedule.isDone() || schedule.isCancelled()){
                schedule = mService.schedule(() -> hangUp(1), delay, TimeUnit.SECONDS);
            } else {
                schedule.cancel(true);
                schedule = mService.schedule(() -> hangUp(1), delay, TimeUnit.SECONDS);
            }
        }
        Log.d("zcg", delay + "任务开始");
    }

    private class FinishActivityRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //根据需求添加自己需要关闭页面的action
            if ("OnLineDetailActivity".equals(intent.getAction())) {
                OnLineDetailActivity.this.finish();
            }
        }
    }

    private void registerFinishReciver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("OnLineDetailActivity");
        registerReceiver(mRecevier, intentFilter);
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
        getOnlineDoctor();
        BotMessageListener.getInstance().addCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BotMessageListener.getInstance().clearCallback();
    }

}
