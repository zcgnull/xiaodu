package com.dataport.wellness.activity;

import static androidx.core.os.HandlerCompat.postDelayed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.duer.bot.BotMessageProtocol;
import com.baidu.duer.botsdk.BotIntent;
import com.baidu.duer.botsdk.BotSdk;
import com.dataport.wellness.R;
import com.dataport.wellness.activity.dialog.BaseDialog;
import com.dataport.wellness.activity.dialog.WaitDialog;
import com.dataport.wellness.adapter.OnlineAdapter;
import com.dataport.wellness.api.health.AdviceDoctorApi;
import com.dataport.wellness.api.health.IMLoginApi;
import com.dataport.wellness.api.health.IMParamApi;
import com.dataport.wellness.api.health.JudgeAdviceApi;
import com.dataport.wellness.api.health.OnlineDoctorV2Api;
import com.dataport.wellness.api.health.OnlineAdviceNumApi;
import com.dataport.wellness.api.health.OnlineDoctorApi;
import com.dataport.wellness.api.health.StartRecordApi;
import com.dataport.wellness.api.health.TurnOffApi;
import com.dataport.wellness.api.health.TurnOnApi;
import com.dataport.wellness.api.smalldu.GuideDataApi;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.http.HttpData;
import com.dataport.wellness.http.HttpIntData;
import com.dataport.wellness.http.glide.GlideApp;
import com.dataport.wellness.utils.BotConstants;
import com.google.android.material.tabs.TabLayout;
import com.hjq.http.EasyHttp;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.http.request.HttpRequest;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
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

public class OnLineActivity extends BaseActivity implements IBotIntentCallback {

    private static final String TAG = "OnLineActivity";
    private TabLayout tabLayout;
    private RecyclerView contentRv;
    private RefreshLayout refreshLayout;
    private RelativeLayout noData, rlSuccess, rlFail;

    private MarqueeView marqueeView;
    private TextView noOnline;
    private ImageView ivQr;

    private OnlineAdapter onlineAdapter;
    private List<String> dateTabs = new ArrayList<>();
    private List<OnlineDoctorV2Api.Bean.DoctorListDTO> doctorList = new ArrayList<>();
    private OnlineDoctorV2Api.Bean.DoctorListDTO doctor;
    private int pageNum = 0;
    private int pageSize = 10;
    private String businessType = "";
    private String idCard;
    private long binderId;
    private boolean isLogin = false;
    private String doctorTimId;
    private String binderTimId;
    private String type = "";
    private String handleType = "";
    private int delay = 0;

