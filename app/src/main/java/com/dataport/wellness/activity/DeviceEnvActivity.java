package com.dataport.wellness.activity;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.duer.botsdk.BotIntent;
import com.dataport.wellness.R;
import com.dataport.wellness.adapter.DeviceEnvAdapter;
import com.dataport.wellness.api.health.CancelRecordApi;
import com.dataport.wellness.api.health.DeviceEnvApi;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.http.HttpData;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;
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

    private long binderId;
    private int pageNum = 0;
    private int pageSize = 10;

    private List<DeviceEnvApi.Bean.DeviceEnvListDTO> recordList = new ArrayList<>();
    private DeviceEnvAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_env_list);
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
        adapter = new DeviceEnvAdapter(this);
        contentRv.setAdapter(adapter);
        adapter.setFlagReadClickListener((data, pos) -> flagRead(data.getId()));//标记已读
        getOnlineRecord(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 获取分页数据
     * @param type 1=刷新，2=加载
     */
    private void getOnlineRecord(int type) {//type:1代表刷新2代表加载
        EasyHttp.get(this)
                .api(new DeviceEnvApi(binderId, pageNum, pageSize))
                .request(new HttpCallback<HttpData<DeviceEnvApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<DeviceEnvApi.Bean> result) {
                      /*  if (type == 1) {
                            recordList.clear();
                            refreshLayout.finishRefresh();
                            if (null==result.getData().getDeviceEnvListDTOList()||result.getData().getDeviceEnvListDTOList().size() == 0) {
                                noData.setVisibility(View.VISIBLE);
                            } else {
                                noData.setVisibility(View.GONE);
                                recordList = result.getData().getDeviceEnvListDTOList();
                            }
                        } else {
                            refreshLayout.finishLoadMore();
                            if (null==result.getData().getDeviceEnvListDTOList()||result.getData().getDeviceEnvListDTOList().size() == 0) {
                            } else {
                                recordList.addAll(result.getData().getDeviceEnvListDTOList());
                            }
                        }*/
                        DeviceEnvApi a=new DeviceEnvApi(1,1,1);
                        DeviceEnvApi.Bean b=a.new Bean();
                        DeviceEnvApi.Bean.DeviceEnvListDTO c=b.new DeviceEnvListDTO();
                        c.setAlarmReason("河北省秦皇岛市海港区海怡学府，水浸告警");
                        c.setId(111);
                        c.setDeviceName("告警设备");
                        c.setAlarmTime("2023-07-14 12:00:00");
                        DeviceEnvApi.Bean.DeviceEnvListDTO d=b.new DeviceEnvListDTO();
                        d.setAlarmReason("河北省秦皇岛市海港区海怡学府，水浸告警");
                        d.setId(111);
                        d.setDeviceName("告警设备");
                        d.setAlarmTime("2023-07-14 12:00:00");
                        d.setReadTime("2024-03-03 12:00:00");
                        recordList.add(c);
                        recordList.add(d);
                        adapter.setList(recordList);
                    }
                });
    }

    /*标记已读*/
    private void flagRead(int adviceId) {
        Toast.makeText(this,"我被点击",Toast.LENGTH_LONG).show();
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
