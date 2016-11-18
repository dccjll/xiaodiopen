package com.bluetoothle.core.writeData;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by dessmann on 16/10/17.
 * 写数据监听器
 */

public interface OnBLEWriteDataListener {
    void onWriteDataFinish();
    void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
    void onWriteDataFail(String errorCode);
}
