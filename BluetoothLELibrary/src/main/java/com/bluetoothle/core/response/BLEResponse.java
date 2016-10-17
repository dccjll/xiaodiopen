package com.bluetoothle.core.response;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by dessmann on 16/10/17.
 * 蓝牙回复数据
 */

public interface BLEResponse {
    void receiveData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
}
