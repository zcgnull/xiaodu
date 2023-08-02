package com.dataport.wellness.been;


import java.io.Serializable;
import java.util.List;

public class OnLineDoctorBean implements Serializable {
    private int videoSum;
    private int publicReamin;
    private List<OnlineDoctorListBean> doctorList;
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

    public List<OnlineDoctorListBean> getDoctorList() {
        return doctorList;
    }

    public void setDoctorList(List<OnlineDoctorListBean> doctorList) {
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


}
