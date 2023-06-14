package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

public class CancelRecordApi implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/service/xiaodu/cancelAdviceUerRecord";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.JK_URL;
    }

    private int adviceId;

    public CancelRecordApi(int adviceId) {
        this.adviceId = adviceId;
    }
}
