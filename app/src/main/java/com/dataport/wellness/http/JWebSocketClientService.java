package com.dataport.wellness.http;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.dataport.wellness.R;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class JWebSocketClientService extends Service {

    public JWebSocketClient client;
    private JWebSocketClientBinder mBinder = new JWebSocketClientBinder();
    private final static int GRAY_SERVICE_ID = 1001;

    //灰色保活
    public static class GrayInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    PowerManager.WakeLock wakeLock;//锁屏唤醒

    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    @SuppressLint("InvalidWakeLockTag")
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    //用于Activity和service通讯
    public class JWebSocketClientBinder extends Binder {
        public JWebSocketClientService getService() {
            return JWebSocketClientService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
        //初始化websocket
        initSocketClient();
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //初始化websocket
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测
        acquireWakeLock();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        closeConnect();
        super.onDestroy();
    }

    NotificationManager notificationManager;

    String notificationId = "channelId";

    String notificationName = "channelName";

    private void startForegroundService() {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //创建NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification getNotification() {

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("随访包服务")
                .setContentText("随访包服务正在运行...");
        //设置Notification的ChannelID,否则不能正常显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
        Notification notification = builder.build();
        return notification;
    }


    public JWebSocketClientService() {
    }

    /**
     * 初始化websocket连接
     */
    public void initSocketClient() {
        URI uri;
        uri = URI.create("ws://10.20.1.13:48080/websocket/wenxin?userId=950745EAV663360209E9");
        Log.e("","连接的地址是************************：" + uri.toString());
        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                Log.e("","Service收到的消息：" + message);
                Intent intent = new Intent();
                intent.setAction("com.dhy.health.healthhut");
                intent.putExtra("message", message);
                intent.setPackage(getPackageName());
                sendBroadcast(intent);
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                Log.e("","websocket连接成功");
                mHandler.removeCallbacks(heartBeatRunnable);
                mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测
                if (null != client) {
                    Log.e("","第一次连接成功发送的消息：-----------------------------------" + "Heartbeat");
                    client.send("ok");
                }
            }
        };
        client.setConnectionLostTimeout(110*1000);
        connect();
    }

    /**
     * 连接websocket
     */
    private void connect() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    client.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        if (null != client) {
            if (client.isOpen()){
                client.send(msg+"");
            }else {
                reconnectWs();
            }
        }else {
            initSocketClient();
        }
    }

    /**
     * 断开连接
     */
    public void closeConnect() {
        try {
            if (null != client) {
                mHandler.removeCallbacks(heartBeatRunnable);
                client.close();
                client=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }

    //    -------------------------------------websocket心跳检测------------------------------------------------
    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (client != null) {
                Log.e("心跳包检测websocket连接状态",client.isClosed()+"/");
                if (client.isClosed()) {
                    reconnectWs();
                }
            } else {
                //如果client已为空，重新初始化连接
                client = null;
                initSocketClient();
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    //    -------------------------------------websocket心跳检测------------------------------------------------
    private static final long HEART_BEAT_RATE2 = 110 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler2 = new Handler();
    private Runnable heartBeatRunnable2 = new Runnable() {
        @Override
        public void run() {
            Log.e("","发送Heartbeat心跳包检测websocket连接状态"+"/");
            if (client != null) {
                if (!client.isClosed()){
                    sendMsg("Heartbeat");
                }else {
                    reconnectWs();
                }
//                sendMsg("Heartbeat");
            } else {
                //如果client已为空，重新初始化连接
                client = null;
                initSocketClient();
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler2.postDelayed(this, HEART_BEAT_RATE2);
        }
    };
    /**
     * 开启重连
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.e( "开启重连","");
                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
