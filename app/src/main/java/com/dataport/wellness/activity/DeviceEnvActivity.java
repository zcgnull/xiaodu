package com.dataport.wellness.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.duer.bot.BotMessageProtocol;
import com.baidu.duer.botsdk.BotIntent;
import com.baidu.duer.botsdk.BotSdk;
import com.dataport.wellness.R;
import com.dataport.wellness.adapter.DeviceEnvAdapter;
import com.dataport.wellness.api.health.DeviceEnvApi;
import com.dataport.wellness.api.health.DeviceEnvCountApi;
import com.dataport.wellness.api.health.DeviceEnvProcessApi;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.http.HttpData;
import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 环境监测
 */
public class DeviceEnvActivity extends BaseActivity implements IBotIntentCallback {
    private static final String TAG = "DeviceEnvActivity";
    private RefreshLayout refreshLayout;
    private RelativeLayout noData;
    private RecyclerView contentRv;

    private Long userId;
    private int pageNum = 0;
    private int pageSize = 10;

    private List<DeviceEnvApi.Bean.DeviceEnvListDTO> warnList = new ArrayList<>();
    private DeviceEnvAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_env_list);
        userId = getIntent().getLongExtra("userId", 0);
        findViewById(R.id.ln_back).setOnClickListener(v -> finish());
        noData = findViewById(R.id.rl_no_data);
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            pageNum = 0;
            getDeviceEnvList(1);
        });
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            pageNum += 1;
            getDeviceEnvList(2);
        });
        contentRv = findViewById(R.id.rv_content);
        GridLayoutManager contentManger = new GridLayoutManager(this, 1);
        contentManger.setOrientation(GridLayoutManager.VERTICAL);
        contentRv.setLayoutManager(contentManger);
        adapter = new DeviceEnvAdapter(this);
        contentRv.setAdapter(adapter);
        adapter.setFlagReadClickListener((data, pos) -> flagRead(
                "小度智能屏",BotConstants.SN,
                "4",
                "",
                "",
                 data.getRecordId(),
                "2"));//标记已读
        getDeviceEnvList(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
        warnList = null;
    }

    /**
     * 获取用户未处理报警数量
     *
     * @param userId 用户主键
     */
    private void getDeviceEnvCount(Long userId) {
        EasyHttp.get(this)
                .api(new DeviceEnvCountApi(userId))
                .request(new HttpCallback<HttpData<DeviceEnvCountApi.Bean>>(this) {
                    @Override
                    public void onSucceed(HttpData<DeviceEnvCountApi.Bean> result) {
                        if (result.getCode().equals("00000")) {
                            BotSdk.getInstance().speakRequest("您有" + result.getData().getNum() + "个未处理的告警，请及时处理。");
                        }

                    }
                });
    }

    /**
     * 获取分页数据
     *
     * @param type 1=刷新，2=加载
     */
    private void getDeviceEnvList(int type) {//type:1代表刷新2代表加载
        EasyHttp.get(this)
                .api(new DeviceEnvApi(userId, pageNum, pageSize, BotConstants.SN))
                .request(new HttpCallback<HttpData<DeviceEnvApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<DeviceEnvApi.Bean> result) {
                        if(result.getCode().equals("00000")) {
                            if (type == 1) {
                                warnList.clear();
                                refreshLayout.finishRefresh();
                                if (null == result.getData().getWarnList() || result.getData().getWarnList().size() == 0) {
                                    noData.setVisibility(View.VISIBLE);
                                } else {
                                    noData.setVisibility(View.GONE);
                                    warnList = result.getData().getWarnList();
                                }
                                getDeviceEnvCount(userId);
                            } else {
                                refreshLayout.finishLoadMore();
                                if (null == result.getData().getWarnList() || result.getData().getWarnList().size() == 0) {
                                } else {
                                    warnList.addAll(result.getData().getWarnList());
                                }
                            }

                            adapter.setList(warnList);
                        }else{
                            noData.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    /*标记已读*/
    private void flagRead(String processEnd, String processEquipmentNo, String processWay, String processWayOverview, String processName , Long recordId, String processState) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String processTime  = formatter.format(curDate);
        DeviceEnvProcessApi deviceEnvProcessApi = new DeviceEnvProcessApi(processEnd, processEquipmentNo, processWay, processWayOverview, processName, processTime, recordId, processState);
        EasyHttp.post(this)
                .api(deviceEnvProcessApi)
                .request(new HttpCallback<HttpData>(this) {

                    @Override
                    public void onSucceed(HttpData result) {
                        if (result.getCode().equals("00000")) {
                            pageNum = 0;
                            getDeviceEnvList(1);
                        }
                    }
                });
    }


    @Override
    public void handleIntent(BotIntent intent, String customData) {

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
    }
}
