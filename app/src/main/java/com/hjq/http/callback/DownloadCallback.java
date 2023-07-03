package com.hjq.http.callback;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hjq.http.EasyUtils;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.exception.FileMd5Exception;
import com.hjq.http.lifecycle.HttpLifecycleManager;
import com.hjq.http.request.HttpRequest;
import com.hjq.http.EasyLog;
import com.hjq.http.exception.NullBodyException;
import com.hjq.http.exception.ResponseException;
import com.hjq.http.listener.OnDownloadListener;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/11/25
 *    desc   : 下载接口回调
 */
public final class DownloadCallback extends BaseCallback {

    /** 请求配置 */
    @NonNull
    private final HttpRequest<?> mHttpRequest;

    /** 文件 MD5 正则表达式 */
    private static final String FILE_MD5_REGEX = "^[\\w]{32}$";

    /** 保存的文件 */
    private File mFile;

    /** 校验的 MD5 */
    private String mMd5;

    /** 下载监听回调 */
    private OnDownloadListener mListener;

    /** 下载总字节 */
    private long mTotalByte;

    /** 已下载字节 */
    private long mDownloadByte;

    /** 下载进度 */
    private int mDownloadProgress;

    public DownloadCallback(@NonNull HttpRequest<?> request) {
        super(request);
        mHttpRequest = request;
    }

    public DownloadCallback setFile(File file) {
        mFile = file;
        return this;
    }

    public DownloadCallback setMd5(String md5) {
        mMd5 = md5;
        return this;
    }

    public DownloadCallback setListener(OnDownloadListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    protected void onStart(Call call) {
        mHttpRequest.getRequestHandler().downloadStart(mHttpRequest, mFile);
        EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), this::dispatchDownloadStartCallback);
    }

    @Override
    protected void onResponse(Response response) throws Exception {
        // 打印请求耗时时间
        EasyLog.printLog(mHttpRequest, "RequestConsuming：" +
                (response.receivedResponseAtMillis() - response.sentRequestAtMillis()) + " ms");

        IRequestInterceptor interceptor = mHttpRequest.getRequestInterceptor();
        if (interceptor != null) {
            response = interceptor.interceptResponse(mHttpRequest, response);
        }

        if (!response.isSuccessful())  {
            throw new ResponseException("The request failed, responseCode: " +
                    response.code() + ", message: " + response.message(), response);
        }

        // 如果没有指定文件的 md5 值
        if (mMd5 == null) {
            // 获取响应头中的文件 MD5 值
            String md5 = response.header("Content-MD5");
            // 这个 md5 值必须是文件的 md5 值
            if (md5 != null && md5.matches(FILE_MD5_REGEX)) {
                mMd5 = md5;
            }
        }

        File parentFile = mFile.getParentFile();
        if (parentFile != null) {
            EasyUtils.createFolder(parentFile);
        }
        ResponseBody body = response.body();
        if (body == null) {
            throw new NullBodyException("The response body is empty");
        }

        mTotalByte = body.contentLength();
        if (mTotalByte < 0) {
            mTotalByte = 0;
        }

        // 如果这个文件已经下载过，并且经过校验 MD5 是同一个文件的话，就直接回调下载成功监听
        if (!TextUtils.isEmpty(mMd5) && mFile.isFile() &&
                mMd5.equalsIgnoreCase(EasyUtils.getFileMd5(EasyUtils.openFileInputStream(mFile)))) {
            // 文件已存在，跳过下载
            EasyLog.printLog(mHttpRequest, mFile.getPath() + " file already exists, skip download");
            EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), () -> dispatchDownloadCompleteCallback(true));
            return;
        }

        int readLength;
        mDownloadByte = 0;
        byte[] bytes = new byte[8192];
        InputStream inputStream = body.byteStream();
        OutputStream outputStream = EasyUtils.openFileOutputStream(mFile);
        while ((readLength = inputStream.read(bytes)) != -1) {
            mDownloadByte += readLength;
            outputStream.write(bytes, 0, readLength);
            EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), this::dispatchDownloadProgressCallback);
        }
        EasyUtils.closeStream(inputStream);
        EasyUtils.closeStream(outputStream);

        String md5 = EasyUtils.getFileMd5(EasyUtils.openFileInputStream(mFile));
        if (!TextUtils.isEmpty(mMd5) && !mMd5.equalsIgnoreCase(md5)) {
            // 文件 MD5 值校验失败
            throw new FileMd5Exception("MD5 verify failure", md5);
        }

        // 下载成功
        mHttpRequest.getRequestHandler().downloadSucceed(mHttpRequest, response, mFile);

        EasyLog.printLog(mHttpRequest, mFile.getPath() + " download completed");
        EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), () -> dispatchDownloadCompleteCallback(false));
    }

    @Override
    protected void onFailure(final Exception e) {
        EasyLog.printThrowable(mHttpRequest, e);
        // 打印错误堆栈
        final Exception finalException = mHttpRequest.getRequestHandler().downloadFail(mHttpRequest, e);
        if (finalException != e) {
            EasyLog.printThrowable(mHttpRequest, finalException);
        }

        EasyLog.printLog(mHttpRequest, mFile.getPath() + " download error");
        EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), () -> callDownloadError(finalException));
    }

    private void dispatchDownloadStartCallback() {
        if (mListener == null || !HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            return;
        }
        mListener.onStart(mFile);
    }

    private void dispatchDownloadProgressCallback() {
        if (mListener == null || !HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            return;
        }
        mListener.onByte(mFile, mTotalByte, mDownloadByte);
        int progress = EasyUtils.getProgressProgress(mTotalByte, mDownloadByte);
        // 只有下载进度发生改变的时候才回调此方法，避免引起不必要的 View 重绘
        if (progress == mDownloadProgress) {
            return;
        }
        mDownloadProgress = progress;
        mListener.onProgress(mFile, mDownloadProgress);
        EasyLog.printLog(mHttpRequest, mFile.getPath() +
                ", downloaded: " + mDownloadByte + " / " + mTotalByte +
                ", progress: " + progress + " %");
    }

    private void dispatchDownloadCompleteCallback(boolean cache) {
        if (mListener == null || !HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            return;
        }
        mListener.onComplete(mFile, cache);
        mListener.onEnd(mFile);
    }

    private void callDownloadError(Exception e) {
        if (mListener == null || !HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            return;
        }
        mListener.onError(mFile, e);
        mListener.onEnd(mFile);
    }
}