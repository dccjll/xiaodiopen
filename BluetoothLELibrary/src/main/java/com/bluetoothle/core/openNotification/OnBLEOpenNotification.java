package com.bluetoothle.core.openNotification;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by dessmann on 16/10/17.
 * 打开通知
 */

public interface OnBLEOpenNotification {
    void onOpenNotificationSuccess(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);
    void onOpenNotificationFail(Integer errorCode);
}
