package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;

public class AdviceDoctorApi implements IRequestApi, IRequestServer {

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

    private String serviceCode;
    private long binderId;
    private long doctorId;

    public AdviceDoctorApi(String serviceCode, long binderId, long doctorId) {
        this.serviceCode = serviceCode;
        this.binderId = binderId;
        this.doctorId = doctorId;
    }

    public static class Bean implements Serializable {


        private String msg;
        private String code;
        private InfoDTO info;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public InfoDTO getInfo() {
            return info;
        }

        public void setInfo(InfoDTO info) {
            this.info = info;
        }

        public static class InfoDTO {
            private int adviceUseRecordId;
            private String isLimit;
            private int limitDuration;

            public int getAdviceUseRecordId() {
                return adviceUseRecordId;
            }

            public void setAdviceUseRecordId(int adviceUseRecordId) {
                this.adviceUseRecordId = adviceUseRecordId;
            }

            public String getIsLimit() {
                return isLimit;
            }

            public void setIsLimit(String isLimit) {
                this.isLimit = isLimit;
            }

            public int getLimitDuration() {
                return limitDuration;
            }

            public void setLimitDuration(int limitDuration) {
                this.limitDuration = limitDuration;
            }
        }
    }
}
