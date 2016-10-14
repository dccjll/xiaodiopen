package com.bluetoothle.init;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by dessmann on 16/10/14.
 * 蓝牙初始化监听器
 */

public interface OnInitListener {
    void onInitSuccess(BluetoothAdapter bluetoothAdapter);
    void onInitFail(Integer errorCode);
}
