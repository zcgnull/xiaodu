package com.dataport.wellness.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.duer.botsdk.BotIntent;
import com.baidu.duer.botsdk.BotSdk;
import com.dataport.wellness.R;
import com.dataport.wellness.adapter.OnLineRecordAdapter;
import com.dataport.wellness.api.health.CancelRecordApi;
import com.dataport.wellness.api.health.IMLoginApi;
import com.dataport.wellness.api.health.OnlineRecordApi;
import com.dataport.wellness.api.health.TurnOffApi;
import com.dataport.wellness.api.health.TurnOnApi;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.http.HttpData;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
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

public class OnLineRecordActivity extends BaseActivity implements IBotIntentCallback {

    private static final String TAG = "OnLineRecordActivity";
    private RefreshLayout refreshLayout;
    private RelativeLayout noData;
    private RecyclerView contentRv;

    private long binderId;
    private int pageNum = 0;
    private int pageSize = 10;
    private int delay = 0;
    private String doctorTimId;
    private String binderTimId;
    private String type;

    private List<OnlineRecordApi.Bean.AdviceRecordListDTO> recordList = new ArrayList<>();
    private OnLineRecordAdapter adapter;
    private FinishActivityRecevier mRecevier;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        binderId = getIntent().getLongExtra("binderId", 0);
        findViewById(R.id.ln_back).setOnClickListener(v -> finish());
        noData = findViewById(R.id.rl_no_data);
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            pageNum = 0;
            getOnlineRecord(1);
        });
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            pageNum += 1;
            getOnlineRecord(2);
        });
        contentRv = findViewById(R.id.rv_content);
        GridLayoutManager contentManger = new GridLayoutManager(this, 1);
        contentManger.setOrientation(GridLayoutManager.VERTICAL);
        contentRv.setLayoutManager(contentManger);
        adapter = new OnLineRecordAdapter(this);
        contentRv.setAdapter(adapter);
        adapter.setCancelListener((data, pos) -> cancelRecord(data.getId()));
        adapter.setAdviceListener((data, pos) -> {
            type = data.getServiceCode();
            delay = Integer.valueOf(data.getLimitDuration());
            getIMLogin(binderId, data.getDoctorId());
        });
        getOnlineRecord(1);
        TUICallEngine.createInstance(OnLineRecordActivity.this).addObserver(observer);
        //注册广播
        mRecevier = new FinishActivityRecevier();
        registerFinishReciver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TUICallEngine.createInstance(OnLineRecordActivity.this).removeObserver(observer);
        recordList=null;
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
            statService(delay);
        }

        public void onCallEnd(TUICommonDefine.RoomId roomId, TUICallDefine.MediaType callMediaType, TUICallDefine.Role callRole, long totalTime) {
            Log.i(TAG, "onCallEnd!");
            turnOff(type);
            getOnlineRecord(1);
        }

        public void onUserNetworkQualityChanged(List<TUICommonDefine.NetworkQualityInfo> networkQualityList) {
            Log.i(TAG, "onUserNetworkQualityChanged!");
        }
    };

    private void getOnlineRecord(int type) {//type:1代表刷新2代表加载
        EasyHttp.get(this)
                .api(new OnlineRecordApi(binderId, pageNum, pageSize))
                .request(new HttpCallback<HttpData<OnlineRecordApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<OnlineRecordApi.Bean> result) {
                        if (type == 1) {
                            recordList.clear();
                            refreshLayout.finishRefresh();
                            if (result.getData().getAdviceRecordList().size() == 0) {
                                noData.setVisibility(View.VISIBLE);
                            } else {
                                noData.setVisibility(View.GONE);
                                recordList = result.getData().getAdviceRecordList();
                            }
                        } else {
                            refreshLayout.finishLoadMore();
                            if (result.getData().getAdviceRecordList().size() == 0) {
                            } else {
                                recordList.addAll(result.getData().getAdviceRecordList());
                            }
                        }
                        adapter.setList(recordList);
                    }
                });
    }

    private void cancelRecord(int adviceId) {
        EasyHttp.post(this)
                .api(new CancelRecordApi(adviceId))
                .request(new HttpCallback<HttpData>(this) {

                    @Override
                    public void onSucceed(HttpData result) {
                        if (result.getCode().equals("00000")) {
                            pageNum = 0;
                            getOnlineRecord(1);
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
                            doctorTimId = result.getData().getTimDoctor().getDoctorTimId();
                            binderTimId = result.getData().getTimBinder().getBinderTimId();
                            if (type.equals("3")) {
                                TUICallKit.createInstance(OnLineRecordActivity.this).call(doctorTimId, TUICallDefine.MediaType.Audio);
                            } else {
                                TUICallKit.createInstance(OnLineRecordActivity.this).call(doctorTimId, TUICallDefine.MediaType.Video);
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

    private void statService(int delay) {
        ScheduledExecutorService mService = Executors.newScheduledThreadPool(1);
        mService.schedule(new Runnable() {
            @Override
            public void run() {
                hangUp();
            }
        }, delay, TimeUnit.SECONDS);
    }

    private void hangUp() {
        TUICallEngine.createInstance(OnLineRecordActivity.this).hangup(new TUICommonDefine.Callback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "hangup!");
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void handleIntent(BotIntent intent, String customData) {
        if ("hangup".equals(intent.name)) {
            hangUp();
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
    private class FinishActivityRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //根据需求添加自己需要关闭页面的action
            if ("OnLineRecordActivity".equals(intent.getAction())) {
                OnLineRecordActivity.this.finish();
            }
        }
    }

    private void registerFinishReciver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("OnLineRecordActivity");
        registerReceiver(mRecevier, intentFilter);
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
    }

}
