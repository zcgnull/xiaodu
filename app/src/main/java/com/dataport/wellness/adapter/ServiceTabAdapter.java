package com.dataport.wellness.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dataport.wellness.R;
import com.dataport.wellness.api.old.ServiceTabApi;
import com.dataport.wellness.api.old.ServiceTabBean;

import java.util.List;

public class ServiceTabAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ServiceTabBean> list;

    public ServiceTabAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<ServiceTabBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_service_tab, parent, false);
        return new TabHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TabHolder tabHolder = (TabHolder) holder;
        tabHolder.mTV.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class TabHolder extends RecyclerView.ViewHolder {
        public TextView mTV;

        public TabHolder(View itemView) {
            super(itemView);

            mTV = itemView.findViewById(R.id.item_tv_tab);
            mTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, mTV.getText(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
