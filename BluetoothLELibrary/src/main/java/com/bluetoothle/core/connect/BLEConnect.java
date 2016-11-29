package com.bluetoothle.core.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;

import com.bluetoothle.core.BLEConstants;
import com.bluetoothle.core.BLECoreResponse;
import com.bluetoothle.core.BLEGattCallback;
import com.bluetoothle.core.BLEService;
import com.bluetoothle.core.response.OnBLEResponse;
import com.bluetoothle.util.BLELogUtil;

/**
 * Created by dessmann on 16/10/14.
 * 连接设备
 */

public class BLEConnect {

    private final static String TAG = BLEConnect.class.getSimpleName();
    private Context context;//蓝牙连接的上下文对象
    private BluetoothDevice bluetoothDevice;//需要连接的蓝牙设备
    private BLEGattCallback bleGattCallback;//连接状态回调管理器
    private BluetoothAdapter bluetoothAdapter;//当前设备蓝牙适配器
    private String targetMacAddress;//远程蓝牙设备的mac地址
    private OnBLEConnectListener onBLEConnectListener;//蓝牙连接监听器

    /**
     * 连接蓝牙服务器回调接口
     */
    public interface OnGattBLEConnectListener {
        void onConnectSuccss(BluetoothGatt bluetoothGatt, int status, int newState, BLEGattCallback bleGattCallback);
        void onConnectFail(String errorCode);
    }

    /**
     * 指定设备对象连接设备,默认最多重试连接次数
     * @param bluetoothDevice   需要连接的蓝牙设备
     * @param onBLEConnectListener 蓝牙连接监听器
     */
    public BLEConnect(BluetoothDevice bluetoothDevice, BLECoreResponse bleCoreResponse, OnBLEResponse onBLEResponse, OnBLEConnectListener onBLEConnectListener) {
        this.bluetoothDevice = bluetoothDevice;
        this.onBLEConnectListener = onBLEConnectListener;
        context = BLEService.bleService;
        if (bleGattCallback == null) {
            bleGattCallback = new BLEGattCallback();
            bleGattCallback.registerBleCoreResponse(bleCoreResponse);
            bleGattCallback.registerOnBLEResponseListener(onBLEResponse);
        }
    }

    /**
     * 指定设备MAC地址连接设备
     * @param bluetoothAdapter   当前设备蓝牙适配器
     * @param targetMacAddress   需要连接的蓝牙设备MAC地址
     * @param onBLEConnectListener 蓝牙连接监听器
     */
    public BLEConnect(BluetoothAdapter bluetoothAdapter, String targetMacAddress, BLECoreResponse bleCoreResponse, OnBLEConnectListener onBLEConnectListener) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.targetMacAddress = targetMacAddress;
        this.onBLEConnectListener = onBLEConnectListener;
        context = BLEService.bleService;
        if (bleGattCallback == null) {
            bleGattCallback = new BLEGattCallback();
            bleGattCallback.registerBleCoreResponse(bleCoreResponse);
        }
    }

    /**
     * 连接设备
     */
    public void connect(){
        if(onBLEConnectListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        if(context == null){
            onBLEConnectListener.onConnectFail(BLEConstants.Error.BLEConext);
            return;
        }
        if(bluetoothDevice == null && (bluetoothAdapter == null || targetMacAddress == null || targetMacAddress.split(":").length != 6)){
            onBLEConnectListener.onConnectFail(BLEConstants.Error.Device_Address);
            return;
        }
        bleGattCallback.registerOnGattConnectListener(
                new OnGattBLEConnectListener() {
                    @Override
                    public void onConnectSuccss(BluetoothGatt bluetoothGatt, int status, int newState, BLEGattCallback bleGattCallback) {
                        onBLEConnectListener.onConnectSuccess(bluetoothGatt, status, newState, bleGattCallback);
                    }

                    @Override
                    public void onConnectFail(String errorCode) {
                        onBLEConnectListener.onConnectFail(errorCode);
                    }
                }
        );
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (bluetoothDevice != null) {
                            connectDevice(bluetoothDevice);
                        }else{
                            connectAddress();
                        }
                    }
                }
        ).start();
    }

    /**
     * 连接方法重载,通过蓝牙设备连接
     * @param bluetoothDevice
     */
    private void connectDevice(BluetoothDevice bluetoothDevice){
        if(bluetoothDevice == null){
            onBLEConnectListener.onConnectFail(BLEConstants.Error.Device_Address);
            return;
        }
        bluetoothDevice.connectGatt(context, false, bleGattCallback);
    }

    /**
     * 连接方法重载,通过蓝牙设备mac地址连接
     */
    private void connectAddress(){
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(targetMacAddress);
        connectDevice(bluetoothDevice);
    }
}
