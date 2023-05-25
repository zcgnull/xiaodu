package com.dataport.wellness.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataport.wellness.R;
import com.dataport.wellness.adapter.ServiceContentAdapter;
import com.dataport.wellness.adapter.ServiceTabAdapter;
import com.dataport.wellness.api.QueryCommodityApi;
import com.dataport.wellness.api.ServiceTabApi;
import com.dataport.wellness.http.HttpData;
import com.google.android.material.tabs.TabLayout;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.sunfusheng.marqueeview.MarqueeView;

import java.util.ArrayList;
import java.util.List;

public class ServiceOrderActivity extends BaseActivity {

    private RelativeLayout noData;
    private RefreshLayout refreshLayout;
    private RecyclerView contentRv;
    private TabLayout firstTab;
    private TabLayout secondTab;
    private MarqueeView marqueeView;

    private ServiceContentAdapter contentAdapter;

    private List<ServiceTabApi.Bean> tabList = new ArrayList<>();
    private List<ServiceTabApi.Bean.ChildDTO> secondTabList = new ArrayList<>();
    private List<QueryCommodityApi.Bean.ListDTO> serviceList = new ArrayList<>();
    private int pageNum = 1;
    private int pageSize = 10;
    private String serviceId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_order);
        findViewById(R.id.ln_back).setOnClickListener(v -> finish());

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
                    secondTab.setVisibility(View.GONE);
                    serviceId = "";
                    pageNum = 1;
                    queryCommodity(serviceId, 1);
                } else {
                    secondTab.removeAllTabs();
                    secondTab.setVisibility(View.VISIBLE);
                    secondTabList = tabList.get(tab.getPosition()).getChild();
                    for (ServiceTabApi.Bean.ChildDTO bean : secondTabList) {
                        TabLayout.Tab itemTab = secondTab.newTab();
                        itemTab.setText(bean.getName());
                        secondTab.addTab(itemTab);
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

        secondTab = findViewById(R.id.second_tab);
        secondTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pageNum = 1;
                serviceId = secondTabList.get(tab.getPosition()).getId();
                queryCommodity(serviceId, 1);
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
        queryTab();
    }

    private void queryTab() {
        EasyHttp.get(this)
                .api(new ServiceTabApi(""))
                .request(new HttpCallback<List<ServiceTabApi.Bean>>(this) {

                    @Override
                    public void onSucceed(List<ServiceTabApi.Bean> result) {
                        tabList = result;
                        ServiceTabApi.Bean data = new ServiceTabApi.Bean("0", "全部", null);
                        tabList.add(0, data);
                        for (ServiceTabApi.Bean bean : tabList) {
                            TabLayout.Tab tab = firstTab.newTab();
                            View tabView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_service_tab, null);
                            TextView tabText = tabView.findViewById(R.id.item_tv_tab);
                            tabText.setText(bean.getName());
                            tab.setCustomView(tabView);
                            firstTab.addTab(tab);
                        }
                    }
                });
    }

    private void queryCommodity(String id, int type) {//type:1代表刷新2代表加载
        EasyHttp.get(this)
                .api(new QueryCommodityApi(id, "ASC", "39.93545", "119.59964", pageNum, pageSize))
                .request(new HttpCallback<QueryCommodityApi.Bean>(this) {

                    @Override
                    public void onSucceed(QueryCommodityApi.Bean result) {
                        if (type == 1) {
                            serviceList.clear();
                            refreshLayout.finishRefresh();
                            if (result.getList().size() == 0) {
                                noData.setVisibility(View.VISIBLE);
                            } else {
                                noData.setVisibility(View.GONE);
                                serviceList = result.getList();
                            }
                        } else {
                            refreshLayout.finishLoadMore();
                            if (result.getList().size() == 0) {
//                                Toast.makeText(ServiceOrderActivity.this, "暂无更多数据", Toast.LENGTH_SHORT).show();
                            } else {
                                serviceList.addAll(result.getList());
                            }
                        }
                        contentAdapter.setList(serviceList);
                    }
                });
    }

}
