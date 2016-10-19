package com.bluetoothle.core.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.bluetoothle.core.BLEConstants;
import com.bluetoothle.core.BLEUtil;
import com.bluetoothle.util.BLELogUtil;
import com.bluetoothle.util.BLEStringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dessmann on 16/10/14.
 * 蓝牙扫描
 */

public class BLEScan {

    private BluetoothAdapter bluetoothAdapter;//本地蓝牙适配器
    private String targetDeviceAddress;//目前设备地址
    private List<String> targetDeviceAddressList;//目标设备地址列表
    private UUID[] serviceUUIDs;//指定uuid搜索
    private Integer timeoutScanBLE = 5*1000;//扫描蓝牙默认超时时间
    private OnBLEScanListener onBLEScanListener;//扫描监听器

    private List<Map<String, Object>> foundDeviceList = new ArrayList<>();
    private Boolean isScaning = false;
    private Handler scanHandler;
    private Runnable scanRunnable;

    private BluetoothAdapter.LeScanCallback leScanCallback;

    /**
     * 扫描周围的设备
     * @param bluetoothAdapter  蓝牙适配器
     * @param targetDeviceAddress   目标设备mac地址
     * @param targetDeviceAddressList   目标设备mac地址列表
     * @param serviceUUIDs    指定uuid搜索
     * @param timeoutScanBLE    扫描蓝牙超时时间
     * @param onBLEScanListener 扫描监听器
     */
    public BLEScan(BluetoothAdapter bluetoothAdapter, final String targetDeviceAddress, final List<String> targetDeviceAddressList, final UUID[] serviceUUIDs, Integer timeoutScanBLE, final OnBLEScanListener onBLEScanListener) {
        this.bluetoothAdapter = bluetoothAdapter;
        if(timeoutScanBLE != null && timeoutScanBLE > 0){
            this.timeoutScanBLE = timeoutScanBLE;
        }
        if(BLEStringUtil.isNotEmpty(targetDeviceAddress) && targetDeviceAddress.split(":").length == 6){
            this.targetDeviceAddress = targetDeviceAddress;
        }
//        boolean targetDeviceAddressListCheck = true;
//        for(String mac : targetDeviceAddressList){
//            if(mac == null || mac.split(":").length != 6){
//                targetDeviceAddressListCheck = false;
//                break;
//            }
//        }
//        if(targetDeviceAddressListCheck){
//            this.targetDeviceAddressList = targetDeviceAddressList;
//        }
        if(BLEUtil.checkTargetAddressList(targetDeviceAddressList)){
            this.targetDeviceAddressList = targetDeviceAddressList;
        }
        if(serviceUUIDs != null && serviceUUIDs.length >= 0){
            this.serviceUUIDs = serviceUUIDs;
        }
        this.onBLEScanListener = onBLEScanListener;

        leScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                synchronized (BLEScan.this) {
                    boolean contains = false;
                    for(Map<String, Object> entry : foundDeviceList){
                        if(((BluetoothDevice)entry.get("device")).getAddress().equalsIgnoreCase(device.getAddress())){
                            contains = true;
                            break;
                        }
                    }
                    if(!contains){
                        Map<String, Object> deviceAttrMap = new HashMap<>();
                        deviceAttrMap.put("device", device);
                        deviceAttrMap.put("rssi", rssi);
                        deviceAttrMap.put("scanRecord", scanRecord);
                        foundDeviceList.add(deviceAttrMap);
                    }
                    if(!BLEStringUtil.isEmpty(targetDeviceAddress)){
                        if(device.getAddress().equalsIgnoreCase(targetDeviceAddress)){
                            scanControl(false);
                            onBLEScanListener.foundDevice(device, rssi, scanRecord);
                        }
                    }else if(targetDeviceAddressList.size() > 0){
                        if(targetDeviceAddressList.contains(device.getAddress())){
                            scanControl(false);
                            onBLEScanListener.foundDevice(device, rssi, scanRecord);
                        }
                    }else{
                        onBLEScanListener.foundDevice(device, rssi, scanRecord);
                    }
                }
            }
        };

        scanHandler = new Handler();
        scanRunnable = new Runnable() {
            @Override
            public void run() {
                scanControl(false);
                if(BLEStringUtil.isNotEmpty(targetDeviceAddress) || (targetDeviceAddressList.size() > 0)){
                    onBLEScanListener.scanFail(BLEConstants.Error.NotFoundDeviceError);
                    return;
                }
                onBLEScanListener.scanFinish(foundDeviceList);
            }
        };
    }

    /**
     * 扫描设备
     */
    public void scan(){
        if(onBLEScanListener == null){
            BLELogUtil.e("没有设置扫描回调接口");
            return;
        }
        if(bluetoothAdapter == null){
            onBLEScanListener.scanFail(BLEConstants.Error.CheckBluetoothAdapterError);
            return;
        }
        scanHandler.postDelayed(scanRunnable, timeoutScanBLE);
        foundDeviceList.clear();
        try {
            scanControl(true);
        } catch (Exception e) {
            e.printStackTrace();
            onBLEScanListener.scanFail(BLEConstants.Error.OccurScanningError);
        }
    }

    /**
     * 扫描控制,scanFlag=true 开始扫描 scanFlag=false 停止扫描
     * @param scanFlag
     */
    private void scanControl(Boolean scanFlag){
        if(scanFlag){
            if(isScaning){
                onBLEScanListener.scanFail(BLEConstants.Error.BLEScanningError);
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
