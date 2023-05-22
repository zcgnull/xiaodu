package com.dataport.wellness.http;

import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.BodyType;

/**
 *    author : lb
 *    desc   : 服务器配置
 */
public class RequestServer implements IRequestServer {

    @Override
    public String getHost() {
        return "https://qhdmzj.dataport.com.cn/";
    }

//    @Override
//    public BodyType getBodyType() {
//        // 参数以 Json 格式提交（默认是表单）
//        return BodyType.JSON;
//    }

}