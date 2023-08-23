package com.dataport.wellness.activity.dialog;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.dataport.wellness.R;


/**
 *    desc   : 数值 Dialog 布局封装
 */
public final class RecordDialog {

    @SuppressWarnings("unchecked")
    public static class Builder<B extends Builder<?>>
            extends BaseDialog.Builder<B> {
        private TextView tv_time;

        private TextView tv_parameter;


        public Builder(Context context) {
            super(context);

            setContentView(R.layout.record_dialog);
            setAnimStyle(BaseDialog.ANIM_IOS);
            setBackgroundDimEnabled(false);
            setGravity(Gravity.LEFT);

            tv_time = findViewById(R.id.tv_time);
            tv_parameter = findViewById(R.id.tv_parameter);

        }

        public Builder setOffset(int xOffset, int yOffset){
            setXOffset(xOffset);
            setYOffset(yOffset);
            return this;
        }

        public Builder setTime(String time) {
            tv_time.setText(time);
            return this;
        }

        public Builder setParameter(String parameter) {
            tv_parameter.setText(parameter);
            return this;
        }
    }
}