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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dessmann on 16/10/14.
 * 蓝牙低功耗管理
 */

public class BLEManage {

    private final static String TAG = BLEManage.class.getSimpleName();
    public static List<Map<BluetoothGatt, Long>> connectedBluetoothGattList = new ArrayList<>();//当前已连接的设备服务器连接池  第一次参数表示某一个连接对象，第二个参数表示该连接上一次通讯(读过或接收过数据)的时间戳
    private BluetoothGatt bluetoothGatt;//当前已连接的设备服务器
    private BLECoreResponse bleCoreResponse = new BLECoreResponse();//蓝牙核心响应管理器

    /**
     * 蓝牙任务管理器构造器
     * @param bluetoothAdapter  本地蓝牙适配器
     * @param targetDeviceAddress   目前设备地址
     * @param targetDeviceAddressList   目标设备地址列表
     * @param serviceUUIDs  设备的UUID,uuids=2 则不接受设备返回的数据, uuids=5 则接收设备返回的数据
     * @param timeoutScanBLE    扫描蓝牙超时时间
     */
    public BLEManage(BluetoothAdapter bluetoothAdapter, final String targetDeviceAddress, final List<String> targetDeviceAddressList, final UUID[] serviceUUIDs, Integer timeoutScanBLE) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.targetDeviceAddress = targetDeviceAddress;
        this.targetDeviceAddressList = targetDeviceAddressList;
        this.serviceUUIDs = serviceUUIDs;
        this.timeoutScanBLE = timeoutScanBLE;
        bleCoreResponse.setRunning(true);
        bleCoreResponse.setMac(targetDeviceAddress);
    }

    public BLEManage(BluetoothAdapter bluetoothAdapter, final String targetDeviceAddress, final List<String> targetDeviceAddressList, final UUID[] serviceUUIDs, Integer timeoutScanBLE, Boolean disconnectOnFinish) {
        this(bluetoothAdapter, targetDeviceAddress, targetDeviceAddressList, serviceUUIDs, timeoutScanBLE);
        bleCoreResponse.setDisconnectOnFinish(disconnectOnFinish);
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
        public void onFoundDevice(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            if(onBLEConnectListener != null || onBLEFindServiceListener != null || onBLEOpenNotificationListener != null || onBLEWriteDataListener != null){
                bleConnect = new BLEConnect(bluetoothDevice, onBLEConnectListener_);
                bleConnect.connect();
                return;
            }
            if (onBLEScanListener != null) {
                bleCoreResponse.onFoundDevice(onBLEScanListener, bluetoothDevice, rssi, scanRecord);
            }
        }

        @Override
        public void onScanFinish(List<Map<String, Object>> bluetoothDeviceList) {
            if(bluetoothDeviceList.size() == 0){
                onResponseError(BLEConstants.Error.NotFoundDeviceError);
                return;
            }
            if (onBLEScanListener != null) {
                bleCoreResponse.onScanFinish(onBLEScanListener, bluetoothDeviceList);
            }
        }

        @Override
        public void onScanFail(Integer errorCode) {
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
        if(currentScanCount ++ == BLEConfig.MaxScanCount){
            onResponseError(BLEConstants.Error.NotFoundDeviceError);
            return;
        }
        bleScan = new BLEScan(bluetoothAdapter, targetDeviceAddress, targetDeviceAddressList, null, timeoutScanBLE, onBLEScanListener_);
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
            BLEManage.this.bluetoothGatt = bluetoothGatt;
            if (BLEUtil.getBluetoothGatt(bluetoothGatt.getDevice().getAddress()) == null) {
                Map<BluetoothGatt, Long> bluetoothGattLongMap = new HashMap<>();
                bluetoothGattLongMap.put(bluetoothGatt, System.currentTimeMillis());
                connectedBluetoothGattList.add(bluetoothGattLongMap);
            }
            if(onBLEFindServiceListener != null || onBLEOpenNotificationListener != null || onBLEWriteDataListener != null){
                if(bleFindService == null){
                    bleFindService = new BLEFindService(bluetoothGatt, onBLEFindServiceListener_);
                }
                bleFindService.findService();
                return;
            }
            if (onBLEConnectListener != null) {
                bleCoreResponse.onConnectSuccess(onBLEConnectListener, bluetoothGatt, status, newState);
            }
        }

        @Override
        public void onConnectFail(Integer errorCode) {
            BLELogUtil.e(TAG, "第" + ++currentConnectCount + "次连接失败,errorCode=" + errorCode);
            if(currentConnectCount == BLEConfig.MaxConnectCount){
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
            bleCoreResponse.onConnectSuccess(onBLEConnectListener, bluetoothGatt, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_CONNECTED);
            return;
        }
        scan();
    }

    /**
     * 寻找服务
     */
    private Integer currentFindServiceCount = 0;//当前找服务次数
    private Integer currentReconnectCountWhenDisconnectedOnFindService = 0;//当前找服务时断开连接重连的次数
    private BLEFindService bleFindService;//蓝牙找服务管理器
    private OnBLEFindServiceListener onBLEFindServiceListener;//找服务监听器
    public void setOnBLEFindServiceListener(OnBLEFindServiceListener onBLEFindServiceListener) {
        this.onBLEFindServiceListener = onBLEFindServiceListener;
    }
    private OnBLEFindServiceListener onBLEFindServiceListener_ = new OnBLEFindServiceListener() {//临时的蓝牙找服务器监听器
        @Override
        public void onFindServiceSuccess(BluetoothGatt bluetoothGatt, int status, List<BluetoothGattService> bluetoothGattServices) {
            if(onBLEFindServiceListener != null){
                bleCoreResponse.onFindServiceSuccess(onBLEFindServiceListener, bluetoothGatt, status, bluetoothGattServices);
                return;
            }
            if(onBLEOpenNotificationListener != null || (onBLEWriteDataListener != null && receiveBLEData)){
                if(bleOpenNotification == null){
                    bleOpenNotification = new BLEOpenNotification(bluetoothGattServices, bluetoothGatt, notificationuuids, onBLEOpenNotificationListener_);
                }
                bleOpenNotification.openNotification();
            }else{
                if(bleWriteData == null){
                    bleWriteData = new BLEWriteData(bluetoothGatt, writeuuids, data, onBLEWriteDataListener_);
                }
                bleWriteData.writeData();
            }
        }

        @Override
        public void onFindServiceFail(Integer errorCode) {
            BLELogUtil.e(TAG, "第" + ++currentFindServiceCount + "次找服务失败,errorCode=" + errorCode);
            if(errorCode == BLEConstants.Error.DisconnectError){//找服务时断开连接，有限的次数重新连接
                if(currentReconnectCountWhenDisconnectedOnFindService ++ < BLEConfig.MaxReconnectCountWhenDisconnectedOnFindService){
                    bluetoothGatt.connect();
                    BLELogUtil.e(TAG, "找服务时断开连接，第" + currentReconnectCountWhenDisconnectedOnFindService + "次重连");
                    return;
                }
                onResponseError(errorCode);
                return;
            }
            if(currentFindServiceCount == BLEConfig.MaxFindServiceCount){
                onResponseError(errorCode);
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
            bleCoreResponse.onResponseError(onBLEFindServiceListener, BLEConstants.Error.CheckUUIDArraysError);
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
            bleCoreResponse.onFindServiceSuccess(onBLEFindServiceListener, bluetoothGatt, BluetoothGatt.GATT_SUCCESS, bluetoothGatt.getServices());
            return;
        }
        scan();
    }

    /**
     * 打开通知
     */
    private Integer currentOpenNotificationCount = 0;//当前打开通知次数
    private Integer currentReconnectCountWhenDisconnectedOnOpenNotification = 0;//当前打开通知时断开连接重连的次数
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
                bleCoreResponse.onOpenNotificationSuccess(onBLEOpenNotificationListener, gatt, descriptor, status);
            }
        }

        @Override
        public void onOpenNotificationFail(Integer errorCode) {
            BLELogUtil.e(TAG, "第" + ++currentOpenNotificationCount + "次打开通知失败,errorCode=" + errorCode);
            if(errorCode == BLEConstants.Error.DisconnectError){
                if(currentReconnectCountWhenDisconnectedOnOpenNotification ++ < BLEConfig.MaxReconnectCountWhenDisconnectedOnOpenNotification){
                    BLELogUtil.e(TAG, "打开通知时断开连接，第" + currentReconnectCountWhenDisconnectedOnOpenNotification + "次重连");
                    bluetoothGatt.connect();
                    return;
                }
                onResponseError(errorCode);
                return;
            }
            if(currentOpenNotificationCount == BLEConfig.MaxOpenNotificationCount){
                onResponseError(errorCode);
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
            bleCoreResponse.onResponseError(onBLEOpenNotificationListener, BLEConstants.Error.CheckUUIDArraysError);
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
    private UUID[] writeuuids;//从serviceUUIDs分离出来的通知UUID,取serviceUUIDs的0,1个UUID
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
                bleCoreResponse.onWriteDataFinish(onBLEWriteDataListener);
            }
        }

        @Override
        public void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(onBLEWriteDataListener != null){
                bleCoreResponse.onWriteDataSuccess(onBLEWriteDataListener, gatt, characteristic, status);
            }
        }

        @Override
        public void onWriteDataFail(Integer errorCode) {
            if(onBLEWriteDataListener != null){
                bleCoreResponse.onResponseError(onBLEWriteDataListener, errorCode);
            }
        }
    };
    public void write(){
        if(onBLEWriteDataListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        if(data == null || data.length == 0){
            bleCoreResponse.onResponseError(onBLEWriteDataListener, BLEConstants.Error.CheckBLEDataError);
            return;
        }
        if(serviceUUIDs == null || (serviceUUIDs.length != 2 && serviceUUIDs.length != 5) ){
            bleCoreResponse.onResponseError(onBLEWriteDataListener, BLEConstants.Error.CheckUUIDArraysError);
            return;
        }
        writeuuids = new UUID[2];
        writeuuids[0] = serviceUUIDs[0];
        writeuuids[1] = serviceUUIDs[1];
        if (serviceUUIDs.length == 5) {
            notificationuuids = new UUID[3];
            notificationuuids[0] = serviceUUIDs[2];
            notificationuuids[1] = serviceUUIDs[3];
            notificationuuids[2] = serviceUUIDs[4];
            receiveBLEData = true;
        }
        if(receiveBLEData && onBLEResponseListener == null){
            bleCoreResponse.onResponseError(onBLEWriteDataListener, BLEConstants.Error.CheckOnBLEResponseListenerError);
            return;
        }
        if(BLEConnect.bleGattCallback == null){
            BLEConnect.bleGattCallback = new BLEGattCallback();
        }
        BLEConnect.bleGattCallback.registerOnBLEResponseListener(onBLEResponseListener);
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
            bleCoreResponse.onResponseError(onBLEWriteDataListener, errorCode);
            return;
        }
        if(onBLEOpenNotificationListener != null){
            bleCoreResponse.onResponseError(onBLEOpenNotificationListener, errorCode);
            return;
        }
        if(onBLEFindServiceListener != null){
            bleCoreResponse.onResponseError(onBLEFindServiceListener, errorCode);
            return;
        }
        if(onBLEConnectListener != null){
            bleCoreResponse.onResponseError(onBLEConnectListener, errorCode);
            return;
        }
        if(onBLEScanListener != null){
            bleCoreResponse.onResponseError(onBLEScanListener, errorCode);
        }
    }
}
