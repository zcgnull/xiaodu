package com.dataport.wellness.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.duer.bot.BotMessageProtocol;
import com.baidu.duer.botsdk.BotIntent;
import com.baidu.duer.botsdk.BotSdk;
import com.baidu.duer.botsdk.UiContextPayload;
import com.dataport.wellness.R;
import com.dataport.wellness.adapter.ServiceContentAdapter;
import com.dataport.wellness.api.old.QueryCommodityApi;
import com.dataport.wellness.api.old.ServiceTabApi;
import com.dataport.wellness.api.old.ServiceTabBean;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.http.HttpIntData;
import com.dataport.wellness.utils.AuthenticationUtil;
import com.dataport.wellness.utils.BotConstants;
import com.dataport.wellness.utils.GlobalConstants;
import com.dataport.wellness.utils.SlotsUtil;
import com.google.android.material.tabs.TabLayout;
import com.hjq.http.EasyHttp;
import com.hjq.http.body.JsonBody;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.http.request.HttpRequest;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.sunfusheng.marqueeview.MarqueeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceOrderActivity extends BaseActivity implements IBotIntentCallback {

    private static final String TAG = "ServiceOrderActivity";
    private RelativeLayout noData;
    private RefreshLayout refreshLayout;
    private RecyclerView contentRv;
    private TabLayout firstTab;
    private TabLayout secondTab;
    private MarqueeView marqueeView;

    private ServiceContentAdapter contentAdapter;

    private List<ServiceTabBean> tabList = new ArrayList<>();
    private List<ServiceTabBean.ChildDTO> secondTabList = new ArrayList<>();
    private List<QueryCommodityApi.Bean.ListDTO> serviceList = new ArrayList<>();
    private int totalNum = 0;
    private int pageNum = 1;
    private int pageSize = 9;

    private boolean lastPage = false;
    private String serviceId = "";
    private String longitude;
    private String latitude;

    private int position = 1;
    private LinearSmoothScroller smoothScroller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_order);
        findViewById(R.id.ln_back).setOnClickListener(v -> finish());
        Intent getIntent = getIntent();
        if (null != getIntent.getStringExtra("location")) {
            String[] location = getIntent.getStringExtra("location").split(",");
            longitude = location[0];
            latitude = location[1];
            Log.d(TAG, "onCreate: " + longitude + "," + latitude);
        }
        noData = findViewById(R.id.rl_no_data);
        marqueeView = findViewById(R.id.marqueeView);
        contentRv = findViewById(R.id.rv_content);
        GridLayoutManager contentManger = new GridLayoutManager(this, 3);
        contentManger.setOrientation(GridLayoutManager.VERTICAL);
        contentRv.setLayoutManager(contentManger);
        contentAdapter = new ServiceContentAdapter(this);
        contentRv.setAdapter(contentAdapter);
        contentAdapter.setListener((data, pos) -> {
            Intent intent = new Intent(getApplicationContext(), ServiceDetailActivity.class);
            intent.putExtra("productId", data.getProductId());
            intent.putExtra("providerId", data.getProviderId());
            intent.putExtra("distance", data.getDistance());
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        firstTab = findViewById(R.id.first_tab);
        firstTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabView = tab.getCustomView().findViewById(R.id.item_tv_tab);
                tabView.setBackground(getResources().getDrawable(R.drawable.service_tab_bg));
                tabView.setTextColor(getResources().getColor(R.color.colorWhite));
                if (tab.getPosition() == 0) {
                    lastPage = false;
                    secondTab.setVisibility(View.GONE);
                    serviceId = "";
                    pageNum = 1;
                    queryCommodity(serviceId, 1);
                } else {
                    secondTab.removeAllTabs();
                    secondTab.setVisibility(View.VISIBLE);
                    secondTabList = tabList.get(tab.getPosition()).getChild();
                    for (ServiceTabBean.ChildDTO bean : secondTabList) {
                        TabLayout.Tab itemTab = secondTab.newTab();
                        itemTab.setText(bean.getName());
                        secondTab.addTab(itemTab);
                    }
                }
                BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
                BotSdk.getInstance().speakRequest(tabList.get(tab.getPosition()).getName());
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
                lastPage = false;
                pageNum = 1;
                serviceId = secondTabList.get(tab.getPosition()).getId();
                queryCommodity(serviceId, 1);
                BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
                BotSdk.getInstance().speakRequest(secondTabList.get(tab.getPosition()).getName());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            lastPage = false;
            pageNum = 1;
            queryCommodity(serviceId, 1);
        });
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            pageNum += 1;
            queryCommodity(serviceId, 2);
        });

        List<String> messages = new ArrayList<>();
        messages.add("请试试对我说：“小度小度，打开第1个”");
        marqueeView.startWithList(messages);
        marqueeView.setOnItemClickListener((position, textView) -> {
            BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
            BotSdk.getInstance().speakRequest(messages.get(position));
        });
        queryTab();

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
    }

    @Override
    public void handleIntent(BotIntent intent, String customData) {
        Intent activityIntent;
        // name意图标识 slots插槽
        String intentResult = getString(R.string.result_intent) + intent.name + ",slots:" + intent.slots;
        Log.d(TAG, "handleIntent: " + intentResult);
        if ("app_list_select_item".equals(intent.name)) {
            int value = SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number");
            if (value > serviceList.size() && value <= totalNum){
                BotSdk.getInstance().speakRequest("本页没有此项目，请翻页看看");
            } else {
                if (value > totalNum){
                    BotSdk.getInstance().speakRequest("抱歉，没有第" + value + "个项目");
                } else {
                    activityIntent = new Intent(getApplicationContext(), ServiceDetailActivity.class);
                    activityIntent.putExtra("productId", serviceList.get(SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number") - 1).getProductId());
                    activityIntent.putExtra("providerId", serviceList.get(SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number") - 1).getProviderId());
                    activityIntent.putExtra("distance", serviceList.get(SlotsUtil.getIntValue(intent.slots, "app_list_select_item_number") - 1).getDistance());
                    activityIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(activityIntent);
                }
            }
        } else {
//            BotSdk.getInstance().speak("我没有听清，请再说一遍",true);
            BotSdk.getInstance().speakRequest("我没有听清，请再说一遍");
        }
    }


    @Override
    public void onHandleScreenNavigatorEvent(int event) {

    }

    private void queryTab() {
        String sign = "";
        JSONObject requestJson;
        Map<String, Object> mapJson = new HashMap<>();
        mapJson.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        mapJson.put("appId", BotConstants.YL_APPID);
        mapJson.put("signType", "MD5");
        mapJson.put("nonceStr", AuthenticationUtil.getRandomCode());
        JSONObject json = new JSONObject(mapJson);
        try {
            sign = AuthenticationUtil.generateSignature(json,
                    BotConstants.YL_KEY, GlobalConstants.SignType.MD5);
            mapJson.put("sign", sign);
            requestJson = new JSONObject(mapJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        EasyHttp.post(this)
                .api(new ServiceTabApi())
                .body(new JsonBody(JSON.toJSONString(requestJson)))
                .request(new HttpCallback<HttpIntData<List<ServiceTabBean>>>(this) {

                    @Override
                    public void onSucceed(HttpIntData<List<ServiceTabBean>> result) {
                        tabList = result.getData();
                        ServiceTabBean data = new ServiceTabBean("0", "全部", null);
                        tabList.add(0, data);
                        for (ServiceTabBean bean : tabList) {
                            TabLayout.Tab tab = firstTab.newTab();
                            View tabView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_service_tab, null);
                            TextView tabText = tabView.findViewById(R.id.item_tv_tab);
                            tabText.setText(bean.getName());
                            tab.setCustomView(tabView);
                            firstTab.addTab(tab);
                        }
                        initClientContext(); //注册本地意图
                    }
                });


    }

    private void queryCommodity(String id, int type) {//type:1代表刷新2代表加载
        if(lastPage){
            BotSdk.getInstance().speakRequest("没有更多数据了");
            refreshLayout.finishLoadMore();
            return;
        }
        String sign = "";
        JSONObject requestJson;
        Map<String, Object> mapJson = new HashMap<>();
        mapJson.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        mapJson.put("appId", BotConstants.YL_APPID);
        mapJson.put("signType", "MD5");
        mapJson.put("nonceStr", AuthenticationUtil.getRandomCode());
        mapJson.put("value", id);
        mapJson.put("sort", "ASC");
        mapJson.put("latitude", latitude);
        mapJson.put("longitude", longitude);
        mapJson.put("pageNo", pageNum);
        mapJson.put("pageSize", pageSize);
        JSONObject json = new JSONObject(mapJson);
        try {
            sign = AuthenticationUtil.generateSignature(json,
                    BotConstants.YL_KEY, GlobalConstants.SignType.MD5);
            mapJson.put("sign", sign);
            requestJson = new JSONObject(mapJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        EasyHttp.post(this)
                .api(new QueryCommodityApi())
                .body(new JsonBody(JSON.toJSONString(requestJson)))
                .request(new HttpCallback<HttpIntData<QueryCommodityApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpIntData<QueryCommodityApi.Bean> result) {
                        Log.d("zcg", "asd");
                        lastPage = result.getData().isLastPage();
                        if (type == 1) {
                            totalNum = result.getData().getTotalCount();
                            serviceList.clear();
                            refreshLayout.finishRefresh();
                            if (result.getData().getList().size() == 0) {
                                noData.setVisibility(View.VISIBLE);
                            } else {
                                noData.setVisibility(View.GONE);
                                serviceList.addAll(result.getData().getList());
                            }
                        } else {
                            refreshLayout.finishLoadMore();
                            if (result.getData().getList().size() == 0) {
//                                Toast.makeText(ServiceOrderActivity.this, "暂无更多数据", Toast.LENGTH_SHORT).show();
                            } else {
                                serviceList.addAll(result.getData().getList());

                            }
                        }
                        contentAdapter.setList(serviceList);
                    }
                });
    }

    /**
     * 判度RecyclerView是否滑动到了底部
     * @param recyclerView
     * @param percent 百分比
     * @return
     */
    public boolean isSlideToBottom(RecyclerView recyclerView, float percent){
        if (recyclerView == null) return false;
        if ((recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()) * percent >= recyclerView.computeVerticalScrollRange()){
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
        }
    }

    /**
     * 下一页
     * @param itemNum 每页标签数
     */
    private void pageDown(int itemNum){
        if (isSlideToBottom(contentRv, 1.1f)){
            //加载更多
            refreshLayout.autoLoadMore();
        } else {
            position+=itemNum;
            if(position > serviceList.size()){
                position = serviceList.size();
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

        //切换tab
        HashMap<String, String> params = null;
        for (ServiceTabBean serviceTabBean : tabList) {
            params = new HashMap<>();
            params.put("name", serviceTabBean.getName());
            payload.addHyperUtterance(
                    BotConstants.VOICE_TAB_URL,
                    null,
                    BotConstants.UiControlType.TAB,
                    params);
        }

        BotSdk.getInstance().updateUiContext(payload);
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
                pageDown(6);
            } else if ("-1".equals(paramMap.get("by"))){
                //上一页
                pageUp(6);
            }
        } else if (BotConstants.VOICE_TAB_URL.equals(url)){
            firstTab.selectTab(firstTab.getTabAt(Integer.parseInt(paramMap.get("index_da")) - 1));
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabList = null;
        secondTabList = null;
        serviceList = null;
    }
}
