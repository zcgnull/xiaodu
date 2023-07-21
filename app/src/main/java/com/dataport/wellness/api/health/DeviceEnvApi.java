package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;
import java.util.List;

/*环境监测数据*/
public class DeviceEnvApi implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/service/xiaodu/queryAdviceUerRecordPage";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.JK_URL;
    }

    private Integer pageNo;//页码 一开始
    private Integer pageSize;//每页条数
    private long userId;

    public DeviceEnvApi(long userId, Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.userId = userId;
    }

    public class Bean implements Serializable {


        private List<DeviceEnvListDTO> deviceEnvListDTOList;
        private int totalElements;

        public List<DeviceEnvListDTO> getDeviceEnvListDTOList() {
            return deviceEnvListDTOList;
        }

        public void setDeviceEnvListDTOList(List<DeviceEnvListDTO> deviceEnvListDTOList) {
            this.deviceEnvListDTOList = deviceEnvListDTOList;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(int totalElements) {
            this.totalElements = totalElements;
        }

        public class DeviceEnvListDTO implements Serializable {
            private Long id;
            private String binderName;//绑定人姓名
            private String binderPhone;//绑定人手机号
            private String equipmentName;//设备名称
            private String alarmType;//报警类型
            private String alarmAdress;//报警地址
            private String installationPosition;//安装位置
            private String alarmTime;//报警时间
            private String processState;//处理状态
            private String processTime;//处理时间
            private String processEnd;//处理端
            private String processWay;//处理方式
            private String processName;//处理人
            private String processWayOverview;//处理方式描述

            public Long getId() {
                return id;
            }

            public void setId(Long id) {
                this.id = id;
            }

            public String getBinderName() {
                return binderName;
            }

            public void setBinderName(String binderName) {
                this.binderName = binderName;
            }

            public String getBinderPhone() {
                return binderPhone;
            }

            public void setBinderPhone(String binderPhone) {
                this.binderPhone = binderPhone;
            }

            public String getEquipmentName() {
                return equipmentName;
            }

            public void setEquipmentName(String equipmentName) {
                this.equipmentName = equipmentName;
            }

            public String getAlarmType() {
                return alarmType;
            }

            public void setAlarmType(String alarmType) {
                this.alarmType = alarmType;
            }

            public String getAlarmAdress() {
                return alarmAdress;
            }

            public void setAlarmAdress(String alarmAdress) {
                this.alarmAdress = alarmAdress;
            }

            public String getInstallationPosition() {
                return installationPosition;
            }

            public void setInstallationPosition(String installationPosition) {
                this.installationPosition = installationPosition;
            }

            public String getAlarmTime() {
                return alarmTime;
            }

            public void setAlarmTime(String alarmTime) {
                this.alarmTime = alarmTime;
            }

            public String getProcessState() {
                return processState;
            }

            public void setProcessState(String processState) {
                this.processState = processState;
            }

            public String getProcessTime() {
                return processTime;
            }

            public void setProcessTime(String processTime) {
                this.processTime = processTime;
            }

            public String getProcessEnd() {
                return processEnd;
            }

            public void setProcessEnd(String processEnd) {
                this.processEnd = processEnd;
            }

            public String getProcessWay() {
                return processWay;
            }

            public void setProcessWay(String processWay) {
                this.processWay = processWay;
            }

            public String getProcessName() {
                return processName;
            }

            public void setProcessName(String processName) {
                this.processName = processName;
            }

            public String getProcessWayOverview() {
                return processWayOverview;
            }

            public void setProcessWayOverview(String processWayOverview) {
                this.processWayOverview = processWayOverview;
            }
        }
    }
}
