package com.bluetoothle.core.request;

/**
 * Created by dessmann on 16/10/14.
 * 请求操作结果监听
 */

public interface OnRequestListener {
    void onRequestSuccss(Request request);
    void onRequestFail(Integer errorCode);
}
