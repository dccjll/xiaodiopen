package com.bluetoothle.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.bluetoothle.util.BLELogUtil;

/**
 * Created by dessmann on 16/7/27.
 * 底层蓝牙扫描结果监听类
 */
public class BluetoothLeScanCallback implements BluetoothAdapter.LeScanCallback {

    private final static String tag = BluetoothLeScanCallback.class.getSimpleName();

    private Handler mBLEHandler;
    public BluetoothLeScanCallback(Handler mBLEHandler) {
        this.mBLEHandler = mBLEHandler;
    }

    private OnLeScanListener onLeScanListener;

    public OnLeScanListener getOnLeScanListener() {
        return onLeScanListener;
    }

    public interface OnLeScanListener{
        void foundDevice(BluetoothDevice device, int rssi, byte[] scanRecord);
        void notFoundDevice();
        void scanError(String error);
    }

    public BluetoothLeScanCallback(OnLeScanListener onLeScanListener) {
        this.onLeScanListener = onLeScanListener;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        BLELogUtil.d(tag, "找到附近的设备,mac=" + device.getAddress());
        if(mBLEHandler != null){
            mBLEHandler.obtainMessage(BluetoothLeManage2.MSG_BLUETOOTHLESERVICE_SCAN_FOUND_DEVICE, device).sendToTarget();
        }else if(onLeScanListener != null){
            onLeScanListener.foundDevice(device, rssi, scanRecord);
        }

    }
}
