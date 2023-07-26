package com.dataport.wellness.been;

import java.io.Serializable;

public class WebSocketSendBean implements Serializable {

    private String type;
    private BodyDTO body;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BodyDTO getBody() {
        return body;
    }

    public void setBody(BodyDTO body) {
        this.body = body;
    }

    public static class BodyDTO implements Serializable{
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
