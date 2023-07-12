package com.dataport.wellness.api.old;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;
import java.util.List;

public class ServiceTabApi implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "biz/ne/mjzx/b/a/treeDatas";
    }


    @NonNull
    @Override
    public String getHost() {
        return BotConstants.YZ_URL;
    }

    private String pid;

    public ServiceTabApi(String pid) {
        this.pid = pid;
    }

}
