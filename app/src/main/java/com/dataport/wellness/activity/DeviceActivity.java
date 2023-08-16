package com.dataport.wellness.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.duer.bot.BotMessageProtocol;
import com.baidu.duer.botsdk.BotSdk;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dataport.wellness.R;
import com.dataport.wellness.activity.dialog.BaseDialog;
import com.dataport.wellness.activity.dialog.InterpretationDialog;
import com.dataport.wellness.activity.dialog.ReferenceDialog;
import com.dataport.wellness.adapter.DeviceContentAdapter;
import com.dataport.wellness.api.health.DeviceContentApi;
import com.dataport.wellness.api.health.DeviceContentNewApi;
import com.dataport.wellness.api.health.DeviceContentPageApi;
import com.dataport.wellness.api.health.DeviceContentPageNewApi;
import com.dataport.wellness.api.health.DeviceEnvCountApi;
import com.dataport.wellness.api.health.EquipmentListApi;
import com.dataport.wellness.api.health.IndicatorInterpretationApi;
import com.dataport.wellness.api.health.SignTypeApi;
import com.dataport.wellness.api.smalldu.GuideDataApi;
import com.dataport.wellness.http.HttpData;
import com.dataport.wellness.http.HttpInfo;
import com.dataport.wellness.http.HttpIntData;
import com.dataport.wellness.http.glide.GlideApp;
import com.dataport.wellness.utils.BotConstants;
import com.dataport.wellness.utils.LineChartManager;
import com.dataport.wellness.utils.RichTextUtil;
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
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class DeviceActivity extends BaseActivity implements View.OnClickListener {

    private TabLayout firstTab, thirdTab;
    //private TabLayout secondTab;
    private RefreshLayout refreshLayout;
    private RecyclerView contentRv;
    private RelativeLayout noData, noDataLine, rlSuccess, rlFail;
    private LineChart lineChart;
    private TextView qs, jl;
    private TextView noBind;
    private LinearLayout ln_env_device;
    //    private LinearLayout ln_show;
//
//    private LinearLayout ln_show_left;
//
//    private LinearLayout ln_show_right;
    private LinearLayout ln_env_device1;
    private ImageView ivQr;

    private List<String> dateTabs = new ArrayList<>();
    private List<SignTypeApi.Bean.ListDTO> signTabs = new ArrayList<>();
    //    private List<EquipmentListApi.Bean.ListDTO> equipmentTabs = new ArrayList<>();
    private List<DeviceContentApi.Bean.ListDTO> contentList = new ArrayList<>();
    private List<DeviceContentPageApi.Bean.RecordListDTO> rightList = new ArrayList<>();
    private long binderId;
    private Long userId;
    private int equipmentBindId = 0;
    private String dataTypeCode = "1";
    private int pageNum = 0;
    private int pageSize = 10;
    private DeviceContentAdapter adapter;
    private LineChartManager lineChartManager;
    //    private View arteriosclerosisView;
    private InterpretationDialog.Builder interpretationDialog;
    private ReferenceDialog.Builder referenceDialog;
    //    private boolean asiFirst = true;
    private ImageView iv_more;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        findViewById(R.id.ln_back).setOnClickListener(v -> finish());
        findViewById(R.id.ln_back1).setOnClickListener(v -> finish());
        rlSuccess = findViewById(R.id.rl_success);
        rlFail = findViewById(R.id.rl_fail);
        ln_env_device = findViewById(R.id.ln_env_device);
        ln_env_device1 = findViewById(R.id.ln_env_device1);
//        ln_show=findViewById(R.id.ll_show);
//        ln_show_left=findViewById(R.id.ll_show_left);
//        ln_show_right=findViewById(R.id.ll_show_right);
//        LayoutInflater layoutInflater=LayoutInflater.from(this);
//        arteriosclerosisView = layoutInflater.inflate(R.layout.item_arteriosclerosis, null);

        noBind = findViewById(R.id.tv_nobind);
        ivQr = findViewById(R.id.iv_qr);
        Intent intent = getIntent();
        binderId = intent.getLongExtra("binderId", 0);
        userId = intent.getLongExtra("userId", 0);
        noData = findViewById(R.id.rl_no_data);
        noDataLine = findViewById(R.id.rl_no_data_line);
        lineChart = findViewById(R.id.line_chart);
        qs = findViewById(R.id.tv_qs);
        jl = findViewById(R.id.tv_jl);
        iv_more = findViewById(R.id.iv_more);
        iv_more.setOnClickListener(v -> {
            String type = "";
            String title = "";
            switch (dataTypeCode) {
                case "1":
                    type = "blp";
                    title = "血压";
                    break;
                case "2":
                    type = "bls";
                    title = "血糖";
                    break;
                case "3":
                    type = "blk";
                    title = "血酮";
                    break;
                case "4":
                    type = "uric";
                    title = "尿酸";
                    break;
                case "5":
                    type = "blo";
                    title = "血氧";
                    break;
                case "6":
                    type = "heart";
                    title = "心率";
                    break;
                case "7":
                    type = "asi";
                    title = "动脉硬化";
                    break;
                default:
                    break;
            }
            showReferenceDialog(v.getContext(), "", type, "0", "0", title);
        });

        firstTab = findViewById(R.id.first_tab);
        //环境监测
        ln_env_device.setOnClickListener(v -> {
            Intent deviceEnvIntent = new Intent(DeviceActivity.this, DeviceEnvActivity.class);
            deviceEnvIntent.putExtra("userId", userId);
            deviceEnvIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(deviceEnvIntent);
        });
        ln_env_device1.setOnClickListener(v -> {
            Intent deviceEnvIntent = new Intent(DeviceActivity.this, DeviceEnvActivity.class);
            deviceEnvIntent.putExtra("userId", userId);
            deviceEnvIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(deviceEnvIntent);
        });
        firstTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabView = tab.getCustomView().findViewById(R.id.item_tv_tab);
                tabView.setBackground(getResources().getDrawable(R.drawable.service_tab_bg));
                tabView.setTextColor(getResources().getColor(R.color.colorWhite));
                dataTypeCode = signTabs.get(tab.getPosition()).getDataTypeCode();
                pageNum = 0;
                //getEquipmentList(binderId, dataTypeCode);
//                if (ln_show.getChildCount() >= 3){
//                    ln_show_left.setVisibility(View.VISIBLE);
//                    ln_show_right.setVisibility(View.VISIBLE);
//                    secondTab.setVisibility(View.VISIBLE);
//                    BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
//                    ln_show.removeView(arteriosclerosisView);
//                }
//                asiFirst = true;
                switch (dataTypeCode) {
                    case "1":
                        qs.setText("血压趋势");
                        jl.setText("血压记录");
                        break;
                    case "2":
                        qs.setText("血糖趋势");
                        jl.setText("血糖记录");
                        break;
                    case "3":
                        qs.setText("血酮趋势");
                        jl.setText("血酮记录");
                        break;
                    case "4":
                        qs.setText("尿酸趋势");
                        jl.setText("尿酸记录");
                        break;
                    case "5":
                        qs.setText("血氧趋势");
                        jl.setText("血氧记录");
                        break;
                    case "6":
                        qs.setText("心率趋势");
                        jl.setText("心率记录");
                        break;
                    case "7":
                        qs.setText("动脉硬化趋势");
                        jl.setText("动脉硬化记录");
                        break;
                    default:
                        qs.setText("无");
                        jl.setText("无");
                        break;
                }
                if (null != thirdTab.getTabAt(0)) {
                    if (thirdTab.getTabAt(0).isSelected()) {
                        getDeviceContent(dataTypeCode, TimeUtil.getInstance().getTimeOneMonth(), TimeUtil.getInstance().getCurrentTime(), 3);
//                        getDeviceContentPage(dataTypeCode, TimeUtil.getInstance().getTimeOneMonth(), TimeUtil.getInstance().getCurrentTime(), 1);
                    } else {
                        thirdTab.getTabAt(0).select();
                    }
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

//        secondTab = findViewById(R.id.second_tab);
//        secondTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                equipmentBindId = equipmentTabs.get(tab.getPosition()).getEquipmentBindId();
//                if (thirdTab.getTabAt(0).isSelected()) {
//                    getDeviceContent(dataTypeCode, TimeUtil.getInstance().getTimeOneMonth(), TimeUtil.getInstance().getCurrentTime(), 1);
//                    getDeviceContentPage(dataTypeCode, TimeUtil.getInstance().getTimeOneMonth(), TimeUtil.getInstance().getCurrentTime(), 1);
//                } else {
//                    thirdTab.getTabAt(0).select();
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        thirdTab = findViewById(R.id.thire_tab);
        thirdTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabView = tab.getCustomView().findViewById(R.id.item_date_tab);
                tabView.setBackground(getResources().getDrawable(R.drawable.service_tab_bg));
                tabView.setTextColor(getResources().getColor(R.color.colorWhite));
                if (tab.getPosition() == 1) {
                    getDeviceContent(dataTypeCode, TimeUtil.getInstance().getTimeAWeek(), TimeUtil.getInstance().getCurrentTime(), 1);
//                    getDeviceContentPage(dataTypeCode, TimeUtil.getInstance().getTimeAWeek(), TimeUtil.getInstance().getCurrentTime(), 1);
                } else if (tab.getPosition() == 2) {
                    getDeviceContent(dataTypeCode, TimeUtil.getInstance().getYesterdayTime(), TimeUtil.getInstance().getCurrentTime(), 2);
//                    getDeviceContentPage(dataTypeCode, TimeUtil.getInstance().getYesterdayTime(), TimeUtil.getInstance().getCurrentTime(), 1);
                } else {
                    //if (equipmentBindId != 0) {
                    getDeviceContent(dataTypeCode, TimeUtil.getInstance().getTimeOneMonth(), TimeUtil.getInstance().getCurrentTime(), 3);
//                    getDeviceContentPage(dataTypeCode, TimeUtil.getInstance().getTimeOneMonth(), TimeUtil.getInstance().getCurrentTime(), 1);
                    //}
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


        contentRv = findViewById(R.id.rv_content);
        GridLayoutManager contentManger = new GridLayoutManager(getApplicationContext(), 1);
        contentManger.setOrientation(GridLayoutManager.VERTICAL);
        contentRv.setLayoutManager(contentManger);
        adapter = new DeviceContentAdapter(getApplicationContext());
        contentRv.setAdapter(adapter);
        adapter.setListener((data, pos) -> {
            BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
            String speak = "";
            String time = data.getStartTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                Date date = sdf.parse(time);
                Calendar calender = Calendar.getInstance();
                calender.setTime(date);
                time = calender.get(Calendar.YEAR) + "年" + (calender.get(Calendar.MONTH) + 1) + "月" + calender.get(Calendar.DATE) + "日" + calender.get(Calendar.HOUR_OF_DAY) + "点" + calender.get(Calendar.MINUTE) + "分" + calender.get(Calendar.SECOND) + "秒";
            } catch (ParseException e) {
                e.printStackTrace();
            }
            switch (data.getDataType()) {
                case "1":
                    speak = "血压指标详情：" + time + "，您的血压为，高压" + data.getSbp() + "毫米汞柱，低压" + data.getDbp() + "毫米汞柱，脉率为" + data.getBpm() + "次每分钟。";
                    showInterpretationDialog(this, speak, data, "blp", data.getDbp(), data.getSbp(), "血压");
                    break;
                case "2":
                    speak = "血糖指标详情：" + time + "，您的血糖为" + data.getGls() + "毫摩尔每升。";
                    showInterpretationDialog(this, speak, data, "bls", data.getGls(), null, "血糖");
                    break;
                case "3":
                    speak = "血酮指标详情：" + time + "，您的血酮为" + data.getBlk() + "毫摩尔每升。";
                    showInterpretationDialog(this, speak, data, "blk", data.getBlk(), null, "血酮");
                    break;
                case "4":
                    speak = "尿酸指标详情：" + time + "，您的尿酸为" + data.getUric() + "毫摩尔每升。";
                    showInterpretationDialog(this, speak, data, "uric", data.getUric(), null, "尿酸");
                    break;
                case "5":
                    speak = "血氧指标详情：" + time + "，您的血氧为" + data.getBlo() + "%。";
                    showInterpretationDialog(this, speak, data, "blo", data.getBlo(), null, "血氧");
                    break;
                case "6":
                    speak = "心率指标详情：" + time + "，您的心率为" + data.getBpm() + "次每分钟。";
                    showInterpretationDialog(this, speak, data, "heart", data.getBpm(), null, "心率");
                    break;
                case "7":
                    speak = "动脉硬化指标详情：" + time +
                            "，您的动脉硬化指数为" + data.getAsi() + "。" +
                            "血管年龄：" + data.getBlvAge() + "岁。" +
                            "收缩压" + data.getSbp() + "毫米汞柱。" +
                            "舒张压" + data.getDbp() + "毫米汞柱。" +
                            "脉率" + data.getBpm() + "次每分钟。";
                    showInterpretationDialog(this, speak, data, "asi", data.getAsi(), null, "动脉硬化");
                    break;
                default:

                    break;
            }

        });

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            pageNum = 0;
            if (thirdTab.getTabAt(0).isSelected()) {
                getDeviceContentPage(dataTypeCode, TimeUtil.getInstance().getTimeOneMonth(), TimeUtil.getInstance().getCurrentTime(), 1);
            } else if (thirdTab.getTabAt(1).isSelected()) {
                getDeviceContentPage(dataTypeCode, TimeUtil.getInstance().getTimeAWeek(), TimeUtil.getInstance().getCurrentTime(), 1);
            } else {
                getDeviceContentPage(dataTypeCode, TimeUtil.getInstance().getYesterdayTime(), TimeUtil.getInstance().getCurrentTime(), 1);
            }
        });
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            pageNum += 1;
            if (thirdTab.getTabAt(0).isSelected()) {
                getDeviceContentPage(dataTypeCode, TimeUtil.getInstance().getTimeOneMonth(), TimeUtil.getInstance().getCurrentTime(), 2);
            } else if (thirdTab.getTabAt(1).isSelected()) {
                getDeviceContentPage(dataTypeCode, TimeUtil.getInstance().getTimeAWeek(), TimeUtil.getInstance().getCurrentTime(), 2);
            } else {
                getDeviceContentPage(dataTypeCode, TimeUtil.getInstance().getYesterdayTime(), TimeUtil.getInstance().getCurrentTime(), 2);
            }
        });

        getSignType(binderId);
        // TODO: 2023/8/8
        //showArteriosclerosis ();
        //showInterpretationDialog();
    }

    /**
     * 绘制角标
     *
     * @param view
     */
    private void drawBadge(View view, Integer count) {
        QBadgeView qBadgeView = new QBadgeView(getApplicationContext());
        qBadgeView.setBadgeBackgroundColor(Color.RED);
        qBadgeView.bindTarget(view);
        qBadgeView.setBadgeNumber(count);
        qBadgeView.setBadgeGravity(Gravity.END | Gravity.TOP);
        qBadgeView.setGravityOffset(100, 0, true);
        qBadgeView.setBadgeTextSize(18, true);
        qBadgeView.setBadgePadding(5, true);
        qBadgeView.setOnDragStateChangedListener((dragState, badge, targetView) -> {
            if (Badge.OnDragStateChangedListener.STATE_SUCCEED == dragState) {
                badge.hide(true);
            }
        });
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
                            drawBadge(rlSuccess, result.getData().getNum());
                            drawBadge(rlFail, result.getData().getNum());
                        }

                    }
                });
    }

    private void getSignType(long binderId) {
        EasyHttp.get(DeviceActivity.this)

                .api(new SignTypeApi(binderId))
                .request(new HttpCallback<HttpData<SignTypeApi.Bean>>(DeviceActivity.this) {

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
                                    .skipMemoryCache(true)//禁用内存缓存功能
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不缓存任何内容
                                    .into(ivQr);
                        }
                    }
                });
    }

