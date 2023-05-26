package com.dataport.wellness.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dataport.wellness.R;
import com.dataport.wellness.adapter.ServicePriceAdapter;
import com.dataport.wellness.api.ImgBean;
import com.dataport.wellness.api.QueryServiceDetailApi;
import com.dataport.wellness.api.ServiceTabApi;
import com.dataport.wellness.utils.RichTextUtil;
import com.google.android.material.tabs.TabLayout;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.sunfusheng.marqueeview.MarqueeView;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.zzhoujay.richtext.CacheType;
import com.zzhoujay.richtext.RichText;

import java.util.ArrayList;
import java.util.List;

public class ServiceDetailActivity extends BaseActivity {

    private Banner banner, richBanner;
    private TabLayout tabLayout;
    private MarqueeView marqueeView;
    private TextView richText;
    private RecyclerView priceRv;
    private TextView name, price, distance, saleCount, company;
    private LinearLayout lnPrice;
    private RelativeLayout noData;

    private ServicePriceAdapter priceAdapter;
    private List<QueryServiceDetailApi.Bean.OtherItemsDTO> tabList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);
        initView();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabView = tab.getCustomView().findViewById(R.id.item_tv_tab);
                tabView.setBackground(getResources().getDrawable(R.drawable.service_tab_bg));
                tabView.setTextColor(getResources().getColor(R.color.colorWhite));
                price.setText("价格：￥" + tabList.get(tab.getPosition()).getCategoryPrice());
                saleCount.setText("已售：" + tabList.get(tab.getPosition()).getSaleCount());

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            //禁止竖向滑动 RecyclerView 为垂直状态（VERTICAL）
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        priceRv.setLayoutManager(layoutManager);
        priceAdapter = new ServicePriceAdapter(this);
        priceRv.setAdapter(priceAdapter);

        List<String> messages = new ArrayList<>();
        messages.add("请试试对我说：“小度小度,呼叫康养管家”进行代下单。");
        marqueeView.startWithList(messages);

        Intent intent = getIntent();
        distance.setText("距离：" + intent.getStringExtra("distance"));
        queryDetail(intent.getStringExtra("productId"), intent.getStringExtra("providerId"));
    }

    private void queryDetail(String productId, String providerId) {
        EasyHttp.get(this)
                .api(new QueryServiceDetailApi(productId, providerId))
                .request(new HttpCallback<QueryServiceDetailApi.Bean>(this) {

                    @Override
                    public void onSucceed(QueryServiceDetailApi.Bean result) {
                        name.setText(result.getProductName());
                        price.setText("价格：￥" + result.getPrice());
                        company.setText(result.getProviderName());
                        setBanner(result.getCarouselPicture(), banner);
//                        RichText.initCacheDir(ServiceDetailActivity.this);
//                        RichText.fromHtml(result.getProductDetail())
//                                .autoFix(false)
//                                .cache(CacheType.none)
//                                .into(richText);
                        tabList = result.getOtherItems();
                        for (QueryServiceDetailApi.Bean.OtherItemsDTO bean : tabList) {
                            TabLayout.Tab tab = tabLayout.newTab();
                            View tabView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_service_tab, null);
                            TextView tabText = tabView.findViewById(R.id.item_tv_tab);
                            tabText.setText(bean.getCategoryName());
                            tab.setCustomView(tabView);
                            tabLayout.addTab(tab);
                        }

                        if (result.getSurchargeItems().size() > 0) {//附加费
                            lnPrice.setVisibility(View.VISIBLE);
                            priceAdapter.setList(result.getSurchargeItems());
                        }
                        String content = RichTextUtil.getRichTextStr(result.getProductDetail());//详情
                        if (!content.equals("")) {
                            richText.setVisibility(View.VISIBLE);
                            richText.setText(content);
                        }
                        List<String> imgs = RichTextUtil.getRichTextImgListUtil(result.getProductDetail());//详情图片
                        if (imgs.size() > 0) {
                            richBanner.setVisibility(View.VISIBLE);
                            setBanner(imgs, richBanner);
                        }

                        if (result.getSurchargeItems().size() == 0 && content.equals("") && imgs.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void setBanner(List<String> data, Banner banner) {
        List<ImgBean> banners = new ArrayList<>();
        for (String url : data) {
            banners.add(new ImgBean(url, null, 1));
        }

        banner.setAdapter(new BannerImageAdapter<ImgBean>(banners) {
            @Override
            public void onBindView(BannerImageHolder holder, ImgBean data, int position, int size) {
                Glide.with(holder.itemView)
                        .load(data.imageUrl)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                        .into(holder.imageView);
            }
        }).addBannerLifecycleObserver(this)//添加生命周期观察者
                .setIndicator(new CircleIndicator(this));
    }

    private void initView() {
        findViewById(R.id.ln_back).setOnClickListener(v -> finish());
        banner = findViewById(R.id.banner);
        marqueeView = findViewById(R.id.marqueeView);
        richText = findViewById(R.id.rich_text);
        name = findViewById(R.id.tv_name);
        price = findViewById(R.id.tv_price);
        distance = findViewById(R.id.tv_distance);
        saleCount = findViewById(R.id.tv_saleCount);
        company = findViewById(R.id.tv_company);
        tabLayout = findViewById(R.id.detail_tab);
        priceRv = findViewById(R.id.rv_price);
        lnPrice = findViewById(R.id.ln_price);
        richBanner = findViewById(R.id.rich_banner);
        noData = findViewById(R.id.rl_no_data);
    }

}