package com.dataport.wellness.api.smalldu;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;

public class MessageTypeApi implements IRequestApi, IRequestServer {
    @NonNull
    @Override
    public String getApi() {
        return "app-api/wenxin/message/get-message-type";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.XD_URL;
    }

    public MessageTypeApi() {
    }

    public class Bean implements Serializable {

        private int id;
        private String value;
        private String label;
        private String remark;
        private int sort;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }
    }
}
