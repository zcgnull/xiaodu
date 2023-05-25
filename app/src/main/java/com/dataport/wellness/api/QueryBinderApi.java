package com.dataport.wellness.api;

import androidx.annotation.NonNull;

import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;
import java.util.List;

public class QueryBinderApi implements IRequestApi, IRequestServer {
    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/jkgl/xiaodu/queryBinder";
    }

    @NonNull
    @Override
    public String getHost() {
        return "http://10.20.5.21:8110/";
    }

    private String equipmentNo;

    public QueryBinderApi(String equipmentNo) {
        this.equipmentNo = equipmentNo;
    }

    public static class Bean implements Serializable {


        private List<ListDTO> list;

        public List<ListDTO> getList() {
            return list;
        }

        public void setList(List<ListDTO> list) {
            this.list = list;
        }

        public static class ListDTO implements Serializable{
            private int binderId;
            private String relation;
            private String binderPicture;
            private String binderGender;
            private String binderAge;
            private String binderBirth;
            private String binderProvince;
            private String binderCity;
            private String binderDistrict;
            private String binderAddress;
            private String binderPhone;
            private String binderName;
            private String binderIdcard;

            public int getBinderId() {
                return binderId;
            }

            public void setBinderId(int binderId) {
                this.binderId = binderId;
            }

            public String getRelation() {
                return relation;
            }

            public void setRelation(String relation) {
                this.relation = relation;
            }

            public String getBinderPicture() {
                return binderPicture;
            }

            public void setBinderPicture(String binderPicture) {
                this.binderPicture = binderPicture;
            }

            public String getBinderGender() {
                return binderGender;
            }

            public void setBinderGender(String binderGender) {
                this.binderGender = binderGender;
            }

            public String getBinderAge() {
                return binderAge;
            }

            public void setBinderAge(String binderAge) {
                this.binderAge = binderAge;
            }

            public String getBinderBirth() {
                return binderBirth;
            }

            public void setBinderBirth(String binderBirth) {
                this.binderBirth = binderBirth;
            }

            public String getBinderProvince() {
                return binderProvince;
            }

            public void setBinderProvince(String binderProvince) {
                this.binderProvince = binderProvince;
            }

            public String getBinderCity() {
                return binderCity;
            }

            public void setBinderCity(String binderCity) {
                this.binderCity = binderCity;
            }

            public String getBinderDistrict() {
                return binderDistrict;
            }

            public void setBinderDistrict(String binderDistrict) {
                this.binderDistrict = binderDistrict;
            }

            public String getBinderAddress() {
                return binderAddress;
            }

            public void setBinderAddress(String binderAddress) {
                this.binderAddress = binderAddress;
            }

            public String getBinderPhone() {
                return binderPhone;
            }

            public void setBinderPhone(String binderPhone) {
                this.binderPhone = binderPhone;
            }

            public String getBinderName() {
                return binderName;
            }

            public void setBinderName(String binderName) {
                this.binderName = binderName;
            }

            public String getBinderIdcard() {
                return binderIdcard;
            }

            public void setBinderIdcard(String binderIdcard) {
                this.binderIdcard = binderIdcard;
            }
        }
    }
}
