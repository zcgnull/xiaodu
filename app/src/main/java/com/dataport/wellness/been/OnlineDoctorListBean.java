package com.dataport.wellness.been;

import java.io.Serializable;

public class OnlineDoctorListBean implements Serializable {
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
