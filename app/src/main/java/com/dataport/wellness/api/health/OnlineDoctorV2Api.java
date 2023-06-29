package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;
import java.util.List;

public class OnlineDoctorV2Api implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/service/xiaodu/queryDoctorPageV2";
    }

    @NonNull
    @Override
    public String getHost() {
        return BotConstants.JK_URL;
    }

    private String idcard;
    private Integer pageNo;//页码 一开始
    private Integer pageSize;//每页条数

    public OnlineDoctorV2Api(String idcard, Integer pageNo, Integer pageSize) {
        this.idcard = idcard;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public static class Bean implements Serializable {

        private int videoSum;
        private int publicReamin;
        private List<DoctorListDTO> doctorList;
        private int videoReamin;
        private int vioceSum;
        private int vioceReamin;
        private int publicSum;
        private int totalElements;

        public int getVideoSum() {
            return videoSum;
        }

        public void setVideoSum(int videoSum) {
            this.videoSum = videoSum;
        }

        public int getPublicReamin() {
            return publicReamin;
        }

        public void setPublicReamin(int publicReamin) {
            this.publicReamin = publicReamin;
        }

        public List<DoctorListDTO> getDoctorList() {
            return doctorList;
        }

        public void setDoctorList(List<DoctorListDTO> doctorList) {
            this.doctorList = doctorList;
        }

        public int getVideoReamin() {
            return videoReamin;
        }

        public void setVideoReamin(int videoReamin) {
            this.videoReamin = videoReamin;
        }

        public int getVioceSum() {
            return vioceSum;
        }

        public void setVioceSum(int vioceSum) {
            this.vioceSum = vioceSum;
        }

        public int getVioceReamin() {
            return vioceReamin;
        }

        public void setVioceReamin(int vioceReamin) {
            this.vioceReamin = vioceReamin;
        }

        public int getPublicSum() {
            return publicSum;
        }

        public void setPublicSum(int publicSum) {
            this.publicSum = publicSum;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(int totalElements) {
            this.totalElements = totalElements;
        }

        public static class DoctorListDTO implements  Serializable{
            private long id;
            private String doctorCode;
            private String doctorName;
            private String titleName;
            private Object introduction;
            private String sex;
            private String doctorUrl;
            private String goodat;
            private String idcardno;
            private String doctorPhone;
            private String deptName;
            private String institutionName;
            private int serviceNum;
            private boolean startVoice;
            private boolean startVideo;

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getDoctorCode() {
                return doctorCode;
            }

            public void setDoctorCode(String doctorCode) {
                this.doctorCode = doctorCode;
            }

            public String getDoctorName() {
                return doctorName;
            }

            public void setDoctorName(String doctorName) {
                this.doctorName = doctorName;
            }

            public String getTitleName() {
                return titleName;
            }

            public void setTitleName(String titleName) {
                this.titleName = titleName;
            }

            public Object getIntroduction() {
                return introduction;
            }

            public void setIntroduction(Object introduction) {
                this.introduction = introduction;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getDoctorUrl() {
                return doctorUrl;
            }

            public void setDoctorUrl(String doctorUrl) {
                this.doctorUrl = doctorUrl;
            }

            public String getGoodat() {
                return goodat;
            }

            public void setGoodat(String goodat) {
                this.goodat = goodat;
            }

            public String getIdcardno() {
                return idcardno;
            }

            public void setIdcardno(String idcardno) {
                this.idcardno = idcardno;
            }

            public String getDoctorPhone() {
                return doctorPhone;
            }

            public void setDoctorPhone(String doctorPhone) {
                this.doctorPhone = doctorPhone;
            }

            public String getDeptName() {
                return deptName;
            }

            public void setDeptName(String deptName) {
                this.deptName = deptName;
            }

            public String getInstitutionName() {
                return institutionName;
            }

            public void setInstitutionName(String institutionName) {
                this.institutionName = institutionName;
            }

            public int getServiceNum() {
                return serviceNum;
            }

            public void setServiceNum(int serviceNum) {
                this.serviceNum = serviceNum;
            }

            public boolean isStartVoice() {
                return startVoice;
            }

            public void setStartVoice(boolean startVoice) {
                this.startVoice = startVoice;
            }

            public boolean isStartVideo() {
                return startVideo;
            }

            public void setStartVideo(boolean startVideo) {
                this.startVideo = startVideo;
            }
        }
    }
}
