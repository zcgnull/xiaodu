package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.CacheMode;

import java.io.Serializable;
import java.util.List;

public class IndicatorInterpretationApi implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/jkgl/healthRecords/queryIllustrate";
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
    private String identifying;
    private String value1;
    private String value2;
    private String binderId;

    public IndicatorInterpretationApi(String identifying, String value1, String value2, String binderId) {
        this.identifying = identifying;
        this.value1 = value1;
        this.value2 = value2;
        this.binderId = binderId;
    }

    public class Bean implements Serializable {
        private String refer;
        private String illustrate;
        private String tips;
        private String content;

        public String getRefer() {
            return refer;
        }

        public void setRefer(String refer) {
            this.refer = refer;
        }

        public String getIllustrate() {
            return illustrate;
        }

        public void setIllustrate(String illustrate) {
            this.illustrate = illustrate;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
