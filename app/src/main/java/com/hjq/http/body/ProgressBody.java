package com.hjq.http.body;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyUtils;
import com.hjq.http.lifecycle.HttpLifecycleManager;
import com.hjq.http.listener.OnUpdateListener;
import com.hjq.http.request.HttpRequest;
import com.hjq.http.EasyLog;

import java.io.IOException;

import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/08/15
 *    desc   : RequestBody 包装类（用于获取上传进度）
 */
public class ProgressBody extends WrapperBody {

    private final HttpRequest<?> mHttpRequest;
    private final OnUpdateListener<?> mListener;
    private final LifecycleOwner mLifecycleOwner;

    /** 总字节数 */
    private long mTotalByte;
    /** 已上传字节数 */
    private long mUpdateByte;
    /** 上传进度值 */
    private int mUpdateProgress;

    public ProgressBody(HttpRequest<?> httpRequest, RequestBody body, LifecycleOwner lifecycleOwner, OnUpdateListener<?> listener) {
        super(body);
        mHttpRequest = httpRequest;
        mLifecycleOwner = lifecycleOwner;
        mListener = listener;
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        mTotalByte = contentLength();
        sink = Okio.buffer(new WrapperSink(sink));
        getRequestBody().writeTo(sink);
        sink.flush();
    }

    private class WrapperSink extends ForwardingSink {

        public WrapperSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            mUpdateByte += byteCount;

            EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), ProgressBody.this::callOnProgress);
        }
    }

    private void callOnProgress() {
        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mLifecycleOwner)) {
            mListener.onByte(mTotalByte, mUpdateByte);
        }
        int progress = EasyUtils.getProgressProgress(mTotalByte, mUpdateByte);
        // 只有上传进度发生改变的时候才回调此方法，避免引起不必要的 View 重绘
        if (progress != mUpdateProgress) {
            mUpdateProgress = progress;
            if (mListener != null && HttpLifecycleManager.isLifecycleActive(mLifecycleOwner)) {
                mListener.onProgress(progress);
            }
            EasyLog.printLog(mHttpRequest, "Uploading in progress, uploaded: " +
                    mUpdateByte + " / " + mTotalByte +
                    ", progress: " + progress + "%");
        }
    }
}