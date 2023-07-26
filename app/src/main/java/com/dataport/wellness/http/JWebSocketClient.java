package com.dataport.wellness.http;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import javax.net.ssl.SSLParameters;

public class JWebSocketClient extends WebSocketClient {

    @Override
    protected void onSetSSLParameters(SSLParameters sslParameters) {

    }

    public JWebSocketClient(URI uri) {
        super(uri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("WebSocket", "connect");
    }

    @Override
    public void onMessage(String message) {
        Log.e("WebSocket", message);

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("WebSocket", reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.e("WebSocket", ex.toString());
    }
}
