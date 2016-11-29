package com.bluetoothle.core.writeData;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;

import com.bluetoothle.core.BLEGattCallback;

/**
 * Created by dessmann on 16/10/17.
 * 写数据监听器
 */

public interface OnBLEWriteDataListener {
    void onWriteDataFinish();
    void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status, BLEGattCallback bleGattCallback);
    void onWriteDataFail(String errorCode);
}
