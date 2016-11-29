package com.bluetoothle.core.openNotification;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.bluetoothle.core.BLEConstants;
import com.bluetoothle.core.BLEGattCallback;
import com.bluetoothle.core.connect.BLEConnect;
import com.bluetoothle.util.BLELogUtil;

import java.util.List;
import java.util.UUID;

/**
 * Created by dessmann on 16/10/17.
 * 打开通知
 */

public class BLEOpenNotification {

    private final static String TAG = BLEOpenNotification.class.getSimpleName();
    private List<BluetoothGattService> bluetoothGattServices;
    private BluetoothGatt bluetoothGatt;
    private UUID[] uuids;
    private BLEGattCallback bleGattCallback;//蓝牙连接状态管理器
    private OnBLEOpenNotificationListener onBLEOpenNotificationListener;

    /**
     * gatt服务器打开通知监听器
     */
    public interface OnGattBLEOpenNotificationListener {
        void onOpenNotificationSuccess(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);
        void onOpenNotificationFail(String errorCode);
    }

    /**
     * 打开通知构造器
     * @param bluetoothGattServices 蓝牙服务列表
     * @param bluetoothGatt gatt服务器
     * @param uuids 包含服务、特征与描述对象的3个UUID(按顺序 uuids[0]为服务UUID, uuids[1]为特征UUID, uuids[2]为描述UUID)
     * @param onBLEOpenNotificationListener 打开通知监听器
     */
    public BLEOpenNotification(List<BluetoothGattService> bluetoothGattServices, BluetoothGatt bluetoothGatt, UUID[] uuids, BLEGattCallback bleGattCallback, OnBLEOpenNotificationListener onBLEOpenNotificationListener) {
        this.bluetoothGattServices = bluetoothGattServices;
        this.bluetoothGatt = bluetoothGatt;
        this.uuids = uuids;
        this.bleGattCallback = bleGattCallback;
        this.onBLEOpenNotificationListener = onBLEOpenNotificationListener;
    }

    /**
     * 打开通知
     */
    public void openNotification(){
        if(onBLEOpenNotificationListener == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        if(bluetoothGatt == null){
            onBLEOpenNotificationListener.onOpenNotificationFail(BLEConstants.Error.BluetoothGatt);
            return;
        }
        if(bluetoothGattServices == null || bluetoothGattServices.size() == 0){
            onBLEOpenNotificationListener.onOpenNotificationFail(BLEConstants.Error.GatService);
            return;
        }
        if(uuids == null || uuids.length != 3){
            onBLEOpenNotificationListener.onOpenNotificationFail(BLEConstants.Error.UUIDArrays);
            return;
        }
        if(bleGattCallback == null){
            onBLEOpenNotificationListener.onOpenNotificationFail(BLEConstants.Error.BluetoothGattCallBack);
            return;
        }
        bleGattCallback.setUuidCharacteristicChange(uuids[1]);
        bleGattCallback.setUuidDescriptorWrite(uuids[2]);
        bleGattCallback.registerOnGattBLEOpenNotificationListener(
                new OnGattBLEOpenNotificationListener() {
                    @Override
                    public void onOpenNotificationSuccess(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                        onBLEOpenNotificationListener.onOpenNotificationSuccess(gatt, descriptor, status, bleGattCallback);
                    }

                    @Override
                    public void onOpenNotificationFail(String errorCode) {
                        onBLEOpenNotificationListener.onOpenNotificationFail(errorCode);
                    }
                }
        );
        BluetoothGattService bluetoothGattService = null;
        for(BluetoothGattService bluetoothGattService_ : bluetoothGattServices){
            if(bluetoothGattService_.getUuid().toString().equalsIgnoreCase(uuids[0].toString())){
                bluetoothGattService = bluetoothGattService_;
                break;
            }
        }
        if(bluetoothGattService == null){
            onBLEOpenNotificationListener.onOpenNotificationFail(BLEConstants.Error.BluetoothGattService);
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(uuids[1]);
        if(bluetoothGattCharacteristic == null){
            onBLEOpenNotificationListener.onOpenNotificationFail(BLEConstants.Error.BluetoothGattCharacteristic);
            return;
        }
        BluetoothGattDescriptor bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(uuids[2]);
        if(bluetoothGattDescriptor == null){
            onBLEOpenNotificationListener.onOpenNotificationFail(BLEConstants.Error.BluetoothGattDescriptor);
            return;
        }
        bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        if(!bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true)){
            onBLEOpenNotificationListener.onOpenNotificationFail(BLEConstants.Error.SetCharacteristicNotification);
            return;
        }
        if(!bluetoothGatt.writeDescriptor(bluetoothGattDescriptor)){
            onBLEOpenNotificationListener.onOpenNotificationFail(BLEConstants.Error.WriteDescriptor);
            return;
        }
    }
}
