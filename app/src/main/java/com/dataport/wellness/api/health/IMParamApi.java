package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;

public class IMParamApi implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/service/xiaodu/getImParam";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.JK_URL;
    }

    private long binderId;
    private String doctorId;

    public IMParamApi(long binderId, String doctorId) {
        this.binderId = binderId;
        this.doctorId = doctorId;
    }

    public static class Bean implements Serializable {

        private int sdkappid;
        private String key;

        public int getSdkappid() {
            return sdkappid;
        }

        public void setSdkappid(int sdkappid) {
            this.sdkappid = sdkappid;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
