package com.dataport.wellness.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.dataport.wellness.R;
import com.dataport.wellness.api.health.OnlineDoctorApi;
import com.dataport.wellness.api.health.OnlineRecordApi;
import com.dataport.wellness.http.glide.GlideApp;

import java.util.List;

public class OnLineRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<OnlineRecordApi.Bean.AdviceRecordListDTO> list;
    private OnCancelClickListener cancelClickListener;
    private OnAdviceClickListener adviceClickListener;

    public OnLineRecordAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<OnlineRecordApi.Bean.AdviceRecordListDTO> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setCancelListener(OnCancelClickListener listener) {
        this.cancelClickListener = listener;
    }

    public void setAdviceListener(OnAdviceClickListener listener) {
        this.adviceClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_online_record, parent, false);
        return new ContentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContentHolder contentHolder = (ContentHolder) holder;
        if (list.get(position).getServiceCode().equals("3")) {
            contentHolder.type.setText("语音咨询");
        } else {
            contentHolder.type.setText("视频咨询");
        }
        contentHolder.doctor.setText(list.get(position).getDoctorName());
        contentHolder.time.setText(list.get(position).getCreateTime());

        contentHolder.xz.setText(Integer.valueOf(list.get(position).getLimitDuration()) / 60 + "分");
        contentHolder.sj.setText(Integer.valueOf(list.get(position).getServiceDuration()) / 60 + "分");
        contentHolder.done.setText(list.get(position).getCompleteTime());
        if (list.get(position).getUseState().equals("1")) {
            contentHolder.status.setText("未接诊");
            contentHolder.lnBtn.setVisibility(View.VISIBLE);
        } else if (list.get(position).getUseState().equals("2")) {
            contentHolder.status.setText("已接诊");
            contentHolder.lnBtn.setVisibility(View.INVISIBLE);
        } else if (list.get(position).getUseState().equals("6")) {
            contentHolder.status.setText("已完成");
            contentHolder.lnBtn.setVisibility(View.INVISIBLE);
        } else {
            contentHolder.status.setText("已关闭");
            contentHolder.lnBtn.setVisibility(View.INVISIBLE);
        }
        contentHolder.cancel.setOnClickListener(v -> cancelClickListener.onCancelClickListener(list.get(position), position));
        contentHolder.confirm.setOnClickListener(v -> adviceClickListener.onAdviceClickListener(list.get(position), position));
    }

    public interface OnCancelClickListener {
        void onCancelClickListener(OnlineRecordApi.Bean.AdviceRecordListDTO data, int pos);
    }

    public interface OnAdviceClickListener {
        void onAdviceClickListener(OnlineRecordApi.Bean.AdviceRecordListDTO data, int pos);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ContentHolder extends RecyclerView.ViewHolder {
        public TextView type;
        public TextView doctor;
        public TextView time;
        public TextView xz;
        public TextView sj;
        public TextView done;
        public TextView status;
        public Button cancel;
        public Button confirm;
        public LinearLayout lnBtn;

        public ContentHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.tv_type);
            doctor = itemView.findViewById(R.id.tv_doctor);
            time = itemView.findViewById(R.id.tv_time);
            xz = itemView.findViewById(R.id.tv_xz);
            sj = itemView.findViewById(R.id.tv_sj);
            done = itemView.findViewById(R.id.tv_done);
            status = itemView.findViewById(R.id.tv_status);
            cancel = itemView.findViewById(R.id.btn_cancel);
            confirm = itemView.findViewById(R.id.btn_confirm);
            lnBtn = itemView.findViewById(R.id.ln_btn);
        }
    }
}
