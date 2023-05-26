package com.dataport.wellness.api;

import androidx.annotation.NonNull;

import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;

public class DeviceInfoApi  implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "app-api/dueros/device-warehouse/parse-device-info";
    }

    @NonNull
    @Override
    public String getHost() {
        return "http://10.20.1.13:48080/";
    }

    private String deviceId;
    private String apiAccessToken;

    public DeviceInfoApi(String deviceId, String apiAccessToken) {
        this.deviceId = deviceId;
        this.apiAccessToken = apiAccessToken;
    }

    public static class Bean implements Serializable {

        private String sn;
        private int tenantId;
        private boolean inWarehouse;

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
    }
}
