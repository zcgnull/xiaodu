package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.BodyType;

/**
 * 环境监测告警数据处理
 */
public class DeviceEnvProcessApi implements IRequestApi, IRequestServer {



    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/hjjc/xiaoDuWarn/processWarn";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.JK_URL;
    }

    private String processEnd;//处理端
    private String processEquipmentNo;//处理设备编号
    private String processWay;//处理方式(1上门处理 2远程处理 3关闭报警 4默认)
    private String processWayOverview;//处理方式描述
    private String processName;//处理人
    private String processTime;//处理时间（yyyy-MM-dd HH:mm:ss）
    private Long recordId;//记录id
    private String processState;//处理状态（1未处理 2已处理 3误报）

    public DeviceEnvProcessApi(String processEnd, String processEquipmentNo, String processWay, String processWayOverview, String processName, String processTime, Long recordId, String processState) {
        this.processEnd = processEnd;
        this.processEquipmentNo = processEquipmentNo;
        this.processWay = processWay;
        this.processWayOverview = processWayOverview;
        this.processName = processName;
        this.processTime = processTime;
        this.recordId = recordId;
        this.processState = processState;
    }

    @NonNull
    @Override
    public BodyType getBodyType() {
        return BodyType.JSON;
    }
}
