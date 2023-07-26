package com.dataport.wellness.api.old;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dataport.wellness.utils.AuthenticationUtil;
import com.dataport.wellness.utils.BotConstants;
import com.dataport.wellness.utils.GlobalConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class ServiceTabApi implements IRequestApi, IRequestServer {



    @NonNull
    @Override
    public String getApi() {
        return "openiot/treeDatas";
    }


    @NonNull
    @Override
    public String getHost() {
        return BotConstants.YZ_URL;
    }


    public ServiceTabApi() {
    }

}
