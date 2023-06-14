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

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.dataport.wellness.R;
import com.dataport.wellness.api.health.OnlineDoctorApi;
import com.dataport.wellness.api.old.QueryCommodityApi;
import com.dataport.wellness.http.glide.GlideApp;

import java.util.List;

public class OnlineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<OnlineDoctorApi.Bean.DoctorListDTO> list;
    private OnItemClickListener listener;

    public OnlineAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<OnlineDoctorApi.Bean.DoctorListDTO> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_online_doctor, parent, false);
        return new ContentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContentHolder contentHolder = (ContentHolder) holder;
        contentHolder.index.setText(position + 1 + "");
        contentHolder.name.setText(list.get(position).getDoctorName());
        contentHolder.ks.setText("科室：" + list.get(position).getDeptName());
        contentHolder.zc.setText("职称：" + list.get(position).getTitleName());
        contentHolder.company.setText(list.get(position).getInstitutionName());
//        // 交叉淡入的方式显示占位符，避免占位符还能显示。
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        GlideApp.with(context)
                .load(list.get(position).getDoctorUrl())
                .transition(DrawableTransitionOptions.withCrossFade(factory))
                .transform(new RoundedCorners((int) context.getResources().getDimension(R.dimen.dp_10)))
                .into(contentHolder.icon);
        contentHolder.item.setOnClickListener(v -> listener.onItemClick(list.get(position), position));
    }

    public interface OnItemClickListener{
        void onItemClick(OnlineDoctorApi.Bean.DoctorListDTO data, int pos);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ContentHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView ks;
        public TextView zc;
        public TextView company;
        public TextView index;
        public ImageView icon;
        private RelativeLayout item;

        public ContentHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            ks = itemView.findViewById(R.id.tv_ks);
            zc = itemView.findViewById(R.id.tv_zc);
            company = itemView.findViewById(R.id.tv_company);
            index = itemView.findViewById(R.id.tv_index);
            icon = itemView.findViewById(R.id.iv_doctor);
            item = itemView.findViewById(R.id.rv_item);
        }
    }
}
