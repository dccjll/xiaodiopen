package com.bluetoothle.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.bluetoothle.core.BLEConstants;
import com.bluetoothle.util.BLELogUtil;
import com.bluetoothle.util.BLEStringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by dessmann on 16/10/14.
 * 蓝牙扫描
 */

public class BLEScan {

    private BluetoothAdapter bluetoothAdapter;//本地蓝牙适配器
    private Integer timeoutScanBLE = 5*1000;//扫描蓝牙默认超时时间
    private String targetDeviceAddress;//目前设备地址
    private List<String> targetDeviceAddressList;//目标设备地址列表
    private UUID[] serviceUUIDs;
    private OnBLEScanListener onBLEScanListener;

    private List<BluetoothDevice> foundDeviceList = new ArrayList<>();
    private Boolean isScaning = false;
    private Handler scanHandler;
    private Runnable scanRunnable;

    private BluetoothAdapter.LeScanCallback leScanCallback;

    /**
     * 扫描周围的设备,不确定mac地址,扫描到超时为止,逐个返回
     * @param bluetoothAdapter  蓝牙适配器
     * @param timeoutScanBLE    扫描蓝牙默认超时时间
     * @param onBLEScanListener 扫描监听器
     */
    public BLEScan(BluetoothAdapter bluetoothAdapter, Integer timeoutScanBLE, final OnBLEScanListener onBLEScanListener) {
        if(onBLEScanListener == null){
            BLELogUtil.e("没有设置扫描回调接口");
            return;
        }
        this.bluetoothAdapter = bluetoothAdapter;
        if(timeoutScanBLE != null && timeoutScanBLE > 0){
            this.timeoutScanBLE = timeoutScanBLE;
        }
        this.onBLEScanListener = onBLEScanListener;

        leScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                synchronized (BLEScan.this) {
                    if(!foundDeviceList.contains(device)){
                        foundDeviceList.add(device);
                    }
                    if(!BLEStringUtil.isEmpty(targetDeviceAddress)){
                        if(device.getAddress().equalsIgnoreCase(targetDeviceAddress)){
                            scanControl(false);
                            onBLEScanListener.foundDevice(device);
                            return;
                        }
                    }else if(targetDeviceAddressList != null && targetDeviceAddressList.size() > 0){
                        if(targetDeviceAddressList.contains(device.getAddress())){
                            scanControl(false);
                            onBLEScanListener.foundDevice(device);
                            return;
                        }
                    }else{
                        onBLEScanListener.foundDevice(device);
                        return;
                    }
                }
            }
        };

        scanHandler = new Handler();
        scanRunnable = new Runnable() {
            @Override
            public void run() {
                scanControl(false);
                if(BLEStringUtil.isEmpty(targetDeviceAddress) && (targetDeviceAddressList == null || targetDeviceAddressList.size() == 0)){
                    onBLEScanListener.scanFail(BLEConstants.ScanError.ScanError_NotFoundDevice);
                    return;
                }
                onBLEScanListener.scanFinish(foundDeviceList);
            }
        };
    }

    /**
     * 扫描周围的设备,不确定mac地址,扫描到超时为止,逐个返回,指定UUID
     * @param bluetoothAdapter  蓝牙适配器
     * @param timeoutScanBLE    扫描蓝牙默认超时时间
     * @param serviceUUIDs      指定uuid搜索
     * @param onBLEScanListener 扫描监听器
     */
    public BLEScan(BluetoothAdapter bluetoothAdapter, Integer timeoutScanBLE, UUID[] serviceUUIDs, OnBLEScanListener onBLEScanListener) {
        this(bluetoothAdapter, timeoutScanBLE, onBLEScanListener);
        if(serviceUUIDs == null || serviceUUIDs.length == 0){
            onBLEScanListener.scanFail(BLEConstants.ScanError.ScanError_errorServiceUUIDs);
            return;
        }
        this.serviceUUIDs = serviceUUIDs;
    }

    /**
     * 扫描周围的设备,确定mac地址,扫描到即停止扫描,立即返回
     * @param bluetoothAdapter  蓝牙适配器
     * @param timeoutScanBLE    扫描蓝牙默认超时时间
     * @param targetDeviceAddress   mac地址
     * @param onBLEScanListener  扫描监听器
     */
    public BLEScan(BluetoothAdapter bluetoothAdapter, Integer timeoutScanBLE, String targetDeviceAddress, OnBLEScanListener onBLEScanListener){
        this(bluetoothAdapter, timeoutScanBLE, onBLEScanListener);
        if(targetDeviceAddress == null || targetDeviceAddress.split(":").length != 6){
            onBLEScanListener.scanFail(BLEConstants.ScanError.ScanError_errorMacAddress);
            return;
        }
        this.targetDeviceAddress = targetDeviceAddress;
    }

    /**
     * 扫描周围的设备,确定mac地址,扫描到即停止扫描,立即返回,指定UUID
     * @param bluetoothAdapter  蓝牙适配器
     * @param timeoutScanBLE    扫描蓝牙默认超时时间
     * @param targetDeviceAddress   mac地址
     * @param serviceUUIDs  指定uuid搜索
     * @param onBLEScanListener  扫描监听器
     */
    public BLEScan(BluetoothAdapter bluetoothAdapter, Integer timeoutScanBLE, String targetDeviceAddress, UUID[] serviceUUIDs, OnBLEScanListener onBLEScanListener){
        this(bluetoothAdapter, timeoutScanBLE, onBLEScanListener);
        if(targetDeviceAddress == null || targetDeviceAddress.split(":").length != 6){
            onBLEScanListener.scanFail(BLEConstants.ScanError.ScanError_errorMacAddress);
            return;
        }
        if(serviceUUIDs == null || serviceUUIDs.length == 0){
            onBLEScanListener.scanFail(BLEConstants.ScanError.ScanError_errorServiceUUIDs);
            return;
        }
        this.targetDeviceAddress = targetDeviceAddress;
        this.serviceUUIDs = serviceUUIDs;
    }

    /**
     * 扫描周围的设备,确定mac地址列表,扫描到即停止扫描,立即返回
     * @param bluetoothAdapter  蓝牙适配器
     * @param timeoutScanBLE    扫描蓝牙默认超时时间
     * @param targetDeviceAddressList mac地址列表
     * @param onBLEScanListener 扫描监听器
     */
    public BLEScan(BluetoothAdapter bluetoothAdapter, Integer timeoutScanBLE, List<String> targetDeviceAddressList, OnBLEScanListener onBLEScanListener){
        this(bluetoothAdapter, timeoutScanBLE, onBLEScanListener);
        if(targetDeviceAddressList == null || targetDeviceAddressList.size() == 0){
            onBLEScanListener.scanFail(BLEConstants.ScanError.ScanError_errorMacAddressList);
            return;
        }
        for(String mac : targetDeviceAddressList){
            if(mac == null || mac.split(":").length != 6){
                onBLEScanListener.scanFail(BLEConstants.ScanError.ScanError_errorMacAddressList);
                return;
            }
        }
        this.targetDeviceAddressList = targetDeviceAddressList;
    }

    /**
     * 扫描周围的设备,确定mac地址列表,扫描到即停止扫描,立即返回
     * @param bluetoothAdapter  蓝牙适配器
     * @param timeoutScanBLE    扫描蓝牙默认超时时间
     * @param targetDeviceAddressList mac地址列表
     * @param serviceUUIDs  指定uuid搜索
     * @param onBLEScanListener 扫描监听器
     */
    public BLEScan(BluetoothAdapter bluetoothAdapter, Integer timeoutScanBLE, List<String> targetDeviceAddressList, UUID[] serviceUUIDs, OnBLEScanListener onBLEScanListener){
        this(bluetoothAdapter, timeoutScanBLE, onBLEScanListener);
        if(targetDeviceAddressList == null || targetDeviceAddressList.size() == 0){
            onBLEScanListener.scanFail(BLEConstants.ScanError.ScanError_errorMacAddressList);
            return;
        }
        for(String mac : targetDeviceAddressList){
            if(mac == null || mac.split(":").length != 6){
                onBLEScanListener.scanFail(BLEConstants.ScanError.ScanError_errorMacAddressList);
                return;
            }
        }
        if(serviceUUIDs == null || serviceUUIDs.length == 0){
            onBLEScanListener.scanFail(BLEConstants.ScanError.ScanError_errorServiceUUIDs);
            return;
        }
        this.targetDeviceAddressList = targetDeviceAddressList;
        this.serviceUUIDs = serviceUUIDs;
    }

    /**
     * 扫描设备
     */
    public void scan(){
        scanHandler.postDelayed(scanRunnable, timeoutScanBLE);
        foundDeviceList.clear();
        scanControl(true);
    }

    /**
     * 扫描控制,scanFlag=true 开始扫描 scanFlag=false 停止扫描
     * @param scanFlag
     */
    private void scanControl(Boolean scanFlag){
        if(scanFlag){
            if(isScaning){
                onBLEScanListener.scanFail(BLEConstants.ScanError.ScanError_isScaning);
                return;
            }
            isScaning = true;
            if(serviceUUIDs == null){
                bluetoothAdapter.startLeScan(leScanCallback);
            }else{
                bluetoothAdapter.startLeScan(serviceUUIDs, leScanCallback);
            }
        }else{
            bluetoothAdapter.stopLeScan(leScanCallback);
            scanHandler.removeCallbacks(scanRunnable);
            isScaning = false;
        }
    }
}
