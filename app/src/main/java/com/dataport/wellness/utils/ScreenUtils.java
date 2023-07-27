package com.dataport.wellness.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

/**
 * @author: zcg
 * @date: 2023/7/26
 * @desc: 屏幕工具类
 */
public class ScreenUtils {

    public static String TAG = "ScreenUtils";
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     *
     * @param context 上下文
     * @param view 适应的控件
     * @param widthPCT 宽度百分比
     * @param heightPCT 高度百分比
     */
    public static void adaptScreen(Context context, View view, float widthPCT, float heightPCT){
        //适配屏幕
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        //获取屏幕信息
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        // 计算新的宽高
        int newWidth = (int) (screenWidth * widthPCT); //
        int newHeight = (int) (screenHeight * heightPCT); // 设置为屏幕高度的三分之一

        ViewGroup.LayoutParams layoutParams = null;
        ViewParent parent = view.getParent();
        if (parent instanceof LinearLayout) {
            layoutParams = new LinearLayout.LayoutParams(newWidth, newHeight);
        } else if (parent instanceof RelativeLayout){
            layoutParams = new RelativeLayout.LayoutParams(newWidth, newHeight);
        } else if (parent instanceof FrameLayout){
            layoutParams = new FrameLayout.LayoutParams(newWidth, newHeight);
        } else if (parent instanceof TableLayout){
            layoutParams = new TableLayout.LayoutParams(newWidth, newHeight);
        } else {
            //无法处理
            Log.d(TAG, "无法处理");
            return;
        }
        view.setLayoutParams(layoutParams);
    }
}
