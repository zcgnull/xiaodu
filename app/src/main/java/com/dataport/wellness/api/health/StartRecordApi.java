package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

public class StartRecordApi implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/service/VoiceVideo/startRecord";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.JK_URL;
    }

    private int adviceUseRecordId;
    private String startPeople;

    public StartRecordApi(int adviceUseRecordId, String startPeople) {
        this.adviceUseRecordId = adviceUseRecordId;
        this.startPeople = startPeople;
    }
}
