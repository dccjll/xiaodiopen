package com.bluetoothle.core.openNotification;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattDescriptor;

import com.bluetoothle.core.BLEGattCallback;

/**
 * Created by dessmann on 16/10/17.
 * 打开通知
 */

public interface OnBLEOpenNotificationListener {
    void onOpenNotificationSuccess(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status, BLEGattCallback bleGattCallback);
    void onOpenNotificationFail(String errorCode);
}
