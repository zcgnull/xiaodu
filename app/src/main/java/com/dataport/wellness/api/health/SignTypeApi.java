package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;
import java.util.List;

public class SignTypeApi implements IRequestApi, IRequestServer {
    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/jkgl/xiaodu/querySignType";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.JK_URL;
    }

    private int binderId;

    public SignTypeApi(int binderId) {
        this.binderId = binderId;
    }

    public static class Bean implements Serializable {

        private List<ListDTO> list;

        public List<ListDTO> getList() {
            return list;
        }

        public void setList(List<ListDTO> list) {
            this.list = list;
        }

        public static class ListDTO {
            private String dataTypeCode;
            private String dataTypeName;

            public String getDataTypeCode() {
                return dataTypeCode;
            }

            public void setDataTypeCode(String dataTypeCode) {
                this.dataTypeCode = dataTypeCode;
            }

            public String getDataTypeName() {
                return dataTypeName;
            }

            public void setDataTypeName(String dataTypeName) {
                this.dataTypeName = dataTypeName;
            }
        }
    }
}
