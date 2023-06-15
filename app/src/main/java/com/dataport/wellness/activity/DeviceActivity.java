package com.dataport.wellness.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.duer.bot.BotMessageProtocol;
import com.baidu.duer.botsdk.BotSdk;
import com.dataport.wellness.R;
import com.dataport.wellness.adapter.DeviceContentAdapter;
import com.dataport.wellness.api.health.DeviceContentApi;
import com.dataport.wellness.api.health.EquipmentListApi;
import com.dataport.wellness.api.health.SignTypeApi;
import com.dataport.wellness.api.smalldu.GuideDataApi;
import com.dataport.wellness.http.HttpData;
import com.dataport.wellness.http.HttpIntData;
import com.dataport.wellness.http.glide.GlideApp;
import com.dataport.wellness.utils.BotConstants;
import com.dataport.wellness.utils.LineChartManager;
import com.dataport.wellness.utils.TimeUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.tabs.TabLayout;
import com.hjq.http.EasyHttp;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.http.request.HttpRequest;

import java.util.ArrayList;
import java.util.List;

public class DeviceActivity extends BaseActivity implements View.OnClickListener {

    private TabLayout firstTab, secondTab, thirdTab;
    //    private RefreshLayout refreshLayout;
    private RecyclerView contentRv;
    private RelativeLayout noData, noDataLine, rlSuccess, rlFail;
    private LineChart lineChart;
    private TextView qs, jl;
    private TextView noBind;
    private ImageView ivQr;

    private List<String> dateTabs = new ArrayList<>();
    private List<SignTypeApi.Bean.ListDTO> signTabs = new ArrayList<>();
    private List<EquipmentListApi.Bean.ListDTO> equipmentTabs = new ArrayList<>();
    private List<DeviceContentApi.Bean.ListDTO> contentList = new ArrayList<>();
    private int binderId;
    private int equipmentBindId = 0;
    private String dataTypeCode = "1";
//    private int pageNum = 1;
//    private int pageSize = 10;

    private DeviceContentAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        findViewById(R.id.ln_back).setOnClickListener(v -> finish());
        findViewById(R.id.ln_back1).setOnClickListener(v -> finish());
        rlSuccess = findViewById(R.id.rl_success);
        rlFail = findViewById(R.id.rl_fail);
        noBind = findViewById(R.id.tv_nobind);
        ivQr = findViewById(R.id.iv_qr);
        Intent intent = getIntent();
        binderId = intent.getIntExtra("binderId", 0);
        noData = findViewById(R.id.rl_no_data);
        noDataLine = findViewById(R.id.rl_no_data_line);
        lineChart = findViewById(R.id.line_chart);
        qs = findViewById(R.id.tv_qs);
        jl = findViewById(R.id.tv_jl);

