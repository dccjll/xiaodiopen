package com.bluetoothle.core.scan;

import android.bluetooth.BluetoothDevice;

import java.util.List;
import java.util.Map;

/**
 * Created by dessmann on 16/10/14.
 * 蓝牙扫描监听器
 */

public interface OnBLEScanListener {
    void foundDevice(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord);
    void scanFinish(List<Map<String, Object>> bluetoothDeviceList);
    void scanFail(Integer errorCode);
}
