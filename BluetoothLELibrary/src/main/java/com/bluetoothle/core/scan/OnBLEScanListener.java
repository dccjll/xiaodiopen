package com.bluetoothle.core.scan;

import android.bluetooth.BluetoothDevice;

import java.util.List;
import java.util.Map;

/**
 * Created by dessmann on 16/10/14.
 * 蓝牙扫描监听器
 */

public interface OnBLEScanListener {
    void onFoundDevice(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord);
    void onScanFinish(List<Map<String, Object>> bluetoothDeviceList);
    void onScanFail(Integer errorCode);
}
