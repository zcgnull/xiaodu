package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.CacheMode;

import java.io.Serializable;
import java.util.List;

public class OnlineRecordApi implements IRequestApi, IRequestServer {

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
    @NonNull
    @Override
    public CacheMode getCacheMode() {
        return CacheMode.NO_CACHE;
    }
    private Integer pageNo;//页码 一开始
    private Integer pageSize;//每页条数
    private long binderId;

    public OnlineRecordApi(long binderId,Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.binderId = binderId;
    }

    public class Bean implements Serializable {


        private List<AdviceRecordListDTO> adviceRecordList;
        private int totalElements;

        public List<AdviceRecordListDTO> getAdviceRecordList() {
            return adviceRecordList;
        }

        public void setAdviceRecordList(List<AdviceRecordListDTO> adviceRecordList) {
            this.adviceRecordList = adviceRecordList;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(int totalElements) {
            this.totalElements = totalElements;
        }

        public class AdviceRecordListDTO implements Serializable{
            private int id;
            private int servicePackageId;
            private String servicePackageName;
            private String serviceCode;
            private int serviceOrderRelationalId;
            private String useState;
            private long binderId;
            private String binderName;
            private long doctorId;
            private String doctorName;
            private String adviceQuestionDescription;
            private String adviceQuestionUrl;
            private String orderNumber;
            private String completeTime;
            private String createTime;
            private String serviceDuration;
            private String connectionDuration;
            private String limitDuration;
            private String isLimit;

            public String getIsLimit() {
                return isLimit;
            }

            public void setIsLimit(String isLimit) {
                this.isLimit = isLimit;
            }

            public String getLimitDuration() {
                return limitDuration;
            }

            public void setLimitDuration(String limitDuration) {
                this.limitDuration = limitDuration;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getServicePackageId() {
                return servicePackageId;
            }

            public void setServicePackageId(int servicePackageId) {
                this.servicePackageId = servicePackageId;
            }

            public String getServicePackageName() {
                return servicePackageName;
            }

            public void setServicePackageName(String servicePackageName) {
                this.servicePackageName = servicePackageName;
            }

            public String getServiceCode() {
                return serviceCode;
            }

            public void setServiceCode(String serviceCode) {
                this.serviceCode = serviceCode;
            }

            public int getServiceOrderRelationalId() {
                return serviceOrderRelationalId;
            }

            public void setServiceOrderRelationalId(int serviceOrderRelationalId) {
                this.serviceOrderRelationalId = serviceOrderRelationalId;
            }

            public String getUseState() {
                return useState;
            }

            public void setUseState(String useState) {
                this.useState = useState;
            }

            public long getBinderId() {
                return binderId;
            }

            public void setBinderId(long binderId) {
                this.binderId = binderId;
            }

            public String getBinderName() {
                return binderName;
            }

            public void setBinderName(String binderName) {
                this.binderName = binderName;
            }

            public long getDoctorId() {
                return doctorId;
            }

            public void setDoctorId(long doctorId) {
                this.doctorId = doctorId;
            }

            public String getDoctorName() {
                return doctorName;
            }

            public void setDoctorName(String doctorName) {
                this.doctorName = doctorName;
            }

            public String getAdviceQuestionDescription() {
                return adviceQuestionDescription;
            }

            public void setAdviceQuestionDescription(String adviceQuestionDescription) {
                this.adviceQuestionDescription = adviceQuestionDescription;
            }

            public String getAdviceQuestionUrl() {
                return adviceQuestionUrl;
            }

            public void setAdviceQuestionUrl(String adviceQuestionUrl) {
                this.adviceQuestionUrl = adviceQuestionUrl;
            }

            public String getOrderNumber() {
                return orderNumber;
            }

            public void setOrderNumber(String orderNumber) {
                this.orderNumber = orderNumber;
            }



            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getCompleteTime() {
                return completeTime;
            }

            public void setCompleteTime(String completeTime) {
                this.completeTime = completeTime;
            }

            public String getServiceDuration() {
                return serviceDuration;
            }

            public void setServiceDuration(String serviceDuration) {
                this.serviceDuration = serviceDuration;
            }

            public String getConnectionDuration() {
                return connectionDuration;
            }

            public void setConnectionDuration(String connectionDuration) {
                this.connectionDuration = connectionDuration;
            }
        }
    }
}