        firstTab = findViewById(R.id.first_tab);
        firstTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabView = tab.getCustomView().findViewById(R.id.item_tv_tab);
                tabView.setBackground(getResources().getDrawable(R.drawable.service_tab_bg));
                tabView.setTextColor(getResources().getColor(R.color.colorWhite));
                dataTypeCode = signTabs.get(tab.getPosition()).getDataTypeCode();
                getEquipmentList(binderId, dataTypeCode);
                if (dataTypeCode.equals("1")) {
                    qs.setText("血压趋势");
                    jl.setText("血压记录");
                } else {
                    qs.setText("血糖趋势");
                    jl.setText("血糖记录");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabView = tab.getCustomView().findViewById(R.id.item_tv_tab);
                tabView.setBackground(getResources().getDrawable(R.drawable.service_selected_tab_bg));
                tabView.setTextColor(getResources().getColor(R.color.colorBule));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        secondTab = findViewById(R.id.second_tab);
        secondTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                equipmentBindId = equipmentTabs.get(tab.getPosition()).getEquipmentBindId();
                if (thirdTab.getTabAt(0).isSelected()) {
                    getDeviceContent(dataTypeCode, TimeUtil.getInstance().getTimeOneMonth(), TimeUtil.getInstance().getCurrentTime(), 1);
                } else {
                    thirdTab.getTabAt(0).select();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        thirdTab = findViewById(R.id.thire_tab);
        thirdTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabView = tab.getCustomView().findViewById(R.id.item_date_tab);
                tabView.setBackground(getResources().getDrawable(R.drawable.service_tab_bg));
                tabView.setTextColor(getResources().getColor(R.color.colorWhite));
                if (tab.getPosition() == 1) {
                    getDeviceContent(dataTypeCode, TimeUtil.getInstance().getTimeAWeek(), TimeUtil.getInstance().getCurrentTime(), 1);
                } else if (tab.getPosition() == 2) {
                    getDeviceContent(dataTypeCode, TimeUtil.getInstance().getYesterdayTime(), TimeUtil.getInstance().getCurrentTime(), 1);
                } else {
                    if (equipmentBindId != 0) {
                        getDeviceContent(dataTypeCode, TimeUtil.getInstance().getTimeOneMonth(), TimeUtil.getInstance().getCurrentTime(), 1);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabView = tab.getCustomView().findViewById(R.id.item_date_tab);
                tabView.setBackground(getResources().getDrawable(R.drawable.date_tab_unselected));
                tabView.setTextColor(getResources().getColor(R.color.black));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        dateTabs.add("月");
        dateTabs.add("周");
        dateTabs.add("日");
        for (String bean : dateTabs) {
            TabLayout.Tab tab = thirdTab.newTab();
            View tabView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.date_tab, null);
            TextView tabText = tabView.findViewById(R.id.item_date_tab);
            tabText.setText(bean);
            tab.setCustomView(tabView);
            thirdTab.addTab(tab);
        }

        contentRv = findViewById(R.id.rv_content);
        GridLayoutManager contentManger = new GridLayoutManager(this, 1);
        contentManger.setOrientation(GridLayoutManager.VERTICAL);
        contentRv.setLayoutManager(contentManger);
        adapter = new DeviceContentAdapter(this);
        contentRv.setAdapter(adapter);
        adapter.setListener(new DeviceContentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DeviceContentApi.Bean.ListDTO data, int pos) {
                BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
                if (data.getDataType().equals("1")) {
                    String speck = data.getStartTime() + "，您的血压为，高压" + data.getSbp() + "毫米汞柱，低压" + data.getDbp() + "毫米汞柱，脉率为" + data.getBpm() + "次每分钟";
                    BotSdk.getInstance().speakRequest(speck);
                } else {
                    String speck = data.getStartTime() + "，您的血糖为" + data.getGls();
                    BotSdk.getInstance().speakRequest(speck);
                }
            }
        });

//        refreshLayout = findViewById(R.id.refreshLayout);
//        refreshLayout.setOnRefreshListener(refreshlayout -> {
//            pageNum = 1;
//            if (thirdTab.getTabAt(0).isSelected()) {
//                getDeviceContent(dataTypeCode, TimeUtil.getInstance().getYesterdayTime(), TimeUtil.getInstance().getCurrentTime(), 1);
//            } else if (thirdTab.getTabAt(0).isSelected()) {
//                getDeviceContent(dataTypeCode, TimeUtil.getInstance().getTimeAWeek(), TimeUtil.getInstance().getCurrentTime(), 1);
//            } else {
//                getDeviceContent(dataTypeCode, TimeUtil.getInstance().getTimeOneMonth(), TimeUtil.getInstance().getCurrentTime(), 1);
//            }
//        });
//        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
//            pageNum += 1;
//            if (thirdTab.getTabAt(0).isSelected()) {
//                getDeviceContent(dataTypeCode, TimeUtil.getInstance().getYesterdayTime(), TimeUtil.getInstance().getCurrentTime(), 2);
//            } else if (thirdTab.getTabAt(0).isSelected()) {
//                getDeviceContent(dataTypeCode, TimeUtil.getInstance().getTimeAWeek(), TimeUtil.getInstance().getCurrentTime(), 2);
//            } else {
//                getDeviceContent(dataTypeCode, TimeUtil.getInstance().getTimeOneMonth(), TimeUtil.getInstance().getCurrentTime(), 2);
//            }
//        });

        getSignType(binderId);
    }

