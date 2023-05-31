package com.dataport.wellness.http;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.BodyType;

/**
 *    author : lb
 *    desc   : 服务器配置
 */
public class RequestServer implements IRequestServer {

    @Override
    public String getHost() {
        return BotConstants.XD_URL;
    }

//    @Override
//    public BodyType getBodyType() {
//        // 参数以 Json 格式提交（默认是表单）
//        return BodyType.JSON;
//    }

}