    private BaseDialog mWaitDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        initView();
        GridLayoutManager contentManger = new GridLayoutManager(this, 2);
        contentManger.setOrientation(GridLayoutManager.VERTICAL);
        contentRv.setLayoutManager(contentManger);
        onlineAdapter = new OnlineAdapter(this);
        contentRv.setAdapter(onlineAdapter);
        onlineAdapter.setListener((data, pos) -> {
            Intent intent = new Intent(OnLineActivity.this, OnLineDetailActivity.class);
            intent.putExtra("data", data);
            intent.putExtra("binderId", binderId);
            startActivity(intent);
        });
        onlineAdapter.setYyClickListener((data, pos) -> getIMDoctor(binderId, data.getId(), "3"));
        onlineAdapter.setSpClickListener((data, pos) -> getIMDoctor(binderId, data.getId(), "4"));
    }

    private void initView() {
        findViewById(R.id.ln_back).setOnClickListener(v -> finish());
        findViewById(R.id.ln_back1).setOnClickListener(v -> finish());
        findViewById(R.id.ln_record).setOnClickListener(v -> {
            Intent intent = new Intent(OnLineActivity.this, OnLineRecordActivity.class);
            intent.putExtra("binderId", binderId);
            startActivity(intent);
        });
        idCard = getIntent().getStringExtra("idCard");
        binderId = getIntent().getLongExtra("binderId", 0);
        noData = findViewById(R.id.rl_no_data);
        rlSuccess = findViewById(R.id.rl_success);
        rlFail = findViewById(R.id.rl_fail);
        noOnline = findViewById(R.id.tv_no_online);
        ivQr = findViewById(R.id.iv_qr);
        tabLayout = findViewById(R.id.first_tab);
        contentRv = findViewById(R.id.rv_content);
        //        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                TextView tabView = tab.getCustomView().findViewById(R.id.item_tv_tab);
//                tabView.setBackground(getResources().getDrawable(R.drawable.service_tab_bg));
//                tabView.setTextColor(getResources().getColor(R.color.colorWhite));
//                pageNum = 0;
//                if (tab.getPosition() == 0) {
//                    businessType = "";
//                    getOnlineDoctor(1);
//                } else if (tab.getPosition() == 1) {//4语音咨询
//                    businessType = "4";
//                    getOnlineDoctor(1);
//                } else {//5视频咨询
//                    businessType = "5";
//                    getOnlineDoctor(1);
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                TextView tabView = tab.getCustomView().findViewById(R.id.item_tv_tab);
//                tabView.setBackground(getResources().getDrawable(R.drawable.service_selected_tab_bg));
//                tabView.setTextColor(getResources().getColor(R.color.colorBule));
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            pageNum = 0;
            getOnlineDoctor(1);
        });
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            pageNum += 1;
            getOnlineDoctor(2);
        });
        List<String> messages = new ArrayList<>();
        messages.add("请试试对我说：“小度小度，语音咨询第1个”");
        messages.add("请试试对我说：“小度小度，视频咨询第1个”");
        messages.add("请试试对我说：“小度小度，咨询第1个”");
        messages.add("请试试对我说：“小度小度，语音咨询”");
        messages.add("请试试对我说：“小度小度，视频咨询”");
        marqueeView = findViewById(R.id.marqueeView);
        marqueeView.startWithList(messages);
        marqueeView.setOnItemClickListener((position, textView) -> {
            BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
            BotSdk.getInstance().speakRequest(messages.get(position));
        });
        TUICallEngine.createInstance(OnLineActivity.this).addObserver(observer);
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

    private void statService(int delay) {
        ScheduledExecutorService mService = Executors.newScheduledThreadPool(1);
        mService.schedule(() -> hangUp(), delay, TimeUnit.SECONDS);
    }

    private void hangUp() {
        TUICallEngine.createInstance(OnLineActivity.this).hangup(new TUICommonDefine.Callback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "hangup!");
            }

            @Override
            public void onError(int i, String s) {

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
                                if (type.equals("3")) {
                                    TUICallKit.createInstance(OnLineActivity.this).call(doctorTimId, TUICallDefine.MediaType.Audio);
                                } else {
                                    TUICallKit.createInstance(OnLineActivity.this).call(doctorTimId, TUICallDefine.MediaType.Video);
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
                                TUICallKit.createInstance(OnLineActivity.this).call(doctorTimId, TUICallDefine.MediaType.Audio);
                            } else {
                                TUICallKit.createInstance(OnLineActivity.this).call(doctorTimId, TUICallDefine.MediaType.Video);
                            }
                        }
                    }
                });
    }

    private void getOnlineNum() {
        EasyHttp.get(this)
                .api(new OnlineAdviceNumApi(idCard))
                .request(new HttpCallback<HttpData<OnlineAdviceNumApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<OnlineAdviceNumApi.Bean> result) {
                        if (result.getData().getVioceReamin() == 0 && result.getData().getVideoReamin() == 0) {
                            getGuideData("noServiceCount");
                        } else {
                            rlSuccess.setVisibility(View.VISIBLE);
                            tabLayout.removeAllTabs();
                            dateTabs.clear();
                            dateTabs.add("全部");
//                        dateTabs.add("语音咨询（剩余" + result.getData().getVioceReamin() + "/" + result.getData().getVioceSum() + "次）");
//                        dateTabs.add("视频咨询（剩余" + result.getData().getVideoReamin() + "/" + result.getData().getVideoSum() + "次）");
                            dateTabs.add("语音咨询(" + result.getData().getVioceReamin() + "次)");
                            dateTabs.add("视频咨询(" + result.getData().getVideoReamin() + "次)");
                            for (String bean : dateTabs) {
                                TabLayout.Tab tab = tabLayout.newTab();
                                View tabView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_online_tab, null);
                                TextView tabText = tabView.findViewById(R.id.item_tv_tab);
                                tabText.setText(bean);
                                tab.setCustomView(tabView);
                                tabLayout.addTab(tab);
                            }
                        }
                    }
                });
    }

