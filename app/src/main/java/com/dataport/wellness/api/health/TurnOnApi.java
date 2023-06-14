package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;

public class TurnOnApi  implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/service/VoiceVideo/turnOn";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.JK_URL;
    }

    private String binderTimId;
    private String doctorTimId;
    private String serviceCode;

    public TurnOnApi(String binderTimId, String doctorTimId, String serviceCode) {
        this.binderTimId = binderTimId;
        this.doctorTimId = doctorTimId;
        this.serviceCode = serviceCode;
    }

    public static class Bean implements Serializable {

    }
}
