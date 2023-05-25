package com.dataport.wellness.api;

import androidx.annotation.NonNull;

import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;
import java.util.List;

public class EquipmentListApi  implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/jkgl/xiaodu/queryEquipmentList";
    }

    @NonNull
    @Override
    public String getHost() {
        return "http://10.20.5.21:8110/";
    }

    private String dataTypeCode;
    private int binderId;

    public EquipmentListApi(String dataTypeCode, int binderId) {
        this.dataTypeCode = dataTypeCode;
        this.binderId = binderId;
    }

    public static class Bean implements Serializable {

        private List<ListDTO> list;

        public List<ListDTO> getList() {
            return list;
        }

        public void setList(List<ListDTO> list) {
            this.list = list;
        }

        public static class ListDTO {
            private String equipmentName;
            private Object factoryName;
            private Object equipmentPicture;
            private Object equipmentModel;
            private String equipmentNo;
            private int binderId;
            private int equipmentBindId;

            public String getEquipmentName() {
                return equipmentName;
            }

            public void setEquipmentName(String equipmentName) {
                this.equipmentName = equipmentName;
            }

            public Object getFactoryName() {
                return factoryName;
            }

            public void setFactoryName(Object factoryName) {
                this.factoryName = factoryName;
            }

            public Object getEquipmentPicture() {
                return equipmentPicture;
            }

            public void setEquipmentPicture(Object equipmentPicture) {
                this.equipmentPicture = equipmentPicture;
            }

            public Object getEquipmentModel() {
                return equipmentModel;
            }

            public void setEquipmentModel(Object equipmentModel) {
                this.equipmentModel = equipmentModel;
            }

            public String getEquipmentNo() {
                return equipmentNo;
            }

            public void setEquipmentNo(String equipmentNo) {
                this.equipmentNo = equipmentNo;
            }

            public int getBinderId() {
                return binderId;
            }

            public void setBinderId(int binderId) {
                this.binderId = binderId;
            }

            public int getEquipmentBindId() {
                return equipmentBindId;
            }

            public void setEquipmentBindId(int equipmentBindId) {
                this.equipmentBindId = equipmentBindId;
            }
        }
    }
}
