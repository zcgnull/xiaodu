package com.dataport.wellness.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dataport.wellness.R;
import com.dataport.wellness.api.health.DeviceContentApi;
import com.dataport.wellness.api.health.DeviceContentPageApi;

import java.util.List;

public class DeviceContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<DeviceContentPageApi.Bean.RecordListDTO> list;
    private OnItemClickListener listener;

    public DeviceContentAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<DeviceContentPageApi.Bean.RecordListDTO> list) {
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
        String dataSources = list.get(position).getDataSources();
        if (position == 0) {
            contentHolder.lnItem.setBackground(context.getResources().getDrawable(R.drawable.first_bg));
        } else {
            contentHolder.lnItem.setBackground(context.getResources().getDrawable(R.drawable.back_bg));
        }
        if (dataSources != null) {
            if (dataSources.equals("1")) {
                contentHolder.type.setText("手动录入");
                contentHolder.type.setTextColor(context.getResources().getColor(R.color.colorBule));
            } else {
                contentHolder.type.setText("设备录入");
                contentHolder.type.setTextColor(context.getResources().getColor(R.color.colorBule));
            }
        } else {
            contentHolder.type.setText("设备录入");
            contentHolder.type.setTextColor(context.getResources().getColor(R.color.colorBule));
        }
        contentHolder.add1.setVisibility(View.GONE);
        contentHolder.add2.setVisibility(View.GONE);
        contentHolder.show1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        contentHolder.show2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        contentHolder.show3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        contentHolder.show4.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        contentHolder.show5.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        contentHolder.show1.setText("");
        contentHolder.show2.setText("");
        contentHolder.show3.setText("");
        contentHolder.show4.setText("");
        contentHolder.show5.setText("");
        switch (list.get(position).getDataType()) {
            case "1":
                contentHolder.add1.setVisibility(View.VISIBLE);
                contentHolder.show1.setText("收缩压 " + list.get(position).getSbp() + " mmHg");
                showIcon(list.get(position).getSbpType(), contentHolder.show1);
                contentHolder.show2.setText("舒张压 " + list.get(position).getDbp() + " mmHg");
                showIcon(list.get(position).getDbpType(), contentHolder.show2);
                contentHolder.show3.setText("脉率 " + list.get(position).getBpm() + " 次/分");
                showIcon(list.get(position).getBpmType(), contentHolder.show3);
                if (list.get(position).getPharmacySituation().equals("1")) {//用药前
                    contentHolder.show4.setText("用药前");
                } else if (list.get(position).getPharmacySituation().equals("2")) {
                    contentHolder.show4.setText("用药后");
                }
                showIcon("2", contentHolder.show4);
                break;
            case "2":
                contentHolder.add1.setVisibility(View.GONE);
                contentHolder.show1.setText("血糖 " + list.get(position).getGls() + " mmol/L");
                showIcon(list.get(position).getGlsType(), contentHolder.show1);
                contentHolder.show2.setText(list.get(position).getMeasureSituation());
                showIcon("2", contentHolder.show2);
                break;
            case "3":
                contentHolder.add1.setVisibility(View.GONE);
                contentHolder.show1.setText("血酮 " + list.get(position).getBlk() + " mmol/L");
                showIcon(list.get(position).getBlkType(), contentHolder.show1);
                break;
            case "4":
                contentHolder.add1.setVisibility(View.GONE);
                contentHolder.show1.setText("尿酸 " + list.get(position).getUric() + " mmol/L");
                showIcon(list.get(position).getUricType(), contentHolder.show1);
                break;
            case "5":
                contentHolder.add1.setVisibility(View.GONE);
                contentHolder.show1.setText("血氧 " + list.get(position).getBlo() + "%");
                showIcon(list.get(position).getBloType(), contentHolder.show1);
                break;
            case "6":
                contentHolder.add1.setVisibility(View.GONE);
                contentHolder.show1.setText("心率 " + list.get(position).getBpm());
                showIcon(list.get(position).getBpmType(), contentHolder.show1);
                break;
            case "7":
                contentHolder.add1.setVisibility(View.VISIBLE);
                contentHolder.add2.setVisibility(View.VISIBLE);
                contentHolder.show1.setText("动脉硬化指数 " + list.get(position).getAsi());
                showIcon(list.get(position).getAsiType(), contentHolder.show1);
                contentHolder.show2.setText("血管年龄 " + list.get(position).getBlvAge() + " 岁");
                showIcon("2", contentHolder.show2);
                contentHolder.show3.setText("收缩压 " + list.get(position).getSbp() + " mmHg");
                showIcon(list.get(position).getSbpType(), contentHolder.show3);
                contentHolder.show4.setText("舒张压 " + list.get(position).getDbp() + " mmHg");
                showIcon(list.get(position).getDbpType(), contentHolder.show4);
                contentHolder.show5.setText("脉搏 " + list.get(position).getBpm() + " 次/分");
                showIcon(list.get(position).getBpmType(), contentHolder.show5);
                break;
            default:
                break;
        }
        contentHolder.lnItem.setOnClickListener(v -> listener.onItemClick(list.get(position), position));
    }

    public interface OnItemClickListener {
        void onItemClick(DeviceContentPageApi.Bean.RecordListDTO data, int pos);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ContentHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView type;
        public TextView show1, show2, show3, show4, show5, show6;
        public LinearLayout add1, add2, lnItem;

        public ContentHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.tv_time);
            type = itemView.findViewById(R.id.tv_type);
            show1 = itemView.findViewById(R.id.tv_show1);
            show2 = itemView.findViewById(R.id.tv_show2);
            show3 = itemView.findViewById(R.id.tv_show3);
            show4 = itemView.findViewById(R.id.tv_show4);
            show5 = itemView.findViewById(R.id.tv_show5);
            show6 = itemView.findViewById(R.id.tv_show6);
            add1 = itemView.findViewById(R.id.ln_add1);
            add2 = itemView.findViewById(R.id.ln_add2);
            lnItem = itemView.findViewById(R.id.ln_all);
        }
    }

//    private void showIcon(String type, TextView view) {
//        if ("0".equals(type)) {
//            view.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.arrow_down_green, null), null, null, null);
//            view.setCompoundDrawablePadding(0);
//        } else if ("1".equals(type)) {
//            view.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.arrow_up_red, null), null, null, null);
//            view.setCompoundDrawablePadding(0);
//        } else {
//            view.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.icon_null, null), null, null, null);
//            view.setCompoundDrawablePadding(0);
//        }
//    }

    private void showIcon(String type, TextView view) {
        Drawable icon;
        if ("0".equals(type)) {
            icon = context.getResources().getDrawable(R.mipmap.arrow_down_green, null);
            // 必须设置
            icon.setBounds(1, 1, 30, 30);
            view.setCompoundDrawables(icon, null, null, null);
        } else if ("1".equals(type)) {
            icon = context.getResources().getDrawable(R.mipmap.arrow_up_red, null);
            icon.setBounds(1, 1, 30, 30);
            view.setCompoundDrawables(icon, null, null, null);
        } else {
            icon = context.getResources().getDrawable(R.drawable.null_bg, null);
            icon.setBounds(1, 1, 30, 30);
            view.setCompoundDrawables(icon, null, null, null);
        }
    }
}
