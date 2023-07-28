package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.CacheMode;

import java.io.Serializable;

public class OnlineAdviceNumApi implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/service/xiaodu/queryBinderAdviceNum";
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
    private String idcard;

    public OnlineAdviceNumApi(String idcard) {
        this.idcard = idcard;
    }

    public class Bean implements Serializable {

        private int videoSum;
        private int videoReamin;
        private int vioceSum;
        private int vioceReamin;

        public int getVideoSum() {
            return videoSum;
        }

        public void setVideoSum(int videoSum) {
            this.videoSum = videoSum;
        }

        public int getVideoReamin() {
            return videoReamin;
        }

        public void setVideoReamin(int videoReamin) {
            this.videoReamin = videoReamin;
        }

        public int getVioceSum() {
            return vioceSum;
        }

        public void setVioceSum(int vioceSum) {
            this.vioceSum = vioceSum;
        }

        public int getVioceReamin() {
            return vioceReamin;
        }

        public void setVioceReamin(int vioceReamin) {
            this.vioceReamin = vioceReamin;
        }
    }
}
