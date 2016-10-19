package com.bluetoothle.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;

import com.bluetoothle.core.connect.BLEConnect;
import com.bluetoothle.core.connect.OnBLEConnectListener;
import com.bluetoothle.core.findService.BLEFindService;
import com.bluetoothle.core.findService.OnBLEFindServiceListener;
import com.bluetoothle.core.openNotification.BLEOpenNotification;
import com.bluetoothle.core.openNotification.OnBLEOpenNotificationListener;
import com.bluetoothle.core.response.OnBLEResponseListener;
import com.bluetoothle.core.scan.BLEScan;
import com.bluetoothle.core.scan.OnBLEScanListener;
import com.bluetoothle.core.writeData.BLEWriteData;
import com.bluetoothle.core.writeData.OnBLEWriteDataListener;
import com.bluetoothle.util.BLELogUtil;
import com.bluetoothle.util.BLEStringUtil;

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
    public static List<BluetoothGatt> connectedBluetoothGattList = new ArrayList<>();//当前已连接的设备服务器

    /**
     * 蓝牙任务管理器构造器
     * @param bluetoothAdapter  本地蓝牙适配器
     * @param targetDeviceAddress   目前设备地址
     * @param targetDeviceAddressList   目标设备地址列表
     * @param serviceUUIDs  设备的UUID,uuids=2 则不接受设备返回的数据, uuids=5 则接收设备返回的数据
     * @param timeoutScanBLE    扫描蓝牙超时时间
     */
    public BluetoothLeManage(BluetoothAdapter bluetoothAdapter, final String targetDeviceAddress, final List<String> targetDeviceAddressList, final UUID[] serviceUUIDs, Integer timeoutScanBLE) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.targetDeviceAddress = targetDeviceAddress;
        this.targetDeviceAddressList = targetDeviceAddressList;
        this.serviceUUIDs = serviceUUIDs;
        this.timeoutScanBLE = timeoutScanBLE;
    }

    /**
     * 扫描周围的蓝牙设备
     */
    private BluetoothAdapter bluetoothAdapter;//本地蓝牙适配器
    private String targetDeviceAddress;//目前设备地址
    private List<String> targetDeviceAddressList;//目标设备地址列表
    private UUID[] serviceUUIDs;//设备的UUID,uuids=2 则不接受设备返回的数据, uuids=5 则接收设备返回的数据
    private UUID[] notificationuuids;//从serviceUUIDs分离出来的通知UUID,取serviceUUIDs的2,3,4个UUID
    private Integer timeoutScanBLE;//扫描蓝牙超时时间
    private Integer currentScanCount = 0;//当前扫描次数
    private BLEScan bleScan;//蓝牙扫描管理器
    private OnBLEScanListener onBLEScanListener;//蓝牙扫描监听器
    public void setOnBLEScanListener(OnBLEScanListener onBLEScanListener) {
        this.onBLEScanListener = onBLEScanListener;
    }
    private OnBLEScanListener onBLEScanListener_ = new OnBLEScanListener() {//临时的蓝牙扫描监听器
        @Override
        public void foundDevice(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            if(onBLEConnectListener != null || onBLEFindServiceListener != null || onBLEOpenNotificationListener != null || onBLEWriteDataListener != null){
                bleConnect = new BLEConnect(bluetoothDevice, onBLEConnectListener_);
                bleConnect.connect();
                return;
            }
            if (onBLEScanListener != null) {
                onBLEScanListener.foundDevice(bluetoothDevice, rssi, scanRecord);
            }
        }

        @Override
        public void scanFinish(List<Map<String, Object>> bluetoothDeviceList) {
            if(bluetoothDeviceList.size() == 0){
                onResponseError(BLEConstants.Error.NotFoundDeviceError);
                return;
            }
            if (onBLEScanListener != null) {
                onBLEScanListener.scanFinish(bluetoothDeviceList);
            }
        }

        @Override
        public void scanFail(Integer errorCode) {
            BLELogUtil.e(TAG, "第" + currentScanCount + "次扫描失败,errorCode=" + errorCode);
            scan();
        }
    };
    public void scan(){
        if(onBLEScanListener == null && onBLEConnectListener == null && onBLEFindServiceListener == null && onBLEOpenNotificationListener == null && onBLEWriteDataListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        if(BLEStringUtil.isEmpty(targetDeviceAddress) && targetDeviceAddressList == null){
            onResponseError(BLEConstants.Error.CheckConnectDeviceError);
            return;
        }
        if(BLEStringUtil.isNotEmpty(targetDeviceAddress)){
            if(!BLEUtil.checkAddress(targetDeviceAddress)){
                onResponseError(BLEConstants.Error.CheckMacAddressError);
                return;
            }
        }else{
            if(!BLEUtil.checkTargetAddressList(targetDeviceAddressList)){
                onResponseError(BLEConstants.Error.CheckMacAddressListError);
                return;
            }
        }
        if(currentScanCount ++ == BluetoothLeConfig.maxScanCount){
            onResponseError(BLEConstants.Error.NotFoundDeviceError);
            return;
        }
        bleScan = new BLEScan(bluetoothAdapter, targetDeviceAddress, targetDeviceAddressList, serviceUUIDs, timeoutScanBLE, onBLEScanListener_);
        bleScan.scan();
    }

    /**
     * 连接蓝牙设备
     */
    private Integer currentConnectCount = 0;//当前连接次数
    private BLEConnect bleConnect;//蓝牙连接管理器
    private OnBLEConnectListener onBLEConnectListener;//蓝牙连接监听器
    public void setOnBLEConnectListener(OnBLEConnectListener onBLEConnectListener) {
        this.onBLEConnectListener = onBLEConnectListener;
    }
    private OnBLEConnectListener onBLEConnectListener_ = new OnBLEConnectListener() {//临时的蓝牙连接监听器
        @Override
        public void onConnectSuccess(BluetoothGatt bluetoothGatt, int status, int newState) {
            if (BLEUtil.getBluetoothGatt(bluetoothGatt.getDevice().getAddress()) == null) {
                connectedBluetoothGattList.add(bluetoothGatt);
            }
            if(onBLEFindServiceListener != null || onBLEOpenNotificationListener != null || onBLEWriteDataListener != null){
                if(bleFindService == null){
                    bleFindService = new BLEFindService(bluetoothGatt, onBLEFindServiceListener_);
                }
                bleFindService.findService();
                return;
            }
            if (onBLEConnectListener != null) {
                onBLEConnectListener.onConnectSuccess(bluetoothGatt, status, newState);
            }
        }

        @Override
        public void onConnectFail(Integer errorCode) {
            BLELogUtil.e(TAG, "第" + currentConnectCount + "次连接失败,errorCode=" + errorCode);
            if(currentConnectCount ++ == BluetoothLeConfig.maxConnectCount){
                onResponseError(BLEConstants.Error.ConnectError);
                return;
            }
            bleConnect.connect();
        }
    };
    public void connect(){
        if(onBLEConnectListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        BluetoothGatt bluetoothGatt = BLEUtil.getBluetoothGatt(targetDeviceAddress);
        if(bluetoothGatt != null){
            onBLEConnectListener.onConnectSuccess(bluetoothGatt, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_CONNECTED);
            return;
        }
        scan();
    }

    /**
     * 寻找服务
     */
    private Integer currentFindServiceCount = 0;//当前找服务次数
    private BLEFindService bleFindService;//蓝牙找服务管理器
    private OnBLEFindServiceListener onBLEFindServiceListener;//找服务监听器
    public void setOnBLEFindServiceListener(OnBLEFindServiceListener onBLEFindServiceListener) {
        this.onBLEFindServiceListener = onBLEFindServiceListener;
    }
    private OnBLEFindServiceListener onBLEFindServiceListener_ = new OnBLEFindServiceListener() {//临时的蓝牙找服务器监听器
        @Override
        public void onFindServiceSuccess(BluetoothGatt bluetoothGatt, int status, List<BluetoothGattService> bluetoothGattServices) {
            if(onBLEOpenNotificationListener != null || onBLEWriteDataListener != null){
                if(bleOpenNotification == null){
                    bleOpenNotification = new BLEOpenNotification(bluetoothGattServices, bluetoothGatt, notificationuuids, onBLEOpenNotificationListener_);
                }
                bleOpenNotification.openNotification();
                return;
            }
            if(onBLEFindServiceListener != null){
                onBLEFindServiceListener.onFindServiceSuccess(bluetoothGatt, status, bluetoothGattServices);
            }
        }

        @Override
        public void onFindServiceFail(Integer errorCode) {
            BLELogUtil.e(TAG, "第" + currentFindServiceCount + "次找服务失败,errorCode=" + errorCode);
            if(currentFindServiceCount ++ == BluetoothLeConfig.maxFindServiceCount){
                onResponseError(BLEConstants.Error.FindServiceError);
                return;
            }
            bleFindService.findService();
        }
    };
    public void findService(){
        if(onBLEFindServiceListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        if(serviceUUIDs == null || (serviceUUIDs.length != 2 && serviceUUIDs.length != 5) ){
            onBLEFindServiceListener.onFindServiceFail(BLEConstants.Error.CheckUUIDArraysError);
            return;
        }
        if (serviceUUIDs.length == 5) {
            notificationuuids = new UUID[3];
            notificationuuids[0] = serviceUUIDs[2];
            notificationuuids[1] = serviceUUIDs[3];
            notificationuuids[2] = serviceUUIDs[4];
            receiveBLEData = true;
        }
        BluetoothGatt bluetoothGatt = BLEUtil.getBluetoothGatt(targetDeviceAddress);
        if(bluetoothGatt != null){
            if(bluetoothGatt.getServices() == null || bluetoothGatt.getServices().size() == 0){
                bleFindService = new BLEFindService(bluetoothGatt, onBLEFindServiceListener_);
                bleFindService.findService();
                return;
            }
            onBLEFindServiceListener.onFindServiceSuccess(bluetoothGatt, BluetoothGatt.GATT_SUCCESS, bluetoothGatt.getServices());
            return;
        }
        scan();
    }

    /**
     * 打开通知
     */
    private Integer currentOpenNotificationCount = 0;//当前打开通知次数
    private BLEOpenNotification bleOpenNotification;//蓝牙打开通知管理器
    private OnBLEOpenNotificationListener onBLEOpenNotificationListener;//打开通知监听器
    public void setOnBLEOpenNotificationListener(OnBLEOpenNotificationListener onBLEOpenNotificationListener) {
        this.onBLEOpenNotificationListener = onBLEOpenNotificationListener;
    }
    private OnBLEOpenNotificationListener onBLEOpenNotificationListener_ = new OnBLEOpenNotificationListener() {//临时的蓝牙打开通知监听器
        @Override
        public void onOpenNotificationSuccess(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if(onBLEWriteDataListener != null){
                if(bleWriteData == null){
                    bleWriteData = new BLEWriteData(gatt, serviceUUIDs, data, onBLEWriteDataListener_);
                }
                bleWriteData.writeData();
                return;
            }
            if(onBLEOpenNotificationListener != null){
                onBLEOpenNotificationListener.onOpenNotificationSuccess(gatt, descriptor, status);
            }
        }

        @Override
        public void onOpenNotificationFail(Integer errorCode) {
            BLELogUtil.e(TAG, "第" + (currentOpenNotificationCount + 1) + "次打开通知失败,errorCode=" + errorCode);
            if(currentOpenNotificationCount ++ == BluetoothLeConfig.maxOpenNotificationCount){
                onResponseError(BLEConstants.Error.OpenNotificationError);
                return;
            }
            bleOpenNotification.openNotification();
        }
    };
    public void openNotification(){
        if(onBLEOpenNotificationListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        if(serviceUUIDs == null || (serviceUUIDs.length != 2 && serviceUUIDs.length != 5) ){
            onBLEOpenNotificationListener.onOpenNotificationFail(BLEConstants.Error.CheckUUIDArraysError);
            return;
        }
        if (serviceUUIDs.length == 5) {
            notificationuuids = new UUID[3];
            notificationuuids[0] = serviceUUIDs[2];
            notificationuuids[1] = serviceUUIDs[3];
            notificationuuids[2] = serviceUUIDs[4];
            receiveBLEData = true;
        }
        BluetoothGatt bluetoothGatt = BLEUtil.getBluetoothGatt(targetDeviceAddress);
        if(bluetoothGatt != null){
            if(bluetoothGatt.getServices() == null || bluetoothGatt.getServices().size() == 0){
                bleFindService = new BLEFindService(bluetoothGatt, onBLEFindServiceListener_);
                bleFindService.findService();
                return;
            }
            bleOpenNotification = new BLEOpenNotification(bluetoothGatt.getServices(), bluetoothGatt, notificationuuids, onBLEOpenNotificationListener_);
            bleOpenNotification.openNotification();
            return;
        }
        scan();
    }

    /**
     * 写数据
     */
    private Boolean receiveBLEData = false;//是否接收设备返回的数据
    private OnBLEResponseListener onBLEResponseListener;//接收数据监听器
    public void setOnBLEResponseListener(OnBLEResponseListener onBLEResponseListener) {
        this.onBLEResponseListener = onBLEResponseListener;
    }
    private byte[] data;//发送的总数据包字节数组
    public void setData(byte[] data) {
        this.data = data;
    }
    private BLEWriteData bleWriteData;//写数据管理器
    private OnBLEWriteDataListener onBLEWriteDataListener;//写数据监听器
    public void setOnBLEWriteDataListener(OnBLEWriteDataListener onBLEWriteDataListener) {
        this.onBLEWriteDataListener = onBLEWriteDataListener;
    }
    private OnBLEWriteDataListener onBLEWriteDataListener_ = new OnBLEWriteDataListener() {//临时的写数据监听器
        @Override
        public void onWriteDataFinish() {
            if(onBLEWriteDataListener != null){
                onBLEWriteDataListener.onWriteDataFinish();
            }
        }

        @Override
        public void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(onBLEWriteDataListener != null){
                onBLEWriteDataListener.onWriteDataSuccess(gatt, characteristic, status);
            }
        }

        @Override
        public void onWriteDataFail(Integer errorCode) {
            if(onBLEWriteDataListener != null){
                onBLEWriteDataListener.onWriteDataFail(errorCode);
            }
        }
    };
    public void write(){
        if(onBLEWriteDataListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        if(data == null || data.length == 0){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckBLEDataError);
            return;
        }
        if(serviceUUIDs == null || (serviceUUIDs.length != 2 && serviceUUIDs.length != 5) ){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckUUIDArraysError);
            return;
        }
        if (serviceUUIDs.length == 5) {
            notificationuuids = new UUID[3];
            notificationuuids[0] = serviceUUIDs[2];
            notificationuuids[1] = serviceUUIDs[3];
            notificationuuids[2] = serviceUUIDs[4];
            receiveBLEData = true;
        }
        if(receiveBLEData && onBLEResponseListener == null){
            onBLEWriteDataListener.onWriteDataFail(BLEConstants.Error.CheckOnBLEResponseListenerError);
            return;
        }
        if(BLEConnect.bluetoothLeGattCallback == null){
            BLEConnect.bluetoothLeGattCallback = new BluetoothLeGattCallback();
        }
        BLEConnect.bluetoothLeGattCallback.registerOnBLEResponseListener(onBLEResponseListener);
        BluetoothGatt bluetoothGatt = BLEUtil.getBluetoothGatt(targetDeviceAddress);
        if(bluetoothGatt != null){
            if(bluetoothGatt.getServices() == null || bluetoothGatt.getServices().size() == 0){
                bleFindService = new BLEFindService(bluetoothGatt, onBLEFindServiceListener_);
                bleFindService.findService();
                return;
            }
            bleOpenNotification = new BLEOpenNotification(bluetoothGatt.getServices(), bluetoothGatt, notificationuuids, onBLEOpenNotificationListener_);
            bleOpenNotification.openNotification();
            return;
        }
        scan();
    }

    /**
     * 响应失败回复
     * @param errorCode     错误码
     */
    private void onResponseError(Integer errorCode){
        if(onBLEWriteDataListener != null){
            onBLEWriteDataListener.onWriteDataFail(errorCode);
            return;
        }
        if(onBLEOpenNotificationListener != null){
            onBLEOpenNotificationListener.onOpenNotificationFail(errorCode);
            return;
        }
        if(onBLEFindServiceListener != null){
            onBLEFindServiceListener.onFindServiceFail(errorCode);
            return;
        }
        if(onBLEConnectListener != null){
            onBLEConnectListener.onConnectFail(errorCode);
            return;
        }
        if(onBLEScanListener != null){
            onBLEScanListener.scanFail(errorCode);
        }
    }
}
