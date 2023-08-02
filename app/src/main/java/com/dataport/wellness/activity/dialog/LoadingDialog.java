package com.dataport.wellness.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import com.airbnb.lottie.LottieAnimationView;
import com.dataport.wellness.R;

/**
 * @author: zcg
 * @date: 2023/7/31
 * @desc:
 */
public class LoadingDialog extends Dialog{

    private Context mContext;
    /** 提示图标 */
    private LottieAnimationView mLottieView;
    /** 提示文本 */
    private TextView mTextView;

    private String msg;
    private int resId = -1;
    private int textSize = 20;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog);
        getWindow().setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.loading_bg));
        setCancelable(false);
        initView();
        refreshView();
    }
    private void refreshView() {
        if (TextUtils.isEmpty(msg)){
            mTextView.setText("");
        } else {
            mTextView.setText(msg);
        }

        if (resId == -1){
            mLottieView.setAnimation(R.raw.progress);
        } else {
            mLottieView.setAnimation(resId);
        }

        if (!mLottieView.isAnimating()) {
            mLottieView.playAnimation();
        }
        mTextView.setTextSize(textSize);
    }

    private void initView() {
        mLottieView = findViewById(R.id.iv_status_icon);
        mTextView = findViewById(R.id.iv_status_text);
    }

    public LoadingDialog setAnimation(@RawRes int id){
        this.resId = id;
        return this;
    }
    public LoadingDialog setMsg(String msg){
        this.msg = msg;
        return this;
    }
    public LoadingDialog setTextSize(int textSize){
        this.textSize = textSize;
        return this;
    }

    @Override
    public void show() {
        super.show();
        refreshView();
    }
}
