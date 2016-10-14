package com.bluetoothle.connect;

import android.bluetooth.BluetoothGatt;

/**
 * Created by dessmann on 16/10/14.
 * 蓝牙连接监听器
 */

public interface OnBLEConnectListener {
    void onConnectSuccess(BluetoothGatt bluetoothGatt);
    void onConnectFail(Integer errorCode);
}
