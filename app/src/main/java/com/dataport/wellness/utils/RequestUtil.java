package com.dataport.wellness.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.dataport.wellness.api.smalldu.DeviceTokenApi;
import com.dataport.wellness.http.HttpIntData;
import com.hjq.http.EasyHttp;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.http.request.HttpRequest;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class RequestUtil {
    public static void get(String url, Map<String,Object> formMap){
        System.out.println();
//        Toast.makeText(ContextUtil.getContext(),"测试",Toast.LENGTH_LONG).show();
    }


}