//    private void getOnlineDoctor(int type) {//type:1代表刷新2代表加载
//        EasyHttp.get(this)
//                .api(new OnlineDoctorApi(businessType, pageNum, pageSize))
//                .request(new HttpCallback<HttpData<OnlineDoctorApi.Bean>>(this) {
//
//                    @Override
//                    public void onSucceed(HttpData<OnlineDoctorApi.Bean> result) {
//                        if (type == 1) {
//                            doctorList.clear();
//                            refreshLayout.finishRefresh();
//                            if (result.getData().getDoctorList().size() == 0) {
//                                noData.setVisibility(View.VISIBLE);
//                            } else {
//                                noData.setVisibility(View.GONE);
//                                doctorList = result.getData().getDoctorList();
//                            }
//                        } else {
//                            refreshLayout.finishLoadMore();
//                            if (result.getData().getDoctorList().size() == 0) {
//                            } else {
//                                doctorList.addAll(result.getData().getDoctorList());
//                            }
//                        }
//                        onlineAdapter.setList(doctorList);
//                        if (!isLogin && doctorList.size() > 0) {
//                            getIMLogin(binderId, doctorList.get(0).getId());
//                        }
//                    }
//                });
//    }

    private void getOnlineDoctor(int type) {//type:1代表刷新2代表加载
        EasyHttp.get(this)
                .api(new OnlineDoctorV2Api(idCard, pageNum, pageSize))
                .request(new HttpCallback<HttpData<OnlineDoctorV2Api.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<OnlineDoctorV2Api.Bean> result) {
                        if (result.getData().getPublicReamin() == 0 && result.getData().getVioceReamin() == 0 && result.getData().getVideoReamin() == 0) {
                            getGuideData("noServiceCount");
                        } else {
                            rlSuccess.setVisibility(View.VISIBLE);
                            if (type == 1) {
                                doctorList.clear();
                                refreshLayout.finishRefresh();
                                if (result.getData().getDoctorList().size() == 0) {
                                    noData.setVisibility(View.VISIBLE);
                                } else {
                                    noData.setVisibility(View.GONE);
                                    doctorList = result.getData().getDoctorList();
                                }
                            } else {
                                refreshLayout.finishLoadMore();
                                if (result.getData().getDoctorList().size() == 0) {
                                } else {
                                    doctorList.addAll(result.getData().getDoctorList());
                                }
                            }
                            onlineAdapter.setList(doctorList);
                            if (!isLogin && doctorList.size() > 0) {
                                getIMLogin(binderId, doctorList.get(0).getId());
                            }
                        }
                    }
                });
    }

    private void getIMParam(long binderId, String doctorId) {
        EasyHttp.get(this)
                .api(new IMParamApi(binderId, doctorId))
                .request(new HttpCallback<HttpData<IMParamApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<IMParamApi.Bean> result) {

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
                            binderTimId = result.getData().getTimBinder().getBinderTimId();
                            loginTUI(result.getData().getTimBinder().getBinderTimId(), result.getData().getTimBinder().getBinderTimUsersig());
//                            loginTUI("70906b8e1a2da6b5dd25ec81973db37c", "eJw1TksOgjAUvEvXBtoHtIXEhTuNfBJRWQOvQjUoVoJE490loLObb*ZN9mFqqaHVRpFAAB2xmLReGRIQsCiZ*QMvedtqJAFzKeWO60qYHY3q2umTngqC*pQXUrEcMOeFhwieKiXzhYOFI8r-mq7GsNlVxWaIwiSpzdkc12Eq7k093F52V2*zJx76eIWQxZHtLn-FTjfjUcYlBwYSxOcLRDY4rw__");
                        }
                    }
                });
    }

    private void getIMDoctor(long binderId, long doctorId, String serviceCode) {
        EasyHttp.get(this)
                .api(new IMLoginApi(binderId, doctorId))
                .request(new HttpCallback<HttpData<IMLoginApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<IMLoginApi.Bean> result) {
                        if (result.getCode().equals("00000")) {
                            doctorTimId = result.getData().getTimDoctor().getDoctorTimId();
                            judgeAdvice(serviceCode, doctorId, binderId);
                        }
                    }
                });
    }

    private void loginTUI(String userId, String userSig) {
        if (mWaitDialog == null) {
            mWaitDialog = new WaitDialog.Builder(this)
                    // 消息文本可以不用填写
                    .setMessage("音视频组件初始化中。。。")
                    .create();
        }
        if (!mWaitDialog.isShowing()) {
            mWaitDialog.show();
        }
        //设置对登录结果的监听器
        TUILoginListener mLoginListener = new TUILoginListener() {
            @Override
            public void onKickedOffline() {
                super.onKickedOffline();
                Log.i(TAG, "You have been kicked off the line. Please login again!");
                //logout();
            }

            @Override
            public void onUserSigExpired() {
                super.onUserSigExpired();
                Log.i(TAG, "Your user signature information has expired");
                //logout();
            }
        };
        TUILogin.addLoginListener(mLoginListener);

        //登录
        TUILogin.login(this,
                1400634482,     // 请替换为步骤一取到的 SDKAppID
                userId,        // 请替换为您的 UserID
                userSig,  // 您可以在控制台中计算一个 UserSig 并填在这个位置
                new TUICallback() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "login success");
                        isLogin = true;
                        mWaitDialog.dismiss();
                    }


                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        Log.e(TAG, "login failed, errorCode: " + errorCode + " msg:" + errorMessage);
                        Toast.makeText(OnLineActivity.this, errorMessage + ",请退出重试！", Toast.LENGTH_SHORT).show();
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
                            rlFail.setVisibility(View.VISIBLE);
                            noOnline.setText(result.getData().getSteerDesc());
                            GlideApp.with(OnLineActivity.this)
                                    .load(result.getData().getSteerImg())
                                    .into(ivQr);
                        }
                    }
                });
    }

    @Override
    public void handleIntent(BotIntent intent, String customData) {
        Intent activityIntent;
        String intentResult = getString(R.string.result_intent) + intent.name + ",slots:" + intent.slots;
        Log.d(TAG, "handleIntent: " + intentResult);
        Log.d(TAG, "handleIntentType: " + handleType);
        if ("app_consultation_video".equals(intent.name)) {//视频咨询
            if (handleType.equals("5")){
                handleType = "";
                getIMDoctor(binderId, doctor.getId(), "4");
            } else {
                handleType = "4";
                BotSdk.getInstance().speak("请问您想视频咨询第几个医生", true);
            }
        } else if ("app_consultation_audio".equals(intent.name)) {//语音咨询
            if (handleType.equals("5")){
                handleType = "";
                getIMDoctor(binderId, doctor.getId(), "3");
            } else {
                handleType = "3";
                BotSdk.getInstance().speak("请问您想语音咨询第几个医生", true);
            }
        } else if ("app_list_select_item".equals(intent.name)) {//打开第几个
            if (Integer.parseInt(intent.slots.get(0).value) <= doctorList.size()) {
                if (handleType.equals("3")) {
                    handleType = "";
                    getIMDoctor(binderId, doctorList.get(Integer.parseInt(intent.slots.get(0).value) - 1).getId(), "3");
                } else if (handleType.equals("4")) {
                    handleType = "";
                    getIMDoctor(binderId, doctorList.get(Integer.parseInt(intent.slots.get(0).value) - 1).getId(), "4");
                } else {
                    handleType = "5";
                    doctor = doctorList.get(Integer.parseInt(intent.slots.get(0).value) - 1);
                    if (doctor.isStartVoice() && doctor.isStartVideo()){
                        BotSdk.getInstance().speak("请问您想语音咨询还是视频咨询", true);
                    } else {
                        if (doctor.isStartVoice()){
                            handleType = "";
                            doctor = null;
                            getIMDoctor(binderId, doctor.getId(), "3");
                        } else if (doctor.isStartVideo()){
                            handleType = "";
                            doctor = null;
                            getIMDoctor(binderId, doctor.getId(), "4");
                        }
                    }
//                    activityIntent = new Intent(OnLineActivity.this, OnLineDetailActivity.class);
//                    activityIntent.putExtra("data", doctorList.get(Integer.parseInt(intent.slots.get(0).value) - 1));
//                    activityIntent.putExtra("binderId", binderId);
//                    startActivity(activityIntent);
                }
            } else {
                BotSdk.getInstance().speakRequest("抱歉！没有第" + intent.slots.get(0).value + "个医生");
            }
        } else if ("consultation_list".equals(intent.name)) {//咨询第几个
            if (Integer.parseInt(intent.slots.get(1).value) <= doctorList.size()) {
                if ("app_consultation_audio".equals(intent.slots.get(0).name)) {
                    getIMDoctor(binderId, doctorList.get(Integer.parseInt(intent.slots.get(1).value) - 1).getId(), "3");
                } else if ("app_consultation_video".equals(intent.slots.get(0).name)) {
                    getIMDoctor(binderId, doctorList.get(Integer.parseInt(intent.slots.get(1).value) - 1).getId(), "4");
                }
            } else {
                BotSdk.getInstance().speakRequest("抱歉！没有第" + intent.slots.get(1).value + "个医生");
            }
        } else {
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
//        getOnlineNum();
        pageNum = 0;
        getOnlineDoctor(1);
        BotMessageListener.getInstance().addCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BotMessageListener.getInstance().clearCallback();
    }

}
