package com.bluetoothle.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;

import com.bluetoothle.core.connect.BLEConnect;
import com.bluetoothle.core.connect.OnBLEConnectListener;
import com.bluetoothle.core.findService.BLEFindService;
import com.bluetoothle.core.findService.OnBLEFindServiceListener;
import com.bluetoothle.core.init.BLEInit;
import com.bluetoothle.core.openNotification.BLEOpenNotification;
import com.bluetoothle.core.openNotification.OnBLEOpenNotificationListener;
import com.bluetoothle.core.response.OnBLEResponse;
import com.bluetoothle.core.scan.BLEScan;
import com.bluetoothle.core.scan.OnBLEScanListener;
import com.bluetoothle.core.writeData.BLEWriteData;
import com.bluetoothle.core.writeData.OnBLEWriteDataListener;
import com.bluetoothle.util.BLEByteUtil;
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
    public static List<Map<String, Object>> connectedBluetoothGattList = new ArrayList<>();//当前已连接的设备服务器连接池  第一次参数表示某一个连接对象，第二个参数表示该连接上一次通讯(读过或接收过数据)的时间戳
    private BLECoreResponse bleCoreResponse = new BLECoreResponse();//蓝牙核心响应管理器
    private Object listenterObject;//响应对象
    private boolean timeout = false;//是否超时
    private Handler timeoutHandler = new Handler();//超时管理对象
    private Runnable timeoutRunnable = new Runnable() {//超时任务
        @Override
        public void run() {
            timeout = true;
            bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.Timeout, BLEManage.this);
        }
    };

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
        timeoutHandler.postDelayed(timeoutRunnable, BLEConfig.TimeoutWholeTast);
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
    private BluetoothDevice bluetoothDevice;//扫描到的目标蓝牙设备
    public void setOnBLEScanListener(OnBLEScanListener onBLEScanListener) {
        this.onBLEScanListener = onBLEScanListener;
        listenterObject = onBLEScanListener;
    }
    private OnBLEScanListener onBLEScanListener_ = new OnBLEScanListener() {//临时的蓝牙扫描监听器
        @Override
        public void onFoundDevice(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            BLELogUtil.d(TAG, "onFoundDevice,bluetoothDevice=" + bluetoothDevice + ",rssi=" + rssi + ",scanRecord=" + BLEByteUtil.bytesToHexString(scanRecord));
            BLEManage.this.bluetoothDevice = bluetoothDevice;
            if(onBLEConnectListener != null || onBLEFindServiceListener != null || onBLEOpenNotificationListener != null || onBLEWriteDataListener != null || onBLEResponse != null){
                bleConnect = new BLEConnect(bluetoothDevice, bleCoreResponse, onBLEResponse, BLEManage.this, onBLEConnectListener_);
                bleConnect.connect();
                return;
            }
            if (onBLEScanListener != null) {
                bleCoreResponse.onFoundDevice(onBLEScanListener, bluetoothDevice, rssi, scanRecord);
            }
        }

        @Override
        public void onScanFinish(List<Map<String, Object>> bluetoothDeviceList) {
            BLELogUtil.d(TAG, "onScanFinish,bluetoothDeviceList.size()=" + bluetoothDeviceList.size());
            if (onBLEScanListener != null && bluetoothDeviceList.size() > 0) {
                bleCoreResponse.onScanFinish(onBLEScanListener, bluetoothDeviceList);
                return;
            }
            bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.NotFoundDevice, BLEManage.this);

        }

        @Override
        public void onScanFail(String errorCode) {
            BLELogUtil.d(TAG, "onScanFail,errorCode=" + errorCode);
            BLELogUtil.e(TAG, "第" + currentScanCount + "次扫描失败,errorCode=" + errorCode);
            scan();
        }
    };
    public void scan(){
        if(onBLEScanListener == null && onBLEConnectListener == null && onBLEFindServiceListener == null && onBLEOpenNotificationListener == null && onBLEWriteDataListener == null && onBLEResponse == null){
            BLELogUtil.e(TAG, "scan,没有配置回调接口");
            return;
        }
        if(!BLEInit.status){
            bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.BLEInit, BLEManage.this);
            return;
        }
        if(BLEStringUtil.isEmpty(targetDeviceAddress) && targetDeviceAddressList == null){
            bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.Device_Address, BLEManage.this);
            return;
        }
        if(BLEStringUtil.isNotEmpty(targetDeviceAddress)){
            if(!BLEUtil.checkAddress(targetDeviceAddress)){
                bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.MacAddress, BLEManage.this);
                return;
            }
        }else{
            if(!BLEUtil.checkTargetAddressList(targetDeviceAddressList)){
                bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.MacAddressList, BLEManage.this);
                return;
            }
        }
        if(currentScanCount ++ == BLEConfig.MaxScanCount.intValue()){
            bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.NotFoundDevice, BLEManage.this);
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
        listenterObject = onBLEConnectListener;
    }
    private OnBLEConnectListener onBLEConnectListener_ = new OnBLEConnectListener() {//临时的蓝牙连接监听器
        @Override
        public void onConnectSuccess(BluetoothGatt bluetoothGatt, int status, int newState, BLEGattCallback bleGattCallback) {
            if (BLEUtil.getBluetoothGatt(connectedBluetoothGattList, bluetoothGatt.getDevice().getAddress()) == null) {
                Map<String, Object> bluetoothGattMap = new HashMap<>();
                bluetoothGattMap.put("bluetoothGatt", bluetoothGatt);
                bluetoothGattMap.put("connectedTime", System.currentTimeMillis());
                bluetoothGattMap.put("bluetoothGattCallback", bleGattCallback);
                connectedBluetoothGattList.add(bluetoothGattMap);
            }
            if(onBLEFindServiceListener != null || onBLEOpenNotificationListener != null || onBLEWriteDataListener != null || onBLEResponse != null){
                if(bleFindService == null){
                    bleFindService = new BLEFindService(bluetoothGatt, bleGattCallback, onBLEFindServiceListener_);
                }
                bleFindService.findService();
                return;
            }
            if (onBLEConnectListener != null) {
                bleCoreResponse.onConnectSuccess(onBLEConnectListener, bluetoothGatt, status, newState, bleGattCallback);
            }
        }

        @Override
        public void onConnectFail(String errorCode) {
            BLELogUtil.e(TAG, "第" + ++currentConnectCount + "次连接失败,errorCode=" + errorCode);
            if(currentConnectCount == BLEConfig.MaxConnectCount){
                bleCoreResponse.onResponseError(listenterObject, errorCode, BLEManage.this);
                return;
            }
            bleConnect.connect();
        }
    };
    public void connect(){
        if(onBLEConnectListener == null && onBLEFindServiceListener == null && onBLEOpenNotificationListener == null && onBLEWriteDataListener == null && onBLEResponse == null){
            BLELogUtil.e(TAG, "connect,没有配置回调接口");
            return;
        }
        if(!BLEInit.status){
            bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.BLEInit, BLEManage.this);
            return;
        }
        BluetoothGatt bluetoothGatt = BLEUtil.getBluetoothGatt(connectedBluetoothGattList, targetDeviceAddress);
        if(bluetoothGatt != null){
            BLEGattCallback bleGattCallback = (BLEGattCallback)BLEUtil.getBluetoothGattMap(connectedBluetoothGattList, bluetoothGatt).get("bluetoothGattCallback");
            bleCoreResponse.onConnectSuccess(onBLEConnectListener, bluetoothGatt, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_CONNECTED, bleGattCallback);
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
        listenterObject = onBLEFindServiceListener;
    }
    private OnBLEFindServiceListener onBLEFindServiceListener_ = new OnBLEFindServiceListener() {//临时的蓝牙找服务器监听器
        @Override
        public void onFindServiceSuccess(BluetoothGatt bluetoothGatt, int status, List<BluetoothGattService> bluetoothGattServices, BLEGattCallback bleGattCallback) {
            //遍历服务
            for(BluetoothGattService bluetoothGattService : bluetoothGattServices){
                BLELogUtil.e(TAG, "++++service uuid:" + bluetoothGattService.getUuid());
                for(BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()){
                    BLELogUtil.e(TAG, "--------characteristics uuid:" + bluetoothGattCharacteristic.getUuid());
                    for(BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattCharacteristic.getDescriptors()){
                        BLELogUtil.e(TAG, "------------descriptor uuid:" + bluetoothGattDescriptor.getUuid());
                    }
                }
            }
            if(receiveBLEData){
                if(bleOpenNotification == null){
                    bleOpenNotification = new BLEOpenNotification(bluetoothGattServices, bluetoothGatt, notificationuuids, bleGattCallback, onBLEOpenNotificationListener_);
                }
                bleOpenNotification.openNotification();
            }else if(onBLEFindServiceListener != null){
                bleCoreResponse.onFindServiceSuccess(onBLEFindServiceListener, bluetoothGatt, status, bluetoothGattServices, bleGattCallback);
            }else{
                if(bleWriteData == null){
                    bleWriteData = new BLEWriteData(bluetoothGatt, writeuuids, data, bleGattCallback, onBLEWriteDataListener_);
                }
                bleWriteData.writeData();
            }
        }

        @Override
        public void onFindServiceFail(String errorCode) {
            BLELogUtil.e(TAG, "第" + ++currentFindServiceCount + "次找服务失败,errorCode=" + errorCode);
//            BLEManage.this.bluetoothGatt = null;
            if(errorCode.equalsIgnoreCase(BLEConstants.Error.Disconnect)){//找服务时断开连接，有限的次数重新连接
                if(currentReconnectCountWhenDisconnectedOnFindService ++ < BLEConfig.MaxReconnectCountWhenDisconnectedOnFindService){
//                    bluetoothGatt.connect();
                    bleConnect = new BLEConnect(bluetoothDevice, bleCoreResponse, onBLEResponse, BLEManage.this, onBLEConnectListener_);
                    bleConnect.connect();
                    BLELogUtil.e(TAG, "找服务时断开连接，第" + currentReconnectCountWhenDisconnectedOnFindService + "次重连");
                    return;
                }
                bleCoreResponse.onResponseError(listenterObject, errorCode, BLEManage.this);

                return;
            }
            if(currentFindServiceCount.intValue() == BLEConfig.MaxFindServiceCount.intValue()){
                bleCoreResponse.onResponseError(listenterObject, errorCode, BLEManage.this);
                return;
            }
            bleFindService.findService();
        }
    };
    public void findService(){
        if(onBLEFindServiceListener == null && onBLEOpenNotificationListener == null && onBLEWriteDataListener == null && onBLEResponse == null){
            BLELogUtil.e(TAG, "findService,没有配置回调接口");
            return;
        }
        if(!BLEInit.status){
            bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.BLEInit, BLEManage.this);
            return;
        }
        if(serviceUUIDs == null || (serviceUUIDs.length != 2 && serviceUUIDs.length != 5) ){
            bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.UUIDArrays, BLEManage.this);
            return;
        }
        if (serviceUUIDs.length == 5) {
            notificationuuids = new UUID[3];
            notificationuuids[0] = serviceUUIDs[2];
            notificationuuids[1] = serviceUUIDs[3];
            notificationuuids[2] = serviceUUIDs[4];
            receiveBLEData = true;
        }
        BluetoothGatt bluetoothGatt = BLEUtil.getBluetoothGatt(connectedBluetoothGattList, targetDeviceAddress);
        if(bluetoothGatt != null){
            BLEGattCallback bleGattCallback = (BLEGattCallback)BLEUtil.getBluetoothGattMap(connectedBluetoothGattList, bluetoothGatt).get("bluetoothGattCallback");
            if(bluetoothGatt.getServices() == null || bluetoothGatt.getServices().size() == 0){
                bleFindService = new BLEFindService(bluetoothGatt, bleGattCallback, onBLEFindServiceListener_);
                bleFindService.findService();
                return;
            }
            bleCoreResponse.onFindServiceSuccess(onBLEFindServiceListener, bluetoothGatt, BluetoothGatt.GATT_SUCCESS, bluetoothGatt.getServices(), bleGattCallback);
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
        listenterObject = onBLEOpenNotificationListener;
    }
    private OnBLEOpenNotificationListener onBLEOpenNotificationListener_ = new OnBLEOpenNotificationListener() {//临时的蓝牙打开通知监听器
        @Override
        public void onOpenNotificationSuccess(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status, BLEGattCallback bleGattCallback) {
            if(onBLEWriteDataListener != null || onBLEResponse != null){
                if(bleWriteData == null){
                    bleWriteData = new BLEWriteData(gatt, writeuuids, data, bleGattCallback,  onBLEWriteDataListener_);
                }
                bleWriteData.writeData();
                return;
            }
            if(onBLEOpenNotificationListener != null){
                bleCoreResponse.onOpenNotificationSuccess(onBLEOpenNotificationListener, gatt, descriptor, status, bleGattCallback);
            }
        }

        @Override
        public void onOpenNotificationFail(String errorCode) {
            BLELogUtil.e(TAG, "第" + ++currentOpenNotificationCount + "次打开通知失败,errorCode=" + errorCode);
            if(errorCode.equalsIgnoreCase(BLEConstants.Error.Disconnect)){
                if(currentReconnectCountWhenDisconnectedOnOpenNotification ++ < BLEConfig.MaxReconnectCountWhenDisconnectedOnOpenNotification){
                    BLELogUtil.e(TAG, "打开通知时断开连接，第" + currentReconnectCountWhenDisconnectedOnOpenNotification + "次重连");
//                    bluetoothGatt.connect();
                    bleConnect = new BLEConnect(bluetoothDevice, bleCoreResponse, onBLEResponse, BLEManage.this, onBLEConnectListener_);
                    bleConnect.connect();
                    return;
                }
                bleCoreResponse.onResponseError(listenterObject, errorCode, BLEManage.this);
                return;
            }
            if(currentOpenNotificationCount.intValue() == BLEConfig.MaxOpenNotificationCount.intValue()){
                bleCoreResponse.onResponseError(listenterObject, errorCode, BLEManage.this);
                return;
            }
            bleOpenNotification.openNotification();
        }
    };
    public void openNotification(){
        if(onBLEOpenNotificationListener == null && onBLEWriteDataListener == null && onBLEResponse == null){
            BLELogUtil.e(TAG, "openNotification,没有配置回调接口");
            return;
        }
        if(!BLEInit.status){
            bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.BLEInit, BLEManage.this);
            return;
        }
        if(serviceUUIDs == null || (serviceUUIDs.length != 2 && serviceUUIDs.length != 5) ){
            bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.UUIDArrays, BLEManage.this);
            return;
        }
        if (serviceUUIDs.length == 5) {
            notificationuuids = new UUID[3];
            notificationuuids[0] = serviceUUIDs[2];
            notificationuuids[1] = serviceUUIDs[3];
            notificationuuids[2] = serviceUUIDs[4];
            receiveBLEData = true;
        }
        BluetoothGatt bluetoothGatt = BLEUtil.getBluetoothGatt(connectedBluetoothGattList, targetDeviceAddress);
        if(bluetoothGatt != null){
            BLEGattCallback bleGattCallback = (BLEGattCallback)BLEUtil.getBluetoothGattMap(connectedBluetoothGattList, bluetoothGatt).get("bluetoothGattCallback");
            if(bluetoothGatt.getServices() == null || bluetoothGatt.getServices().size() == 0){
                bleFindService = new BLEFindService(bluetoothGatt, bleGattCallback, onBLEFindServiceListener_);
                bleFindService.findService();
                return;
            }
            bleOpenNotification = new BLEOpenNotification(bluetoothGatt.getServices(), bluetoothGatt, notificationuuids, bleGattCallback, onBLEOpenNotificationListener_);
            bleOpenNotification.openNotification();
            return;
        }
        scan();
    }

    /**
     * 写数据
     */
    private UUID[] writeuuids;//从serviceUUIDs分离出来的写数据UUID,取serviceUUIDs的0,1个UUID,分别为服务与特征uuid
    private Boolean receiveBLEData = false;//是否接收设备返回的数据
    private OnBLEResponse onBLEResponse;//接收数据监听器
    public void setOnBLEResponse(OnBLEResponse onBLEResponse) {
        this.onBLEResponse = onBLEResponse;
        listenterObject = onBLEResponse;
        this.onBLEResponse.setBleCoreResponse(bleCoreResponse);
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
            BLELogUtil.i(TAG, "onWriteDataFinish");
            if(onBLEWriteDataListener != null){
                bleCoreResponse.onWriteDataFinish(onBLEWriteDataListener);
            }
        }

        @Override
        public void onWriteDataSuccess(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status, BLEGattCallback bleGattCallback) {
            BLELogUtil.i(TAG, "onWriteDataSuccess, writtenData=" + BLEByteUtil.bytesToHexString(characteristic.getValue()));
            if(onBLEWriteDataListener != null){
                bleCoreResponse.onWriteDataSuccess(onBLEWriteDataListener, gatt, characteristic, status, bleGattCallback);
            }
        }

        @Override
        public void onWriteDataFail(String errorCode) {
            if(onBLEWriteDataListener != null){
                bleCoreResponse.onResponseError(listenterObject, errorCode, BLEManage.this);
            }
            if(onBLEResponse != null){
                bleCoreResponse.onResponseError(listenterObject, errorCode, BLEManage.this);
            }
        }
    };
    public void write(){
        if(onBLEWriteDataListener == null && onBLEResponse == null){
            BLELogUtil.e(TAG, "write,没有配置回调接口");
            return;
        }
        if(!BLEInit.status){
            bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.BLEInit, BLEManage.this);
            return;
        }
        if(data == null || data.length == 0){
            bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.CheckBLEDataError, BLEManage.this);
            return;
        }
        if(serviceUUIDs == null || (serviceUUIDs.length != 2 && serviceUUIDs.length != 5) ){
            bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.UUIDArrays, BLEManage.this);
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
        if(receiveBLEData && onBLEResponse == null){
            bleCoreResponse.onResponseError(listenterObject, BLEConstants.Error.OnBLEResponse, BLEManage.this);
            return;
        }
        BluetoothGatt bluetoothGatt = BLEUtil.getBluetoothGatt(connectedBluetoothGattList, targetDeviceAddress);
        if(bluetoothGatt != null){
            BLEGattCallback bleGattCallback = (BLEGattCallback)BLEUtil.getBluetoothGattMap(connectedBluetoothGattList, bluetoothGatt).get("bluetoothGattCallback");
            if(bleGattCallback != null){
                bleGattCallback.registerBleCoreResponse(bleCoreResponse);
                bleGattCallback.registerOnBLEResponseListener(onBLEResponse);
            }
            if(bluetoothGatt.getServices() == null || bluetoothGatt.getServices().size() == 0){
                bleFindService = new BLEFindService(bluetoothGatt, bleGattCallback, onBLEFindServiceListener_);
                bleFindService.findService();
                return;
            }
            if (onBLEOpenNotificationListener != null) {
                bleOpenNotification = new BLEOpenNotification(bluetoothGatt.getServices(), bluetoothGatt, notificationuuids, bleGattCallback, onBLEOpenNotificationListener_);
                bleOpenNotification.openNotification();
                return;
            }
            if(bleWriteData == null){
                bleWriteData = new BLEWriteData(bluetoothGatt, writeuuids, data, bleGattCallback, onBLEWriteDataListener_);
            }
            bleWriteData.writeData();
            return;
        }
        scan();
    }

    /**
     * 移除超时任务
     */
    public void removeTimeoutCallback(){
        timeoutHandler.removeCallbacks(timeoutRunnable);
    }

    /**
     * 判定是否超时
     */
    public boolean checkTimeout(){
        return timeout;
    }
}
