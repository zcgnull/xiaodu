package com.dataport.wellness.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.duer.bot.BotMessageProtocol;
import com.baidu.duer.botsdk.BotSdk;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dataport.wellness.R;
import com.dataport.wellness.adapter.ServicePriceAdapter;
import com.dataport.wellness.api.old.QueryServiceDetailApi;
import com.dataport.wellness.base.ImgBean;
import com.dataport.wellness.http.HttpIntData;
import com.dataport.wellness.utils.AuthenticationUtil;
import com.dataport.wellness.utils.BotConstants;
import com.dataport.wellness.utils.GlobalConstants;
import com.dataport.wellness.utils.RichTextUtil;
import com.google.android.material.tabs.TabLayout;
import com.hjq.http.EasyHttp;
import com.hjq.http.body.JsonBody;
import com.hjq.http.listener.HttpCallback;
import com.sunfusheng.marqueeview.MarqueeView;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceDetailActivity extends BaseActivity {

    private Banner banner, richBanner;
    private TabLayout tabLayout;
    private MarqueeView marqueeView;
    private TextView richText;
    private RecyclerView priceRv;
    private TextView name, price, distance, saleCount, company;
    private LinearLayout lnPrice;
    private RelativeLayout noData;
    private String speck;

    private ServicePriceAdapter priceAdapter;
    private List<QueryServiceDetailApi.Bean.OtherItemsDTO> tabList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);
        initView();
        findViewById(R.id.ln_voice).setOnClickListener(v -> {
            BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
            BotSdk.getInstance().speakRequest(speck);
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabView = tab.getCustomView().findViewById(R.id.item_tv_tab);
                tabView.setBackground(getResources().getDrawable(R.drawable.service_tab_bg));
                tabView.setTextColor(getResources().getColor(R.color.colorWhite));
                price.setText("价格：￥" + tabList.get(tab.getPosition()).getCategoryPrice());
                saleCount.setText("已售：" + tabList.get(tab.getPosition()).getSaleCount());
                BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
                BotSdk.getInstance().speakRequest(tabList.get(tab.getPosition()).getCategoryName() + name.getText().toString() + "," + price.getText().toString());
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
        messages.add("请试试对我说：“小度小度,呼叫服务中心”进行代下单。");
        marqueeView.startWithList(messages);
        marqueeView.setOnItemClickListener((position, textView) -> {
            BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
            BotSdk.getInstance().speakRequest(messages.get(position));
        });

        Intent intent = getIntent();
        distance.setText("距离：" + intent.getStringExtra("distance") + "km");
        queryDetail(intent.getStringExtra("productId"), intent.getStringExtra("providerId"));
    }

    private void queryDetail(String productId, String providerId) {
        String sign = "";
        JSONObject requestJson;
        Map<String, Object> mapJson = new HashMap<>();
        mapJson.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        mapJson.put("appId", BotConstants.YL_APPID);
        mapJson.put("signType", "MD5");
        mapJson.put("nonceStr", AuthenticationUtil.getRandomCode());
        mapJson.put("productId", productId);
        mapJson.put("providerId", providerId);
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
                .api(new QueryServiceDetailApi())
                .body(new JsonBody(JSON.toJSONString(requestJson)))
                .request(new HttpCallback<HttpIntData<QueryServiceDetailApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpIntData<QueryServiceDetailApi.Bean> result) {
                        name.setText(result.getData().getProductName());
                        price.setText("价格：￥" + result.getData().getPrice());
                        company.setText(result.getData().getProviderName());
                        setBanner(result.getData().getCarouselPicture(), banner);
//                        RichText.initCacheDir(ServiceDetailActivity.this);
//                        RichText.fromHtml(result.getProductDetail())
//                                .autoFix(false)
//                                .cache(CacheType.none)
//                                .into(richText);
                        tabList = result.getData().getOtherItems();
                        for (QueryServiceDetailApi.Bean.OtherItemsDTO bean : tabList) {
                            TabLayout.Tab tab = tabLayout.newTab();
                            View tabView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_service_tab, null);
                            TextView tabText = tabView.findViewById(R.id.item_tv_tab);
                            tabText.setText(bean.getCategoryName());
                            tab.setCustomView(tabView);
                            tabLayout.addTab(tab);
                        }

                        if (result.getData().getSurchargeItems().size() > 0) {//附加费
                            lnPrice.setVisibility(View.VISIBLE);
                            priceAdapter.setList(result.getData().getSurchargeItems());
                        }
                        String content = RichTextUtil.getRichTextStr(result.getData().getProductDetail());//详情
                        if (!content.equals("")) {
                            richText.setVisibility(View.VISIBLE);
                            richText.setText(content);
                            if (result.getData().getSurchargeItems().size() > 0) {//附加费
                                speck = name.getText().toString() + "," + price.getText().toString() + "," + "本服务有额外的附加费用，服务人员服务前可能收取。" + content;
                            } else {
                                speck = name.getText().toString() + "," + price.getText().toString() + "," + content;
                            }
                            BotSdk.getInstance().speakRequest(speck);
                        } else {
                            if (result.getData().getSurchargeItems().size() > 0) {//附加费
                                speck = name.getText().toString() + "," + price.getText().toString() + "," + "本服务有额外的附加费用，服务人员服务前可能收取。";
                            } else {
                                speck = name.getText().toString() + "," + price.getText().toString();
                            }
                            BotSdk.getInstance().speakRequest(speck);
                        }
                        List<String> imgs = RichTextUtil.getRichTextImgListUtil(result.getData().getProductDetail());//详情图片
                        if (imgs.size() > 0) {
                            richBanner.setVisibility(View.VISIBLE);
                            setBanner(imgs, richBanner);
                        }

                        if (result.getData().getSurchargeItems().size() == 0 && content.equals("") && imgs.size() == 0) {
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
        tabList=null;
    }

}
