package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

/**
 * 环境监测告警数据处理
 */
public class DeviceEnvProcessApi implements IRequestApi, IRequestServer {



    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/service/xiaodu/adviceDoctor";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.JK_URL;
    }

    private Long id;
    public DeviceEnvProcessApi(Long id) {
        this.id = id;
    }
}
