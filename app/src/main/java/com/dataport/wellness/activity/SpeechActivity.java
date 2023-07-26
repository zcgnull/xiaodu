package com.dataport.wellness.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.dataport.wellness.adapter.SpeechAdapter;
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
import com.dataport.wellness.utils.AuthUtil;
import com.dataport.wellness.utils.AutoCheck;
import com.dataport.wellness.utils.BotConstants;
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
import java.util.List;
import java.util.Map;

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
    private ImageView ivVoice;
    private List<MessageBean> msgList = new ArrayList<>();
    private List<MessageTypeApi.Bean> msgTypeList = new ArrayList<>();
    private String sn;
    private String msgType;
    private boolean isAnswering = false;
    private boolean webSocketStatus = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        initView();
        initPermission();
        initSpeech();
    }

    private void initView() {
        findViewById(R.id.ln_back).setOnClickListener(v -> finish());
        sendTv = findViewById(R.id.tv_send);
        contentRv = findViewById(R.id.rv_speech);
        ivVoice = findViewById(R.id.iv_voice);
        GlideApp.with(this).asGif().load(R.mipmap.voice).into(ivVoice);

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
        findViewById(R.id.iv_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        isAnswering = true;
        String sendMsg = "";
        EventBean eventBean = GsonFactory.getSingletonGson().fromJson(params, EventBean.class);
        sendMsg = eventBean.getBest_result();
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
    }

    private void setTv(String params) {
        String sendMsg = "";
        EventBean eventBean = GsonFactory.getSingletonGson().fromJson(params, EventBean.class);
        sendMsg = eventBean.getBest_result();
        sendTv.setText(sendMsg);
    }

    /**
     * 接收消息
     */
    private void getWebSocket(String params) {
        webSocketStatus = true;
        WebSocketGetBean getBean = GsonFactory.getSingletonGson().fromJson(params, WebSocketGetBean.class);
        runOnUiThread(() -> sendTv.setText("你可以对我说：“小度小度，我要咨询”"));
        if (!getBean.getType().equals("AUTH_TOKEN_RESPONSE")) {
            isAnswering = false;
            msgList.remove(msgList.size() - 1);
            MessageBean messageBean = GsonFactory.getSingletonGson().fromJson(getBean.getBody().getMessage(), MessageBean.class);
            msgList.add(messageBean);
            BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
            BotSdk.getInstance().speakRequest(messageBean.getContent());
            runOnUiThread(() -> {
                adapter.setList(msgList);
                contentRv.scrollToPosition(msgList.size() - 1);
            });
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
                            if (result.getData().getList().size() == 0) {
//                                noData.setVisibility(View.VISIBLE);
                            } else {
//                                noData.setVisibility(View.GONE);
                                msgList.addAll(result.getData().getList());
                            }
                            adapter.setList(msgList);
                            contentRv.scrollToPosition(adapter.getItemCount() - 1);
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
                            msgType = result.getData().get(0).getValue();
                            getMessage(1, sn, msgType);
                        } else {
                            Toast.makeText(getApplicationContext(), "获取绘画模型失败，请退出重试", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void showCloseDialog() {
        webSocketStatus = false;
        sendTv.setText("对话超时、是否继续,可以对我说：“小度小度，继续”，或者可以对我说：“小度小度，取消”");
        ivVoice.setVisibility(View.GONE);
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
        BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
        BotSdk.getInstance().speakRequest("对话超时、是否继续,可以对我说：“小度小度，继续”，或者可以对我说：“小度小度，取消”");
        // 消息对话框
        new MessageDialog.Builder(this)
                // 标题可以不用填写
                .setTitle("提示")
                // 内容必须要填写
                .setMessage("对话超时、是否继续")
                // 确定按钮文本
                .setConfirm("继续")
                // 设置 null 表示不显示取消按钮
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
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "`````````````````````````onDestroy");
        closeConnect();
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        asr.unregisterListener(this);
        asr = null;
        BotSdk.getInstance().triggerDuerOSCapacity(BotMessageProtocol.DuerOSCapacity.AI_DUER_SHOW_INTERRPT_TTS, null);
        BotMessageListener.getInstance().removeCallback(this);
        BotSdk.getInstance().setDialogStateListener(null);
    }

    //    -------------------------------------websocket------------------------------------------------

    /**
     * 初始化websocket
     */
    public void initWebSocket() {
        URI uri = URI.create(BotConstants.WEB_SOCKET_URL + "websocket/wenxin?userId=" + sn);
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
//        client.setConnectionLostTimeout(110 * 1000);
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
    private void initSpeech() {
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this);
        asrStart();
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name: " + name;
        if (webSocketStatus) {
            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
                sendTv.setText("语音识别中...");
                ivVoice.setVisibility(View.VISIBLE);
            }
            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)) {
                if (!isAnswering) {
                    sendTv.setText("你可以对我说：“小度小度，我要咨询”");
                }
                ivVoice.setVisibility(View.GONE);
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
                    setTv(params);
                } else if (params.contains("\"final_result\"")) {
                    // 一句话的最终识别结果
                    logTxt += ", 最终识别结果：" + params;
                    sendWebSocket(params);
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

    private void asrStart() {
        final String[] stLog = {""};
        Map<String, Object> params = AuthUtil.getParam();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event
        // 基于SDK集成2.1 设置识别参数
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        // params.put(SpeechConstant.NLU, "enable");
        // params.put(SpeechConstant.BDS_ASR_ENABLE_LONG_SPEECH, true);//长语音  优先级高于VAD_ENDPOINT_TIMEOUT
        // params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音

        // params.put(SpeechConstant.IN_FILE, "res:///com/baidu/android/voicedemo/16k_test.pcm");
//         params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        params.put(SpeechConstant.VAD, "10");
        // params.put(SpeechConstant.PID, 1537); // 中文输入法模型，有逗号

        /* 语音自训练平台特有参数 */
        // params.put(SpeechConstant.PID, 8002);
        // 语音自训练平台特殊pid，8002：模型类似开放平台 1537  具体是8001还是8002，看自训练平台页面上的显示
        // params.put(SpeechConstant.LMID,1068);
        // 语音自训练平台已上线的模型ID，https://ai.baidu.com/smartasr/model
        // 注意模型ID必须在你的appId所在的百度账号下
        /* 语音自训练平台特有参数 */

        /* 测试InputStream*/
        // InFileStream.setContext(this);
        // params.put(SpeechConstant.IN_FILE,
        // "#com.baidu.aip.asrwakeup3.core.inputstream.InFileStream.createMyPipedInputStream()");

        // 请先使用如‘在线识别’界面测试和生成识别参数。 params同ActivityRecog类中myRecognizer.start(params);
        // 复制此段可以自动检测错误
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
        asr.send(event, json, null, 0, 0);
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
        }

    }

    @Override
    public void handleIntent(BotIntent intent, String customData) {

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
        if (dialogState.name().equals("TTS_FINISH")) {
            // TODO: 2023/7/25
            asrStart();
        }
    }


}
