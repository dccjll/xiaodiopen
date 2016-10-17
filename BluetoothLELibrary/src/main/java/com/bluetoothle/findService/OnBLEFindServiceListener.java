package com.bluetoothle.findService;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * Created by dessmann on 16/10/17.
 * 找服务监听接口
 */

public interface OnBLEFindServiceListener {
    void onFindServiceSuccess(BluetoothGatt bluetoothGatt, int status, List<BluetoothGattService> bluetoothGattServices);
    void onFindServiceFail(Integer errorCode);
}
