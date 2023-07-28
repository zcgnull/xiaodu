package com.dataport.wellness.api.health;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.CacheMode;

import java.io.Serializable;

public class IMLoginApi implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "api/jkgl/service/xiaodu/getImUserParam";
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
    private long doctorId;

    public IMLoginApi(long binderId, long doctorId) {
        this.binderId = binderId;
        this.doctorId = doctorId;
    }

    public class Bean implements Serializable {
        private TimBinderDTO timBinder;
        private TimDoctorDTO timDoctor;

        public TimBinderDTO getTimBinder() {
            return timBinder;
        }

        public void setTimBinder(TimBinderDTO timBinder) {
            this.timBinder = timBinder;
        }

        public TimDoctorDTO getTimDoctor() {
            return timDoctor;
        }

        public void setTimDoctor(TimDoctorDTO timDoctor) {
            this.timDoctor = timDoctor;
        }

        public class TimBinderDTO {
            private long binderId;
            private String binderName;
            private String binderTimId;
            private String binderTimUsersig;
            private String binderTimTime;
            private int userId;

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

            public String getBinderTimId() {
                return binderTimId;
            }

            public void setBinderTimId(String binderTimId) {
                this.binderTimId = binderTimId;
            }

            public String getBinderTimUsersig() {
                return binderTimUsersig;
            }

            public void setBinderTimUsersig(String binderTimUsersig) {
                this.binderTimUsersig = binderTimUsersig;
            }

            public String getBinderTimTime() {
                return binderTimTime;
            }

            public void setBinderTimTime(String binderTimTime) {
                this.binderTimTime = binderTimTime;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }
        }

        public class TimDoctorDTO {
            private long doctorId;
            private String doctorName;
            private String doctorTimId;
            private String doctorTimUsersig;
            private String doctorTimTime;

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

            public String getDoctorTimId() {
                return doctorTimId;
            }

            public void setDoctorTimId(String doctorTimId) {
                this.doctorTimId = doctorTimId;
            }

            public String getDoctorTimUsersig() {
                return doctorTimUsersig;
            }

            public void setDoctorTimUsersig(String doctorTimUsersig) {
                this.doctorTimUsersig = doctorTimUsersig;
            }

            public String getDoctorTimTime() {
                return doctorTimTime;
            }

            public void setDoctorTimTime(String doctorTimTime) {
                this.doctorTimTime = doctorTimTime;
            }
        }
    }
}
