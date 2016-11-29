package com.bluetoothle.core.findService;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;

import com.bluetoothle.core.BLEConstants;
import com.bluetoothle.core.BLEGattCallback;
import com.bluetoothle.core.connect.BLEConnect;
import com.bluetoothle.util.BLELogUtil;

import java.util.List;

/**
 * Created by dessmann on 16/10/17.
 * 连接上之后寻找服务
 */

public class BLEFindService {

    private final static String TAG = BLEFindService.class.getSimpleName();
    private BluetoothGatt bluetoothGatt;//蓝牙连接服务器
    private OnBLEFindServiceListener onBLEFindServiceListener;//找服务器监听器
    private BLEGattCallback bleGattCallback;//蓝牙连接状态管理器

    /**
     * gatt服务器找服务监听器
     */
    public interface OnGattBLEFindServiceListener{
        void onFindServiceSuccess(BluetoothGatt bluetoothGatt, int status, List<BluetoothGattService> bluetoothGattServices);
        void onFindServiceFail(String errorCode);
    }

    /**
     * 找服务
     * @param bluetoothGatt  蓝牙连接服务器
     * @param onBLEFindServiceListener 找服务监听器
     */
    public BLEFindService(BluetoothGatt bluetoothGatt, BLEGattCallback bleGattCallback, OnBLEFindServiceListener onBLEFindServiceListener) {
        this.bluetoothGatt = bluetoothGatt;
        this.bleGattCallback = bleGattCallback;
        this.onBLEFindServiceListener = onBLEFindServiceListener;
    }

    /**
     * 找服务
     */
    public void findService(){
        if(onBLEFindServiceListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        if(bluetoothGatt == null){
            onBLEFindServiceListener.onFindServiceFail(BLEConstants.Error.BluetoothGatt);
            return;
        }
        if(bleGattCallback == null){
            onBLEFindServiceListener.onFindServiceFail(BLEConstants.Error.BluetoothGattCallBack);
            return;
        }
        bleGattCallback.registerOnGattBLEFindServiceListener(
                new OnGattBLEFindServiceListener() {
                    @Override
                    public void onFindServiceSuccess(BluetoothGatt bluetoothGatt, int status, List<BluetoothGattService> bluetoothGattServices) {
                        onBLEFindServiceListener.onFindServiceSuccess(bluetoothGatt, status, bluetoothGattServices, bleGattCallback);
                    }

                    @Override
                    public void onFindServiceFail(String errorCode) {
                        onBLEFindServiceListener.onFindServiceFail(errorCode);
                    }
                }
        );
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        bluetoothGatt.discoverServices();
                    }
                }
        ).start();
    }
}
