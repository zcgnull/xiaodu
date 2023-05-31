package com.dataport.wellness.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dataport.wellness.R;
import com.dataport.wellness.api.health.DeviceContentApi;

import java.util.List;

public class DeviceContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<DeviceContentApi.Bean.ListDTO> list;
    private OnItemClickListener listener;

    public DeviceContentAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<DeviceContentApi.Bean.ListDTO> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_device_content, parent, false);
        return new ContentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContentHolder contentHolder = (ContentHolder) holder;
        contentHolder.time.setText(list.get(position).getStartTime());
        if (list.get(position).getDataSources().equals("1")) {
            contentHolder.type.setText("手动录入");
            contentHolder.type.setTextColor(context.getResources().getColor(R.color.colorBule));
        } else {
            contentHolder.type.setText("设备录入");
            contentHolder.type.setTextColor(context.getResources().getColor(R.color.green));
        }
        if (list.get(position).getDataType().equals("1")) {//血压
            contentHolder.lnType.setVisibility(View.VISIBLE);
            contentHolder.sbp.setText("收缩压 " + list.get(position).getSbp() + " mmHg");
            contentHolder.dbp.setText("舒张压 " + list.get(position).getDbp() + " mmHg");
            contentHolder.bpm.setText("脉率 " + list.get(position).getBpm() + " 次/分");
            if (list.get(position).getPharmacySituation().equals("1")) {//用药前
                contentHolder.equipmentName.setText("用药前");
            } else {
                contentHolder.equipmentName.setText("用药后");
            }
        } else {
            contentHolder.lnType.setVisibility(View.GONE);
            contentHolder.sbp.setText("血糖 " + list.get(position).getGls() + " mmol/L");
            contentHolder.dbp.setText(list.get(position).getMeasureSituation());
        }
        contentHolder.lnItem.setOnClickListener(v -> listener.onItemClick(list.get(position), position));
    }

    public interface OnItemClickListener {
        void onItemClick(DeviceContentApi.Bean.ListDTO data, int pos);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ContentHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView type;
        public TextView sbp;
        public TextView dbp;
        public TextView bpm;
        public TextView equipmentName;
        public LinearLayout lnType,lnItem;

        public ContentHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.tv_time);
            type = itemView.findViewById(R.id.tv_type);
            sbp = itemView.findViewById(R.id.sbp);
            dbp = itemView.findViewById(R.id.dbp);
            bpm = itemView.findViewById(R.id.bpm);
            equipmentName = itemView.findViewById(R.id.equipmentName);
            lnType = itemView.findViewById(R.id.ln_type);
            lnItem = itemView.findViewById(R.id.ln_all);
        }
    }
}
