package com.dataport.wellness.api.smalldu;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;
import java.util.List;

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

    private String sn;

    public DeviceInfoApi(String sn) {
        this.sn = sn;
    }

    public class Bean implements Serializable {

        private String sn;

        public long getTenantId() {
            return tenantId;
        }

        public void setTenantId(long tenantId) {
            this.tenantId = tenantId;
        }

        private long tenantId;
        private boolean inWarehouse;

        private String oldUrl;
        private String healthUrl;

        private boolean consultingShow;

        public boolean isConsultingShow() {
            return consultingShow;
        }

        public void setConsultingShow(boolean consultingShow) {
            this.consultingShow = consultingShow;
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

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }



        public boolean isInWarehouse() {
            return inWarehouse;
        }

        public void setInWarehouse(boolean inWarehouse) {
            this.inWarehouse = inWarehouse;
        }
    }
}
