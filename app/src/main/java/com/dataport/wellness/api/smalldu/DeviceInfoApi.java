package com.dataport.wellness.api.smalldu;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.CacheMode;

import java.io.Serializable;

public class DeviceInfoApi  implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "app-api/dueros/device-warehouse/V1/parse-device-info";
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
    private String sn;

    public DeviceInfoApi(String sn) {
        this.sn = sn;
    }

    public class Bean implements Serializable {

        private String sn;
        private int tenantId;

        private String tenantName;
        private boolean inWarehouse;
        private String oldUrl;
        private String healthUrl;
        private boolean consultingShow;
        private BaiduSpeechConfigDTO baiduSpeechConfig;

        public String getTenantName() {
            return tenantName;
        }

        public void setTenantName(String tenantName) {
            this.tenantName = tenantName;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public int getTenantId() {
            return tenantId;
        }

        public void setTenantId(int tenantId) {
            this.tenantId = tenantId;
        }

        public boolean isInWarehouse() {
            return inWarehouse;
        }

        public void setInWarehouse(boolean inWarehouse) {
            this.inWarehouse = inWarehouse;
        }

        public String getOldUrl() {
            return oldUrl;
        }

        public void setOldUrl(String oldUrl) {
            this.oldUrl = oldUrl;
        }

        public String getHealthUrl() {
            return healthUrl;
        }

        public void setHealthUrl(String healthUrl) {
            this.healthUrl = healthUrl;
        }

        public boolean isConsultingShow() {
            return consultingShow;
        }

        public void setConsultingShow(boolean consultingShow) {
            this.consultingShow = consultingShow;
        }

        public BaiduSpeechConfigDTO getBaiduSpeechConfig() {
            return baiduSpeechConfig;
        }

        public void setBaiduSpeechConfig(BaiduSpeechConfigDTO baiduSpeechConfig) {
            this.baiduSpeechConfig = baiduSpeechConfig;
        }

        public class BaiduSpeechConfigDTO {
            private String appKey;
            private String secretKey;
            private String appId;
            private String appName;

            public String getAppKey() {
                return appKey;
            }

            public void setAppKey(String appKey) {
                this.appKey = appKey;
            }

            public String getSecretKey() {
                return secretKey;
            }

            public void setSecretKey(String secretKey) {
                this.secretKey = secretKey;
            }

            public String getAppId() {
                return appId;
            }

            public void setAppId(String appId) {
                this.appId = appId;
            }

            public String getAppName() {
                return appName;
            }

            public void setAppName(String appName) {
                this.appName = appName;
            }
        }
    }
}
