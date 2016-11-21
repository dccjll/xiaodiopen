package com.bluetoothle.core.response;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.bluetoothle.core.BLECoreResponse;

/**
 * Created by dessmann on 16/10/17.
 * 蓝牙接收数据
 */

public abstract class OnBLEResponse {
    private BLECoreResponse bleCoreResponse;
    public abstract  void receiveData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
    public abstract  void onError(String errorCode);

    public BLECoreResponse getBleCoreResponse() {
        return bleCoreResponse;
    }

    public void setBleCoreResponse(BLECoreResponse bleCoreResponse) {
        this.bleCoreResponse = bleCoreResponse;
    }
}
