package com.dataport.wellness.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.duer.bot.BotMessageProtocol;
import com.baidu.duer.bot.event.payload.LinkClickedEventPayload;
import com.baidu.duer.botsdk.BotIntent;
import com.baidu.duer.botsdk.BotSdk;
import com.baidu.duer.botsdk.UiContextPayload;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dataport.wellness.R;
import com.dataport.wellness.activity.dialog.LoadingDialog;
import com.dataport.wellness.adapter.OnlineAdapter;
import com.dataport.wellness.api.health.AdviceDoctorApi;
import com.dataport.wellness.api.health.IMLoginApi;
import com.dataport.wellness.api.health.JudgeAdviceApi;
import com.dataport.wellness.api.health.OnlineDoctorV2Api;
import com.dataport.wellness.api.health.StartRecordApi;
import com.dataport.wellness.api.health.TurnOffApi;
import com.dataport.wellness.api.health.TurnOnApi;
import com.dataport.wellness.api.smalldu.GuideDataApi;
import com.dataport.wellness.been.OnLineDoctorBean;
import com.dataport.wellness.been.OnlineDoctorListBean;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.http.HttpData;
import com.dataport.wellness.http.HttpIntData;
import com.dataport.wellness.http.glide.GlideApp;
import com.dataport.wellness.utils.BotConstants;
import com.dataport.wellness.utils.SlotsUtil;
import com.dataport.wellness.utils.ToastUtil;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class OnLineActivity extends BaseActivity implements IBotIntentCallback {

    private String TAG = "OnLineActivity";
    private RecyclerView contentRv;
    private RefreshLayout refreshLayout;
    private RelativeLayout noData, rlSuccess, rlFail;
    private MarqueeView marqueeView;
    private TextView noOnline;
    private ImageView ivQr;
    private OnlineAdapter onlineAdapter;
    private List<OnlineDoctorListBean> doctorList = new ArrayList<>();
    private int doctorNum; //医生总人数
    private OnlineDoctorListBean doctor;
    private int pageNum = 0;
    private int pageSize = 8;
    private String idCard;
    private long binderId;
    private boolean isLogin = false;
    private String doctorTimId;
    private String binderTimId;
    private String type = "";
    private String handleType = "";
    private int delay = 0;
    private LoadingDialog mWaitDialog;
    //private TUILogin tuiLogin;
    private GridLayoutManager contentManger;
    private List<String> messages = new ArrayList<>();

    private TUICallback tuiCallback;

    private boolean calling = false; //是否正在通话

    private int limit = 0; //是否限制时长

    private ScheduledExecutorService mService; //挂断倒计时用的线程池
    private ScheduledFuture<?> schedule; //挂断倒计时任务
    private int position = 1;
    private LinearSmoothScroller smoothScroller;

    //设置对登录结果的监听器
    private TUILoginListener mLoginListener = new TUILoginListener() {
        @Override
        public void onKickedOffline() {
            super.onKickedOffline();
            Log.i(TAG, "You have been kicked off the line. Please login again!");
            logout();
            ToastUtil.showShort(getApplicationContext(), "账号在其他地方登录了,请重新进入线上医生！");
            Intent intent = new Intent("OnLineDetailActivity");
            OnLineActivity.this.sendBroadcast(intent);
            intent = new Intent("OnLineRecordActivity");
            OnLineActivity.this.sendBroadcast(intent);
            finish();
        }

        @Override
        public void onUserSigExpired() {
            super.onUserSigExpired();
            Log.i(TAG, "Your user signature information has expired");
            ToastUtil.showShort(getApplicationContext(), "账号登录过期了,请重新进入线上医生！");
            logout();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        initView();
        //initData();

        contentManger = new GridLayoutManager(getApplicationContext(), 2);
        contentManger.setOrientation(GridLayoutManager.VERTICAL);
        contentRv.setLayoutManager(contentManger);
        onlineAdapter = new OnlineAdapter(this);
        contentRv.setAdapter(onlineAdapter);
        onlineAdapter.setListener((data, pos) -> {
            TUICallEngine.createInstance(getApplicationContext()).removeObserver(observer);
            Intent intent = new Intent(getApplicationContext(), OnLineDetailActivity.class);
            intent.putExtra("data", data);
            intent.putExtra("binderId", binderId);
            intent.putExtra("idCard", idCard);
            startActivity(intent);
        });
        onlineAdapter.setYyClickListener((data, pos) -> getIMDoctor(binderId, data.getId(), "3"));
        onlineAdapter.setSpClickListener((data, pos) -> getIMDoctor(binderId, data.getId(), "4"));
        smoothScroller = new LinearSmoothScroller(this) {
            //垂直滑动结束的位置
            @Override
            public int getVerticalSnapPreference() {
                return SNAP_TO_START;
            }
            //滑动速度
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 0.5f;
            }
        };

        initClientContext();
    }

    private void initView() {
        findViewById(R.id.ln_back_online).setOnClickListener(v -> finish());
        findViewById(R.id.ln_back1).setOnClickListener(v -> finish());
        findViewById(R.id.ln_record).setOnClickListener(v -> {
            TUICallEngine.createInstance(getApplicationContext()).removeObserver(observer);
            Intent intent = new Intent(getApplicationContext(), OnLineRecordActivity.class);
            intent.putExtra("binderId", binderId);
            startActivity(intent);
        });
        idCard = getIntent().getStringExtra("idCard");
        binderId = getIntent().getLongExtra("binderId", 0);
        Log.d("zcg", String.valueOf(binderId));
        noData = findViewById(R.id.rl_no_data);
        rlSuccess = findViewById(R.id.rl_success);
        rlFail = findViewById(R.id.rl_fail);
        noOnline = findViewById(R.id.tv_no_online);
        ivQr = findViewById(R.id.iv_qr);
        contentRv = findViewById(R.id.rv_content);
        contentRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState){
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                //判断是当前layoutManager是否为LinearLayoutManager
                // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取第一个可见view的位置
                    position = linearManager.findFirstVisibleItemPosition();
                    Log.d("zcg", "第一个可见view的位置: " + position);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            pageNum = 0;
            getOnlineDoctor(1);
        });
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            pageNum += 1;
            getOnlineDoctor(2);
        });

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
            Log.d("zcg", "通话取消");
            calling = false;
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

    /**
     *
     * @param state 状态 1：自动挂断 2：小度挂断
     */
    private void hangUp(int state) {
        if (state == 1 && calling){
            TUICallEngine.createInstance(getApplicationContext()).hangup(new TUICommonDefine.Callback() {
                @Override
                public void onSuccess() {
                    ToastUtil.showShort(getApplicationContext(), "通话超过单次服务时长限制，系统自动挂断。");
                    Log.i(TAG, "hangup!");
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
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
                                    TUICallKit.createInstance(getApplicationContext()).call(doctorTimId, TUICallDefine.MediaType.Audio);
                                } else {
                                    TUICallKit.createInstance(getApplicationContext()).call(doctorTimId, TUICallDefine.MediaType.Video);
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
                                TUICallKit.createInstance(getApplicationContext()).call(doctorTimId, TUICallDefine.MediaType.Audio);
                            } else {
                                TUICallKit.createInstance(getApplicationContext()).call(doctorTimId, TUICallDefine.MediaType.Video);
                            }
                        }
                    }
                });
    }

    private void getOnlineDoctor(int type) {//type:1代表刷新2代表加载
        EasyHttp.get(this)
                .api(new OnlineDoctorV2Api(idCard, pageNum, pageSize))
                .request(new HttpCallback<HttpData<OnLineDoctorBean>>(this) {

                    @Override
                    public void onSucceed(HttpData<OnLineDoctorBean> result) {
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
                                    doctorList.addAll(result.getData().getDoctorList());
                                }
                            } else {
                                refreshLayout.finishLoadMore();
                                if (result.getData().getDoctorList().size() == 0) {
                                    BotSdk.getInstance().speakRequest("没有更多数据了");
                                } else {
                                    doctorList.addAll(result.getData().getDoctorList());
                                }
                            }
                            onlineAdapter.setList(doctorList);
                            doctorNum = result.getData().getTotalElements();
                            if (!isLogin && doctorList.size() > 0) {
                               getIMLogin(binderId, doctorList.get(0).getId());
                            }
                            Log.d("zcg",  "剩余语音次数：" + result.getData().getVioceReamin() + "   剩余视频次数" + result.getData().getVideoReamin());
                        }
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
            mWaitDialog = new LoadingDialog(this)
                    // 消息文本可以不用填写
                    .setMsg("音视频组件初始化中...")
                    .setTextSize(15);
        }
        if (!mWaitDialog.isShowing()) {
            mWaitDialog.show();
        }

        TUILogin.addLoginListener(mLoginListener);
        tuiCallback= new TUICallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "login success");
                isLogin = true;
                mWaitDialog.dismiss();
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.e(TAG, "login failed, errorCode: " + errorCode + " msg:" + errorMessage);
                ToastUtil.showShort(getApplicationContext(), errorMessage + ",请退出重试！");
            }
        };
        //登录
        TUILogin.login(getApplicationContext(),
                1400634482,     // 请替换为步骤一取到的 SDKAppID
                userId,        // 请替换为您的 UserID
                userSig,  // 您可以在控制台中计算一个 UserSig 并填在这个位置
                tuiCallback
               );
    }

    /**
     * 获取引导页数据
     * @param steerType
     */
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
                            rlSuccess.setVisibility(View.GONE);
                            rlFail.setVisibility(View.VISIBLE);

                            noOnline.setText(result.getData().getSteerDesc());
                            GlideApp.with(getApplicationContext())
                                    .load(result.getData().getSteerImg())
                                    .skipMemoryCache(true)//禁用内存缓存功能
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不缓存任何内容
                                    .into(ivQr);
                        }
                    }
                });
    }

    /**
     * 判度RecyclerView是否滑动到了底部区域
     * @param recyclerView
     * @return
     */
    public boolean isSlideToBottom(RecyclerView recyclerView){
        if (recyclerView == null) return false;
        if ((recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()) * 1.1 >= recyclerView.computeVerticalScrollRange()){
            return true;
        }
        return false;
    }

    /**
     * 判度RecyclerView是否滑动到了顶部
     * @param recyclerView
     * @return
     */
    public boolean isSlideToTop(RecyclerView recyclerView){
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollOffset() == 0){
            return true;
        }
        return false;
    }


    /**
     * 上一页
     * @param itemNum 每页标签数
     */
    private void pageUp(int itemNum){
        if (isSlideToTop(contentRv)){
            BotSdk.getInstance().speakRequest("已经是顶部了");
        } else {
            position-=itemNum;
            if(position < 0){
                position = 0;
            }
            smoothScroller.setTargetPosition(position); //要到达的位置
            RecyclerView.LayoutManager layoutManager = contentRv.getLayoutManager();
            layoutManager.startSmoothScroll(smoothScroller); //开始滑动
            Log.d("zcg", "isAtBottom" + isSlideToBottom(contentRv));
        }
    }

    /**
     * 下一页
     * @param itemNum 每页标签数
     */
    private void pageDown(int itemNum){
        if (isSlideToBottom(contentRv)){
            //加载更多
            refreshLayout.autoLoadMore();
        } else {
            position+=itemNum;
            if(position > doctorList.size()){
                position = doctorList.size();
            }
            smoothScroller.setTargetPosition(position); //要到达的位置
            RecyclerView.LayoutManager layoutManager = contentRv.getLayoutManager();

            layoutManager.startSmoothScroll(smoothScroller); //开始滑动
        }
    }

    /**
     * 注册UIControl， UIControl就是根据界面元素自定义语音指令
     */
    private void initClientContext() {
        UiContextPayload payload = new UiContextPayload();
        //翻页
        payload.addHyperUtterance(
                BotConstants.VOICE_PAGE_URL,
                null,
                BotConstants.UiControlType.STEP,
                null);


        BotSdk.getInstance().updateUiContext(payload);
    }



    @Override
    public void handleIntent(BotIntent intent, String customData) {
        String intentResult = getString(R.string.result_intent) + intent.name + ",slots:" + intent.slots;
        Log.d(TAG, "handleIntent: " + intentResult);
        Log.d(TAG, "handleIntentType: " + handleType);
        if ("app_consultation_video".equals(intent.name)) {//视频咨询
            if (handleType.equals("5")) {
                handleType = "";
                if (doctor.isStartVideo()) {
                    getIMDoctor(binderId, doctor.getId(), "4");
                } else {
                    BotSdk.getInstance().speakRequest("当前医生不支持视频咨询");
                }
            } else {
                handleType = "4";
                BotSdk.getInstance().speak("请问您想视频咨询第几个医生", true);
            }
        } else if ("app_consultation_audio".equals(intent.name)) {//语音咨询
            if (handleType.equals("5")) {
                handleType = "";
                if (doctor.isStartVoice()) {
                    getIMDoctor(binderId, doctor.getId(), "3");
                } else {
                    BotSdk.getInstance().speakRequest("当前医生不支持语音咨询");
                }
            } else {
                handleType = "3";
                BotSdk.getInstance().speak("请问您想语音咨询第几个医生", true);
            }
        } else if ("app_list_select_item".equals(intent.name)) {//咨询第几个
            if (SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number") <= doctorList.size() && SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number") <= doctorNum) {
                if (handleType.equals("3")) {
                    handleType = "";
                    if (doctorList.get(SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number") - 1).isStartVoice()) {
                        getIMDoctor(binderId, doctorList.get(SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number") - 1).getId(), "3");
                    } else {
                        BotSdk.getInstance().speakRequest("当前医生不支持语音咨询");
                    }
                } else if (handleType.equals("4")) {
                    handleType = "";
                    if (doctorList.get(SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number") - 1).isStartVideo()) {
                        getIMDoctor(binderId, doctorList.get(SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number") - 1).getId(), "4");
                    } else {
                        BotSdk.getInstance().speakRequest("当前医生不支持视频咨询");
                    }
                } else {
                    handleType = "5";
                    doctor = doctorList.get(SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number") - 1);
                    if (doctor.isStartVoice() && doctor.isStartVideo()) {
                        BotSdk.getInstance().speak("请问您想语音咨询还是视频咨询", true);
                    } else {
                        if (doctor.isStartVoice()) {
                            getIMDoctor(binderId, doctor.getId(), "3");
                            handleType = "";
                            doctor = null;
                        } else if (doctor.isStartVideo()) {
                            getIMDoctor(binderId, doctor.getId(), "4");
                            handleType = "";
                            doctor = null;
                        }
                    }
                }
            } else if (SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number") <= doctorNum) {
                BotSdk.getInstance().speakRequest("抱歉！本页没有第" + SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number") + "个医生，请翻页看看");
            } else {
                BotSdk.getInstance().speakRequest("抱歉！没有第" + SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number") + "个医生");
            }
        } else if ("consultation_list".equals(intent.name)) {//咨询第几个
            int num = SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number");
            if (num <= doctorList.size() &&  num <= doctorNum) {
                if (SlotsUtil.hasSlot(intent.slots, "app_consultation_audio")) {
                    if (doctorList.get(num - 1).isStartVoice()) {
                        getIMDoctor(binderId, doctorList.get(num - 1).getId(), "3");
                    } else {
                        BotSdk.getInstance().speakRequest("当前医生不支持语音咨询");
                    }
                } else if (SlotsUtil.hasSlot(intent.slots, "app_consultation_video")) {
                    if (doctorList.get(num - 1).isStartVideo()) {
                        getIMDoctor(binderId, doctorList.get(num - 1).getId(), "4");
                    } else {
                        BotSdk.getInstance().speakRequest("当前医生不支持视频咨询");
                    }
                }
            } else if (num <= doctorNum) {
                BotSdk.getInstance().speakRequest("抱歉！本页没有第" + num + "个医生，请翻页看看");
            } else {
                BotSdk.getInstance().speakRequest("抱歉！没有第" + num + "个医生");
            }


        } else if ("hangup".equals(intent.name)) {
            hangUp(2);
        } else {
            BotSdk.getInstance().speakRequest("我没有听清，请再说一遍");
        }
    }

    /**
     * 云端返回的UIContext匹配结果
     *
     * @param url      自定义交互描述中的url
     * @param paramMap 对于系统内建类型，参数列表。参数就是从query中通过分词取得的关键词。
     */
    @Override
    public void onClickLink(String url, HashMap<String, String> paramMap) {
        Log.d("zcg", "本地意图");
        if (BotConstants.VOICE_PAGE_URL.equals(url)) {
            if ("1".equals(paramMap.get("by"))){
                //下一页
                pageDown(4);
            } else if ("-1".equals(paramMap.get("by"))){
                //上一页
                pageUp(4);
            }
        }
    }

    @Override
    public void onHandleScreenNavigatorEvent(int event) {

    }

    private void logout(){
        TUILogin.logout(new TUICallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "onSuccess: tui logout success");
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.i(TAG, "onError: tui logout error:" + errorMessage);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pageNum = 0;

        getOnlineDoctor(1);
        BotMessageListener.getInstance().addCallback(this);
        TUICallEngine.createInstance(getApplicationContext()).addObserver(observer);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BotMessageListener.getInstance().clearCallback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TUILogin.removeLoginListener(mLoginListener);
        logout();
        TUICallEngine.createInstance(getApplicationContext()).removeObserver(observer);
        TUICallEngine.destroyInstance();
        contentRv.stopScroll();
        smoothScroller = null;
        if (schedule != null){
            schedule.cancel(true);
        }
        if (mService != null){
            mService.shutdown();
        }
        mService = null;
        ToastUtil.cancelToast();
        ivQr=null;
        tuiCallback=null;
        observer=null;
        //tuiLogin = null;
        doctorList = null;
        mLoginListener = null;
        onlineAdapter = null;
        doctor = null;
        contentRv = null;
        mWaitDialog = null;
        contentManger = null;
        messages = null;
        marqueeView=null;
        doctorTimId=null;
        binderTimId=null;
    }
}


