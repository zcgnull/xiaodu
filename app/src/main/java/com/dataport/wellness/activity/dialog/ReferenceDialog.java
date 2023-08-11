package com.dataport.wellness.activity.dialog;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.dataport.wellness.R;


/**
 *    desc   : 数值参考 Dialog 布局封装
 */
public final class ReferenceDialog {

    @SuppressWarnings("unchecked")
    public static class Builder<B extends Builder<?>>
            extends BaseDialog.Builder<B> {

        private boolean mAutoDismiss = true;

        private final TextView tv_illustrate;

        private final TextView tv_reference;

        private TextView tv_title_illustrate;

        private TextView tv_title_reference;


        public Builder(Context context) {
            super(context);

            setContentView(R.layout.reference_dialog);
            setAnimStyle(BaseDialog.ANIM_IOS);
            setGravity(Gravity.CENTER);

            tv_illustrate = findViewById(R.id.tv_illustrate);
            tv_reference = findViewById(R.id.tv_reference);
            tv_title_illustrate = findViewById(R.id.tv_title_illustrate);
            tv_title_reference = findViewById(R.id.tv_title_reference);
        }

        public Builder setTitle(String title) {
            if (title != null){
                tv_title_illustrate.setText(title + tv_title_illustrate.getText());
                tv_title_reference.setText(title + tv_title_reference.getText());
            }
            return this;
        }

        public Builder setIllustrate(String text) {
            tv_illustrate.setText(text);
            //tv_illustrate.setMovementMethod(ScrollingMovementMethod.getInstance());
            return this;
        }

        public Builder setReference(String text) {
            tv_reference.setText(text);
            //tv_reference.setMovementMethod(ScrollingMovementMethod.getInstance());
            return this;
        }
    }
}