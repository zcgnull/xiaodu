package com.dataport.wellness.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dataport.wellness.R;
import com.dataport.wellness.api.ImgBean;
import com.dataport.wellness.api.QueryCommodityApi;
import com.dataport.wellness.api.QueryServiceDetailApi;
import com.dataport.wellness.api.ServiceTabApi;
import com.dataport.wellness.utils.StringUtil;
import com.google.android.material.tabs.TabLayout;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.sunfusheng.marqueeview.MarqueeView;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.zzhoujay.richtext.RichText;

import java.util.ArrayList;
import java.util.List;

public class ServiceDetailActivity extends BaseActivity {

    private Banner banner;
    private TabLayout tabLayout;
    private MarqueeView marqueeView;
    private TextView richText;

    private ImgBean imgBean;
    private QueryServiceDetailApi.Bean detailData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);
        findViewById(R.id.ln_back).setOnClickListener(v -> finish());
        banner = findViewById(R.id.banner);
        marqueeView = findViewById(R.id.marqueeView);
        richText = findViewById(R.id.rich_text);
        tabLayout = findViewById(R.id.detail_tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabView = tab.getCustomView().findViewById(R.id.item_tv_tab);
                tabView.setBackground(getResources().getDrawable(R.drawable.service_tab_bg));
                tabView.setTextColor(getResources().getColor(R.color.clolrWhite));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabView = tab.getCustomView().findViewById(R.id.item_tv_tab);
                tabView.setBackground(getResources().getDrawable(R.drawable.service_selected_tab_bg));
                tabView.setTextColor(getResources().getColor(R.color.clolrBule));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        for (int i = 0; i < 4; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View tabView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_service_tab, null);
            TextView tabText = tabView.findViewById(R.id.item_tv_tab);
            tabText.setText("规格一");
            tab.setCustomView(tabView);
            tabLayout.addTab(tab);
        }


        List<String> messages = new ArrayList<>();
        messages.add("请试试对我说：“小度小度......”");
        marqueeView.startWithList(messages);

        Intent intent = getIntent();
        queryDetail(intent.getStringExtra("productId"), intent.getStringExtra("providerId"));
    }

    private void queryDetail(String productId, String providerId) {
        EasyHttp.get(this)
                .api(new QueryServiceDetailApi(productId, providerId))
                .request(new HttpCallback<QueryServiceDetailApi.Bean>(this) {

                    @Override
                    public void onSucceed(QueryServiceDetailApi.Bean result) {
                        detailData = result;
                        setBanner(result.getCarouselPicture());
                        String img = "<img alt=\"\" src=\"https://qhdmzj.dataport.com.cn:8888/qhdtestrjzlyl/mz/upload/pub/load/mzpic_view/4028818b878dfc4b0187920792400004\" />";
                        RichText.fromHtml(img)
                                .showBorder(true)
                                .noImage(false)
                                .into(richText);
                    }
                });
    }



//    protected void showEditData(String content) {
//        richText.clearAllLayout();
//        List<String> textList = StringUtil.cutStringByImgTag(content);
//        for (int i = 0; i < textList.size(); i++) {
//            String text = textList.get(i);
//            if (text.contains("<img") && text.contains("src=")) {
//                //imagePath可能是本地路径，也可能是网络地址
//                String imagePath = StringUtil.getImgSrc(text);
//                richText.addImageViewAtIndex(richText.getLastIndex(), imagePath);
//            } else {
//                richText.addTextViewAtIndex(richText.getLastIndex(), text);
//            }
//        }
//    }

    private void setBanner(List<String> data){
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

}
