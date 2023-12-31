package com.dataport.wellness.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dataport.wellness.R;
import com.dataport.wellness.api.health.DeviceEnvApi;

import java.util.List;

//环境监测列表
public class DeviceEnvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<DeviceEnvApi.Bean.DeviceEnvListDTO> list;


    private OnFlagReadClickListener flagReadClickListener;
    private OnItemClickListener itemClickListener;

    public DeviceEnvAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<DeviceEnvApi.Bean.DeviceEnvListDTO> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    public void setFlagReadClickListener(OnFlagReadClickListener flagReadClickListener) {
        this.flagReadClickListener = flagReadClickListener;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_device_env, parent, false);

        return new ContentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContentHolder contentHolder = (ContentHolder) holder;
        contentHolder.llwarn.setOnClickListener(v -> {itemClickListener.OnItemClickListener(position);});
//        contentHolder.deviceName.setText(list.get(position).getEquipmentName());
        contentHolder.alarmAddress.setText(list.get(position).getAddress() + list.get(position).getInstallationPositionName());
        contentHolder.alarmTime.setText(list.get(position).getAlarmTime());
        contentHolder.binderName.setText(list.get(position).getBinderName());
        contentHolder.processState.setText(list.get(position).getProcessStateName());
        contentHolder.alarmTypeName.setText(list.get(position).getEquipmentName()+"\n"+list.get(position).getAlarmTypeName());
        if (!"1".equals(list.get(position).getProcessState())) {
            contentHolder.btnFlagRead.setVisibility(View.GONE);
            contentHolder.readTime.setVisibility(View.VISIBLE);
            contentHolder.readTime.setText(list.get(position).getProcessTime());
        } else {
            contentHolder.btnFlagRead.setVisibility(View.VISIBLE);
            contentHolder.readTime.setVisibility(View.GONE);
        }
        contentHolder.btnFlagRead.setOnClickListener(v -> flagReadClickListener.OnFlagReadClickListener(list.get(position), position));
    }

    public interface OnFlagReadClickListener {
        void OnFlagReadClickListener(DeviceEnvApi.Bean.DeviceEnvListDTO data, int pos);
    }

    public interface OnItemClickListener {
        void OnItemClickListener(int pos);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ContentHolder extends RecyclerView.ViewHolder {
//        public TextView deviceName;

        public LinearLayout llwarn;
        public TextView binderName;
        public TextView alarmAddress;
        public TextView alarmTime;
        public TextView readTime;
        public TextView processState;
        private TextView alarmTypeName;
        public Button btnFlagRead;

        public LinearLayout lnBtn;

        public ContentHolder(View itemView) {
            super(itemView);
            llwarn = itemView.findViewById(R.id.ll_warn);
            processState=itemView.findViewById(R.id.process_state);
//            deviceName = itemView.findViewById(R.id.device_name);
            binderName = itemView.findViewById(R.id.binderName);
            alarmAddress = itemView.findViewById(R.id.alarm_address);
            alarmTime = itemView.findViewById(R.id.alarm_time);
            readTime = itemView.findViewById(R.id.read_time);
            btnFlagRead = itemView.findViewById(R.id.btn_flag_read);
            lnBtn = itemView.findViewById(R.id.ln_btn);
            alarmTypeName=itemView.findViewById(R.id.alarm_type_name);
        }
    }
}
