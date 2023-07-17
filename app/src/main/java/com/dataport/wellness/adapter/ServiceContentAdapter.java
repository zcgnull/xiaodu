package com.dataport.wellness.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.dataport.wellness.R;
import com.dataport.wellness.api.old.QueryCommodityApi;
import com.dataport.wellness.http.glide.GlideApp;

import java.util.List;

public class ServiceContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<QueryCommodityApi.Bean.ListDTO> list;
    private OnItemClickListener listener;

    public ServiceContentAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<QueryCommodityApi.Bean.ListDTO> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_service_content, parent, false);
        return new ContentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContentHolder contentHolder = (ContentHolder) holder;
        contentHolder.index.setText(position + 1 + "");
        contentHolder.name.setText(list.get(position).getProductName());
        contentHolder.price.setText("价格：¥" + list.get(position).getProductPrice());
//        contentHolder.distance.setText("距离：" + list.get(position).getDistance());
        contentHolder.sale.setText("已售：" + list.get(position).getSaleCount());
        contentHolder.company.setText(list.get(position).getProviderName());
        // 交叉淡入的方式显示占位符，避免占位符还能显示。
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        GlideApp.with(context)
                .load(list.get(position).getPicture())
                .transition(DrawableTransitionOptions.withCrossFade(factory))
                .skipMemoryCache(true)//禁用内存缓存功能
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不缓存任何内容
                .transform(new RoundedCorners((int) context.getResources().getDimension(R.dimen.dp_10)))
                .into(contentHolder.icon);
        contentHolder.item.setOnClickListener(v -> listener.onItemClick(list.get(position), position));
    }

    public interface OnItemClickListener{
        void onItemClick(QueryCommodityApi.Bean.ListDTO data, int pos);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ContentHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView price;
        public TextView distance;
        public TextView sale;
        public TextView company;
        public TextView index;
        public ImageView icon;
        private RelativeLayout item;

        public ContentHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            price = itemView.findViewById(R.id.tv_price);
            distance = itemView.findViewById(R.id.tv_distance);
            sale = itemView.findViewById(R.id.tv_saleCount);
            company = itemView.findViewById(R.id.tv_company);
            index = itemView.findViewById(R.id.tv_index);
            icon = itemView.findViewById(R.id.iv_service);
            item = itemView.findViewById(R.id.rv_item);
        }
    }
}
