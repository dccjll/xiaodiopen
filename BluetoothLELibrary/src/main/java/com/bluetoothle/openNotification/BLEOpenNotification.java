package com.bluetoothle.openNotification;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.bluetoothle.BLEConstants;
import com.bluetoothle.connect.BLEConnect;
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
    private OnBLEOpenNotification onBLEOpenNotification;

    /**
     * gatt服务器打开通知监听器
     */
    public interface OnGattBLEOpenNotificationListener {
        void onOpenNotificationSuccess(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);
        void onOpenNotificationFail(Integer errorCode);
    }

    /**
     * 打开通知构造器
     * @param bluetoothGattServices 蓝牙服务列表
     * @param bluetoothGatt gatt服务器
     * @param uuids 包含服务、特征与描述对象的3个UUID(按顺序 uuids[0]为服务UUID, uuids[1]为特征UUID, uuids[2]为描述UUID)
     * @param onBLEOpenNotification 打开通知监听器
     */
    public BLEOpenNotification(List<BluetoothGattService> bluetoothGattServices, BluetoothGatt bluetoothGatt, UUID[] uuids, OnBLEOpenNotification onBLEOpenNotification) {
        this.bluetoothGattServices = bluetoothGattServices;
        this.bluetoothGatt = bluetoothGatt;
        this.uuids = uuids;
        this.onBLEOpenNotification = onBLEOpenNotification;
    }

    /**
     * 打开通知
     */
    public void openNotification(){
        if(onBLEOpenNotification == null){
            BLELogUtil.e(TAG, "没有配置回调接口");
            return;
        }
        if(bluetoothGatt == null){
            onBLEOpenNotification.onOpenNotificationFail(BLEConstants.OpenNotificationError.OpenNotificationError_BluetoothGattError);
            return;
        }
        if(bluetoothGattServices == null || bluetoothGattServices.size() == 0){
            onBLEOpenNotification.onOpenNotificationFail(BLEConstants.OpenNotificationError.OpenNotificationError_GattServicesError);
            return;
        }
        if(uuids == null || uuids.length != 3){
            onBLEOpenNotification.onOpenNotificationFail(BLEConstants.OpenNotificationError.OpenNotificationError_ServiceUUIDsError);
            return;
        }
        BLEConnect.bluetoothLeGattCallback.setUuidCharacteristicChange(uuids[1]);
        BLEConnect.bluetoothLeGattCallback.setUuidDescriptorWrite(uuids[2]);
        BLEConnect.bluetoothLeGattCallback.registerOnGattBLEOpenNotificationListener(
                new OnGattBLEOpenNotificationListener() {
                    @Override
                    public void onOpenNotificationSuccess(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                        onBLEOpenNotification.onOpenNotificationSuccess(gatt, descriptor, status);
                    }

                    @Override
                    public void onOpenNotificationFail(Integer errorCode) {
                        onBLEOpenNotification.onOpenNotificationFail(errorCode);
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
            onBLEOpenNotification.onOpenNotificationFail(BLEConstants.OpenNotificationError.OpenNotificationError_CannotFindNotificationServiceUUID);
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(uuids[1]);
        if(bluetoothGattCharacteristic == null){
            onBLEOpenNotification.onOpenNotificationFail(BLEConstants.OpenNotificationError.OpenNotificationError_CannotFindNotificationCharacteristicUUID);
            return;
        }
        BluetoothGattDescriptor bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(uuids[2]);
        if(bluetoothGattDescriptor == null){
            onBLEOpenNotification.onOpenNotificationFail(BLEConstants.OpenNotificationError.OpenNotificationError_CannotFindNotificationDescriptorUUID);
            return;
        }
        bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        if(!bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true)){
            onBLEOpenNotification.onOpenNotificationFail(BLEConstants.OpenNotificationError.OpenNotificationError_SetCharacteristicNotification);
            return;
        }
        if(!bluetoothGatt.writeDescriptor(bluetoothGattDescriptor)){
            onBLEOpenNotification.onOpenNotificationFail(BLEConstants.OpenNotificationError.OpenNotificationError_WriteDescriptor);
            return;
        }
    }
}