    private void getSignType(int binderId) {
        EasyHttp.get(this)
                .api(new SignTypeApi(binderId))
                .request(new HttpCallback<HttpData<SignTypeApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<SignTypeApi.Bean> result) {
                        if (result.getCode().equals("00000")) {
                            signTabs = result.getData().getList();
                            if (signTabs.size() > 0) {
                                rlSuccess.setVisibility(View.VISIBLE);
                                rlFail.setVisibility(View.GONE);
                                for (SignTypeApi.Bean.ListDTO bean : signTabs) {
                                    TabLayout.Tab tab = firstTab.newTab();
                                    View tabView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_service_tab, null);
                                    TextView tabText = tabView.findViewById(R.id.item_tv_tab);
                                    tabText.setText(bean.getDataTypeName());
                                    tab.setCustomView(tabView);
                                    firstTab.addTab(tab);
                                }
                            } else {
                                getGuideData("noSign");
                                rlSuccess.setVisibility(View.GONE);
                            }
                        } else {
                            getGuideData("noSign");
                            rlSuccess.setVisibility(View.GONE);
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
                            rlFail.setVisibility(View.VISIBLE);
                            noBind.setText(result.getData().getSteerDesc());
                            GlideApp.with(DeviceActivity.this)
                                    .load(result.getData().getSteerImg())
                                    .into(ivQr);
                        }
                    }
                });
    }

    private void getEquipmentList(int binderId, String dataTypeCode) {
        EasyHttp.get(this)
                .api(new EquipmentListApi(dataTypeCode, binderId))
                .request(new HttpCallback<HttpData<EquipmentListApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<EquipmentListApi.Bean> result) {
                        if (result.getCode().equals("00000")) {
                            equipmentTabs.clear();
                            secondTab.removeAllTabs();
                            equipmentTabs = result.getData().getList();
                            if (equipmentTabs.size() > 0) {
                                for (EquipmentListApi.Bean.ListDTO bean : equipmentTabs) {
                                    TabLayout.Tab itemTab = secondTab.newTab();
                                    itemTab.setText(bean.getEquipmentName());
                                    secondTab.addTab(itemTab);
                                }
                            }
                        }
                    }
                });
    }

    private void getDeviceContent(String dataTypeCode, String beginDate, String endDate, int type) {//type:1代表刷新2代表加载
        EasyHttp.get(this)
                .api(new DeviceContentApi(equipmentBindId, dataTypeCode, beginDate, endDate))
                .request(new HttpCallback<HttpData<DeviceContentApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<DeviceContentApi.Bean> result) {
                        if (type == 1) {
                            contentList.clear();
//                            refreshLayout.finishRefresh();
                            if (result.getData().getList().size() == 0) {
                                noData.setVisibility(View.VISIBLE);
                            } else {
                                noData.setVisibility(View.GONE);
                                contentList = result.getData().getList();
                            }
                        } else {
//                            refreshLayout.finishLoadMore();
                            if (result.getData().getList().size() > 0) {
                                contentList.addAll(result.getData().getList());
                            }
                        }
                        adapter.setList(contentList);
                        if (contentList.size() > 0) {
                            noDataLine.setVisibility(View.GONE);
                            lineChart.setVisibility(View.VISIBLE);
                            if (dataTypeCode.equals("1")) {
                                showXYLineChart(contentList);
                            } else {
                                showXTLineChart(contentList);
                            }
                        } else {
                            noDataLine.setVisibility(View.VISIBLE);
                            lineChart.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void showXYLineChart(List<DeviceContentApi.Bean.ListDTO> contentList) {
        LineChartManager lineChartManager = new LineChartManager(lineChart);
        //设置x轴的数据
        ArrayList<String> xValues = new ArrayList<>();
        for (DeviceContentApi.Bean.ListDTO bean : contentList) {
            xValues.add(bean.getStartTime());
        }
        List<Entry> entries1 = new ArrayList<Entry>();
        List<Entry> entries2 = new ArrayList<Entry>();
        for (int i = 0; i < contentList.size(); i++) {
            entries1.add(new Entry(i, Float.parseFloat(contentList.get(i).getSbp())));
            entries2.add(new Entry(i, Float.parseFloat(contentList.get(i).getDbp())));
        }
        List<String> lableNameList = new ArrayList<>();
        lableNameList.add("收缩压");
        lableNameList.add("舒张压");
        List<Integer> colorList = new ArrayList<>();
        colorList.add(getResources().getColor(R.color.colorBule));
        colorList.add(getResources().getColor(R.color.green));
//        lineChartManager.showLineChart(xValues, yValues, lableNameList, colorList);
        lineChartManager.setDescription("");
        lineChartManager.setLine(xValues, entries1, entries2, lableNameList, colorList);
    }

    private void showXTLineChart(List<DeviceContentApi.Bean.ListDTO> contentList) {
        LineChartManager lineChartManager = new LineChartManager(lineChart);
        //设置x轴的数据
        ArrayList<String> xValues = new ArrayList<>();
        for (DeviceContentApi.Bean.ListDTO bean : contentList) {
            xValues.add(bean.getStartTime());
        }
        List<Entry> entries1 = new ArrayList<Entry>();
        for (int i = 0; i < contentList.size(); i++) {
            entries1.add(new Entry(i, Float.parseFloat(contentList.get(i).getGls())));
        }
        List<String> lableNameList = new ArrayList<>();
        lableNameList.add("血糖");
        List<Integer> colorList = new ArrayList<>();
        colorList.add(getResources().getColor(R.color.colorBule));
        lineChartManager.setDescription("");
        lineChartManager.setSingleLine(xValues, entries1, lableNameList, colorList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
    }

    @Override
    public void onClick(View v) {

    }
}
