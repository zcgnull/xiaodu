package com.dataport.wellness.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.baidu.duer.bot.BotMessageProtocol;
import com.baidu.duer.botsdk.BotIntent;
import com.baidu.duer.botsdk.BotSdk;
import com.baidu.duer.botsdk.IDialogStateListener;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.dataport.wellness.R;
import com.dataport.wellness.activity.dialog.BaseDialog;
import com.dataport.wellness.activity.dialog.MessageDialog;
import com.dataport.wellness.activity.dialog.SettingDialog;
import com.dataport.wellness.adapter.SpeechAdapter;
import com.dataport.wellness.api.health.SignTypeApi;
import com.dataport.wellness.api.smalldu.MessageApi;
import com.dataport.wellness.api.smalldu.MessageTypeApi;
import com.dataport.wellness.been.EventBean;
import com.dataport.wellness.been.MessageBean;
import com.dataport.wellness.been.WebSocketGetBean;
import com.dataport.wellness.been.WebSocketSendBean;
import com.dataport.wellness.botsdk.BotMessageListener;
import com.dataport.wellness.botsdk.IBotIntentCallback;
import com.dataport.wellness.http.HttpIntData;
import com.dataport.wellness.http.JWebSocketClient;
import com.dataport.wellness.http.glide.GlideApp;
import com.dataport.wellness.utils.AutoCheck;
import com.dataport.wellness.utils.BotConstants;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.EasyHttp;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.http.request.HttpRequest;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SpeechActivity extends BaseActivity implements EventListener, IDialogStateListener, IBotIntentCallback {

    private static final String TAG = "SpeechActivity";
    private EventManager asr;
    private RefreshLayout refreshLayout;
    private RecyclerView contentRv;
    private int pageNum = 1;
    private int pageSize = 10;
    private SpeechAdapter adapter;
    private JWebSocketClient client;
    private TextView sendTv;
    private EditText sendEt;
    private LottieAnimationView ivVoice, ivStatus;
    private LinearLayout lnVoice, lnText;
    private List<MessageBean> msgList = new ArrayList<>();
    private List<MessageTypeApi.Bean> msgTypeList = new ArrayList<>();
    private String msgType;
    private boolean isAnswering = false;
    private boolean webSocketStatus = true;
    private boolean isJK = true;
    private boolean isIdentify = false;
    private boolean canStart = true;
    private int settingPos = 0;
    private MessageDialog.Builder closeDialog;
    private SettingDialog.Builder settingDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private MessageDialog.Builder messageDialog;

    private Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            contentRv.scrollToPosition(msgList.size() - 1);
        }
    };
    private ScheduledExecutorService mService;
    private ScheduledFuture<?> scheduledFuture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        mService = Executors.newScheduledThreadPool(1);
        sharedPreferences = getSharedPreferences("XD", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        initPermission();
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this);

    }

    private void initView() {
        findViewById(R.id.ln_back).setOnClickListener(v -> finish());
        sendTv = findViewById(R.id.tv_send);
        contentRv = findViewById(R.id.rv_speech);
        ivVoice = findViewById(R.id.iv_voice);
        ivStatus = findViewById(R.id.iv_status);
        sendEt = findViewById(R.id.et_send);
        lnVoice = findViewById(R.id.ln_bottom);
        lnText = findViewById(R.id.ln_bottom_text);
        ivVoice.setAnimation(R.raw.voice);
        ivStatus.setAnimation(R.raw.voice);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contentRv.setLayoutManager(linearLayoutManager);
        adapter = new SpeechAdapter(this);
        contentRv.setAdapter(adapter);
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            pageNum += 1;
            getMessage(2, BotConstants.SN, msgType);
        });
        findViewById(R.id.iv_setting).setOnClickListener(v -> {
            if (!isAnswering && !isIdentify) {
                BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
                List<String> data = new ArrayList<>();
                for (MessageTypeApi.Bean bean : msgTypeList) {
                    data.add(bean.getLabel());
                }
                if (null != settingDialog) {
                    settingDialog.show();
                } else {
                    settingDialog = new SettingDialog.Builder(SpeechActivity.this);
                    settingDialog.setList(data)  //设置下拉框内容
                            .setPosition(settingPos)  //设置初始 问答模型
                            .setGeek(isJK)  //设置是否使用 极客模式
                            .setWidthAndHeight(0.5f, 0.5f) //百分比
                            .setListener((dialog, position, geekModel, QAModel) -> {
                                //监听  position 选择的位置 geekModel 是否使用 极客模式 QAModel 问答模型
                                settingPos = position;
                                msgType = msgTypeList.get(position).getValue();
                                isJK = geekModel;
                                editor.putString("msgType", msgType);
                                editor.putBoolean("isJK", isJK);
                                editor.commit();
                                getMessage(1, BotConstants.SN, msgType);
                            })
                            .show();
                }
            }
        });
        findViewById(R.id.btn_send).setOnClickListener(v -> {
            if (!isAnswering && !isIdentify) {
                sendTextWeb();
            }
        });
        findViewById(R.id.iv_mic).setOnClickListener(v -> {
            if (!isAnswering && !isIdentify) {
                BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
                asrStart();
            }
        });
        initWebSocket();
        getMessageType();
        BotMessageListener.getInstance().addCallback(this);
        BotSdk.getInstance().setDialogStateListener(this);
    }

    /**
     * 发送消息
     */
    private void sendWebSocket(String params) {
        String sendMsg = "";
        EventBean eventBean = GsonFactory.getSingletonGson().fromJson(params, EventBean.class);
        sendMsg = eventBean.getOrigin_result().getResult().getWord().get(0);
        if (isJK) {
            isAnswering = true;
            sendTv.setText(sendMsg);
            WebSocketSendBean sendBean = new WebSocketSendBean();
            WebSocketSendBean.BodyDTO bodyDTO = new WebSocketSendBean.BodyDTO();
            bodyDTO.setMessage(sendMsg);
            sendBean.setBody(bodyDTO);
            sendBean.setType(msgType);
            String sendJson = new Gson().toJson(sendBean);
            sendMsg(sendJson);
            MessageBean userMsg = new MessageBean();
            userMsg.setRole("user");
            userMsg.setContent(sendMsg);
            userMsg.setCreated(System.currentTimeMillis() / 1000);
            MessageBean waitingMsg = new MessageBean();
            waitingMsg.setRole("assistant");
            waitingMsg.setContent("回答中...");
            waitingMsg.setCreated(System.currentTimeMillis() / 1000);
            msgList.add(userMsg);
            msgList.add(waitingMsg);
            adapter.setList(msgList);
            contentRv.scrollToPosition(msgList.size() - 1);
        } else {
            sendEt.setText(sendMsg);
        }
    }

    private void sendTextWeb() {
        if (sendEt.getText().toString().trim().equals(""))
            return;
        BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
        isAnswering = true;
        WebSocketSendBean sendBean = new WebSocketSendBean();
        WebSocketSendBean.BodyDTO bodyDTO = new WebSocketSendBean.BodyDTO();
        bodyDTO.setMessage(sendEt.getText().toString().trim());
        sendBean.setBody(bodyDTO);
        sendBean.setType(msgType);
        String sendJson = new Gson().toJson(sendBean);
        sendMsg(sendJson);
        MessageBean userMsg = new MessageBean();
        userMsg.setRole("user");
        userMsg.setContent(sendEt.getText().toString().trim());
        userMsg.setCreated(System.currentTimeMillis() / 1000);
        MessageBean waitingMsg = new MessageBean();
        waitingMsg.setRole("assistant");
        waitingMsg.setContent("回答中...");
        waitingMsg.setCreated(System.currentTimeMillis() / 1000);
        msgList.add(userMsg);
        msgList.add(waitingMsg);
        adapter.setList(msgList);
        contentRv.scrollToPosition(msgList.size() - 1);
    }

    private void setTv(String params) {
        String sendMsg = "";
        EventBean eventBean = GsonFactory.getSingletonGson().fromJson(params, EventBean.class);
        sendMsg = eventBean.getBest_result();
        if (isJK) {
            sendTv.setText(sendMsg);
        } else {
            sendEt.setText(sendMsg);
        }
    }

    /**
     * 接收消息
     */
    private void getWebSocket(String params) {
        webSocketStatus = true;
        WebSocketGetBean getBean = GsonFactory.getSingletonGson().fromJson(params, WebSocketGetBean.class);
        runOnUiThread(() -> {
            sendTv.setText("你可以对我说：“小度小度，我要咨询”");
            sendEt.setText("");
            sendEt.setHint("输入文字后发送");
        });
        if (!getBean.getType().equals("AUTH_TOKEN_RESPONSE")) {
            isAnswering = false;
            msgList.remove(msgList.size() - 1);
            MessageBean messageBean = GsonFactory.getSingletonGson().fromJson(getBean.getBody().getMessage(), MessageBean.class);
            msgList.add(messageBean);
            BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
            BotSdk.getInstance().speakRequest(messageBean.getContent());
            runOnUiThread(() -> {
                adapter.setList(msgList);
                contentRv.postDelayed(scrollRunnable, 500);
            });
        }
    }

    private void checkSendStatus() {
        if (isJK) {
            lnVoice.setVisibility(View.VISIBLE);
            lnText.setVisibility(View.GONE);
            asrStart();
        } else {
            lnText.setVisibility(View.VISIBLE);
            lnVoice.setVisibility(View.GONE);
        }
    }

    private void getMessage(int type, String sn, String messageType) {//type:1代表刷新2代表加载
        EasyHttp.get(this)
                .interceptor(new IRequestInterceptor() {
                    @Override
                    public void interceptArguments(@NonNull HttpRequest<?> httpRequest, @NonNull HttpParams params, @NonNull HttpHeaders headers) {
                        headers.put("tenant-id", BotConstants.TENANT_ID);
                        headers.put("Authorization", BotConstants.DEVICE_TOKEN);
                    }
                })
                .api(new MessageApi(pageNum, pageSize, sn, messageType))
                .request(new HttpCallback<HttpIntData<MessageApi.Bean>>(this) {

                    @Override
                    public void onSucceed(HttpIntData<MessageApi.Bean> result) {
                        if (type == 1) {
                            msgList.clear();
                            msgList.addAll(result.getData().getList());
                            adapter.setList(msgList);
                            contentRv.scrollToPosition(adapter.getItemCount() - 1);
                            checkSendStatus();
                        } else {
                            refreshLayout.finishRefresh();
                            if (result.getData().getList().size() == 0) {
                            } else {
                                msgList.addAll(0, result.getData().getList());
                            }
                            adapter.setList(msgList);
                        }
                    }
                });
    }

    private void getMessageType() {
        EasyHttp.get(this)
                .interceptor(new IRequestInterceptor() {
                    @Override
                    public void interceptArguments(@NonNull HttpRequest<?> httpRequest, @NonNull HttpParams params, @NonNull HttpHeaders headers) {
                        headers.put("tenant-id", BotConstants.TENANT_ID);
                        headers.put("Authorization", BotConstants.DEVICE_TOKEN);
                    }
                })
                .api(new MessageTypeApi())
                .request(new HttpCallback<HttpIntData<List<MessageTypeApi.Bean>>>(this) {

                    @Override
                    public void onSucceed(HttpIntData<List<MessageTypeApi.Bean>> result) {
                        if (result.getCode() == 0) {
                            msgTypeList = result.getData();
                            isJK = sharedPreferences.getBoolean("isJK", true);
                            String type = sharedPreferences.getString("msgType", result.getData().get(0).getValue());
                            String typeDef = sharedPreferences.getString("msgTypeDef", result.getData().get(0).getValue());
                            if (!typeDef.equals(result.getData().get(0).getValue())) {
                                msgType = result.getData().get(0).getValue();
                            } else {
                                msgType = type;
                                for (int i = 0; i < msgTypeList.size(); i++) {
                                    if (msgType.equals(msgTypeList.get(i).getValue())){
                                        settingPos = i;
                                    }
                                }
                            }
                            editor.putString("msgType", msgType);
                            editor.putString("msgTypeDef", result.getData().get(0).getValue());
                            editor.commit();
                            getMessage(1, BotConstants.SN, msgType);
                        } else {
                            Toast.makeText(getApplicationContext(), "获取会话模型失败，请退出重试", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void showCloseDialog() {
        webSocketStatus = false;
        sendTv.setText("对话超时，是否继续？可以对我说：“小度小度，继续”，或者可以对我说：“小度小度，取消”");
        ivVoice.setVisibility(View.GONE);
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
        BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
        BotSdk.getInstance().speakRequest("对话超时，是否继续？可以对我说：“小度小度，继续”，或者可以对我说：“小度小度，取消”");
        if (null != closeDialog) {
            closeDialog.show();
        } else {
            closeDialog = new MessageDialog.Builder(this);
            closeDialog.setTitle("提示")
                    .setMessage("对话超时，是否继续？")
                    .setConfirm("继续")
                    .setCancel("取消")
                    .setCanceledOnTouchOutside(false)
                    .setListener(new MessageDialog.OnListener() {

                        @Override
                        public void onConfirm(BaseDialog dialog) {
                            reconnectWs();//websocket重连
                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                            finish();
                        }
                    }).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "`````````````````````````onDestroy");
        if (scheduledFuture != null){
            scheduledFuture.cancel(true);
        }
        mService.shutdown();
        mService = null;
        editor.clear();
        sharedPreferences = null;
        messageDialog = null;
        adapter = null;
        msgList = null;
        msgTypeList = null;
        closeDialog = null;
        settingDialog = null;
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        asr.unregisterListener(this);
        asr = null;
        closeConnect();

    }

    @Override
    protected void onPause() {
        super.onPause();
        BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
        BotMessageListener.getInstance().clearCallback();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: 2023/9/4 可能会有问题
        BotMessageListener.getInstance().addCallback(this);
    }

    //    -------------------------------------websocket------------------------------------------------

    /**
     * 初始化websocket
     */
    public void initWebSocket() {
        URI uri = URI.create(BotConstants.WEB_SOCKET_URL + "websocket/wenxin?userId=" + BotConstants.SN);
        //创建websocket
        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                super.onMessage(message);
                Log.e(TAG, "websocket收到消息:" + message);
                getWebSocket(message);
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                Log.e(TAG, "websocket连接成功");
            }

            @Override
            public void onError(Exception ex) {
                super.onError(ex);
                Log.e(TAG, "websocket连接错误:" + ex);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                super.onClose(code, reason, remote);
                if (code != 1000) {
                    runOnUiThread(() -> showCloseDialog());
                }
                Log.e(TAG, "websocket断开连接：·code:" + code + "·reason:" + reason + "·remote:" + remote);
            }
        };
        String token = BotConstants.DEVICE_TOKEN.replace("Bearer ", "");
        client.addHeader("Sec-WebSocket-Protocol", token);
        //设置超时时间
        client.setConnectionLostTimeout(Integer.MAX_VALUE);
        //连接websocket
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
            Log.e(TAG, "^_^Websocket发送的消息：-----------------------------------^_^" + msg);
            if (client.isOpen()) {
                client.send(msg);
            }
        }
    }

    /**
     * 开启重连
     */
    private void reconnectWs() {
//        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.e(TAG, "开启重连");
                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 断开连接
     */
    private void closeConnect() {
        try {
            //关闭websocket
            if (null != client) {
                client.close();
            }
            //停止心跳
//            if (mHandler != null) {
//                mHandler.removeCallbacksAndMessages(null);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }

    //    -------------------------------------websocket心跳检测------------------------------------------------

//    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
//    private Handler mHandler = new Handler();
//    private Runnable heartBeatRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (client != null) {
//                if (client.isClosed()) {
//                    Log.e("心跳包检测websocket连接状态1", client.isOpen() + "/");
//                    reconnectWs();//心跳机制发现断开开启重连
//                } else {
//                    Log.e("心跳包检测websocket连接状态2", client.isOpen() + "/");
//                    sendMsg("Heartbeat");
//                }
//            } else {
//                Log.e("心跳包检测websocket连接状态重新连接", "");
//                //如果client已为空，重新初始化连接
//                client = null;
////                initSocketClient();
//            }
//            //每隔一定的时间，对长连接进行一次心跳检测
//            mHandler.postDelayed(this, HEART_BEAT_RATE);
//        }
//    };


    //    -------------------------------------语音识别------------------------------------------------
    private String lastParams = ""; //最优临时结果，没有最后的结果时使用，大部分时候不会生效
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        Log.i("zcg", "    name : " + name);
        statListener(5);
        if (params != null){
            Log.i("zcg", "   params：" + params);
        }else {
            Log.i("zcg", "params is null     " + lastParams);
            if (!"".equals(lastParams)){
                sendWebSocket(lastParams);
                lastParams = "";
            }
        }

        String logTxt = "name: " + name;
        if (webSocketStatus) {
            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
                isIdentify = true;
                if (isJK) {
                    sendTv.setText("语音识别中...");
                    ivVoice.setVisibility(View.VISIBLE);
                } else {
                    sendEt.setHint("语音识别中...");
                    ivStatus.setVisibility(View.VISIBLE);
                }
            }
            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)) {
                isIdentify = false;
                if (isJK) {
                    if (!isAnswering) {
                        sendTv.setText("你可以对我说：“小度小度，我要咨询”");
                    }
                    ivVoice.setVisibility(View.GONE);
                } else {
                    if (!isAnswering) {
                        sendEt.setHint("输入文字后发送");
                    }
                    ivStatus.setVisibility(View.GONE);
                }
            }
            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                // 引擎就绪，可以说话，一般在收到此事件后通过UI通知用户可以说话了
                // 识别相关的结果都在这里
                if (params == null || params.isEmpty()) {
                    return;
                }
                if (params.contains("\"partial_result\"")) {
                    // 一句话的临时识别结果
                    logTxt += ", 临时识别结果：" + params;
                    lastParams = params;
                    setTv(params);
                } else if (params.contains("\"final_result\"")) {
                    // 一句话的最终识别结果
                    logTxt += ", 最终识别结果：" + params;
                    sendWebSocket(params);
                    lastParams = "";
                    if (scheduledFuture != null){
                        if (!scheduledFuture.isDone()){
                            scheduledFuture.cancel(true);
                        }
                    }
                } else {
                    // 一般这里不会运行
                    logTxt += " ;params :" + params;
                    if (data != null) {
                        logTxt += " ;data length=" + data.length;
                    }
                }
            } else {
                // 识别开始，结束，音量，音频数据回调
                if (params != null && !params.isEmpty()) {
                    logTxt += " ;params :" + params;
                }
                if (data != null) {
                    logTxt += " ;data length=" + data.length;
                }
            }
        }
        Log.i(TAG, "onEvent!" + logTxt);
    }

    /**
     * 没有更多内容后，结束监听
     * @param delay 倒计时时间
     */
    private void statListener(int delay) {
        if (scheduledFuture != null){
            scheduledFuture.cancel(true);
        }
        //onCreate中有时无法创建ScheduledExecutorService
        if (mService == null){
            Log.i("zcg", "新建的线程池");
            mService = Executors.newScheduledThreadPool(1);
        }
        scheduledFuture = mService.schedule(() -> {
            Log.i("zcg", "结束识别");
            //发送停止录音事件，提前结束录音等待识别结果
            asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
        }, delay, TimeUnit.SECONDS);
    }

    private void asrStart() {
        Map<String, Object> params = new LinkedHashMap<>();
        // 基于SDK集成2.1 设置识别参数
        params.put(SpeechConstant.APP_ID, BotConstants.BaiduSpeechAppId); // 添加appId
        params.put(SpeechConstant.APP_KEY, BotConstants.BaiduSpeechAppKey); // 添加apiKey
        params.put(SpeechConstant.SECRET, BotConstants.BaiduSpeechSecretKey); // 添加secretKey
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        (new AutoCheck(getApplicationContext(), new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainErrorMessage();
                        Log.i(TAG, "AutoCheckMessage:" + message);
                    }
                }
            }
        }, false)).checkAsr(params);
        String json = null; // 可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(SpeechConstant.ASR_START, json, null, 0, 0);
        Log.i(TAG, "输入参数:" + json);
    }

    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }else {
            initView();
        }

    }

    private void gotoAudioSettingIntent() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, 321);
    }



    @Override
    public void handleIntent(BotIntent intent, String customData) {
        String intentResult = getString(R.string.result_intent) + intent.name + ",slots:" + intent.slots;
        Log.d(TAG, "handleIntent: " + intentResult);
        if (isJK) {
            if ("app_health_consultation_ask".equals(intent.name)) {
                canStart = true;
                asrStart();
            } else if ("ai.dueros.common.cancel_intent".equals(intent.name)) {
                if (null != closeDialog && closeDialog.isShowing()) {
                    closeDialog.dismiss();
                    finish();
                }
            } else if ("ai.dueros.common.continue_intent".equals(intent.name)) {
                if (null != closeDialog && closeDialog.isShowing()) {
                    closeDialog.dismiss();
                    reconnectWs();//websocket重连
                }
            } else {
                BotSdk.getInstance().speakRequest("我没有听清，请再说一遍");
            }
        }
    }

    @Override
    public void onClickLink(String url, HashMap<String, String> paramMap) {

    }

    @Override
    public void onHandleScreenNavigatorEvent(int event) {

    }



    /**
     * 当前聆听状态回调，包含
     * {@link IDialogStateListener.DialogState#IDLE} 空闲态
     * {@link IDialogStateListener.DialogState#LISTENING} 聆听中
     * {@link IDialogStateListener.DialogState#SPEAKING} 语音播报中
     * {@link IDialogStateListener.DialogState#THINKING} 语义识别中
     *
     * @param dialogState
     */
    @Override
    public void onDialogStateChanged(DialogState dialogState) {
        Log.i("监听bot状态============", dialogState.name());
        if (dialogState.name().equals("TTS_FINISH") && webSocketStatus && isJK && canStart) {
            asrStart();
        } else if (dialogState.name().equals("LISTENING")) {
            lastParams = "";
            canStart = false;
            Log.i("zcg", "取消语音识别");
            asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
            asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                initView();
            } else {
                messageDialog = new MessageDialog.Builder(this);
                messageDialog.setTitle("申请权限")
                        .setMessage("健康咨询需要申请麦克风权限")
                        .setConfirm("确认")
                        .setCancel("取消")
                        .setCanceledOnTouchOutside(false)
                        .setListener(new MessageDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog) {
                                gotoAudioSettingIntent();
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                BotSdk.getInstance().speakRequest("健康咨询需要麦克风权限才能使用");
                                finish();
                            }
                        }).show();
            }
        }else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //再次检查是否有麦克风权限
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            // 进入到这里代表没有权限.
            BotSdk.getInstance().speakRequest("健康咨询需要麦克风权限才能使用");
            this.finish();
        } else {
            initView();
        }
    }


}


