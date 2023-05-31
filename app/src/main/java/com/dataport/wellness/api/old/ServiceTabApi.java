package com.dataport.wellness.api.old;

import androidx.annotation.NonNull;

import com.dataport.wellness.utils.BotConstants;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

import java.io.Serializable;
import java.util.List;

public class ServiceTabApi implements IRequestApi, IRequestServer {

    @NonNull
    @Override
    public String getApi() {
        return "biz/ne/mjzx/b/a/treeDatas";
    }


    @NonNull
    @Override
    public String getHost() {
        return BotConstants.YZ_URL;
    }

    private String pid;

    public ServiceTabApi(String pid) {
        this.pid = pid;
    }

    public static class Bean implements Serializable {

        public Bean(String id, String name, List<ChildDTO> child) {
            this.id = id;
            this.name = name;
            this.child = child;
        }

        private String id;
        private String name;
        private List<ChildDTO> child;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<ChildDTO> getChild() {
            return child;
        }

        public void setChild(List<ChildDTO> child) {
            this.child = child;
        }

        public class ChildDTO implements Serializable{
            private String name;
            private String pid;
            private String id;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPid() {
                return pid;
            }

            public void setPid(String pid) {
                this.pid = pid;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }
}
