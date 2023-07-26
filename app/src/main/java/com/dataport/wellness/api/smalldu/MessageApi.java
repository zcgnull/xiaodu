package com.dataport.wellness.api.smalldu;

import androidx.annotation.NonNull;

import com.dataport.wellness.been.MessageBean;
import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;
import java.util.List;

public class MessageApi implements IRequestApi, IRequestServer {
    @NonNull
    @Override
    public String getApi() {
        return "app-api/wenxin/message/page";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.XD_URL;
    }

    private int pageNo;
    private int pageSize;
    private String userId;
    private String messageType;

    public MessageApi(int pageNo, int pageSize, String userId, String messageType) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.userId = userId;
        this.messageType = messageType;
    }

    public class Bean implements Serializable {

        private List<MessageBean> list;
        private int total;

        public List<MessageBean> getList() {
            return list;
        }

        public void setList(List<MessageBean> list) {
            this.list = list;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
