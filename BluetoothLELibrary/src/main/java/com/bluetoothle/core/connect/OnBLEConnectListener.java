package com.bluetoothle.core.connect;

import android.bluetooth.BluetoothGatt;

/**
 * Created by dessmann on 16/10/14.
 * 蓝牙连接监听器
 */

public interface OnBLEConnectListener {
    void onConnectSuccess(BluetoothGatt bluetoothGatt, int status, int newState);
    void onConnectFail(String errorCode);
}
