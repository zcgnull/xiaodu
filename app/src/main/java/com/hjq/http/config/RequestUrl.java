package com.hjq.http.config;

import androidx.annotation.NonNull;

import com.hjq.http.annotation.HttpIgnore;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2022/03/03
 *    desc   : 请求 url 简单配置类
 */
public final class RequestUrl implements IRequestServer, IRequestApi {

   /** 主机地址 */
   @HttpIgnore
   private final String mHost;

   /** 接口地址 */
   @HttpIgnore
   private final String mApi;

   public RequestUrl(String url) {
      this(url, "");
   }

   public RequestUrl(String host, String api) {
      mHost = host;
      mApi = api;
   }

   @NonNull
   @Override
   public String getHost() {
      return mHost;
   }

   @NonNull
   @Override
   public String getApi() {
      return mApi;
   }

   @NonNull
   @Override
   public String toString() {
      return mHost + mApi;
   }
}