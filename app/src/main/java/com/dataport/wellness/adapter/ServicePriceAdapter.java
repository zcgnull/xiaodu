package com.dataport.wellness.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dataport.wellness.R;
import com.dataport.wellness.api.old.QueryCommodityApi;
import com.dataport.wellness.api.old.QueryServiceDetailApi;

import java.util.List;

public class ServicePriceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private List<QueryServiceDetailApi.Bean.SurchargeItemsDTO> list;
    private ServiceContentAdapter.OnItemClickListener listener;

    public ServicePriceAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<QueryServiceDetailApi.Bean.SurchargeItemsDTO> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setListener(ServiceContentAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_service_price, parent, false);
        return new ContentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContentHolder contentHolder = (ContentHolder) holder;
        contentHolder.name.setText(list.get(position).getSurchargeName());
        contentHolder.price.setText(list.get(position).getSurchargePrice());
        contentHolder.content.setText(list.get(position).getSurchargeDesc());
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
        public TextView content;

        public ContentHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            price = itemView.findViewById(R.id.tv_price);
            content = itemView.findViewById(R.id.tv_content);
        }
    }
}
