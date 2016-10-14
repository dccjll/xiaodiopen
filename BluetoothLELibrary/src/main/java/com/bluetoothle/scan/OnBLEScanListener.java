package com.bluetoothle.scan;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * Created by dessmann on 16/10/14.
 * 蓝牙扫描监听器
 */

public interface OnBLEScanListener {
    void foundDevice(BluetoothDevice bluetoothDevice);
    void scanFinish(List<BluetoothDevice> bluetoothDeviceList);
    void scanFail(Integer errorCode);
}
