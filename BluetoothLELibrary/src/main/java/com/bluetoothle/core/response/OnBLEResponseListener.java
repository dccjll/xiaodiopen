package com.bluetoothle.core.response;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by dessmann on 16/10/17.
 * 蓝牙回复数据
 */

public interface OnBLEResponseListener {
    void receiveData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
    void onError(String errorCode);
}
