package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.CacheMode;

public class TurnOffApi implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/service/VoiceVideo/turnOff";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.JK_URL;
    }
    @NonNull
    @Override
    public CacheMode getCacheMode() {
        return CacheMode.NO_CACHE;
    }
    private String binderTimId;
    private String doctorTimId;
    private String serviceCode;

    public TurnOffApi(String binderTimId, String doctorTimId, String serviceCode) {
        this.binderTimId = binderTimId;
        this.doctorTimId = doctorTimId;
        this.serviceCode = serviceCode;
    }
}
