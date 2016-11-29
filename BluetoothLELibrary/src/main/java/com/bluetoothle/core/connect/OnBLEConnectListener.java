package com.bluetoothle.core.connect;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;

import com.bluetoothle.core.BLEGattCallback;

/**
 * Created by dessmann on 16/10/14.
 * 蓝牙连接监听器
 */

public interface OnBLEConnectListener {
    void onConnectSuccess(BluetoothGatt bluetoothGatt, int status, int newState, BLEGattCallback bleGattCallback);
    void onConnectFail(String errorCode);
}
