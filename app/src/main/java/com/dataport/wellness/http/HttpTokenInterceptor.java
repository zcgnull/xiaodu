package com.dataport.wellness.http;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dataport.wellness.api.smalldu.XiaoDuResultDto;
import com.dataport.wellness.api.smalldu.XiaoduTokenDto;
import com.dataport.wellness.utils.BotConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpTokenInterceptor implements Interceptor {
    private Context applicationContext;
    public HttpTokenInterceptor(Context applicationContext) {
        this.applicationContext=applicationContext;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        // 获取响应
        Response response = chain.proceed(request);
        // 在这里判断是不是是token失效
        MediaType mediaType = null;
        if (response.body() != null && response.body().contentType() != null) {
            mediaType = response.body().contentType();
        }
        if (response.code() == 200&&null!=mediaType&&mediaType.subtype().equals("json")) {
            if(request.url().toString().startsWith(BotConstants.XD_URL)) {
                String responseBody = response.body().string();
//            FResult fResult = new Gson().fromJson(responseBody, FResult.class);
                System.out.println();
                //response.body().string() 只能调用一次   就会close  所以要重建response
                XiaoDuResultDto<Object> xdResult = JSON.parseObject(responseBody, new TypeReference<XiaoDuResultDto<Object>>(){}.getType());
                if(xdResult.getCode()==401||xdResult.getCode()==403){
                    String newToken=refreshToken();
                    if (!TextUtils.isEmpty(newToken)) {
                        // 创建新的请求，并增加header
                        Request retryRequest = chain.request()
                                .newBuilder()
                                .header("Authorization", "Bearer "+newToken)
                                .build();

                        // 再次发起请求
                        return chain.proceed(retryRequest);
                    }
                }
                response = response.newBuilder()
                        .body(ResponseBody.create(mediaType, responseBody))
                        .build();
            }
        }

        return response;
    }
    private String refreshToken() {
        String newtoken = null;
        /**
         * 必须使用同步请求
         */
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        Map<String, String> map = new HashMap<>();
        RequestBody requestBody = RequestBody.create(mediaType, JSON.toJSONString(map));
        Request request = new Request.Builder().url(String.format(BotConstants.XD_URL+"admin-api/system/oauth2/token?grant_type=%s&client_id=%s&client_secret=%s","client_credentials","dueros_client","Mqd7Wk9WRmUHBRyMj3Twz4jUeJ")).post(requestBody).header("tenant-id",BotConstants.TENANT_ID).build();
        okhttp3.Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String responseBodyStr=response.body().string();
            XiaoDuResultDto<XiaoduTokenDto> xdResult=JSON.parseObject(responseBodyStr,new TypeReference<XiaoDuResultDto<XiaoduTokenDto>>(){}.getType());
            if(xdResult.getCode()==0){
                newtoken=xdResult.getData().getAccess_token();
                BotConstants.DEVICE_TOKEN = "Bearer " + newtoken;
                SharedPreferences preferences = applicationContext.getSharedPreferences("XD", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("XD_TOKEN", BotConstants.DEVICE_TOKEN);
                editor.commit();
            }
            System.out.println();
//            FLog.i("HttpLog", "refreshToken response=" + response.body().string());//只能有效调用一次,不能打印

       /*     CredentialDTO credentialDTO = new Gson().fromJson(response.body().string(), CredentialDTO.class);
            if (null != credentialDTO.accessToken && null != credentialDTO.refreshToken) {
                newtoken = credentialDTO.accessToken;
                TokenManager.getInstance().setAccessToken(credentialDTO.accessToken);
                TokenManager.getInstance().setRefreshToken(credentialDTO.refreshToken);
                FLog.i("HttpLog", "refreshToken accessToken==" + credentialDTO.accessToken);
            } else {
                newtoken = "";
            }*/
        } catch (IOException e) {
            e.printStackTrace();
            newtoken = "";
        }

        return newtoken;
    }
}
