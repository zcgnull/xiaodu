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
    private long binderId;
    public DeviceEnvApi(long binderId, Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.binderId = binderId;
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

        public class DeviceEnvListDTO implements Serializable{
            private int id;
            private String deviceName;
            private String alarmReason;
            private String alarmTime;

            private String readTime;

            public String getReadTime() {
                return readTime;
            }

            public void setReadTime(String readTime) {
                this.readTime = readTime;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getDeviceName() {
                return deviceName;
            }

            public void setDeviceName(String deviceName) {
                this.deviceName = deviceName;
            }

            public String getAlarmReason() {
                return alarmReason;
            }

            public void setAlarmReason(String alarmReason) {
                this.alarmReason = alarmReason;
            }

            public String getAlarmTime() {
                return alarmTime;
            }

            public void setAlarmTime(String alarmTime) {
                this.alarmTime = alarmTime;
            }
        }
    }
}
