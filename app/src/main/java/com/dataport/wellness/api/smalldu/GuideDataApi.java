package com.dataport.wellness.api.smalldu;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.CacheMode;

import java.io.Serializable;

public class GuideDataApi implements IRequestApi, IRequestServer {
    @NonNull
    @Override
    public String getApi() {
        return "app-api/dueros/steer-config/get-by-type";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.XD_URL;
    }

    @NonNull
    @Override
    public CacheMode getCacheMode() {
        return CacheMode.NO_CACHE;
    }

    //noBind(1, "设备未绑定"),
    //noAuth(2, "设备未授权"),
    //noSign(3, "体征数据不存在");
    private String steerType;

    public GuideDataApi(String steerType) {
        this.steerType = steerType;
    }

    public class Bean implements Serializable {

        private int steerType;
        private String steerDesc;
        private String steerImg;

        public int getSteerType() {
            return steerType;
        }

        public void setSteerType(int steerType) {
            this.steerType = steerType;
        }

        public String getSteerDesc() {
            return steerDesc;
        }

        public void setSteerDesc(String steerDesc) {
            this.steerDesc = steerDesc;
        }

        public String getSteerImg() {
            return steerImg;
        }

        public void setSteerImg(String steerImg) {
            this.steerImg = steerImg;
        }
    }
}
