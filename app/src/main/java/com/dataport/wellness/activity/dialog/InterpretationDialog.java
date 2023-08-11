package com.dataport.wellness.activity.dialog;

import android.content.Context;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BulletSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;

import com.dataport.wellness.R;
import com.dataport.wellness.api.health.DeviceContentPageApi;
import com.dataport.wellness.utils.RichTextUtil;


/**
 *    desc   : 指标解读 Dialog 布局封装
 */
public final class InterpretationDialog {

    @SuppressWarnings("unchecked")
    public static class Builder<B extends Builder<?>>
            extends BaseDialog.Builder<B> {

        private Context mContext;
        private final TextView tv_unscramble;
        private TextView tv_state;
        public TextView tv_title_detail;
        public TextView tv_title_unscramble;
        public TextView time;
        public TextView type;
        public TextView show1,show2,show3,show4,show5,show6;
        public LinearLayout add1,add2;

        public Builder(Context context) {
            super(context);
            mContext = context;
            setContentView(R.layout.interpretation_dialog);
            setAnimStyle(BaseDialog.ANIM_IOS);
            setGravity(Gravity.CENTER);

           
            tv_unscramble = findViewById(R.id.tv_unscramble);
            tv_state = findViewById(R.id.tv_state);
            tv_title_detail = findViewById(R.id.tv_title_detail);
            tv_title_unscramble = findViewById(R.id.tv_title_unscramble);
            time = findViewById(R.id.tv_time);
            type = findViewById(R.id.tv_type);
            show1 = findViewById(R.id.tv_show1);
            show2 = findViewById(R.id.tv_show2);
            show3 = findViewById(R.id.tv_show3);
            show4 = findViewById(R.id.tv_show4);
            show5 = findViewById(R.id.tv_show5);
            show6 = findViewById(R.id.tv_show6);
            add1 = findViewById(R.id.ln_add1);
            add2 = findViewById(R.id.ln_add2);
        }

        public Builder setTitle(String title) {
            if (title !=null){
                tv_title_detail.setText(title + tv_title_detail.getText());
                tv_title_unscramble.setText(title + tv_title_unscramble.getText());
            }
            return this;
        }

        public Builder setUnscramble(String text) {
            tv_unscramble.setText(text);
            //tv_unscramble.setMovementMethod(ScrollingMovementMethod.getInstance());
            return this;
        }

        public Builder setState(String text) {
            tv_state.setText(text);
            if (text.contains("异常")){
                tv_state.setTextColor(Color.RED);
            } else if (text.contains("正常")){
                tv_state.setTextColor(0xFF008000);
            } else {
                tv_state.setTextColor(0x5791FF);
            }
            return this;
        }

        public Builder setRecord(DeviceContentPageApi.Bean.RecordListDTO list) {
            time.setText(list.getStartTime());
            String dataSources = list.getDataSources();
            if (dataSources != null){
                if (dataSources.equals("1")) {
                    type.setText("手动录入");
                    type.setTextColor(mContext.getResources().getColor(R.color.colorBule));
                } else {
                    type.setText("设备录入");
                    type.setTextColor(mContext.getResources().getColor(R.color.colorBule));
                }
            } else {
                type.setText("设备录入");
                type.setTextColor(mContext.getResources().getColor(R.color.colorBule));
            }
            switch (list.getDataType()){
                case "1":
                    add1.setVisibility(View.VISIBLE);
                    show1.setText("收缩压 " + list.getSbp() + " mmHg");
                    showIcon(list.getSbpType(), show1);
                    show2.setText("舒张压 " + list.getDbp() + " mmHg");
                    showIcon(list.getDbpType(), show2);
                    show3.setText("脉率 " + list.getBpm() + " 次/分");
                    showIcon(list.getBpmType(), show3);
                    if (list.getPharmacySituation().equals("1")) {//用药前
                        show4.setText("用药前");
                    } else if (list.getPharmacySituation().equals("2")){
                        show4.setText("用药后");
                    }
                    showIcon("2", show4);
                    break;
                case "2":
                    add1.setVisibility(View.GONE);
                    show1.setText("血糖 " + list.getGls() + " mmol/L");
                    showIcon(list.getGlsType(), show1);
                    show2.setText(list.getMeasureSituation());
                    showIcon("2", show2);
                    break;
                case "3":
                    add1.setVisibility(View.GONE);
                    show1.setText("血酮 " + list.getBlk() + " mmol/L");
                    showIcon(list.getBlkType(), show1);
                    break;
                case "4":
                    add1.setVisibility(View.GONE);
                    show1.setText("尿酸 " + list.getUric() + " mmol/L");
                    showIcon(list.getUricType(), show1);
                    break;
                case "5":
                    add1.setVisibility(View.GONE);
                    show1.setText("血氧 " + list.getBlo() + "%");
                    showIcon(list.getBloType(), show1);
                    break;
                case "6":
                    add1.setVisibility(View.GONE);
                    show1.setText("心率 " + list.getBpm());
                    showIcon(list.getBpmType(), show1);
                    break;
                case "7":
                    add1.setVisibility(View.VISIBLE);
                    add2.setVisibility(View.VISIBLE);
                    show1.setText("动脉硬化指数 " + list.getAsi());
                    showIcon(list.getAsiType(), show1);
                    show2.setText("血管年龄 " + list.getBlvAge() + " 岁");
                    showIcon("2", show2);
                    show3.setText("收缩压 " + list.getSbp() + " mmHg");
                    showIcon(list.getSbpType(), show3);
                    show4.setText("舒张压 " + list.getDbp() + " mmHg");
                    showIcon(list.getDbpType(), show4);
                    show5.setText("脉搏 " + list.getBpm() + " 次/分");
                    showIcon(list.getBpmType(), show5);
                    break;
                default:
                    break;
            }
            return this;
        }

        private void showIcon(String type, TextView view){
            if ("0".equals(type)){
                view.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.mipmap.arrow_down_green, null), null,null , null);
                view.setCompoundDrawablePadding(0);
            } else if ("1".equals(type)){
                view.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.mipmap.arrow_up_red, null), null, null, null);
                view.setCompoundDrawablePadding(0);
            } else {
                view.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.mipmap.icon_null, null), null, null, null);
                view.setCompoundDrawablePadding(0);
            }
        }
    }
    
}