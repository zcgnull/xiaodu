package com.dataport.wellness.activity.dialog;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dataport.wellness.R;
import com.dataport.wellness.utils.ScreenUtils;
import com.dataport.wellness.utils.SingleClick;

import java.util.List;

public class SettingDialog {
    public static final class Builder
            extends BaseDialog.Builder<Builder>
            implements AdapterView.OnItemSelectedListener {
        @SuppressWarnings("rawtypes")
        @Nullable
        private RelativeLayout mMainLinearLayout;
        private OnListener mListener;
        private ImageView mIvModel;
        private Button mBtnOk;
        private Spinner mSpinnerModel;

        //用于记录回调信息
        private int position = 0;
        private String QAModel = "";
        private boolean geekModel = true;

        private final SettingDialog.SpinnerAdapter mAdapter;

        public Builder(Context context){
            super(context);
            setContentView(R.layout.zcgtest_dialog);
            setAnimStyle(BaseDialog.ANIM_BOTTOM);

            mMainLinearLayout = findViewById(R.id.ll_dialog);

            mIvModel = findViewById(R.id.iv_model);
            setOnClickListener(mIvModel);

            mBtnOk = findViewById(R.id.btn_dialog_ok);
            setOnClickListener(mBtnOk);

            mSpinnerModel = findViewById(R.id.spinner_model);
            mAdapter = new SettingDialog.SpinnerAdapter(getContext());
            mSpinnerModel.setAdapter(mAdapter);
//            mSpinnerModel.setPrompt("请选择视频分类");
            //设置下拉框默认的显示第一项
            mSpinnerModel.setSelection(0);
            //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
            mSpinnerModel.setOnItemSelectedListener(this);
        }

        public Builder setList(List data) {
            mAdapter.addAll(data);
            mAdapter.notifyDataSetChanged();
            return this;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        public Builder setPosition(int position){
            this.position = position;
            mSpinnerModel.setSelection(position);
            return this;
        }

        public Builder setGeek(boolean geek){
            this.geekModel = geek;
            if (geek){
                mIvModel.setImageResource(R.drawable.guide_1_bg);
            } else {
                mIvModel.setImageResource(R.drawable.jk_bg);
            }
            return this;
        }

        public Builder setWidthAndHeight(float widthPerc, float heightPerc){
            ScreenUtils.adaptScreen(getContext(), mMainLinearLayout, widthPerc, heightPerc);
            return this;
        }


        @SingleClick
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_dialog_ok){
                //确认
                mListener.onSure(getDialog(), position, geekModel, QAModel);
                dismiss();
            } else if (view.getId() == R.id.iv_model){
                geekModel = !geekModel;
                if (geekModel){
                    mIvModel.setImageResource(R.drawable.guide_1_bg);
                } else {
                    mIvModel.setImageResource(R.drawable.jk_bg);
                }
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            QAModel = String.valueOf(adapterView.getSelectedItem());
            position = i;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private static final class SpinnerAdapter extends ArrayAdapter<Object> {

        public SpinnerAdapter(@NonNull Context context) {
            super(context, R.layout.video_type_item_dropdown);
            setDropDownViewResource(R.layout.video_type_item_dropdown);
        }
    }

    public interface OnListener<T> {

        /**
         * 点击确认时回调
         */
        void onSure(BaseDialog dialog, int position, boolean geekModel, String QAModel) ;
    }
}
