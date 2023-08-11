package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.CacheMode;

import java.io.Serializable;
import java.util.List;

public class DeviceContentPageApi implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/jkgl/xiaodu/queryDataRecordPage";
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
    private int equipmentBindId;
    private String dataTypeCode;
    private String beginDate;
    private String endDate;
    private Integer pageNo;//页码 一开始
    private Integer pageSize;//每页条数

    public DeviceContentPageApi(int equipmentBindId, String dataTypeCode, String beginDate, String endDate, Integer pageNo, Integer pageSize) {
        this.equipmentBindId = equipmentBindId;
        this.dataTypeCode = dataTypeCode;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public class Bean implements Serializable {

        private List<RecordListDTO> recordList;
        private Integer totalElements;

        public List<RecordListDTO> getRecordList() {
            return recordList;
        }

        public void setRecordList(List<RecordListDTO> recordList) {
            this.recordList = recordList;
        }

        public Integer getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(Integer totalElements) {
            this.totalElements = totalElements;
        }

        public class RecordListDTO implements Serializable{
            private String equipmentNo;
            private String equipmentName;
            private Integer equipmentBindId;

            private Long binderId;
            private String dbp;
            private String sbp;
            private String bpm;
            private String blk;
            private String uric;
            private String blo;
            private String asi;
            private String blvAge;
            private String dbpType;
            private String sbpType;
            private String bpmType;
            private String blkType;
            private String uricType;
            private String bloType;
            private String asiType;
            private String startTime;
            private String dataSources;
            private String pharmacySituation;
            private String dataType;
            private String gls;

            public String getGlsType() {
                return glsType;
            }

            public void setGlsType(String glsType) {
                this.glsType = glsType;
            }

            private String glsType;
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

            public Integer getEquipmentBindId() {
                return equipmentBindId;
            }

            public void setEquipmentBindId(Integer equipmentBindId) {
                this.equipmentBindId = equipmentBindId;
            }

            public Long getBinderId() {
                return binderId;
            }

            public void setBinderId(Long binderId) {
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

            public String getBlvAge() {
                return blvAge;
            }

            public void setBlvAge(String blvAge) {
                this.blvAge = blvAge;
            }

            public String getDbpType() {
                return dbpType;
            }

            public void setDbpType(String dbpType) {
                this.dbpType = dbpType;
            }

            public String getSbpType() {
                return sbpType;
            }

            public void setSbpType(String sbpType) {
                this.sbpType = sbpType;
            }

            public String getBpmType() {
                return bpmType;
            }

            public void setBpmType(String bpmType) {
                this.bpmType = bpmType;
            }

            public String getBlkType() {
                return blkType;
            }

            public void setBlkType(String blkType) {
                this.blkType = blkType;
            }

            public String getUricType() {
                return uricType;
            }

            public void setUricType(String uricType) {
                this.uricType = uricType;
            }

            public String getBloType() {
                return bloType;
            }

            public void setBloType(String bloType) {
                this.bloType = bloType;
            }

            public String getAsiType() {
                return asiType;
            }

            public void setAsiType(String asiType) {
                this.asiType = asiType;
            }
        }
    }
}

