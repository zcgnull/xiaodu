package com.dataport.wellness.api.smalldu;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;

public class DeviceTokenApi implements IRequestApi, IRequestServer {
    @NonNull
    @Override
    public String getApi() {
        return "admin-api/system/oauth2/token";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.XD_URL;
    }

    private String grant_type;
    private String refresh_token;
    private String client_id;
    private String client_secret;

    public DeviceTokenApi(String grant_type, String refresh_token, String client_id, String client_secret) {
        this.grant_type = grant_type;
        this.refresh_token = refresh_token;
        this.client_id = client_id;
        this.client_secret = client_secret;
    }

    public class Bean implements Serializable {

        private String scope;
        private String access_token;
        private String refresh_token;
        private String token_type;
        private int expires_in;

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getRefresh_token() {
            return refresh_token;
        }

        public void setRefresh_token(String refresh_token) {
            this.refresh_token = refresh_token;
        }

        public String getToken_type() {
            return token_type;
        }

        public void setToken_type(String token_type) {
            this.token_type = token_type;
        }

        public int getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(int expires_in) {
            this.expires_in = expires_in;
        }
    }
}