//    private void getEquipmentList(long binderId, String dataTypeCode) {
//        EasyHttp.get(this)
//                .api(new EquipmentListApi(dataTypeCode, binderId))
//                .request(new HttpCallback<HttpData<EquipmentListApi.Bean>>(this) {
//
//                    @Override
//                    public void onSucceed(HttpData<EquipmentListApi.Bean> result) {
//                        if (result.getCode().equals("00000")) {
//                            equipmentTabs.clear();
//                            secondTab.removeAllTabs();
//                            equipmentTabs = result.getData().getList();
//                            if (equipmentTabs.size() > 0) {
//                                for (EquipmentListApi.Bean.ListDTO bean : equipmentTabs) {
//                                    TabLayout.Tab itemTab = secondTab.newTab();
//                                    itemTab.setText(bean.getEquipmentName());
//                                    secondTab.addTab(itemTab);
//                                }
//                            }
//                        }
//                    }
//                });
//    }

    private void getDeviceContent(String dataTypeCode, String beginDate, String endDate, int type) {
        EasyHttp.get(this)
                .api(new DeviceContentNewApi(binderId, dataTypeCode, beginDate, endDate))
                .request(new HttpCallback<HttpData<DeviceContentApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpData<DeviceContentApi.Bean> result) {
                        contentList.clear();
                        contentList = result.getData().getList();
                        if (contentList.size() > 0) {
                            noDataLine.setVisibility(View.GONE);
                            lineChart.setVisibility(View.VISIBLE);
                            switch (dataTypeCode) {
                                case "1":
                                    showXYLineChart(contentList);
                                    break;
                                case "2":
                                    showXTLineChart(contentList);
                                    break;
                                case "3":
                                    showXTongLineChart(contentList);
                                    break;
                                case "4":
                                    showNSLineChart(contentList);
                                    break;
                                case "5":
                                    showXYangLineChart(contentList);
                                    break;
                                case "6":
                                    showXLLineChart(contentList);
                                    break;
                                case "7":
                                    showDMYHLineChart(contentList);
                                    break;
                                default:

                                    break;
                            }

                        } else {
                            noDataLine.setVisibility(View.VISIBLE);
                            lineChart.setVisibility(View.GONE);
                        }
                        if (type == 1){
                            getDeviceContentPage(dataTypeCode, TimeUtil.getInstance().getTimeAWeek(), TimeUtil.getInstance().getCurrentTime(), 1);
                        } else if (type == 2){
                            getDeviceContentPage(dataTypeCode, TimeUtil.getInstance().getYesterdayTime(), TimeUtil.getInstance().getCurrentTime(), 1);
                        } else {
                            getDeviceContentPage(dataTypeCode, TimeUtil.getInstance().getTimeOneMonth(), TimeUtil.getInstance().getCurrentTime(), 1);
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDeviceEnvCount(userId);
    }

    private void getDeviceContentPage(String dataTypeCode, String beginDate, String endDate, int type) {//type:1代表刷新2代表加载
        EasyHttp.get(DeviceActivity.this)
                .api(new DeviceContentPageNewApi(binderId, dataTypeCode, beginDate, endDate, pageNum, pageSize))
                .request(new HttpCallback<HttpData<DeviceContentPageApi.Bean>>(DeviceActivity.this) {

                    @Override
                    public void onSucceed(HttpData<DeviceContentPageApi.Bean> result) {
                        if (result.getCode().equals("00000")) {
                            if (type == 1) {
                                rightList.clear();
                                refreshLayout.finishRefresh();
                                if (result.getData().getRecordList().size() == 0) {
                                    noData.setVisibility(View.VISIBLE);
                                } else {
                                    noData.setVisibility(View.GONE);
                                    rightList.addAll(result.getData().getRecordList());
                                }
                            } else {
                                refreshLayout.finishLoadMore();
                                if (result.getData().getRecordList().size() > 0) {
                                    rightList.addAll(result.getData().getRecordList());
                                }
                            }
                            adapter.setList(rightList);
//                            if ("7".equals(dataTypeCode) && rightList.size() > 0 && asiFirst){
//                                showArteriosclerosis(rightList.get(0));
//                                asiFirst = false;
//                            }
                        }

                    }
                });
    }

    private void showXYLineChart(List<DeviceContentApi.Bean.ListDTO> contentList) {
        lineChartManager = null;
        lineChartManager = new LineChartManager(lineChart);
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
        lineChartManager = null;
        lineChartManager = new LineChartManager(lineChart);
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

    private void showXTongLineChart(List<DeviceContentApi.Bean.ListDTO> contentList) {
        lineChartManager = null;
        lineChartManager = new LineChartManager(lineChart);
        //设置x轴的数据
        ArrayList<String> xValues = new ArrayList<>();
        for (DeviceContentApi.Bean.ListDTO bean : contentList) {
            xValues.add(bean.getStartTime());
        }
        List<Entry> entries1 = new ArrayList<Entry>();
        for (int i = 0; i < contentList.size(); i++) {
            entries1.add(new Entry(i, Float.parseFloat(contentList.get(i).getBlk())));
        }
        List<String> lableNameList = new ArrayList<>();
        lableNameList.add("血酮");
        List<Integer> colorList = new ArrayList<>();
        colorList.add(getResources().getColor(R.color.colorBule));
        lineChartManager.setDescription("");
        lineChartManager.setSingleLine(xValues, entries1, lableNameList, colorList);
    }

    private void showNSLineChart(List<DeviceContentApi.Bean.ListDTO> contentList) {
        lineChartManager = null;
        lineChartManager = new LineChartManager(lineChart);
        //设置x轴的数据
        ArrayList<String> xValues = new ArrayList<>();
        for (DeviceContentApi.Bean.ListDTO bean : contentList) {
            xValues.add(bean.getStartTime());
        }
        List<Entry> entries1 = new ArrayList<Entry>();
        for (int i = 0; i < contentList.size(); i++) {
            entries1.add(new Entry(i, Float.parseFloat(contentList.get(i).getUric())));
        }
        List<String> lableNameList = new ArrayList<>();
        lableNameList.add("尿酸");
        List<Integer> colorList = new ArrayList<>();
        colorList.add(getResources().getColor(R.color.colorBule));
        lineChartManager.setDescription("");
        lineChartManager.setSingleLine(xValues, entries1, lableNameList, colorList);
    }

    private void showXYangLineChart(List<DeviceContentApi.Bean.ListDTO> contentList) {
        lineChartManager = null;
        lineChartManager = new LineChartManager(lineChart);
        //设置x轴的数据
        ArrayList<String> xValues = new ArrayList<>();
        for (DeviceContentApi.Bean.ListDTO bean : contentList) {
            xValues.add(bean.getStartTime());
        }
        List<Entry> entries1 = new ArrayList<Entry>();
        for (int i = 0; i < contentList.size(); i++) {
            entries1.add(new Entry(i, Float.parseFloat(contentList.get(i).getBlo())));
        }
        List<String> lableNameList = new ArrayList<>();
        lableNameList.add("血氧");
        List<Integer> colorList = new ArrayList<>();
        colorList.add(getResources().getColor(R.color.colorBule));
        lineChartManager.setDescription("");
        lineChartManager.setSingleLine(xValues, entries1, lableNameList, colorList);
    }

    private void showXLLineChart(List<DeviceContentApi.Bean.ListDTO> contentList) {
        lineChartManager = null;
        lineChartManager = new LineChartManager(lineChart);
        //设置x轴的数据
        ArrayList<String> xValues = new ArrayList<>();
        for (DeviceContentApi.Bean.ListDTO bean : contentList) {
            xValues.add(bean.getStartTime());
        }
        List<Entry> entries1 = new ArrayList<Entry>();
        for (int i = 0; i < contentList.size(); i++) {
            entries1.add(new Entry(i, Float.parseFloat(contentList.get(i).getBpm())));
        }
        List<String> lableNameList = new ArrayList<>();
        lableNameList.add("心率");
        List<Integer> colorList = new ArrayList<>();
        colorList.add(getResources().getColor(R.color.colorBule));
        lineChartManager.setDescription("");
        lineChartManager.setSingleLine(xValues, entries1, lableNameList, colorList);
    }

    private void showDMYHLineChart(List<DeviceContentApi.Bean.ListDTO> contentList) {
        lineChartManager = null;
        lineChartManager = new LineChartManager(lineChart);
        //设置x轴的数据
        ArrayList<String> xValues = new ArrayList<>();
        for (DeviceContentApi.Bean.ListDTO bean : contentList) {
            xValues.add(bean.getStartTime());
        }
        List<Entry> entries1 = new ArrayList<Entry>();
        for (int i = 0; i < contentList.size(); i++) {
            entries1.add(new Entry(i, Float.parseFloat(contentList.get(i).getAsi())));
        }
        List<String> lableNameList = new ArrayList<>();
        lableNameList.add("动脉硬化");
        List<Integer> colorList = new ArrayList<>();
        colorList.add(getResources().getColor(R.color.colorBule));
        lineChartManager.setDescription("");
        lineChartManager.setSingleLine(xValues, entries1, lableNameList, colorList);
    }

    /**
     * 动脉硬化展示页
     */
    private void showArteriosclerosis(DeviceContentPageApi.Bean.RecordListDTO recordListDTO) {
//        ln_show_left.setVisibility(View.GONE);
//        ln_show_right.setVisibility(View.GONE);
//        //secondTab.setVisibility(View.INVISIBLE);
//        String speak = "";
//        TextView tv_asi = arteriosclerosisView.findViewById(R.id.tv_asi);
//        TextView tv_vascularage = arteriosclerosisView.findViewById(R.id.tv_vascularage);
//        TextView tv_sbp = arteriosclerosisView.findViewById(R.id.tv_sbp);
//        TextView tv_dbp = arteriosclerosisView.findViewById(R.id.tv_dbp);
//        TextView tv_bpm = arteriosclerosisView.findViewById(R.id.tv_bpm);
//        TextView tv_more = arteriosclerosisView.findViewById(R.id.tv_more);
//        tv_asi.setText(recordListDTO.getAsi());
//        tv_vascularage.setText(recordListDTO.getBlvAge());
//        tv_sbp.setText(recordListDTO.getSbp());
//        tv_dbp.setText(recordListDTO.getDbp());
//        tv_bpm.setText(recordListDTO.getBpm());
//        showIcon(recordListDTO.getAsiType(), arteriosclerosisView.findViewById(R.id.iv_asi));
//        showIcon(recordListDTO.getSbpType(), arteriosclerosisView.findViewById(R.id.iv_sbp));
//        showIcon(recordListDTO.getDbpType(), arteriosclerosisView.findViewById(R.id.iv_dbp));
//        showIcon(recordListDTO.getBpmType(), arteriosclerosisView.findViewById(R.id.iv_bpm));
//
//        tv_more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ln_show_left.setVisibility(View.VISIBLE);
//                ln_show_right.setVisibility(View.VISIBLE);
//                //secondTab.setVisibility(View.VISIBLE);
//                BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
//                ln_show.removeView(arteriosclerosisView);
//            }
//        });
//        int asi = Integer.parseInt(recordListDTO.getAsi());
//        speak = "您的ASI动脉硬化指数为" + asi;
//
//        if (asi <= 70){
//            speak += ",评定结果：正常，干预方案：日常检测，健康的生活方式。";
//        } else if (asi <= 80){
//            speak += ",评定结果：正常高值，干预方案：加强监管，健康的生活方式。";
//        } else if (asi <= 160) {
//            speak += ",评定结果：轻度到中度，干预方案：明确病因，日常检查，对症治疗。";
//        } else {
//            speak += ",评定结果：中度到重度，干预方案：明确病因，日常检查，对症治疗。";
//        }
//
//        speak += "血管年龄：" +
//                recordListDTO.getBlvAge() +
//                "岁。" +
//                "收缩压" +
//                recordListDTO.getSbp() +
//                "毫米汞柱。" +
//                "舒张压" +
//                recordListDTO.getDbp() +
//                "毫米汞柱。" +
//                "脉率" +
//                recordListDTO.getBpm() +
//                "次每分钟。";
//
//        BotSdk.getInstance().speakRequest(speak);
//        ln_show.addView(arteriosclerosisView);
    }

    private void showInterpretationDialog(Context context, String speak, DeviceContentPageApi.Bean.RecordListDTO list, String identifying, String value1, String value2, String title) {
        final String speakNew = speak;
        EasyHttp.get(this)
                .api(new IndicatorInterpretationApi(identifying, value1, value2, String.valueOf(binderId)))
                .request(new HttpCallback<HttpInfo<IndicatorInterpretationApi.Bean>>(this) {
                    @Override
                    public void onSucceed(HttpInfo<IndicatorInterpretationApi.Bean> result) {
                        if (result.getCode().equals("00000")) {
                            String speak = speakNew;
                            String content = RichTextUtil.getRichTextStr(result.getInfo().getContent());
                            speak += title + "指标解读：" +
                                    result.getInfo().getTips() + "\n" + content;
                            interpretationDialog = new InterpretationDialog.Builder(context);
                            interpretationDialog.setUnscramble(content)
                                    .setTitle(title)
                                    .setState(result.getInfo().getTips())
                                    .setRecord(list)
                                    .addOnCancelListener(new BaseDialog.OnCancelListener() {
                                        @Override
                                        public void onCancel(BaseDialog dialog) {
                                            BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
                                        }
                                    });
                            interpretationDialog.show();
                            BotSdk.getInstance().speakRequest(speak);
                        }
                    }
                });
    }

    private void showReferenceDialog(Context context, String speak, String identifying, String value1, String value2, String title) {
        final String speakNew = speak;
        EasyHttp.get(this)
                .api(new IndicatorInterpretationApi(identifying, value1, value2, String.valueOf(binderId)))
                .request(new HttpCallback<HttpInfo<IndicatorInterpretationApi.Bean>>(this) {
                    @Override
                    public void onSucceed(HttpInfo<IndicatorInterpretationApi.Bean> result) {
                        if (result.getCode().equals("00000")) {
                            String speak = speakNew;
                            String content = RichTextUtil.getRichTextStr(result.getInfo().getIllustrate());//详情
                            speak += title + "指标说明：" +
                                    content +
                                    title + "参考值：" +
                                    result.getInfo().getRefer();
                            referenceDialog = new ReferenceDialog.Builder(context);
                            referenceDialog.setIllustrate(content);
                            referenceDialog.setReference(result.getInfo().getRefer());
                            referenceDialog.setTitle(title);
                            referenceDialog.addOnCancelListener(new BaseDialog.OnCancelListener() {
                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
                                }
                            });
                            referenceDialog.show();
                            BotSdk.getInstance().speakRequest(speak);
                        }
                    }
                });
    }

    private void showIcon(String type, ImageView view) {
        if ("0".equals(type)) {
            view.setImageResource(R.mipmap.arrow_down_green);
        } else if ("1".equals(type)) {
            view.setImageResource(R.mipmap.arrow_up_red);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
        userId = null;
        dateTabs = null;
        adapter = null;
//        equipmentTabs = null;
        contentList = null;
        rightList = null;
        signTabs = null;
        lineChartManager = null;
        interpretationDialog = null;
        referenceDialog = null;
        firstTab.clearOnTabSelectedListeners();
        firstTab.removeAllTabs();
//        secondTab.clearOnTabSelectedListeners();
//        secondTab.removeAllTabs();
        thirdTab.clearOnTabSelectedListeners();
        thirdTab.removeAllTabs();
        EasyHttp.cancel();
    }

    @Override
    public void onClick(View v) {

    }
}
