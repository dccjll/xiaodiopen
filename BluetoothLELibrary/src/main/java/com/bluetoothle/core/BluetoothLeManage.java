package com.bluetoothle.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import com.bluetoothle.core.connect.BLEConnect;
import com.bluetoothle.core.connect.OnBLEConnectListener;
import com.bluetoothle.core.findService.OnBLEFindServiceListener;
import com.bluetoothle.core.openNotification.OnBLEOpenNotificationListener;
import com.bluetoothle.core.response.OnBLEResponseListener;
import com.bluetoothle.core.scan.BLEScan;
import com.bluetoothle.core.scan.OnBLEScanListener;
import com.bluetoothle.core.writeData.OnBLEWriteDataListener;
import com.bluetoothle.util.BLELogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dessmann on 16/10/14.
 * 蓝牙低功耗管理
 */

public class BluetoothLeManage {

    private final static String TAG = BluetoothLeManage.class.getSimpleName();
    private Boolean receiveBLEData = true;//是否接收设备返回的数据
    private static List<BluetoothGatt> connectedBluetoothGattList = new ArrayList<>();//当前已连接的设备服务器

    private Integer currentFindServiceCound;//当前找服务次数
    private Integer currentOpenNotificationCount;//当前打开通知次数

    private byte[] data;//发送的总数据包字节数组

    private BluetoothAdapter bluetoothAdapter;//本地蓝牙适配器
    private String targetDeviceAddress;//目前设备地址
    private List<String> targetDeviceAddressList;//目标设备地址列表
    private UUID[] serviceUUIDs;//设备的UUID,uuids=2 则不接受设备返回的数据, uuids=5 则接收设备返回的数据
    private Integer timeoutScanBLE;//扫描蓝牙超时时间

    private Integer currentScanCount;//当前扫描次数
    private BLEScan bleScan;//蓝牙扫描管理器
    private OnBLEScanListener onBLEScanListener;//蓝牙扫描监听器
    private OnBLEScanListener onBLEScanListener_ = new OnBLEScanListener() {//临时的蓝牙扫描监听器
        @Override
        public void foundDevice(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            if(onBLEConnectListener != null){

                return;
            }
            onBLEScanListener.foundDevice(bluetoothDevice, rssi, scanRecord);
        }

        @Override
        public void scanFinish(List<Map<String, Object>> bluetoothDeviceList) {
            if(bluetoothDeviceList.size() == 0){
                onBLEScanListener.scanFail(BLEConstants.ScanError.ScanError_NotFoundDevice);
                return;
            }
            onBLEScanListener.scanFinish(bluetoothDeviceList);
        }

        @Override
        public void scanFail(Integer errorCode) {
            BLELogUtil.e(TAG, "第" + ++currentScanCount + "次扫描失败,errorCode=" + errorCode);
            scan();
        }
    };

    private Integer currentConnectCount;//当前连接次数
    private BLEConnect bleConnect;//蓝牙连接管理器
    private OnBLEConnectListener onBLEConnectListener;//蓝牙连接监听器
    private OnBLEConnectListener onBLEConnectListener_ = new OnBLEConnectListener() {//临时的蓝牙连接监听器
        @Override
        public void onConnectSuccess(BluetoothGatt bluetoothGatt, int status, int newState) {

        }

        @Override
        public void onConnectFail(Integer errorCode) {

        }
    };

    private OnBLEFindServiceListener onBLEFindServiceListener;//找服务监听器
    private OnBLEOpenNotificationListener onBLEOpenNotificationListener;//打开通知监听器
    private OnBLEWriteDataListener onBLEWriteDataListener;//写数据监听器
    private OnBLEResponseListener onBLEResponseListener;//接收数据监听器

    private void initScan(BluetoothAdapter bluetoothAdapter, final String targetDeviceAddress, final List<String> targetDeviceAddressList, final UUID[] serviceUUIDs, Integer timeoutScanBLE, final OnBLEScanListener onBLEScanListener){
        this.bluetoothAdapter = bluetoothAdapter;
        this.targetDeviceAddress = targetDeviceAddress;
        this.targetDeviceAddressList = targetDeviceAddressList;
        this.serviceUUIDs = serviceUUIDs;
        this.timeoutScanBLE = timeoutScanBLE;
        this.onBLEScanListener = onBLEScanListener;
        bleScan = new BLEScan(bluetoothAdapter, targetDeviceAddress, targetDeviceAddressList, serviceUUIDs, timeoutScanBLE, onBLEScanListener_);
    }

    /**
     * 蓝牙任务管理器构造器
     * @param bluetoothAdapter  本地蓝牙适配器
     * @param targetDeviceAddress   目前设备地址
     * @param targetDeviceAddressList   目标设备地址列表
     * @param serviceUUIDs  设备的UUID,uuids=2 则不接受设备返回的数据, uuids=5 则接收设备返回的数据
     * @param timeoutScanBLE    扫描蓝牙超时时间
     * @param onBLEScanListener 扫描监听器
     */
    public void BluetoothLeManage(BluetoothAdapter bluetoothAdapter, final String targetDeviceAddress, final List<String> targetDeviceAddressList, final UUID[] serviceUUIDs, Integer timeoutScanBLE, final OnBLEScanListener onBLEScanListener) {
        initScan(bluetoothAdapter, targetDeviceAddress, targetDeviceAddressList, serviceUUIDs, timeoutScanBLE, onBLEScanListener);
    }

    /**
     * 蓝牙任务管理器构造器
     * @param bluetoothAdapter  本地蓝牙适配器
     * @param targetDeviceAddress   目前设备地址
     * @param targetDeviceAddressList   目标设备地址列表
     * @param serviceUUIDs  设备的UUID,uuids=2 则不接受设备返回的数据, uuids=5 则接收设备返回的数据
     * @param timeoutScanBLE    扫描蓝牙超时时间
     * @param onBLEConnectListener 连接监听器
     */
    public void BluetoothLeManage(BluetoothAdapter bluetoothAdapter, final String targetDeviceAddress, final List<String> targetDeviceAddressList, final UUID[] serviceUUIDs, Integer timeoutScanBLE, final OnBLEConnectListener onBLEConnectListener) {
        initScan(bluetoothAdapter, targetDeviceAddress, targetDeviceAddressList, serviceUUIDs, timeoutScanBLE, onBLEScanListener);
        this.onBLEConnectListener = onBLEConnectListener;
    }

    /**
     * 扫描周围的蓝牙设备
     */
    public void scan(){
        if(currentScanCount ++ == BluetoothLeConfig.maxScanCount){
            onBLEScanListener.scanFail(BLEConstants.ScanError.ScanError_NotFoundDevice);
            return;
        }
        bleScan.scan();
    }

    /**
     * 连接蓝牙设备
     */
    public void connect(){

    }

}
