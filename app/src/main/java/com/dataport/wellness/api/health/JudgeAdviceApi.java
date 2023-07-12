package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;

public class JudgeAdviceApi implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/service/PYServicePackage/judegAdvice";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.JK_URL;
    }

    private String serviceCode;
    private long doctorId;
    private long binderId;

    public JudgeAdviceApi(String serviceCode, long doctorId, long binderId) {
        this.serviceCode = serviceCode;
        this.doctorId = doctorId;
        this.binderId = binderId;
    }

    public class Bean implements Serializable {

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

        public class InfoDTO {
            private int adviceUseRecordId;
            private String serviceDuration;
            private String remainingDuration;
            private String isTimeOut;
            private String isExist;

            public int getAdviceUseRecordId() {
                return adviceUseRecordId;
            }

            public void setAdviceUseRecordId(int adviceUseRecordId) {
                this.adviceUseRecordId = adviceUseRecordId;
            }

            public String getServiceDuration() {
                return serviceDuration;
            }

            public void setServiceDuration(String serviceDuration) {
                this.serviceDuration = serviceDuration;
            }

            public String getRemainingDuration() {
                return remainingDuration;
            }

            public void setRemainingDuration(String remainingDuration) {
                this.remainingDuration = remainingDuration;
            }

            public String getIsTimeOut() {
                return isTimeOut;
            }

            public void setIsTimeOut(String isTimeOut) {
                this.isTimeOut = isTimeOut;
            }

            public String getIsExist() {
                return isExist;
            }

            public void setIsExist(String isExist) {
                this.isExist = isExist;
            }
        }
    }
}
