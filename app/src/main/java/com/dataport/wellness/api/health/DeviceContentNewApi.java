package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.CacheMode;

import java.io.Serializable;
import java.util.List;

public class DeviceContentNewApi implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/jkgl/xiaodu/queryDataRecordByBinderId";
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
    private long binderId;
    private String dataTypeCode;
    private String beginDate;
    private String endDate;

    public DeviceContentNewApi(long binderId, String dataTypeCode, String beginDate, String endDate) {
        this.binderId = binderId;
        this.dataTypeCode = dataTypeCode;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public class Bean implements Serializable {

        private List<ListDTO> list;

        public List<ListDTO> getList() {
            return list;
        }

        public void setList(List<ListDTO> list) {
            this.list = list;
        }

        public class ListDTO {
            private String equipmentNo;
            private String equipmentName;
            private String factoryName;
            private long equipmentPicture;
            private String equipmentBindId;
            private long binderId;
            private String dbp;
            private String sbp;
            private String bpm;
            private String blk;
            private String uric;
            private String blo;
            private String asi;
            private String startTime;
            private String dataSources;
            private String pharmacySituation;
            private String dataType;
            private String gls;
            private String measureSituation;

            public String getEquipmentNo() {
                return equipmentNo;
            }

            public void setEquipmentNo(String equipmentNo) {
                this.equipmentNo = equipmentNo;
            }

            public String getEquipmentName() {
                return equipmentName;
            }

            public void setEquipmentName(String equipmentName) {
                this.equipmentName = equipmentName;
            }

            public String getEquipmentBindId() {
                return equipmentBindId;
            }

            public void setEquipmentBindId(String equipmentBindId) {
                this.equipmentBindId = equipmentBindId;
            }

            public long getBinderId() {
                return binderId;
            }

            public void setBinderId(long binderId) {
                this.binderId = binderId;
            }

            public String getDbp() {
                return dbp;
            }

            public void setDbp(String dbp) {
                this.dbp = dbp;
            }

            public String getSbp() {
                return sbp;
            }

            public void setSbp(String sbp) {
                this.sbp = sbp;
            }

            public String getBpm() {
                return bpm;
            }

            public void setBpm(String bpm) {
                this.bpm = bpm;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public String getDataSources() {
                return dataSources;
            }

            public void setDataSources(String dataSources) {
                this.dataSources = dataSources;
            }

            public String getPharmacySituation() {
                return pharmacySituation;
            }

            public void setPharmacySituation(String pharmacySituation) {
                this.pharmacySituation = pharmacySituation;
            }

            public String getDataType() {
                return dataType;
            }

            public void setDataType(String dataType) {
                this.dataType = dataType;
            }

            public String getGls() {
                return gls;
            }

            public void setGls(String gls) {
                this.gls = gls;
            }

            public String getMeasureSituation() {
                return measureSituation;
            }

            public void setMeasureSituation(String measureSituation) {
                this.measureSituation = measureSituation;
            }

            public String getFactoryName() {
                return factoryName;
            }

            public void setFactoryName(String factoryName) {
                this.factoryName = factoryName;
            }

            public long getEquipmentPicture() {
                return equipmentPicture;
            }

            public void setEquipmentPicture(long equipmentPicture) {
                this.equipmentPicture = equipmentPicture;
            }

            public String getBlk() {
                return blk;
            }

            public void setBlk(String blk) {
                this.blk = blk;
            }

            public String getUric() {
                return uric;
            }

            public void setUric(String uric) {
                this.uric = uric;
            }

            public String getBlo() {
                return blo;
            }

            public void setBlo(String blo) {
                this.blo = blo;
            }

            public String getAsi() {
                return asi;
            }

            public void setAsi(String asi) {
                this.asi = asi;
            }


        }
    }
}
