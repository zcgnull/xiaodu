package com.dataport.wellness.api;


import com.dataport.wellness.R;
import com.dataport.wellness.utils.ContextUtil;
import com.dataport.wellness.utils.RequestUtil;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/*康养管家后台接口*/
public class WellNessApi {
    //获取设备sn
    public static JSONObject getDeviceSn(String deviceId,String apiAccessToken){
        Map<String,Object>mp=new HashMap<>();
        mp.put("deviceId",deviceId);
        mp.put("apiAccessToken", URLEncoder.encode(apiAccessToken));
        RequestUtil.get(ContextUtil.getContext().getResources().getString(R.string.well_ness_api_url)+"/admin-api/dueros/device-warehouse/get-device-sn", mp);
        return null;
    }
}