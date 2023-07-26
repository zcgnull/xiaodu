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

import com.dataport.wellness.R;
import com.dataport.wellness.api.health.DeviceContentPageApi;
import com.dataport.wellness.api.smalldu.MessageApi;
import com.dataport.wellness.been.MessageBean;
import com.dataport.wellness.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class SpeechAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MessageBean> list;

    public SpeechAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<MessageBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_speech, parent, false);
        return new ContentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContentHolder contentHolder = (ContentHolder) holder;
        if (list.get(position).getRole().equals("user")) {
            contentHolder.rl_right.setVisibility(View.VISIBLE);
            contentHolder.rl_left.setVisibility(View.GONE);
            contentHolder.userTime.setText(TimeUtil.getInstance().dateToDate(list.get(position).getCreated() * 1000));
            contentHolder.userContent.setText(list.get(position).getContent());
        } else {
            contentHolder.rl_left.setVisibility(View.VISIBLE);
            contentHolder.rl_right.setVisibility(View.GONE);
            contentHolder.assistantTime.setText(TimeUtil.getInstance().dateToDate(list.get(position).getCreated() * 1000));
            contentHolder.assistantContent.setText(list.get(position).getContent());
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private class ContentHolder extends RecyclerView.ViewHolder {
        public TextView userTime, assistantTime;
        public TextView userContent, assistantContent;
        private RelativeLayout rl_left, rl_right;

        public ContentHolder(View itemView) {
            super(itemView);
            userTime = itemView.findViewById(R.id.user_time);
            userContent = itemView.findViewById(R.id.user_content);
            assistantTime = itemView.findViewById(R.id.assistant_time);
            assistantContent = itemView.findViewById(R.id.assistant_content);
            rl_left = itemView.findViewById(R.id.rl_left);
            rl_right = itemView.findViewById(R.id.rl_right);
        }
    }
}
